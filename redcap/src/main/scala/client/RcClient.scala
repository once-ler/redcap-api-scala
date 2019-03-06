package com.eztier.redcap.client

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
// import akka.http.scaladsl.model.HttpProtocols
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.Materializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import io.circe.{Decoder, Encoder, Json}
import io.circe.syntax._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import scala.concurrent.{ExecutionContext, Future}

import com.eztier.redcap.implicits.ExecutionContext._

// import com.eztier.redcap.types._
// import com.eztier.redcap.types.EnumerationImplicits._

class RcClient (config: RcConfig, connection: Option[Flow[HttpRequest, HttpResponse, _]] = None) extends WithRcClient {

  private val uri = Uri
    .apply(config.endpoint.toASCIIString)
    .withPath(Uri.Path(config.path))

  private val defaultRequestBody = Map(
    "token" -> this.config.token,
    "format" -> "json",
    "type" -> "flat"
  )

  private def getUriSource(params: Option[FormData] = None) = {
    val request = HttpRequest(method = HttpMethods.POST,
      uri = uri,
      headers = List(
        RawHeader("Accept", "*/*"),
        RawHeader("Content-Type", "application/x-www-form-urlencoded")
      ),
      protocol = HttpProtocols.`HTTP/1.1`
    )

    val requestWithBody = request.copy(
      entity = params match {
        case Some(a) => a.toEntity(HttpCharsets.`UTF-8`)
        case _ => request.entity
      }
    )

    Source.single(requestWithBody)
  }

  def getSingleRequest[A](implicit ev: Decoder[A]) = {
    Http().singleRequest(HttpRequest(uri = uri))
      .flatMap(Unmarshal(_).to[A])
      .map(Right.apply)
      .recover {case ex: Exception => Left(ex)}
  }

  /*
    records: an array of record IDs to pull. If omitted, all records will be pulled.
    fields: an array of field names to pull. If omitted, all fields are pulled.
    forms: an array of form names you wish to pull records for (spaces replaced with underscores).
           If omitted, all records are pulled.
    filterLogic: A string of logic text that will filter the data to be exported. For example, [age] > 30
                 will only export records where the age field is greater than 30.
   */
  override def exportRecords[A](options: Map[String, String])(implicit ev: Decoder[A]): Future[Either[Throwable, A]] = {
    val params = FormData(
      defaultRequestBody ++
        Map("content" -> "record") ++
        options
    )

    getUriSource(Some(params))
      .via(connection.getOrElse(defaultConnection))
      .mapAsync(1)(handleError)
      .mapAsync(1)(Unmarshal(_).to[A])
      .runWith(Sink.head)
      .map(Right.apply)
      .recover {case ex => Left(ex)}
  }

  override def importRecords[A](records: A, options: Option[Map[String, String]] = None)(implicit ev: Encoder[A]): Future[Either[Throwable, Json]] = {
    val j: Json = records.asJson

    val params = FormData(
      defaultRequestBody ++
        Map(
          "content" -> "record",
          "overwriteBehavior" -> "normal",
          "data" -> j.noSpaces
        ) ++
        options.getOrElse(Map())
    )

    getUriSource(Some(params))
      .via(connection.getOrElse(defaultConnection))
      .mapAsync(1)(handleError)
      .mapAsync(1)(Unmarshal(_).to[Json])
      .runWith(Sink.head)
      .map(Right.apply)
      .recover {
        case ex => Left(ex)
      }
  }

  private def defaultConnection: Flow[HttpRequest, HttpResponse, _] =
    config.endpoint.getScheme match {
      case "http"  => Http().outgoingConnection(config.getHost, config.getPort)
      case "https" => Http().outgoingConnectionHttps(config.getHost, config.getPort)
    }

  private def handleError(response: HttpResponse)(implicit ec: ExecutionContext, mat: Materializer): Future[HttpResponse] = {
    if (response.status.isFailure()) Future.failed(new RuntimeException(s"code: ${response.status.value} description: ${response.status.reason}"))
    else Future.successful(response)
  }
}

object RcClient {
  def apply(config: RcConfig)(implicit system: ActorSystem): RcClient =
    new RcClient(config)

  def apply(config: RcConfig, connection: Flow[HttpRequest, HttpResponse, _])(implicit system: ActorSystem): RcClient =
    new RcClient(config, Some(connection))
}
