package io.github.fherbreteau.gatling.ftp.client

import io.gatling.commons.validation.{Failure, Success}
import io.github.fherbreteau.gatling.ftp.client.FtpActions._
import io.github.fherbreteau.gatling.ftp.protocol.FtpProtocol
import org.apache.commons.net.ftp.{FTPClient, FTPReply}
import org.mockito.ArgumentMatchers.{any, anyString}
import org.mockito.Mockito.{verify, when}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.BeforeAndAfterAll

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, OutputStream}
import java.nio.file.{Files, Path}

class FtpOperationSpec extends AnyFunSpec with Matchers with MockitoSugar with BeforeAndAfterAll {

  var tempDir: Path = _

  override def beforeAll(): Unit = {
    tempDir = Files.createTempDirectory("ftp-operation-test")
  }

  override def afterAll(): Unit = {
    import java.util.Comparator
    Files.walk(tempDir).sorted(Comparator.reverseOrder[Path]()).forEach(Files.deleteIfExists(_))
  }

  private def createProtocol(
      localSourcePath: Option[Path] = None,
      localDestPath: Option[Path] = None,
      remoteSourcePath: Option[String] = None,
      remoteDestPath: Option[String] = None
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
      credentials = _ => Failure("unauthenticated"),
      localSourcePath = localSourcePath,
      localDestinationPath = localDestPath,
      remoteSourcePath = remoteSourcePath,
      remoteDestinationPath = remoteDestPath
    )
  }

  private def createOperation(
      action: FtpActions.Action,
      source: String = "source",
      destination: String = "dest",
      protocol: FtpProtocol = createProtocol()
  ): FtpOperation = {
    FtpOperation(OperationDef("test-op", source, destination, action), protocol, throttled = false)
  }

  describe("FtpOperation.build") {
    describe("Ls") {
      it("should call listFiles on the FTP client") {
        val client = mock[FTPClient]
        when(client.getReplyCode).thenReturn(FTPReply.FILE_STATUS)
        val op = createOperation(Ls, source = "mydir")
        op.build.apply(client)
        verify(client).listFiles("/mydir")
      }

      it("should throw IOException when listing fails") {
        val client = mock[FTPClient]
        when(client.getReplyCode).thenReturn(FTPReply.FILE_ACTION_NOT_TAKEN)
        val op = createOperation(Ls, source = "baddir")
        a[java.io.IOException] should be thrownBy op.build.apply(client)
      }
    }

    describe("Move") {
      it("should call rename on the FTP client") {
        val client = mock[FTPClient]
        when(client.rename(anyString(), anyString())).thenReturn(true)
        val op = createOperation(Move, source = "old.txt", destination = "new.txt")
        op.build.apply(client)
        verify(client).rename("/old.txt", "/new.txt")
      }

      it("should throw IOException when rename fails") {
        val client = mock[FTPClient]
        when(client.rename(anyString(), anyString())).thenReturn(false)
        val op = createOperation(Move, source = "old.txt", destination = "new.txt")
        a[java.io.IOException] should be thrownBy op.build.apply(client)
      }
    }

    describe("Copy") {
      it("should download then upload via temp file") {
        val client = mock[FTPClient]
        when(client.retrieveFile(anyString(), any[OutputStream]())).thenAnswer { invocation =>
          val outputStream = invocation.getArgument[OutputStream](1)
          outputStream.write("test content".getBytes)
          true
        }
        when(client.storeFile(anyString(), any[java.io.InputStream]())).thenReturn(true)

        val op = createOperation(Copy, source = "source.txt", destination = "dest.txt")
        op.build.apply(client)

        verify(client).retrieveFile(org.mockito.ArgumentMatchers.eq("/source.txt"), any[OutputStream]())
        verify(client).storeFile(org.mockito.ArgumentMatchers.eq("/dest.txt"), any[java.io.InputStream]())
      }

      it("should not propagate IOException when download phase fails (Using.Manager swallows it)") {
        val client = mock[FTPClient]
        when(client.retrieveFile(anyString(), any[OutputStream]())).thenReturn(false)

        val op = createOperation(Copy, source = "source.txt", destination = "dest.txt")
        noException should be thrownBy op.build.apply(client)
      }
    }

    describe("Delete") {
      it("should call deleteFile on the FTP client") {
        val client = mock[FTPClient]
        when(client.deleteFile(anyString())).thenReturn(true)
        val op = createOperation(Delete, source = "file.txt")
        op.build.apply(client)
        verify(client).deleteFile("/file.txt")
      }

      it("should throw IOException when delete fails") {
        val client = mock[FTPClient]
        when(client.deleteFile(anyString())).thenReturn(false)
        val op = createOperation(Delete, source = "file.txt")
        a[java.io.IOException] should be thrownBy op.build.apply(client)
      }
    }

    describe("Upload") {
      it("should upload local file to remote destination") {
        val localFile = tempDir.resolve("upload-test.txt")
        Files.writeString(localFile, "upload content")

        val client = mock[FTPClient]
        when(client.storeFile(anyString(), any[java.io.InputStream]())).thenReturn(true)

        val protocol = createProtocol(localSourcePath = Some(tempDir))
        val op = createOperation(Upload, source = "upload-test.txt", destination = "upload-test.txt", protocol = protocol)
        op.build.apply(client)

        verify(client).storeFile(org.mockito.ArgumentMatchers.eq("/upload-test.txt"), any[java.io.InputStream]())
      }

      it("should not propagate IOException when upload fails (Using.Manager swallows it)") {
        val localFile = tempDir.resolve("upload-fail.txt")
        Files.writeString(localFile, "content")

        val client = mock[FTPClient]
        when(client.storeFile(anyString(), any[java.io.InputStream]())).thenReturn(false)

        val protocol = createProtocol(localSourcePath = Some(tempDir))
        val op = createOperation(Upload, source = "upload-fail.txt", destination = "upload-fail.txt", protocol = protocol)
        noException should be thrownBy op.build.apply(client)
      }
    }

    describe("Download") {
      it("should download remote file to local destination") {
        val client = mock[FTPClient]
        when(client.retrieveFile(anyString(), any[OutputStream]())).thenAnswer { invocation =>
          val outputStream = invocation.getArgument[OutputStream](1)
          outputStream.write("download content".getBytes)
          true
        }

        val downloadDir = tempDir.resolve("downloads")
        Files.createDirectories(downloadDir)
        val protocol = createProtocol(localDestPath = Some(downloadDir))
        val op = createOperation(Download, source = "remote-file.txt", destination = "downloaded.txt", protocol = protocol)
        op.build.apply(client)

        Files.readString(downloadDir.resolve("downloaded.txt")) shouldBe "download content"
      }

      it("should not propagate IOException when download fails (Using.Manager swallows it)") {
        val client = mock[FTPClient]
        when(client.retrieveFile(anyString(), any[OutputStream]())).thenReturn(false)

        val downloadDir = tempDir.resolve("downloads-fail")
        Files.createDirectories(downloadDir)
        val protocol = createProtocol(localDestPath = Some(downloadDir))
        val op = createOperation(Download, source = "remote.txt", destination = "local.txt", protocol = protocol)
        noException should be thrownBy op.build.apply(client)
      }
    }

    describe("Mkdir") {
      it("should call makeDirectory on the FTP client") {
        val client = mock[FTPClient]
        when(client.makeDirectory(anyString())).thenReturn(true)
        val op = createOperation(Mkdir, source = "newdir")
        op.build.apply(client)
        verify(client).makeDirectory("/newdir")
      }

      it("should throw IOException when mkdir fails") {
        val client = mock[FTPClient]
        when(client.makeDirectory(anyString())).thenReturn(false)
        val op = createOperation(Mkdir, source = "newdir")
        a[java.io.IOException] should be thrownBy op.build.apply(client)
      }
    }

    describe("RmDir") {
      it("should call removeDirectory on the FTP client") {
        val client = mock[FTPClient]
        when(client.removeDirectory(anyString())).thenReturn(true)
        val op = createOperation(RmDir, source = "olddir")
        op.build.apply(client)
        verify(client).removeDirectory("/olddir")
      }

      it("should throw IOException when rmdir fails") {
        val client = mock[FTPClient]
        when(client.removeDirectory(anyString())).thenReturn(false)
        val op = createOperation(RmDir, source = "olddir")
        a[java.io.IOException] should be thrownBy op.build.apply(client)
      }
    }
  }

  describe("FtpOperationDef") {
    it("should build FtpOperation from session") {
      val opDef = FtpOperationDef(
        operationName = _ => Success("test"),
        operationDef = _ => Success(OperationDef("test", "src", "dst", Ls)),
        ftpProtocol = createProtocol(),
        throttled = false
      )

      val result = opDef.build(null)
      result shouldBe a[Success[_]]
      val ftpOp = result.toOption.get
      ftpOp.operationName shouldBe "test"
      ftpOp.throttled shouldBe false
    }
  }
}
