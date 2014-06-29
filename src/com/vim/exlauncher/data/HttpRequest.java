package com.vim.exlauncher.data;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.text.TextUtils;
import android.util.Log;

public class HttpRequest {
    private static final String TAG = "HttpRequest";

    private static final int CONNECT_TIME_OUT = 10 * 1000;

    public static InputStream getStreamFromUrl(String strUrl) {
        if (TextUtils.isEmpty(strUrl)) {
            Log.d(TAG, "[getStreamFromUrl] url is empty!");
            return null;
        }

        InputStream is = null;
        try {
            URL url = new URL(strUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(CONNECT_TIME_OUT);
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                is = conn.getInputStream();
            }
        } catch (Exception e) {
            Log.e(TAG, "[getStreamFromUrl] error when getting logo on "
                    + strUrl);
            // e.printStackTrace();
            is = null;
        }

        return is;
    }

    public static JSONObject getDataFromUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            Log.e(TAG, "[getDataFromUrl] url is empty!");
            return null;
        }

        logd(url);
        url = url.replaceAll(" ", "%20");
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
            Log.e(TAG, "[getDataFromUrl] error when getting logo on " + url);
            // e.printStackTrace();
            resultJsonObject = null;
        } finally {
            // 关闭连接 ,释放资源
            client.getConnectionManager().shutdown();
        }
        return resultJsonObject;
    }

    private static void logd(String strs) {
        Log.d(TAG, strs + " --- VIM");
    }
}
