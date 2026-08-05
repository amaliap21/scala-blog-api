package com.nolimit.blog

import cats.effect.{IO, IOApp, ExitCode}
import cats.syntax.semigroupk._
import com.nolimit.blog.application.{AuthService, PostService}
import com.nolimit.blog.infrastructure.repository.{Database, DoobiePostRepository, DoobieUserRepository}
import com.nolimit.blog.presentation.controller.{ApiPlaygroundController, AuthController, JwtAuthMiddleware, PostController}
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import com.comcast.ip4s._

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    Database.makeTransactor.use { xa =>
      val userRepo = new DoobieUserRepository(xa)
      val postRepo = new DoobiePostRepository(xa)

      val authService = new AuthService(userRepo)
      val postService = new PostService(postRepo)

      val authController = new AuthController(authService)
      val postController = new PostController(postService)
      val playgroundController = new ApiPlaygroundController

      val httpApp = (
        playgroundController.routes <+>
          authController.routes <+>
          postController.publicRoutes <+>
          JwtAuthMiddleware.middleware(postController.protectedRoutes)
      ).orNotFound

      EmberServerBuilder
        .default[IO]
        .withHost(ipv4"0.0.0.0")
        .withPort(port"8080")
        .withHttpApp(httpApp)
        .build
        .use(_ => IO.never)
        .as(ExitCode.Success)
    }
  }
}
