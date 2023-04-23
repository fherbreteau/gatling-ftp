package io.github.fherbreteau.gatling.ftp.client

import com.typesafe.scalalogging.StrictLogging
import io.gatling.core.action.Action
import io.gatling.core.session.Session

case class FtpTransaction(session: Session,
                          ftpOperation: FtpOperation,
                          next: Action) extends StrictLogging {

  def fullRequestName: String = ftpOperation.operationName

  def server: String = ftpOperation.ftpProtocol.exchange.server

  def scenario: String = session.scenario

  def userId: Long = session.userId

  def throttled: Boolean = ftpOperation.throttled

  def action: OperationDef = ftpOperation.definition
}
