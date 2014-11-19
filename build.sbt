import sbt._

import sbtprotobuf.{ProtobufPlugin=>PB}

name := "butterfly"

version := "0.1.0"

seq(PB.protobufSettings: _*)

generatedTargets in PB.protobufConfig <++= (sourceDirectory in Compile){ dir =>
  Seq((dir / "generated" / "scala", "*.scala"))
}

resolvers ++= Seq(
  Resolver.mavenLocal,
  "gideondk-repo" at "https://raw.github.com/gideondk/gideondk-mvn-repo/master",
  "Typesafe Releases" at "https://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.7",
  "com.propensive" %% "rapture-io" % "0.9.0",
  "nl.gideondk" %% "sentinel" % "0.7.5.1",
  "com.typesafe.akka" %% "akka-stream-experimental" % "0.10"
)

Revolver.settings