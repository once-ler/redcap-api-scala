package com.eztier.redcap.test

import java.net.URI
import java.time.LocalDate

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{FormData, HttpRequest, Uri}
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.eztier.redcap.client.{RcClient, RcConfig}
import org.scalatest.{FunSpec, Matchers}

import scala.concurrent.Await
import scala.concurrent.duration._
import com.eztier.redcap.implicits.ExecutionContext._
import com.eztier.redcap.test.UserImplicits._
import com.eztier.redcap.types.{Enumeration11, Enumeration2, Enumeration5}
import io.circe.{Decoder, Json}
import io.circe.syntax._

// import io.circe.generic.auto._
// import io.circe.syntax._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._

class TestREDCapClientSpec extends FunSpec with Matchers {

  describe("REDCap Suite") {
    val testUrl = "https://jsonplaceholder.typicode.com/users"
    val config = RcConfig("ABC123TOKEN", URI.create(testUrl), "/users")

    it("Should convert array to querystring correctly") {
      // querystring.stringify({ foo: 'bar', baz: ['qux', 'quux'], corge: '' });
      // returns 'foo=bar&baz=qux&baz=quux&corge='

      /*
      Reference from the R lib: https://github.com/nutterb/redcapAPI/blob/master/R/exportRecords.R#L346
      body[['fields']] <- paste0(field_names, collapse=",")
      if (!is.null(forms)) body[['forms']] <- paste0(forms, collapse=",")
      if (!is.null(events)) body[['events']] <- paste0(events, collapse=",")
      if (!is.null(records)) body[['records']] <- paste0(records, collapse=",")
      */

      val records = Seq("a", "b", "c")
      val forms = Seq("d", "e", "f")
      val fields = Seq("f0", "f1", "f2")

      val fd = FormData(
        ("records", records.mkString(",")),
        ("forms", forms.mkString(",")),
        ("fields" -> "1"),
        ("fields" -> "2"),
        ("fields" -> "3"),
        ("format", "json")
      )

      val fd2 = FormData(
        "format" -> "xml",
        "fields" -> fields.mkString(",")
      )

      val fd3 = FormData((fd.fields ++ fd2.fields).toMap)

      fd3.fields.get("format") should be (Some("xml"))
    }

    it("Should implicitly return a typed case class from json array") {
      val f = Http().singleRequest(HttpRequest(uri = testUrl))
        .flatMap(Unmarshal(_).to[Json])

      val r = Await.result(f, 10 seconds)

      val l = r.as[List[User]] // Right(List[User])
    }

    def getSingleRequestFromUrl[A](uriStr: String)(implicit ev: Decoder[A]) = {
      Http().singleRequest(HttpRequest(uri = uriStr))
        .flatMap(Unmarshal(_).to[A])
        .map(Right.apply)
        .recover{case ex: Exception => Left(ex)}
    }

    it("Should accept generic type if implicit circe decoder is in scope") {
      val f = getSingleRequestFromUrl[List[UserSlim]](testUrl)
      val r = Await.result(f, 10 seconds)

      r should be ('right)
    }

    it("Should accept generic type for RcClient if implicit circe decoder is in scope") {
      val client = new RcClient(config)
      val f = client.getSingleRequest[List[UserSlim]]
      val r = Await.result(f, 10 seconds) // Future[Either[Exception, List[UserSlim]]]

      r should be ('right)
    }

    it("Should adapt to case classes with fewer fields than the raw JSON") {
      val f = Http().singleRequest(HttpRequest(uri = testUrl))
        .flatMap(Unmarshal(_).to[List[UserSlim]])
        .map(Right.apply)
        .recover{case ex: Exception => Left(ex)}

      val r = Await.result(f, 10 seconds) // Future[Either[Exception, List[UserSlim]]]

      r should be ('right)
    }

    it("Should export demographics records") {
      import com.eztier.common.Configuration.conf
      import com.eztier.redcap.test.RcRecordImplicits._

      val client = new RcClient(RcConfig(conf))

      val f = client.exportRecords[List[Demographics]](
        Map(
          "records" -> "20",
          "fields" -> "dmmrn,dmlast,dmfirst,dmdob",
          "forms" -> "demographics"
        )
      )

      val r = Await.result(f, 10 seconds)

      println(r)
    }

    it("Should export all demographics records") {
      import com.eztier.common.Configuration.conf
      import com.eztier.redcap.test.RcRecordImplicits._

      val client = new RcClient(RcConfig(conf))

      val f = client.exportRecords[List[Demographics]](
        Map(
          "fields" -> "dmmrn,dmlast,dmfirst,dmdob",
          "forms" -> "demographics",
          "filterLogic" -> "[dmmrn] <> ''"
        )
      )

      val r = Await.result(f, 10 seconds)

      println(r)

    }

    it("Should export research specimen records") {
      import com.eztier.common.Configuration.conf
      import com.eztier.redcap.test.RcRecordImplicits._

      val client = new RcClient(RcConfig(conf))

      val f = client.exportRecords[List[ResearchSpecimen]](
        Map(
          "records" -> "20",
          "fields" -> "record_id,pretreresspec,pretredia,pretrevisit,pretrespectype",
          "forms" -> "pretreatmentinitial_research_specimens"
        )
      )

      val r = Await.result(f, 10 seconds)

      println(r)
    }

    it("Should export all fields in research specimen records in JSON") {
      import com.eztier.common.Configuration.conf
      import com.eztier.redcap.test.RcRecordImplicits._

      val client = new RcClient(RcConfig(conf))

      val f = client.exportRecords[List[Json]](
        Map(
          "records" -> "20",
          "forms" -> "pretreatmentinitial_research_specimens"
        )
      )

      val r = Await.result(f, 10 seconds)

      println(r)
    }

    it("Should import research specimen records") {
      import com.eztier.common.Configuration.conf
      import com.eztier.redcap.test.RcRecordImplicits._

      val client = new RcClient(RcConfig(conf))

      val rec = OtherResearchSpecimen(
        RecordId = "20",
        RedcapRepeatInstrument = "pretreatmentinitial_research_specimens",
        RedcapRepeatInstance = 21, // 32767 is max instance number.
        Pretreresspec = Enumeration2.Option0,
        Pretredia = "",
        Pretrevisit = Enumeration5.Option2,
        Pretrespectype = Enumeration11.Option10,
        Pretrespeid_10 = "OTHER123",
        Pretredate_10 = LocalDate.now
      )

      val j = rec asJson
      val b = j.hcursor.get[String]("record_id").toOption

      import io.circe.optics.JsonPath._

      val c = root.record_id.string
      val d = c.getOption(j)

      b should === (d)

      val f = client.importRecords(List(rec))

      val r = Await.result(f, 10 seconds)

      println(r)

    }

  }
}
