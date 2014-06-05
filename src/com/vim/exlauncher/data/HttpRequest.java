package com.vim.exlauncher.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.nfc.Tag;
import android.text.TextUtils;
import android.util.Log;

public class HttpRequest {
    private static final String TAG = "HttpRequest";

    public static JSONObject getDataFromUrl(String url) {
        if (TextUtils.isEmpty(url)){
            Log.e(TAG, "[getDataFromUrl] url is empty!");
            return null;
        }

        logd(url);
        url = url.replaceAll(" ","%20");
        logd(url);

        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(url);
        JSONObject resultJsonObject = null;

        try {
            HttpResponse res = client.execute(get);
            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity httpEntity = res.getEntity();
                StringBuilder entityStringBuilder = new StringBuilder();

                if (httpEntity != null) {
                    BufferedReader bufferedReader = new BufferedReader(
                            new InputStreamReader(httpEntity.getContent(),
                                    "UTF-8"), 8 * 1024);

                    String line = null;
                    while ((line = bufferedReader.readLine()) != null) {
                        entityStringBuilder.append(line);
                    }

                    // 利用从HttpEntity中得到的String生成JsonObject
                    resultJsonObject = new JSONObject(
                            entityStringBuilder.toString());
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "[getDataFromUrl] error!");
            e.printStackTrace();
            resultJsonObject = null;
        } finally {
            // 关闭连接 ,释放资源
            client.getConnectionManager().shutdown();
        }
        return resultJsonObject;
    }

    
    private static void logd(String strs){
        Log.d(TAG, strs + " --- VIM");
    }
}
