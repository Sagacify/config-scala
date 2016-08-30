package com.sagacify

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

  final def envGet(name: String): Option[String] = {
    Properties.envOrNone(name)
  }

  final def load(path: String): Map[String, JValue] = {
    load(Source.fromFile(path, "UTF-8"))
  }

  final def load(src: Source): Map[String, JValue] = {
    parse(src.mkString) match {
      case JObject(results) => results.toMap
      case _ => throw new Error("Unparsable")
    }
  }

  final def merge(
      path: String,
      config: Map[String, JValue]): Map[String, JValue] = {
    merge(Source.fromFile(path, "UTF-8"), path, config)
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
