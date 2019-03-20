organization in ThisBuild := "com.example"
version in ThisBuild := "1.0-SNAPSHOT"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.12.4"

val macwire = "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.4" % Test
val akkaManagementClusterHttp = "com.lightbend.akka.management" %% "akka-management-cluster-http" % "1.0.0-RC2"
val postgresDriver = "org.postgresql" % "postgresql" % "42.2.5"

lazy val `hello-world` = (project in file("."))
  .aggregate(`hello-world-api`, `hello-world-impl`)

lazy val `hello-world-api` = (project in file("hello-world-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `hello-world-impl` = (project in file("hello-world-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceJdbc,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      macwire,
      scalaTest,
      postgresDriver,
      akkaManagementClusterHttp
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`hello-world-api`)

