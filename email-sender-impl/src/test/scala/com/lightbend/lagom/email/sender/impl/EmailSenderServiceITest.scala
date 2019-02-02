package com.lightbend.lagom.email.sender.impl

import java.nio.file.{Files, Paths}

import com.lightbend.lagom.email.sender.api._
import com.lightbend.lagom.scaladsl.server.{LagomApplication, LocalServiceLocator}
import com.lightbend.lagom.scaladsl.testkit.ServiceTest
import org.scalatest._
import play.api.libs.ws.ahc.AhcWSComponents

class EmailSenderServiceITest extends AsyncWordSpec with BeforeAndAfterAll with Matchers {
  private val server = ServiceTest.startServer(ServiceTest.defaultSetup) { ctx =>
    new LagomApplication(ctx) with LocalServiceLocator with EmailSenderComponents with AhcWSComponents
  }
  override def afterAll() = server.stop()

  private val service = server.serviceClient.implement[EmailSenderService]

  "The email sender service" should {
    "send email" in {
      val mailContent = MailContent(
        from = "no-reply@email.com",
        recipients = Seq("dmitriy.kuzkin@gmail.com"),
        htmlContent = "<div>Test email</div>",
        attachments = Seq(MailFileAttachment("scala-ecosystem.jpg", Files.readAllBytes(Paths.get(this.getClass.getResource("/attachments/scala.jpg").toURI))))
      )
      for {
        _ <- service.send.invoke(mailContent)
      } yield Succeeded // intentionally this is dev check with mailhog ui
    }
  }
}
