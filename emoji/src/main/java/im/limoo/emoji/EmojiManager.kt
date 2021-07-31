package im.limoo.emoji

import android.content.Context
import android.text.Spannable
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import im.limoo.emoji.model.Emoji
import im.limoo.emoji.view.EmojiSpan
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*
import java.util.regex.Pattern

object EmojiManager {

    val peopleEmojis: List<Emoji> by lazy {
        ALL_EMOJIS.filter { it.category == "Smileys & People" }.sortedBy { it.sort_order }
    }

    val natureEmojis: List<Emoji> by lazy {
        ALL_EMOJIS.filter { it.category == "Animals & Nature" }.sortedBy { it.sort_order }
    }
    val carEmojis: List<Emoji> by lazy {
        ALL_EMOJIS.filter { it.category == "Travel & Places" }.sortedBy { it.sort_order }
    }
    val foodEmojis: List<Emoji> by lazy {
        ALL_EMOJIS.filter { it.category == "Food & Drink" }.sortedBy { it.sort_order }
    }
    val sportEmojis: List<Emoji> by lazy {
        ALL_EMOJIS.filter { it.category == "Activities" }.sortedBy { it.sort_order }
    }
    val objectsEmojis: List<Emoji> by lazy {
        ALL_EMOJIS.filter { it.category == "Objects" }.sortedBy { it.sort_order }
    }

    val symbolEmojis: List<Emoji> by lazy {
        ALL_EMOJIS.filter { it.category == "Symbols" }.sortedBy { it.sort_order }
    }

    val flagsEmojis: List<Emoji> by lazy {
        ALL_EMOJIS.filter { it.category == "Flags" }.sortedBy { it.sort_order }
    }

    private val EMOJIS_BY_SHORT_NAMES by lazy {
        val aliasMap = HashMap<String, Emoji>()
        ALL_EMOJIS.forEach { emoji ->
            emoji.short_names?.forEach { alias ->
                aliasMap[alias] = emoji
            }
        }
        aliasMap
    }

    private val ALL_EMOJIS by lazy {
        ArrayList<Emoji>()
    }

    private const val PATH = "emoji_pretty.json"

    /**
     * Initializes emoji objects from an asset file in the library directory
     *
     * @param context any valid application context
     * @throws Exception may throw but not limited to [java.io.IOException] when an error occurs
     */
    @Throws(Exception::class)
    fun initEmojiData(context: Context) {
        if (ALL_EMOJIS.isEmpty()) {
            val gson = GsonBuilder()
                    .enableComplexMapKeySerialization()
                    .setLenient().create()
            InputStreamReader(context.assets.open(PATH)).use { streamReader ->
                BufferedReader(streamReader).use {
                    ALL_EMOJIS.apply {
                        addAll(gson.fromJson(it, object : TypeToken<ArrayList<Emoji>>() {}.type))
                        val iterator = iterator()
                        while (iterator.hasNext()) {
                            val emoji = iterator.next()
                            val initProperties = emoji.initProperties(context)
                            if (initProperties == 0) iterator.remove()
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns the [Emoji] for a given alias.
     *
     * @param alias the alias
     *
     * @return the associated [Emoji], null if the alias
     * is unknown
     */
    private fun getForShortName(alias: String?): Emoji? {
        return alias?.let {
            EMOJIS_BY_SHORT_NAMES[trimAlias(it)]
        }
    }

    private fun trimAlias(alias: String): String {
        var result = alias
        if (result.startsWith(":")) {
            result = result.substring(1, result.length)
        }
        if (result.endsWith(":")) {
            result = result.substring(0, result.length - 1)
        }
        return result
    }

    /**
     * Returns all the [Emoji]s
     *
     * @return all the [Emoji]s
     */
    fun getAll(): List<Emoji> {
        return ALL_EMOJIS
    }

    /**
     * Convert emoji characters of the given Spannable to the according emojicon.
     */
    fun addEmojis(context: Context, text: Spannable, emojiSize: Int, emojiAlignment: Int, textSize: Int, useSystemDefault: Boolean) {

        if (useSystemDefault) {
            return
        }

        val textLength = text.length

        // remove spans throughout all text
        val oldSpans = text.getSpans(0, textLength, EmojiSpan::class.java)
        for (i in oldSpans.indices) {
            text.removeSpan(oldSpans[i])
        }

        val pattern = Pattern.compile(":\\+?(\\w|\\||-)+:")
        val matcher = pattern.matcher(text)
        while (matcher.find()) {
            val shortName = matcher.group()
            val emoji = getForShortName(shortName)
            if (emoji != null)
                text.setSpan(EmojiSpan(context,
                        emoji.imageResourceId,
                        emojiSize, emojiAlignment, textSize), matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }
}
