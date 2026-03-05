package io.github.fherbreteau.gatling.ftp.examples

import io.gatling.core.Predef._
import io.gatling.core.feeder.FeederBuilder
import io.gatling.core.structure.ScenarioBuilder
import io.github.fherbreteau.gatling.ftp.Predef.ftp
import io.github.fherbreteau.gatling.ftp.protocol.FtpProtocolBuilder

import java.nio.file.Paths

class FtpSimulationScala extends Simulation {

  // Set up Ftp protocol with user credential
  val ftpProtocol: FtpProtocolBuilder = ftp
    .server("localhost")
    .port(2121)
    .credentials("#{username}", "#{password}")
    .passiveMode(true)
    .localPath(Paths.get("./src/test/resources/data"))
    .remotePath(".")

  // Load credentials from CSV
  val credentialsFeeder: FeederBuilder = csv("credential.csv").circular

  val remotePath = "."
  val source = "file_to_upload.txt"
  val destination = "file_copied.txt"
  val folder = "folder"

  // Define the test scenario
  val scn: ScenarioBuilder = scenario("FTP Scenario")
    .feed(credentialsFeeder)
    .exec(
      exec(ftp("List remote directory").ls(remotePath)),
      exec(ftp("Upload a file").upload(source)),
      exec(ftp("Copy remote file").copy(source, destination)),
      exec(ftp("Delete remote file").delete(source)),
      exec(ftp("Move remote file").move(destination, source)),
      exec(ftp("Download remote file").download(source)),
      exec(ftp("Delete remote file").delete(source)),
      exec(ftp("Create a remote dir").mkdir(folder)),
      exec(ftp("Delete a remote dir").rmdir(folder))
    )

  // Set up the simulation with open workload model
  setUp(scn.inject(atOnceUsers(4)).protocols(ftpProtocol))
}
