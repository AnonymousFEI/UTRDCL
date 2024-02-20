package com.moran.hotfixdemo.util;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

/**
 * @FunctionName NetworkUtil
 * @Author name
 * @Date 3/21/23
 * @Description
 */
public interface NetworkUtil {
//    @Headers({
//            "Cache-Control: max-age=640000",
//            "User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko"
//    })
    @GET("/{filename}")
    Call<ResponseBody> getFileString(@Path("filename") String fileName);
}
