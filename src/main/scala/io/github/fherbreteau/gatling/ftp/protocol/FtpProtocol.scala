package io.github.fherbreteau.gatling.ftp.protocol

import com.typesafe.scalalogging.StrictLogging
import io.gatling.commons.model.Credentials
import io.gatling.commons.validation.Failure
import io.gatling.core.CoreComponents
import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.protocol.{Protocol, ProtocolKey}
import io.gatling.core.session.{Expression, Session}
import io.github.fherbreteau.gatling.ftp.client.Exchange

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
        port = 22
      ),
      credentials = (_: Session) => Failure("unauthenticated"),
      localSourcePath = None,
      localDestinationPath = None,
      remoteSourcePath = None,
      remoteDestinationPath = None
    )
}

final case class FtpProtocol(exchange: Exchange,
                             credentials: Expression[Credentials],
                             localSourcePath: Option[Path],
                             localDestinationPath: Option[Path],
                             remoteSourcePath: Option[String],
                             remoteDestinationPath: Option[String]) extends Protocol {
  type Components = FtpComponents

  def localSource(file: String): Path = {
    localSourcePath.getOrElse(Paths.get(".")).resolve(file)
  }

  def localDestination(file: String): Path = {
    localDestinationPath.getOrElse(Paths.get(".")).resolve(file)
  }

  def remoteSource(file: String): String = {
    remoteSourcePath.getOrElse("").concat("/").concat(file)
  }

  def remoteDestination(file: String): String = {
    remoteDestinationPath.getOrElse("").concat("/").concat(file)
  }

  def credential(session: Session): Credentials =
    credentials(session).toOption.get
}

