package io.github.fherbreteau.gatling.ftp.examples

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.github.fherbreteau.gatling.ftp.Predef.ftp
import io.github.fherbreteau.gatling.ftp.protocol.FtpProtocolBuilder

import java.nio.file.Paths

class FtpSimulation extends Simulation {

  val ftpProtocol: FtpProtocolBuilder = ftp
    .server("localhost")
    .port(2222)
    .credentials("user", "password")
    .localPath(Paths.get("./src/test/resources/data"))
    .remotePath("/tmp")

  val scn: ScenarioBuilder = scenario("FTP Scenario")
    .exec(
      ftp("Upload a file")
        .upload("file_to_upload"))
    .exec(
      ftp("Move remote file")
        .move("file_to_upload", "file_moved"))
    .exec(
      ftp("Delete remote file")
        .delete("file_moved")
    )

  setUp(scn.inject(atOnceUsers(1)).protocols(ftpProtocol))
}
