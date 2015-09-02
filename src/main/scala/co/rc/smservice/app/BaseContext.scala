package co.rc.smservice.app

import akka.actor.ActorSystem
import scala.concurrent.ExecutionContext
import com.typesafe.config.Config

/**
 * Trait that defines a base context
 */
trait BaseContext {

  /**
   * Base actor system
   */
  implicit def system: ActorSystem

  /**
   * Base execution context
   */
  implicit def executionContext: ExecutionContext

  /**
   * Base configuration
   */
  implicit def config: Config

}
