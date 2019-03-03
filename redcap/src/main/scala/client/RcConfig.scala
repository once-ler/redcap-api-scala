package com.eztier.redcap.client

import java.net.URI

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
