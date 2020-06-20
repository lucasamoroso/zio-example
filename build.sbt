import Dependencies._
import sbt._
import sbt.Package.ManifestAttributes
import com.typesafe.sbt.packager.archetypes.JavaAppPackaging
import com.typesafe.sbt.packager.universal.UniversalPlugin
import com.typesafe.sbt.packager.universal.UniversalPlugin.autoImport._

val alias: Seq[sbt.Def.Setting[_]] =
  addCommandAlias("build", "prepare; testJVM") ++ addCommandAlias("prepare", "fix; fmt") ++ addCommandAlias(
    "fix",
    "all compile:scalafix test:scalafix"
  ) ++ addCommandAlias("fixCheck", "; compile:scalafix --check ; test:scalafix --check") ++ addCommandAlias(
    "fmt",
    "all root/scalafmtSbt root/scalafmtAll"
  ) ++ addCommandAlias("fmtCheck", "all root/scalafmtSbtCheck root/scalafmtCheckAll")

lazy val zioExample = project
  .in(file("."))
  .settings(thisBuildSettings)
  .settings(Compile / mainClass := Some("com.lamoroso.example.main.Main"))
  .settings(alias)
  .settings(Defaults.itSettings)
  .enablePlugins(UniversalPlugin)
  .enablePlugins(JavaAppPackaging)

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
    packageOptions := Seq(
      ManifestAttributes(
        ("Implementation-Version", (ThisProject / version).value)
      )
    ),
    libraryDependencies ++= dependencies ++ plugins,
    scalacOptions ++= List(
      "-Yrangepos",
      "-P:semanticdb:synthetics:on"
    )

    // scalacOptions ++= Seq("-Ymacro-annotations", "-Wunused:imports", "-Yrangepos"),
    // semanticdbEnabled := true,                       // enable SemanticDB
    // semanticdbVersion := scalafixSemanticdb.revision // use Scalafix compatible version
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

scalafixDependencies in ThisBuild += "com.nequissimus" %% "sort-imports" % "0.5.0"
