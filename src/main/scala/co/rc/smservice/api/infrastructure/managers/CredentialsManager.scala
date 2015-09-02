package co.rc.smservice.api.infrastructure.managers

import com.typesafe.config.Config
import net.ceedubs.ficus.Ficus._
import scala.concurrent.{ Future, ExecutionContext }
import spray.routing.authentication.ContextAuthenticator
import spray.http.HttpHeader
import spray.routing.AuthenticationFailedRejection

/**
 * Trait that implements authentication behaviour
 */
trait CredentialsManager {

  /**
   * Method that validates a request come from an authorized application
   * @param executionContext Implicit execution context for future management
   * @param config Implicit application config loaded
   * @return
   */
  def validateAppKey()( implicit executionContext: ExecutionContext, config: Config ): ContextAuthenticator[ String ] = { ctx =>
    val appKeyHeaderOption: Option[ HttpHeader ] = ctx.request.headers.find( _.name == "app-key" )
    appKeyHeaderOption match {
      case None =>
        Future( Left( AuthenticationFailedRejection( AuthenticationFailedRejection.CredentialsMissing, List() ) ) )
      case Some( appKeyHeader ) =>
        val allowedKeys: List[ String ] = config.as[ List[ String ] ]( "co.rc.smservice.security.allowed-keys" )
        if ( allowedKeys.exists( _ == appKeyHeader.value ) ) Future( Right( appKeyHeader.value ) )
        else Future( Left( AuthenticationFailedRejection( AuthenticationFailedRejection.CredentialsRejected, List() ) ) )
    }
  }

}
