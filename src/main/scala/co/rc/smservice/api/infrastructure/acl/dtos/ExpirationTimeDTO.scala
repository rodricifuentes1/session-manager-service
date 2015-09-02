package co.rc.smservice.api.infrastructure.acl.dtos

/**
 * Class that represents a expiration time dto
 * @param value Expiration time value
 * @param unit Expiration time unit
 */
case class ExpirationTimeDTO( value: Int, unit: String ) {
  require( List( "seconds", "minutes", "hours", "days" ).contains( unit ),
    "Invalid unit for expiration time field" )
}
