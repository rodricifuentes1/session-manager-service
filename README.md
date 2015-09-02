## Session manager service
Session manager service provides a REST API Layer for [session manager library](https://github.com/rodricifuentes1/session-manager) built on top of [spray.io](spray.io)
## Usage
### Configuration
Session manager service has an `application.conf` file defined with these settings:
```
co.rc.smservice {
  api {
    base = "session_manager" // Services base path prefix
    sessions-path = "sessions" // Services sessions path
  }
  security {
    // YOU NEED TO OVERRIDE THIS SETTING
    allowed-keys = [ "app1-key", "app2-key" ] // Authorized keys to use the service
  }
  server {
    host = "0.0.0.0" // Rest service host
    port = 7777 // Exposed port
    startup-timeout = "10 seconds" // Max timeout to wait for the server to start
  }
  sessionmanager {
    reload-on-create = false // A boolean that indicates whether the session must be reloaded on create if it was created before.
    reload-on-query = true // A boolean that indicates whether the session must be reloaded on query when if it was created before.
  }
}
```
### External file configuration
You can provide an external configuration file located in the same path where the generated `jar` is. This file must be named `session-manager-service.conf` and it should contain valid settings as defined before. When the service is starting, it will look for the external file and will override previously defined settings. Also, if you want to override session-manager library settings, you can add them in the external file.

Here's an example of a valid external configuration file `session-manager-service.conf`:
```
// Settings to override
co.rc.smservice {
  security {
    allowed-keys = [ "tciidk6j78eYVAq5YQs517Ej8v66HN54" ]
  }
  server {
    port = 9999 // Exposed port
  }
  sessionmanager {
    reload-on-create = true
  }
}

// To override session-manager library (https://github.com/rodricifuentes1/session-manager) default settings
co.rc.sessionmanager {
  exptime-value = 60
  exptime-unit = "seconds"
  ask-timeout = 5
}
```
### Security
This service has a basic authentication system. It basically verifies that the incoming request is from an authorized origin checking the `app-key` header value. If the header is not present in th request the service will return `401 - Unauthorized` specifiying that authentication is required. If the header is present but is not an authorized origin the service will also return `401 - Unauthorized` specifying that credentials are invalid.

Authorized origins need to be overriden via `session-manager-service.conf` under `co.rc.smservice.security.allowed-keys` setting. It is posible to define multiple authorized origins like so:
```
co.rc.smservice {
  security {
    allowed-keys = [ "allowedOriginKey1", "allowedOriginKey2", "allowedOriginKey3" ]
  }
}
```

### Exposed services
There are three services exposed that are built using `co.rc.smservice` configuration values like so: `http://server.host:server.port/api.base/api.sessions-path`. Assuming the default configuration, the exposed rest services are:

| Verb | Url | Description |
| ---- | --- | ----------- |
| `POST` | `http://0.0.0.0:7777/session_manager/sessions` | Create a new session |
| `GET` | `http://0.0.0.0:7777/session_manager/sessions/{session-id}` | Query a session |
| `DELETE` | `http://0.0.0.0:7777/session_manager/sessions/{session-id}` | Delete a session |
### Exposed services details
#### Service: Create a new session
* Verb: `POST`
* Url: `http://server.host:server.port/api.base/api.sessions-path`
* Default config url: `http://0.0.0.0:7777/session_manager/sessions`
* Entities:

**ExpirationTime**

| Attr name | Attr type | Description | Mandatory |
| --------- | --------- | ----------- | --------- |
| value     | Int       | Expiration time value  | YES |
| unit      | String    | Expiration time value  | YES |

**Session**

| Attr name | Attr type | Description | Mandatory |
| --------- | --------- | ----------- | --------- |
| id        | String    | Session id  | YES       |
| data      | String    | Data to store in session | NO |
| expirationTime | ExpirationTime | Session expiration time  | NO |

* Request example - `application/json`
```json
{
	"id":"1",
	"data":"{ \"myIntData\": 10, \"myStringData\" : \"hello world\" }",
	"expirationTime": {
	  "value": 10,
	  "unit": "minutes"
	}
}
```
* Service responses:

If session is created successfully, the service will return code `201 - Created` with the following payload
```json
{
    "response": "Session was created",
    "sessionId": "1"
}
```
If you are attempting to create a session that already exist, the service wil return code `409 - Conflict`with the following payload
```json
{
    "response": "Session already exist",
    "sessionId": "1",
    "sessionWasUpdated": false,
    "sessionData": "{ \"myIntData\": 10, \"myStringData\" : \"hello world\" }"
}
```
*Note: 'sessionWasUpdated' attribute  will vary depending on reload-on-create setting*
#### Service: Query a session
* Verb: `GET`
* Url: `http://server.host:server.port/api.base/api.sessions-path/{session-id}`
* Default config url: `http://0.0.0.0:7777/session_manager/sessions/{session-id}`
* Service responses:

If session is found, the service will return code `200 - OK` with the following payload
```json
{
    "response": "Session was found",
    "sessionId": "1",
    "sessionWasUpdated": true,
    "sessionData": "{ \"myIntData\": 10, \"myStringData\" : \"hello world\" }"
}
```
*Note: 'sessionWasUpdated' attribute  will vary depending on reload-on-query setting*

If you are attempting to query a session that not exist, the service wil return code `404 - NotFound` with the following payload
```json
{
    "response": "Requested session was not found or does not exist"
}
```
#### Service: Delete a session
* Verb: `DELETE`
* Url: `http://server.host:server.port/api.base/api.sessions-path/{session-id}`
* Default config url: `http://0.0.0.0:7777/session_manager/sessions/{session-id}`
* Service responses:

If session is deleted successfully, the service will return code `200 - OK` with the following payload
```json
{
    "response": "Request executed successfully"
}
```
*Note: 'sessionWasUpdated' attribute  will vary depending on reload-on-query setting*

If you are attempting to delete a session that not exist, the service wil return code `404 - NotFound` with the following payload
```json
{
    "response": "Requested session was not found or does not exist"
}
```
### Logs
This service uses `scala-logging` and `logback` libraries to handle generated log. By default it is configured to log only `INFO` events but this can be re-defined modifying the `logback.xml` file at the resources folder.

Logs will be exported into a `log` folder located at the base project or jar path `(./)` using logback daily rolling policy under `session-manager-service.log` name
### Build this project
1. `clone` this project.
2. Execute `sbt` command in project directory.
3. Execute `update` and `compile` in sbt console.
4. To export project into a executable `jar` execute `assembly` command in sbt console. This will generate a `dist` folder located in the base path `(./)`with the generated `session-manager-service.jar` file.
5. To run tests execute `test` command in sbt console.
6. To generate tests report using `scoverage` plugin, execute `coverage` and `test` commans in sbt console. This will generate tests report under `target/scala_2.11/scoverage_report` folder.
7. To run this project execute `run` or `reStart` commands in sbt console.
8. To generate project for Intellij-Idea ide, execute `gen-idea` command in sbt console.

### Test code coverage - 100%
