package co.rc.smservice.api.routing

import co.rc.smservice.api.infrastructure.managers.{ CredentialsManager, JsonManager }
import co.rc.smservice.app.BaseContext
import net.ceedubs.ficus.Ficus._
import spray.routing.{ RouteConcatenation, Directives }
import co.rc.smservice.api.infrastructure.auditing.AuditingHelper

/**
 * Trait that defines commons route actions
 */
trait IRoute extends JsonManager
    with CredentialsManager
    with Directives
    with RouteConcatenation
    with AuditingHelper { this: BaseContext =>

  /**
   * Method that retrieves a defined path
   * @return Loaded string path
   */
  def getPath( name: String ) = config.as[ String ]( s"co.rc.smservice.api.$name" )

}
