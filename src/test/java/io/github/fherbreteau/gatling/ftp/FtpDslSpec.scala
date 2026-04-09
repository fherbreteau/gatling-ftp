package io.github.fherbreteau.gatling.ftp

import io.gatling.commons.validation.Success
import io.gatling.core.session.Expression
import io.github.fherbreteau.gatling.ftp.client.FtpActions
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class FtpDslSpec extends AnyFunSpec with Matchers {

  private val operationName: Expression[String] = _ => Success("test-op")
  private val ftpDsl = Ftp(operationName)
  private def constExpr(value: String): Expression[String] = _ => Success(value)

  describe("Ftp DSL") {
    it("ls should create action builder with Ls action") {
      val builder = ftpDsl.ls(constExpr("dir"))
      builder.action shouldBe FtpActions.Ls
    }

    it("mkdir should create action builder with Mkdir action") {
      val builder = ftpDsl.mkdir(constExpr("dir"))
      builder.action shouldBe FtpActions.Mkdir
    }

    it("move should create action builder with Move action") {
      val builder = ftpDsl.move(constExpr("src"), constExpr("dst"))
      builder.action shouldBe FtpActions.Move
    }

    it("copy should create action builder with Copy action") {
      val builder = ftpDsl.copy(constExpr("src"), constExpr("dst"))
      builder.action shouldBe FtpActions.Copy
    }

    it("upload with single arg should create action builder with Upload action") {
      val builder = ftpDsl.upload(constExpr("file"))
      builder.action shouldBe FtpActions.Upload
    }

    it("upload with two args should create action builder with Upload action") {
      val builder = ftpDsl.upload(constExpr("src"), constExpr("dst"))
      builder.action shouldBe FtpActions.Upload
    }

    it("download with single arg should create action builder with Download action") {
      val builder = ftpDsl.download(constExpr("file"))
      builder.action shouldBe FtpActions.Download
    }

    it("download with two args should create action builder with Download action") {
      val builder = ftpDsl.download(constExpr("src"), constExpr("dst"))
      builder.action shouldBe FtpActions.Download
    }

    it("delete should create action builder with Delete action") {
      val builder = ftpDsl.delete(constExpr("file"))
      builder.action shouldBe FtpActions.Delete
    }

    it("rmdir should create action builder with RmDir action") {
      val builder = ftpDsl.rmdir(constExpr("dir"))
      builder.action shouldBe FtpActions.RmDir
    }
  }

  describe("FtpActionBuilder properties") {
    it("should carry the operation name expression") {
      val builder = ftpDsl.ls(constExpr("dir"))
      builder.operationName.apply(null) shouldBe Success("test-op")
    }

    it("should carry the source expression") {
      val builder = ftpDsl.move(constExpr("src-file"), constExpr("dst-file"))
      builder.source.apply(null) shouldBe Success("src-file")
    }

    it("should carry the destination expression") {
      val builder = ftpDsl.move(constExpr("src-file"), constExpr("dst-file"))
      builder.destination.apply(null) shouldBe Success("dst-file")
    }
  }
}
