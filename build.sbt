import sbtassembly.AssemblyPlugin.defaultShellScript

name := "redcap-api"
organization in ThisBuild := "com.eztier"
scalaVersion in ThisBuild := "2.12.4"

lazy val compilerOptions = Seq(
  "-unchecked",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-deprecation",
  "-encoding",
  "utf8",
  "-Ylog-classpath",
  "-Ypartial-unification"
)

lazy val global = project
  .in(file("."))
  .settings(settings)
  .aggregate(
    common,
    redcap
  )
  
lazy val commonSettings = Seq(
  version := "0.1.6",
  organization := "com.eztier",
  scalaVersion := "2.12.4",
  scalacOptions ++= compilerOptions,
  resolvers ++= Seq(
    Resolver.sonatypeRepo("public"),
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots"),
    Resolver.bintrayRepo("hseeberger", "maven")
  )
)

lazy val settings = commonSettings

lazy val common = project
  .settings(
    name := "common",
    settings,
    libraryDependencies ++= Seq(
      scalaTest,
      logback,
      akkaStream,
      akkaSlf4j,
      akkaStreamTestkit,
      alpakkaCsv
    )
  )

// Common
val logback = "ch.qos.logback" % "logback-classic" % "1.2.3"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.5" % Test

// akka
val akka = "com.typesafe.akka"
val akkaHttpV = "10.1.5"

val akkaStream = akka %% "akka-stream" % "2.5.18"
val akkaSlf4j = akka %% "akka-slf4j" % "2.5.18"
val akkaStreamTestkit = akka %% "akka-stream-testkit" % "2.5.18" % Test

val alpakkaCsv = "com.lightbend.akka" %% "akka-stream-alpakka-csv" % "1.0-M2"

// HTTP server
val akkaHttp = akka %% "akka-http" % akkaHttpV
val akkaHttpTestkit = akka %% "akka-http-testkit" % akkaHttpV % Test

// akka-http circe
val akkaHttpCirce = "de.heikoseeberger" %% "akka-http-circe" % "1.22.0"

// circe
val circeVersion = "0.10.0"
val circeGenericExtras = "io.circe" %% "circe-generic-extras" % circeVersion
val circeOptics = "io.circe" %% "circe-optics" % circeVersion
val circeJava8 = "io.circe" %% "circe-java8" % "0.11.1"
val circeDerivation = "io.circe" %% "circe-derivation" % "0.10.0-M1"

lazy val redcap = project.
  settings(
    name := "redcap",
    settings,
    assemblySettings,
    libraryDependencies ++= Seq(
      akkaHttp,
      akkaHttpTestkit,
      akkaHttpCirce,
      circeGenericExtras,
      circeJava8,
      circeDerivation,
      circeOptics,
      scalaTest
    )
  ).dependsOn(
    common
  )

// Custom app
lazy val app = project.
  settings(
    name := "app",
    settings,
    assemblySettings,
    /*
    Seq(
      assemblyJarName in assembly := s"redcap-api-${version.value}"
    ),
    */
    Seq(
      javaOptions ++= Seq(
        // "-Dlogback.configurationFile=./logback.xml",
        "-Xms1G",
        "-Xmx3G"
      )
    ),
    libraryDependencies ++= Seq(
      scalaTest
    )
  ).dependsOn(
    common,
    redcap
  )

// Skip tests for assembly  
lazy val assemblySettings = Seq(
  assemblyJarName in assembly := s"${name.value}-${version.value}.jar",
  
  assemblyMergeStrategy in assembly := {
    case PathList("javax", "servlet", xs @ _*)         => MergeStrategy.first
    case PathList(ps @ _*) if ps.last endsWith ".html" => MergeStrategy.first
    case x if x.endsWith("io.netty.versions.properties") => MergeStrategy.first
    case "application.conf"                            => MergeStrategy.concat
    case "logback.xml"                            => MergeStrategy.first
    case x =>
      val oldStrategy = (assemblyMergeStrategy in assembly).value
      oldStrategy(x)
  },
  test in assembly := {}
)
