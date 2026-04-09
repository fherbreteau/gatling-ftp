package io.github.fherbreteau.gatling.ftp.util

import io.gatling.commons.validation.{Failure, Success}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class FtpHelperSpec extends AnyFunSpec with Matchers {

  describe("FtpHelper") {
    describe("buildCredentials") {
      it("should create Credentials from expressions") {
        val expr = FtpHelper.buildCredentials(
          _ => Success("testuser"),
          _ => Success("testpass")
        )
        val result = expr.apply(null)
        result shouldBe a[Success[_]]
        val creds = result.toOption.get
        creds.username shouldBe "testuser"
        creds.password shouldBe "testpass"
      }

      it("should propagate username expression failure") {
        val expr = FtpHelper.buildCredentials(
          _ => Failure("user error"),
          _ => Success("pass")
        )
        val result = expr.apply(null)
        result shouldBe a[Failure]
      }

      it("should propagate password expression failure") {
        val expr = FtpHelper.buildCredentials(
          _ => Success("user"),
          _ => Failure("pass error")
        )
        val result = expr.apply(null)
        result shouldBe a[Failure]
      }
    }
  }
}
