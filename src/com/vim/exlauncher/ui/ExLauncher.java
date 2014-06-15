package com.vim.exlauncher.ui;

import static com.vim.exlauncher.data.JsonAdData.AD_PIC_PREFIX;
import static com.vim.exlauncher.data.JsonAdData.PVAD_PIC_PREFIX;
import static com.vim.exlauncher.data.JsonAdData.STATIC_AD_PIC_PREFIX;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vim.exlauncher.R;
import com.vim.exlauncher.data.HttpRequest;
import com.vim.exlauncher.data.JsonAdData;
import com.vim.exlauncher.data.JsonWeatherData;
import com.vim.exlauncher.data.LauncherUtils;
import com.vim.exlauncher.data.Weather;

public class ExLauncher extends Activity {
    private static final String TAG = "ExLauncher";

    private ImageView mIvLogo;
    private Button mBtnOtaUpdateInfo;

    // the lastest hits
    private BottomImageButton mIbFirst;
    private BottomImageButton mIbSecond;
    private BottomImageButton mIbThird;
    private BottomImageButton mIbForth;
    private BottomImageButton mIbFifth;
    private BottomImageButton mIbSixth;
    private BottomImageButton mIbSeventh;

    // the bottom buttons
    private BottomImageButton mIbTv;
    private BottomImageButton mIbMovies;
    private BottomImageButton mIbDrama;
    private BottomImageButton mIbYoutube;
    private BottomImageButton mIbGames;
    private BottomImageButton mIbRadio;
    private BottomImageButton mIbApps;
    private BottomImageButton mIbSetting;

    // shadow image
    public static AbsoluteLayout mAlShadow;
    public static ImageView mIvShadow;

    // the right side
    private TextView mTvTime;
    private TextView mTvDate;
    private TextView mTvCity;
    private TextView mTvWeather;
    private TextView mTvTempHigh;
    private TextView mTvTempLow;
    private TextView mTvWind;
    private static TextView mTvMsg;
    private static Button mBtnRead;
    private static ImageButton mIbRead;
    private ImageView mIvWeather;
    private ImageView mIvWifi;
    private ImageView mIvEthernet;
    private ImageView mIvUsb;

    private ImageButtonOnClickListener mImageButtonListener;
    private OnFocusChangeListener mImageButtonOnFocusChangedListener;
    private BottomImageButton mImageButtonFocus;
    private String mCurrentFirmwareVer;
    private Toast mToast;
    public static Context mContext;

    private static final String TIME_FORMAT = "HH:mm";
    public static final String ETH_ADDRESS_PATH = "/sys/class/net/eth0/address";
    private static final String MAC_PARAM_ETH = "mac=";
    private static final String MAC_PARAM_WIFI = "mac2=";
    private static final String WEATHER_QUERY_PREFIX = "q=";

    private static final String FIRMWARE_VERSION_PROP = "otaupdater.otaver";

    private static final String EXTERNAL_STORAGE = "/storage/external_storage";
    private static final String SD_PATH = "/storage/external_storage/sdcard1";
    private static final String CONNECTIVITY_CHANGED = "android.net.conn.CONNECTIVITY_CHANGE";

    private static final String JSON_DATA_AD_URL = "http://mymobiletvhd.com/android/fitv.php";
    private static final String JSON_DATA_WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather?units=metric";
    private static final String LOGO_NAME = "logo.png";

    public static final int MAX_AD_PIC_ROTATE_NUM = 4;
    private static final int MAX_VIDEO_PIC_ROTATE_NUM = 4;
    private static final int MAX_STATIC_AD_PIC_NUM = 5;

    private int mAdvPicIndex;
    private static String mKeyAdv;

    private int mPVAdvPicIndex;
    private static String mKeyPVAdv;

    private static JsonAdData mJsonAdData;
    private JsonWeatherData mJsonWeatherData;
    private Bitmap mLogoBitmap;
    private Map<String, Bitmap> mAdvBitmapMap;
    private Map<String, Bitmap> mPVAdvBitmapMap;
    private Map<String, Bitmap> mStaticAdvBitmapMap;

    private static SharedPreferences mSharedPreferences;
    private boolean mFirstGetData = true;

    // insure MSG_UI_DISPLAY_LATEST_HITS msg be sent only once
    private boolean mStartUpdateLatestHits = false;
    private DataHandler mDataHandler;
    private PicHandler mPicHandler;
    private Weather mWeather;
    private AlertDialog mUpdateDialog;

    private static final int MSG_DATA_QUIT = 0;
    private static final int MSG_DATA_GET_JSON = 1;

    private static final int GET_JSON_DELAY = 15 * 60 * 1000;

    final class DataHandler extends Handler {
        public DataHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_DATA_QUIT:
                this.getLooper().quit();
                break;

            case MSG_DATA_GET_JSON:
                getJsonData();
                this.sendEmptyMessageDelayed(MSG_DATA_GET_JSON, GET_JSON_DELAY);
                break;
            }
        }

    }

    private static final int MSG_PIC_QUIT = 0;
    private static final int MSG_PIC_GET_DATA = 1;

    final class PicHandler extends Handler {
        public PicHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_PIC_QUIT:
                this.getLooper().quit();
                break;

            case MSG_PIC_GET_DATA:
                getDealerLogo();
                getLatestHitsPic();
                break;
            }
        }

    }

    private static final int MSG_UI_DISPLAY_LATEST_HITS = 1;
    private static final int MSG_UI_DISPLAY_LOGO = 2;
    private static final int MSG_UI_CHECK_OTA_UPDATE_INFO = 3;
    private static final int MSG_UI_DISPLAY_RIGHT_SIDE = 4;

    private static final int MSG_UI_LAYTEST_HITS_UPDATE_INTERNAL = 5 * 1000;

    private Handler mUiHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_UI_DISPLAY_LATEST_HITS:
                displayLatestHits();
                this.sendEmptyMessageDelayed(MSG_UI_DISPLAY_LATEST_HITS,
                        MSG_UI_LAYTEST_HITS_UPDATE_INTERNAL);
                break;

            case MSG_UI_DISPLAY_LOGO:
                displayLogo();
                break;

            case MSG_UI_CHECK_OTA_UPDATE_INFO:
                checkOtaUpdateInfo();
                break;

            case MSG_UI_DISPLAY_RIGHT_SIDE:
                displayRightSide();
                break;
            }
        }

    };

    public static void resetMsgStatusAndRefresh(boolean status) {
        if (mJsonAdData != null) {
            mJsonAdData.setMsgStatus(status);
        }

        displayBottomMsgInfo();
    }

    private void getAdvPic() {
        // get all the rotated advertising picture from the url and save them
        synchronized (mAdvBitmapMap) {
            Map<String, String> advMap = new HashMap<String, String>();
            for (int i = 0; i < MAX_AD_PIC_ROTATE_NUM; i++) {
                String advKey = AD_PIC_PREFIX + (i + 1);
                String advUrl = mSharedPreferences.getString(advKey, null);

                logd("[getAdvPic] advKey : " + advKey + ", advUrl : " + advUrl);
                if (!TextUtils.isEmpty(advUrl)) {
                    advMap.put(advKey, advUrl);
                }
            }

            logd("[getAdvPic] advMap : " + advMap);
            if (advMap.size() == 0) {
                logd("[getAdvPic] we don't get any adv url from ad data!");
                return;
            }

            InputStream isBitmap = null;
            mAdvBitmapMap = new HashMap<String, Bitmap>();
            for (int i = 0; i < advMap.size(); i++) {
                String advKey = AD_PIC_PREFIX + (i + 1);
                String advUrl = advMap.get(advKey);
                isBitmap = HttpRequest.getStreamFromUrl(advUrl);

                if (isBitmap == null) {
                    logd("[getAdvPic] error when getting logo on " + advUrl);
                    continue;
                }

                Bitmap bitmap = BitmapFactory.decodeStream(isBitmap);
                if (bitmap != null) {
                    mAdvBitmapMap.put(advKey, bitmap);
                    LauncherUtils.saveBitmap(this, bitmap, advKey + ".png");
                }
            }
        }
    }

    private void getPVAdvPic() {
        // get all the rotated video picture from the url and save them
        synchronized (mPVAdvBitmapMap) {
            Map<String, String> pvadvMap = new HashMap<String, String>();
            for (int i = 0; i < MAX_VIDEO_PIC_ROTATE_NUM; i++) {
                String pvadvKey = PVAD_PIC_PREFIX + (i + 1);
                String pvadvUrl = mSharedPreferences.getString(pvadvKey, null);

                if (!TextUtils.isEmpty(pvadvUrl)) {
                    pvadvMap.put(pvadvKey, pvadvUrl);
                }
            }

            logd("[getPVAdvPic] pvadvMap : " + pvadvMap);
            if (pvadvMap.size() == 0) {
                logd("[getPVAdvPic] we don't get any adv url from ad data!");
                return;
            }

            InputStream isBitmap = null;
            mPVAdvBitmapMap = new HashMap<String, Bitmap>();
            for (int i = 0; i < pvadvMap.size(); i++) {
                String pvadvKey = PVAD_PIC_PREFIX + (i + 1);
                String pvadvUrl = pvadvMap.get(pvadvKey);
                isBitmap = HttpRequest.getStreamFromUrl(pvadvUrl);

                if (isBitmap == null) {
                    logd("[getPVAdvPic] error when getting logo on " + pvadvUrl);
                    continue;
                }

                Bitmap bitmap = BitmapFactory.decodeStream(isBitmap);
                if (bitmap != null) {
                    mPVAdvBitmapMap.put(pvadvKey, bitmap);
                    LauncherUtils.saveBitmap(this, bitmap, pvadvKey + ".png");
                }
            }
        }
    }

    private void getStaticAdvPic() {
        // get other five static pictures from the url and save them
        synchronized (mStaticAdvBitmapMap) {
            Map<String, String> staticAdvMap = new HashMap<String, String>();
            for (int i = 0; i < MAX_STATIC_AD_PIC_NUM; i++) {
                String staticAdvKey = STATIC_AD_PIC_PREFIX + (i + 1);
                String staticAdvUrl = mSharedPreferences.getString(
                        staticAdvKey, null);

                if (!TextUtils.isEmpty(staticAdvUrl)) {
                    staticAdvMap.put(staticAdvKey, staticAdvUrl);
                }
            }

            logd("[getStaticAdvPic] staticAdvMap : " + staticAdvMap);
            if (staticAdvMap.size() == 0) {
                logd("[getStaticAdvPic] we don't get any adv url from ad data!");
                return;
            }

            InputStream isBitmap = null;
            mStaticAdvBitmapMap = new HashMap<String, Bitmap>();
            for (int i = 0; i < staticAdvMap.size(); i++) {
                String staticAdvKey = STATIC_AD_PIC_PREFIX + (i + 1);
                String staticAdvUrl = staticAdvMap.get(staticAdvKey);
                isBitmap = HttpRequest.getStreamFromUrl(staticAdvUrl);

                if (isBitmap == null) {
                    logd("[getStaticAdvPic] error when getting logo on "
                            + staticAdvUrl);
                    continue;
                }

                Bitmap bitmap = BitmapFactory.decodeStream(isBitmap);
                if (bitmap != null) {
                    mStaticAdvBitmapMap.put(staticAdvKey, bitmap);
                    LauncherUtils.saveBitmap(this, bitmap, staticAdvKey
                            + ".png");
                }
            }
        }
    }

    private void getLatestHitsPic() {
        // get advertisement picture
        new Thread() {
            @Override
            public void run() {
                getAdvPic();
            }
        }.start();

        // get video ad picture
        new Thread() {
            @Override
            public void run() {
                getPVAdvPic();
            }
        }.start();

        // get other five advertisement picture
        new Thread() {
            @Override
            public void run() {
                getStaticAdvPic();
            }
        }.start();

        // we already start ui updating when onResume
        // mUiHandler.sendEmptyMessage(MSG_UI_DISPLAY_LATEST_HITS);
    }

    private void getDealerLogo() {
        String logoUrlString = mSharedPreferences.getString(
                JsonAdData.DEALER_LOGO, null);

        if (TextUtils.isEmpty(logoUrlString)) {
            Log.e(TAG, "[getDealerLogo] logo url is empty!");
            return;
        }

        InputStream isBitmap = HttpRequest.getStreamFromUrl(logoUrlString);
        if (isBitmap == null) {
            logd("[getDealerLogo] error when getting logo on " + logoUrlString);
            return;
        }

        mLogoBitmap = BitmapFactory.decodeStream(isBitmap);

        if (mLogoBitmap != null) {
            LauncherUtils.saveBitmap(this, mLogoBitmap, LOGO_NAME);
        }

        mUiHandler.sendEmptyMessage(MSG_UI_DISPLAY_LOGO);
    }

    private void getAdData() {
        String macEth = LauncherUtils.getEthMac(ETH_ADDRESS_PATH);
        String macWifi = LauncherUtils.getWifiMac(this);

        logd("[getAdData] macEth : " + macEth + ", macWifi : " + macWifi);
        if (TextUtils.isEmpty(macEth) || TextUtils.isEmpty(macWifi)) {
            Log.e(TAG, "[getAdData] macEth OR macWifi is empty!");
            return;
        }

        // String macEth = "00116d063cfc";
        // String macWifi = "a0f4594776de";

        StringBuilder adParamsSb = new StringBuilder();
        adParamsSb.append(MAC_PARAM_ETH + macEth);
        adParamsSb.append("&");
        adParamsSb.append(MAC_PARAM_WIFI + macWifi);
        String adUrl = JSON_DATA_AD_URL + "?" + adParamsSb.toString();
        JSONObject jsonAdObj = HttpRequest.getDataFromUrl(adUrl);

        if (jsonAdObj == null) {
            logd("[getAdData] can not get ad data from " + adUrl);
            return;
        }

        logd("jsonAdObj : " + jsonAdObj.toString());
        mJsonAdData = new JsonAdData(this, jsonAdObj);
        mJsonAdData.parseAndSaveData();
        logd(mJsonAdData.toString());
    }

    private void getWeatherData() {
        String city = mSharedPreferences.getString(JsonAdData.CITY, null);
        String country = mSharedPreferences.getString(JsonAdData.COUNTRY, null);
        if (TextUtils.isEmpty(city) || TextUtils.isEmpty(country)) {
            Log.w(TAG,
                    "[getWeatherData] we didn't get city and country from ad data yet!");
            return;
        }

        logd("[getWeatherData] city : " + city + ", country : " + country);

        StringBuilder weatherParamsSb = new StringBuilder();
        weatherParamsSb.append(WEATHER_QUERY_PREFIX);
        weatherParamsSb.append(city);
        weatherParamsSb.append(",");
        weatherParamsSb.append(country);
        String weatherUrl = JSON_DATA_WEATHER_URL + "&"
                + weatherParamsSb.toString();
        JSONObject jsonWeatherObj = HttpRequest.getDataFromUrl(weatherUrl);

        if (jsonWeatherObj == null) {
            logd("[getAdData] can not get weather data from " + weatherUrl);
            return;
        }

        logd("jsonWeatherObj : " + jsonWeatherObj.toString());
        mJsonWeatherData = new JsonWeatherData(this, jsonWeatherObj);
        mJsonWeatherData.parseAndSaveData();
        logd(mJsonWeatherData.toString());
    }

    private void getJsonData() {
        // advertisement data
        getAdData();

        mPicHandler.sendEmptyMessage(MSG_PIC_GET_DATA);

        // weather data
        getWeatherData();

        mUiHandler.sendEmptyMessage(MSG_UI_CHECK_OTA_UPDATE_INFO);
        mUiHandler.sendEmptyMessage(MSG_UI_DISPLAY_RIGHT_SIDE);
    }

    private boolean hasNetworkConnected() {
        boolean isWifiConnected = false;
        boolean isEthConnected = false;
        try {
            ConnectivityManager connectManager = (ConnectivityManager) this
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            
            NetworkInfo wifiNetworkInfo = connectManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (wifiNetworkInfo != null) {
                State wifiState = wifiNetworkInfo.getState();
                isWifiConnected = (wifiState == State.CONNECTED);
            }

            NetworkInfo ethNetworkInfo = connectManager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
            if (ethNetworkInfo != null) {
                State ethState = ethNetworkInfo.getState();
                isEthConnected = (ethState == State.CONNECTED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isWifiConnected || isEthConnected;
    }

    private void showNewVersionUpdate() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.new_version_available);
        builder.setPositiveButton(R.string.yes,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.setComponent(new ComponentName("com.otaupdater",
                                "com.otaupdater.OTAUpdaterActivity"));

                        try {
                            ExLauncher.this.startActivity(intent);
                        } catch (ActivityNotFoundException anf) {
                            Log.e(TAG, "Activity not found!");
                            anf.printStackTrace();

                            if (mToast != null) {
                                mToast.cancel();
                            }
                            mToast = Toast.makeText(ExLauncher.this,
                                    "OTAUpdateCenter App Not Found!",
                                    Toast.LENGTH_SHORT);
                            mToast.show();
                        }

                    }
                });

        builder.setNegativeButton(R.string.later,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        // TODO Auto-generated method stub
                        mUpdateDialog.dismiss();
                        mUpdateDialog = null;
                    }
                });

        mUpdateDialog = builder.create();
        mUpdateDialog.show();
    }

    private void checkOtaUpdateInfo() {
        String firmwareVer = mSharedPreferences.getString(
                JsonAdData.FIRMWARE_VERSION, null);
        if (TextUtils.isEmpty(firmwareVer)) {
            logd("[checkOtaUpdateInfo] we haven't got firmware ver from ad data yet!");
            // mBtnOtaUpdateInfo.setVisibility(View.GONE);
            return;
        }

        // String newVersion = getString(R.string.new_version_prefix) + " "
        // + firmwareVer + " " + getString(R.string.new_version_postfix);

        if (TextUtils.isEmpty(mCurrentFirmwareVer)) {
            // mBtnOtaUpdateInfo.setText(newVersion);
            // mBtnOtaUpdateInfo.setVisibility(View.VISIBLE);
            if (mUpdateDialog == null) {
                showNewVersionUpdate();
            }
        } else {
            try {
                int intCurrentVer = Integer.parseInt(mCurrentFirmwareVer);
                int intNewVer = Integer.parseInt(firmwareVer);

                if (intNewVer > intCurrentVer) {
                    // mBtnOtaUpdateInfo.setText(newVersion);
                    // mBtnOtaUpdateInfo.setVisibility(View.VISIBLE);
                    // } else {
                    // mBtnOtaUpdateInfo.setVisibility(View.GONE);
                    if (mUpdateDialog == null) {
                        showNewVersionUpdate();
                    }
                }
            } catch (NumberFormatException nfe) {
                Log.e(TAG, "[checkOtaUpdateInfo] parse " + mCurrentFirmwareVer
                        + " and " + firmwareVer + " error!");
                // mBtnOtaUpdateInfo.setVisibility(View.GONE);
            }
        }
    }

    private void displayDateAndTime() {
        String timeFormat = getTimeFormattedString(TIME_FORMAT);
        mTvTime.setText(timeFormat);
        mTvTime.setVisibility(View.VISIBLE);

        // display date
        String dateFormat = getDateFormattedString("MM-dd-yyyy");
        mTvDate.setText(dateFormat);
        mTvDate.setVisibility(View.VISIBLE);
    }

    private void displayCityAndWeatherIcon() {
        String city = mSharedPreferences.getString(JsonAdData.CITY, null);
        if (!TextUtils.isEmpty(city)) {
            mTvCity.setText(city);
        }
        mTvCity.setVisibility(View.VISIBLE);

        String icon = mSharedPreferences.getString(JsonWeatherData.ICON, null);
        if (!TextUtils.isEmpty(icon)) {
            int iconId = -1;
            iconId = mWeather.getResIdFromIconName(icon);

            if (iconId != -1) {
                mIvWeather.setImageResource(iconId);
                mIvWeather.setVisibility(View.VISIBLE);
            }
        }
    }

    private void displayWeatherInfo() {
        String weatherMain = mSharedPreferences.getString(
                JsonWeatherData.WEATHER_MAIN, null);
        float temp = mSharedPreferences.getFloat(JsonWeatherData.TEMP, 0.0f);
        if (!TextUtils.isEmpty(weatherMain) && (temp != 0.0f)) {
            StringBuilder weatherSb = new StringBuilder();
            weatherSb.append(weatherMain);
            weatherSb.append(", ");
            weatherSb.append(String.format("%.1f", temp) + " "
                    + getString(R.string.centigrade));
            mTvWeather.setText(weatherSb.toString());
        }
        mTvWeather.setVisibility(View.VISIBLE);

        float tempMax = mSharedPreferences.getFloat(JsonWeatherData.TEMP_MAX,
                0.0f);
        if (temp != 0.0f) {
            mTvTempHigh.setText("High: " + String.format("%.1f", tempMax) + " "
                    + getString(R.string.centigrade));
        }
        mTvTempHigh.setVisibility(View.VISIBLE);

        float tempMin = mSharedPreferences.getFloat(JsonWeatherData.TEMP_MIN,
                0.0f);
        if (temp != 0.0f) {
            mTvTempLow.setText("Low: " + String.format("%.1f", tempMin) + " "
                    + getString(R.string.centigrade));
        }
        mTvTempLow.setVisibility(View.VISIBLE);
    }

    private void displayWindInfo() {
        float speed = mSharedPreferences.getFloat(JsonWeatherData.SPEED, 0.0f);
        float degree = mSharedPreferences.getFloat(JsonWeatherData.DEG, 0.0f);
        if ((speed != 0.0f) && (degree != 0.0f)) {
            StringBuilder windSb = new StringBuilder();
            windSb.append("Wind degree: " + degree);
            windSb.append(" \nat " + speed + " KPH");
            mTvWind.setText(windSb.toString());
        }
        mTvWind.setVisibility(View.VISIBLE);
    }

    private static void displayBottomMsgInfo() {
        String bottomMsg = mSharedPreferences.getString(JsonAdData.BOTTOM_MSG,
                null);
        if (!TextUtils.isEmpty(bottomMsg)) {
            mTvMsg.setText(bottomMsg);
            mTvMsg.setVisibility(View.VISIBLE);

            if ((mJsonAdData != null) && mJsonAdData.isMsgNew()) {
                mBtnRead.setVisibility(View.GONE);
                mIbRead.setVisibility(View.VISIBLE);
            } else {
                mBtnRead.setVisibility(View.VISIBLE);
                mIbRead.setVisibility(View.GONE);
            }
        }
    }

    private void displayRightSide() {
        displayDateAndTime();
        displayCityAndWeatherIcon();
        displayWeatherInfo();
        displayWindInfo();
        displayBottomMsgInfo();
    }

    private void displayLatestHits() {
        displayAdvPic();
        displayPVAdvPic();
        displayStaticAdvPic();
    }

    public static String getCurrentAdvKey() {
        return mKeyAdv;
    }

    public static String getCurrentPVAdvKey() {
        return mKeyPVAdv;
    }

    private void displayAdvPic() {
        synchronized (mAdvBitmapMap) {
            if ((mAdvBitmapMap == null) || mAdvBitmapMap.isEmpty()) {
                logd("[displayAdvPic] mAdvBitmapMap is empty, try to load from the file");

                mAdvBitmapMap = new HashMap<String, Bitmap>();
                for (int i = 0; i < MAX_AD_PIC_ROTATE_NUM; i++) {
                    String advKey = AD_PIC_PREFIX + (i + 1);
                    Bitmap bitmap = LauncherUtils.loadBitmap(this, advKey
                            + ".png");
                    if (bitmap != null) {
                        mAdvBitmapMap.put(advKey, bitmap);
                    }
                }
            }

            logd("[displayAdvPic] mAdvBitmapMap size : " + mAdvBitmapMap.size()
                    + ", mAdvPicIndex : " + mAdvPicIndex);
            if (mAdvBitmapMap.size() > 0) {
                // get the near pic
                String advKey = null;
                boolean hasLoop = false;
                for (int i = mAdvPicIndex; i <= mAdvBitmapMap.size(); i++) {
                    if (mAdvBitmapMap.containsKey(AD_PIC_PREFIX + i)) {
                        // found it
                        advKey = AD_PIC_PREFIX + i;
                        break;
                    }

                    if (!hasLoop && (i == mAdvBitmapMap.size())) {
                        // loop again for one time
                        i = 1;
                        hasLoop = true;
                    }
                }

                logd("[displayAdvPic] mAdvPicIndex : " + mAdvPicIndex
                        + ", advKey : " + advKey);
                if (TextUtils.isEmpty(advKey)) {
                    logd("[displayAdvPic] not found adv pic!");
                    return;
                }

                mAdvPicIndex++;
                if (mAdvPicIndex > mAdvBitmapMap.size()) {
                    mAdvPicIndex = 1;
                }

                mIbSecond.setBackgroundDrawable(new BitmapDrawable(
                        mAdvBitmapMap.get(advKey)));
                mKeyAdv = advKey;

                if ((mImageButtonFocus != null)
                        && (mImageButtonFocus.getId() == R.id.ib_lh_second)) {
                    mImageButtonFocus.setShadowEffect();
                }
            }

        }
    }

    private void displayPVAdvPic() {
        synchronized (mPVAdvBitmapMap) {
            if ((mPVAdvBitmapMap == null) || mPVAdvBitmapMap.isEmpty()) {
                logd("[displayPVAdvPic] mPVAdvBitmapMap is empty, try to load from the file");

                mPVAdvBitmapMap = new HashMap<String, Bitmap>();
                for (int i = 0; i < MAX_VIDEO_PIC_ROTATE_NUM; i++) {
                    String pvadvKey = PVAD_PIC_PREFIX + (i + 1);
                    Bitmap bitmap = LauncherUtils.loadBitmap(this, pvadvKey
                            + ".png");
                    if (bitmap != null) {
                        mPVAdvBitmapMap.put(pvadvKey, bitmap);
                    }
                }
            }

            logd("[displayPVAdvPic] mPVAdvBitmapMap size : "
                    + mPVAdvBitmapMap.size() + ", mPVAdvPicIndex : "
                    + mPVAdvPicIndex);
            if (mPVAdvBitmapMap.size() > 0) {
                // get the near pic
                String pvadvKey = null;
                boolean hasLoop = false;
                for (int i = mPVAdvPicIndex; i <= mPVAdvBitmapMap.size(); i++) {
                    if (mPVAdvBitmapMap.containsKey(PVAD_PIC_PREFIX + i)) {
                        // found it
                        pvadvKey = PVAD_PIC_PREFIX + i;
                        break;
                    }

                    if (!hasLoop && (i == mPVAdvBitmapMap.size())) {
                        // loop again for one time
                        i = 1;
                        hasLoop = true;
                    }
                }

                logd("[displayPVAdvPic] mPVAdvPicIndex : " + mPVAdvPicIndex
                        + ", pvadvKey : " + pvadvKey);
                if (TextUtils.isEmpty(pvadvKey)) {
                    logd("[displayPVAdvPic] not found video pic!");
                    return;
                }

                mPVAdvPicIndex++;
                if (mPVAdvPicIndex > mPVAdvBitmapMap.size()) {
                    mPVAdvPicIndex = 1;
                }

                mIbFirst.setBackgroundDrawable(new BitmapDrawable(
                        mPVAdvBitmapMap.get(pvadvKey)));
                mKeyPVAdv = pvadvKey;

                if ((mImageButtonFocus != null)
                        && (mImageButtonFocus.getId() == R.id.ib_lh_first)) {
                    mImageButtonFocus.setShadowEffect();
                }
            }
        }
    }

    private void displayStaticAdvPic() {
        synchronized (mStaticAdvBitmapMap) {
            if ((mStaticAdvBitmapMap == null) || mStaticAdvBitmapMap.isEmpty()) {
                logd("[displayStaticAdvPic] mStaticAdvBitmapMap is empty, try to load from the file");

                mStaticAdvBitmapMap = new HashMap<String, Bitmap>();
                for (int i = 0; i < MAX_STATIC_AD_PIC_NUM; i++) {
                    String staticAdvKey = STATIC_AD_PIC_PREFIX + (i + 1);
                    Bitmap bitmap = LauncherUtils.loadBitmap(this, staticAdvKey
                            + ".png");
                    if (bitmap != null) {
                        mStaticAdvBitmapMap.put(staticAdvKey, bitmap);
                    }
                }
            }

            logd("[displayStaticAdvPic] mStaticAdvBitmapMap size : "
                    + mStaticAdvBitmapMap.size());
            if (mStaticAdvBitmapMap.size() == 0) {
                logd("[displayStaticAdvPic] mStaticAdvBitmapMap is empty!");
                return;
            }

            String staticAdvKey = null;
            Bitmap bitmap = null;

            // display the third pic
            staticAdvKey = STATIC_AD_PIC_PREFIX + 1;
            bitmap = mStaticAdvBitmapMap.get(staticAdvKey);
            if (bitmap != null) {
                mIbThird.setBackgroundDrawable(new BitmapDrawable(bitmap));
            }

            // display the forth pic
            staticAdvKey = STATIC_AD_PIC_PREFIX + 2;
            bitmap = mStaticAdvBitmapMap.get(staticAdvKey);
            if (bitmap != null) {
                mIbForth.setBackgroundDrawable(new BitmapDrawable(bitmap));
            }

            // display the fifth pic
            staticAdvKey = STATIC_AD_PIC_PREFIX + 3;
            bitmap = mStaticAdvBitmapMap.get(staticAdvKey);
            if (bitmap != null) {
                mIbFifth.setBackgroundDrawable(new BitmapDrawable(bitmap));
            }

            // display the sixth pic
            staticAdvKey = STATIC_AD_PIC_PREFIX + 4;
            bitmap = mStaticAdvBitmapMap.get(staticAdvKey);
            if (bitmap != null) {
                mIbSixth.setBackgroundDrawable(new BitmapDrawable(bitmap));
            }

            // display the seventh pic
            staticAdvKey = STATIC_AD_PIC_PREFIX + 5;
            bitmap = mStaticAdvBitmapMap.get(staticAdvKey);
            if (bitmap != null) {
                mIbSeventh.setBackgroundDrawable(new BitmapDrawable(bitmap));
            }
        }
    }

    private void displayLogo() {
        if (mLogoBitmap == null) {
            mLogoBitmap = LauncherUtils.loadBitmap(this, LOGO_NAME);
        }

        if (mLogoBitmap == null) {
            logd("[displayLogo] we don't get logo from ad data yet!");
            return;
        }

        mIvLogo.setImageBitmap(mLogoBitmap);
        mIvLogo.setVisibility(View.VISIBLE);
    }

    private String getTimeFormattedString(String format) {
        Date currentTime = new Date();
        SimpleDateFormat timeFormatter = new SimpleDateFormat(format);
        String formattedString = timeFormatter.format(currentTime);

        logd("[getTimeFormattedString] formattedString : " + formattedString);
        return formattedString;
    }

    private String getDateFormattedString(String format) {
        Calendar cal = Calendar.getInstance();
        String dayOfWeek = cal.getDisplayName(Calendar.DAY_OF_WEEK,
                Calendar.SHORT, Locale.getDefault());
        String month = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT,
                Locale.getDefault());
        int dayOfMonth = cal.get(Calendar.DATE);
        int year = cal.get(Calendar.YEAR);

        logd("[getDateFormattedString] dayOfWeek : " + dayOfWeek + ", month : "
                + month + ", dayOfMonth : " + dayOfMonth + ", year : " + year);

        String dateFormattedString = dayOfWeek + " " + month + " " + dayOfMonth
                + "." + year;
        logd("[getDateFormattedString] dateFormattedString : "
                + dateFormattedString);

        return dateFormattedString;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ex_launcher);

        initRes();

        HandlerThread htData = new HandlerThread("ExLauncher_Data");
        htData.start();
        mDataHandler = new DataHandler(htData.getLooper());

        HandlerThread htPic = new HandlerThread("ExLauncher_Pic");
        htPic.start();
        mPicHandler = new PicHandler(htPic.getLooper());

        mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        mWeather = Weather.getInstance();
        mWeather.initIconMap();

        mAdvPicIndex = 1;
        mPVAdvPicIndex = 1;
        mAdvBitmapMap = new HashMap<String, Bitmap>();
        mPVAdvBitmapMap = new HashMap<String, Bitmap>();
        mStaticAdvBitmapMap = new HashMap<String, Bitmap>();
        mCurrentFirmwareVer = getCurrentFirmwareVer();
        mContext = this;

        registerStatusReceiver();
    }

    @Override
    protected void onStart() {
        super.onStart();
        logd("[onStart]");
        mPicHandler.sendEmptyMessage(MSG_PIC_GET_DATA);

        if (mFirstGetData) {
            mDataHandler.sendEmptyMessage(MSG_DATA_GET_JSON);
            mFirstGetData = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayStatus();
        displayRightSide();
        displayLogo();

        if (!mStartUpdateLatestHits) {
            mUiHandler.sendEmptyMessage(MSG_UI_DISPLAY_LATEST_HITS);
            mStartUpdateLatestHits = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        logd("[onStop]");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterStatusReceiver();
        mDataHandler.sendEmptyMessage(MSG_DATA_QUIT);
        mPicHandler.sendEmptyMessage(MSG_PIC_QUIT);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private String getCurrentFirmwareVer() {
        return LauncherUtils.getprop(FIRMWARE_VERSION_PROP);
    }

    private BroadcastReceiver mediaReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, " mediaReceiver action = " + action);

            if (Intent.ACTION_MEDIA_EJECT.equals(action)
                    || Intent.ACTION_MEDIA_UNMOUNTED.equals(action)
                    || Intent.ACTION_MEDIA_MOUNTED.equals(action)) {
                displayStatus();
            }
        }
    };

    private BroadcastReceiver netReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "netReceiver action = " + action);

            if (action.equals(CONNECTIVITY_CHANGED)) {
                displayStatus();
            } else if (action.equals(Intent.ACTION_TIME_TICK)) {
                // mDataHandler.sendEmptyMessage(MSG_DATA_GET_JSON);
                displayDateAndTime();
            } else if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)){
                if (hasNetworkConnected()) {
                    getJsonData();
                }
            }
        }
    };

    private void registerStatusReceiver() {
        IntentFilter mediaFilter = new IntentFilter();
        mediaFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        mediaFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        mediaFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        mediaFilter.addDataScheme("file");
        registerReceiver(mediaReceiver, mediaFilter);

        IntentFilter netFilter = new IntentFilter();
        netFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION); 
        netFilter.addAction(CONNECTIVITY_CHANGED);
        netFilter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(netReceiver, netFilter);
    }

    private void unregisterStatusReceiver() {
        unregisterReceiver(mediaReceiver);
        unregisterReceiver(netReceiver);
    }

    private void initRes() {
        mImageButtonListener = new ImageButtonOnClickListener(this);
        mImageButtonOnFocusChangedListener = new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                Log.d(TAG, "view : " + view + ", hasFocus : " + hasFocus);

                if (hasFocus) {
                    ((BottomImageButton) view).setShadowEffect();
                    mImageButtonFocus = (BottomImageButton) view;
                }
            }
        };

        mIvLogo = (ImageView) findViewById(R.id.iv_logo);
        mBtnOtaUpdateInfo = (Button) findViewById(R.id.btn_ota_update_info);
        mBtnOtaUpdateInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setComponent(new ComponentName("com.otaupdater",
                        "com.otaupdater.OTAUpdaterActivity"));

                try {
                    ExLauncher.this.startActivity(intent);
                } catch (ActivityNotFoundException anf) {
                    Log.e(TAG, "Activity not found!");
                    anf.printStackTrace();

                    if (mToast != null) {
                        mToast.cancel();
                    }
                    mToast = Toast.makeText(ExLauncher.this,
                            "OTAUpdateCenter App Not Found!",
                            Toast.LENGTH_SHORT);
                    mToast.show();
                }

            }
        });
        mBtnOtaUpdateInfo.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    mBtnOtaUpdateInfo.setBackgroundColor(Color.DKGRAY);
                } else {
                    mBtnOtaUpdateInfo.setBackgroundColor(0);
                }
            }
        });

        mAlShadow = (AbsoluteLayout) findViewById(R.id.ly_shadow_focus);
        mIvShadow = (ImageView) findViewById(R.id.iv_shadow_focus);

        initBottomButton();
        initLatestHits();
        initRightSide();
    }

    private void initBottomButton() {
        // the bottom buttons
        LinearLayout llWeatherAndStatus = (LinearLayout) findViewById(R.id.ll_bottom);
        llWeatherAndStatus.getBackground().setAlpha(200);

        mIbTv = (BottomImageButton) findViewById(R.id.ib_tv);
        mIbMovies = (BottomImageButton) findViewById(R.id.ib_movies);
        mIbDrama = (BottomImageButton) findViewById(R.id.ib_drama);
        mIbYoutube = (BottomImageButton) findViewById(R.id.ib_youtube);
        mIbGames = (BottomImageButton) findViewById(R.id.ib_games);
        mIbRadio = (BottomImageButton) findViewById(R.id.ib_radio);
        mIbApps = (BottomImageButton) findViewById(R.id.ib_apps);
        mIbSetting = (BottomImageButton) findViewById(R.id.ib_setting);

        mIbTv.setOnClickListener(mImageButtonListener);
        mIbMovies.setOnClickListener(mImageButtonListener);
        mIbDrama.setOnClickListener(mImageButtonListener);
        mIbYoutube.setOnClickListener(mImageButtonListener);
        mIbGames.setOnClickListener(mImageButtonListener);
        mIbRadio.setOnClickListener(mImageButtonListener);
        mIbApps.setOnClickListener(mImageButtonListener);
        mIbSetting.setOnClickListener(mImageButtonListener);

        mIbTv.setOnFocusChangeListener(mImageButtonOnFocusChangedListener);
        mIbMovies.setOnFocusChangeListener(mImageButtonOnFocusChangedListener);
        mIbDrama.setOnFocusChangeListener(mImageButtonOnFocusChangedListener);
        mIbYoutube.setOnFocusChangeListener(mImageButtonOnFocusChangedListener);
        mIbGames.setOnFocusChangeListener(mImageButtonOnFocusChangedListener);
        mIbRadio.setOnFocusChangeListener(mImageButtonOnFocusChangedListener);
        mIbApps.setOnFocusChangeListener(mImageButtonOnFocusChangedListener);
        mIbSetting.setOnFocusChangeListener(mImageButtonOnFocusChangedListener);
    }

    private void initLatestHits() {
        // the lastest hits
        mIbFirst = (BottomImageButton) findViewById(R.id.ib_lh_first);
        mIbSecond = (BottomImageButton) findViewById(R.id.ib_lh_second);
        mIbThird = (BottomImageButton) findViewById(R.id.ib_lh_third);
        mIbForth = (BottomImageButton) findViewById(R.id.ib_lh_forth);
        mIbFifth = (BottomImageButton) findViewById(R.id.ib_lh_fifth);
        mIbSixth = (BottomImageButton) findViewById(R.id.ib_lh_sixth);
        mIbSeventh = (BottomImageButton) findViewById(R.id.ib_lh_seventh);

        mIbFirst.setOnClickListener(mImageButtonListener);
        mIbSecond.setOnClickListener(mImageButtonListener);
        mIbThird.setOnClickListener(mImageButtonListener);
        mIbForth.setOnClickListener(mImageButtonListener);
        mIbFifth.setOnClickListener(mImageButtonListener);
        mIbSixth.setOnClickListener(mImageButtonListener);
        mIbSeventh.setOnClickListener(mImageButtonListener);

        mIbFirst.setOnFocusChangeListener(mImageButtonOnFocusChangedListener);
        mIbSecond.setOnFocusChangeListener(mImageButtonOnFocusChangedListener);
        mIbThird.setOnFocusChangeListener(mImageButtonOnFocusChangedListener);
        mIbForth.setOnFocusChangeListener(mImageButtonOnFocusChangedListener);
        mIbFifth.setOnFocusChangeListener(mImageButtonOnFocusChangedListener);
        mIbSixth.setOnFocusChangeListener(mImageButtonOnFocusChangedListener);
        mIbSeventh.setOnFocusChangeListener(mImageButtonOnFocusChangedListener);
    }

    private void initRightSide() {
        // the right side
        LinearLayout llWeatherAndStatus = (LinearLayout) findViewById(R.id.ll_weather_and_status);
        llWeatherAndStatus.getBackground().setAlpha(150);

        mTvTime = (TextView) findViewById(R.id.tv_time);
        mTvDate = (TextView) findViewById(R.id.tv_date);
        mTvCity = (TextView) findViewById(R.id.tv_city);
        mIvWeather = (ImageView) findViewById(R.id.iv_weather);
        mTvWeather = (TextView) findViewById(R.id.tv_weather);
        mTvTempHigh = (TextView) findViewById(R.id.tv_temp_high);
        mTvTempLow = (TextView) findViewById(R.id.tv_temp_low);
        mTvWind = (TextView) findViewById(R.id.tv_wind);
        mTvMsg = (TextView) findViewById(R.id.tv_msg);
        mBtnRead = (Button) findViewById(R.id.btn_read);
        mIbRead = (ImageButton) findViewById(R.id.ib_read);
        mIvWifi = (ImageView) findViewById(R.id.iv_wifi);
        mIvEthernet = (ImageView) findViewById(R.id.iv_ethernet);
        mIvUsb = (ImageView) findViewById(R.id.iv_usb);

        mBtnRead.setOnClickListener(new MsgButtonOnClickListener(this));
        mBtnRead.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        mBtnRead.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    mBtnRead.setBackgroundColor(Color.YELLOW);
                } else {
                    mBtnRead.setBackgroundColor(0);
                }
            }
        });

        mIbRead.setOnClickListener(new MsgButtonOnClickListener(this));
    }

    private void displayStatus() {
        mIvWifi.setVisibility(isWifiOn() ? View.VISIBLE : View.GONE);
        mIvEthernet.setVisibility(isEthernetOn() ? View.VISIBLE : View.GONE);
        mIvUsb.setVisibility(isUsbExists() ? View.VISIBLE : View.GONE);
    }

    private boolean isWifiOn() {
        boolean wifiOn = false;
        ConnectivityManager con = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = con
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (wifiInfo != null) {
            wifiOn = wifiInfo.isConnected();
        }

        return wifiOn;
    }

    private boolean isEthernetOn() {
        boolean ethernetOn = false;
        ConnectivityManager connectivity = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ethInfo = connectivity
                .getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
        if (ethInfo != null) {
            ethernetOn = ethInfo.isConnected();
        }

        return ethernetOn;
    }

    private boolean isUsbExists() {
        File dir = new File(EXTERNAL_STORAGE);
        if (dir.exists() && dir.isDirectory() && (dir.listFiles() != null)
                && (dir.listFiles().length > 0)) {
            for (File file : dir.listFiles()) {
                String path = file.getAbsolutePath();
                if (path.startsWith(EXTERNAL_STORAGE + "/sd")
                        && !path.equals(SD_PATH)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static void logd(String strs) {
        Log.d(TAG, strs + " --- VIM");
    }
}
