package co.rc.smservice.api.routing

import akka.actor.ActorSystem
import com.typesafe.config.Config
import scala.concurrent.ExecutionContext
import spray.routing.HttpServiceActor
import co.rc.smservice.api.routing.routes.SessionRoutes

/**
 * Class that defines rest service router actor
 */
class SmServiceRouter()( implicit system: ActorSystem,
    executionContext: ExecutionContext,
    config: Config ) extends HttpServiceActor {

  /**
   * Service
   */
  private val sessionRoutes: SessionRoutes = new SessionRoutes()

  /**
   * Service router receive method
   * @return Service router receive strategy
   */
  override def receive: Receive = runRoute( sessionRoutes.routes )

}
