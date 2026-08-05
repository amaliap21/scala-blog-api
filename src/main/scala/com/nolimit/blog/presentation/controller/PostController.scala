package com.nolimit.blog.presentation.controller

import cats.effect.IO
import com.nolimit.blog.application.PostService
import com.nolimit.blog.presentation.dto.{ApiResponse, PostRequest}
import com.nolimit.blog.presentation.dto.Requests._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.io._
import io.circe.generic.auto._
import java.util.UUID

class PostController(postService: PostService) {

  implicit val postReqEntityDecoder: EntityDecoder[IO, PostRequest] = jsonOf[IO, PostRequest]

  val publicRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {

    case GET -> Root / "posts" =>
      for {
        posts    <- postService.getAllPosts
        response <- Ok(ApiResponse.success("Berhasil mengambil semua post", posts))
      } yield response

    case GET -> Root / "posts" / UUIDVar(id) =>
      for {
        maybePost <- postService.getPostById(id)
        response  <- maybePost match {
          case Some(post) => Ok(ApiResponse.success("Post ditemukan", post))
          case None       => NotFound(ApiResponse.error[String]("Post tidak ditemukan"))
        }
      } yield response
  }

  val protectedRoutes: AuthedRoutes[UUID, IO] = AuthedRoutes.of[UUID, IO] {

    case req @ POST -> Root / "posts" as userId =>
      for {
        postReq <- req.req.as[PostRequest]
        result  <- postService.createPost(postReq, userId)
        response <- result match {
          case Right(post) => Created(ApiResponse.success("Post berhasil dibuat", post))
          case Left(err)   => BadRequest(ApiResponse.error[String](err))
        }
      } yield response

    case req @ PUT -> Root / "posts" / UUIDVar(id) as userId =>
      for {
        postReq <- req.req.as[PostRequest]
        result  <- postService.updatePost(id, postReq, userId)
        response <- result match {
          case Right(post) => Ok(ApiResponse.success("Post berhasil diperbarui", post))
          case Left(err) if err.contains("akses") => Forbidden(ApiResponse.error[String](err))
          case Left(err) if err.contains("ditemukan") => NotFound(ApiResponse.error[String](err))
          case Left(err)   => BadRequest(ApiResponse.error[String](err))
        }
      } yield response

    case DELETE -> Root / "posts" / UUIDVar(id) as userId =>
      for {
        result <- postService.deletePost(id, userId)
        response <- result match {
          case Right(_)  => Ok(ApiResponse.success[String]("Post berhasil dihapus"))
          case Left(err) if err.contains("akses") => Forbidden(ApiResponse.error[String](err))
          case Left(err) if err.contains("ditemukan") => NotFound(ApiResponse.error[String](err))
          case Left(err) => BadRequest(ApiResponse.error[String](err))
        }
      } yield response
  }
}