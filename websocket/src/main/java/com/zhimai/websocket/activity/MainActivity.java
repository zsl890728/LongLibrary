package com.zhimai.websocket.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.zhimai.websocket.R;
import com.zhimai.websocket.util.MsgWebSocketUtil;

/**
 * 这里只是启动socket的示例，也可以放全局或者其它地方启动
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //开启消息长连接 websocket
        MsgWebSocketUtil.getInstance().initSocket();
    }
}
