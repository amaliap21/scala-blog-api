package com.nolimit.blog.application

import cats.effect.IO
import com.nolimit.blog.domain.model.Post
import com.nolimit.blog.domain.repository.PostRepository
import com.nolimit.blog.presentation.dto.PostRequest
import java.time.Instant
import java.util.UUID

class PostService(postRepo: PostRepository[IO]) {

  def getAllPosts: IO[List[Post]] = postRepo.findAll()

  def getPostById(id: UUID): IO[Option[Post]] = postRepo.findById(id)

  def createPost(req: PostRequest, authorId: UUID): IO[Either[String, Post]] = {
    if (req.content.trim.isEmpty) {
      IO.pure(Left("Konten post tidak boleh kosong"))
    } else {
      val now = Instant.now()
      val post = Post(
        id = UUID.randomUUID(),
        content = req.content,
        createdAt = now,
        updatedAt = now,
        authorId = authorId
      )
      postRepo.create(post).map(Right(_))
    }
  }

  def updatePost(id: UUID, req: PostRequest, authorId: UUID): IO[Either[String, Post]] = {
    if (req.content.trim.isEmpty) {
      IO.pure(Left("Konten post tidak boleh kosong"))
    } else {
      postRepo.findById(id).flatMap {
        case None => IO.pure(Left("Post tidak ditemukan"))
        case Some(existingPost) if existingPost.authorId != authorId =>
          IO.pure(Left("Anda tidak memiliki akses untuk mengubah post ini"))
        case Some(existingPost) =>
          val updatedPost = existingPost.copy(
            content = req.content,
            updatedAt = Instant.now()
          )
          postRepo.update(updatedPost).map {
            case Some(p) => Right(p)
            case None    => Left("Gagal memperbarui post")
          }
      }
    }
  }

  def deletePost(id: UUID, authorId: UUID): IO[Either[String, Unit]] = {
    postRepo.findById(id).flatMap {
      case None => IO.pure(Left("Post tidak ditemukan"))
      case Some(existingPost) if existingPost.authorId != authorId =>
        IO.pure(Left("Anda tidak memiliki akses untuk menghapus post ini"))
      case Some(_) =>
        postRepo.delete(id).map {
          case true  => Right(())
          case false => Left("Gagal menghapus post")
        }
    }
  }
}