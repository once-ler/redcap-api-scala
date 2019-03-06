package com.eztier.redcap.implicits

import com.eztier.redcap.types._
import io.circe.generic.extras.Configuration
import io.circe.{Decoder, Encoder}

object EnumerationImplicits {
  val renameKeys = (name: String) => name.charAt(0).toLower.toString + name.substring(1)

  implicit val customConfig: Configuration = Configuration.default.copy(transformMemberNames = renameKeys)

  implicit val encodeEnumeration2: Encoder[Enumeration2.Value] = Encoder.encodeString.contramap[Enumeration2.Value](_.toString)
  implicit val decodeEnumeration2: Decoder[Enumeration2.Value] = Decoder.decodeString.emap { str =>
    Enumeration2.values.find(_.toString == str) match {
      case Some(a) => Right(a)
      case _ => Left("Enumeration not found")
    }
  }

  implicit val encodeEnumeration3: Encoder[Enumeration3.Value] = Encoder.encodeString.contramap[Enumeration3.Value](_.toString)
  implicit val decodeEnumeration3: Decoder[Enumeration3.Value] = Decoder.decodeString.emap { str =>
    Enumeration3.values.find(_.toString == str) match {
      case Some(a) => Right(a)
      case _ => Left("Enumeration not found")
    }
  }

  implicit val encodeEnumeration4: Encoder[Enumeration4.Value] = Encoder.encodeString.contramap[Enumeration4.Value](_.toString)
  implicit val decodeEnumeration4: Decoder[Enumeration4.Value] = Decoder.decodeString.emap { str =>
    Enumeration4.values.find(_.toString == str) match {
      case Some(a) => Right(a)
      case _ => Left("Enumeration not found")
    }
  }

  implicit val encodeEnumeration5: Encoder[Enumeration5.Value] = Encoder.encodeString.contramap[Enumeration5.Value](_.toString)
  implicit val decodeEnumeration5: Decoder[Enumeration5.Value] = Decoder.decodeString.emap { str =>
    Enumeration5.values.find(_.toString == str) match {
      case Some(a) => Right(a)
      case _ => Left("Enumeration not found")
    }
  }

  implicit val encodeEnumeration6: Encoder[Enumeration6.Value] = Encoder.encodeString.contramap[Enumeration6.Value](_.toString)
  implicit val decodeEnumeration6: Decoder[Enumeration6.Value] = Decoder.decodeString.emap { str =>
    Enumeration6.values.find(_.toString == str) match {
      case Some(a) => Right(a)
      case _ => Left("Enumeration not found")
    }
  }

  implicit val encodeEnumeration7: Encoder[Enumeration7.Value] = Encoder.encodeString.contramap[Enumeration7.Value](_.toString)
  implicit val decodeEnumeration7: Decoder[Enumeration7.Value] = Decoder.decodeString.emap { str =>
    Enumeration7.values.find(_.toString == str) match {
      case Some(a) => Right(a)
      case _ => Left("Enumeration not found")
    }
  }

  implicit val encodeEnumeration8: Encoder[Enumeration8.Value] = Encoder.encodeString.contramap[Enumeration8.Value](_.toString)
  implicit val decodeEnumeration8: Decoder[Enumeration8.Value] = Decoder.decodeString.emap { str =>
    Enumeration8.values.find(_.toString == str) match {
      case Some(a) => Right(a)
      case _ => Left("Enumeration not found")
    }
  }

  implicit val encodeEnumeration9: Encoder[Enumeration9.Value] = Encoder.encodeString.contramap[Enumeration9.Value](_.toString)
  implicit val decodeEnumeration9: Decoder[Enumeration9.Value] = Decoder.decodeString.emap { str =>
    Enumeration9.values.find(_.toString == str) match {
      case Some(a) => Right(a)
      case _ => Left("Enumeration not found")
    }
  }

  implicit val encodeEnumeration10: Encoder[Enumeration10.Value] = Encoder.encodeString.contramap[Enumeration10.Value](_.toString)
  implicit val decodeEnumeration10: Decoder[Enumeration10.Value] = Decoder.decodeString.emap { str =>
    Enumeration10.values.find(_.toString == str) match {
      case Some(a) => Right(a)
      case _ => Left("Enumeration not found")
    }
  }

  implicit val encodeEnumeration11: Encoder[Enumeration11.Value] = Encoder.encodeString.contramap[Enumeration11.Value](_.toString)
  implicit val decodeEnumeration11: Decoder[Enumeration11.Value] = Decoder.decodeString.emap { str =>
    Enumeration11.values.find(_.toString == str) match {
      case Some(a) => Right(a)
      case _ => Left("Enumeration not found")
    }
  }
}
