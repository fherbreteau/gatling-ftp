package io.github.fherbreteau.gatling.ftp.client

import com.typesafe.scalalogging.StrictLogging
import io.gatling.commons.validation.Validation
import io.gatling.core.session.{Expression, Session}
import io.github.fherbreteau.gatling.ftp.client.FtpActions.{Action, Copy, Delete, Download, Mkdir, Move, RmDir, Upload}
import io.github.fherbreteau.gatling.ftp.protocol.FtpProtocol
import org.apache.commons.net.ftp.{FTPClient, FTPReply}
import org.apache.commons.net.io.Util

import java.io.IOException
import java.nio.file.Files
import scala.util.Using

final case class FtpOperationDef(operationName: Expression[String],
                                 operationDef: Expression[OperationDef],
                                 ftpProtocol: FtpProtocol,
                                 throttled: Boolean) {
  def build(session: Session): Validation[FtpOperation] =
    operationDef(session).map(definition => FtpOperation(definition, ftpProtocol, throttled))
}

object FtpOperation {
  def apply(definition: OperationDef, ftpProtocol: FtpProtocol, throttled: Boolean): FtpOperation =
    FtpOperation(definition.operationName, definition, ftpProtocol, throttled)
}

final case class FtpOperation(operationName: String,
                              definition: OperationDef,
                              ftpProtocol: FtpProtocol,
                              throttled: Boolean) extends StrictLogging {

  def build: FTPClient => Unit = {
    val localSourcePath = ftpProtocol.localSource(definition.source)
    val localDestPath = ftpProtocol.localDestination(definition.destination)
    val remoteSourcePath = ftpProtocol.remoteSource(definition.source)
    val remoteDestPath = ftpProtocol.remoteDestination(definition.destination)
    definition.action match {
      case Move => client => {
        logger.debug(s"Moving file $remoteSourcePath to $remoteDestPath")
        if (!client.rename(remoteSourcePath, remoteDestPath)) {
          throw new IOException("Failed to move file")
        }
      }
      case Copy => client => {
        Using.Manager { use =>
          logger.debug(s"Copying file $remoteSourcePath to $remoteDestPath")
          val source = use(client.retrieveFileStream(remoteSourcePath))
          if (!FTPReply.isPositiveIntermediate(client.getReplyCode)) {
            throw new IOException("Failed to retrieve source file stream")
          }
          val destination = use(client.storeFileStream(remoteDestPath))
          if (!FTPReply.isPositiveIntermediate(client.getReplyCode)) {
            throw new IOException("Failed to destination source file stream")
          }
          Util.copyStream(source, destination)
          if (!client.completePendingCommand())
            throw new IOException("Failed to copy file")
        }
      }
      case Delete => client => {
        logger.debug(s"Deleting file $remoteSourcePath")
        if (!client.deleteFile(remoteSourcePath))
          throw new IOException("Failed to delete file")
      }
      case Download => client => {
        Using.Manager { use =>
          logger.debug(s"Downloading file $remoteSourcePath to $localDestPath")
          val localFile = use(Files.newOutputStream(localDestPath))
          if (!client.retrieveFile(remoteSourcePath, localFile))
            throw new IOException("Failed to download file")
        }
      }
      case Upload => client => {
        Using.Manager { use =>
          logger.debug(s"Upload file $localSourcePath to $remoteDestPath")
          val localFile = use(Files.newInputStream(localSourcePath))
          if (!client.storeFile(remoteDestPath, localFile))
            throw new IOException("Failed to upload file")
        }
      }
      case Mkdir => client => {
        logger.debug(s"Create remote directory $remoteDestPath")
        if (!client.makeDirectory(remoteDestPath))
          throw new IOException("Failed to create directory")
      }
      case RmDir => client => {
        logger.debug(s"Removing remote directory $remoteDestPath")
        if (!client.removeDirectory(remoteDestPath))
          throw new IOException("Failed to remove directory")

      }
    }
  }
}

final case class OperationDef(operationName: String,
                              source: String,
                              destination: String,
                              action: Action) {}
