package co.rc.smservice.api.infrastructure.auditing

import akka.actor.ActorRef
import co.rc.smservice.api.infrastructure.auditing.entities.AuditAction
import org.joda.time.DateTime
import spray.http.{ HttpCharsets, HttpResponse }
import spray.routing.RequestContext

/**
 * Trait that implements auditing methods
 */
trait AuditingHelper {

  /**
   * Method that audit an action in log
   * @param ctx Request context
   * @param appKey Application key
   * @param date Request date
   * @return Modified request context
   */
  def auditTo( target: ActorRef, ctx: RequestContext,
    appKey: String, date: DateTime = new DateTime() ): RequestContext = ctx.withRouteResponseMapped {
    case response: HttpResponse =>

      // Request format
      val audRequest: String = s"Request { AppKey: $appKey, " +
        s"HttpMethod: ${ctx.request.method.value}, " +
        s"Url: ${ctx.request.uri.path.toString()}, " +
        s"Body: ${cleanJson( ctx.request.entity.asString( HttpCharsets.`UTF-8` ) )}, " +
        s"Time: ${date.toString( "dd/MM/yyyy HH:mm:ss" )} }"

      // Response format
      val audResponse: String = s"Response { StatusCode: ${response.status.value}, " +
        s"Body: ${cleanJson( response.entity.asString( HttpCharsets.`UTF-8` ) )}, " +
        s"Time: ${new DateTime().toString( "dd/MM/yyyy HH:mm:ss" )} }"

      // Action to log
      val action: AuditAction = AuditAction( "INFO", audRequest, audResponse )

      // Send action to target actor
      target ! action

      // Return http response
      response

    case x => x
  }

  /**
   * Utility method that cleans json spaces and new lines
   * @param json Json to clean
   * @return Cleaned json
   */
  private def cleanJson( json: String ) = json.split( '\n' ).map( _.trim.filter( _ >= ' ' ) ).mkString

}
