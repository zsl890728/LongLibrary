package com.slzhang.downfile;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.tsy.sdk.myokhttp.response.DownloadResponseHandler;

import java.io.File;

/**
 * 下载工具入口
 */
public class DownloadUtil {
    private static final String TAG = "DownloadUtil";
    private static DownloadUtil downloadUtil;
    private static Context mContext;
    private boolean isUpdating = false;
    private static String savedirec = "";

    public DownloadUtil() {

    }

    public DownloadUtil(Context context) {
        mContext = context;
    }

    public static DownloadUtil getInstance(Context context) {
        if (downloadUtil == null) {
            downloadUtil = new DownloadUtil(context);
        }
        savedirec = mContext.getCacheDir() + "/";
        return downloadUtil;
    }

    /**
     * 外部调用 入口
     */
    public void download(){

    }

    /**
     * 创建文件夹
     * Android平台中，能操作文件夹的只有两个地方：
     * sdcard
     * data/data/<package-name>/files
     */
    private void downloadOnece(final String url) {
        String file_name = url.substring(url.lastIndexOf("/"));
        final String filename = System.currentTimeMillis() / 1000 + "" + file_name;
        Log.e(TAG, "savePath+filename= " + savedirec + filename);
        File file = new File(savedirec, filename);
        //判断目标文件所在的目录是否存在
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                isUpdating = false;
                e.printStackTrace();
                Log.e(TAG, "创建目标文件所在目录失败!" + e.getMessage());
                return;
            }
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                downLoadFile(url, filename);
            }
        }, 10000);
    }

    /**
     * 下载APK
     */
    private void downLoadFile(String url, final String filename) {
        HttpUtil.getInstance().download()
                .url(url)
//                .filePath(Environment.getExternalStorageDirectory() + "/nxapk/"+apk_name+".apk")
//                .filePath(Environment.DIRECTORY_DOWNLOADS + "/nxapk/"+apk_name+".apk")
                .filePath(savedirec + filename)
                .tag(this)
                .enqueue(new DownloadResponseHandler() {
                    @Override
                    public void onStart(long totalBytes) {
                        Log.e(TAG, "doDownload onStart");
                    }

                    @Override
                    public void onFinish(File downloadFile) {
                        Log.e(TAG, "doDownload onFinish:");

                    }

                    @Override
                    public void onProgress(long currentBytes, long totalBytes) {
                        Log.e(TAG, "doDownload onProgress:" + currentBytes + "/" + totalBytes);
                    }

                    @Override
                    public void onFailure(String error_msg) {
//                        isUpdating=false;//安装失败 重置升级状态
                        Log.e(TAG, "doDownload onFailure:" + error_msg);
                        isUpdating = false;
                    }
                });
    }

    public interface MyDownloadListener{

    }
}
