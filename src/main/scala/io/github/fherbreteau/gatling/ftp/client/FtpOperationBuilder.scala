package io.github.fherbreteau.gatling.ftp.client

import com.typesafe.scalalogging.LazyLogging
import io.gatling.commons.validation.{SuccessWrapper, Validation, safely}
import io.gatling.core.session.{Expression, Session}
import io.github.fherbreteau.gatling.ftp.client.FtpActions.Action
import io.github.fherbreteau.gatling.ftp.client.FtpOperationBuilder.BuildOperationErrorMapper

object FtpOperationBuilder {
  val BuildOperationErrorMapper: String => String = "Failed to build operation: " + _
}

case class FtpOperationBuilder(operationName: Expression[String],
                               source: Expression[String],
                               destination: Expression[String],
                               action: Action) extends LazyLogging {

  type OperationBuilderConfigure = Session => OperationBuilder => Validation[OperationBuilder]

  val ConfigureIdentity: OperationBuilderConfigure = _ => _.success

  def build: Expression[OperationDef] =
    session =>
      safely(BuildOperationErrorMapper) {
        for {
          requestName <- operationName(session)
          source <- source(session)
          destination <- destination(session)
          operationBuilder = OperationBuilder(requestName, source, destination, action)
          cb <- configOperationBuilder(session, operationBuilder)
        } yield cb.build
      }

  def configOperationBuilder(session: Session, operationBuilder: OperationBuilder): Validation[OperationBuilder] = {
    ConfigureIdentity(session)(operationBuilder)
  }
}

case class OperationBuilder(operationName: String, source: String, destination: String, action: FtpActions.Action) {

  def build: OperationDef = OperationDef(operationName, source, destination, action)
}