package io.github.fherbreteau.gatling.ftp

import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.session.Expression
import io.github.fherbreteau.gatling.ftp.protocol.{FtpProtocol, FtpProtocolBuilder}

class FtpDsl {

  def ftp(implicit configuration: GatlingConfiguration): FtpProtocolBuilder = FtpProtocolBuilder(configuration)

  def ftp(requestName: Expression[String]) = Ftp(requestName)

  implicit def ftpProtocolBuilder2SftpProtocol(builder: FtpProtocolBuilder): FtpProtocol = builder.build

}
