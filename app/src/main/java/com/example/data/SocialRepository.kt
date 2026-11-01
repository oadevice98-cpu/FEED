package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.Comment
import com.example.model.Post
import com.example.model.Reel
import com.example.model.Story
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

class SocialRepository(context: Context) {
  private val prefs: SharedPreferences =
    context.getSharedPreferences("tokbook_storage", Context.MODE_PRIVATE)

  private val _posts = MutableStateFlow<List<Post>>(emptyList())
  val posts: StateFlow<List<Post>> = _posts.asStateFlow()

  private val _reels = MutableStateFlow<List<Reel>>(emptyList())
  val reels: StateFlow<List<Reel>> = _reels.asStateFlow()

  private val _comments = MutableStateFlow<Map<String, List<Comment>>>(emptyMap())
  val comments: StateFlow<Map<String, List<Comment>>> = _comments.asStateFlow()

  val stories: List<Story> = listOf(
    Story("s1", "Elena", "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=150&q=80", "https://images.unsplash.com/photo-1533105079780-92b9be482077?auto=format&fit=crop&w=800&q=80"),
    Story("s2", "Matteo", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=150&q=80", "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?auto=format&fit=crop&w=800&q=80"),
    Story("s3", "Kira", "https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=150&q=80", "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=800&q=80"),
    Story("s4", "Marcus", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=150&q=80", "https://images.unsplash.com/photo-1470071459604-3b5ec3a7fe05?auto=format&fit=crop&w=800&q=80"),
    Story("s5", "Sarah", "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=150&q=80", "https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=800&q=80")
  )

  init {
    loadData()
  }

  private fun loadData() {
    // 1. Posts
    val savedPostsJson = prefs.getString("posts_json", null)
    if (savedPostsJson != null) {
      try {
        val array = JSONArray(savedPostsJson)
        val list = mutableListOf<Post>()
        for (i in 0 until array.length()) {
          val obj = array.getJSONObject(i)
          list.add(
            Post(
              id = obj.getString("id"),
              authorName = obj.getString("authorName"),
              authorHandle = obj.optString("authorHandle", "user"),
              authorAvatar = obj.getString("authorAvatar"),
              content = obj.getString("content"),
              imageUrl = if (obj.has("imageUrl") && !obj.isNull("imageUrl")) obj.getString("imageUrl") else null,
              timestamp = obj.getString("timestamp"),
              category = obj.optString("category", "General"),
              feeling = if (obj.has("feeling") && !obj.isNull("feeling")) obj.getString("feeling") else null,
              likesCount = obj.getInt("likesCount"),
              isLiked = obj.getBoolean("isLiked"),
              reactionType = if (obj.has("reactionType") && !obj.isNull("reactionType")) obj.getString("reactionType") else null,
              commentsCount = obj.getInt("commentsCount"),
              sharesCount = obj.optInt("sharesCount", 0),
              isUserPost = obj.optBoolean("isUserPost", false)
            )
          )
        }
        _posts.value = list
      } catch (e: Exception) {
        _posts.value = getInitialPosts()
      }
    } else {
      _posts.value = getInitialPosts()
      savePosts()
    }

    // 2. Reels
    val savedReelsJson = prefs.getString("reels_json", null)
    if (savedReelsJson != null) {
      try {
        val array = JSONArray(savedReelsJson)
        val list = mutableListOf<Reel>()
        for (i in 0 until array.length()) {
          val obj = array.getJSONObject(i)
          list.add(
            Reel(
              id = obj.getString("id"),
              creatorName = obj.getString("creatorName"),
              creatorHandle = obj.getString("creatorHandle"),
              creatorAvatar = obj.getString("creatorAvatar"),
              caption = obj.getString("caption"),
              musicTitle = obj.getString("musicTitle"),
              likesCount = obj.getInt("likesCount"),
              isLiked = obj.getBoolean("isLiked"),
              commentsCount = obj.getInt("commentsCount"),
              sharesCount = obj.optInt("sharesCount", 0),
              colorFromHex = obj.optLong("colorFromHex", 0xFFFE2C55),
              colorToHex = obj.optLong("colorToHex", 0xFF25F4EE),
              isUserReel = obj.optBoolean("isUserReel", false)
            )
          )
        }
        _reels.value = list
      } catch (e: Exception) {
        _reels.value = getInitialReels()
      }
    } else {
      _reels.value = getInitialReels()
      saveReels()
    }

    // 3. Comments
    val savedCommentsJson = prefs.getString("comments_json", null)
    if (savedCommentsJson != null) {
      try {
        val root = JSONObject(savedCommentsJson)
        val map = mutableMapOf<String, List<Comment>>()
        val keys = root.keys()
        while (keys.hasNext()) {
          val key = keys.next()
          val arr = root.getJSONArray(key)
          val cList = mutableListOf<Comment>()
          for (j in 0 until arr.length()) {
            val cObj = arr.getJSONObject(j)
            cList.add(
              Comment(
                id = cObj.getString("id"),
                targetId = cObj.getString("targetId"),
                authorName = cObj.getString("authorName"),
                authorAvatar = cObj.getString("authorAvatar"),
                text = cObj.getString("text"),
                timestamp = cObj.getString("timestamp"),
                likesCount = cObj.optInt("likesCount", 0),
                isLiked = cObj.optBoolean("isLiked", false)
              )
            )
          }
          map[key] = cList
        }
        _comments.value = map
      } catch (e: Exception) {
        _comments.value = getInitialComments()
      }
    } else {
      _comments.value = getInitialComments()
      saveComments()
    }
  }

  private fun savePosts() {
    val array = JSONArray()
    for (p in _posts.value) {
      val obj = JSONObject().apply {
        put("id", p.id)
        put("authorName", p.authorName)
        put("authorHandle", p.authorHandle)
        put("authorAvatar", p.authorAvatar)
        put("content", p.content)
        put("imageUrl", p.imageUrl)
        put("timestamp", p.timestamp)
        put("category", p.category)
        put("feeling", p.feeling)
        put("likesCount", p.likesCount)
        put("isLiked", p.isLiked)
        put("reactionType", p.reactionType)
        put("commentsCount", p.commentsCount)
        put("sharesCount", p.sharesCount)
        put("isUserPost", p.isUserPost)
      }
      array.put(obj)
    }
    prefs.edit().putString("posts_json", array.toString()).apply()
  }

  private fun saveReels() {
    val array = JSONArray()
    for (r in _reels.value) {
      val obj = JSONObject().apply {
        put("id", r.id)
        put("creatorName", r.creatorName)
        put("creatorHandle", r.creatorHandle)
        put("creatorAvatar", r.creatorAvatar)
        put("caption", r.caption)
        put("musicTitle", r.musicTitle)
        put("likesCount", r.likesCount)
        put("isLiked", r.isLiked)
        put("commentsCount", r.commentsCount)
        put("sharesCount", r.sharesCount)
        put("colorFromHex", r.colorFromHex)
        put("colorToHex", r.colorToHex)
        put("isUserReel", r.isUserReel)
      }
      array.put(obj)
    }
    prefs.edit().putString("reels_json", array.toString()).apply()
  }

  private fun saveComments() {
    val root = JSONObject()
    for ((key, list) in _comments.value) {
      val arr = JSONArray()
      for (c in list) {
        val cObj = JSONObject().apply {
          put("id", c.id)
          put("targetId", c.targetId)
          put("authorName", c.authorName)
          put("authorAvatar", c.authorAvatar)
          put("text", c.text)
          put("timestamp", c.timestamp)
          put("likesCount", c.likesCount)
          put("isLiked", c.isLiked)
        }
        arr.put(cObj)
      }
      root.put(key, arr)
    }
    prefs.edit().putString("comments_json", root.toString()).apply()
  }

  fun addPost(content: String, imageUrl: String?, category: String, feeling: String?) {
    val newPost = Post(
      id = "post_${System.currentTimeMillis()}",
      authorName = "Alex Mercer",
      authorHandle = "alexmercer",
      authorAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=150&q=80",
      content = content.ifBlank { "Shared an update ✨" },
      imageUrl = imageUrl,
      timestamp = "Just now",
      category = category,
      feeling = feeling,
      likesCount = 1,
      isLiked = true,
      reactionType = "👍",
      commentsCount = 0,
      sharesCount = 0,
      isUserPost = true
    )
    _posts.value = listOf(newPost) + _posts.value
    savePosts()
  }

  fun togglePostLike(postId: String, reaction: String = "👍") {
    _posts.value = _posts.value.map { p ->
      if (p.id == postId) {
        val willLike = !p.isLiked
        p.copy(
          isLiked = willLike,
          likesCount = p.likesCount + if (willLike) 1 else -1,
          reactionType = if (willLike) reaction else null
        )
      } else p
    }
    savePosts()
  }

  fun deletePost(postId: String) {
    _posts.value = _posts.value.filter { it.id != postId }
    savePosts()
  }

  fun addReel(caption: String, musicTitle: String, colorFrom: Long, colorTo: Long) {
    val newReel = Reel(
      id = "reel_${System.currentTimeMillis()}",
      creatorName = "Alex Mercer",
      creatorHandle = "alexmercer",
      creatorAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=150&q=80",
      caption = caption.ifBlank { "New reel vibe #tokbook 🔥" },
      musicTitle = musicTitle.ifBlank { "Alex Mercer - Original Sound 🎵" },
      likesCount = 1,
      isLiked = true,
      commentsCount = 0,
      sharesCount = 0,
      colorFromHex = colorFrom,
      colorToHex = colorTo,
      isUserReel = true
    )
    _reels.value = listOf(newReel) + _reels.value
    saveReels()
  }

  fun toggleReelLike(reelId: String) {
    _reels.value = _reels.value.map { r ->
      if (r.id == reelId) {
        val willLike = !r.isLiked
        r.copy(
          isLiked = willLike,
          likesCount = r.likesCount + if (willLike) 1 else -1
        )
      } else r
    }
    saveReels()
  }

  fun addComment(targetId: String, text: String) {
    val newComment = Comment(
      id = "c_${System.currentTimeMillis()}",
      targetId = targetId,
      authorName = "Alex Mercer",
      authorAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=150&q=80",
      text = text,
      timestamp = "Just now",
      likesCount = 0,
      isLiked = false
    )
    val currentMap = _comments.value.toMutableMap()
    val list = currentMap[targetId].orEmpty()
    currentMap[targetId] = listOf(newComment) + list
    _comments.value = currentMap
    saveComments()

    // Update post count
    _posts.value = _posts.value.map { p ->
      if (p.id == targetId) p.copy(commentsCount = p.commentsCount + 1) else p
    }
    savePosts()
  }

  fun toggleCommentLike(targetId: String, commentId: String) {
    val currentMap = _comments.value.toMutableMap()
    val list = currentMap[targetId].orEmpty()
    currentMap[targetId] = list.map { c ->
      if (c.id == commentId) {
        val willLike = !c.isLiked
        c.copy(isLiked = willLike, likesCount = c.likesCount + if (willLike) 1 else -1)
      } else c
    }
    _comments.value = currentMap
    saveComments()
  }

  private fun getInitialPosts(): List<Post> {
    return listOf(
      Post(
        id = "post_1",
        authorName = "Elena Rostova",
        authorHandle = "elenarostova",
        authorAvatar = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=150&q=80",
        content = "Just wrapped shooting our brand new cinematic travel series! 🎬 Here is a sneak preview of the sunrise over the Amalfi coast. What do you all think?",
        imageUrl = "https://images.unsplash.com/photo-1533105079780-92b9be482077?auto=format&fit=crop&w=800&q=80",
        timestamp = "15m ago",
        category = "Travel",
        feeling = "Grateful ✨",
        likesCount = 142,
        isLiked = false,
        reactionType = null,
        commentsCount = 18,
        sharesCount = 6
      ),
      Post(
        id = "post_2",
        authorName = "Marcus Vance",
        authorHandle = "marcus_tech",
        authorAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=150&q=80",
        content = "Quick tech poll: Are vertical reels completely replacing traditional horizontal feeds for you, or do you still love long-form timeline discussions? Drop your thoughts below! 👇🔥",
        imageUrl = null,
        timestamp = "1h ago",
        category = "Tech",
        feeling = "Curious 🤔",
        likesCount = 89,
        isLiked = true,
        reactionType = "👍",
        commentsCount = 34,
        sharesCount = 12
      ),
      Post(
        id = "post_3",
        authorName = "Chef Matteo",
        authorHandle = "matteocooks",
        authorAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=150&q=80",
        content = "Homemade Neapolitan sourdough pizza fresh out of the 900°F wood oven! Crispy, airy cornicione and fresh basil. Recipe dropping tonight! 🍕🌿",
        imageUrl = "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?auto=format&fit=crop&w=800&q=80",
        timestamp = "3h ago",
        category = "Foodie",
        feeling = "Hungry 😋",
        likesCount = 310,
        isLiked = false,
        reactionType = null,
        commentsCount = 42,
        sharesCount = 27
      )
    )
  }

  private fun getInitialReels(): List<Reel> {
    return listOf(
      Reel(
        id = "reel_1",
        creatorName = "Kira Cyberpunk",
        creatorHandle = "kiraneon",
        creatorAvatar = "https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=150&q=80",
        caption = "Night ride through Tokyo shinjuku with glowing synthwave beats 🏍️💨 #tokyo #neon #cyberpunk #vibe",
        musicTitle = "Synthwave Dreams · Nightcall VIP 🎵",
        likesCount = 12430,
        isLiked = false,
        commentsCount = 428,
        sharesCount = 129,
        colorFromHex = 0xFFFE2C55,
        colorToHex = 0xFF25F4EE
      ),
      Reel(
        id = "reel_2",
        creatorName = "David Wanderlust",
        creatorHandle = "david_nature",
        creatorAvatar = "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?auto=format&fit=crop&w=150&q=80",
        caption = "Golden hour drone glide over the alpine peaks of Switzerland 🏔️☀️ Tag someone you would bring here!",
        musicTitle = "Peaceful Moments · Original Audio 🎵",
        likesCount = 24890,
        isLiked = true,
        commentsCount = 912,
        sharesCount = 345,
        colorFromHex = 0xFFF59E0B,
        colorToHex = 0xFF9333EA
      ),
      Reel(
        id = "reel_3",
        creatorName = "Studio Choreography",
        creatorHandle = "dancewave",
        creatorAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=150&q=80",
        caption = "Hit this routine in 20 minutes! Can you guess the tempo? Drop your rate 1-10 🔥🕺 #dance #challenge #tokbook",
        musicTitle = "Electro Bass Drop · Trend Sound 2026 🎵",
        likesCount = 45100,
        isLiked = false,
        commentsCount = 1530,
        sharesCount = 680,
        colorFromHex = 0xFF06B6D4,
        colorToHex = 0xFF3B82F6
      )
    )
  }

  private fun getInitialComments(): Map<String, List<Comment>> {
    return mapOf(
      "post_1" to listOf(
        Comment("c1", "post_1", "Sophia Chen", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=100&q=80", "This looks straight out of a movie! Absolutely stunning view.", "10m ago", 4, false),
        Comment("c2", "post_1", "Liam Walker", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=100&q=80", "Which lens was used for this shot? 📷", "5m ago", 2, false)
      ),
      "reel_1" to listOf(
        Comment("rc1", "reel_1", "CyberNinja", "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?auto=format&fit=crop&w=100&q=80", "Tokyo night drives hit different 🔥", "2h ago", 18, true)
      )
    )
  }
}
