package co.rc.smservice.api.routing.routes

import com.typesafe.config.ConfigFactory
import org.scalatest.FlatSpec
import spray.http.HttpEntity
import spray.http.HttpHeaders._
import spray.http.MediaTypes._
import spray.http.StatusCodes._
import utils.SmServiceTestContext

/**
 * Class that implements tests for SmServiceRouter
 */
class SessionRoutesTest extends FlatSpec {

  // -----------
  // AUTH TESTS
  // -----------

  "SmServiceRouterTest" should "AUTH: Get missing credentials when requesting the api without api-key header" in new SmServiceTestContext {
    val sessionRoutes: SessionRoutes = new SessionRoutes()
    Get( "/session_manager/sessions/1" ) ~> sealRoute( sessionRoutes.routes ) ~> check {
      status shouldBe Unauthorized
      responseAs[ String ] shouldBe "The resource requires authentication, which was not supplied with the request"
    }
  }

  it should "AUTH: Get invalid credentials when requesting the api with and invalid api-key" in new SmServiceTestContext {
    val invalidHeader: RawHeader = RawHeader( "app-key", "invalid-key" )
    val sessionRoutes: SessionRoutes = new SessionRoutes()
    Get( "/session_manager/sessions/1" ) ~> addHeader( invalidHeader ) ~> sealRoute( sessionRoutes.routes ) ~> check {
      status shouldBe Unauthorized
      responseAs[ String ] shouldBe "The supplied authentication is invalid"
    }
  }

  // -----------
  // PATH TESTS
  // -----------

  it should "PATH: Change base path as defined in configuration" in new SmServiceTestContext {
    override implicit def config = ConfigFactory.parseString(
      """co.rc.smservice {
        |  api {
        |    base = "s_m"
        |  }
        |}
      """.stripMargin ).withFallback( ConfigFactory.load() )
    val appKeyHeader: RawHeader = RawHeader( "app-key", "app1-key" )
    val sessionRoutes: SessionRoutes = new SessionRoutes()
    Get( "/s_m/sessions/1" ) ~> addHeader( appKeyHeader ) ~> sessionRoutes.routes ~> check {
      handled shouldBe true
    }
  }

  it should "PATH: Change sessions path as defined in configuration" in new SmServiceTestContext {
    override implicit def config = ConfigFactory.parseString(
      """co.rc.smservice {
        |  api {
        |    sessions-path = "sess"
        |  }
        |}
      """.stripMargin ).withFallback( ConfigFactory.load() )
    val appKeyHeader: RawHeader = RawHeader( "app-key", "app1-key" )
    val sessionRoutes: SessionRoutes = new SessionRoutes()
    Get( "/session_manager/sess/1" ) ~> addHeader( appKeyHeader ) ~> sessionRoutes.routes ~> check {
      handled shouldBe true
    }
  }

  it should "PATH: Change both base and sessions paths as defined in configuration" in new SmServiceTestContext {
    override implicit def config = ConfigFactory.parseString(
      """co.rc.smservice {
        |  api {
        |    base = "s_m"
        |    sessions-path = "sess"
        |  }
        |}
      """.stripMargin ).withFallback( ConfigFactory.load() )
    val appKeyHeader: RawHeader = RawHeader( "app-key", "app1-key" )
    val sessionRoutes: SessionRoutes = new SessionRoutes()
    Get( "/s_m/sess/1" ) ~> addHeader( appKeyHeader ) ~> sessionRoutes.routes ~> check {
      handled shouldBe true
    }
  }

  // ----------------------------------------
  // CREATE SESSION TESTS: ENTITY VALIDATION
  // ----------------------------------------

  it should "CREATE: Get bad request when trying to create a session without body" in new SmServiceTestContext {
    val appKeyHeader: RawHeader = RawHeader( "app-key", "app1-key" )
    val sessionRoutes: SessionRoutes = new SessionRoutes()
    Post( "/session_manager/sessions" ) ~> addHeader( appKeyHeader ) ~> sealRoute( sessionRoutes.routes ) ~> check {
      status shouldBe BadRequest
      responseAs[ String ] shouldBe "Request entity expected but not supplied"
    }
  }

  it should "CREATE: Get bad request when trying to create a session with empty id" in new SmServiceTestContext {
    val appKeyHeader: RawHeader = RawHeader( "app-key", "app1-key" )
    val sessionRoutes: SessionRoutes = new SessionRoutes()
    val jsonBody: String =
      """
        |{
        |	"id":""
        |}
      """.stripMargin
    Post( "/session_manager/sessions", HttpEntity( `application/json`, jsonBody ) ) ~> addHeaders( appKeyHeader ) ~> sealRoute( sessionRoutes.routes ) ~> check {
      status shouldBe BadRequest
      responseAs[ String ] shouldBe "The request content was malformed:\nrequirement failed: Session id must not be empty"
    }
  }

  it should "CREATE: Get bad request when trying to create a session with empty data" in new SmServiceTestContext {
    val appKeyHeader: RawHeader = RawHeader( "app-key", "app1-key" )
    val sessionRoutes: SessionRoutes = new SessionRoutes()
    val jsonBody: String =
      """
        |{
        |	"id":"1",
        | "data":""
        |}
      """.stripMargin
    Post( "/session_manager/sessions", HttpEntity( `application/json`, jsonBody ) ) ~> addHeaders( appKeyHeader ) ~> sealRoute( sessionRoutes.routes ) ~> check {
      status shouldBe BadRequest
      responseAs[ String ] shouldBe "The request content was malformed:\nrequirement failed: Data must not be empty"
    }
  }

  it should "CREATE: Get bad request when trying to create a session with invalid unit in expiration time" in new SmServiceTestContext {
    val appKeyHeader: RawHeader = RawHeader( "app-key", "app1-key" )
    val sessionRoutes: SessionRoutes = new SessionRoutes()
    val jsonBody: String =
      """
        |{
        |	"id":"1",
        | "data":"hello",
        | "expirationTime": {
        |   "value":10,
        |   "unit":"invalid unit"
        | }
        |}
      """.stripMargin
    Post( "/session_manager/sessions", HttpEntity( `application/json`, jsonBody ) ) ~> addHeaders( appKeyHeader ) ~> sealRoute( sessionRoutes.routes ) ~> check {
      status shouldBe BadRequest
      responseAs[ String ] shouldBe "The request content was malformed:\nrequirement failed: Invalid unit for expiration time field"
    }
  }

  // -------------------------------
  // CREATE SESSION TESTS: CREATION
  // -------------------------------

  it should "CREATE: Create a session successfully with id '1'" in new SmServiceTestContext {
    val appKeyHeader: RawHeader = RawHeader( "app-key", "app1-key" )
    val sessionRoutes: SessionRoutes = new SessionRoutes()
    val jsonBody: String =
      """
        |{
        |	"id":"1"
        |}
      """.stripMargin
    Post( "/session_manager/sessions", HttpEntity( `application/json`, jsonBody ) ) ~> addHeaders( appKeyHeader ) ~> sealRoute( sessionRoutes.routes ) ~> check {
      status shouldBe Created
    }
  }

  it should "CREATE: Create a session successfully with id '1' and data 'Hello world'" in new SmServiceTestContext {
    val appKeyHeader: RawHeader = RawHeader( "app-key", "app1-key" )
    val sessionRoutes: SessionRoutes = new SessionRoutes()
    val jsonBody: String =
      """
        |{
        |	"id":"1",
        | "data":"Hello world"
        |}
      """.stripMargin
    Post( "/session_manager/sessions", HttpEntity( `application/json`, jsonBody ) ) ~> addHeaders( appKeyHeader ) ~> sealRoute( sessionRoutes.routes ) ~> check {
      status shouldBe Created
    }
  }

  it should "CREATE: Create a session successfully with id '1', data 'Hello world' and expiration time 1 minutes" in new SmServiceTestContext {
    val appKeyHeader: RawHeader = RawHeader( "app-key", "app1-key" )
    val sessionRoutes: SessionRoutes = new SessionRoutes()
    val jsonBody: String =
      """
        |{
        |	"id":"1",
        | "data":"Hello world",
        | "expirationTime": {
        |   "value":1,
        |   "unit":"minutes"
        | }
        |}
      """.stripMargin
    Post( "/session_manager/sessions", HttpEntity( `application/json`, jsonBody ) ) ~> addHeaders( appKeyHeader ) ~> sealRoute( sessionRoutes.routes ) ~> check {
      status shouldBe Created
    }
  }

  it should "CREATE: Get conflict when creating an existing session" in new SmServiceTestContext {
    val appKeyHeader: RawHeader = RawHeader( "app-key", "app1-key" )
    val sessionRoutes: SessionRoutes = new SessionRoutes()
    val jsonBody: String =
      """
        |{
        |	"id":"1",
        | "data":"Hello world",
        | "expirationTime": {
        |   "value":1,
        |   "unit":"minutes"
        | }
        |}
      """.stripMargin
    Post( "/session_manager/sessions", HttpEntity( `application/json`, jsonBody ) ) ~> addHeaders( appKeyHeader ) ~> sealRoute( sessionRoutes.routes ) ~> check {
      status shouldBe Created
    }
    Post( "/session_manager/sessions", HttpEntity( `application/json`, jsonBody ) ) ~> addHeaders( appKeyHeader ) ~> sealRoute( sessionRoutes.routes ) ~> check {
      status shouldBe Conflict
    }
  }

  // ---------------------------
  // QUERY SESSION TESTS: QUERY
  // ---------------------------

  it should "QUERY: Get not found when querying a non existing session" in new SmServiceTestContext {
    val appKeyHeader: RawHeader = RawHeader( "app-key", "app1-key" )
    val sessionRoutes: SessionRoutes = new SessionRoutes()
    Get( "/session_manager/sessions/non-existing" ) ~> addHeaders( appKeyHeader ) ~> sealRoute( sessionRoutes.routes ) ~> check {
      status shouldBe NotFound
    }
  }

  it should "QUERY: Get OK when querying an existing session with empty data" in new SmServiceTestContext {
    val appKeyHeader: RawHeader = RawHeader( "app-key", "app1-key" )
    val sessionRoutes: SessionRoutes = new SessionRoutes()
    val jsonBody: String =
      """
        |{
        |	"id":"1",
        | "expirationTime": {
        |   "value":1,
        |   "unit":"minutes"
        | }
        |}
      """.stripMargin
    Post( "/session_manager/sessions", HttpEntity( `application/json`, jsonBody ) ) ~> addHeaders( appKeyHeader ) ~> sealRoute( sessionRoutes.routes ) ~> check {
      status shouldBe Created
    }
    Get( "/session_manager/sessions/1" ) ~> addHeaders( appKeyHeader ) ~> sealRoute( sessionRoutes.routes ) ~> check {
      status shouldBe OK
    }
  }

  it should "QUERY: Get OK when querying an existing session with provided data" in new SmServiceTestContext {
    val appKeyHeader: RawHeader = RawHeader( "app-key", "app1-key" )
    val sessionRoutes: SessionRoutes = new SessionRoutes()
    val jsonBody: String =
      """
        |{
        |	"id":"1",
        | "data":"Hello world",
        | "expirationTime": {
        |   "value":1,
        |   "unit":"minutes"
        | }
        |}
      """.stripMargin
    Post( "/session_manager/sessions", HttpEntity( `application/json`, jsonBody ) ) ~> addHeaders( appKeyHeader ) ~> sealRoute( sessionRoutes.routes ) ~> check {
      status shouldBe Created
    }
    Get( "/session_manager/sessions/1" ) ~> addHeaders( appKeyHeader ) ~> sealRoute( sessionRoutes.routes ) ~> check {
      status shouldBe OK
    }
  }

  // ---------------------------
  // CLOSE SESSION TESTS: CLOSE
  // ---------------------------

  it should "CLOSE: Get not found when closing a non existing session" in new SmServiceTestContext {
    val appKeyHeader: RawHeader = RawHeader( "app-key", "app1-key" )
    val sessionRoutes: SessionRoutes = new SessionRoutes()
    Delete( "/session_manager/sessions/non-existing" ) ~> addHeaders( appKeyHeader ) ~> sealRoute( sessionRoutes.routes ) ~> check {
      status shouldBe NotFound
    }
  }

  it should "CLOSE: Get OK when closing an existing session" in new SmServiceTestContext {
    val appKeyHeader: RawHeader = RawHeader( "app-key", "app1-key" )
    val sessionRoutes: SessionRoutes = new SessionRoutes()
    val jsonBody: String =
      """
        |{
        |	"id":"1"
        |}
      """.stripMargin
    Post( "/session_manager/sessions", HttpEntity( `application/json`, jsonBody ) ) ~> addHeaders( appKeyHeader ) ~> sealRoute( sessionRoutes.routes ) ~> check {
      status shouldBe Created
    }
    Delete( "/session_manager/sessions/1" ) ~> addHeaders( appKeyHeader ) ~> sealRoute( sessionRoutes.routes ) ~> check {
      status shouldBe OK
    }
  }

}
