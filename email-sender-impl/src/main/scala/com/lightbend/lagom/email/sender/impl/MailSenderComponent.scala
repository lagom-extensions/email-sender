package com.lightbend.lagom.email.sender.impl

import org.springframework.mail.javamail.JavaMailSenderImpl

trait MailSenderComponent {
  val settings: EmailSettinsConfig
  // intentionally impl, for spring actuator integration
  lazy val mailSender: JavaMailSenderImpl = {
    val mailSender = new JavaMailSenderImpl()
    mailSender.setHost(settings.smtpHost)
    mailSender.setPort(settings.smtpPort)
    mailSender.setUsername(settings.smtpUserName)
    mailSender.setPassword(settings.smtpPassword)

    val props = mailSender.getJavaMailProperties
    props.put("mail.transport.protocol", "smtp")
    props.put("mail.smtp.auth", s"${settings.smtpAuth}")
    props.put("mail.smtp.starttls.enable", s"${settings.smtpTlsEnabled}")
    props.put("mail.debug", s"${settings.smtpDebug}")

    mailSender
  }

}
