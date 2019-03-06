package com.eztier.redcap.client

import java.net.URI

import com.typesafe.config.Config

trait WithRcConfig {
  def token: String
  def endpoint: URI
  def path: String
}

case class RcConfig
(
  token: String,
  endpoint: URI,
  path: String = "/apps/redcap/api"
) extends WithRcConfig {
  def getHost: String = endpoint.getHost
  def getPort: Int = endpoint.getScheme match {
    case "http"  => if (endpoint.getPort == -1) 80 else endpoint.getPort
    case "https" => if (endpoint.getPort == -1) 443 else endpoint.getPort
  }
}

object RcConfig {
  def apply(conf: Config) = {
    val token = conf.getString("redcap.token")
    val endpoint = conf.getString("redcap.endpoint")
    val path = conf.getString("redcap.path")

    new RcConfig(token, URI.create(endpoint), path)
  }
}
