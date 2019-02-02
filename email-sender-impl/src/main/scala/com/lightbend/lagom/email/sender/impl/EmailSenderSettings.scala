package com.lightbend.lagom.email.sender.impl

import com.typesafe.config.ConfigFactory

final class EmailSenderSettings extends EmailSettinsConfig {
  private val rootConfig = ConfigFactory.load

  private val smtp = rootConfig.getConfig("smtp")
  override val smtpUserName = smtp.getString("userName")
  override val smtpPassword = smtp.getString("password")
  override val smtpHost = smtp.getString("host")
  override val smtpPort = smtp.getInt("port")
  override val smtpAuth = smtp.getBoolean("auth")
  override val smtpDebug = smtp.getBoolean("debug")
  override val smtpTlsEnabled = smtp.getBoolean("tlsEnabled")

}
