package com.nolimit.blog.presentation.controller

import cats.effect.IO
import com.nolimit.blog.application.AuthService
import com.nolimit.blog.presentation.dto.{ApiResponse, LoginRequest, RegisterRequest, UserResponse}
import com.nolimit.blog.presentation.dto.Requests._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.headers.`WWW-Authenticate`
import io.circe.generic.auto._

class AuthController(authService: AuthService) {

  implicit val registerReqEntityDecoder: EntityDecoder[IO, RegisterRequest] = jsonOf[IO, RegisterRequest]
  implicit val loginReqEntityDecoder: EntityDecoder[IO, LoginRequest] = jsonOf[IO, LoginRequest]

  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {

    case req @ POST -> Root / "register" =>
      for {
        registerReq <- req.as[RegisterRequest]
        result      <- authService.register(registerReq)
        response    <- result match {
          case Right(user) =>
            Created(ApiResponse.success("User berhasil terdaftar", UserResponse.fromUser(user)))
          case Left(err) =>
            BadRequest(ApiResponse.error[String](err))
        }
      } yield response

    case req @ POST -> Root / "login" =>
      for {
        loginReq <- req.as[LoginRequest]
        result   <- authService.login(loginReq)
        response <- result match {
          case Right(token) =>
            Ok(ApiResponse.success("Login berhasil", Map("token" -> token)))
          case Left(err) =>
            Unauthorized(`WWW-Authenticate`(Challenge("Bearer", "blog-api")), ApiResponse.error[String](err))
        }
      } yield response
  }
}
