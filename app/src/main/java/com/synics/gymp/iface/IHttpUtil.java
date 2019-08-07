package com.synics.gymp.iface;

import java.util.Map;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by Administrator on 2016/10/27.
 */

public interface IHttpUtil {
    @GET() Call<String> get(@Url String url, @QueryMap Map<String, String> params);

    @FormUrlEncoded @POST() Call<String> post(@Url String url,
                                              @FieldMap Map<String, String> params);

    @GET() Observable<String> Obget(@Url String url, @QueryMap Map<String, String> params);

    @FormUrlEncoded @POST() Observable<String> Obpost(@Url String url,
                                                      @FieldMap Map<String, String> params);

    @Headers({ "Content-type:application/json;charset=UTF-8" }) @POST() Call<String> post(
            @Url String url, @Body RequestBody route);
}
