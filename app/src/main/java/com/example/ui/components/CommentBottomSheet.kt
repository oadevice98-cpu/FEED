package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import coil.compose.AsyncImage
import com.example.model.Comment
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkCard
import com.example.ui.theme.TokBookPink

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentBottomSheet(
  targetId: String,
  comments: List<Comment>,
  onDismiss: () -> Unit,
  onAddComment: (text: String) -> Unit,
  onLikeComment: (commentId: String) -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var textInput by remember { mutableStateOf("") }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = DarkBg,
    contentColor = Color.White,
    modifier = Modifier.testTag("comments_bottom_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .navigationBarsPadding()
        .padding(horizontal = 16.dp)
        .padding(bottom = 12.dp)
    ) {
      // Header
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Comments (${comments.size})",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = Color.White,
          modifier = Modifier.weight(1f)
        )
        IconButton(
          onClick = onDismiss,
          modifier = Modifier.testTag("close_comments_button")
        ) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close comments",
            tint = Color.Gray
          )
        }
      }

      // Comments List
      if (comments.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "No comments yet. Start the conversation! 💬",
            color = Color.Gray,
            fontSize = 13.sp
          )
        }
      } else {
        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
        ) {
          items(comments, key = { it.id }) { comment ->
            CommentItem(
              comment = comment,
              onLikeClick = { onLikeComment(comment.id) }
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Input row
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        AsyncImage(
          model = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=150&q=80",
          contentDescription = "User profile avatar",
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape),
          contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(8.dp))

        OutlinedTextField(
          value = textInput,
          onValueChange = { textInput = it },
          placeholder = { Text("Add a comment... ❤️", fontSize = 12.sp, color = Color.Gray) },
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = DarkCard,
            unfocusedContainerColor = DarkCard,
            focusedBorderColor = TokBookPink,
            unfocusedBorderColor = Color(0xFF2E3248),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
          ),
          shape = RoundedCornerShape(24.dp),
          modifier = Modifier
            .weight(1f)
            .testTag("comment_input_field"),
          singleLine = true
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
          onClick = {
            if (textInput.isNotBlank()) {
              onAddComment(textInput.trim())
              textInput = ""
            }
          },
          modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(TokBookPink)
            .testTag("send_comment_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.Send,
            contentDescription = "Send comment",
            tint = Color.White,
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }
  }
}

@Composable
fun CommentItem(
  comment: Comment,
  onLikeClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 6.dp),
    verticalAlignment = Alignment.Top
  ) {
    AsyncImage(
      model = comment.authorAvatar,
      contentDescription = "${comment.authorName} avatar",
      modifier = Modifier
        .size(32.dp)
        .clip(CircleShape),
      contentScale = ContentScale.Crop
    )

    Spacer(modifier = Modifier.width(10.dp))

    Column(
      modifier = Modifier
        .weight(1f)
        .background(DarkCard, RoundedCornerShape(14.dp))
        .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = comment.authorName,
          fontWeight = FontWeight.Bold,
          fontSize = 12.sp,
          color = Color.White
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = comment.timestamp,
          fontSize = 10.sp,
          color = Color.Gray
        )
      }
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = comment.text,
        fontSize = 12.sp,
        color = Color(0xFFE2E8F0)
      )
    }

    Spacer(modifier = Modifier.width(8.dp))

    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier
        .clickable { onLikeClick() }
        .padding(4.dp)
    ) {
      Icon(
        imageVector = if (comment.isLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
        contentDescription = "Like comment",
        tint = if (comment.isLiked) TokBookPink else Color.Gray,
        modifier = Modifier.size(16.dp)
      )
      if (comment.likesCount > 0) {
        Text(
          text = "${comment.likesCount}",
          fontSize = 10.sp,
          color = Color.Gray
        )
      }
    }
  }
}
