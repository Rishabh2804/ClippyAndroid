package rish.dev.android.clippy

import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.lifecycle.android.AndroidLifecycle
import com.tinder.scarlet.streamadapter.rxjava2.RxJava2StreamAdapterFactory
import com.tinder.scarlet.websocket.okhttp.newWebSocketFactory
import okhttp3.OkHttpClient
import org.junit.Test

import org.junit.Assert.*
import rish.dev.android.clippy.service.MyWebSocketService
import rish.dev.android.clippy.util.SOCKET_URL

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun connectToSocket(){
//        val okHttpClient = OkHttpClient.Builder().build()
//        val lifecycle = AndroidLifecycle.ofApplicationForeground(application)
//        val scarletInstance = Scarlet.Builder()
//            .webSocketFactory(okHttpClient.newWebSocketFactory(SOCKET_URL))
//            .addStreamAdapterFactory(RxJava2StreamAdapterFactory())
//            .lifecycle(lifecycle)
//            .build()
//
//        var myWebSocketService : MyWebSocketService= scarletInstance.create()

    }
}