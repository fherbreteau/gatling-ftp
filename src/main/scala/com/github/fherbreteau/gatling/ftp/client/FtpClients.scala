package com.github.fherbreteau.gatling.ftp.client

import com.github.fherbreteau.gatling.ftp.protocol.FtpProtocol
import io.gatling.core.session.Session

object FtpClients {
  private val exchange: String = "sftp.exchange"

  def setFtpClient(ftpProtocol: FtpProtocol): Session => Session =
    session => {
      session.set(exchange, ftpProtocol.exchange)
    }

  def ftpClient(session: Session): Option[Exchange] =
    session.attributes.get(exchange).map(_.asInstanceOf[Exchange])

}
