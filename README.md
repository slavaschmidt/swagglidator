# swagglidator - an SBT Swagger validator plugin

[![Build Status](https://travis-ci.org/slavaschmidt/swagglidator.svg?branch=master)](https://travis-ci.org/slavaschmidt/swagglidator)

# Usage

```sbt validate```

SBT build will fail in the case of validation errors

By default swagglidator plugin validates all ```json``` and ```yaml``` files in SBT ```sourceDirectory```. 

This can be changed using ```swaggerFiles``` configuration parameter.

The ```deepValidation``` controls whether the validation should be done deeply or not.

### Validation on compile

To run validate whenever compilation is run, enable the plugin with:
```
enablePlugins(swagglidator.SwaggerValidatorPlugin)
```
