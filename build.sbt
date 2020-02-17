import play.core.PlayVersion.akkaVersion
import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := "live-stocks",
    version := "1.0-SNAPSHOT",
    scalaVersion := "2.13.1",
    libraryDependencies ++= Seq(
      guice,
      ws,
      "org.webjars" % "flot" % "0.8.3-1",
      "org.webjars" % "bootstrap" % "3.3.7",
      "org.julienrf" %% "play-json-derived-codecs" % "7.0.0",
      "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
      "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,
      "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test,
      "org.awaitility" % "awaitility" % "4.0.1" % Test,
      "com.yahoofinance-api" % "YahooFinanceAPI" % "3.15.0",
    ),
    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-Xfatal-warnings"
    )
  ).dependsOn(models.jvm)

lazy val models = crossProject(JSPlatform, JVMPlatform).in(file("models"))
  .settings(
    name := "live-stocks-models",
    version := "1.0-SNAPSHOT",
    scalaVersion := "2.13.1",
    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-Xfatal-warnings"
    )
  )
  .jsSettings(
    libraryDependencies ++= Seq(
      "com.typesafe.play" %%% "play-json" % "2.8.1",
      "org.julienrf" %%% "play-json-derived-codecs" % "7.0.0",
    ),
  )
  .jvmSettings(
    libraryDependencies ++= Seq(
      "com.typesafe.play" %% "play-json" % "2.8.1",
      "org.julienrf" %% "play-json-derived-codecs" % "7.0.0",
    ),
  )

lazy val frontend = (project in file("frontend"))
  .enablePlugins(ScalaJSPlugin)
  .enablePlugins(ScalaJSBundlerPlugin)
  .settings(
    name := "react-scala-test",
    scalaVersion := "2.13.1",

    npmDependencies in Compile += "react" -> "16.8.6",
    npmDependencies in Compile += "react-dom" -> "16.8.6",
    npmDependencies in Compile += "react-proxy" -> "1.1.8",
//    npmDependencies in Compile += "nivo" -> "0.31.0",

    npmDevDependencies in Compile += "file-loader" -> "3.0.1",
    npmDevDependencies in Compile += "style-loader" -> "0.23.1",
    npmDevDependencies in Compile += "css-loader" -> "2.1.1",
    npmDevDependencies in Compile += "html-webpack-plugin" -> "3.2.0",
    npmDevDependencies in Compile += "copy-webpack-plugin" -> "5.0.2",
    npmDevDependencies in Compile += "webpack-merge" -> "4.2.1",

    libraryDependencies += "me.shadaj" %%% "slinky-web" % "0.6.3",
    libraryDependencies += "me.shadaj" %%% "slinky-hot" % "0.6.3",
    libraryDependencies += "com.typesafe.play" %%% "play-json" % "2.8.1",
    //    libraryDependencies += "me.shadaj" %%% "slinky-scalajsreact-interop" % "0.6.3",

    libraryDependencies += "org.scalatest" %%% "scalatest" % "3.1.0" % Test,

    scalacOptions += "-P:scalajs:sjsDefinedByDefault",
    scalacOptions += "-Ymacro-annotations",
//    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full),

    version in webpack := "4.41.6",
    version in startWebpackDevServer := "3.10.3",
//    version in webpack := "4.29.6",

    webpackResources := baseDirectory.value / "webpack" * "*",

    webpackConfigFile in fastOptJS := Some(baseDirectory.value / "webpack" / "webpack-fastopt.config.js"),
    webpackConfigFile in fullOptJS := Some(baseDirectory.value / "webpack" / "webpack-opt.config.js"),
    webpackConfigFile in Test := Some(baseDirectory.value / "webpack" / "webpack-core.config.js"),

    webpackDevServerExtraArgs in fastOptJS := Seq("--inline", "--hot"),
    webpackBundlingMode in fastOptJS := BundlingMode.LibraryOnly(),

    useYarn := true,
    requireJsDomEnv in Test := true,

    addCommandAlias("frontendDev", ";frontend/fastOptJS::startWebpackDevServer;~frontend/fastOptJS"),
    addCommandAlias("frontendBuild", "frontend/fullOptJS::webpack"),
  ).dependsOn(models.js)
