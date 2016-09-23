javaHome := Some(file("C:\\Program Files\\Java\\jdk1.7.0_79"))
name := "twittergeoperborough"

version := "1.0"

scalaVersion := "2.10.1"
unmanagedJars in Compile += file("lib/geometryutils-assembly.jar")

resolvers += "conjars" at "http://conjars.org/repo"

assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)
includeFilter in (Compile, unmanagedJars) := "*.jar"

// additional libraries
libraryDependencies ++= Seq(
	"org.apache.spark" % "spark-core_2.10" % "1.6.2" % "provided",
	"org.apache.spark" % "spark-sql_2.10" % "1.6.2" % "provided",
	"org.apache.spark" % "spark-streaming_2.10" % "1.6.2" % "provided",
 	"org.apache.spark" % "spark-streaming-kafka_2.10" % "1.6.2",
 	"org.apache.kafka" % "kafka_2.10" % "0.9.0.1" % "provided",
 	"org.apache.avro" % "avro" % "1.8.0",
	"org.mongodb.spark" % "mongo-spark-connector_2.10" % "1.0.0",
	"org.mongodb" % "mongo-java-driver" % "3.2.1",
	"joda-time" % "joda-time" % "2.9.4",
	"org.joda" % "joda-convert" % "1.8.1"

)

assemblyExcludedJars in assembly := { 
  val cp = (fullClasspath in assembly).value
  cp filter {x => x.data.getName.matches("sbt.*") || x.data.getName.matches("scalaz.*") || x.data.getName.matches(".*unused.*") || x.data.getName.matches(".*minlog-1.2.*")}
}