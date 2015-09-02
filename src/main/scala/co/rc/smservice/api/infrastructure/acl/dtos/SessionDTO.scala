package co.rc.smservice.api.infrastructure.acl.dtos

import co.rc.sessionmanager.{ EmptyData, SessionData }
import co.rc.smservice.api.infrastructure.translation.entities.StringData

/**
 * Class that represents a session dto
 * @param id Session id
 * @param data Session data to store - Optional
 * @param expirationTime Session expiration time - Optional
 */
case class SessionDTO( id: String,
    data: Option[ String ],
    expirationTime: Option[ ExpirationTimeDTO ] ) {

  require( !id.isEmpty, "Session id must not be empty" )
  if ( data.isDefined ) require( !data.get.isEmpty, "Data must not be empty" )

  /**
   * Method that converts string data to a valid SessionData class
   * @return Mapped data value. If data is it returns an instance of StringDataDTO with data value inside.
   *         Otherwise it returns EmptyData object
   */
  def dataToSessionData(): SessionData = data.map( d => StringData( d ) ).getOrElse( EmptyData )

}
