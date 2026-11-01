package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.Post
import com.example.model.Story
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.TokBookBlue
import com.example.ui.theme.TokBookCyan
import com.example.ui.theme.TokBookPink

@Composable
fun FeedScreen(
  posts: List<Post>,
  stories: List<Story>,
  activeFilter: String,
  onFilterSelect: (String) -> Unit,
  onPostLike: (postId: String) -> Unit,
  onCommentClick: (postId: String) -> Unit,
  onDeletePost: (postId: String) -> Unit,
  onOpenCreatePost: () -> Unit,
  onStoryClick: (Story) -> Unit
) {
  val filters = listOf("All", "🔥 Trending", "📸 Photos", "👤 My Posts")

  val filteredPosts = when (activeFilter) {
    "📸 Photos" -> posts.filter { it.imageUrl != null }
    "🔥 Trending" -> posts.sortedByDescending { it.likesCount + it.commentsCount }
    "👤 My Posts" -> posts.filter { it.isUserPost }
    else -> posts
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(DarkBg)
      .testTag("feed_screen_list"),
    contentPadding = PaddingValues(bottom = 90.dp)
  ) {
    // 1. Stories Tray
    item {
      StoriesTray(
        stories = stories,
        onAddStoryClick = onOpenCreatePost,
        onStoryClick = onStoryClick
      )
    }

    // 2. What's on your mind? Composer Card
    item {
      ComposerCard(onOpenCreatePost = onOpenCreatePost)
    }

    // 3. Filter Chips
    item {
      LazyRow(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(filters) { filter ->
          val isSelected = activeFilter == filter
          FilterChip(
            selected = isSelected,
            onClick = { onFilterSelect(filter) },
            label = { Text(filter, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = TokBookBlue,
              selectedLabelColor = Color.White,
              containerColor = DarkSurface,
              labelColor = Color.LightGray
            ),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.testTag("filter_chip_$filter")
          )
        }
      }
    }

    // 4. Posts Feed
    if (filteredPosts.isEmpty()) {
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "No posts found in this filter.",
            color = Color.Gray,
            fontSize = 14.sp
          )
        }
      }
    } else {
      items(filteredPosts, key = { it.id }) { post ->
        PostCard(
          post = post,
          onLike = { onPostLike(post.id) },
          onComment = { onCommentClick(post.id) },
          onDelete = { onDeletePost(post.id) }
        )
      }
    }
  }
}

@Composable
fun StoriesTray(
  stories: List<Story>,
  onAddStoryClick: () -> Unit,
  onStoryClick: (Story) -> Unit
) {
  LazyRow(
    modifier = Modifier
      .fillMaxWidth()
      .background(DarkSurface)
      .padding(vertical = 12.dp),
    contentPadding = PaddingValues(horizontal = 12.dp),
    horizontalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    // Add My Story
    item {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
          .clickable { onAddStoryClick() }
          .testTag("add_story_button")
      ) {
        Box(
          modifier = Modifier
            .size(68.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(DarkCard)
            .border(2.dp, Color(0xFF33384F), RoundedCornerShape(20.dp)),
          contentAlignment = Alignment.Center
        ) {
          AsyncImage(
            model = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=150&q=80",
            contentDescription = "My avatar",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.5f
          )
          Box(
            modifier = Modifier
              .size(28.dp)
              .clip(CircleShape)
              .background(TokBookBlue),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Add, contentDescription = "Add Story", tint = Color.White, modifier = Modifier.size(18.dp))
          }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Your Story",
          fontSize = 11.sp,
          color = Color.LightGray,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
    }

    // Stories list
    items(stories, key = { it.id }) { story ->
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
          .clickable { onStoryClick(story) }
          .testTag("story_item_${story.id}")
      ) {
        Box(
          modifier = Modifier
            .size(68.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
              Brush.linearGradient(
                colors = listOf(TokBookPink, TokBookBlue, TokBookCyan)
              )
            )
            .padding(2.5.dp)
        ) {
          AsyncImage(
            model = story.userAvatar,
            contentDescription = "${story.userName} story",
            modifier = Modifier
              .fillMaxSize()
              .clip(RoundedCornerShape(18.dp)),
            contentScale = ContentScale.Crop
          )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = story.userName,
          fontSize = 11.sp,
          color = Color.White,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
    }
  }
}

@Composable
fun ComposerCard(onOpenCreatePost: () -> Unit) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(12.dp)
      .clickable { onOpenCreatePost() }
      .testTag("composer_card"),
    colors = CardDefaults.cardColors(containerColor = DarkSurface),
    shape = RoundedCornerShape(18.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        AsyncImage(
          model = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=150&q=80",
          contentDescription = "My avatar",
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape),
          contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(10.dp))

        Box(
          modifier = Modifier
            .weight(1f)
            .height(38.dp)
            .background(DarkCard, RoundedCornerShape(20.dp))
            .padding(horizontal = 14.dp),
          contentAlignment = Alignment.CenterStart
        ) {
          Text(
            text = "What's on your mind, Alex?",
            fontSize = 12.sp,
            color = Color.Gray
          )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Icon(
          imageVector = Icons.Outlined.Image,
          contentDescription = "Attach image",
          tint = Color(0xFF10B981),
          modifier = Modifier.size(24.dp)
        )
      }
    }
  }
}

@Composable
fun PostCard(
  post: Post,
  onLike: () -> Unit,
  onComment: () -> Unit,
  onDelete: () -> Unit
) {
  val likeButtonColor by animateColorAsState(
    targetValue = if (post.isLiked) TokBookBlue else Color.Gray,
    animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
    label = "likeColor"
  )

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 12.dp, vertical = 6.dp)
      .testTag("post_card_${post.id}"),
    colors = CardDefaults.cardColors(containerColor = DarkSurface),
    shape = RoundedCornerShape(18.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        AsyncImage(
          model = post.authorAvatar,
          contentDescription = "${post.authorName} avatar",
          modifier = Modifier
            .size(42.dp)
            .clip(CircleShape),
          contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = post.authorName,
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = "Verified",
              tint = TokBookBlue,
              modifier = Modifier.size(14.dp)
            )
            if (post.feeling != null) {
              Text(
                text = " · ${post.feeling}",
                fontSize = 11.sp,
                color = Color.LightGray,
                maxLines = 1
              )
            }
          }
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = post.timestamp,
              fontSize = 11.sp,
              color = Color.Gray
            )
            Text(
              text = " · ",
              fontSize = 11.sp,
              color = Color.Gray
            )
            Text(
              text = post.category,
              fontSize = 11.sp,
              color = TokBookCyan,
              fontWeight = FontWeight.Medium
            )
          }
        }

        if (post.isUserPost) {
          IconButton(
            onClick = onDelete,
            modifier = Modifier.testTag("delete_post_${post.id}")
          ) {
            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray, modifier = Modifier.size(18.dp))
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Content text
      Text(
        text = post.content,
        style = MaterialTheme.typography.bodyMedium,
        color = Color(0xFFF1F5F9),
        lineHeight = 20.sp
      )

      // Post Media Image if present
      if (post.imageUrl != null) {
        Spacer(modifier = Modifier.height(10.dp))
        AsyncImage(
          model = post.imageUrl,
          contentDescription = "Post image",
          modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
            .clip(RoundedCornerShape(14.dp)),
          contentScale = ContentScale.Crop
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Social summary bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(text = "👍 ❤️ 🔥", fontSize = 12.sp)
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "${post.likesCount}",
            fontSize = 12.sp,
            color = Color.LightGray,
            fontWeight = FontWeight.SemiBold
          )
        }

        Text(
          text = "${post.commentsCount} comments · ${post.sharesCount} shares",
          fontSize = 11.sp,
          color = Color.Gray
        )
      }

      Spacer(modifier = Modifier.height(8.dp))
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(1.dp)
          .background(Color(0xFF2E3248))
      )
      Spacer(modifier = Modifier.height(4.dp))

      // Action Buttons: Like, Comment, Share
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
      ) {
        // Like Button
        Row(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onLike() }
            .padding(vertical = 8.dp)
            .testTag("like_button_${post.id}"),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = if (post.isLiked) Icons.Default.ThumbUp else Icons.Outlined.ThumbUp,
            contentDescription = "Like",
            tint = likeButtonColor,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (post.isLiked) "Liked" else "Like",
            color = likeButtonColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
          )
        }

        // Comment Button
        Row(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onComment() }
            .padding(vertical = 8.dp)
            .testTag("comment_button_${post.id}"),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Outlined.ChatBubbleOutline,
            contentDescription = "Comment",
            tint = Color.Gray,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Comment",
            color = Color.Gray,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
          )
        }

        // Share Button
        Row(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))
            .clickable { /* Share handled */ }
            .padding(vertical = 8.dp)
            .testTag("share_button_${post.id}"),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Share,
            contentDescription = "Share",
            tint = Color.Gray,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Share",
            color = Color.Gray,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
          )
        }
      }
    }
  }
}
