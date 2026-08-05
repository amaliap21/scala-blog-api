ThisBuild / scalaVersion := "2.13.12"
ThisBuild / version      := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.nolimit"

addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.13.2" cross CrossVersion.full)

lazy val root = (project in file("."))
  .enablePlugins(JavaAppPackaging)
  .settings(
    name := "scala-blog-api",
    scalacOptions ++= Seq(
      "-Ymacro-annotations"
    ),
    libraryDependencies ++= Seq(
      "org.http4s"      %% "http4s-ember-server" % "0.23.23",
      "org.http4s"      %% "http4s-ember-client" % "0.23.23",
      "org.http4s"      %% "http4s-dsl"          % "0.23.23",
      "org.http4s"      %% "http4s-circe"        % "0.23.23",
      "io.circe"        %% "circe-generic"       % "0.14.6",
      "org.tpolecat"    %% "doobie-core"         % "1.0.0-RC4",
      "org.tpolecat"    %% "doobie-postgres"     % "1.0.0-RC4",
      "org.tpolecat"    %% "doobie-hikari"       % "1.0.0-RC4",
      "org.mindrot"      % "jbcrypt"             % "0.4",
      "com.github.jwt-scala" %% "jwt-circe"      % "9.4.4",
      "org.typelevel"   %% "log4cats-slf4j"      % "2.6.0",
      "ch.qos.logback"   % "logback-classic"     % "1.4.11",
      "org.scalatest"   %% "scalatest"           % "3.2.17" % Test
    )
  )
