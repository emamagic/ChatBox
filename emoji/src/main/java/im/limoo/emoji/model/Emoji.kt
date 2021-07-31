package im.limoo.emoji.model

import android.content.Context


data class Emoji(
        val name: String? = null,
        val short_names: List<String>? = null,
        var short_name: String,
        val unified: String? = null,
        var image: String? = null,
        var unicode: String? = null,
        var imageResourceId: Int = 0,
        val category: String? = null,
        val sort_order: Int = 0
) {
    constructor(name: String) : this(short_name = name)

    internal fun initProperties(context: Context): Int {
        short_name = ":$short_name:"
        imageResourceId = context.resources.getIdentifier(
                "emoji_" + image?.replace(".png", "")?.replace("-", "_"),
                "drawable",
                context.packageName)
        return imageResourceId
    }

    override fun equals(other: Any?): Boolean {
        return !(other == null || other !is Emoji) && other.unified == unified
    }

    override fun hashCode(): Int {
        return unified.hashCode()
    }


    /**
     * Returns the String representation of the Emoji object.<br></br>
     * <br></br>
     * Example:<br></br>
     * `Emoji {
     * name='smiling face with open mouth and smiling eyes',
     * supportsFitzpatrick=false,
     * short_names=[smile],
     * tags=[happy, joy, pleased],
     * unicode='😄',
     * htmlDec='&#128516;',
     * htmlHex='&#x1f604;'
     * }`
     *
     * @return the string representation
     */
    override fun toString(): String {
        return "Emoji{" +
                "name='" + name + '\''.toString() +
                ", short_names=" + short_names +
                '}'.toString()
    }
}