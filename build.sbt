// Copyright (C) 2015-2016 Sagacify.

organization := "com.sagacify"

name := "scala-utils"

version := "0.0.1"

scalaVersion := "2.11.8"

scalacOptions ++= Seq("-unchecked", "-feature", "-Ywarn-unused-import")

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.6" % "test"
