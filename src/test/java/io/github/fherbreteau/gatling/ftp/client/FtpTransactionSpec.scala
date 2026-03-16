package io.github.fherbreteau.gatling.ftp.client

import io.gatling.commons.validation.Failure
import io.github.fherbreteau.gatling.ftp.client.FtpActions._
import io.github.fherbreteau.gatling.ftp.protocol.FtpProtocol
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class FtpTransactionSpec extends AnyFunSpec with Matchers {

  private val protocol = FtpProtocol(
    exchange = Exchange(
      factory = FtpClientFactory(),
      server = "ftp.example.com",
      port = 2121,
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

  private val operation = FtpOperation(
    OperationDef("list-files", "dir", "", Ls),
    protocol,
    throttled = true
  )

  describe("FtpTransaction") {
    it("should expose fullRequestName from operation") {
      val tx = FtpTransaction(null, operation, null)
      tx.fullRequestName shouldBe "list-files"
    }

    it("should expose server from protocol exchange") {
      val tx = FtpTransaction(null, operation, null)
      tx.server shouldBe "ftp.example.com"
    }

    it("should expose throttled flag from operation") {
      val tx = FtpTransaction(null, operation, null)
      tx.throttled shouldBe true
    }

    it("should expose action definition") {
      val tx = FtpTransaction(null, operation, null)
      tx.action.operationName shouldBe "list-files"
      tx.action.action shouldBe Ls
    }
  }
}
