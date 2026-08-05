package com.nolimit.blog

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import cats.syntax.semigroupk._
import com.nolimit.blog.application.{AuthService, PostService}
import com.nolimit.blog.presentation.controller.{AuthController, JwtAuthMiddleware, PostController}
import io.circe.Json
import io.circe.parser.parse
import org.http4s.{Method, Request, Status}
import org.http4s.circe._
import org.http4s.implicits._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import fs2.text

class RoutesSpec extends AnyFlatSpec with Matchers {

  "Public registration route" should "not require a JWT token" in {
    val authController = new AuthController(new AuthService(new MockUserRepository()))
    val postController = new PostController(new PostService(new MockPostRepository()))
    val httpApp = (
      authController.routes <+>
        postController.publicRoutes <+>
        JwtAuthMiddleware.middleware(postController.protectedRoutes)
    ).orNotFound

    val request = Request[IO](Method.POST, uri"/register").withEntity(
      Json.obj(
        "name" -> Json.fromString("Test User"),
        "email" -> Json.fromString("test@example.com"),
        "password" -> Json.fromString("password123")
      )
    )

    val response = httpApp.run(request).unsafeRunSync()
    response.status shouldBe Status.Created

    val responseBody = response.body.through(text.utf8.decode).compile.string.unsafeRunSync()
    val json = parse(responseBody).fold(error => fail(error.getMessage), identity)
    json.hcursor.downField("data").downField("password").focus shouldBe None
  }

  it should "return a standard JSON response when a protected endpoint has no token" in {
    val postController = new PostController(new PostService(new MockPostRepository()))
    val httpApp = JwtAuthMiddleware.middleware(postController.protectedRoutes).orNotFound
    val request = Request[IO](Method.POST, uri"/posts").withEntity(
      Json.obj("content" -> Json.fromString("A protected post"))
    )

    val response = httpApp.run(request).unsafeRunSync()
    response.status shouldBe Status.Unauthorized

    val responseBody = response.body.through(text.utf8.decode).compile.string.unsafeRunSync()
    val json = parse(responseBody).fold(error => fail(error.getMessage), identity)
    json.hcursor.downField("status").as[String] shouldBe Right("error")
    json.hcursor.downField("message").as[String] shouldBe Right("Token JWT tidak valid atau tidak ditemukan")
  }
}
