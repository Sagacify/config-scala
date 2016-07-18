package com.sagacify

import scala.util.Properties

object Config {

  /** Gets paramter from environment or fails */
  final def env(name: String): String = {
    Properties.envOrNone(name).getOrElse(
      throw new Exception(f"Environment not set : ${name}"))
  }

  /** Gets paramter from environment or use default */
  final def envGet(name: String, default: String): String = {
    Properties.envOrElse(name, default)
  }
}
