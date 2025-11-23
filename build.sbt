import sbt.Keys.libraryDependencies

import scala.collection.Seq

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.7.2"

lazy val root = (project in file("."))
  .settings(
    name := "blog-gh-pages",
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "os-lib" % "0.11.4",
      "com.lihaoyi" %% "scalatags" % "0.13.1",
      "com.lihaoyi" %% "cask" % "0.10.2",
      // markdown
      "org.commonmark" % "commonmark" % "0.24.0",
    )
  )
