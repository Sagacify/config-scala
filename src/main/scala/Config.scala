package com.sagacify

import ConfigUtils.load
import ConfigUtils.envGet
import ConfigUtils.merge
import ConfigUtils.loadEnv

object Config {
  lazy val cfg: Map[String, Any] = {
    // Default config
    val defCfg = load("./config/default.json")

    // Run env specific config
    val runCfg: Map[String, JValue] = envGet("RUN_ENV")
      .flatMap{ env =>
        val file = Paths.get("./config", f"$env.json".toLowerCase).toFile
        if (file.isFile) {
          Some(merge(Source.fromFile(file, encoding), file.toString, defCfg))
        } else {
          System.err.println(f"WARN: No config found for RUN_ENV=$env")
          None
        }
      }.getOrElse(defCfg)

    // Environement variable
    val envCfg = loadEnv(versionCfg)

    // Check all set and done
    val missing = envCfg.filter{ case (key, value) =>
      value match {
        case None => true
        case _ => false
      }
    }
    if (missing.size == 1)
      throw new NoSuchElementException(
        s"Mandatory variable not set : ${missing.head}")
    if (missing.size > 1)
      throw new NoSuchElementException(
        s"""${missing.size} mandatories variables not set :
            |{${missing.mkString(", ")}}""".stripMargin)
    envCfg
  }

  def s(name: String): String = cfg.get(name) match {
    case None =>
      throw new NoSuchElementException(s"Paramater doesn't exists : $name")
    case Some(s: String) => s
    case Some(s: Any) => {
      System.err.println(f"WARN: casting $s to string")
      s.toString
    }
  }

  def i(name: String): Int = cfg.get(name) match {
    case None =>
      throw new NoSuchElementException(s"Paramater doesn't exists : $name")
    case Some(s: Int) => s
    case Some(s: String) => s.toInt
    case Some(s: Any) => throw new Exception(f"Parameter is not an Int: $name")
  }

  def apply(name: String): Any = cfg(name)
}
