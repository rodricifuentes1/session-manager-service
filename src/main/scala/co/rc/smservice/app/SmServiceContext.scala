package co.rc.smservice.app

import akka.actor.ActorSystem
import com.typesafe.config.{ Config, ConfigFactory }
import scala.concurrent.ExecutionContext
import java.io.File
import scala.util.{ Failure, Success, Try }

/**
 * Trait that implements service context
 */
trait SmServiceContext { this: BaseContext =>

  /**
   * Service actor system
   */
  override implicit val system: ActorSystem = ActorSystem( "session-manager-service-system" )

  /**
   * Service execution context
   */
  override implicit val executionContext: ExecutionContext = system.dispatcher

  /**
   * Service configuration
   */
  override implicit val config: Config = Try( new File( "./session-manager-service.conf" ) ) match {
    case Success( file ) => ConfigFactory.parseFile( file ).withFallback( ConfigFactory.load() )
    case Failure( ex )   => ConfigFactory.load()
  }

}

/**
 * Object that implements service context behaviour
 */
object SmServiceContext extends BaseContext with SmServiceContext
