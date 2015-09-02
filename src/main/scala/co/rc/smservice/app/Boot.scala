package co.rc.smservice.app

import akka.actor.Props
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import co.rc.smservice.api.routing.SmServiceRouter
import net.ceedubs.ficus.Ficus._
import spray.can.Http

import scala.concurrent.duration._

/**
 * Object that implements application entry point
 */
object Boot extends App {

  // Import system, executionContext, config
  import co.rc.smservice.app.SmServiceContext._

  // Defines spray can server startup timeout
  implicit val timeout: Timeout = config.as[ FiniteDuration ]( "co.rc.smservice.server.startup-timeout" )

  // Starts the server
  IO( Http ) ? Http.Bind( system.actorOf( Props( new SmServiceRouter() ) ),
    interface = config.as[ String ]( "co.rc.smservice.server.host" ),
    port = config.as[ Int ]( "co.rc.smservice.server.port" ) )

}
