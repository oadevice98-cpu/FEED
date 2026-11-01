package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.Reel
import com.example.ui.theme.TokBookCyan
import com.example.ui.theme.TokBookPink
import kotlinx.coroutines.launch
import kotlin.math.sin

@Composable
fun ReelsScreen(
  reels: List<Reel>,
  onReelLike: (reelId: String) -> Unit,
  onCommentClick: (reelId: String) -> Unit
) {
  if (reels.isEmpty()) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color.Black),
      contentAlignment = Alignment.Center
    ) {
      Text(text = "No reels available.", color = Color.White)
    }
    return
  }

  val pagerState = rememberPagerState(pageCount = { reels.size })

  VerticalPager(
    state = pagerState,
    modifier = Modifier
      .fillMaxSize()
      .background(Color.Black)
      .testTag("reels_vertical_pager")
  ) { page ->
    val reel = reels[page]
    ReelItem(
      reel = reel,
      pageIndex = page,
      totalPages = reels.size,
      onLike = { onReelLike(reel.id) },
      onComment = { onCommentClick(reel.id) }
    )
  }
}

@Composable
fun ReelItem(
  reel: Reel,
  pageIndex: Int,
  totalPages: Int,
  onLike: () -> Unit,
  onComment: () -> Unit
) {
  var isBookmarked by remember { mutableStateOf(false) }
  var showHeartBurst by remember { mutableStateOf(false) }
  val scope = rememberCoroutineScope()

  // Vinyl rotation animation
  val rotation = remember { Animatable(0f) }
  LaunchedEffect(Unit) {
    rotation.animateTo(
      targetValue = 360f,
      animationSpec = infiniteRepeatable(
        animation = tween(durationMillis = 6000, easing = LinearEasing),
        repeatMode = RepeatMode.Restart
      )
    )
  }

  // Visualizer soundwave wave offset
  var phase by remember { mutableFloatStateOf(0f) }
  LaunchedEffect(Unit) {
    while (true) {
      phase += 0.1f
      kotlinx.coroutines.delay(30)
    }
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .pointerInput(reel.id) {
        detectTapGestures(
          onDoubleTap = {
            if (!reel.isLiked) {
              onLike()
            }
            showHeartBurst = true
            scope.launch {
              kotlinx.coroutines.delay(800)
              showHeartBurst = false
            }
          }
        )
      }
      .testTag("reel_page_${reel.id}")
  ) {
    // 1. Dynamic Video Backdrop Simulation with Animated Gradient & Waves
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          Brush.linearGradient(
            colors = listOf(
              Color(reel.colorFromHex),
              Color(0xFF0D0E15),
              Color(reel.colorToHex)
            )
          )
        )
    )

    // Animated Ambient Canvas Waveforms
    Canvas(modifier = Modifier.fillMaxSize()) {
      val canvasWidth = size.width
      val canvasHeight = size.height

      // Center glowing ambient circular orb
      drawCircle(
        color = Color.White.copy(alpha = 0.06f),
        radius = 280f,
        center = Offset(canvasWidth / 2, canvasHeight / 2)
      )

      // Sound wave bars
      val barCount = 24
      val barWidth = canvasWidth / (barCount * 2)
      for (i in 0 until barCount) {
        val waveH = (sin(phase + i * 0.4f) * 60f + 80f).coerceAtLeast(15f)
        val x = canvasWidth / 2 - (barCount * barWidth) / 2 + (i * barWidth * 2)
        drawLine(
          color = Color.White.copy(alpha = 0.35f),
          start = Offset(x, canvasHeight / 2 - waveH / 2),
          end = Offset(x, canvasHeight / 2 + waveH / 2),
          strokeWidth = barWidth * 0.8f
        )
      }
    }

    // Vignette gradients for readable text
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(180.dp)
        .align(Alignment.TopCenter)
        .background(
          Brush.verticalGradient(
            colors = listOf(Color.Black.copy(alpha = 0.7f), Color.Transparent)
          )
        )
    )
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(280.dp)
        .align(Alignment.BottomCenter)
        .background(
          Brush.verticalGradient(
            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
          )
        )
    )

    // 2. Top Status Bar (Reel Index + Live Music Pill)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .statusBarsPadding()
        .padding(horizontal = 16.dp, vertical = 10.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
          .padding(horizontal = 12.dp, vertical = 4.dp)
      ) {
        Text(
          text = "Reel ${pageIndex + 1}/$totalPages",
          color = Color.White,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold
        )
      }

      Box(
        modifier = Modifier
          .background(TokBookPink, RoundedCornerShape(16.dp))
          .padding(horizontal = 10.dp, vertical = 4.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.PlayArrow, contentDescription = "Live", tint = Color.White, modifier = Modifier.size(12.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "LIVE SOUND",
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }

    // 3. Heart Burst Animation on Double Tap
    if (showHeartBurst) {
      Icon(
        imageVector = Icons.Default.Favorite,
        contentDescription = "Heart Burst",
        tint = TokBookPink,
        modifier = Modifier
          .size(110.dp)
          .align(Alignment.Center)
      )
    }

    // 4. Bottom Information & Right Action Rail
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .align(Alignment.BottomCenter)
        .padding(horizontal = 16.dp, vertical = 24.dp)
        .padding(bottom = 60.dp), // accommodate bottom navigation
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.Bottom
    ) {
      // Left: Creator, Caption, Sound
      Column(
        modifier = Modifier
          .weight(1f)
          .padding(end = 16.dp)
      ) {
        // Creator row
        Row(verticalAlignment = Alignment.CenterVertically) {
          AsyncImage(
            model = reel.creatorAvatar,
            contentDescription = "${reel.creatorName} avatar",
            modifier = Modifier
              .size(38.dp)
              .clip(CircleShape)
              .border(1.5.dp, TokBookPink, CircleShape),
            contentScale = ContentScale.Crop
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = reel.creatorName,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )
          Spacer(modifier = Modifier.width(8.dp))
          Box(
            modifier = Modifier
              .background(TokBookPink, RoundedCornerShape(12.dp))
              .clickable { /* follow */ }
              .padding(horizontal = 8.dp, vertical = 3.dp)
          ) {
            Text(
              text = "Follow",
              color = Color.White,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Caption
        Text(
          text = reel.caption,
          color = Color.White,
          fontSize = 13.sp,
          lineHeight = 18.sp,
          maxLines = 3
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Music Sound Row
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Icon(
            imageVector = Icons.Default.MusicNote,
            contentDescription = "Music",
            tint = TokBookCyan,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = reel.musicTitle,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
          )
        }
      }

      // Right Action Rail (TikTok Style)
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        // Avatar with follow +
        Box(
          modifier = Modifier.size(46.dp),
          contentAlignment = Alignment.Center
        ) {
          AsyncImage(
            model = reel.creatorAvatar,
            contentDescription = "Creator",
            modifier = Modifier
              .size(44.dp)
              .clip(CircleShape)
              .border(2.dp, Color.White, CircleShape),
            contentScale = ContentScale.Crop
          )
          Box(
            modifier = Modifier
              .size(16.dp)
              .clip(CircleShape)
              .background(TokBookPink)
              .align(Alignment.BottomCenter),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Add, contentDescription = "Follow", tint = Color.White, modifier = Modifier.size(12.dp))
          }
        }

        // Like Button
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.clickable { onLike() }
        ) {
          Box(
            modifier = Modifier
              .size(44.dp)
              .background(Color.Black.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Favorite,
              contentDescription = "Like Reel",
              tint = if (reel.isLiked) TokBookPink else Color.White,
              modifier = Modifier.size(24.dp)
            )
          }
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = formatCount(reel.likesCount),
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }

        // Comment Button
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.clickable { onComment() }
        ) {
          Box(
            modifier = Modifier
              .size(44.dp)
              .background(Color.Black.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Outlined.ChatBubbleOutline,
              contentDescription = "Comment Reel",
              tint = Color.White,
              modifier = Modifier.size(22.dp)
            )
          }
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = formatCount(reel.commentsCount),
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }

        // Bookmark Button
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.clickable { isBookmarked = !isBookmarked }
        ) {
          Box(
            modifier = Modifier
              .size(44.dp)
              .background(Color.Black.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
              contentDescription = "Bookmark",
              tint = if (isBookmarked) Color(0xFFF59E0B) else Color.White,
              modifier = Modifier.size(22.dp)
            )
          }
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "Save",
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }

        // Share Button
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.clickable { /* Share handled */ }
        ) {
          Box(
            modifier = Modifier
              .size(44.dp)
              .background(Color.Black.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Share,
              contentDescription = "Share Reel",
              tint = Color.White,
              modifier = Modifier.size(22.dp)
            )
          }
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "Share",
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }

        // Rotating Vinyl Music Disc
        Box(
          modifier = Modifier
            .size(42.dp)
            .rotate(rotation.value)
            .clip(CircleShape)
            .background(Color.DarkGray)
            .border(2.dp, Color.Gray, CircleShape)
            .padding(4.dp),
          contentAlignment = Alignment.Center
        ) {
          AsyncImage(
            model = reel.creatorAvatar,
            contentDescription = "Vinyl",
            modifier = Modifier
              .fillMaxSize()
              .clip(CircleShape),
            contentScale = ContentScale.Crop
          )
        }
      }
    }
  }
}

private fun formatCount(count: Int): String {
  return when {
    count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000.0)
    count >= 1_000 -> String.format("%.1fK", count / 1_000.0)
    else -> count.toString()
  }
}
