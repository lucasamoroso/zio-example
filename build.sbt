import Dependencies._
import sbt._
import sbt.Package.ManifestAttributes
import com.typesafe.sbt.packager.archetypes.JavaAppPackaging
import com.typesafe.sbt.packager.universal.UniversalPlugin
import com.typesafe.sbt.packager.universal.UniversalPlugin.autoImport._

val alias: Seq[sbt.Def.Setting[_]] =
  addCommandAlias(
    "build",
    "all test scalafmtCheck packagedArtifacts publish docker:stage"
  ) ++ addCommandAlias("integrationTest", "it:test") ++
      addCommandAlias("prepare", "fmt; fix") ++
      addCommandAlias("fix", "all compile:scalafix test:scalafix") ++
      addCommandAlias("fixCheck", "; compile:scalafix --check ; test:scalafix --check") ++
      addCommandAlias("fmt", "all scalafmtSbt scalafmtAll") ++
      addCommandAlias("fmtCheck", "all scalafmtSbtCheck scalafmtCheckAll")

lazy val zioExample = project
  .in(file("."))
  .settings(thisBuildSettings)
  .settings(Compile / mainClass := Some("com.lamoroso.example.main.Main"))
  .settings(alias)
  .settings(Defaults.itSettings)
  .configs(IntegrationTest extend Test)
  .enablePlugins(UniversalPlugin)
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(DockerPlugin)

lazy val thisBuildSettings = inThisBuild(
  Seq(
    scalaVersion := "2.13.2",
    name := "zio-example",
    description := "A backend service integrating ZIO with cats, http4s, doobie and tapir",
    startYear := Some(2020),
    Compile / packageDoc / publishArtifact := false,
    packageDoc / publishArtifact := false,
    publish / skip := true,
    Compile / doc / sources := Seq.empty,
    Compile / doc / javaOptions := Seq.empty,
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"),
    IntegrationTest / parallelExecution := false,
    packageOptions := Seq(
          ManifestAttributes(
            ("Implementation-Version", (ThisProject / version).value)
          )
        ),
    libraryDependencies ++= dependencies ++ plugins,
    scalacOptions += "-Ymacro-annotations"
  )
)

lazy val dependencies =
  Cats.all ++
      Http4s.all ++
      Config.all ++
      Streaming.all ++
      ZIO.all ++
      Tapir.all ++
      Doobie.all ++
      Enum.all ++
      STTP.all ++
      Circe.all ++
      Logging.all ++
      Flyway.all ++
      Testing.all

lazy val plugins = Seq(
  compilerPlugin("org.typelevel" %% "kind-projector"     % "0.11.0" cross CrossVersion.full),
  compilerPlugin("io.tryp"        % "splain"             % "0.5.6" cross CrossVersion.patch),
  compilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1"),
  compilerPlugin(scalafixSemanticdb)
)

ThisBuild / scalafixDependencies += "com.nequissimus" %% "sort-imports" % "0.5.0"

packageName in Docker := "zio-cats-backend"
version in Docker := "integration-test"
