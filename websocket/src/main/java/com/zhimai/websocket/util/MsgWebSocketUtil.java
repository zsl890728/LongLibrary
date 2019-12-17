package com.zhimai.websocket.util;

import android.util.Log;

import com.zhimai.websocket.BuildConfig;
import com.zhimai.websocket.Config;
import com.zhimai.websocket.RxWebSocket;
import com.zhimai.websocket.RxWebSocketUtil;
import com.zhimai.websocket.WebSocketSubscriber;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import io.reactivex.annotations.NonNull;
import okhttp3.WebSocket;
import okio.ByteString;

/**
 * 打印消息websocket工具
 */
public class MsgWebSocketUtil {
    private static final String TAG = "---MsgWebSocketUtil----";
    private static final String pingData = "{\"type\":\"ping\",\"data\":\"\"}";
    private static MsgWebSocketUtil msgWebSocketUtil;
    private WebSocket mWebSocket;
    private boolean isConnected = false;

    private MsgWebSocketUtil() {
    }

    public static MsgWebSocketUtil getInstance() {
        if (msgWebSocketUtil == null) {
            msgWebSocketUtil = new MsgWebSocketUtil();
        }
        return msgWebSocketUtil;
    }

    public void initSocket(String websocket_url) {
        //是否开启日志
        RxWebSocketUtil.getInstance().setShowLog(BuildConfig.DEBUG);

        //初始化ping定时器
        initPing();
        // ping/pong 设置：在设置config的时候，从okhttpclient中配置，设置5秒的心跳
//        OkHttpClient client = new OkHttpClient.Builder()
//                .connectTimeout(5, TimeUnit.SECONDS)
//                .readTimeout(3,TimeUnit.SECONDS)
//                .writeTimeout(3,TimeUnit.SECONDS)
//                .pingInterval(3, TimeUnit.SECONDS)
//                .build();
        Config config = new Config.Builder()
                .setShowLog(true)           //show  log
//                .setClient(client)   //if you want to set your okhttpClient
                .setShowLog(true, TAG)
                .setReconnectInterval(2, TimeUnit.SECONDS)  //set reconnect interval
//                .setSSLSocketFactory(yourSSlSocketFactory, yourX509TrustManager) // wss support
                .build();
        RxWebSocket.setConfig(config);

        Log.e(TAG, "开启长连接；；" + websocket_url);
//        RxWebSocket.get(MyApplication.WEBSOCKET_HOST_AND_PORT)
        RxWebSocket.get(websocket_url)
//        RxWebSocket.get("ws://qmapp.dev.zmcms.cn:8282")
                //RxLifecycle : https://github.com/dhhAndroid/RxLifecycle
//                .compose(RxLifecycle.with(MyApplication.mContext).<WebSocketInfo>bindToLifecycle())//如果需要跟activity生命绑定，防止内存泄漏，可以用这个方法
                .subscribe(new WebSocketSubscriber() {
                    @Override
                    public void onOpen(@NonNull WebSocket webSocket) {
                        Log.e(TAG, "onOpen1:");
                        mWebSocket = webSocket;
                        isConnected = true;
                    }

                    @Override
                    public void onMessage(@NonNull String text) {
                        Log.e(TAG, "onMessage;;" + text);
                        checkMessage(text);
                    }

                    @Override
                    public void onMessage(@NonNull ByteString bytes) {
                        Log.e(TAG, bytes.toString());
                    }

                    @Override
                    protected void onReconnect() {
                        Log.e(TAG, "重连");
                        isConnected = false;
                    }

                    @Override
                    protected void onClose() {
                        Log.e(TAG, "关闭");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "错误：" + e.getMessage());
                    }
                });
    }

    /**
     * 处理分发接收到的消息
     * 根据接收的消息，进行相应的处理，这里随便写的，要根据自己定的规则进行处理
     */
    private void checkMessage(String message) {
        try {
            JSONObject jsonObject = new JSONObject(message);
            String type = jsonObject.optString("type");
            switch (type) {
                case "init"://初始化/验证
                    sendBind(jsonObject);
                    break;
                case "success"://验证成功

                    break;
                case "error"://验证失败

                    break;
                case "******"://根据不同反馈信息进行相应处理，这个就是自己定的规则了

                    break;
                case "￥￥￥￥￥￥"://根据不同反馈信息进行相应处理，这个就是自己定的规则了

                    break;
            }
        } catch (JSONException e) {

        }
    }

    /**
     * 发送绑定/验证 请求
     */
    private void sendBind(JSONObject jsonObject) {
        try {
            Log.e(TAG, "sendBind()被调用");
            String devicecode = "123456";
            JSONObject bindJson = new JSONObject();
            bindJson.put("type", "bind");
            bindJson.put("devicecode", devicecode);

            if (mWebSocket != null) {
                Log.e(TAG, "发送绑定数据：" + bindJson.toString());
                mWebSocket.send(bindJson.toString());
            }
        } catch (Exception e) {
            Log.e(TAG, "sendBind 错误:" + e.getMessage());
        }
    }

    /**
     * 开始定时发送心跳
     * 这个是监测 连接是否正常的规则，可自定义
     */
    private void initPing() {
        RxTimerUtil.cancel(SysCode.RX_TIMER_TYPE.SOCKET);
        RxTimerUtil.interval(SysCode.RX_TIMER_TYPE.SOCKET, 5000, new RxTimerUtil.IRxNext() {
            @Override
            public void doNext(long number) {
                if (isConnected) {
                    if (mWebSocket != null) {
                        boolean isPing = mWebSocket.send(pingData);
                        Log.e(TAG, isPing ? "ping..." : "ping失败...");
                    }
                } else {
                    Log.e(TAG, "Socket中断，停止ping...");
                }
            }
        });
    }
}
