import sbt._

import sbtprotobuf.{ProtobufPlugin=>PB}

name := "butterfly"

version := "0.1.0"

seq(PB.protobufSettings: _*)

generatedTargets in PB.protobufConfig <++= (sourceDirectory in Compile){ dir =>
  Seq((dir / "generated" / "scala", "*.scala"))
}

resolvers ++= Seq(
  Resolver.mavenLocal
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" % "akka-actor_2.11" % "2.3.7",
  "com.propensive" %% "rapture-io" % "0.9.0"
)

Revolver.settings