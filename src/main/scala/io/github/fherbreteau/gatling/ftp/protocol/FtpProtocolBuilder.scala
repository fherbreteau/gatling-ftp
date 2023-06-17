package io.github.fherbreteau.gatling.ftp.protocol

import com.softwaremill.quicklens.ModifyPimp
import io.gatling.commons.model.Credentials
import io.gatling.core.config.GatlingConfiguration

import java.nio.file.Path
import scala.language.implicitConversions

object FtpProtocolBuilder {

  implicit def toFtpProtocol(builder: FtpProtocolBuilder): FtpProtocol = builder.build

  def apply(configuration: GatlingConfiguration): FtpProtocolBuilder = FtpProtocolBuilder(FtpProtocol(configuration))
}

final case class FtpProtocolBuilder(protocol: FtpProtocol) {
  def server(server: String): FtpProtocolBuilder = this.modify(_.protocol.exchange.server).setTo(server)

  def port(port: Int): FtpProtocolBuilder = this.modify(_.protocol.exchange.port).setTo(port)

  def credentials(username: String, password: String): FtpProtocolBuilder = this.modify(_.protocol.exchange.credentials).setTo(Credentials(username, password))

  def localPath(path: Path): FtpProtocolBuilder = this.localSourcePath(path).localDestinationPath(path)

  def localSourcePath(sourcePath: Path): FtpProtocolBuilder = this.modify(_.protocol.localSourcePath).setTo(Some(sourcePath))

  def localDestinationPath(destPath: Path): FtpProtocolBuilder = this.modify(_.protocol.localDestinationPath).setTo(Some(destPath))

  def remotePath(path: String): FtpProtocolBuilder = this.remoteSourcePath(path).remoteDestinationPath(path)

  def remoteSourcePath(sourcePath: String): FtpProtocolBuilder = this.modify(_.protocol.remoteSourcePath).setTo(Some(sourcePath))

  def remoteDestinationPath(destPath: String): FtpProtocolBuilder = this.modify(_.protocol.remoteDestinationPath).setTo(Some(destPath))

  def build: FtpProtocol = protocol
}
