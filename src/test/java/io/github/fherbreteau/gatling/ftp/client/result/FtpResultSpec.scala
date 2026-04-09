package io.github.fherbreteau.gatling.ftp.client.result

import io.gatling.commons.stats.{KO, OK}
import io.github.fherbreteau.gatling.ftp.client.OperationDef
import io.github.fherbreteau.gatling.ftp.client.FtpActions
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class FtpResultSpec extends AnyFunSpec with Matchers {

  val opDef: OperationDef = OperationDef("test-op", "src", "dst", FtpActions.Ls)

  describe("FtpResponse") {
    it("should represent a successful result") {
      val response = FtpResponse(opDef, 100L, 200L, OK)
      response.operation shouldBe opDef
      response.startTimestamp shouldBe 100L
      response.endTimestamp shouldBe 200L
      response.status shouldBe OK
      response.message shouldBe None
    }
  }

  describe("FtpFailure") {
    it("should represent a failed result with error message") {
      val failure = FtpFailure(opDef, 100L, 200L, "connection refused", KO)
      failure.operation shouldBe opDef
      failure.startTimestamp shouldBe 100L
      failure.endTimestamp shouldBe 200L
      failure.status shouldBe KO
      failure.message shouldBe Some("connection refused")
      failure.errorMessage shouldBe "connection refused"
    }
  }
}
