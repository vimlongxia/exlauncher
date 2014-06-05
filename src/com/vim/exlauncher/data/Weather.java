package com.vim.exlauncher.data;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.util.Log;

public class Weather {
    private static final String TAG = "GetWeather";
    private static Weather mWeatherInstance;

    private static final String WEATHER_SERVICE_URL_PREFIX = "http://php.weather.sina.com.cn/xml.php?city=";
    private static final String WEATHER_SERVICE_URL_POSTFIX = "&password=DJOYnieT8234jlsK&day=";

    private String mWeather;
    private String mWeather2;
    private String mHigh;
    private String mLow;
    private String mWind;
    private String mPollution;

    private Context mContext;
    private String mLatitude;
    private String mLongitude;

    private Weather() {
        
    }
    
    public static Weather getInstance(){
        if (mWeatherInstance == null) {
            mWeatherInstance = new Weather();
        }
        
        return mWeatherInstance;
    }
    
    private static void logd(String strs) {
        Log.d(TAG, strs + " --- VIM");
    }

    public void getWeather(String name, String day) {
        logd("[getWeather] city name : " + name + ", day : " + day);
        
        if (name == null) {
            logd("[getWeather] city name is null!");
            return;
        }
        
        URL ur;

        try {
            DocumentBuilderFactory domfac = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder dombuilder = domfac.newDocumentBuilder();
            Document doc;
            Element root;
            NodeList books;

            ur = new URL(WEATHER_SERVICE_URL_PREFIX
                    + URLEncoder.encode(name, "gb2312")
                    + WEATHER_SERVICE_URL_POSTFIX + day);

            logd("[getWeather] ur : " + ur);

            doc = dombuilder.parse(ur.openStream());
            root = doc.getDocumentElement();
            books = root.getChildNodes();
            if (books == null || books.item(1) == null) {
                logd("[getWeather] books is null!");
                mWeather = null;
                mWeather2 = null;
                mHigh = null;
                mLow = null;
                mWind = null;
                mPollution = null;
                return;
            }

            for (Node node = books.item(1).getFirstChild(); node != null; node = node
                    .getNextSibling()) {
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    if (node.getNodeName().equals("status1"))
                        mWeather = node.getTextContent();
                    else if (node.getNodeName().equals("status2"))
                        mWeather2 = node.getTextContent();
                    else if (node.getNodeName().equals("temperature1"))
                        mHigh = node.getTextContent();
                    else if (node.getNodeName().equals("temperature2"))
                        mLow = node.getTextContent();
                    else if (node.getNodeName().equals("direction1"))
                        mWind = node.getTextContent();
                    else if (node.getNodeName().equals("pollution_s"))
                        mPollution = node.getTextContent();
                }
            }

            logd("mWeather : " + mWeather + ", mWeather2 : " + mWeather2 + ", mLow : " + mLow + ", mHigh : " + mHigh + ", mWind : " + mWind + ", mPollution : " + mPollution);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getCity(String str_province, String str_city,
            String str_area, String str_come_from) {
        URL url;
        URLConnection conn = null;
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        String str = "";

        try {
            url = new URL("http://iframe.ip138.com/ic.asp");
            conn = url.openConnection();
            is = conn.getInputStream();
            isr = new InputStreamReader(is, "gb2312");
            br = new BufferedReader(isr);
            String input = "";
            while ((input = br.readLine()) != null) {
                str += input;
            }

            String content = new String(str.getBytes("UTF-8"), "UTF-8");

            String mCity = null;
            if (content.indexOf(str_province) >= 0
                    && content.indexOf(str_city) >= 0)
                mCity = content.substring(content.indexOf(str_province) + 1,
                        content.indexOf(str_city));
            else
                mCity = null;

            if (mCity == null) {
                if (content.indexOf(str_area) >= 0
                        && content.indexOf(str_city) >= 0)
                    mCity = content.substring(content.indexOf(str_area) + 1,
                            content.indexOf(str_city));
                else
                    mCity = null;
            }

            if (mCity == null) {
                if (content.indexOf(str_come_from) >= 0
                        && content.indexOf(str_city) >= 0)
                    mCity = content.substring(
                            content.indexOf(str_come_from) + 2,
                            content.indexOf(str_city));
                else
                    mCity = null;
            }

            if (mCity == null) {
                mCity = getLocation();
            }

            Log.d(TAG, "   " + "  @@" + mCity + " ##" + content);

            return mCity;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getLocation() { // for globe locating

        URL url;
        URLConnection conn = null;
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        String str = "";

        try {
            url = new URL("http://j.maxmind.com/app/geoip.js");
            conn = url.openConnection();
            is = conn.getInputStream();
            isr = new InputStreamReader(is, "gb2312");
            br = new BufferedReader(isr);
            String input = "";
            while ((input = br.readLine()) != null) {
                str += input;
            }

            String content = new String(str.getBytes("UTF-8"), "UTF-8");

            String[] list = content.split("'");

            for (int i = 0; i < list.length; i++) {

                // Log.d(TAG, "   " + i + " = " + list[i]);
            }

            mLatitude = list[11];
            mLongitude = list[13];

            return list[5];

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getWeatherPollution(){
        return mPollution;
    }
    
    public String getWeatherStatus() {
        return mWeather;
    }

    public String getWeatherStatus2() {
        return mWeather2;
    }

    public boolean isWeatherWillChange() {
        if (mWeather == null) {
            return false;
        }
        return !mWeather.equals(mWeather2);
    }

    public String getLowTem() {
        return mLow;
    }

    public String getHighTem() {
        return mHigh;
    }

    public String getWindDirection() {
        return mWind;
    }

}
