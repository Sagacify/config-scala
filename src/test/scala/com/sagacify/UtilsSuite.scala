package com.sagacify

import org.scalatest.FunSuite

class UtilsSuite extends FunSuite {

  test("getEnvironmentVariables example") {

    val config = Utils.getEnvironmentVariables(
      Seq(
        "TEST",
        ("TEST_DEFAULT", "DEFAULT"),
        ("NO_DEFAULT", "DEFAULT")
      )
    )
    assert(config("TEST") == "True")
    assert(config("TEST_DEFAULT") == "True")
    assert(config("NO_DEFAULT") == "DEFAULT")
  }

  test("getEnvironmentVariables fails if environmnent is not set") {
    intercept[Exception] {
      // If no defaul
      Utils.getEnvironmentVariables(Seq("SHOULD_FAIL"))
    }
  }

  test("getEnvironmentVariables fails on wrong imput") {
    /* Scala */
    intercept[Exception] {
      Utils.getEnvironmentVariables(Seq(-1))
    }
  }
}
