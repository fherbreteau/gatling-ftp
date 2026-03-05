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

    String remotePath = ".";
    String source = "file_to_upload.txt";
    String destination = "file_copied.txt";
    String folder = "folder";

    // Define the test scenario
    ScenarioBuilder scn = scenario("FTP Scenario")
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
            );

    {
        // Set up the simulation with open workload model
        setUp(scn.injectOpen(atOnceUsers(4)).protocols(ftpProtocol));
    }
}
