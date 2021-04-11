package com.vip.pda.http.interceptor;

import android.text.TextUtils;

import com.pnlyy.pnlclass.ai_practice.utils.SPKey;
import com.pnlyy.pnlclass.ai_practice.utils.SPManager;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class TokenInterceptor implements Interceptor {
    private Map<String, String> headers;

    public TokenInterceptor(Map<String, String> headers) {
        this.headers = headers;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request()
                .newBuilder();
        if (headers != null && headers.size() > 0) {
            Set<String> keys = headers.keySet();
            for (String headerKey : keys) {
                builder.addHeader(headerKey, headers.get(headerKey)).build();
            }
        }
        //处理Token
        String token = SPManager.getInstance().getString(SPKey.TOKEN);
        if (!TextUtils.isEmpty(token)) {
            builder.addHeader("Jwt-Token", token).build();
        }

//        builder.addHeader("memory", BatteryUtils.getAppMemory() + "MB");//内存使用
//        builder.addHeader("cpu", PerformanceDataManager.executeCpuData() + "%");//cpu使用率
//        builder.addHeader("battery", BatteryUtils.getBatteryPower(Utils.getContext()) + "%");//电量
//        builder.addHeader("network", NetworkUtil.getNetworkType(Utils.getContext()));//网络类型
        return chain.proceed(builder.build());
    }
}