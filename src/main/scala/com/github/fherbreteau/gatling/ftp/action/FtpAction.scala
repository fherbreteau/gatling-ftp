package com.github.fherbreteau.gatling.ftp.action

import com.github.fherbreteau.gatling.ftp.client.{FtpOperationDef, FtpTransaction}
import io.gatling.commons.util.Clock
import io.gatling.commons.validation.Validation
import io.gatling.core.CoreComponents
import io.gatling.core.action.{Action, RequestAction}
import io.gatling.core.session.{Expression, Session}
import io.gatling.core.stats.StatsEngine
import io.gatling.core.util.NameGen

class FtpAction(ftpOperationDef: FtpOperationDef,
                 coreComponents: CoreComponents,
                 val next: Action) extends RequestAction with NameGen {

  override val name: String = genName("ftpOperation")

  override def clock: Clock = coreComponents.clock

  override def requestName: Expression[String] = ftpOperationDef.operationName

  override def statsEngine: StatsEngine = coreComponents.statsEngine

  override def sendRequest(session: Session): Validation[Unit] =
    ftpOperationDef.build(session).map { ftpOperation =>
      val transaction = FtpTransaction(
        session,
        ftpOperation,
        next)
      ftpOperation.ftpProtocol.exchange.execute(transaction, coreComponents)
    }

}
