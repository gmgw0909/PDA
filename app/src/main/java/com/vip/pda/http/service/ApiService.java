package com.vip.pda.http.service;

import com.vip.pda.bean.LoginInfo;
import com.vip.pda.http.BaseResponse;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("ElsAuthService/login")
    Observable<BaseResponse<LoginInfo>> login(@Body Map<String, Object> map);

    @POST("order/MaterialStockListService/saveStockDetailList")
    Observable<BaseResponse> saveStockDetailList(@Body List<String> list);

    @POST("order/MaterialStockListService/outStockDetailList")
    Observable<BaseResponse> outStockDetailList(@Body Map<String, Object> map);

    @POST("order/MaterialStockListService/deleteInStockDetailList")
    Observable<BaseResponse> deleteInStockDetailList(@Body List<String> list);

    @POST("order/MaterialStockListService/deleteOutStockDetailList")
    Observable<BaseResponse> deleteOutStockDetailList(@Body List<String> list);

    @POST("order/MaterialStockListService/saveInventoryHead")
    Observable<BaseResponse> saveInventoryHead(@Body Map<String, Object> map);
}
