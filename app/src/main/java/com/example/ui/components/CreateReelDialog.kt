package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkCard
import com.example.ui.theme.TokBookPink

data class ReelTheme(
  val name: String,
  val fromHex: Long,
  val toHex: Long
)

private val REEL_THEMES = listOf(
  ReelTheme("Neon", 0xFFFE2C55, 0xFF25F4EE),
  ReelTheme("Sunset", 0xFFF59E0B, 0xFF9333EA),
  ReelTheme("Cyber", 0xFF06B6D4, 0xFF3B82F6),
  ReelTheme("Aurora", 0xFF10B981, 0xFF047857)
)

@Composable
fun CreateReelDialog(
  onDismiss: () -> Unit,
  onPublish: (caption: String, musicTitle: String, colorFrom: Long, colorTo: Long) -> Unit
) {
  var caption by remember { mutableStateOf("") }
  var musicTitle by remember { mutableStateOf("") }
  var selectedTheme by remember { mutableStateOf(REEL_THEMES[0]) }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = DarkBg,
      tonalElevation = 6.dp,
      modifier = Modifier
        .fillMaxWidth()
        .testTag("create_reel_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(TokBookPink),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.PlayArrow, contentDescription = "Reel", tint = Color.White)
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Create Vertical Reel",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
            Text(
              text = "Full-screen video clip with music",
              fontSize = 11.sp,
              color = Color.Gray
            )
          }
          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("close_create_reel_button")
          ) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Caption
        OutlinedTextField(
          value = caption,
          onValueChange = { caption = it },
          placeholder = { Text("Caption & hashtags (e.g. Sunset vibes #travel #explore)", fontSize = 12.sp, color = Color.Gray) },
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = DarkCard,
            unfocusedContainerColor = DarkCard,
            focusedBorderColor = TokBookPink,
            unfocusedBorderColor = Color(0xFF2E3248),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
          ),
          modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .testTag("create_reel_caption_field"),
          shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Music Sound
        OutlinedTextField(
          value = musicTitle,
          onValueChange = { musicTitle = it },
          placeholder = { Text("Original Audio / Sound Track (e.g. Chill Beats)", fontSize = 12.sp, color = Color.Gray) },
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = DarkCard,
            unfocusedContainerColor = DarkCard,
            focusedBorderColor = TokBookPink,
            unfocusedBorderColor = Color(0xFF2E3248),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
          ),
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Theme visualizer selector
        Text(
          text = "Select Visual Mood Theme:",
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold,
          color = Color.LightGray
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          REEL_THEMES.forEach { theme ->
            val isSelected = selectedTheme.name == theme.name
            Box(
              modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                  Brush.linearGradient(
                    colors = listOf(Color(theme.fromHex), Color(theme.toHex))
                  )
                )
                .border(
                  width = if (isSelected) 2.5.dp else 0.dp,
                  color = if (isSelected) Color.White else Color.Transparent,
                  shape = RoundedCornerShape(12.dp)
                )
                .clickable { selectedTheme = theme },
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = theme.name,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = Color.White
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Publish Button
        Button(
          onClick = {
            onPublish(
              caption,
              musicTitle,
              selectedTheme.fromHex,
              selectedTheme.toHex
            )
            onDismiss()
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .testTag("publish_reel_button"),
          colors = ButtonDefaults.buttonColors(containerColor = TokBookPink),
          shape = RoundedCornerShape(14.dp)
        ) {
          Text(
            text = "Publish Vertical Reel",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Color.White
          )
        }
      }
    }
  }
}
