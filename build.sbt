import sbtrelease.ReleasePlugin.autoImport._
import sbtrelease.ReleaseStateTransformations._

sbtPlugin := true

scalaVersion := "2.12.6"

crossSbtVersions := Seq("0.13.17", "1.2.6")

organization := "de.zalando"

name := "sbt-swagglidator"

libraryDependencies ++= Seq(
  "com.github.fge" % "json-schema-validator" % "2.2.6",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.9.7",
  "com.fasterxml.jackson.dataformat" % "jackson-dataformat-yaml" % "2.9.7",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)

licenses += ("MIT", url("https://opensource.org/licenses/MIT"))

autoAPIMappings := true
bintrayOrganization := Some("???")
bintrayRepository := "???"
pomIncludeRepository := { _ => false }
publishArtifact in Test := false
publishArtifact in (Compile, packageDoc) := true
publishArtifact in (Compile, packageSrc) := true
homepage := Some(new URL("https://github.com/slavaschmidt/swagglidator"))
developers := List(
  Developer(
    id = "slavaschmidt",
    name = "Slava Schmidt",
    email = "???",
    url = url("https://github.com/slavaschmidt")
  ),
  Developer(
    id = "pcejrowski",
    name = "Paweł Cejrowski",
    email = "pcejrowski@gmail.com",
    url = url("https://github.com/pcejrowski")
  )
)
scmInfo := Some(
  ScmInfo(
    url("https://github.com/slavaschmidt/swagglidator"),
    "scm:git:git://github.com/slavaschmidt/swagglidator.git"
  )
)


val ReleaseSettings = Seq(
  releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    releaseStepCommandAndRemaining("^clean"),
    releaseStepCommandAndRemaining("^test"),
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    releaseStepCommandAndRemaining("^publish"),
    setNextVersion,
    commitNextVersion,
    pushChanges
  )
)