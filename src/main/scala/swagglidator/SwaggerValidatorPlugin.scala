package swagglidator

import java.io.File

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.core.report.ProcessingReport
import com.github.fge.jsonschema.main.JsonSchemaFactory

import sbt._
import sbt.Keys._

/**
 * @since 12.06.2015
 */
object SwaggerValidatorPlugin extends AutoPlugin {

  import ValidateSwagger._

  object autoImport {
    val validate = taskKey[Unit]("Validates swagger specifications.")
    val swaggerFiles = settingKey[Seq[String]]("Validate swagger specs.")

    // default values for the tasks and settings
    lazy val baseValidateSwaggerSettings: Seq[Def.Setting[_]] = Seq(
      validate := {
        ValidateSwagger((swaggerFiles in validate).value, (baseDirectory in validate).value)
      },
      swaggerFiles in validate := Seq(s"*$YAML", s"*$JSON"),
      baseDirectory in validate := sourceDirectory.value
    )
  }

  import autoImport._

  override def requires = sbt.plugins.JvmPlugin

  // This plugin is automatically enabled for projects which are JvmPlugin.
  override def trigger = allRequirements

  // a group of settings that are automatically added to projects.
  override val projectSettings =
    inConfig(Compile)(baseValidateSwaggerSettings)

  compile in Compile := {
    (validate in validate).value
    (compile in Compile).value
  }

}

object ValidateSwagger {
  val YAML = ".yaml"
  val JSON = ".json"

  val swaggerSchemaUrl = "https://raw.githubusercontent.com/swagger-api/swagger-spec/master/schemas/v2.0/schema.json"

  def apply(sources: Seq[String], base: File): Unit = sources match {
    case a if a.nonEmpty => validateFiles (files(sources, base).get) foreach failIfUnsuccessful
    case o =>
  }

  def files(sources: Seq[String], base: File): PathFinder =
    base ** sources.map(globFilter).reduce(_ | _)

  def failIfUnsuccessful(report: ProcessingReport) =
    if (!report.isSuccess) {
      println(report)
      throw new IllegalStateException("Swagger validation failed")
    }

  def validateFiles(files: Seq[File]) = if (files.nonEmpty) {
    val byType = files partition { _.name.endsWith(YAML) }
    val jsonStrings = (byType._1 map read(yamlMapper)) :: (byType._2 map read(jsonMapper)) :: Nil
    jsonStrings.flatten map validateJson
  } else Nil

  def read(mapper: ObjectMapper)(file: File) = {
    val node = mapper.readTree(file)
    new ObjectMapper().writeValueAsString(node)
  }

  val yamlMapper = new ObjectMapper(new YAMLFactory())
  val jsonMapper = new ObjectMapper

  def validateJson(jsonString: String): ProcessingReport = {
    val json = JsonLoader.fromString(jsonString)
    val factory = JsonSchemaFactory.byDefault()
    val schema = factory.getJsonSchema(swaggerSchemaUrl)
    schema.validate(json, true)
  }
}