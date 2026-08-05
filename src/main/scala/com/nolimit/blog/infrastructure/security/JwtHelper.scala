package com.nolimit.blog.infrastructure.security

import pdi.jwt.{JwtAlgorithm, JwtCirce, JwtClaim}
import io.circe.parser.parse
import java.time.Instant
import java.util.UUID

object JwtHelper {
  private val secretKey = Option(System.getenv("JWT_SECRET")).getOrElse("default_super_secret_key")
  private val algorithm = JwtAlgorithm.HS256

  // Generate Token JWT dengan masa berlaku 24 jam
  def generateToken(userId: UUID): String = {
    val claim = JwtClaim(
      content = s"""{"userId":"${userId.toString}"}""",
      expiration = Some(Instant.now().plusSeconds(86400).getEpochSecond),
      issuedAt = Some(Instant.now().getEpochSecond)
    )
    JwtCirce.encode(claim, secretKey, algorithm)
  }

  // Verifikasi dan dekode Token JWT untuk mengambil userId
  def validateToken(token: String): Option[UUID] = {
    JwtCirce.decode(token, secretKey, Seq(algorithm)).toOption.flatMap { claim =>
      parse(claim.content).toOption.flatMap { json =>
        json.hcursor.downField("userId").as[String].toOption.flatMap { idStr =>
          scala.util.Try(UUID.fromString(idStr)).toOption
        }
      }
    }
  }
}