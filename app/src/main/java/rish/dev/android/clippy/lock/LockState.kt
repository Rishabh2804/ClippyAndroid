package rish.dev.android.clippy.lock

sealed class LockState {
    data object Open : LockState() {
        override fun <T> tryUnlock(key: T) = AttemptResult.Clear
    }

    data class Locked<T>(val key: T) : LockState() {
        override fun <T> tryUnlock(key: T): AttemptResult {
            return when (this.key) {
                key -> AttemptResult.Success
                else -> AttemptResult.Failure
            }
        }
    }

    abstract fun <T> tryUnlock(key: T): AttemptResult

    override fun toString(): String {
        return when (this) {
            is Open -> "Open"
            is Locked<*> -> "Locked"
        }
    }
}

sealed class LockException(message: String) : Exception(message) {
    data object KeyMismatchException : LockException("Invalid key")
    data object Unlocked : LockException("Resource was locked.")
}
