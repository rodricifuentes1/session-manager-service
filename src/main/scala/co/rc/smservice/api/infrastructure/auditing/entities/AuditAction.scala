package co.rc.smservice.api.infrastructure.auditing.entities

/**
 * Class that represents an audit action
 * @param logLevel Log level: INFO - DEBUG - ERROR
 * @param request Request to log
 * @param response Response to log
 */
case class AuditAction( logLevel: String,
  request: String,
  response: String )
