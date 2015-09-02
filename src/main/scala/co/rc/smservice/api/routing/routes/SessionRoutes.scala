package co.rc.smservice.api.routing.routes

import akka.actor.{ Props, ActorSystem }
import co.rc.sessionmanager.{ EmptyData, SessionData, SessionManager }
import co.rc.sessionmanager.SessionRouter._
import co.rc.smservice.api.infrastructure.auditing.AuditingActor
import co.rc.smservice.api.infrastructure.acl.dtos.SessionDTO
import co.rc.smservice.api.infrastructure.translation.entities.StringData
import co.rc.smservice.api.infrastructure.translation.responses.SuccessfulResponse
import co.rc.smservice.api.routing.IRoute
import co.rc.smservice.app.BaseContext
import com.typesafe.config.Config
import net.ceedubs.ficus.Ficus._
import scala.concurrent.{ ExecutionContext, Future }
import spray.http.StatusCodes
import spray.routing.Route

/**
 * Trait that implements service routes
 */
class SessionRoutes()( implicit val system: ActorSystem,
    val executionContext: ExecutionContext,
    val config: Config ) extends BaseContext with IRoute {

  /**
   * Unique session manager instance
   */
  private val sessionManager: SessionManager = new SessionManager()

  /**
   * Unique auditing actor instance
   */
  private val auditingActor = system.actorOf( Props[ AuditingActor ] )

  /**
   * Service routes definition
   */
  val routes: Route = pathPrefix( getPath( "base" ) ) {
    pathPrefix( getPath( "sessions-path" ) ) {
      authenticate( validateAppKey() ) { appKey =>
        mapRequestContext( r => auditTo( auditingActor, r, appKey ) ) {
          pathEndOrSingleSlash {
            post {
              entity( as[ SessionDTO ] ) { sessionDTO =>
                complete {
                  // Future request
                  val futureRequest: Future[ CreateActionResponse ] = sessionDTO.expirationTime match {
                    case Some( expTime ) => sessionManager.createSession(
                      sessionDTO.id,
                      update = config.as[ Boolean ]( "co.rc.smservice.sessionmanager.reload-on-create" ),
                      sessionDTO.dataToSessionData(),
                      expTime.value,
                      expTime.unit
                    )
                    case None => sessionManager.createSession(
                      sessionDTO.id,
                      update = config.as[ Boolean ]( "co.rc.smservice.sessionmanager.reload-on-create" ),
                      sessionDTO.dataToSessionData()
                    )
                  }
                  // Request map
                  futureRequest.map {
                    case SessionCreated( sessionId ) =>
                      StatusCodes.Created -> SuccessfulResponse(
                        "Session was created",
                        Some( sessionId )
                      )
                    case SessionAlreadyExist( sessionId, updated, data ) =>
                      StatusCodes.Conflict -> SuccessfulResponse(
                        "Session already exist",
                        Some( sessionId ),
                        Some( updated ),
                        mapSessionData( data )
                      )
                  }
                }
              }
            }
          } ~ pathPrefix( Segment ) { sessionId =>
            pathEndOrSingleSlash {
              get {
                complete {
                  val query: Future[ QueryActionResponse ] = sessionManager.querySession(
                    sessionId,
                    update = config.as[ Boolean ]( "co.rc.smservice.sessionmanager.reload-on-query" )
                  )
                  query.map {
                    case SessionFound( _, updated, data ) =>
                      StatusCodes.OK -> SuccessfulResponse(
                        "Session was found",
                        Some( sessionId ),
                        Some( updated ),
                        mapSessionData( data )
                      )
                    case SessionNotFound( _ ) =>
                      StatusCodes.NotFound -> SuccessfulResponse( "Requested session was not found or does not exist" )
                  }
                }
              } ~ delete {
                complete {
                  val close: Future[ CloseActionResponse ] = sessionManager.closeSession( sessionId )
                  close.map {
                    case SessionClosed( _ ) =>
                      StatusCodes.OK -> SuccessfulResponse( "Request executed successfully" )
                    case SessionNotClosed( _ ) =>
                      StatusCodes.NotFound -> SuccessfulResponse( "Requested session was not found or does not exist" )
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  /**
   * Method that verifies session data type
   * @param data Data to verify
   * @return Some(string value) if data is not defined as EmptyData
   *         None otherwise
   */
  def mapSessionData( data: SessionData ): Option[ String ] = data match {
    case EmptyData     => None
    case x: StringData => Some( x.value )
  }

}
