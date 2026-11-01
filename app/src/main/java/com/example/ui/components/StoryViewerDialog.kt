package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.model.Story

@Composable
fun StoryViewerDialog(
  story: Story,
  onDismiss: () -> Unit
) {
  val progress = remember { Animatable(0f) }

  LaunchedEffect(story) {
    progress.snapTo(0f)
    progress.animateTo(
      targetValue = 1f,
      animationSpec = tween(durationMillis = 4000, easing = LinearEasing)
    )
    onDismiss()
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)
        .testTag("story_viewer_dialog")
    ) {
      // Main Story Image
      AsyncImage(
        model = story.imageUrl,
        contentDescription = "Story from ${story.userName}",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Fit
      )

      // Header Overlays
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .statusBarsPadding()
          .padding(16.dp)
      ) {
        // Progress Bar
        LinearProgressIndicator(
          progress = { progress.value },
          modifier = Modifier
            .fillMaxWidth()
            .height(3.dp)
            .clip(RoundedCornerShape(2.dp)),
          color = Color.White,
          trackColor = Color.White.copy(alpha = 0.3f),
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Author Details
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          AsyncImage(
            model = story.userAvatar,
            contentDescription = "${story.userName} avatar",
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape),
            contentScale = ContentScale.Crop
          )
          Spacer(modifier = Modifier.width(10.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = story.userName,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = Color.White
            )
            Text(
              text = "Just now",
              fontSize = 10.sp,
              color = Color.LightGray
            )
          }
          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("close_story_button")
          ) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
          }
        }
      }

      // Tap to dismiss
      Box(
        modifier = Modifier
          .fillMaxSize()
          .clickable { onDismiss() }
      )
    }
  }
}
