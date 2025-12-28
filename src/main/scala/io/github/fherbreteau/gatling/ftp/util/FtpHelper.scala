package io.github.fherbreteau.gatling.ftp.util

import com.typesafe.scalalogging.StrictLogging
import io.gatling.commons.model.Credentials
import io.gatling.core.session.{Expression, Session}

private[ftp] object FtpHelper extends StrictLogging{
 def buildCredentials(username: Expression[String], password: Expression[String]):  Expression[Credentials] =
   (session: Session) =>
     for {
       usernameValue <- username(session)
       passwordValue <- password(session)
     } yield Credentials(usernameValue, passwordValue)

}
