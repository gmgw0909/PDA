package com.vip.pda.http;


import com.vip.pda.utils.ToastUtils;

import io.reactivex.observers.DisposableObserver;

/**
 * 统一的Code封装处理。该类仅供参考，实际业务逻辑, 根据需求来定义，
 */

public abstract class ApiDisposableObserver<T> extends DisposableObserver<T> {
    boolean needLoading;

    public ApiDisposableObserver() {
        this(false);
    }

    public ApiDisposableObserver(boolean needLoading) {
        this.needLoading = needLoading;
    }

    public abstract void onResult(T t);

    @Override
    public void onComplete() {

    }

    @Override
    public void onError(Throwable e) {
        ToastUtils.showShort("网络请求失败,请检查网络");
    }

    @Override
    public void onStart() {
        super.onStart();
//        // if  NetworkAvailable no !   must to call onCompleted
//        if (!NetworkUtils.isNetworkAvailable(Utils.getContext())) {
//            LogUtils.d("ApiDisposableObserver", "无网络，读取缓存数据");
//            onComplete();
//        }
    }

    @Override
    public void onNext(T t) {
        onResult(t);
//        BaseResponse baseResponse = (BaseResponse) t;
//        switch (baseResponse.getCode()) {
//            //请求成功, 正确的操作方式
//            case CodeRule.CODE_200:
//                onResult(t);
//                break;
////            //请求失败
////            case CodeRule.CODE_500:
////                ToastUtils.showShort(baseResponse.getMessage());
////                break;
//            //无效的Token，提示跳入登录页
//            case CodeRule.CODE_401:
//            case CodeRule.CODE_1002:
//
//                break;
//            default:
//                ToastUtils.showShort(baseResponse.getMessage());
//                break;
//        }
    }

    public static final class CodeRule {
        //请求成功
        static final int CODE_200 = 200;
        //请求失败
        static final int CODE_500 = 500;
        //有其他设备在登入
        static final int CODE_401 = 401;
        //Token过期
        static final int CODE_1002 = 1002;
    }

}