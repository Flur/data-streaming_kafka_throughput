ThisBuild / version := "0.1"
ThisBuild / scalaVersion := "2.13.5"
ThisBuild / libraryDependencies += "org.apache.kafka" %% "kafka" % "2.7.0"
ThisBuild / libraryDependencies += "org.json4s" %% "json4s-native" % "3.6.11"

lazy val common = (project in file("common"))

lazy val consumer = (project in file("consumer")).dependsOn(common)
lazy val producer = (project in file("producer")).dependsOn(common)
