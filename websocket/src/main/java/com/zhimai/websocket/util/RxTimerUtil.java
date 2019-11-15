package com.zhimai.websocket.util;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 隔时请求刷新
 */
public class RxTimerUtil {
    private static final String TAG = "RxTimerUtil";
    private static Disposable mDisposableTime;
    private static Disposable mDisposableSocket;
    private static Disposable mDisposablepPrint;

    /**
     * milliseconds毫秒后执行next操作
     *
     * @param milliseconds
     * @param next
     */
//    public static void timer(long milliseconds, final IRxNext next) {
//        Observable.timer(milliseconds, TimeUnit.MILLISECONDS)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<Long>() {
//                    @Override
//                    public void onSubscribe(@NonNull Disposable disposable) {
//                        mDisposableTime = disposable;
//                    }
//
//                    @Override
//                    public void onNext(@NonNull Long number) {
//                        if (next != null) {
//                            next.doNext(number);
//                        }
//                    }
//
//                    @Override
//                    public void onError(@NonNull Throwable e) {
//                        //取消订阅
//                        cancel();
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        //取消订阅
//                        cancel();
//                    }
//                });
//    }


    /**
     * 每隔milliseconds毫秒后执行next操作
     *
     * @param milliseconds
     * @param next
     */
    public static void interval(final int which, long milliseconds, final IRxNext next) {
        Observable.interval(milliseconds, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
//                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {
                        switch (which) {
                            case SysCode.RX_TIMER_TYPE.TIME:
                                mDisposableTime = disposable;
                                break;
                            case SysCode.RX_TIMER_TYPE.SOCKET:
                                mDisposableSocket = disposable;
                                break;
                            case SysCode.RX_TIMER_TYPE.PRINT:
                                mDisposablepPrint = disposable;
                                break;
                        }
                    }

                    @Override
                    public void onNext(@NonNull Long number) {
                        if (next != null) {
//                            Logger.e("************",Thread.currentThread().getName());
                            next.doNext(number);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 取消订阅
     */
    public static void cancel(int which) {
        switch (which) {
            case SysCode.RX_TIMER_TYPE.TIME:
                if (mDisposableTime != null && !mDisposableTime.isDisposed()) {
                    mDisposableTime.dispose();
                    Log.e(TAG, "====定时器取消mDisposableTime======");
                }
                break;
            case SysCode.RX_TIMER_TYPE.SOCKET:
                if (mDisposableSocket != null && !mDisposableSocket.isDisposed()) {
                    mDisposableSocket.dispose();
                    Log.e(TAG, "====定时器取消mDisposableSocket======");
                }
                break;
            case SysCode.RX_TIMER_TYPE.PRINT:
                if (mDisposablepPrint != null && !mDisposablepPrint.isDisposed()) {
                    mDisposablepPrint.dispose();
                    Log.e(TAG, "====定时器取消mDisposablepPrint======");
                }
                break;
        }

    }

    public interface IRxNext {
        void doNext(long number);
    }
}
