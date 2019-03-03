package com.eztier.redcap.client

import io.circe.{Decoder, Encoder, Json}
import scala.concurrent.Future

trait WithRcClient {
  def exportRecords[A](options: Map[String, String])(implicit ev: Decoder[A]): Future[Either[Throwable, A]]

  def importRecords[A](options: Map[String, String], records: A)(implicit ev: Encoder[A]): Future[Either[Throwable, Json]]
}

