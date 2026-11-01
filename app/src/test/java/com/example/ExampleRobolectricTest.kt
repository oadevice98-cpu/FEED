package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.SocialRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Feed", appName)
  }

  @Test
  fun `social repository initial state and persistence`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = SocialRepository(context)

    assertTrue(repository.posts.value.isNotEmpty())
    assertTrue(repository.reels.value.isNotEmpty())

    val initialCount = repository.posts.value.size
    repository.addPost(
      content = "Test post content",
      imageUrl = null,
      category = "Tech",
      feeling = "Inspired"
    )

    assertEquals(initialCount + 1, repository.posts.value.size)
    assertEquals("Test post content", repository.posts.value.first().content)
  }
}
