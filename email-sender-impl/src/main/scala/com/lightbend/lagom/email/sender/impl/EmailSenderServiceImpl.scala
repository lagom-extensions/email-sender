package com.lightbend.lagom.email.sender.impl

import akka.Done
import com.lightbend.lagom.email.sender.api.{EmailSenderService, MailContent}
import com.lightbend.lagom.scaladsl.api.ServiceCall

class EmailSenderServiceImpl(emailManager: EmailManager) extends EmailSenderService {
  override def send: ServiceCall[MailContent, Done] = ServiceCall(mailContent => emailManager.sendMail(mailContent))
}
