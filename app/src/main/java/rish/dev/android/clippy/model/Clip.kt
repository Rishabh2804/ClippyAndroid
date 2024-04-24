package rish.dev.android.clippy.model

data class Clip(
    val clipData: String,
    val clipType: ClipType
) {
    override fun toString(): String {
        return "Clip(clipData='$clipData', clipType=$clipType)"
    }
}

enum class ClipType {
    TEXT,
    IMAGE,

    UNKNOWN;

    companion object {
        fun parse(value: Any) : ClipType{
            return when (value) {
                is String -> ClipType.valueOf(value)
                is Int -> parse(value)
                else -> UNKNOWN
            }
        }

        private fun parse(value: Int): ClipType {
            if (value !in 0..<entries.size) return UNKNOWN
            return entries[value]
        }
    }
}