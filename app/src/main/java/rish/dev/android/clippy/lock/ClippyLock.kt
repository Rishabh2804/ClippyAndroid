package rish.dev.android.clippy.lock

import android.util.Log

class ClippyLock<T>(private val lockName: String) {
    private var lockState: LockState = LockState.Open

    fun lockWith(key: T) {
        lockState = LockState.Locked(key)
    }

    fun unlockWith(key: T): AttemptResult {
        logStatus()
        val unlockResult = lockState.tryUnlock(key)
        when (unlockResult) {
            is AttemptResult.Success -> lockState = LockState.Open
            else -> { /* no-op */ }
        }

        return unlockResult
    }

    fun logStatus() {
        Log.d("ClippyLock", "$lockName: $lockState")
    }
}