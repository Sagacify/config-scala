package com.sagacify

import java.nio.file.Paths

import scala.io.Source
import scala.util.Properties

import org.json4s.jackson.JsonMethods.parse
import org.json4s.JBool
import org.json4s.JDecimal
import org.json4s.JDouble
import org.json4s.JInt
import org.json4s.JLong
import org.json4s.JNull
import org.json4s.JObject
import org.json4s.JObject
import org.json4s.JString
import org.json4s.JValue


object Config {

  final private val encoding = "UTF-8"

  lazy val cfg: Map[String, Any] = {
    // Default config
    val defCfg: Map[String, JValue] =
      load(Source.fromFile("./config/default.json", encoding))

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

    // Check if there are environment variables overiding the config values
    val envCfg = loadEnv(runCfg)

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

    /** Gets parameter from environment or fails */
  final def env(name: String): String = {
    Properties.envOrNone(name).getOrElse(
      throw new Exception(f"Environment not set : ${name}"))
  }

  /** Gets parameter from environment or use default */
  final def envGet(name: String, default: String): String = {
    Properties.envOrElse(name, default)
  }

  /** Gets parameter from environment or None */
  final def envGet(name: String): Option[String] = {
    Properties.envOrNone(name)
  }

  final def load(src: Source): Map[String, JValue] = {
    parse(src.mkString) match {
      case JObject(results) => results.toMap
      case _ => throw new Error("Unparsable")
    }
  }

  final def merge(
      src: Source,
      srcName: String,
      config: Map[String, JValue]): Map[String, JValue] = {
    val child = load(src)
    val newChildKeys = child.keySet -- config.keySet
    if (newChildKeys.size == 1)
      throw new Exception(
        s"Child config $srcName defines a new key : ${newChildKeys.head}")
    if (newChildKeys.size > 1)
      throw new Exception(
        s"""Child config $srcName defines ${newChildKeys.size} new keys :
            |{${newChildKeys.mkString(", ")}}""".stripMargin)
    (config ++ child)
  }

  private final def unbox(key: String, value: JValue): Any = {
    value match {
      case JNull => env(key)
      case JString(s) => envGet(key, s)
      case JDouble(num) =>  envGet(key).map(_.toDouble).getOrElse(num)
      case JDecimal(num) =>  envGet(key).map(_.toDouble).getOrElse(num)
      case JInt(num) =>  envGet(key).map(_.toInt).getOrElse(num)
      case JLong(num) =>  envGet(key).map(_.toDouble).getOrElse(num)
      case JBool(value) => envGet(key).map(_.toBoolean).getOrElse(value)
      case _ =>  throw new Error(f"Unsuported value: $value")
    }
  }

  final def loadEnv(config: Map[String, JValue]): Map[String, Any] = {
    config.map{ case (key, value) =>
      (key -> unbox(key, value))
    }
  }
}
