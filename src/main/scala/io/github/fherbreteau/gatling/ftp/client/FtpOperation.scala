package io.github.fherbreteau.gatling.ftp.client

import com.typesafe.scalalogging.StrictLogging
import io.gatling.commons.stats.{KO, OK}
import io.gatling.commons.util.Clock
import io.gatling.commons.validation.Validation
import io.gatling.core.session.{Expression, Session}
import io.github.fherbreteau.gatling.ftp.client.FtpActions.{Action, Copy, Delete, Download, Move, Upload}
import io.github.fherbreteau.gatling.ftp.client.result.{FtpFailure, FtpResponse, FtpResult}
import io.github.fherbreteau.gatling.ftp.protocol.FtpProtocol
import org.apache.commons.net.ftp.{FTPClient, FTPReply}
import org.apache.commons.net.io.Util

import java.nio.file.Files

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

  def build: (FTPClient, Long, Clock) => FtpResult = {
    val localSourcePath = ftpProtocol.source(definition.file, isLocal = true)
    val localDestPath = ftpProtocol.destination(definition.file, isLocal = true)
    val remoteSourcePath = ftpProtocol.source(definition.file, isLocal = false).toString
    val remoteDestPath = ftpProtocol.destination(definition.file, isLocal = false).toString
    definition.action match {
      case Move => (client, startTime, clock) => {
        logger.debug(s"Moving file $remoteSourcePath to $remoteDestPath")
        if (client.rename(remoteSourcePath, remoteDestPath)) {
          FtpResponse(definition, startTime, clock.nowMillis, OK)
        } else {
          FtpFailure(definition, startTime, clock.nowMillis, "Failed to move file", KO)
        }
      }
      case Copy => (client, startTime, clock) => {
        logger.debug(s"Copying file $remoteSourcePath to $remoteDestPath")
        val source = client.retrieveFileStream(remoteSourcePath)
        if (!FTPReply.isPositiveIntermediate(client.getReplyCode)) {
          FtpFailure(definition, startTime, clock.nowMillis, "Failed to retrieve source file stream", KO)
        } else {
          val destination = client.storeFileStream(remoteDestPath)
          if (!FTPReply.isPositiveIntermediate(client.getReplyCode)) {
            FtpFailure(definition, startTime, clock.nowMillis, "Failed to destination source file stream", KO)
          } else {
            Util.copyStream(source, destination)
            if (client.completePendingCommand())
              FtpResponse(definition, startTime, clock.nowMillis, OK)
            else
              FtpFailure(definition, startTime, clock.nowMillis, "Failed to copy file", KO)
          }
        }
      }
      case Delete => (client, startTime, clock) => {
        logger.debug(s"Deleting file $remoteSourcePath")
        if (client.deleteFile(remoteSourcePath))
          FtpResponse(definition, startTime, clock.nowMillis, OK)
        else
          FtpFailure(definition, startTime, clock.nowMillis, "Failed to delete file", KO)
      }
      case Download => (client, startTime, clock) => {
        logger.debug(s"Downloading file $remoteSourcePath to $localDestPath")
        val localFile = Files.newOutputStream(localDestPath)
        if (client.retrieveFile(remoteSourcePath, localFile))
          FtpResponse(definition, startTime, clock.nowMillis, OK)
        else
          FtpFailure(definition, startTime, clock.nowMillis, "Failed to download file", KO)
      }
      case Upload => (client, startTime, clock) => {
        logger.debug(s"Upload file $localSourcePath to $remoteDestPath")
        val localFile = Files.newInputStream(localSourcePath)
        if(client.storeFile(remoteDestPath, localFile))
          FtpResponse(definition, startTime, clock.nowMillis, OK)
        else
          FtpFailure(definition, startTime, clock.nowMillis, "Failed to upload file", KO)
      }
    }
  }
}

final case class OperationDef(operationName: String,
                              file: String,
                              action: Action) {}
