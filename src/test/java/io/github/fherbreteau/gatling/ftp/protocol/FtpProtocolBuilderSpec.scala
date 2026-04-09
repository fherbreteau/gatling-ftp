package io.github.fherbreteau.gatling.ftp.protocol

import io.gatling.commons.model.Credentials
import io.gatling.commons.validation.{Failure, Success}
import io.gatling.core.session.Expression
import io.github.fherbreteau.gatling.ftp.client.{Exchange, FtpClientFactory}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import java.nio.file.Paths

class FtpProtocolBuilderSpec extends AnyFunSpec with Matchers {

  private def baseProtocol: FtpProtocol = FtpProtocol(
    exchange = Exchange(
      factory = FtpClientFactory(),
      server = "localhost",
      port = 21,
      passiveMode = false,
      protocolLogging = false,
      executor = null
    ),
    credentials = _ => Failure("not configured"),
    localSourcePath = None,
    localDestinationPath = None,
    remoteSourcePath = None,
    remoteDestinationPath = None
  )

  private def baseBuilder: FtpProtocolBuilder = FtpProtocolBuilder(baseProtocol)

  describe("FtpProtocolBuilder") {
    it("should set server") {
      val builder = baseBuilder.server("ftp.example.com")
      builder.protocol.exchange.server shouldBe "ftp.example.com"
    }

    it("should set port") {
      val builder = baseBuilder.port(2121)
      builder.protocol.exchange.port shouldBe 2121
    }

    it("should configure credentials") {
      val builder = baseBuilder.credentials(_ => Success("user"), _ => Success("pass"))
      val credExpr: Expression[Credentials] = builder.protocol.credentials
      val creds = credExpr(null).toOption.get
      creds.username shouldBe "user"
      creds.password shouldBe "pass"
    }

    it("should set passive mode") {
      val builder = baseBuilder.passiveMode(true)
      builder.protocol.exchange.passiveMode shouldBe true
    }

    it("should set protocol logging") {
      val builder = baseBuilder.protocolLogging(true)
      builder.protocol.exchange.protocolLogging shouldBe true
    }

    it("should set local source path") {
      val path = Paths.get("/local/source")
      val builder = baseBuilder.localSourcePath(path)
      builder.protocol.localSourcePath shouldBe Some(path)
    }

    it("should set local destination path") {
      val path = Paths.get("/local/dest")
      val builder = baseBuilder.localDestinationPath(path)
      builder.protocol.localDestinationPath shouldBe Some(path)
    }

    it("should set both local paths with localPath") {
      val path = Paths.get("/local/both")
      val builder = baseBuilder.localPath(path)
      builder.protocol.localSourcePath shouldBe Some(path)
      builder.protocol.localDestinationPath shouldBe Some(path)
    }

    it("should set remote source path") {
      val builder = baseBuilder.remoteSourcePath("/remote/source")
      builder.protocol.remoteSourcePath shouldBe Some("/remote/source")
    }

    it("should set remote destination path") {
      val builder = baseBuilder.remoteDestinationPath("/remote/dest")
      builder.protocol.remoteDestinationPath shouldBe Some("/remote/dest")
    }

    it("should set both remote paths with remotePath") {
      val builder = baseBuilder.remotePath("/remote/both")
      builder.protocol.remoteSourcePath shouldBe Some("/remote/both")
      builder.protocol.remoteDestinationPath shouldBe Some("/remote/both")
    }

    it("should chain multiple settings") {
      val builder = baseBuilder
        .server("example.com")
        .port(3333)
        .remoteSourcePath("/upload")
        .localSourcePath(Paths.get("/local"))
      builder.protocol.exchange.server shouldBe "example.com"
      builder.protocol.exchange.port shouldBe 3333
      builder.protocol.remoteSourcePath shouldBe Some("/upload")
      builder.protocol.localSourcePath shouldBe Some(Paths.get("/local"))
    }

    it("should produce protocol via build") {
      val protocol = baseBuilder.server("host").port(21).build
      protocol shouldBe a[FtpProtocol]
      protocol.exchange.server shouldBe "host"
    }

    it("should implicitly convert to FtpProtocol") {
      val builder = baseBuilder.server("host")
      val protocol: FtpProtocol = FtpProtocolBuilder.toFtpProtocol(builder)
      protocol.exchange.server shouldBe "host"
    }
  }
}
