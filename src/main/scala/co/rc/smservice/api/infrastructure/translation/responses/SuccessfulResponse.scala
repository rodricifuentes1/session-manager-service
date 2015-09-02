package co.rc.smservice.api.infrastructure.translation.responses

/**
 * Class that represents a successful response
 * @param response Response description
 * @param sessionId session id. Optional.
 * @param sessionWasUpdated A boolean that indicates if session was updated. Optional.
 * @param sessionData Session data. Optional.
 */
case class SuccessfulResponse( response: String,
  sessionId: Option[ String ] = None,
  sessionWasUpdated: Option[ Boolean ] = None,
  sessionData: Option[ String ] = None )
