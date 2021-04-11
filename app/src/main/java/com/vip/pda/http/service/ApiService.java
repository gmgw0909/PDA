package com.vip.pda.http.service;

import com.vip.pda.http.BaseResponse;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
//    @POST("v1/login/getCountryCodeInfo")
//    Observable<BaseResponse<List<CountryCodeResp>>> getCountryCodeInfo();

    @POST("ElsAuthService/login")
    Observable<BaseResponse> login(@Body Map<String, Object> map);
}
