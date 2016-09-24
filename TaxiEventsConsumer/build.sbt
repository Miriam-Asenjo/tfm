

javaHome := Some(file("C:\\Program Files\\Java\\jdk1.8.0_74"))

name := "taxistands-events-consumer"

version := "0.1"
scalaVersion := "2.10.6"

assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)


//additional libraries
libraryDependencies ++= Seq(
	"org.apache.spark" % "spark-core_2.10" % "1.6.2" % "provided",
	"org.apache.spark" % "spark-sql_2.10" % "1.6.2" % "provided",
	"org.apache.spark" % "spark-streaming_2.10" % "1.6.2" % "provided",
 	"org.apache.kafka" % "kafka_2.10" % "0.9.0.1" % "provided",
 	"org.apache.spark" % "spark-streaming-kafka_2.10" % "1.6.2",
	"org.mongodb.spark" % "mongo-spark-connector_2.10" % "1.0.0",
	"org.mongodb" % "mongo-java-driver" % "3.2.1",
	"com.google.code.gson" % "gson" % "2.7",
	"joda-time" % "joda-time" % "2.9.4",
	"com.datastax.spark" % "spark-cassandra-connector_2.10" % "1.6.1"

)

assemblyExcludedJars in assembly := { 
  val cp = (fullClasspath in assembly).value
  cp filter {x => x.data.getName.matches("sbt.*") || x.data.getName.matches("scalaz.*") || x.data.getName.matches(".*unused.*")}
}