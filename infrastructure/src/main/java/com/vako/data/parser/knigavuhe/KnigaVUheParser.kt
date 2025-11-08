package com.vako.data.parser.knigavuhe

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vako.data.parser.BookParser
import com.vako.data.parser.Source
import com.vako.data.parser.model.ParsedVoiceoverBookMetadata
import com.vako.data.parser.model.ParsedMediaItem
import com.vako.data.parser.model.ParsedVoiceover
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import javax.inject.Inject

class KnigaVUheParser @Inject constructor(private val gson: Gson) : BookParser() {

    override val source: Source = Source.KnigaVUhe
    private var page = 0

    private val bookDocumentCache = mutableMapOf<String, Document>()

    override suspend fun getRandomBooksMetadata(): List<ParsedVoiceoverBookMetadata> {
        val url = "${source.baseUrl}/new/?page=$page"
        val newBooksPageDocument = Jsoup.connect(url)
            .userAgent("Chrome/4.0.249.0 Safari/532.5")
            .referrer("http://www.google.com")
            .get()
        page += 1
        return parseBookList(newBooksPageDocument)
    }

    override suspend fun getBookMetadata(internalVoiceoverId: String): ParsedVoiceoverBookMetadata {
        val voiceoverPageDocument = getCachedVoiceoverDocumentIfPresent(internalVoiceoverId)

        val title = voiceoverPageDocument.selectFirst(".book_title_name")?.text()?.trim().orEmpty()
        val authors = voiceoverPageDocument.select("[itemprop=author] a")
            .map { it.text().trim() }
            .filter { it.isNotBlank() }
        val coverUrl = voiceoverPageDocument.select(".book_cover img").attr("src")
        // TODO: Add Series parsing
        return ParsedVoiceoverBookMetadata(
            source = source,
            title = title,
            authors = authors,
            series = null,
            seriesIndex = 0,
            coverUrl = coverUrl,
            relatedVoiceoverId = internalVoiceoverId
        )
    }

    override suspend fun getVoiceover(internalVoiceoverId: String): ParsedVoiceover {
        val voiceoverUrl = "${source.baseUrl}/book/$internalVoiceoverId/"
        val voiceoverDocument = Jsoup.connect(voiceoverUrl)
            .userAgent("Chrome/4.0.249.0 Safari/532.5")
            .referrer("http://www.google.com")
            .get()

        val scripts = voiceoverDocument.getElementsByTag("script")

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

        val readers = voiceoverDocument.select(".book_title_block")
            .select("a")
            .filter { it.attr("href").contains("reader") }
            .map { it.text() }

        val voiceover = ParsedVoiceover(
            readers = readers,
            mediaItems = mediaItems,
            source = source,
            internalId = internalVoiceoverId
        )

        return voiceover
    }

    override suspend fun getAlternativeVoiceovers(internalVoiceoverId: String): List<ParsedVoiceover> =
        coroutineScope {
            val bookPageDocument = getCachedVoiceoverDocumentIfPresent(internalVoiceoverId)

            //todo redo search by matchesOwn
            val cycle = bookPageDocument.selectFirst(":matchesOwn(^\\s*Цикл)")?.parent()
                ?.select("a[href]")?.map { it.text().trim() to it.absUrl("href") } ?: emptyList()
            Log.e("asd cycle", cycle.toString())

            //todo redo search by containsOwn
            val otherVoiceoversIds = bookPageDocument.selectFirst(":containsOwn(Другие озвучки)")
                ?.parent()
                ?.select("a")
                ?.map { it.attr("href") }
                ?.filter { it.contains("book") }
                ?.map { it.split("/")[2] }

            Log.e("asd other voice", otherVoiceoversIds.toString())
            val otherVoiceovers = otherVoiceoversIds?.let { otherVoiceoversIds ->
                otherVoiceoversIds.map {
                    async {
                        getVoiceover(it)
                    }
                }.awaitAll()
            }

            return@coroutineScope otherVoiceovers ?: emptyList()
        }

    override suspend fun getBooksInCycle(internalBookId: String): List<ParsedVoiceoverBookMetadata> {
        TODO("Not yet implemented")
    }

    private fun parseBookList(document: Document): List<ParsedVoiceoverBookMetadata> {
        val bookItems = document.select(".bookkitem")

        return bookItems.map { bookItem ->
            val authors = bookItem.select(".bookkitem_author")
                .select("a")
                .map { it.text() }
                .toList()

            val bookItemMeta = bookItem.select(".bookkitem_meta")

            val title = bookItem.select("a.bookkitem_name").text()
            val internalVoiceoverId = bookItem.select("a.bookkitem_name")
                .attr("href")
                .split("/")[2]

            val cover = bookItem.select(".bookkitem_cover_img").attr("src")
            val series = bookItemMeta.select(".-serie").next().select("a").text()

            ParsedVoiceoverBookMetadata(
                source = source,
                title = title,
                authors = authors,
                series = series,
                seriesIndex = null,
                coverUrl = cover,
                relatedVoiceoverId = internalVoiceoverId,
            )
        }
    }

    private fun getCachedVoiceoverDocumentIfPresent(internalVoiceoverId: String): Document {
        val cacheDocument = bookDocumentCache[internalVoiceoverId]
        return if (cacheDocument == null) {
            val url = "${source.baseUrl}/book/$internalVoiceoverId/"
            val bookPageDocument = Jsoup.connect(url)
                .userAgent("Chrome/4.0.249.0 Safari/532.5")
                .referrer("http://www.google.com")
                .get()
            bookDocumentCache[internalVoiceoverId] = bookPageDocument
            bookPageDocument
        } else {
            cacheDocument
        }
    }
}

private data class PlayerParseResult(
    val id: Int,
    val title: String,
    val url: String,
    val error: Int,
    val duration: Long,
)