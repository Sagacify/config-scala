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
    envCfg.keys.map(key => key -> envCfg(key).getOrElse(throw new Exception(s"Mandatory variable not set : $key"))).toMap
  }

  def s(name: String): String = cfg.getOrElse(name, throw new NoSuchElementException(s"Paramaters doesn't exists : $name"))

  def i(name: String): Int = cfg.getOrElse(name, throw new NoSuchElementException(s"Paramaters doesn't exists : $name")).toInt

  def apply(name: String): String = s(name)
}
