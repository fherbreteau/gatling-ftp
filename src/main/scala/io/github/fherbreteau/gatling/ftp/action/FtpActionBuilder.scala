package io.github.fherbreteau.gatling.ftp.action

import io.gatling.core.action.Action
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.protocol.ProtocolComponentsRegistry
import io.gatling.core.session.Expression
import io.gatling.core.structure.ScenarioContext
import io.github.fherbreteau.gatling.ftp.client.FtpActions.{Action => FtpClientAction}
import io.github.fherbreteau.gatling.ftp.client.{FtpOperationBuilder, FtpOperationDef}
import io.github.fherbreteau.gatling.ftp.protocol.{FtpComponents, FtpProtocol}

case class FtpActionBuilder(operationName: Expression[String],
                            file: Expression[String],
                            action: FtpClientAction) extends ActionBuilder {
  override def build(ctx: ScenarioContext, next: Action): Action = {
    val ftpComponents = lookUpSftpComponents(ctx.protocolComponentsRegistry)
    val ftpOperationDef = build(ftpComponents.ftpProtocol, ctx.throttled)
    new FtpAction(ftpOperationDef, ctx.coreComponents, next)
  }

  private def build(ftpProtocol: FtpProtocol, throttled: Boolean): FtpOperationDef = {
    val resolvedOperationExpression = new FtpOperationBuilder(operationName, file, action).build
    FtpOperationDef(
      operationName,
      resolvedOperationExpression,
      ftpProtocol,
      throttled
    )
  }

  private def lookUpSftpComponents(protocolComponentsRegistry: ProtocolComponentsRegistry): FtpComponents =
    protocolComponentsRegistry.components(FtpProtocol.FtpProtocolKey)
}

