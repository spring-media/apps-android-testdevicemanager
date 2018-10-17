package internal

enum class UnlockMethods(val string: String) {
    POWER_BUTTON("power button"),
    SWIPE("swipe"),
    PIN("pin"),
    PASSWORD("password")
}

enum class StayAwakeStatus(val value: Int) {
    STAY_AWAKE(2),
    STAY_NOT_AWAKE(0)
}

enum class SetAnimationsStatus {
    ENABLE_ANIMATIONS,
    DISABLE_ANIMATIONS
}

enum class LockStatus {
    LOCK_DEVICE,
    UNLOCK_DEVICE
}