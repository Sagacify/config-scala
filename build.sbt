// Copyright (C) 2015-2016 Sagacify.

organization := "com.sagacify"

name := "scala-utils"

version := "0.0.2"

scalaVersion := "2.11.8"

scalacOptions ++= Seq("-unchecked", "-feature", "-Ywarn-unused-import")

libraryDependencies ++= Seq(
  "org.json4s"      %% "json4s-jackson" % "latest.integration",
  "org.scalatest"   %% "scalatest"      % "latest.integration" % "test"
)
