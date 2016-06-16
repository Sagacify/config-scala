package com.sagacify

import scala.util.Properties

object Utils {

  /** Gets paramter from environment or fails */
  private def env(name: String): String = {
    Properties.envOrNone(name).getOrElse(
      throw new Exception(f"Environment not set : ${name}"))
  }

  /** Gets paramter from environment or use default */
  private def env_get(name: String, default: String): String = {
    Properties.envOrElse(name, default)
  }

  /** Transforms a list of strings/Tuple2(String, String) into a parameter map

  i.e. : Seq("needed", ("optional", "default_value"))
  */
  final def getEnvironmentVariables(vars: Seq[Any]): Map[String, String] = {
    vars.map{ variable =>
      variable match {
        case key: String => (key -> env(key))
        case (key: String, default: String) => (key -> env_get(key, default))
        case e: Any => throw new Exception(f"Property not understood: ${e}")
      }
    }.toMap
  }
}
