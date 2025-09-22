package com.vako.data.parser.knigavuhe

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vako.data.parser.BooksParser
import com.vako.data.parser.Source
import com.vako.data.parser.model.ParsedBook
import com.vako.data.parser.model.ParsedMediaItem
import com.vako.data.parser.model.ParsedVoiceover
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import javax.inject.Inject

class KnigaVUheParser @Inject constructor(private val gson: Gson) : BooksParser() {

    override val source: Source = Source.KnigaVUhe
    private var page = 0

    override suspend fun getRandomBooks(): List<ParsedBook> {
        val url = "${source.baseUrl}/new/?page=$page"
        val newBooksPageDocument = Jsoup.connect(url)
            .userAgent("Chrome/4.0.249.0 Safari/532.5")
            .referrer("http://www.google.com")
            .get()
        page += 1
        return parseBookList(newBooksPageDocument)
    }

    override suspend fun getBook(internalBookId: String): ParsedBook {
        TODO("Not yet implemented")
    }

    override suspend fun getBookVoiceovers(internalVoiceoverId: String): List<ParsedVoiceover> =
        coroutineScope {
            val url = "${source.baseUrl}/book/$internalVoiceoverId/"
            val bookPageDocument = Jsoup.connect(url)
                .userAgent("Chrome/4.0.249.0 Safari/532.5")
                .referrer("http://www.google.com")
                .get()

            val mainVoiceover = parseVoiceover(bookPageDocument, internalVoiceoverId)

            val otherVoiceoversIds = bookPageDocument.selectFirst(".book_serie_block")?.let {
                it.select(".book_serie_block_item")
                    .select("a")
                    .map { it.attr("href") }
                    .filter { it.contains("book") }
                    .map { it.split("/")[2] }
            }

            Log.e("asd", otherVoiceoversIds.toString())
            val otherVoiceovers = otherVoiceoversIds?.let {
                it.map {
                    async {
                        val voiceoverUrl = "${source.baseUrl}/book/$it/"
                        val voiceoverDocument = Jsoup.connect(voiceoverUrl)
                            .userAgent("Chrome/4.0.249.0 Safari/532.5")
                            .referrer("http://www.google.com")
                            .get()
                        parseVoiceover(voiceoverDocument, internalVoiceoverId)
                    }
                }.awaitAll()
            }

            return@coroutineScope (otherVoiceovers ?: emptyList()) + mainVoiceover
        }

    override suspend fun getBooksInCycle(internalBookId: String): ParsedBook {
        TODO("Not yet implemented")
    }

    private fun parseBookList(document: Document): List<ParsedBook> {
        val bookItems = document.select(".bookkitem")

        return bookItems.map { bookItem ->
            val authors = bookItem.select(".bookkitem_author")
                .select("a")
                .map { it.text() }
                .toList()

            val bookItemMeta = bookItem.select(".bookkitem_meta")

            val title = bookItem.select("a.bookkitem_name").text()
            val internalBookId = bookItem.select("a.bookkitem_name")
                .attr("href")
                .split("/")[2]

            val cover = bookItem.select(".bookkitem_cover_img").attr("src")
            val series = bookItemMeta.select(".-serie").next().select("a").text()

            ParsedBook(
                internalId = internalBookId,
                title = title,
                authors = authors,
                series = series,
                seriesIndex = null,
                coverUrl = cover,
            )
        }
    }

    private fun parseVoiceover(document: Document, internalId: String): ParsedVoiceover {
        val scripts = document.getElementsByTag("script")

        val mediaItemsRegexResult = scripts.firstNotNullOf { part ->
            Regex("""var player = new BookPlayer\([^,]+, (\[\{.*?\}\])""")
                .find(part.toString())?.groupValues?.get(1)
        }

        val playerParseType = object : TypeToken<List<PlayerParseResult>>() {}.type
        val parseResult = gson
            .fromJson<List<PlayerParseResult>>(mediaItemsRegexResult, playerParseType)


        val mediaItems = parseResult.map {
            ParsedMediaItem(
                url = it.url,
                title = it.title,
                duration = it.duration,
            )
        }

        val readers = document.select(".book_title_block")
            .select("a")
            .filter { it.attr("href").contains("reader") }
            .map { it.text() }

        val voiceover = ParsedVoiceover(
            internalId = internalId,
            readers = readers,
            mediaItems = mediaItems
        )


        return voiceover
    }

}

private data class PlayerParseResult(
    val id: Int,
    val title: String,
    val url: String,
    val error: Int,
    val duration: Long,
)