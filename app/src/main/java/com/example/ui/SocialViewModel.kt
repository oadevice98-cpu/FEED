package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.data.SocialRepository
import com.example.model.Comment
import com.example.model.Post
import com.example.model.Reel
import com.example.model.Story
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AppTab {
  FEED,
  REELS,
  EXPLORE,
  WEB_MODE,
  PROFILE
}

class SocialViewModel(application: Application) : AndroidViewModel(application) {
  private val repository = SocialRepository(application.applicationContext)

  val posts: StateFlow<List<Post>> = repository.posts
  val reels: StateFlow<List<Reel>> = repository.reels
  val comments: StateFlow<Map<String, List<Comment>>> = repository.comments
  val stories: List<Story> = repository.stories

  private val _activeTab = MutableStateFlow(AppTab.FEED)
  val activeTab: StateFlow<AppTab> = _activeTab.asStateFlow()

  private val _activeFilter = MutableStateFlow("All")
  val activeFilter: StateFlow<String> = _activeFilter.asStateFlow()

  private val _selectedCommentTargetId = MutableStateFlow<String?>(null)
  val selectedCommentTargetId: StateFlow<String?> = _selectedCommentTargetId.asStateFlow()

  private val _isCreatePostOpen = MutableStateFlow(false)
  val isCreatePostOpen: StateFlow<Boolean> = _isCreatePostOpen.asStateFlow()

  private val _isCreateReelOpen = MutableStateFlow(false)
  val isCreateReelOpen: StateFlow<Boolean> = _isCreateReelOpen.asStateFlow()

  private val _activeStory = MutableStateFlow<Story?>(null)
  val activeStory: StateFlow<Story?> = _activeStory.asStateFlow()

  fun setTab(tab: AppTab) {
    _activeTab.value = tab
  }

  fun setFilter(filter: String) {
    _activeFilter.value = filter
  }

  fun openComments(targetId: String) {
    _selectedCommentTargetId.value = targetId
  }

  fun closeComments() {
    _selectedCommentTargetId.value = null
  }

  fun openCreatePost(open: Boolean) {
    _isCreatePostOpen.value = open
  }

  fun openCreateReel(open: Boolean) {
    _isCreateReelOpen.value = open
  }

  fun openStory(story: Story?) {
    _activeStory.value = story
  }

  fun togglePostLike(postId: String, reaction: String = "👍") {
    repository.togglePostLike(postId, reaction)
  }

  fun toggleReelLike(reelId: String) {
    repository.toggleReelLike(reelId)
  }

  fun addPost(content: String, imageUrl: String?, category: String, feeling: String?) {
    repository.addPost(content, imageUrl, category, feeling)
  }

  fun deletePost(postId: String) {
    repository.deletePost(postId)
  }

  fun addReel(caption: String, musicTitle: String, colorFrom: Long, colorTo: Long) {
    repository.addReel(caption, musicTitle, colorFrom, colorTo)
  }

  fun addComment(targetId: String, text: String) {
    repository.addComment(targetId, text)
  }

  fun toggleCommentLike(targetId: String, commentId: String) {
    repository.toggleCommentLike(targetId, commentId)
  }
}
