name := "spark-ml-example"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.apache.spark" % "spark-core_2.11" % "2.0.0",
  "org.apache.spark" % "spark-sql_2.11" % "2.0.0",
  "org.apache.spark" % "spark-mllib_2.11" % "2.0.0",
  "edu.stanford.nlp" % "stanford-corenlp" % "3.6.0",
  "edu.stanford.nlp" % "stanford-corenlp" % "3.6.0" classifier "models",
  "com.google.protobuf" % "protobuf-java" % "2.6.1",
  "com.pygmalios" % "reactiveinflux-spark_2.11" % "1.4.0.10.0.5.1"
)

resolvers += "Maven Central Server" at "http://repo1.maven.org/maven2"
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
resolvers += Resolver.mavenLocal
resolvers += Resolver.typesafeIvyRepo("releases")
resolvers += "Typesafe Releases" at "https://repo.typesafe.com/typesafe/releases/"