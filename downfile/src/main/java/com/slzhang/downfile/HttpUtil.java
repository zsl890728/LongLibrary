package com.slzhang.downfile;

import com.tsy.sdk.myokhttp.MyOkHttp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class HttpUtil {
    public static MyOkHttp mMyOkhttp;
    public static String headkey="Authorization";
    public static String headvalue="";

    public static MyOkHttp getInstance() {
        if (mMyOkhttp == null) {
            HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLogger());//创建拦截对象
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);//这一句一定要记得写，否则没有数据输出
            OkHttpClient okHttpClient = new OkHttpClient.Builder()


                    .cookieJar(new CookieJar() {

                        private final HashMap<HttpUrl, List<Cookie>> cookieStore = new HashMap<>();

                        @Override
                        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {

                            cookieStore.put(url, cookies);

                        }

                        @Override
                        public List<Cookie> loadForRequest(HttpUrl url) {

                            List<Cookie> cookies = cookieStore.get(url);

                            return cookies != null ? cookies : new ArrayList<Cookie>();
                        }
                    })
                    .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                    .readTimeout(10000L, TimeUnit.MILLISECONDS)
                    .addInterceptor(logInterceptor)  //设置打印拦截日志
//                .addInterceptor(new LogInterceptor())  //自定义的拦截日志，拦截简单东西用，后面会有介绍/////下载打印日志的话，打印内容会超
                    //其他配置
                    .build();
            mMyOkhttp = new MyOkHttp(okHttpClient);
        }
        return mMyOkhttp;
    }

    public static void setHead(String token_type,String access_token){
        headvalue=token_type+" "+access_token;
    }
}
