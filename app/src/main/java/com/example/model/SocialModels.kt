package com.example.model

data class Post(
  val id: String,
  val authorName: String,
  val authorHandle: String,
  val authorAvatar: String,
  val content: String,
  val imageUrl: String? = null,
  val timestamp: String,
  val category: String = "General",
  val feeling: String? = null,
  val likesCount: Int = 0,
  val isLiked: Boolean = false,
  val reactionType: String? = null,
  val commentsCount: Int = 0,
  val sharesCount: Int = 0,
  val isUserPost: Boolean = false
)

data class Reel(
  val id: String,
  val creatorName: String,
  val creatorHandle: String,
  val creatorAvatar: String,
  val caption: String,
  val musicTitle: String,
  val likesCount: Int = 0,
  val isLiked: Boolean = false,
  val commentsCount: Int = 0,
  val sharesCount: Int = 0,
  val colorFromHex: Long = 0xFFFE2C55,
  val colorToHex: Long = 0xFF25F4EE,
  val isUserReel: Boolean = false
)

data class Comment(
  val id: String,
  val targetId: String, // postId or reelId
  val authorName: String,
  val authorAvatar: String,
  val text: String,
  val timestamp: String,
  val likesCount: Int = 0,
  val isLiked: Boolean = false
)

data class Story(
  val id: String,
  val userName: String,
  val userAvatar: String,
  val imageUrl: String,
  val isViewed: Boolean = false
)
