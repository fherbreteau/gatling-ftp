package io.github.fherbreteau.gatling.ftp.integration

import io.gatling.commons.validation.{Failure, Success}
import io.github.fherbreteau.gatling.ftp.client.FtpActions._
import io.github.fherbreteau.gatling.ftp.client._
import io.github.fherbreteau.gatling.ftp.protocol.FtpProtocol
import org.apache.commons.net.ftp.FTPClient
import org.mockftpserver.fake.filesystem.{DirectoryEntry, FileEntry, UnixFakeFileSystem}
import org.mockftpserver.fake.{FakeFtpServer, UserAccount}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}

import java.nio.file.{Files, Path}
import java.util.Comparator

class FtpIntegrationSpec extends AnyFunSpec with Matchers with BeforeAndAfterAll with BeforeAndAfterEach {

  private var ftpServer: FakeFtpServer = _
  private var tempDir: Path = _
  private var serverPort: Int = _

  override def beforeAll(): Unit = {
    tempDir = Files.createTempDirectory("ftp-integration-test")

    val fileSystem = new UnixFakeFileSystem()
    fileSystem.add(new DirectoryEntry("/"))
    fileSystem.add(new DirectoryEntry("/data"))

    ftpServer = new FakeFtpServer()
    ftpServer.setServerControlPort(0)
    ftpServer.addUserAccount(new UserAccount("testuser", "testpass", "/"))
    ftpServer.setFileSystem(fileSystem)
    ftpServer.start()
    serverPort = ftpServer.getServerControlPort
  }

  override def afterAll(): Unit = {
    if (ftpServer != null) ftpServer.stop()
    if (tempDir != null) {
      Files.walk(tempDir)
        .sorted(Comparator.reverseOrder[Path]())
        .forEach(p => Files.deleteIfExists(p))
    }
  }

  override def beforeEach(): Unit = {
    import scala.jdk.CollectionConverters._
    val fileSystem = ftpServer.getFileSystem
    fileSystem.listNames("/").asScala.foreach { name =>
      if (name != "data") {
        fileSystem.delete(s"/$name")
      }
    }
    // Reset /data directory contents
    fileSystem.listNames("/data").asScala.foreach { name =>
      fileSystem.delete(s"/data/$name")
    }
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
        port = serverPort,
        passiveMode = false,
        protocolLogging = false,
        executor = null
      ),
      credentials = _ => Success(io.gatling.commons.model.Credentials("testuser", "testpass")),
      localSourcePath = localSourcePath,
      localDestinationPath = localDestPath,
      remoteSourcePath = remoteSourcePath,
      remoteDestinationPath = remoteDestPath
    )
  }

  private def withFtpClient[T](f: FTPClient => T): T = {
    val client = new FTPClient()
    try {
      client.connect("localhost", serverPort)
      client.login("testuser", "testpass")
      f(client)
    } finally {
      if (client.isConnected) {
        client.logout()
        client.disconnect()
      }
    }
  }

  describe("FTP Operations against embedded server") {
    describe("Mkdir and RmDir") {
      it("should create a directory on the server") {
        withFtpClient { client =>
          val op = FtpOperation(
            OperationDef("mkdir-test", "testdir", "", Mkdir),
            createProtocol(), throttled = false
          )
          op.build.apply(client)
          ftpServer.getFileSystem.exists("/testdir") shouldBe true
          ftpServer.getFileSystem.isDirectory("/testdir") shouldBe true
        }
      }

      it("should remove a directory from the server") {
        ftpServer.getFileSystem.add(new DirectoryEntry("/dir-to-remove"))

        withFtpClient { client =>
          val op = FtpOperation(
            OperationDef("rmdir-test", "dir-to-remove", "", RmDir),
            createProtocol(), throttled = false
          )
          op.build.apply(client)
          ftpServer.getFileSystem.exists("/dir-to-remove") shouldBe false
        }
      }
    }

    describe("Ls") {
      it("should list directory contents without error") {
        ftpServer.getFileSystem.add(new FileEntry("/data/file1.txt", "content1"))
        ftpServer.getFileSystem.add(new FileEntry("/data/file2.txt", "content2"))

        withFtpClient { client =>
          val op = FtpOperation(
            OperationDef("ls-test", "data", "", Ls),
            createProtocol(remoteSourcePath = Some("")), throttled = false
          )
          noException should be thrownBy op.build.apply(client)
        }
      }
    }

    describe("Upload") {
      it("should upload a local file to the remote server") {
        val uploadContent = "Hello, FTP upload!"
        val localFile = tempDir.resolve("local-upload.txt")
        Files.writeString(localFile, uploadContent)

        withFtpClient { client =>
          val protocol = createProtocol(localSourcePath = Some(tempDir))
          val op = FtpOperation(
            OperationDef("upload-test", "local-upload.txt", "remote-uploaded.txt", Upload),
            protocol, throttled = false
          )
          op.build.apply(client)
          ftpServer.getFileSystem.exists("/remote-uploaded.txt") shouldBe true
        }
      }
    }

    describe("Download") {
      it("should download a remote file to local destination") {
        val content = "Hello, FTP download!"
        ftpServer.getFileSystem.add(new FileEntry("/remote-file.txt", content))

        val downloadDir = tempDir.resolve("download-dest")
        Files.createDirectories(downloadDir)

        withFtpClient { client =>
          val protocol = createProtocol(localDestPath = Some(downloadDir))
          val op = FtpOperation(
            OperationDef("download-test", "remote-file.txt", "local-downloaded.txt", Download),
            protocol, throttled = false
          )
          op.build.apply(client)
          Files.readString(downloadDir.resolve("local-downloaded.txt")) shouldBe content
        }
      }
    }

    describe("Copy") {
      it("should copy a file on the remote server") {
        val content = "Copy me!"
        ftpServer.getFileSystem.add(new FileEntry("/original.txt", content))

        withFtpClient { client =>
          val op = FtpOperation(
            OperationDef("copy-test", "original.txt", "copied.txt", Copy),
            createProtocol(), throttled = false
          )
          op.build.apply(client)
          ftpServer.getFileSystem.exists("/original.txt") shouldBe true
          ftpServer.getFileSystem.exists("/copied.txt") shouldBe true
        }
      }
    }

    describe("Move") {
      it("should rename a file on the remote server") {
        ftpServer.getFileSystem.add(new FileEntry("/before-move.txt", "move me"))

        withFtpClient { client =>
          val op = FtpOperation(
            OperationDef("move-test", "before-move.txt", "after-move.txt", Move),
            createProtocol(), throttled = false
          )
          op.build.apply(client)
          ftpServer.getFileSystem.exists("/before-move.txt") shouldBe false
          ftpServer.getFileSystem.exists("/after-move.txt") shouldBe true
        }
      }
    }

    describe("Delete") {
      it("should delete a file on the remote server") {
        ftpServer.getFileSystem.add(new FileEntry("/to-delete.txt", "delete me"))

        withFtpClient { client =>
          val op = FtpOperation(
            OperationDef("delete-test", "to-delete.txt", "", Delete),
            createProtocol(), throttled = false
          )
          op.build.apply(client)
          ftpServer.getFileSystem.exists("/to-delete.txt") shouldBe false
        }
      }
    }
  }
}
