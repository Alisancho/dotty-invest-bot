import sbt.Keys.libraryDependencies
import sbtassembly.MergeStrategy

lazy val root = project
  .in(file("."))
  .settings(
    name := "dotty-simple",
    version := "0.1.0",
    scalaVersion := "0.25.0-RC2",
    libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test",
    libraryDependencies += ("com.typesafe.akka"        %% "akka-actor"              % "2.6.4").withDottyCompat(scalaVersion.value),
    libraryDependencies += ("com.typesafe.akka"        %% "akka-testkit"            % "2.6.4" % Test).withDottyCompat(scalaVersion.value),
    libraryDependencies += ("com.typesafe.akka"        %% "akka-stream"             % "2.6.4").withDottyCompat(scalaVersion.value),
    libraryDependencies += ("com.typesafe.akka"        %% "akka-stream-testkit"     % "2.6.4" % Test).withDottyCompat(scalaVersion.value),
    libraryDependencies += ("com.typesafe.akka"        %% "akka-http"               % "10.1.11").withDottyCompat(scalaVersion.value),
    libraryDependencies += ("com.typesafe.akka"        %% "akka-http-testkit"       % "10.1.11" % Test).withDottyCompat(scalaVersion.value),
    libraryDependencies += ("com.typesafe.akka"        %% "akka-persistence"        % "2.6.4").withDottyCompat(scalaVersion.value),
    libraryDependencies += ("com.typesafe.akka"        %% "akka-distributed-data"   % "2.6.4").withDottyCompat(scalaVersion.value),
    libraryDependencies += ("com.typesafe.akka"        %% "akka-slf4j"              % "2.6.4").withDottyCompat(scalaVersion.value),
    libraryDependencies += ("ch.qos.logback"           % "logback-classic"          % "1.2.3").withDottyCompat(scalaVersion.value),
    libraryDependencies += ("ch.qos.logback"           % "logback-core"             % "1.2.3").withDottyCompat(scalaVersion.value),
    libraryDependencies += ("net.logstash.logback"     % "logstash-logback-encoder" % "6.2").withDottyCompat(scalaVersion.value),
    libraryDependencies += ("org.typelevel"            %% "cats-effect"             % "2.0.0").withDottyCompat(scalaVersion.value),
    libraryDependencies += ("org.typelevel"            %% "cats-core"               % "2.0.0").withDottyCompat(scalaVersion.value),
    libraryDependencies += ("co.fs2"                   %% "fs2-core"                % "2.0.1").withDottyCompat(scalaVersion.value),
    libraryDependencies += ("co.fs2"                   %% "fs2-io"                  % "2.0.1").withDottyCompat(scalaVersion.value),
    libraryDependencies += ("com.softwaremill.macwire" %% "macrosakka"              % "2.3.3" % "provided")
      .withDottyCompat(scalaVersion.value),
    libraryDependencies += ("com.softwaremill.macwire" %% "macros" % "2.3.3" % "provided").withDottyCompat(scalaVersion.value),
    libraryDependencies += ("com.softwaremill.macwire" %% "proxy"  % "2.3.3").withDottyCompat(scalaVersion.value),
    libraryDependencies += ("com.softwaremill.macwire" %% "util"   % "2.3.3").withDottyCompat(scalaVersion.value),
    libraryDependencies += ("ru.tinkoff.invest" % "openapi-java-sdk"         % "0.4.1" pomOnly ()),
    libraryDependencies += ("ru.tinkoff.invest" % "openapi-java-sdk-example" % "0.4.1"),
    libraryDependencies += ("ru.tinkoff.invest" % "openapi-java-sdk-core"    % "0.4.1"),
    libraryDependencies += ("io.getquill"       %% "quill-jdbc-monix"    % "3.4.10").withDottyCompat(scalaVersion.value),
    libraryDependencies += ("io.getquill"       %% "quill-core"          % "3.4.10").withDottyCompat(scalaVersion.value),
    libraryDependencies += ("io.getquill"       %% "quill-sql"           % "3.4.10").withDottyCompat(scalaVersion.value),
    libraryDependencies += ("mysql"             % "mysql-connector-java" % "8.0.18").withDottyCompat(scalaVersion.value),
    libraryDependencies += ("io.projectreactor" % "reactor-core"         % "3.3.5.RELEASE").withDottyCompat(scalaVersion.value),
    libraryDependencies += ("org.telegram"  % "telegrambots" % "4.7"),
    libraryDependencies += ("org.jetbrains" % "annotations"  % "19.0.0")
  )
