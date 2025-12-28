package io.github.fherbreteau.gatling.ftp.examples;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.github.fherbreteau.gatling.ftp.javaapi.FtpDsl.ftp;

import java.nio.file.Paths;

import io.gatling.javaapi.core.FeederBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.github.fherbreteau.gatling.ftp.javaapi.protocol.FtpProtocolBuilder;

public class FtpSimulationJava extends Simulation {

    // Set up Ftp protocol with user credential
    FtpProtocolBuilder ftpProtocol = ftp
            .server("localhost")
            .port(2121)
            .credentials("#{username}", "#{password}")
            .passiveMode(true)
            .localPath(Paths.get("./src/test/resources/data"))
            .remotePath(".");

    // Load credentials from CSV
    FeederBuilder<String> credentialsFeeder = csv("credential.csv").circular();

    String source = "file_to_upload.txt";
    String destination = "file_copied.txt";

    // Define the test scenario
    ScenarioBuilder scn = scenario("FTP Scenario")
            .feed(credentialsFeeder)
            .exec(
                    exec(ftp("Upload a file").upload(source)),
                    exec(ftp("Copy remote file").copy(source, destination)),
                    exec(ftp("Delete remote file").delete(source)),
                    exec(ftp("Move remote file").move(destination, source)),
                    exec(ftp("Delete remote file").delete(source))
            );

    {
        // Set up the simulation with open workload model
        setUp(scn.injectOpen(atOnceUsers(4)).protocols(ftpProtocol));
    }
}
