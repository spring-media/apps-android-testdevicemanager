package internal

val animationScales = linkedMapOf(
        "window_animation_scale" to 1.0F,
        "transition_animation_scale" to 1.0F,
        "animator_duration_scale" to 1.0F
)

fun createAnimationsScalesWithValue(value: Float): LinkedHashMap<String, Float> {
    return linkedMapOf(
            "window_animation_scale" to value,
            "transition_animation_scale" to value,
            "animator_duration_scale" to value
    )
}

fun HashMap<String, Float>.areAllZero(): Boolean {
    this.forEach {
        if (it.value != 0F) return false
    }
    return true
}

fun HashMap<String, Float>.hasNoZeros(): Boolean {
    return !this.values.contains(0F)
}