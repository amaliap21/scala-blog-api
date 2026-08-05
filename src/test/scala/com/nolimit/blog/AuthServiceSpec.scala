package com.nolimit.blog

import cats.effect.unsafe.implicits.global
import com.nolimit.blog.application.AuthService
import com.nolimit.blog.presentation.dto.{LoginRequest, RegisterRequest}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class AuthServiceSpec extends AnyFlatSpec with Matchers {

  "AuthService" should "berhasil meregistrasikan user baru" in {
    val repo = new MockUserRepository()
    val authService = new AuthService(repo)

    val req = RegisterRequest("Test User", "test@example.com", "password123")
    val result = authService.register(req).unsafeRunSync()

    result.isRight shouldBe true
    result.map(_.email) shouldBe Right("test@example.com")
  }

  it should "menolak registrasi jika format email tidak valid" in {
    val repo = new MockUserRepository()
    val authService = new AuthService(repo)

    val req = RegisterRequest("Test User", "invalid-email", "password123")
    val result = authService.register(req).unsafeRunSync()

    result shouldBe Left("Format email tidak valid")
  }

  it should "berhasil login dan mengembalikan JWT token" in {
    val repo = new MockUserRepository()
    val authService = new AuthService(repo)

    val regReq = RegisterRequest("Test User", "login@example.com", "password123")
    authService.register(regReq).unsafeRunSync()

    val loginReq = LoginRequest("login@example.com", "password123")
    val result = authService.login(loginReq).unsafeRunSync()

    result.isRight shouldBe true
  }
}