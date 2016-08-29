package com.sagacify

import org.scalatest.FunSuite

import ConfigUtils.envGet
import ConfigUtils.env

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

  test("Config") {
    Config("required1")
    assert(Config("opt1") == "111")
    assert(Config("opt2") == "new2")
    assert(Config.i("required2") == 2)
    assert(Config("opt3") == "from_env_3")
    assert(Config("opt4") == "from_env_4")
  }
}
