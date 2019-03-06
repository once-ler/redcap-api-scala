package com.eztier.redcap.test

import java.time.LocalDate

// import io.circe.derivation._
import io.circe.{Decoder, Encoder, derivation}
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.auto._
import io.circe.generic.extras.semiauto.{deriveDecoder, deriveEncoder}

// Custom types
import com.eztier.redcap.types._
import com.eztier.redcap.implicits.EnumerationImplicits._

case class Demographics
(
  Dmmrn: String,
  Dmlast: String,
  Dmfirst: String,
  Dmdob: String
)

trait RepeatInstrument {
  def RecordId: String
  def RedcapRepeatInstrument: String
  def RedcapRepeatInstance: Int
}

trait ResearchSpecimenMinimal {
  def RecordId: String
  def Pretreresspec: Enumeration2.Value
  def Pretredia: String
  def Pretrevisit: Enumeration5.Value
  def Pretrespectype: Enumeration11.Value
}

case class ResearchSpecimen
(
  RecordId: String,
  Pretreresspec: Enumeration2.Value,
  Pretredia: String,
  Pretrevisit: Enumeration5.Value,
  Pretrespectype: Enumeration11.Value
) extends ResearchSpecimenMinimal

case class ResearchSpecimenWithInstrument
(
  RecordId: String,
  RedcapRepeatInstrument: String,
  RedcapRepeatInstance: Int,
  Pretreresspec: Enumeration2.Value,
  Pretredia: String,
  Pretrevisit: Enumeration5.Value,
  Pretrespectype: Enumeration11.Value
) extends ResearchSpecimenMinimal with RepeatInstrument

case class OtherResearchSpecimen
(
  RecordId: String,
  RedcapRepeatInstrument: String,
  RedcapRepeatInstance: Int,
  Pretreresspec: Enumeration2.Value,
  Pretredia: String,
  Pretrevisit: Enumeration5.Value,
  Pretrespectype: Enumeration11.Value = Enumeration11.Option10,
  Pretrespeid_10: String,
  Pretredate_10: LocalDate
) extends ResearchSpecimenMinimal with RepeatInstrument

object RcRecordImplicits {
  // val renameKeys = (name: String) => name.charAt(0).toLower.toString + name.substring(1)
  // Required for circe extras configured semi-auto derivation
  implicit val customConfig: Configuration = Configuration.default.copy(transformMemberNames = derivation.renaming.snakeCase)

  implicit val demographicsEncoder: Encoder[Demographics] = deriveEncoder
  implicit val demographicsDecoder: Decoder[Demographics] = deriveDecoder

  implicit val researchSpecimenEncoder: Encoder[ResearchSpecimen] = deriveEncoder
  implicit val researchSpecimenDecoder: Decoder[ResearchSpecimen] = deriveDecoder

  implicit val researchWithInstrumentSpecimenEncoder: Encoder[ResearchSpecimenWithInstrument] = deriveEncoder
  implicit val researchWithInstrumentSpecimenDecoder: Decoder[ResearchSpecimenWithInstrument] = deriveDecoder

  implicit val otherResearchSpecimenEncoder: Encoder[OtherResearchSpecimen] = deriveEncoder
  implicit val otherResearchSpecimenDecoder: Decoder[OtherResearchSpecimen] = deriveDecoder
}