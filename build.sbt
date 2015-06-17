scalaVersion := "2.10.4"

sbtPlugin := true

name := "sbt-swagglidator"

organization := "de.zalando"

version := "0.1"

libraryDependencies ++= Seq(
  "com.github.fge"                    % "json-schema-validator"   % "2.2.6",
  "com.fasterxml.jackson.core"        % "jackson-databind"        % "2.4.4",
  "com.fasterxml.jackson.dataformat"  % "jackson-dataformat-yaml" % "2.4.4",
  "org.scalatest"       %% "scalatest"        % "2.2.3"        % "test"
)
