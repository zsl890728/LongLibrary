package com.tsy.sdk.myokhttp.response;

import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Types;
import com.tsy.sdk.myokhttp.MyOkHttp;
import com.tsy.sdk.myokhttp.model.BaseData;
import com.tsy.sdk.myokhttp.util.LogUtils;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Gson类型的回调接口
 * Created by tsy on 16/8/15.
 */
public abstract class GsonResponseHandler<T> implements IResponseHandler {

    private Type mType;

    public GsonResponseHandler() {
        Type myclass = getClass().getGenericSuperclass();    //反射获取带泛型的class
        if (myclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameter = (ParameterizedType) myclass;      //获取所有泛型
        mType = $Gson$Types.canonicalize(parameter.getActualTypeArguments()[0]);  //将泛型转为type
    }

    private Type getType() {
        return mType;
    }

    @Override
    public final void onSuccess(final Response response) {
        ResponseBody responseBody = response.body();
        String responseBodyStr = "";

        try {
            responseBodyStr = responseBody.string();
        } catch (IOException e) {
            e.printStackTrace();
            LogUtils.e("onResponse fail read response body");
            MyOkHttp.mHandler.post(new Runnable() {
                @Override
                public void run() {
//                    onFailure(response.code(), "fail read response body");
//                    onFailure(response.code(), "fail read response body");
                    onFailure(response.code(), "服务器返回内容异常，读取失败");
                }
            });
            return;
        } finally {
            responseBody.close();
        }

        final String finalResponseBodyStr = responseBodyStr;

        try {
            Gson gson = new Gson();
            final BaseData baseData = gson.fromJson(finalResponseBodyStr,BaseData.class);

            if (!baseData.isStatus()){

                MyOkHttp.mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(baseData.getCode().length()>9){
                            onFailure(Integer.parseInt(baseData.getCode().substring(baseData.getCode().length()-3)), baseData.getMessage());
                        }else{
                            onFailure(Integer.parseInt(baseData.getCode()), baseData.getMessage());
                        }
                    }
                });
                return;
            }


            final T gsonResponse = (T) gson.fromJson(finalResponseBodyStr, getType());
            MyOkHttp.mHandler.post(new Runnable() {
                @Override
                public void run() {
                    onSuccess(response.code(), gsonResponse);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e("onResponse fail parse gson, body=" + finalResponseBodyStr);
            MyOkHttp.mHandler.post(new Runnable() {
                @Override
                public void run() {
//                    onFailure(response.code(), "fail parse gson, body=" + finalResponseBodyStr);
                    onFailure(response.code(), "数据异常");
                }
            });

        }
    }

    public abstract void onSuccess(int statusCode, T response);

    @Override
    public void onProgress(long currentBytes, long totalBytes) {

    }
}
