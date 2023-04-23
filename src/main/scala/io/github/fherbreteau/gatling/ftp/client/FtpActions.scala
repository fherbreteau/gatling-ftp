package io.github.fherbreteau.gatling.ftp.client

object FtpActions extends Enumeration {

  type Action = Value

  val Copy, Move, Delete, Upload, Download = Value
}
