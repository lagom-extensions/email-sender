package com.lightbend.lagom.email.sender.api

import akka.Done
import com.lightbend.lagom.email.sender.api.EmailSenderService.serviceName
import com.lightbend.lagom.scaladsl.api.Service._
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}
import enumeratum.{Enum, EnumEntry}
import julienrf.json.derived
import play.api.libs.json.{Format, Json}

trait EmailSenderService extends Service {
  def send: ServiceCall[MailContent, Done]
  def descriptor: Descriptor = named(serviceName).withCalls(pathCall("/api/send", send))
}

object EmailSenderService {
  val serviceName = "email-sender"
}

sealed trait MailPriority extends EnumEntry { val priority: String; val importance: String }
object MailPriority extends Enum[MailPriority] {
  val values = findValues
  case object LOW extends MailPriority { override val priority: String = "5"; override val importance: String = "Low" }
  case object NORMAL extends MailPriority { override val priority: String = "3"; override val importance: String = "Standard" }
  case object HIGH extends MailPriority { override val priority: String = "1"; override val importance: String = "High" }
  implicit val format: Format[MailPriority] = derived.oformat()
}

sealed trait MailAttachment
object MailAttachment { implicit val format: Format[MailAttachment] = derived.oformat() }
case class MailInlineAttachment(name: String, bytes: Array[Byte], contentId: String) extends MailAttachment
case class MailFileAttachment(name: String, bytes: Array[Byte]) extends MailAttachment

case class MailContent(
    from: String,
    readReceiptRequestTo: Option[String] = None,
    sender: Option[String] = None,
    replyTo: Seq[String] = Seq.empty,
    recipients: Seq[String],
    cc: Seq[String] = Seq.empty,
    bcc: Seq[String] = Seq.empty,
    subject: Option[String] = None,
    htmlContent: String,
    priority: Option[MailPriority] = None,
    attachments: Seq[MailAttachment] = Seq.empty
)
object MailContent {
  implicit val format: Format[MailContent] = Json.format
}
