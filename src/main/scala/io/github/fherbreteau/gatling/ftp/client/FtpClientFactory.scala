package io.github.fherbreteau.gatling.ftp.client

import com.typesafe.scalalogging.StrictLogging
import org.apache.commons.net.ftp.FTPClient

final case class FtpClientFactory() extends StrictLogging {
  private var ftpClientMap = scala.collection.mutable.Map[FtpTransaction, FTPClient]()

  def createClient(transaction: FtpTransaction, server: String, port: Int): FTPClient = {
    logger.debug(s"Create FTP connection to $server:$port scenario=${transaction.scenario} userId=${transaction.userId}")
    val client = new FTPClient()
    client.connect(server, port)
    ftpClientMap += (transaction -> client)
    client
  }

  def removeClient(transaction: FtpTransaction): Unit = {
    logger.debug(s"Disconnect FTP connection scenario=${transaction.scenario} userId=${transaction.userId}")
    ftpClientMap(transaction).disconnect()
    ftpClientMap -= transaction
  }

  def clean(): Unit = {
    ftpClientMap.values.foreach(_.disconnect())
    ftpClientMap.clear()
  }
}

