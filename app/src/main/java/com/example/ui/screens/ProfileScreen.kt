package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.Post
import com.example.model.Reel
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.TokBookBlue
import com.example.ui.theme.TokBookPink

@Composable
fun ProfileScreen(
  posts: List<Post>,
  reels: List<Reel>,
  onOpenCreatePost: () -> Unit,
  onOpenCreateReel: () -> Unit
) {
  val myPosts = posts.filter { it.isUserPost }
  val myReels = reels.filter { it.isUserReel }

  LazyVerticalGrid(
    columns = GridCells.Fixed(2),
    modifier = Modifier
      .fillMaxSize()
      .background(DarkBg)
      .testTag("profile_screen_grid"),
    contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 16.dp, bottom = 90.dp),
    horizontalArrangement = Arrangement.spacedBy(10.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    // Profile Header spanning full width
    item(span = { GridItemSpan(2) }) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(DarkSurface, RoundedCornerShape(20.dp))
          .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Avatar
        Box(contentAlignment = Alignment.BottomEnd) {
          AsyncImage(
            model = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=300&q=80",
            contentDescription = "Profile picture",
            modifier = Modifier
              .size(86.dp)
              .clip(CircleShape)
              .border(3.dp, TokBookPink, CircleShape),
            contentScale = ContentScale.Crop
          )
          Box(
            modifier = Modifier
              .size(24.dp)
              .clip(CircleShape)
              .background(TokBookBlue),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.CheckCircle, contentDescription = "Verified", tint = Color.White, modifier = Modifier.size(16.dp))
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = "Alex Mercer",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
        Text(
          text = "@alexmercer · TokBook Creator",
          fontSize = 12.sp,
          color = Color.Gray
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "Filmmaker & digital nomad exploring life between stories & reels 🎬✨",
          fontSize = 12.sp,
          color = Color(0xFFCBD5E1),
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Stats Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceEvenly
        ) {
          StatColumn(label = "Posts", value = "${myPosts.size + myReels.size}")
          StatColumn(label = "Followers", value = "14.8K")
          StatColumn(label = "Likes", value = "48.2K")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick action buttons
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Button(
            onClick = onOpenCreatePost,
            modifier = Modifier
              .weight(1f)
              .height(40.dp)
              .testTag("profile_new_post_button"),
            colors = ButtonDefaults.buttonColors(containerColor = TokBookBlue),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text("+ New Post", fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }

          Button(
            onClick = onOpenCreateReel,
            modifier = Modifier
              .weight(1f)
              .height(40.dp)
              .testTag("profile_new_reel_button"),
            colors = ButtonDefaults.buttonColors(containerColor = TokBookPink),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text("+ New Reel", fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    // Grid section header
    item(span = { GridItemSpan(2) }) {
      Text(
        text = "Your Published Content (${myPosts.size + myReels.size})",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        modifier = Modifier.padding(vertical = 8.dp)
      )
    }

    if (myPosts.isEmpty() && myReels.isEmpty()) {
      item(span = { GridItemSpan(2) }) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .background(DarkSurface, RoundedCornerShape(16.dp)),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "You haven't posted yet.\nTap '+ New Post' to share your first story!",
            color = Color.Gray,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
          )
        }
      }
    } else {
      // User posts
      items(myPosts, key = { it.id }) { post ->
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = DarkCard),
          modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
        ) {
          Box(modifier = Modifier.fillMaxSize()) {
            if (post.imageUrl != null) {
              AsyncImage(
                model = post.imageUrl,
                contentDescription = "Post photo",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
              )
            } else {
              Box(
                modifier = Modifier
                  .fillMaxSize()
                  .background(
                    Brush.linearGradient(
                      listOf(Color(0xFF1E3A8A), Color(0xFF111827))
                    )
                  )
                  .padding(12.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = post.content,
                  fontSize = 11.sp,
                  color = Color.White,
                  maxLines = 4
                )
              }
            }

            // Likes badge
            Row(
              modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(6.dp)
                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.Favorite, contentDescription = "Likes", tint = TokBookPink, modifier = Modifier.size(10.dp))
              Spacer(modifier = Modifier.width(3.dp))
              Text(text = "${post.likesCount}", fontSize = 10.sp, color = Color.White)
            }
          }
        }
      }

      // User reels
      items(myReels, key = { it.id }) { reel ->
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = DarkCard),
          modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
        ) {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(
                Brush.linearGradient(
                  listOf(Color(reel.colorFromHex), Color(reel.colorToHex))
                )
              )
              .padding(10.dp)
          ) {
            Icon(Icons.Default.PlayArrow, contentDescription = "Reel", tint = Color.White, modifier = Modifier.size(20.dp))
            Text(
              text = reel.caption,
              fontSize = 11.sp,
              color = Color.White,
              fontWeight = FontWeight.Bold,
              maxLines = 3,
              modifier = Modifier.align(Alignment.Center)
            )
            Row(
              modifier = Modifier
                .align(Alignment.BottomStart)
                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.Favorite, contentDescription = "Likes", tint = Color.White, modifier = Modifier.size(10.dp))
              Spacer(modifier = Modifier.width(3.dp))
              Text(text = "${reel.likesCount}", fontSize = 10.sp, color = Color.White)
            }
          }
        }
      }
    }
  }
}

@Composable
fun StatColumn(label: String, value: String) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(
      text = value,
      fontWeight = FontWeight.Bold,
      fontSize = 16.sp,
      color = Color.White
    )
    Text(
      text = label,
      fontSize = 11.sp,
      color = Color.Gray
    )
  }
}
