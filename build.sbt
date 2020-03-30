scalaVersion := "2.12.7"

organization := "com.example"

lazy val `cats-effect-toy` = (project in file("."))
  .settings(name := "Cats Effect Toy")

resolvers += "Sonatype OSS Snapshots" at
  "https://oss.sonatype.org/content/repositories/releases"

testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework")

parallelExecution in Test := false

libraryDependencies += "org.typelevel" %% "cats-effect" % "2.1.0"
libraryDependencies += "org.specs2" %% "specs2-core" % "4.8.3" % Test
libraryDependencies += "org.specs2" %% "specs2-mock" % "4.8.3" % Test
libraryDependencies += "com.storm-enroute" %% "scalameter" % "0.18"
