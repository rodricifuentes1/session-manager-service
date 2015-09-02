outputPath in assembly := file( "dist/session-manager-service.jar" )

test in assembly := {}

mainClass in assembly := Some("co.rc.smservice.app.Boot")

assemblyMergeStrategy in assembly := {
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}