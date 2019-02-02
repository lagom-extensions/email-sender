import sbt.file

ThisBuild / scalaVersion := "2.12.8"
ThisBuild / organization := "com.github.lagom-extensions"
ThisBuild / homepage := Some(url("https://github.com/lagom-extensions/email-sender"))
ThisBuild / scmInfo := Some(ScmInfo(url("https://github.com/lagom-extensions/email-sender"), "git@github.com:lagom-extensions/email-sender.git"))
ThisBuild / developers := List(Developer("kuzkdmy", "Dmitriy Kuzkin", "mail@dmitriy.kuzkin@gmail.com", url("https://github.com/kuzkdmy")))
ThisBuild / licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0"))
ThisBuild / publishMavenStyle := true

lazy val root = (project in file("."))
  .aggregate(
    emailSenderApi,
    emailSenderImpl
  )

lazy val emailSenderApi = project
  .in(file("email-sender-api"))
  .settings(
    libraryDependencies := Seq(
      lagomScaladslApi,
      "com.beachape" %% "enumeratum"               % "1.5.13",
      "org.julienrf" %% "play-json-derived-codecs" % "4.0.0"
    )
  )

lazy val emailSenderImpl = project
  .in(file("email-sender-impl"))
  .dependsOn(emailSenderApi)
  .enablePlugins(JavaAppPackaging, LagomScala)
  .settings(
    libraryDependencies := Seq(
      "com.sun.mail"             % "javax.mail"             % "1.6.2",
      "javax.mail"               % "javax.mail-api"         % "1.6.2",
      "org.springframework"      % "spring-context-support" % "5.1.4.RELEASE",
      "com.jsuereth"             %% "scala-arm"             % "2.0",
      "org.scalatest"            %% "scalatest"             % "3.0.0" % "test",
      "com.softwaremill.macwire" %% "macros"                % "2.2.5" % "provided",
      lagomScaladslServer,
      lagomScaladslDevMode,
      lagomLogback,
      lagomScaladslTestKit
    )
  )

publishTo := Some(
  if (isSnapshot.value) Opts.resolver.sonatypeSnapshots
  else Opts.resolver.sonatypeStaging
)

parallelExecution in ThisBuild := false
