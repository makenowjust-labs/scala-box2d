Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / githubOwner := "MakeNowJust-Labo"
ThisBuild / githubRepository := "scala-box2d"

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
ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.4.4"
ThisBuild / scalafixDependencies += "com.github.vovapolu" %% "scaluzzi" % "0.1.16"

lazy val root = project
  .in(file("."))
  .settings(publish / skip := true)
  .aggregate(box2dJVM, box2dJS, box2dDemoJVM, box2dDemoJS)

lazy val box2d = crossProject(JVMPlatform, JSPlatform)
  .in(file("modules/box2d"))
  .settings(
    organization := "codes.quine.labo",
    name := "box2d",
    version := "0.2.1-SNAPSHOT",
    description := "A box2d-lite port in Scala",
    console / initialCommands := """
      |import codes.quine.labo.box2d._
      |import codes.quine.labo.box2d.MathUtil._
      """.stripMargin,
    Compile / console / scalacOptions -= "-Wunused",
    // Scaladoc options:
    // Set URL mapping of scala standard API for Scaladoc.
    apiMappings ++= scalaInstance.value.libraryJars
      .filter(file => file.getName.startsWith("scala-library") && file.getName.endsWith(".jar"))
      .map(_ -> url(s"http://www.scala-lang.org/api/${scalaVersion.value}/"))
      .toMap,
    Compile / doc / scalacOptions ++= Seq(
      "-doc-title",
      "scala-box2d",
      "-doc-version",
      if (version.value.endsWith("-SNAPSHOT"))
        sys.process.Process("git rev-parse --short HEAD").!!.stripLineEnd
      else version.value
    )
  )
  .jvmSettings(
    // Settings for test on JVM:
    libraryDependencies += "io.monix" %% "minitest" % "2.9.1" % Test,
    testFrameworks += new TestFramework("minitest.runner.Framework")
  )
  .jsSettings(
    // Settings for test on JS:
    libraryDependencies += "io.monix" %%% "minitest" % "2.9.1" % Test,
    testFrameworks += new TestFramework("minitest.runner.Framework")
  )

lazy val box2dJVM = box2d.jvm
lazy val box2dJS = box2d.js

lazy val box2dDemo = crossProject(JVMPlatform, JSPlatform)
  .in(file("modules/box2d-demo"))
  .settings(
    name := "box2d-demo",
    publish / skip := true,
    mainClass := Some("codes.quine.labo.box2d.demo.DemoApp")
  )
  .jvmSettings(
    run / fork := true,
    // Dependencies on JVM:
    libraryDependencies += "org.scalafx" %% "scalafx" % "15.0.1-R20",
    libraryDependencies ++= javaFXModules.map(m => "org.openjfx" % s"javafx-$m" % "16-ea+5" classifier osName)
  )
  .jsSettings(
    scalaJSUseMainModuleInitializer := true,
    // Dependencies on JS:
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "1.1.0"
  )
  .dependsOn(box2d)

lazy val box2dDemoJVM = box2dDemo.jvm
lazy val box2dDemoJS = box2dDemo.js

lazy val osName = System.getProperty("os.name") match {
  case n if n.startsWith("Linux")   => "linux"
  case n if n.startsWith("Mac")     => "mac"
  case n if n.startsWith("Windows") => "win"
  case _                            => throw new Exception("Unknown platform!")
}

lazy val javaFXModules = Seq("base", "controls", "graphics", "media")
