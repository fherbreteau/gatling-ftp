package io.github.fherbreteau.gatling.ftp.examples

import io.gatling.app.Gatling
import io.gatling.core.Predef._
import io.gatling.core.config.GatlingPropertiesBuilder
import io.gatling.core.structure.ScenarioBuilder
import io.github.fherbreteau.gatling.ftp.Predef.ftp
import io.github.fherbreteau.gatling.ftp.protocol.FtpProtocolBuilder

import java.nio.file.Paths

class FtpSimulation extends Simulation {

  val ftpProtocol: FtpProtocolBuilder = ftp
    .server("localhost")
    .port(2222)
    .credentials("user", "password")
    .localSourcePath(Paths.get("./src/test/resources/data"))
    .remoteSourcePath(Paths.get("/tmp"))

  val scn: ScenarioBuilder = scenario("SFTP Scenario")
    .exec(
      ftp("Upload a file")
        .upload("file_to_upload"))
    .exec(
      ftp("Move remote file")
        .copy("file_to_upload"))
    .exec(
      ftp("Delete remote file")
        .delete("file_to_upload")
    )

  setUp(scn.inject(atOnceUsers(1)).protocols(ftpProtocol))
}

object SftpSimulation {
  def main(args: Array[String]): Unit =
    Gatling.fromMap((new GatlingPropertiesBuilder)
      .simulationClass(classOf[FtpSimulation2].getName).build)
}
