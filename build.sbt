import sbt._

import sbtprotobuf.{ProtobufPlugin=>PB}

name := "butterfly"

version := "0.1.0"

seq(PB.protobufSettings: _*)

resolvers ++= Seq(
  Resolver.mavenLocal
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" % "akka" % "2.2.0-RC2"
)

Revolver.settings