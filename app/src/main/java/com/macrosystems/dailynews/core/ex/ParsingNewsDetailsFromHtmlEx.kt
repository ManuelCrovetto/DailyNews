package com.macrosystems.dailynews.core.ex

import com.macrosystems.dailynews.data.model.news.NewsResponse
import com.macrosystems.dailynews.data.model.parcelable.DetailedNewsParcelable
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements


fun NewsResponse.parseNewsDetailsFromHtml(): DetailedNewsParcelable {

    val html: Document = Jsoup.parse(this.content.rendered)
    val imageUrl = html.select("img").first()
    val iframe = html.select("iframe").first()
    val description = html.select("p")
    return DetailedNewsParcelable(
        imageUrl = imageUrl.attr("src"),
        title = this.title.rendered,
        date = this.date.formatDate().orEmpty(),
        description = checkIfEmptyDescription(description),
        videoUrl = checkIfiFrameIsNull(iframe).orEmpty()
    )

}

private fun checkIfEmptyDescription(description: Elements): String {
    return if (description.isNotEmpty()) description[2].text() else ""
}

private fun checkIfiFrameIsNull(iframe: Element?): String? {
    return iframe?.let {
        return@let it.toString()
    } ?: run {
         return@run null
    }
}