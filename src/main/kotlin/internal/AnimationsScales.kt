package internal

const val scaleValueOne = 1.0F
const val scaleValueZero = 0.0F

val animationScales = linkedMapOf(
        "window_animation_scale" to scaleValueOne,
        "transition_animation_scale" to scaleValueOne,
        "animator_duration_scale" to scaleValueOne
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