package swagglidator

import java.io.File

import org.scalatest.{Matchers, FlatSpec}

/**
 * @since 15.06.2015
 */
class ValidateSwaggerTest extends FlatSpec with Matchers {

  val path = "src/test/resources/petstore."

  "Validator Plugin" should "read yaml file" in {
      ValidateSwagger.read(ValidateSwagger.yamlMapper)(new File(s"${path}yaml")).isEmpty shouldBe false
  }

  it should "read json file" in {
    ValidateSwagger.read(ValidateSwagger.jsonMapper)(new File(s"${path}json")).isEmpty shouldBe false
  }

  it should "validate json file" in {
    val json = ValidateSwagger.read(ValidateSwagger.jsonMapper)(new File(s"${path}json"))
    val report = ValidateSwagger.validateJson(json)
    report.isSuccess shouldBe true
  }

  it should "validate yaml file" in {
    val yaml = ValidateSwagger.read(ValidateSwagger.yamlMapper)(new File(s"${path}yaml"))
    val report = ValidateSwagger.validateJson(yaml)
    report.isSuccess shouldBe true
  }

  it should "NOT validate yaml file" in {
    val yaml = ValidateSwagger.read(ValidateSwagger.yamlMapper)(new File(s"${path}bad_yaml"))
    val report = ValidateSwagger.validateJson(yaml)
    report.isSuccess shouldBe false
  }
}
