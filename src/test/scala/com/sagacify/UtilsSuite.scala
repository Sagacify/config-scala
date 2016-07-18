package com.sagacify

import org.scalatest.FunSuite

import Config.envGet
import Config.env

class UtilsSuite extends FunSuite {

  test("Config example") {
    assert(env("TEST") == "true")
    assert(envGet("TEST_DEFAULT", "DEFAULT") == "true")
    assert(envGet("NO_DEFAULT", "DEFAULT") == "DEFAULT")
  }

  test("Config fails if environmnent is not set") {
    intercept[Exception] {
      env("SHOULD_FAIL")
    }
  }
}
