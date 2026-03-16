package io.github.fherbreteau.gatling.ftp.protocol

import io.gatling.commons.model.Credentials
import io.gatling.commons.validation.{Failure, Success}
import io.gatling.core.session.Expression
import io.github.fherbreteau.gatling.ftp.client.{Exchange, FtpClientFactory}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import java.nio.file.{Path, Paths}

class FtpProtocolSpec extends AnyFunSpec with Matchers {

  private def createProtocol(
      localSourcePath: Option[Path] = None,
      localDestinationPath: Option[Path] = None,
      remoteSourcePath: Option[String] = None,
      remoteDestinationPath: Option[String] = None
  ): FtpProtocol = {
    FtpProtocol(
      exchange = Exchange(
        factory = FtpClientFactory(),
        server = "localhost",
        port = 21,
        passiveMode = false,
        protocolLogging = false,
        executor = null
      ),
      credentials = _ => Success(Credentials("user", "pass")),
      localSourcePath = localSourcePath,
      localDestinationPath = localDestinationPath,
      remoteSourcePath = remoteSourcePath,
      remoteDestinationPath = remoteDestinationPath
    )
  }

  describe("FtpProtocol") {
    describe("localSource") {
      it("should resolve file against base source path") {
        val protocol = createProtocol(localSourcePath = Some(Paths.get("/base/source")))
        protocol.localSource("file.txt") shouldBe Paths.get("/base/source/file.txt")
      }

      it("should resolve file against current dir when no base path") {
        val protocol = createProtocol()
        protocol.localSource("file.txt") shouldBe Paths.get("./file.txt")
      }
    }

    describe("localDestination") {
      it("should resolve file against base destination path") {
        val protocol = createProtocol(localDestinationPath = Some(Paths.get("/base/dest")))
        protocol.localDestination("file.txt") shouldBe Paths.get("/base/dest/file.txt")
      }

      it("should resolve file against current dir when no base path") {
        val protocol = createProtocol()
        protocol.localDestination("file.txt") shouldBe Paths.get("./file.txt")
      }
    }

    describe("remoteSource") {
      it("should resolve file against remote source path") {
        val protocol = createProtocol(remoteSourcePath = Some("/remote/src"))
        protocol.remoteSource("file.txt") shouldBe "/remote/src/file.txt"
      }

      it("should resolve file with leading slash when no base path") {
        val protocol = createProtocol()
        protocol.remoteSource("file.txt") shouldBe "/file.txt"
      }
    }

    describe("remoteDestination") {
      it("should resolve file against remote destination path") {
        val protocol = createProtocol(remoteDestinationPath = Some("/remote/dst"))
        protocol.remoteDestination("file.txt") shouldBe "/remote/dst/file.txt"
      }

      it("should resolve file with leading slash when no base path") {
        val protocol = createProtocol()
        protocol.remoteDestination("file.txt") shouldBe "/file.txt"
      }
    }

    describe("credentials") {
      it("should evaluate credentials expression") {
        val protocol = createProtocol()
        val creds = protocol.credential(null)
        creds.username shouldBe "user"
        creds.password shouldBe "pass"
      }

      it("should throw when credentials expression fails") {
        val protocol = FtpProtocol(
          exchange = Exchange(
            factory = FtpClientFactory(),
            server = "localhost",
            port = 21,
            passiveMode = false,
            protocolLogging = false,
            executor = null
          ),
          credentials = _ => Failure("unauthenticated"),
          localSourcePath = None,
          localDestinationPath = None,
          remoteSourcePath = None,
          remoteDestinationPath = None
        )
        a[NoSuchElementException] should be thrownBy protocol.credential(null)
      }
    }
  }
}
