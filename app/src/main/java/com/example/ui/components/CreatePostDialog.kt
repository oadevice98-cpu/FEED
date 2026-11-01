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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkCard
import com.example.ui.theme.TokBookBlue

private val PRESET_PHOTOS = listOf(
  "https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=800&q=80",
  "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=800&q=80",
  "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?auto=format&fit=crop&w=800&q=80",
  "https://images.unsplash.com/photo-1470071459604-3b5ec3a7fe05?auto=format&fit=crop&w=800&q=80",
  "https://images.unsplash.com/photo-1533105079780-92b9be482077?auto=format&fit=crop&w=800&q=80"
)

private val CATEGORIES = listOf("General", "Travel", "Tech", "Foodie", "Lifestyle")

@Composable
fun CreatePostDialog(
  onDismiss: () -> Unit,
  onPublish: (content: String, imageUrl: String?, category: String, feeling: String?) -> Unit
) {
  var content by remember { mutableStateOf("") }
  var selectedImage by remember { mutableStateOf<String?>(null) }
  var selectedCategory by remember { mutableStateOf("General") }
  var feeling by remember { mutableStateOf("") }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = DarkBg,
      tonalElevation = 6.dp,
      modifier = Modifier
        .fillMaxWidth()
        .testTag("create_post_dialog")
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
          AsyncImage(
            model = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=150&q=80",
            contentDescription = "My avatar",
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape),
            contentScale = ContentScale.Crop
          )
          Spacer(modifier = Modifier.width(10.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Alex Mercer",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
            Text(
              text = "Public · TokBook Timeline",
              fontSize = 11.sp,
              color = Color.Gray
            )
          }
          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("close_create_post_button")
          ) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Content field
        OutlinedTextField(
          value = content,
          onValueChange = { content = it },
          placeholder = { Text("What's on your mind? Share text, ideas or photos...", fontSize = 13.sp, color = Color.Gray) },
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = DarkCard,
            unfocusedContainerColor = DarkCard,
            focusedBorderColor = TokBookBlue,
            unfocusedBorderColor = Color(0xFF2E3248),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
          ),
          modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .testTag("create_post_text_field"),
          shape = RoundedCornerShape(16.dp)
        )

        // Image Preview if selected
        if (selectedImage != null) {
          Spacer(modifier = Modifier.height(10.dp))
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(120.dp)
              .clip(RoundedCornerShape(12.dp))
          ) {
            AsyncImage(
              model = selectedImage,
              contentDescription = "Selected photo preview",
              modifier = Modifier.fillMaxWidth(),
              contentScale = ContentScale.Crop
            )
            IconButton(
              onClick = { selectedImage = null },
              modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .size(28.dp)
                .background(Color.Black.copy(alpha = 0.7f), CircleShape)
            ) {
              Icon(Icons.Default.Close, contentDescription = "Remove photo", tint = Color.White, modifier = Modifier.size(16.dp))
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Preset photo choices
        Text(
          text = "Attach photo from gallery:",
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold,
          color = Color.LightGray
        )
        Spacer(modifier = Modifier.height(6.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          items(PRESET_PHOTOS) { photoUrl ->
            Box(
              modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(
                  width = if (selectedImage == photoUrl) 2.dp else 1.dp,
                  color = if (selectedImage == photoUrl) TokBookBlue else Color.Transparent,
                  shape = RoundedCornerShape(8.dp)
                )
                .clickable {
                  selectedImage = if (selectedImage == photoUrl) null else photoUrl
                }
            ) {
              AsyncImage(
                model = photoUrl,
                contentDescription = "Preset thumbnail",
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Crop
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Categories
        Text(
          text = "Category:",
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold,
          color = Color.LightGray
        )
        Spacer(modifier = Modifier.height(4.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          items(CATEGORIES) { cat ->
            FilterChip(
              selected = selectedCategory == cat,
              onClick = { selectedCategory = cat },
              label = { Text(cat, fontSize = 11.sp) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = TokBookBlue,
                selectedLabelColor = Color.White,
                containerColor = DarkCard,
                labelColor = Color.Gray
              )
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Feeling input
        OutlinedTextField(
          value = feeling,
          onValueChange = { feeling = it },
          placeholder = { Text("Feeling / Activity (e.g. Blessed ✨, Exploring ✈️)", fontSize = 11.sp, color = Color.Gray) },
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = DarkCard,
            unfocusedContainerColor = DarkCard,
            focusedBorderColor = TokBookBlue,
            unfocusedBorderColor = Color(0xFF2E3248),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
          ),
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Publish Button
        Button(
          onClick = {
            if (content.isNotBlank() || selectedImage != null) {
              onPublish(
                content,
                selectedImage,
                selectedCategory,
                feeling.takeIf { it.isNotBlank() }
              )
              onDismiss()
            }
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .testTag("publish_post_button"),
          colors = ButtonDefaults.buttonColors(containerColor = TokBookBlue),
          shape = RoundedCornerShape(14.dp)
        ) {
          Text(
            text = "Post to Timeline",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Color.White
          )
        }
      }
    }
  }
}
