package com.vim.exlauncher.ui;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vim.exlauncher.R;
import com.vim.exlauncher.data.HttpRequest;
import com.vim.exlauncher.data.JsonAdData;
import com.vim.exlauncher.data.JsonWeatherData;
import com.vim.exlauncher.data.LauncherUtils;

public class ExLauncher extends Activity {
    private static final String TAG = "ExLauncher";

    private ImageView mIvLogo;

    // the lastest hits
    private ImageButton mIbFirst;
    private ImageButton mIbSecond;
    private ImageButton mIbThird;
    private ImageButton mIbForth;
    private ImageButton mIbFifth;
    private ImageButton mIbSixth;
    private ImageButton mIbSeventh;

    // the bottom buttons
    private BottomImageButton mIbTv;
    private BottomImageButton mIbMovies;
    private BottomImageButton mIbDrama;
    private BottomImageButton mIbYoutube;
    private BottomImageButton mIbGames;
    private BottomImageButton mIbRadio;
    private BottomImageButton mIbApps;
    private BottomImageButton mIbSetting;

    // the right side
    private TextView mTvTime;
    private TextView mTvDate;
    private TextView mTvCity;
    private TextView mTvWeather;
    private TextView mTvTempHigh;
    private TextView mTvTempLow;
    private TextView mTvWind;
    private TextView mTvMsg;
    private Button mBtnRead;
    private ImageView mIvWeather;
    private ImageView mIvWifi;
    private ImageView mIvEthernet;
    private ImageView mIvUsb;

    private ImageButtonOnClickListener mImageButtonListener;
    private ImageButtonOnFocusChangedListener mImageButtonOnFocusChangedListener;

    private static final String EXTERNAL_STORAGE = "/storage/external_storage";
    private static final String SD_PATH = "/storage/external_storage/sdcard1";
    private static final String CONNECTIVITY_CHANGED = "android.net.conn.CONNECTIVITY_CHANGE";

    private static final String JSON_DATA_AD_URL = "http://mymobiletvhd.com/android/userinfo.php";
    private static final String JSON_DATA_WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";

    private static final String LOGO_NAME = "logo.png";

    private JsonAdData mJsonAdData;
    private JsonWeatherData mJsonWeatherData;
    private Bitmap mLogoBitmap;
    private boolean mFirstGetLogo = true;
    private SharedPreferences mSharedPreferences;

    private DataHandler mDataHandler;
    private static final int MSG_DATA_QUIT = 0;
    private static final int MSG_DATA_GET_JSON = 1;
    private static final int MSG_DATA_GET_LOGO = 2;

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
                break;

            case MSG_DATA_GET_LOGO:
                getDealerLogo();
                break;
            }
        }

    }

    private static final int MSG_UI_UPDATE_LATEST_HITS = 1;
    private static final int MSG_UI_UPDATE_LOGO = 2;
    private static final int MSG_UI_UPDATE_RIGHT_SIDE = 3;
    private Handler mUiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_UI_UPDATE_LATEST_HITS:
                // TODO

                break;

            case MSG_UI_UPDATE_LOGO:
                displayLogo();
                break;

            case MSG_UI_UPDATE_RIGHT_SIDE:
                updateRightSide();
                break;
            }
        }
    };

    private void getDealerLogo() {
        String logoUrl = mSharedPreferences.getString(JsonAdData.DEALER_LOGO,
                null);
        if (TextUtils.isEmpty(logoUrl)) {
            Log.e(TAG,
                    "[getDealerLogo] we haven't got the dealer logo url from the ad data yet!");
            return;
        }

        logd("[getDealerLogo] logoUrl : " + logoUrl);
        InputStream isBitmap = HttpRequest.getUrlStream(logoUrl);
        if (isBitmap == null) {
            logd("[getDealerLogo] can not get logo on " + logoUrl);
            return;
        }

        mLogoBitmap = BitmapFactory.decodeStream(isBitmap);
        mUiHandler.sendEmptyMessage(MSG_UI_UPDATE_LOGO);
        LauncherUtils.saveBitmapFile(this, mLogoBitmap, LOGO_NAME);
    }

    private void getAdJsonData() {
        String mac1 = "mac=00116d063cfc";
        String mac2 = "mac2=a0f4594776de";
        StringBuilder adParamsSb = new StringBuilder();
        adParamsSb.append(mac1);
        adParamsSb.append("&");
        adParamsSb.append(mac2);
        String adUrl = JSON_DATA_AD_URL + "?" + adParamsSb.toString();
        JSONObject jsonAdObj = HttpRequest.getDataFromUrl(adUrl);

        if (jsonAdObj == null) {
            logd("[getAdJsonData] can not get data from " + adUrl);
            return;
        }

        Log.d(TAG, jsonAdObj.toString());
        mJsonAdData = new JsonAdData(this, jsonAdObj);
        mJsonAdData.parseAndSaveToSharedPref();

        Log.d(TAG, mJsonAdData.toString());
    }

    private void getWeatherJsonData() {
        String strCity = mSharedPreferences.getString(JsonAdData.CITY, null);
        String strCountry = mSharedPreferences.getString(JsonAdData.COUNTRY,
                null);
        if (TextUtils.isEmpty(strCity) || TextUtils.isEmpty(strCountry)) {
            Log.e(TAG,
                    "[getWeatherJsonData] we haven't got country and city from the ad data yet!");
            return;
        }

        logd("[getWeatherJsonData] strCity : " + strCity + ", strCountry : "
                + strCountry);

        String weatherPrefix = "q=";
        StringBuilder weatherParamsSb = new StringBuilder();
        weatherParamsSb.append(weatherPrefix);
        weatherParamsSb.append(strCity);
        weatherParamsSb.append(",");
        weatherParamsSb.append(strCountry);
        String weatherUrl = JSON_DATA_WEATHER_URL + "?"
                + weatherParamsSb.toString();
        JSONObject jsonWeatherObj = HttpRequest.getDataFromUrl(weatherUrl);
        if (jsonWeatherObj == null) {
            logd("[getWeatherJsonData] can not get data from " + weatherUrl);
            return;
        }

        Log.d(TAG, jsonWeatherObj.toString());
        mJsonWeatherData = new JsonWeatherData(this, jsonWeatherObj);
        mJsonWeatherData.parseAndSaveToSharedPref();

        Log.d(TAG, mJsonWeatherData.toString());
    }

    private void getJsonData() {
        // advertisement data
        getAdJsonData();

        // get logo from dealer_logo addr only one time on resume
        if (mFirstGetLogo) {
            mDataHandler.sendEmptyMessage(MSG_DATA_GET_LOGO);
            mFirstGetLogo = false;
        }

        // weather data
        getWeatherJsonData();

        mUiHandler.sendEmptyMessage(MSG_UI_UPDATE_LATEST_HITS);
        mUiHandler.sendEmptyMessage(MSG_UI_UPDATE_RIGHT_SIDE);
    }

    private void displayDateAndTime(long timestampMilli) {
        String timeFormat = getFormattedString(timestampMilli, "HH:mm");
        mTvTime.setText(timeFormat);
        mTvTime.setVisibility(View.VISIBLE);

        String dateFormat = getFormattedString(timestampMilli, "MM-dd-yyyy");
        mTvDate.setText(dateFormat);
        mTvDate.setVisibility(View.VISIBLE);
    }

    private void updateRightSide() {
        displayDateAndTime(new Date().getTime());

        mTvCity.setText(mSharedPreferences.getString(JsonAdData.CITY, null));
        mTvCity.setVisibility(View.VISIBLE);

        mIvWeather.setVisibility(View.VISIBLE);

        String weatherMain = mSharedPreferences.getString(
                JsonWeatherData.WEATHER_MAIN, null);
        float temp = mSharedPreferences.getFloat(JsonWeatherData.TEMP, 0.0f);
        if (!TextUtils.isEmpty(weatherMain) && (temp != 0.0f)) {
            StringBuilder weatherSb = new StringBuilder();
            weatherSb.append(weatherMain);
            weatherSb.append(", ");
            weatherSb.append((int) (temp / 10) + "¡æ");
            mTvWeather.setText(weatherSb.toString());
        }
        mTvWeather.setVisibility(View.VISIBLE);

        float tempMax = mSharedPreferences.getFloat(JsonWeatherData.TEMP_MAX,
                0.0f);
        if (tempMax != 0.0f) {
            mTvTempHigh.setText("High: " + (int) (tempMax / 10) + "¡æ");
        }
        mTvTempHigh.setVisibility(View.VISIBLE);

        float tempMin = mSharedPreferences.getFloat(JsonWeatherData.TEMP_MIN,
                0.0f);
        if (tempMin != 0.0f) {
            mTvTempLow.setText("High: " + (int) (tempMin / 10) + "¡æ");
        }
        mTvTempLow.setVisibility(View.VISIBLE);

        float windSpeed = mSharedPreferences.getFloat(JsonWeatherData.SPEED,
                0.0f);
        if (windSpeed != 0.0f) {
            StringBuilder windSb = new StringBuilder();
            windSb.append("Wind:");
            // TODO add wind direction here

            windSb.append(" at ");
            windSb.append(windSpeed + " KPH");
            mTvWind.setText(windSb.toString());
        }
        mTvWind.setVisibility(View.VISIBLE);

        String bottomMsg = mSharedPreferences.getString(JsonAdData.BOTTOM_MSG,
                null);
        if (!TextUtils.isEmpty(bottomMsg)) {
            mTvMsg.setText(bottomMsg);
            mTvMsg.setVisibility(View.VISIBLE);
            mBtnRead.setVisibility(View.VISIBLE);
        }
    }

    private void displayLogo() {
        if (mLogoBitmap == null) {
            mLogoBitmap = LauncherUtils.loadBitmap(this, LOGO_NAME);
        }

        if (mLogoBitmap != null) {
            // Bitmap oldBgBitmap =
            // ((BitmapDrawable)mIvLogo.getBackground()).getBitmap();
            // Bitmap newBgBitmap = Bitmap.createBitmap(mLogoBitmap, 0, 0,
            // oldBgBitmap.getWidth(), oldBgBitmap.getHeight());
            // oldBgBitmap.recycle();
            // mIvLogo.setImageBitmap(newBgBitmap);
            mIvLogo.setImageBitmap(mLogoBitmap);
            mIvLogo.setVisibility(View.VISIBLE);
        }
    }

    private String getFormattedString(long timestamp, String format) {
        Date currentTime = new Date(timestamp);
        SimpleDateFormat timeFormatter = new SimpleDateFormat(format);
        String formattedString = timeFormatter.format(currentTime);

        logd("[getFormatString] formattedString : " + formattedString);
        return formattedString;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ex_launcher);

        initRes();

        HandlerThread ht = new HandlerThread("ExLauncher");
        ht.start();
        mDataHandler = new DataHandler(ht.getLooper());
        mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();

        // start to get the data from the ad server and weather server
        mDataHandler.sendEmptyMessage(MSG_DATA_GET_JSON);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        registerReceiver();

        displayDateAndTime(new Date().getTime());
        displayStatus();
        displayLogo();
        updateRightSide();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        unregisterReceiver();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        // unregisterReceiver(mUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mDataHandler.sendEmptyMessage(MSG_DATA_QUIT);
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
                // get data from servers every minute
                mDataHandler.sendEmptyMessage(MSG_DATA_GET_JSON);
            }
        }
    };

    private void registerReceiver() {
        IntentFilter mediaFilter = new IntentFilter();
        mediaFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        mediaFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        mediaFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        mediaFilter.addDataScheme("file");
        registerReceiver(mediaReceiver, mediaFilter);

        IntentFilter netFilter = new IntentFilter();
        netFilter.addAction(CONNECTIVITY_CHANGED);
        netFilter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(netReceiver, netFilter);
    }

    private void unregisterReceiver() {
        unregisterReceiver(mediaReceiver);
        unregisterReceiver(netReceiver);
    }

    private void initRes() {
        mImageButtonListener = new ImageButtonOnClickListener(this);
        mImageButtonOnFocusChangedListener = new ImageButtonOnFocusChangedListener(
                this);

        mIvLogo = (ImageView) findViewById(R.id.iv_logo);

        initBottomButton();
        initLatestHits();
        initRightSide();
    }

    private void initBottomButton() {
        // the bottom buttons
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
        mIbFirst = (ImageButton) findViewById(R.id.ib_lh_first);
        mIbSecond = (ImageButton) findViewById(R.id.ib_lh_second);
        mIbThird = (ImageButton) findViewById(R.id.ib_lh_third);
        mIbForth = (ImageButton) findViewById(R.id.ib_lh_forth);
        mIbFifth = (ImageButton) findViewById(R.id.ib_lh_fifth);
        mIbSixth = (ImageButton) findViewById(R.id.ib_lh_sixth);
        mIbSeventh = (ImageButton) findViewById(R.id.ib_lh_seventh);

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
        llWeatherAndStatus.getBackground().setAlpha(100);

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
        mIvWifi = (ImageView) findViewById(R.id.iv_wifi);
        mIvEthernet = (ImageView) findViewById(R.id.iv_ethernet);
        mIvUsb = (ImageView) findViewById(R.id.iv_usb);

        mBtnRead.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
            }
        });
    }

    private void displayStatus() {
        mIvWifi.setVisibility(isWifiOn() ? View.VISIBLE : View.GONE);
        mIvEthernet.setVisibility(isEthernetOn() ? View.VISIBLE : View.GONE);
        mIvUsb.setVisibility(isUsbExists() ? View.VISIBLE : View.GONE);
    }

    private boolean isWifiOn() {
        boolean result = false;
        ConnectivityManager con = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = con
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (wifiInfo != null) {
            result = wifiInfo.isConnected();
        }

        return result;
    }

    private boolean isEthernetOn() {
        boolean result = false;
        ConnectivityManager connectivity = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ethInfo = connectivity
                .getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);

        if (ethInfo != null) {
            result = ethInfo.isConnected();
        }

        return result;
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
