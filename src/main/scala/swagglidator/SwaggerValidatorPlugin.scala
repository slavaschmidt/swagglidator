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
    val deepValidation = settingKey[Boolean]("Validate deeply")

    // default values for the tasks and settings
    lazy val baseValidateSwaggerSettings: Seq[Def.Setting[_]] = Seq(
      validate := {
        ValidateSwagger((swaggerFiles in validate).value, (baseDirectory in validate).value, (deepValidation in validate).value, streams.value.log)
      },
      swaggerFiles in validate := Seq(s"*$YAML", s"*$JSON"),
      deepValidation in validate := true,
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

  def apply(sources: Seq[String], base: File, deep: Boolean, log: Logger): Unit = sources match {
    case a if a.nonEmpty => validateFiles (files(sources, base).get, deep) foreach failIfUnsuccessful(log)
    case o =>
  }

  def files(sources: Seq[String], base: File): PathFinder =
    base ** sources.map(globFilter).reduce(_ | _)

  def failIfUnsuccessful(log: Logger)(report: (ProcessingReport, String)) =
    if (!report._1.isSuccess) {
      log.error(s"Validation FAILURE: ${report._2}" )
      log.info(report._1.toString)
      throw new IllegalStateException("Swagger validation failed")
    } else {
      log.info(s"Validation success: ${report._2}" )
    }

  def validateFiles(files: Seq[File], deep: Boolean) = if (files.nonEmpty) {
    val names = files map (_.getAbsolutePath)
    val mappers = names map mapperByName
    val jsonStrings = mappers zip files map read
    jsonStrings map validateJson(deep) zip names
  } else Nil

  def read(mapperAndFile: (ObjectMapper, File)) = {
    val node = mapperAndFile._1.readTree(mapperAndFile._2)
    new ObjectMapper().writeValueAsString(node)
  }

  def mapperByName(name: String) = if (name.endsWith(YAML)) yamlMapper else jsonMapper

  val yamlMapper = new ObjectMapper(new YAMLFactory())
  val jsonMapper = new ObjectMapper

  def validateJson(deep: Boolean)(jsonString: String): ProcessingReport = {
    val json = JsonLoader.fromString(jsonString)
    val factory = JsonSchemaFactory.byDefault()
    val schema = factory.getJsonSchema(swaggerSchemaUrl)
    schema.validate(json, deep)
  }
}