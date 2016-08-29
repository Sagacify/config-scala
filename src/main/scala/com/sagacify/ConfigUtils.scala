package com.sagacify

import java.io.InputStream

import scala.io.Source

import scala.util.Properties

object ConfigUtils {

  /** Gets parameter from environment or fails */
  final def env(name: String): String = {
    Properties.envOrNone(name).getOrElse(
      throw new Exception(f"Environment not set : ${name}"))
  }

  /** Gets parameter from environment or use default */
  final def envGet(name: String, default: String): String = {
    Properties.envOrElse(name, default)
  }

  /** Gets paramter from environment or use default */
  final def envGet(name: String, orElse: Option[String]): Option[String] = {
    envGet(name).orElse(orElse)
  }

  final def envGet(name: String): Option[String] = {
    Properties.envOrNone(name)
  }


  final def load(path: String): Map[String, Option[String]] = {
    load(Source.fromFile(path, "UTF-8"))
  }

  final def load(src: InputStream): Map[String, Option[String]] = {
    load(Source.fromInputStream(src, "UTF-8"))
  }

  final def load(src: Source): Map[String, Option[String]] = {
    src.getLines.flatMap(loadLine).toMap
  }


  final def loadInto(path: String, config: Map[String, Option[String]]): Map[String, Option[String]] = {
    loadInto(Source.fromFile(path, "UTF-8"), path, config)
  }

  final def loadInto(src: InputStream, srcName: String, config: Map[String, Option[String]]): Map[String, Option[String]] = {
    loadInto(Source.fromInputStream(src, "UTF-8"), srcName, config)
  }

  final def loadInto(src: Source, srcName: String, config: Map[String, Option[String]]): Map[String, Option[String]] = {
    val child = load(src)
    val newChildKeys = child.keySet -- config.keySet
    if (newChildKeys.size == 1)
      throw new Exception(s"Child config $srcName define a new key : ${newChildKeys.head}")
    if (newChildKeys.size > 1)
      throw new Exception(s"Child config $srcName define ${newChildKeys.size} new keys : {${newChildKeys.mkString(", ")}}")

    (config ++ child)
  }


  final def loadEnv(config: Map[String, Option[String]]): Map[String, Option[String]] = {
    config.keys.map(key => key -> envGet(key, config(key))).toMap
  }


  private final def loadLine(line: String): Option[(String, Option[String])] = {
    val trimmed = line.trim
    if (trimmed.length == 0 || trimmed(0) == '#') {
          None
    } else {
      val index = trimmed.indexOf('=')
      if (index < 0) {
        Some(trimmed -> None)
      } else {
        Some(trimmed.substring(0, index).trim -> Some(trimmed.substring(index + 1).trim))
      }
    }
  }
}
