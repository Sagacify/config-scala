package com.sagacify

import ConfigUtils.load
import ConfigUtils.envGet
import ConfigUtils.loadInto
import ConfigUtils.loadEnv

object Config {
  lazy val cfg: Map[String, String] = {
    // Default config
    val defCfg = load("./config/default.cfg")

    // Specific config
    val versionCfg = envGet("RUN_ENV").map(env => loadInto("./config/" + env, defCfg)).getOrElse(defCfg)

    // Environement variable
    val envCfg = loadEnv(versionCfg)

    // Check all set and done
    val missing = envCfg.keys.filter(key => ! envCfg(key).isDefined).toSeq
    if (missing.size == 1)
      throw new Exception(s"Mandatory variable not set : ${missing.head}")
    if (missing.size > 1)
      throw new Exception(s"${missing.size} mandatories variables not set : {${missing.mkString(", ")}}")

    envCfg.keys.map(key => key -> envCfg(key).get).toMap
  }

  def s(name: String): String = cfg.getOrElse(name, throw new NoSuchElementException(s"Paramaters doesn't exists : $name"))

  def i(name: String): Int = cfg.getOrElse(name, throw new NoSuchElementException(s"Paramaters doesn't exists : $name")).toInt

  def apply(name: String): String = s(name)
}
