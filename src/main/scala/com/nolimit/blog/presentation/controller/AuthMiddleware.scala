package com.nolimit.blog.presentation.controller

import cats.data.{Kleisli, OptionT}
import cats.effect.IO
import com.nolimit.blog.infrastructure.security.JwtHelper
import com.nolimit.blog.presentation.dto.ApiResponse
import org.http4s._
import org.http4s.headers.Authorization
import org.http4s.server.{AuthMiddleware => Http4sAuthMiddleware}
import org.http4s.dsl.io._
import org.http4s.headers.`WWW-Authenticate`
import java.util.UUID

object JwtAuthMiddleware {

  private val authUser: Kleisli[OptionT[IO, *], Request[IO], UUID] =
    Kleisli { request =>
      OptionT.fromOption[IO] {
        for {
          header <- request.headers.get[Authorization]
          Credentials.Token(AuthScheme.Bearer, token) = header.credentials
          userId <- JwtHelper.validateToken(token)
        } yield userId
      }
    }

  private def authFailure(request: Request[IO]): IO[Response[IO]] =
    Unauthorized(
      `WWW-Authenticate`(Challenge("Bearer", "blog-api")),
      ApiResponse.error[String]("Token JWT tidak valid atau tidak ditemukan")
    )

  val middleware: Http4sAuthMiddleware[IO, UUID] =
    Http4sAuthMiddleware.noSpider(authUser, authFailure)
}
