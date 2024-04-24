package rish.dev.android.clippy.util

const val PORT = 8081
const val SOCKET_ROOT = "clippyboot"
const val POST_CLIP_TEXT = "/app/text"
const val TOPIC_CLIP_TEXT = "/clips/text"

const val LOCALHOST = "127.0.0.1"
const val REMOTE = "192.168.1.5"

const val BASE_URL = "ws://$REMOTE:$PORT"

const val SOCKET_URL = "$BASE_URL/$SOCKET_ROOT"