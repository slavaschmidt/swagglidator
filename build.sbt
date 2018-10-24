scalaVersion := "2.12.6"

sbtPlugin := true

crossSbtVersions := Seq("0.13.17", "1.2.6")

name := "sbt-swagglidator"

organization := "de.zalando"

version := "0.1"

libraryDependencies ++= Seq(
  "com.github.fge" % "json-schema-validator" % "2.2.6",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.9.7",
  "com.fasterxml.jackson.dataformat" % "jackson-dataformat-yaml" % "2.9.7",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)
