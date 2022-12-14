package com.github.fherbreteau.gatling.ftp.protocol

import com.github.fherbreteau.gatling.ftp.client.Exchange
import com.typesafe.scalalogging.StrictLogging
import io.gatling.commons.model.Credentials
import io.gatling.core.CoreComponents
import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.protocol.{Protocol, ProtocolKey}

import java.nio.file.{Path, Paths}

object FtpProtocol extends StrictLogging {

  val FtpProtocolKey: ProtocolKey[FtpProtocol, FtpComponents] = new ProtocolKey[FtpProtocol, FtpComponents] {

    override def protocolClass: Class[Protocol] = classOf[FtpProtocol].asInstanceOf[Class[Protocol]]

    override def defaultProtocolValue(configuration: GatlingConfiguration): FtpProtocol =
      throw new IllegalArgumentException("Can't provide a default value for ImportProtocol")

    override def newComponents(coreComponents: CoreComponents): FtpProtocol => FtpComponents = {

      ftpProtocol => FtpComponents(ftpProtocol)
    }
  }

  def apply(configuration: GatlingConfiguration): FtpProtocol =
    new FtpProtocol(
      exchange = Exchange(
        server = "localhost",
        port = 22,
        credentials = Credentials("", "")
      ),
      localSourcePath = None,
      localDestinationPath = None,
      remoteSourcePath = None,
      remoteDestinationPath = None
    )
}

final case class FtpProtocol(exchange: Exchange,
                             localSourcePath: Option[Path],
                             localDestinationPath: Option[Path],
                             remoteSourcePath: Option[Path],
                             remoteDestinationPath: Option[Path]) extends Protocol {
  type Components = FtpComponents

  def source(file: String, isLocal: Boolean): Path = {
    basePath(localSourcePath, remoteSourcePath, isLocal).resolve(file)
  }

  def destination(file: String, isLocal: Boolean): Path = {
    basePath(localDestinationPath, remoteDestinationPath, isLocal).resolve(file)
  }

  private def basePath(localPath: Option[Path], remotePath: Option[Path], isLocal: Boolean): Path =
    if (isLocal) {
      localPath.getOrElse(Paths.get("."))
    } else {
      remotePath.getOrElse(Paths.get(s"/home/${exchange.credentials.username}"))
    }}

