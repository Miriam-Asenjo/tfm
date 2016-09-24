javaHome := Some(file("C:\\Program Files\\Java\\jdk1.7.0_79"))
name := "busyborough"

version := "1.0"

scalaVersion := "2.10.1"
unmanagedJars in Compile += file("lib/geometryutils-assembly.jar")

includeFilter in (Compile, unmanagedJars) := "*.jar"

// additional libraries
libraryDependencies ++= Seq(
	"org.apache.spark" % "spark-core_2.10" % "1.6.2" % "provided",
	"org.apache.spark" % "spark-sql_2.10" % "1.6.2" % "provided",
	"org.mongodb.spark" % "mongo-spark-connector_2.10" % "1.0.0",
	"org.mongodb" % "mongo-java-driver" % "3.2.1",
	"joda-time" % "joda-time" % "2.9.4",
	"org.joda" % "joda-convert" % "1.8.1",
	"io.spray" % "spray-json_2.10" % "1.3.0"
)

assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)

assemblyExcludedJars in assembly := { 
  val cp = (fullClasspath in assembly).value
  cp filter {x => x.data.getName.matches("sbt.*") || x.data.getName.matches("jackson-core-asl.*") || x.data.getName.matches("scalaz.*")}
}