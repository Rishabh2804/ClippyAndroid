package rish.dev.android.clippy.lock

sealed class AttemptResult(val outcome: LockException? = null) {

    data object Success: AttemptResult(outcome = LockException.Unlocked)
    data object Failure: AttemptResult(outcome = LockException.KeyMismatchException)

    data object Clear : AttemptResult()
}
