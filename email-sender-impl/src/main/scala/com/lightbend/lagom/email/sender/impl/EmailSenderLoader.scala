package com.lightbend.lagom.email.sender.impl

import com.lightbend.lagom.scaladsl.client.ConfigurationServiceLocatorComponents
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.server.{LagomApplication, _}
import com.softwaremill.macwire._
import com.lightbend.lagom.email.sender.api.EmailSenderService
import play.api.libs.ws.ahc.AhcWSComponents

trait EmailSenderComponents extends LagomServerComponents with MailSenderComponent {
  this: LagomApplication =>
  implicit override lazy val settings = wire[EmailSenderSettings]
  lazy val emailManager = wire[EmailManager]
  override lazy val lagomServer = serverFor[EmailSenderService](wire[EmailSenderServiceImpl])
}

abstract class EmailSenderApplication(context: LagomApplicationContext) extends LagomApplication(context) with EmailSenderComponents with AhcWSComponents {}

class EmailSenderLoader extends LagomApplicationLoader {
  override def load(context: LagomApplicationContext) = new EmailSenderApplication(context) with ConfigurationServiceLocatorComponents
  override def loadDevMode(context: LagomApplicationContext) = new EmailSenderApplication(context) with LagomDevModeComponents
  override def describeService = Some(readDescriptor[EmailSenderService])
}
