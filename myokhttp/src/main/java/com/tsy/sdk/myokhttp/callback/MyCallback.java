package com.tsy.sdk.myokhttp.callback;

import android.accounts.NetworkErrorException;

import com.tsy.sdk.myokhttp.MyOkHttp;
import com.tsy.sdk.myokhttp.response.IResponseHandler;
import com.tsy.sdk.myokhttp.util.LogUtils;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by tsy on 16/9/18.
 */
public class MyCallback implements Callback {

    private IResponseHandler mResponseHandler;

    public MyCallback(IResponseHandler responseHandler) {
        mResponseHandler = responseHandler;
    }

    @Override
    public void onFailure(Call call, final IOException e) {
        LogUtils.e("onFailure", e);

        MyOkHttp.mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(e instanceof SocketTimeoutException){
                    mResponseHandler.onFailure(0, "网络请求超时，请检查网络");
                }else if(e instanceof ConnectException){
                    mResponseHandler.onFailure(0, "网络异常，请检查网络");
                }else if(e instanceof UnknownHostException){
                    mResponseHandler.onFailure(0, "网络请求异常，请检查网络");
                }else{
//                    mResponseHandler.onFailure(0, e.getMessage());
                    mResponseHandler.onFailure(0, "网络出错，请检查网络");
                    LogUtils.e("onFailure",e.toString());
                }
            }
        });
    }

    @Override
    public void onResponse(Call call, final Response response) {
        if(response.isSuccessful()) {
            mResponseHandler.onSuccess(response);
        } else {
            LogUtils.e("onResponse fail status=" + response.code());

            MyOkHttp.mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mResponseHandler.onFailure(response.code(), "fail status=" + response.code());
                }
            });
        }
    }
}
