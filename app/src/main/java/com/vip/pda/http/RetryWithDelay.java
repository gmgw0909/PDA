package com.vip.pda.http;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * @author: cs
 * @date: 2020/12/16
 * @desc:
 */
public class RetryWithDelay implements
        Function<Observable<? extends Throwable>, Observable<?>> {

    private static final String TAG = "RetryWithDelay";
    private int maxRetries = 1;
    private int retryDelayMillis = 1000;
    private int retryCount;

    public RetryWithDelay(int maxRetries, int retryDelayMillis) {
        this.maxRetries = maxRetries;
        this.retryDelayMillis = retryDelayMillis;
    }

    @Override
    public Observable<?> apply(@NonNull Observable<? extends Throwable> observable) throws Exception {
        return observable
                .flatMap((Function<Throwable, ObservableSource<?>>) throwable -> {
                    if (++retryCount <= maxRetries) {
                        // When this Observable calls onNext, the original Observable will be retried (i.e. re-subscribed).
                        Log.d(TAG, "get error, it will try after " + retryDelayMillis
                                + " millisecond, retry count " + retryCount);
                        return Observable.timer(retryDelayMillis,
                                TimeUnit.MILLISECONDS);
                    }
                    // Max retries hit. Just pass the error along.
                    return Observable.error(throwable);
                });
    }
}
