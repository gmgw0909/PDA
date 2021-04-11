package com.vip.pda.http;

import com.vip.pda.App;
import com.vip.pda.http.cookie.CookieJarImpl;
import com.vip.pda.http.cookie.store.PersistentCookieStore;
import com.vip.pda.http.interceptor.CacheInterceptor;
import com.vip.pda.http.interceptor.logging.Level;
import com.vip.pda.http.interceptor.logging.LoggingInterceptor;
import com.vip.pda.http.service.ApiService;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.internal.platform.Platform;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * RetrofitClient封装单例类, 实现网络请求
 */
public class RetrofitClient {
    //超时时间
    private static final int DEFAULT_TIMEOUT = 20;
    //缓存时间
    private static final int CACHE_TIMEOUT = 10 * 1024 * 1024;

    private static OkHttpClient okHttpClient;
    private static Retrofit retrofit;

    private Cache cache = null;
    private File httpCacheDirectory;

    private RetrofitClient() {
        if (httpCacheDirectory == null) {
            httpCacheDirectory = new File(App.getInstance().getCacheDir(), "vip_cache");
        }
        try {
            if (cache == null) {
                cache = new Cache(httpCacheDirectory, CACHE_TIMEOUT);
            }
        } catch (Exception e) {
//            Log.e("RetrofitClient", "Could not create http cache" + ExceptionUtils.getRootCauseMessage(e));
        }
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory();
        okHttpClient = new OkHttpClient.Builder()
                .cookieJar(new CookieJarImpl(new PersistentCookieStore(App.getInstance())))
//                .cache(cache)
                .addInterceptor(new CacheInterceptor(App.getInstance()))
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .addInterceptor(new LoggingInterceptor
                        .Builder()//构建者模式
                        .loggable(true) //是否开启日志打印
                        .setLevel(Level.BASIC) //打印的等级
                        .log(Platform.INFO) // 打印类型
                        .request("OkHttp") // request的Tag
                        .response("OkHttp")// Response的Tag
                        .addHeader("log-header", "I am the log request header.")
                        .build()
                )
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(8, 15, TimeUnit.SECONDS))
                // 这里你可以根据自己的机型设置同时连接的个数和时间，我这里8个，和每个保持时间为10s
                .build();
        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("http://cs.51qqt.com/ELSServer_GUJIA/rest/")
                .build();

    }

    /**
     * create you ApiService
     * Create an implementation of the API endpoints defined by the {@code service} interface.
     */
    public <T> T create(final Class<T> service) {
        if (service == null) {
            throw new RuntimeException("Api service is null!");
        }
        return retrofit.create(service);
    }

    private static RetrofitClient retrofitClient;
    private static ApiService apiService;

    public static OkHttpClient getOkHttpClient() {
        if (retrofitClient == null) {
            retrofitClient = new RetrofitClient();
        }
        return okHttpClient;
    }

    public static ApiService getApiService() {
        if (retrofitClient == null) {
            retrofitClient = new RetrofitClient();
        }
        if (apiService == null) {
            apiService = retrofitClient.create(ApiService.class);
        }
        return apiService;
    }

    /**
     * refresh token
     */
    public static void reCreate() {
        retrofitClient = new RetrofitClient();
        apiService = retrofitClient.create(ApiService.class);
    }
}
