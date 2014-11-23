import sbt._
import Keys._

name := "butterfly"

version := "0.1.0"

scalaVersion := "2.11.2"

resolvers ++= Seq(
  Classpaths.typesafeReleases,
  Classpaths.typesafeSnapshots,
  Resolver.mavenLocal,
  "Typesafe Repository (releases)" at "http://repo.typesafe.com/typesafe/releases/",
  "Scala Tools Repository (snapshots)" at "http://scala-tools.org/repo-snapshots",
  "Scala Tools Repository (releases)"  at "http://scala-tools.org/repo-releases",
  "gideondk-repo" at "https://raw.github.com/gideondk/gideondk-mvn-repo/master",
  "Sonatype OSS Releases" at "http://oss.sonatype.org/content/repositories/releases/",
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.7",
  "nl.gideondk" %% "sentinel" % "0.7.5.1",
  "com.typesafe.akka" %% "akka-stream-experimental" % "0.10",
  "com.basho.riak.protobuf" % "riak-pb" % "2.0.0.16"
)

Revolver.settings