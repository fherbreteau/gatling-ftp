package io.github.fherbreteau.gatling.ftp

import io.gatling.core.session.{EmptyStringExpressionSuccess, Expression}
import io.github.fherbreteau.gatling.ftp
import io.github.fherbreteau.gatling.ftp.action.FtpActionBuilder
import io.github.fherbreteau.gatling.ftp.client.FtpActions._

case class Ftp(operationName: Expression[String]) {

  def mkdir(directory: Expression[String]): FtpActionBuilder = action(directory, EmptyStringExpressionSuccess, Mkdir)

  def move(source: Expression[String], destination: Expression[String]): FtpActionBuilder = action(source, destination, Move)

  def copy(source: Expression[String], destination: Expression[String]): FtpActionBuilder = action(source, destination, Copy)

  def download(file: Expression[String]): FtpActionBuilder = download(file, file)

  def download(source: Expression[String], destination: Expression[String]): FtpActionBuilder = action(source, destination, Download)

  def upload(file: Expression[String]): FtpActionBuilder = upload(file, file)

  def upload(source: Expression[String], destination: Expression[String]): FtpActionBuilder = action(source, destination, Upload)

  def delete(file: Expression[String]): FtpActionBuilder = action(file, EmptyStringExpressionSuccess, Delete)

  def rmdir(directory: Expression[String]): FtpActionBuilder = action(directory, EmptyStringExpressionSuccess, RmDir)

  private def action(source: Expression[String], destination: Expression[String], action: Action): FtpActionBuilder =
    FtpActionBuilder(operationName, source, destination, action)
}
