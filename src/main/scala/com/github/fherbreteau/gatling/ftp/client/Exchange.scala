package com.github.fherbreteau.gatling.ftp.client

import com.github.fherbreteau.gatling.ftp.client.result.{FtpFailure, FtpResult}
import com.typesafe.scalalogging.StrictLogging
import io.gatling.commons.model.Credentials
import io.gatling.commons.stats.KO
import io.gatling.core.CoreComponents
import io.gatling.core.session.Session
import io.gatling.core.stats.StatsEngine

import java.util.concurrent.{Executor, Executors}
import scala.util.control.NonFatal

object Exchange  {

  def apply(server: String, port: Int, credentials: Credentials): Exchange =
    Exchange(
      factory = FtpClientFactory(),
      server = server,
      port = port,
      credentials = credentials,
      executor = Executors.newSingleThreadExecutor()
    )
}

final case class Exchange(factory: FtpClientFactory,
                          server: String,
                          port: Int,
                          credentials: Credentials,
                          executor: Executor) extends StrictLogging {
  def execute(transaction: FtpTransaction, coreComponents: CoreComponents): Unit = {
    logger.debug(s"Sending operation=${transaction.fullRequestName} server=${transaction.server} scenario=${transaction.scenario} userId=${transaction.userId}")
    coreComponents.throttler match {
      case Some(th) if transaction.throttled =>
        th.throttle(transaction.scenario,
          () => executeOperation(transaction, coreComponents)
        )
      case _ => executeOperation(transaction, coreComponents)
    }
  }

  private def executeOperation(transaction: FtpTransaction, components: CoreComponents): Unit = {
    executor.execute(() => executeOperationAsync(transaction, components))
  }

  private def executeOperationAsync(transaction: FtpTransaction, coreComponents: CoreComponents): Unit = {
    import coreComponents._
    val startTime = clock.nowMillis
    val client = factory.createClient(transaction, server, port)
    val result = try {
      logger.debug(s"Creating New Session scenario=${transaction.scenario} userId=${transaction.userId}")
      client.connect(server, port)
      if (!client.login(credentials.username, credentials.password)) {
        FtpFailure(transaction.action, startTime, clock.nowMillis, "Failed to login to server", KO)
      } else {
        logger.debug(s"Executing required operation scenario=${transaction.scenario} userId=${transaction.userId}")
        val result = transaction.ftpOperation.build.apply(client, startTime, clock)
        if (!client.logout()) {
          FtpFailure(transaction.action, startTime, clock.nowMillis, "Failed to loggout from server", KO)
        }
        result
      }
    } catch {
      case NonFatal(t) =>
        logger.error(s"Failed to execute action ${transaction.action}", t)
        FtpFailure(transaction.action, startTime, clock.nowMillis, t.getMessage, KO)
    } finally {
      factory.removeClient(transaction)
    }
    logger.debug(s"Ftp Operation completed scenario=${transaction.scenario} userId=${transaction.userId}")
    logResult(statsEngine, transaction.session, transaction.fullRequestName, result)
    transaction.next ! transaction.session
  }

  private def logResult(statsEngine: StatsEngine, session: Session, fullRequestName: String, result: FtpResult): Unit = {
    statsEngine.logResponse(
      session.scenario,
      session.groups,
      fullRequestName,
      result.startTimestamp,
      result.endTimestamp,
      result.status,
      None,
      result.message
    )
  }

  def stop(): Unit = factory.clean()
}
