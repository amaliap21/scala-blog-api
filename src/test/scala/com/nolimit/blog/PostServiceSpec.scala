package com.nolimit.blog

import cats.effect.unsafe.implicits.global
import com.nolimit.blog.application.PostService
import com.nolimit.blog.presentation.dto.PostRequest
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import java.util.UUID

class PostServiceSpec extends AnyFlatSpec with Matchers {

  "PostService" should "mencegah user lain mengubah post milik user berbeda" in {
    val repo = new MockPostRepository()
    val postService = new PostService(repo)

    val ownerId = UUID.randomUUID()
    val otherUserId = UUID.randomUUID()

    // 1. Buat post oleh owner
    val createReq = PostRequest("Konten Asli")
    val createdPost = postService.createPost(createReq, ownerId).unsafeRunSync().toOption.get

    // 2. Coba ubah post menggunakan ID user lain
    val updateReq = PostRequest("Konten Edit")
    val updateResult = postService.updatePost(createdPost.id, updateReq, otherUserId).unsafeRunSync()

    updateResult shouldBe Left("Anda tidak memiliki akses untuk mengubah post ini")
  }
}