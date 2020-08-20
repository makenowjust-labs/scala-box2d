Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / githubOwner := "MakeNowJust-Labo"
ThisBuild / githubRepository := "scala-labo-template"

ThisBuild / scalaVersion := "2.13.3"
ThisBuild / scalacOptions ++= Seq(
  "-encoding",
  "UTF-8",
  "-feature",
  "-deprecation",
  "-Wunused"
)

// Scalafix config:
ThisBuild / scalafixScalaBinaryVersion := "2.13"
ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision
ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.4.0"
ThisBuild / scalafixDependencies += "com.github.vovapolu" %% "scaluzzi" % "0.1.12"

lazy val root = project
  .in(file("."))
  .aggregate(box2d, demo)

lazy val box2d = project
  .in(file("modules/box2d"))
  .settings(
    organization := "codes.quine.labo",
    name := "box2d",
    version := "0.1.0-SNAPSHOT",
    description := "A box2d-lite port in Scala",
    console / initialCommands := """
      |import codes.quine.labo.box2d._
      |import codes.quine.labo.box2d.MathUtil._
      """.stripMargin,
    Compile / console / scalacOptions -= "-Wunused",
    // Set URL mapping of scala standard API for Scaladoc.
    apiMappings ++= scalaInstance.value.libraryJars
      .filter(file => file.getName.startsWith("scala-library") && file.getName.endsWith(".jar"))
      .map(_ -> url(s"http://www.scala-lang.org/api/${scalaVersion.value}/"))
      .toMap,
    // Settings for test:
    libraryDependencies += "io.monix" %% "minitest" % "2.8.2" % Test,
    testFrameworks += new TestFramework("minitest.runner.Framework")
  )

lazy val demo = project
  .in(file("modules/box2d-demo"))
  .settings(
    name := "box2d-demo",
    publishTo := None,
    run / mainClass := Some("codes.quine.labo.box2d.demo.Demo"),
    run / fork := true,
    // Dependencies:
    libraryDependencies += "org.scalafx" %% "scalafx" % "14-R19",
    libraryDependencies ++= javaFXModules.map(m => "org.openjfx" % s"javafx-$m" % "14.0.1" classifier osName),
  )
  .dependsOn(box2d)

lazy val osName = System.getProperty("os.name") match {
  case n if n.startsWith("Linux")   => "linux"
  case n if n.startsWith("Mac")     => "mac"
  case n if n.startsWith("Windows") => "win"
  case _                            => throw new Exception("Unknown platform!")
}

lazy val javaFXModules = Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")
