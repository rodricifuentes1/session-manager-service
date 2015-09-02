package co.rc.smservice.api.infrastructure.auditing

import akka.actor.Actor
import co.rc.smservice.api.infrastructure.auditing.entities.AuditAction
import com.typesafe.scalalogging.LazyLogging

/**
 * Actor that implements logging behaviour
 */
class AuditingActor extends Actor with LazyLogging {

  /**
   * Auditing actor receive method
   * @return Auditing actor receive strategy
   */
  def receive: Receive = {
    case AuditAction( logLevel, request, response ) => logLevel match {
      case "WARN"  => logger.warn( s"$request - $response" )
      case "DEBUG" => logger.debug( s"$request - $response" )
      case _       => logger.info( s"$request - $response" )
    }
    case msg: String => logger.info( msg )
    case msg         => logger.error( s"Received invalid message to log. $msg" )
  }

}
