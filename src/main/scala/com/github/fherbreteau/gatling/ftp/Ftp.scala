package com.github.fherbreteau.gatling.ftp

import com.github.fherbreteau.gatling.ftp.action.FtpActionBuilder
import com.github.fherbreteau.gatling.ftp.client.FtpActions._
import io.gatling.core.session.Expression

case class Ftp(operationName: Expression[String]) {

  def move(file: Expression[String]): FtpActionBuilder = action(file, Move)

  def copy(file: Expression[String]): FtpActionBuilder = action(file, Copy)

  def delete(file: Expression[String]): FtpActionBuilder = action(file, Delete)

  def download(file: Expression[String]): FtpActionBuilder = action(file, Download)

  def upload(file: Expression[String]): FtpActionBuilder = action(file, Upload)

  private def action(file: Expression[String], action: Action): FtpActionBuilder =
    FtpActionBuilder(operationName, file, action)
}
