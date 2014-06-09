name := "thesis"

version := "1.0"

scalaVersion := "2.10.4"

resolvers ++= Seq(
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  "spring" at "http://repo.spring.io/milestone",
  "neo4j-releases" at "http://m2.neo4j.org/releases/",
  "Cloudera Hadoop Releases" at "https://repository.cloudera.com/content/repositories/releases/",
  "Thrift location" at "http://people.apache.org/~rawson/repo/",
  "opennlp sourceforge repo" at "http://opennlp.sourceforge.net/maven2"
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.2",
  "com.ning" % "async-http-client" % "1.7.19",
  "ch.qos.logback" % "logback-classic" % "1.0.7",
  "com.github.scala-incubator.io" %% "scala-io-core" % "0.4.3",
  "com.github.scala-incubator.io" %% "scala-io-file" % "0.4.3",
  "org.apache.pdfbox" % "pdfbox" % "1.8.5",
  "org.springframework.data" % "spring-data-neo4j" % "3.1.0.RC1",
  "javax.validation"         % "validation-api"            % "1.0.0.GA"      % "compile",
  "org.springframework.boot" % "spring-boot-starter" % "1.0.1.RELEASE",
  "org.springframework" % "spring-test" % "4.0.0.RELEASE" % "test",
  "org.scalatest" % "scalatest_2.10" % "2.1.3" % "test",
  "org.apache.opennlp" % "opennlp-tools" % "1.5.3",
  "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2",
  "com.typesafe" % "config" % "1.2.1"
)

scalacOptions ++= Seq("-feature", "-language:implicitConversions")