package io.github.fherbreteau.gatling.ftp.examples;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.github.fherbreteau.gatling.ftp.javaapi.protocol.FtpProtocolBuilder;

import java.nio.file.Paths;

import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.github.fherbreteau.gatling.ftp.javaapi.FtpDsl.ftp;

public class FtpSimulation2 extends Simulation {

    FtpProtocolBuilder ftpProtocol = ftp
            .server("localhost")
            .port(2222)
            .credentials("user", "password")
            .localSourcePath(Paths.get("./src/test/resources/data"))
            .remoteSourcePath(Paths.get("/tmp"));

    ScenarioBuilder scn = scenario("SFTP Scenario")
            .exec(ftp("Upload a file")
                    .upload("file_to_upload"))
            .exec(ftp("Move remote file")
                    .copy("file_to_upload"))
            .exec(ftp("Delete remote file")
                    .delete("file_to_upload"));

    {
        setUp(scn.injectOpen(atOnceUsers(1)).protocols(ftpProtocol));
    }
}
