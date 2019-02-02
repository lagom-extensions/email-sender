package com.lightbend.lagom.email.sender.impl

import akka.Done
import com.lightbend.lagom.email.sender.api.{MailAttachment, MailContent, MailFileAttachment, MailInlineAttachment}
import javax.activation.DataHandler
import javax.mail.internet.{InternetAddress, MimeBodyPart, MimeMessage, MimeMultipart}
import javax.mail.util.ByteArrayDataSource
import javax.mail.{BodyPart, Message, Part}
import org.springframework.mail.javamail.{JavaMailSenderImpl, MimeMessagePreparator}

import scala.concurrent.Future

class EmailManager(val mailSender: JavaMailSenderImpl) {

  def sendMail(content: MailContent): Future[Done] = {
    mailSender.send(new MimeMessagePreparator {
      override def prepare(message: MimeMessage): Unit = {
        message.setFrom(new InternetAddress(content.from))
        message.setSubject(content.subject.getOrElse(""), "UTF-8")
        for (email <- content.sender) message.setSender(new InternetAddress(email))
        for (email <- content.recipients) message.addRecipient(Message.RecipientType.TO, new InternetAddress(email))
        for (email <- content.cc) message.addRecipient(Message.RecipientType.CC, new InternetAddress(email))
        for (email <- content.bcc) message.addRecipient(Message.RecipientType.BCC, new InternetAddress(email))
        if (content.replyTo.nonEmpty) message.setReplyTo(content.replyTo.map(email => new InternetAddress(email)).toArray)
        val multipart = new MimeMultipart()
        val textBodyPart = new MimeBodyPart()
        textBodyPart.setContent(content.htmlContent, "text/html; charset=utf-8")
        multipart.addBodyPart(textBodyPart)
        for (attachmentBodyPart <- content.attachments.map(toEmailBodyPart)) multipart.addBodyPart(attachmentBodyPart)
        message.setContent(multipart)
        for (priority <- content.priority) {
          message.setHeader("X-Priority", priority.priority)
          message.setHeader("Importance", priority.importance)
        }
        for (email <- content.readReceiptRequestTo) message.setHeader("Disposition-Notification-To", new InternetAddress(email).toString)
      }
    })
    Future.successful(Done)
  }

  private def toEmailBodyPart(attachment: MailAttachment): BodyPart = {
    def emailDataHandler(bytes: Array[Byte]): DataHandler = new DataHandler(new ByteArrayDataSource(bytes, "application/octet-stream"))
    attachment match {
      case inlineAttachment: MailInlineAttachment =>
        val res = new MimeBodyPart()
        res.setDisposition(Part.INLINE)
        res.setContentID(s"<${inlineAttachment.contentId}>")
        res.setDataHandler(emailDataHandler(inlineAttachment.bytes))
        res.setFileName(inlineAttachment.name)
        res
      case fileAttachment: MailFileAttachment =>
        val res = new MimeBodyPart()
        res.setDataHandler(emailDataHandler(fileAttachment.bytes))
        res.setFileName(fileAttachment.name)
        res
    }
  }
}

trait EmailSettinsConfig {
  val smtpUserName: String
  val smtpPassword: String
  val smtpHost: String
  val smtpPort: Int
  val smtpAuth: Boolean
  val smtpTlsEnabled: Boolean
  val smtpDebug: Boolean
}
