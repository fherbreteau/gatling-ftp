package io.github.fherbreteau.gatling.ftp.client

import io.gatling.core.session.Session
import io.github.fherbreteau.gatling.ftp.protocol.FtpProtocol

object FtpClients {
  private val exchange: String = "sftp.exchange"

  def setFtpClient(ftpProtocol: FtpProtocol): Session => Session =
    session => {
      session.set(exchange, ftpProtocol.exchange)
    }

  def ftpClient(session: Session): Option[Exchange] =
    session.attributes.get(exchange).map(_.asInstanceOf[Exchange])

}
