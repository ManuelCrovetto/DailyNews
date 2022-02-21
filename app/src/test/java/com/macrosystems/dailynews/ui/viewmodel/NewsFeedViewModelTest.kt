package com.macrosystems.dailynews.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.macrosystems.dailynews.core.providers.TestDispatchers
import com.macrosystems.dailynews.data.model.news.*
import com.macrosystems.dailynews.data.network.response.Result
import com.macrosystems.dailynews.domain.GetNewsFeed
import com.macrosystems.dailynews.utils.getOrAwaitValue
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
class NewsFeedViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @RelaxedMockK
    private lateinit var getNewsFeed: GetNewsFeed

    private lateinit var newsFeedViewModel: NewsFeedViewModel

    private lateinit var testDispatchers: TestDispatchers

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        testDispatchers = TestDispatchers()
        newsFeedViewModel = NewsFeedViewModel(getNewsFeed, testDispatchers)
        Dispatchers.setMain(testDispatchers.testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getNewsFeed returns a ResultError type object`() {
        coEvery { getNewsFeed() } returns Result.Error(Exception("network error"))
        newsFeedViewModel.getNewsFeedList()
        assert(newsFeedViewModel.newsFeedList.getOrAwaitValue().isEmpty())
    }

    @Test
    fun `when getNewsFeed sets a NOT EMPTY LIST to newsFeedList livedata value`() = runTest {
        val newsList = mockNewsResponseList()
        coEvery { getNewsFeed() } returns Result.Success(newsList)
        newsFeedViewModel.getNewsFeedList()
        assert(newsFeedViewModel.newsFeedList.getOrAwaitValue().isNotEmpty())
        assert(newsFeedViewModel.newsFeedList.getOrAwaitValue() == newsList)
    }

    @Test
    fun `when getNewsFeed sets an EMPTY LIST to newsFeedList livedata value`() {
        coEvery { getNewsFeed() } returns Result.Success(emptyList())
        newsFeedViewModel.getNewsFeedList()
        assert(newsFeedViewModel.newsFeedList.getOrAwaitValue().isEmpty())
    }

    @Test
    fun `featuredNews loads first news item from the list`() = runTest {
        val newsList = mockNewsResponseList()
        coEvery { getNewsFeed() } returns Result.Success(newsList)
        newsFeedViewModel.getNewsFeedList()
        val titleAndIndex = newsFeedViewModel.featuredNews.getOrAwaitValue()
        assert(titleAndIndex.title == "1" && titleAndIndex.date == "01 02 2022")
    }

    @Test
    fun `viewStateFlow (MutableStateFlow), mutates state as desired when getNewsFeed returns a NOT EMPTY LIST`() =
        runTest {
            coEvery { getNewsFeed() } returns Result.Success(mockNewsResponseList())
            newsFeedViewModel.viewState.test {
                newsFeedViewModel.getNewsFeedList()
                val init = awaitItem()
                val second = awaitItem()
                assert(init.isLoading)
                assert(!second.isLoading && second.success)
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `viewStateFlow (MutableStateFlow), mutates state as desired when getNewsFeed returns a EMPTY LIST`() =
        runTest {
            coEvery { getNewsFeed() } returns Result.Success(emptyList())
            newsFeedViewModel.viewState.test {
                newsFeedViewModel.getNewsFeedList()
                val initialState = awaitItem()
                val finalState = awaitItem()
                assert(initialState.isLoading)
                assert(!finalState.isLoading && finalState.success && finalState.isEmptyList)
                cancelAndConsumeRemainingEvents()
            }

        }

    @Test
    fun `viewStateFlow (MutableStateFlow), mutates state as desired when getNewsFeed returns ResultError with empty exception message`() =
        runTest {
            coEvery { getNewsFeed() } returns Result.Error(null)
            newsFeedViewModel.viewState.test {
                newsFeedViewModel.getNewsFeedList()
                val initialState = awaitItem()
                val finalState = awaitItem()
                assert(initialState.isLoading)
                assert(!finalState.isLoading && finalState.isEmptyList)
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `viewStateFlow (MutableStateFlow), mutates state as desired when getNewsFeed returns ResultError with exception message`() =
        runTest {
            coEvery { getNewsFeed() } returns Result.Error(Exception("network error"))
            newsFeedViewModel.viewState.test(timeout = 3.seconds) {
                newsFeedViewModel.getNewsFeedList()
                val initialState = awaitItem()
                val finalState = awaitItem()
                assert(initialState.isLoading)
                assert(!finalState.isLoading && finalState.error && finalState.isEmptyList)
                cancelAndConsumeRemainingEvents()
            }
        }

    private fun mockNewsResponseList(): List<NewsResponse> {
        val list = mutableListOf<NewsResponse>()
        (1..5).forEachIndexed { index, char ->
            list.add(
                NewsResponse(
                    author = index,
                    categories = listOf(1, 2),
                    comment_status = "",
                    content = Content(
                        true,
                        "<p><iframe loading=\\\"lazy\\\" width=\\\"560\\\" height=\\\"315\\\" src=\\\"https://www.youtube.com/embed/HcCnSXYYuP8\\\" title=\\\"YouTube video player\\\" frameborder=\\\"0\\\" allow=\\\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture\\\" allowfullscreen></iframe></p>\\n<p>Den Haag &#8211; Op de Elandstraat in Den Haag is de toren van een kerk instabiel geworden door de storm.</p>\\n<p>De hulpdiensten kwamen eerder vandaag al ter plaatse, toen werd geconstateerd dat er geen gevaar was voor de omgeving. Omstreeks 21:30 vanavond werd de brandweer opnieuw gealarmeerd. De hulpdiensten hebben opgeschaald naar GRIP 1.</p>\\n<p>Er schuilt mogelijk toch het gevaar dat de kerktoren voor een gedeelte instort. De hulpdiensten zijn opnieuw ter plaatse. De omgeving is afgezet en er wordt momenteel druk overleg gepleegd door de aanwezige hulpdiensten.</p>\\n<p>Meerdere touringcars staan klaar voor wanneer er ontruimd moet worden.</p>\\n<p><img loading=\\\"lazy\\\" class=\\\"alignnone size-full wp-image-234980\\\" src=\\\"https://district8.net/wp-content/D8-DSCF3903-1.jpg\\\" alt=\\\"\\\" width=\\\"1872\\\" height=\\\"1248\\\" srcset=\\\"https://district8.net/wp-content/D8-DSCF3903-1.jpg 1872w, https://district8.net/wp-content/D8-DSCF3903-1-768x512.jpg 768w, https://district8.net/wp-content/D8-DSCF3903-1-1536x1024.jpg 1536w, https://district8.net/wp-content/D8-DSCF3903-1-630x420.jpg 630w, https://district8.net/wp-content/D8-DSCF3903-1-1260x840.jpg 1260w, https://district8.net/wp-content/D8-DSCF3903-1-640x427.jpg 640w, https://district8.net/wp-content/D8-DSCF3903-1-1280x853.jpg 1280w, https://district8.net/wp-content/D8-DSCF3903-1-681x454.jpg 681w, https://district8.net/wp-content/D8-DSCF3903-1-1362x908.jpg 1362w\\\" sizes=\\\"(max-width: 1872px) 100vw, 1872px\\\" /> <img loading=\\\"lazy\\\" class=\\\"alignnone size-medium wp-image-234981\\\" src=\\\"https://district8.net/wp-content/D8-DSCF3908.jpg\\\" alt=\\\"\\\" width=\\\"1872\\\" height=\\\"1248\\\" srcset=\\\"https://district8.net/wp-content/D8-DSCF3908.jpg 1872w, https://district8.net/wp-content/D8-DSCF3908-768x512.jpg 768w, https://district8.net/wp-content/D8-DSCF3908-1536x1024.jpg 1536w, https://district8.net/wp-content/D8-DSCF3908-630x420.jpg 630w, https://district8.net/wp-content/D8-DSCF3908-1260x840.jpg 1260w, https://district8.net/wp-content/D8-DSCF3908-640x427.jpg 640w, https://district8.net/wp-content/D8-DSCF3908-1280x853.jpg 1280w, https://district8.net/wp-content/D8-DSCF3908-681x454.jpg 681w, https://district8.net/wp-content/D8-DSCF3908-1362x908.jpg 1362w\\\" sizes=\\\"(max-width: 1872px) 100vw, 1872px\\\" /> <img loading=\\\"lazy\\\" class=\\\"alignnone size-medium wp-image-234982\\\" src=\\\"https://district8.net/wp-content/D8-DSCF3910.jpg\\\" alt=\\\"\\\" width=\\\"1872\\\" height=\\\"1248\\\" srcset=\\\"https://district8.net/wp-content/D8-DSCF3910.jpg 1872w, https://district8.net/wp-content/D8-DSCF3910-768x512.jpg 768w, https://district8.net/wp-content/D8-DSCF3910-1536x1024.jpg 1536w, https://district8.net/wp-content/D8-DSCF3910-630x420.jpg 630w, https://district8.net/wp-content/D8-DSCF3910-1260x840.jpg 1260w, https://district8.net/wp-content/D8-DSCF3910-640x427.jpg 640w, https://district8.net/wp-content/D8-DSCF3910-1280x853.jpg 1280w, https://district8.net/wp-content/D8-DSCF3910-681x454.jpg 681w, https://district8.net/wp-content/D8-DSCF3910-1362x908.jpg 1362w\\\" sizes=\\\"(max-width: 1872px) 100vw, 1872px\\\" /><img loading=\\\"lazy\\\" class=\\\"alignnone size-full wp-image-234983\\\" src=\\\"https://district8.net/wp-content/D8_IMG_0546.jpg\\\" alt=\\\"\\\" width=\\\"1920\\\" height=\\\"1280\\\" srcset=\\\"https://district8.net/wp-content/D8_IMG_0546.jpg 1920w, https://district8.net/wp-content/D8_IMG_0546-768x512.jpg 768w, https://district8.net/wp-content/D8_IMG_0546-1536x1024.jpg 1536w, https://district8.net/wp-content/D8_IMG_0546-630x420.jpg 630w, https://district8.net/wp-content/D8_IMG_0546-1260x840.jpg 1260w, https://district8.net/wp-content/D8_IMG_0546-640x427.jpg 640w, https://district8.net/wp-content/D8_IMG_0546-1280x853.jpg 1280w, https://district8.net/wp-content/D8_IMG_0546-681x454.jpg 681w, https://district8.net/wp-content/D8_IMG_0546-1362x908.jpg 1362w\\\" sizes=\\\"(max-width: 1920px) 100vw, 1920px\\\" /> <img loading=\\\"lazy\\\" class=\\\"alignnone size-medium wp-image-234984\\\" src=\\\"https://district8.net/wp-content/D8_IMG_0555-1.jpg\\\" alt=\\\"\\\" width=\\\"1920\\\" height=\\\"1280\\\" srcset=\\\"https://district8.net/wp-content/D8_IMG_0555-1.jpg 1920w, https://district8.net/wp-content/D8_IMG_0555-1-768x512.jpg 768w, https://district8.net/wp-content/D8_IMG_0555-1-1536x1024.jpg 1536w, https://district8.net/wp-content/D8_IMG_0555-1-630x420.jpg 630w, https://district8.net/wp-content/D8_IMG_0555-1-1260x840.jpg 1260w, https://district8.net/wp-content/D8_IMG_0555-1-640x427.jpg 640w, https://district8.net/wp-content/D8_IMG_0555-1-1280x853.jpg 1280w, https://district8.net/wp-content/D8_IMG_0555-1-681x454.jpg 681w, https://district8.net/wp-content/D8_IMG_0555-1-1362x908.jpg 1362w\\\" sizes=\\\"(max-width: 1920px) 100vw, 1920px\\\" /> <img loading=\\\"lazy\\\" class=\\\"alignnone size-medium wp-image-234985\\\" src=\\\"https://district8.net/wp-content/D8_IMG_0559.jpg\\\" alt=\\\"\\\" width=\\\"1920\\\" height=\\\"1280\\\" srcset=\\\"https://district8.net/wp-content/D8_IMG_0559.jpg 1920w, https://district8.net/wp-content/D8_IMG_0559-768x512.jpg 768w, https://district8.net/wp-content/D8_IMG_0559-1536x1024.jpg 1536w, https://district8.net/wp-content/D8_IMG_0559-630x420.jpg 630w, https://district8.net/wp-content/D8_IMG_0559-1260x840.jpg 1260w, https://district8.net/wp-content/D8_IMG_0559-640x427.jpg 640w, https://district8.net/wp-content/D8_IMG_0559-1280x853.jpg 1280w, https://district8.net/wp-content/D8_IMG_0559-681x454.jpg 681w, https://district8.net/wp-content/D8_IMG_0559-1362x908.jpg 1362w\\\" sizes=\\\"(max-width: 1920px) 100vw, 1920px\\\" /> <img loading=\\\"lazy\\\" class=\\\"alignnone size-medium wp-image-234986\\\" src=\\\"https://district8.net/wp-content/D8_IMG_0563.jpg\\\" alt=\\\"\\\" width=\\\"1920\\\" height=\\\"1280\\\" srcset=\\\"https://district8.net/wp-content/D8_IMG_0563.jpg 1920w, https://district8.net/wp-content/D8_IMG_0563-768x512.jpg 768w, https://district8.net/wp-content/D8_IMG_0563-1536x1024.jpg 1536w, https://district8.net/wp-content/D8_IMG_0563-630x420.jpg 630w, https://district8.net/wp-content/D8_IMG_0563-1260x840.jpg 1260w, https://district8.net/wp-content/D8_IMG_0563-640x427.jpg 640w, https://district8.net/wp-content/D8_IMG_0563-1280x853.jpg 1280w, https://district8.net/wp-content/D8_IMG_0563-681x454.jpg 681w, https://district8.net/wp-content/D8_IMG_0563-1362x908.jpg 1362w\\\" sizes=\\\"(max-width: 1920px) 100vw, 1920px\\\" /> <img loading=\\\"lazy\\\" class=\\\"alignnone size-medium wp-image-234987\\\" src=\\\"https://district8.net/wp-content/D8-DSCF3955.jpg\\\" alt=\\\"\\\" width=\\\"1872\\\" height=\\\"1248\\\" srcset=\\\"https://district8.net/wp-content/D8-DSCF3955.jpg 1872w, https://district8.net/wp-content/D8-DSCF3955-768x512.jpg 768w, https://district8.net/wp-content/D8-DSCF3955-1536x1024.jpg 1536w, https://district8.net/wp-content/D8-DSCF3955-630x420.jpg 630w, https://district8.net/wp-content/D8-DSCF3955-1260x840.jpg 1260w, https://district8.net/wp-content/D8-DSCF3955-640x427.jpg 640w, https://district8.net/wp-content/D8-DSCF3955-1280x853.jpg 1280w, https://district8.net/wp-content/D8-DSCF3955-681x454.jpg 681w, https://district8.net/wp-content/D8-DSCF3955-1362x908.jpg 1362w\\\" sizes=\\\"(max-width: 1872px) 100vw, 1872px\\\" /></p>\\n<p>Foto&#8217;s &#038; video: Owen O’Brien &amp; Jordy de Groot</p>\\n\""
                    ),
                    date = "2022-02-${index + 1}T23:20:00",
                    date_gmt = "",
                    excerpt = Excerpt(false, ""),
                    featured_media = 23,
                    format = "",
                    guid = Guid(""),
                    id = index,
                    link = "",
                    meta = listOf("hello", "hello"),
                    modified = "",
                    modified_gmt = "",
                    ping_status = "",
                    slug = "",
                    status = "",
                    sticky = false,
                    tags = listOf("", ""),
                    template = "",
                    title = Title(char.toString()),
                    type = "",
                    yoast_head = ""
                )
            )
        }
        return list
    }
}