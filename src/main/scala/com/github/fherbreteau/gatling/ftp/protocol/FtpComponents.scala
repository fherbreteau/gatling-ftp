package com.github.fherbreteau.gatling.ftp.protocol

import com.github.fherbreteau.gatling.ftp.client.FtpClients
import io.gatling.core.protocol.ProtocolComponents
import io.gatling.core.session.Session

case class FtpComponents(ftpProtocol: FtpProtocol) extends ProtocolComponents {

  override lazy val onStart: Session => Session = FtpClients.setFtpClient(ftpProtocol)

  override def onExit: Session => Unit = session => {
    FtpClients.ftpClient(session).foreach(_.stop())
  }
}

