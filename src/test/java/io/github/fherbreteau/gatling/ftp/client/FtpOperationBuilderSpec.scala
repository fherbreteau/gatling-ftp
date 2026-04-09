package io.github.fherbreteau.gatling.ftp.client

import io.gatling.commons.validation.{Failure, Success}
import io.gatling.core.session.Expression
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class FtpOperationBuilderSpec extends AnyFunSpec with Matchers {

  private def constExpr(value: String): Expression[String] = _ => Success(value)
  private def failExpr(msg: String): Expression[String] = _ => Failure(msg)

  describe("FtpOperationBuilder") {
    it("should build OperationDef from constant expressions") {
      val builder = FtpOperationBuilder(
        operationName = constExpr("test-op"),
        source = constExpr("source.txt"),
        destination = constExpr("dest.txt"),
        action = FtpActions.Upload
      )

      val result = builder.build.apply(null)
      result shouldBe a[Success[_]]
      val opDef = result.toOption.get
      opDef.operationName shouldBe "test-op"
      opDef.source shouldBe "source.txt"
      opDef.destination shouldBe "dest.txt"
      opDef.action shouldBe FtpActions.Upload
    }

    it("should propagate operation name expression failure") {
      val builder = FtpOperationBuilder(
        operationName = failExpr("name error"),
        source = constExpr("source.txt"),
        destination = constExpr("dest.txt"),
        action = FtpActions.Ls
      )

      val result = builder.build.apply(null)
      result shouldBe a[Failure]
    }

    it("should propagate source expression failure") {
      val builder = FtpOperationBuilder(
        operationName = constExpr("op"),
        source = failExpr("source error"),
        destination = constExpr("dest.txt"),
        action = FtpActions.Ls
      )

      val result = builder.build.apply(null)
      result shouldBe a[Failure]
    }

    it("should propagate destination expression failure") {
      val builder = FtpOperationBuilder(
        operationName = constExpr("op"),
        source = constExpr("source.txt"),
        destination = failExpr("dest error"),
        action = FtpActions.Ls
      )

      val result = builder.build.apply(null)
      result shouldBe a[Failure]
    }

    it("should build for each action type") {
      FtpActions.values.foreach { action =>
        val builder = FtpOperationBuilder(
          operationName = constExpr("op"),
          source = constExpr("src"),
          destination = constExpr("dst"),
          action = action
        )
        val result = builder.build.apply(null)
        result shouldBe a[Success[_]]
        result.toOption.get.action shouldBe action
      }
    }
  }

  describe("OperationBuilder") {
    it("should build OperationDef with correct values") {
      val builder = OperationBuilder("op-name", "src", "dst", FtpActions.Move)
      val opDef = builder.build
      opDef.operationName shouldBe "op-name"
      opDef.source shouldBe "src"
      opDef.destination shouldBe "dst"
      opDef.action shouldBe FtpActions.Move
    }
  }
}
