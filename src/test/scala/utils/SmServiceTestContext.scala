package utils

import org.scalatest.{ FlatSpec, Matchers }
import spray.routing.HttpService
import spray.testkit.ScalatestRouteTest
import com.typesafe.config.ConfigFactory

abstract class SmServiceTestContext extends FlatSpec
    with ScalatestRouteTest
    with HttpService
    with Matchers {

  def actorRefFactory = system

  implicit def config = ConfigFactory.load()

}
