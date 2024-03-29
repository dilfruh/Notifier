package com.dil.notifier

data class vibrationData(
    val name: String,
    val pattern: LongArray,
) {
    // These were suggested and autogenerated
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as vibrationData

        if (name != other.name) return false
        return pattern.contentEquals(other.pattern)
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + pattern.contentHashCode()
        return result
    }
}
