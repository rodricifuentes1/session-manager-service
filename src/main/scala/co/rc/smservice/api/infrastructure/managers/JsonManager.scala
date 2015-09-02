package co.rc.smservice.api.infrastructure.managers

import org.json4s.native.Serialization
import org.json4s.{ Formats, NoTypeHints }
import spray.httpx.Json4sSupport

/**
 * Trait that implements json serializers
 */
trait JsonManager extends Json4sSupport {

  // Json4s serializers formats
  override implicit def json4sFormats: Formats = Serialization.formats( NoTypeHints )

}
