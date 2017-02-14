lazy val root = (project in file("."))
  .settings(    // set the name of the project
    name := "hackernews",
    version := "0.0.1",
    // set the Scala version used for the project
    scalaVersion := "2.11.8",

    // add a test dependency on ScalaCheck
    libraryDependencies ++= Seq(
      "org.scala-lang.modules" % "scala-xml_2.11" % "1.0.6",
      "org.ccil.cowan.tagsoup" % "tagsoup"        % "1.2.1",
      "org.typelevel"          %% "cats"          % "0.7.2",
      "com.typesafe.play"      %% "play-json"     % "2.5.1",
      "org.scalatest"          %% "scalatest"     % "3.0.1"  % "test",
      "org.scalacheck"         %% "scalacheck"    % "1.13.4" % "test"
    ),
    // append several options to the list of options passed to the Java compiler
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8")
  )
  .settings(xerial.sbt.Pack.packSettings: _*)
  .settings(packMain := Map("hackernews" -> "Application"))

