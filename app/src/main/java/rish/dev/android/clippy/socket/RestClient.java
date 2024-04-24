package rish.dev.android.clippy.socket;

import static rish.dev.android.clippy.util.ConfigKt.BASE_URL;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rish.dev.android.clippy.data.ClippyRepo;

public class RestClient {

    private static RestClient instance;
    private static final Object lock = new Object();

    public static RestClient getInstance() {
        RestClient instance = RestClient.instance;
        if (instance == null) {
            synchronized (lock) {
                instance = RestClient.instance;
                if (instance == null) {
                    RestClient.instance = instance = new RestClient();
                }
            }
        }
        return instance;
    }

    private final ClippyRepo mExampleRepository;

    private RestClient() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        mExampleRepository = retrofit.create(ClippyRepo.class);
    }

    public ClippyRepo getExampleRepository() {
        return mExampleRepository;
    }
}