package rish.dev.android.clippy.socket

import android.util.Log
import com.google.gson.Gson
import io.reactivex.Completable
import io.reactivex.CompletableTransformer
import io.reactivex.FlowableSubscriber
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import rish.dev.android.clippy.model.Clip
import rish.dev.android.clippy.util.POST_CLIP_TEXT
import rish.dev.android.clippy.util.SOCKET_URL
import rish.dev.android.clippy.util.TOPIC_CLIP_TEXT
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent


class SocketManager {

    private val gson: Gson = Gson()
    private var stompClient: StompClient? = null
    private var compositeDisposable: CompositeDisposable? = null

    init {
        initConnection()
    }

    private fun initConnection() {
        stompClient = Stomp.over(Stomp.ConnectionProvider.JWS, SOCKET_URL)
        setupDisposables()
        subscribeLifecycle()
        Log.d("SocketManager", "SocketManager created at URL : $SOCKET_URL")
    }

    private fun setupDisposables() {
        if (compositeDisposable == null) compositeDisposable = CompositeDisposable()
    }

    fun connect(onMessage: (Clip) -> Unit = {}) {
        if (stompClient == null) initConnection()

        try {
            stompClient?.withClientHeartbeat(1000)?.withServerHeartbeat(1000)

            setupDisposables()

            subscribeLifecycle()
            subscribeToTopic(TOPIC_CLIP_TEXT, onMessage)

            stompClient?.connect()
        } catch (e: Exception) {
            Log.e("SocketManager", "Error connecting to socket", e)
        }
    }

    private fun subscribeLifecycle() {

        try {
            val flowableLifecycle = object : FlowableSubscriber<LifecycleEvent> {
                override fun onSubscribe(s: org.reactivestreams.Subscription) {
                    s.request(1)
                }

                override fun onNext(t: LifecycleEvent?) {
                    Log.d("SocketManager", "Lifecycle event: $t")
                }

                override fun onError(t: Throwable) {
                    Log.e("SocketManager", "Error in lifecycle: $t", t)
                }

                override fun onComplete() {
                    Log.d("SocketManager", "Lifecycle completed")
                }
            }

//            stompClient?.lifecycle()?.subscribe(flowableLifecycle)
        } catch (e: Exception) {
            Log.e("SocketManager", "Error subscribing to lifecycle", e)
        }
    }

    fun subscribeToTopic(topic: String, onMessage: (Clip) -> Unit = {}) {
        try {
            val disposable = stompClient?.topic(topic)
                ?.subscribe { topicMessage ->
                    Log.d(TAG, topicMessage.payload)

                    val receivedClip = fromJson(topicMessage.payload)
                    onMessage(receivedClip)
                } ?: run {
                Log.e(TAG, "Error subscribing to topic")
                return
            }

            compositeDisposable?.add(disposable)
        } catch (e: Exception) {
            Log.e(TAG, "Error subscribing to topic", e)
        }
    }

    fun sendMessage(message: Clip) {
        try {
            val sendingDisposable = stompClient?.send(POST_CLIP_TEXT, message.toJson())
                ?.compose(applySchedulers())
                ?.subscribe({
                    Log.d(TAG, "STOMP echo send successfully");
                }, {
                    Log.e(TAG, "Error send STOMP", it)
                }) ?: run {
                Log.e(TAG, "Error sending message")
                return
            }

            Log.d(TAG, "Sending message: ${message.toJson()}")
            compositeDisposable?.add(sendingDisposable)

        } catch (e: Exception) {
            Log.e(TAG, "Error sending message", e)
        }
    }

    fun closeWebSocket() {
        stompClient?.disconnect()
        stompClient = null

        compositeDisposable?.dispose()
        compositeDisposable = null
    }

    private fun applySchedulers(): CompletableTransformer? {
        return CompletableTransformer { upstream: Completable ->
            upstream
                .unsubscribeOn(Schedulers.newThread())
//                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        }
    }

    private fun Clip.toJson(): String {
        return gson.toJson(this)
    }

    private fun fromJson(json: String): Clip {
        return gson.fromJson(json, Clip::class.java)
    }

    companion object {
        private const val TAG = "SocketManager"
    }
}