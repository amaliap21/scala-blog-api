package com.nolimit.blog.domain.model

import java.time.Instant
import java.util.UUID

// Entitas User
final case class User(
  id: UUID,
  name: String,
  email: String,
  password: String
)

// Entitas Post
final case class Post(
  id: UUID,
  content: String,
  createdAt: Instant,
  updatedAt: Instant,
  authorId: UUID
)