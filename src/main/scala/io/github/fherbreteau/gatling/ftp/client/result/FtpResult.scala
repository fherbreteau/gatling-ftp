package io.github.fherbreteau.gatling.ftp.client.result

import io.gatling.commons.stats.Status
import io.github.fherbreteau.gatling.ftp.client.OperationDef

trait FtpResult {
  def operation: OperationDef

  def startTimestamp: Long

  def endTimestamp: Long

  def status: Status

  def message: Option[String]
}

final case class FtpFailure(operation: OperationDef,
                            startTimestamp: Long,
                            endTimestamp: Long,
                            errorMessage: String,
                            status: Status) extends FtpResult {
  override def message: Option[String] = Some(errorMessage)
}

final case class FtpResponse(operation: OperationDef,
                             startTimestamp: Long,
                             endTimestamp: Long,
                             status: Status) extends FtpResult {
  override def message: Option[String] = None
}
