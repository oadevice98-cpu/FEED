package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.CommentBottomSheet
import com.example.ui.components.CreatePostDialog
import com.example.ui.components.CreateReelDialog
import com.example.ui.components.StoryViewerDialog
import com.example.ui.screens.FeedScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ReelsScreen
import com.example.ui.screens.WebViewScreen
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.TokBookBlue
import com.example.ui.theme.TokBookCyan
import com.example.ui.theme.TokBookPink
import kotlinx.coroutines.launch

@Composable
fun TokBookApp(viewModel: SocialViewModel) {
  val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
  val posts by viewModel.posts.collectAsStateWithLifecycle()
  val reels by viewModel.reels.collectAsStateWithLifecycle()
  val commentsMap by viewModel.comments.collectAsStateWithLifecycle()
  val activeFilter by viewModel.activeFilter.collectAsStateWithLifecycle()
  val selectedCommentTargetId by viewModel.selectedCommentTargetId.collectAsStateWithLifecycle()
  val isCreatePostOpen by viewModel.isCreatePostOpen.collectAsStateWithLifecycle()
  val isCreateReelOpen by viewModel.isCreateReelOpen.collectAsStateWithLifecycle()
  val activeStory by viewModel.activeStory.collectAsStateWithLifecycle()

  var showCreateChoice by remember { mutableStateOf(false) }
  val snackbarHostState = remember { SnackbarHostState() }
  val scope = rememberCoroutineScope()

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    containerColor = DarkBg,
    contentWindowInsets = WindowInsets(0, 0, 0, 0),
    snackbarHost = { SnackbarHost(snackbarHostState) },
    topBar = {
      // Show top bar on Feed and Profile tabs
      if (activeTab != AppTab.REELS) {
        TokBookTopBar(
          activeTab = activeTab,
          onTabSelect = { viewModel.setTab(it) },
          onAddClick = { showCreateChoice = true },
          onNotificationClick = {
            scope.launch {
              snackbarHostState.showSnackbar("You're all caught up on notifications!")
            }
          }
        )
      }
    },
    bottomBar = {
      TokBookBottomNav(
        activeTab = activeTab,
        onTabSelect = { viewModel.setTab(it) },
        onCenterAction = { showCreateChoice = true }
      )
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(
          top = if (activeTab != AppTab.REELS) innerPadding.calculateTopPadding() else 0.dp,
          bottom = innerPadding.calculateBottomPadding()
        )
    ) {
      when (activeTab) {
        AppTab.FEED -> {
          FeedScreen(
            posts = posts,
            stories = viewModel.stories,
            activeFilter = activeFilter,
            onFilterSelect = { viewModel.setFilter(it) },
            onPostLike = { viewModel.togglePostLike(it) },
            onCommentClick = { viewModel.openComments(it) },
            onDeletePost = {
              viewModel.deletePost(it)
              scope.launch { snackbarHostState.showSnackbar("Post deleted") }
            },
            onOpenCreatePost = { viewModel.openCreatePost(true) },
            onStoryClick = { viewModel.openStory(it) }
          )
        }
        AppTab.REELS -> {
          ReelsScreen(
            reels = reels,
            onReelLike = { viewModel.toggleReelLike(it) },
            onCommentClick = { viewModel.openComments(it) }
          )
        }
        AppTab.WEB_MODE -> {
          WebViewScreen()
        }
        AppTab.PROFILE -> {
          ProfileScreen(
            posts = posts,
            reels = reels,
            onOpenCreatePost = { viewModel.openCreatePost(true) },
            onOpenCreateReel = { viewModel.openCreateReel(true) }
          )
        }
        else -> {}
      }
    }
  }

  // DIALOGS & SHEETS

  // 1. Comments Sheet
  if (selectedCommentTargetId != null) {
    val targetId = selectedCommentTargetId!!
    val currentComments = commentsMap[targetId].orEmpty()
    CommentBottomSheet(
      targetId = targetId,
      comments = currentComments,
      onDismiss = { viewModel.closeComments() },
      onAddComment = { text ->
        viewModel.addComment(targetId, text)
        scope.launch { snackbarHostState.showSnackbar("Comment added! 💬") }
      },
      onLikeComment = { commentId ->
        viewModel.toggleCommentLike(targetId, commentId)
      }
    )
  }

  // 2. Create Choice Dialog
  if (showCreateChoice) {
    CreateChoiceDialog(
      onDismiss = { showCreateChoice = false },
      onCreatePost = {
        showCreateChoice = false
        viewModel.openCreatePost(true)
      },
      onCreateReel = {
        showCreateChoice = false
        viewModel.openCreateReel(true)
      }
    )
  }

  // 3. Create Post Dialog
  if (isCreatePostOpen) {
    CreatePostDialog(
      onDismiss = { viewModel.openCreatePost(false) },
      onPublish = { content, imageUrl, category, feeling ->
        viewModel.addPost(content, imageUrl, category, feeling)
        viewModel.setTab(AppTab.FEED)
        scope.launch { snackbarHostState.showSnackbar("Post published to Timeline! 🎉") }
      }
    )
  }

  // 4. Create Reel Dialog
  if (isCreateReelOpen) {
    CreateReelDialog(
      onDismiss = { viewModel.openCreateReel(false) },
      onPublish = { caption, musicTitle, colorFrom, colorTo ->
        viewModel.addReel(caption, musicTitle, colorFrom, colorTo)
        viewModel.setTab(AppTab.REELS)
        scope.launch { snackbarHostState.showSnackbar("Reel published to Reels feed! 🎥") }
      }
    )
  }

  // 5. Story Viewer
  if (activeStory != null) {
    StoryViewerDialog(
      story = activeStory!!,
      onDismiss = { viewModel.openStory(null) }
    )
  }
}

@Composable
fun TokBookTopBar(
  activeTab: AppTab,
  onTabSelect: (AppTab) -> Unit,
  onAddClick: () -> Unit,
  onNotificationClick: () -> Unit
) {
  Surface(
    color = DarkSurface,
    tonalElevation = 4.dp,
    modifier = Modifier
      .fillMaxWidth()
      .statusBarsPadding()
      .testTag("tokbook_top_bar")
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(56.dp)
        .padding(horizontal = 14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      // App Logo
      Row(verticalAlignment = Alignment.CenterVertically) {
        androidx.compose.foundation.Image(
          painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.app_logo),
          contentDescription = "App Logo",
          modifier = Modifier
            .size(34.dp)
            .clip(RoundedCornerShape(8.dp))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Feed",
          fontWeight = FontWeight.ExtraBold,
          fontSize = 19.sp,
          color = Color.White
        )
      }

      // Center Quick Pill Switch (Timeline vs Reels)
      Row(
        modifier = Modifier
          .background(DarkCard, RoundedCornerShape(20.dp))
          .padding(3.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (activeTab == AppTab.FEED) TokBookBlue else Color.Transparent)
            .clickable { onTabSelect(AppTab.FEED) }
            .padding(horizontal = 12.dp, vertical = 5.dp)
        ) {
          Text(
            text = "Feed",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (activeTab == AppTab.FEED) Color.White else Color.Gray
          )
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (activeTab == AppTab.REELS) TokBookPink else Color.Transparent)
            .clickable { onTabSelect(AppTab.REELS) }
            .padding(horizontal = 12.dp, vertical = 5.dp)
        ) {
          Text(
            text = "Reels",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (activeTab == AppTab.REELS) Color.White else Color.Gray
          )
        }
      }

      // Top Actions
      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
          onClick = onAddClick,
          modifier = Modifier
            .size(34.dp)
            .background(TokBookPink.copy(alpha = 0.15f), CircleShape)
            .testTag("top_add_button")
        ) {
          Icon(Icons.Default.Add, contentDescription = "Add Content", tint = TokBookPink, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
          onClick = onNotificationClick,
          modifier = Modifier
            .size(34.dp)
            .background(DarkCard, CircleShape)
            .testTag("top_notifications_button")
        ) {
          Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color.LightGray, modifier = Modifier.size(18.dp))
        }
      }
    }
  }
}

@Composable
fun TokBookBottomNav(
  activeTab: AppTab,
  onTabSelect: (AppTab) -> Unit,
  onCenterAction: () -> Unit
) {
  Surface(
    color = DarkSurface.copy(alpha = 0.98f),
    tonalElevation = 8.dp,
    modifier = Modifier
      .fillMaxWidth()
      .windowInsetsPadding(WindowInsets.navigationBars)
      .testTag("tokbook_bottom_navigation")
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(64.dp)
        .padding(horizontal = 8.dp),
      horizontalArrangement = Arrangement.SpaceAround,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Feed
      BottomNavItem(
        icon = if (activeTab == AppTab.FEED) Icons.Default.Home else Icons.Outlined.Home,
        label = "Feed",
        isSelected = activeTab == AppTab.FEED,
        activeColor = TokBookBlue,
        onClick = { onTabSelect(AppTab.FEED) },
        testTag = "nav_tab_feed"
      )

      // Reels
      BottomNavItem(
        icon = if (activeTab == AppTab.REELS) Icons.Default.VideoLibrary else Icons.Outlined.VideoLibrary,
        label = "Reels",
        isSelected = activeTab == AppTab.REELS,
        activeColor = TokBookPink,
        onClick = { onTabSelect(AppTab.REELS) },
        testTag = "nav_tab_reels"
      )

      // Floating Center + Button
      Box(
        modifier = Modifier
          .size(46.dp)
          .clip(RoundedCornerShape(16.dp))
          .background(
            Brush.linearGradient(
              listOf(TokBookBlue, TokBookPink)
            )
          )
          .clickable { onCenterAction() }
          .testTag("nav_center_create_button"),
        contentAlignment = Alignment.Center
      ) {
        Icon(Icons.Default.Add, contentDescription = "Create", tint = Color.White, modifier = Modifier.size(24.dp))
      }

      // Web App Mode (Runs the single-file web app in WebView)
      BottomNavItem(
        icon = if (activeTab == AppTab.WEB_MODE) Icons.Default.Language else Icons.Outlined.Language,
        label = "Web Mode",
        isSelected = activeTab == AppTab.WEB_MODE,
        activeColor = TokBookCyan,
        onClick = { onTabSelect(AppTab.WEB_MODE) },
        testTag = "nav_tab_web_mode"
      )

      // Profile
      BottomNavItem(
        icon = if (activeTab == AppTab.PROFILE) Icons.Default.Person else Icons.Outlined.Person,
        label = "Profile",
        isSelected = activeTab == AppTab.PROFILE,
        activeColor = Color(0xFFA855F7),
        onClick = { onTabSelect(AppTab.PROFILE) },
        testTag = "nav_tab_profile"
      )
    }
  }
}

@Composable
fun BottomNavItem(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  label: String,
  isSelected: Boolean,
  activeColor: Color,
  onClick: () -> Unit,
  testTag: String
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .clip(RoundedCornerShape(12.dp))
      .clickable { onClick() }
      .padding(horizontal = 12.dp, vertical = 6.dp)
      .testTag(testTag)
  ) {
    Icon(
      imageVector = icon,
      contentDescription = label,
      tint = if (isSelected) activeColor else Color.Gray,
      modifier = Modifier.size(22.dp)
    )
    Spacer(modifier = Modifier.height(3.dp))
    Text(
      text = label,
      fontSize = 10.sp,
      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
      color = if (isSelected) activeColor else Color.Gray
    )
  }
}

@Composable
fun CreateChoiceDialog(
  onDismiss: () -> Unit,
  onCreatePost: () -> Unit,
  onCreateReel: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = DarkBg,
      tonalElevation = 6.dp,
      modifier = Modifier
        .fillMaxWidth()
        .testTag("create_choice_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        Text(
          text = "Create New Content",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Post option
        Card(
          onClick = onCreatePost,
          colors = CardDefaults.cardColors(containerColor = DarkSurface),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(44.dp)
                .background(TokBookBlue.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Home, contentDescription = "Post", tint = TokBookBlue)
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
              Text(text = "Timeline Post", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
              Text(text = "Share text updates, discussions & photos", fontSize = 11.sp, color = Color.Gray)
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Reel option
        Card(
          onClick = onCreateReel,
          colors = CardDefaults.cardColors(containerColor = DarkSurface),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(44.dp)
                .background(TokBookPink.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.PlayArrow, contentDescription = "Reel", tint = TokBookPink)
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
              Text(text = "Vertical Reel", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
              Text(text = "Publish full-screen video with sound & tags", fontSize = 11.sp, color = Color.Gray)
            }
          }
        }
      }
    }
  }
}
