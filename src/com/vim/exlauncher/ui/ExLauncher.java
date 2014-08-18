package com.vim.exlauncher.ui;

import static com.vim.exlauncher.data.JsonAdData.AD_PIC_PREFIX;
import static com.vim.exlauncher.data.JsonAdData.PVAD_PIC_PREFIX;
import static com.vim.exlauncher.data.JsonAdData.STATIC_AD_PIC_PREFIX;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import android.R.bool;
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
import android.content.SharedPreferences.Editor;
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
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.serpro.library.String.MCrypt;
import com.vim.exlauncher.R;
import com.vim.exlauncher.data.HttpRequest;
import com.vim.exlauncher.data.JsonAdData;
import com.vim.exlauncher.data.JsonWeatherData;
import com.vim.exlauncher.data.LauncherUtils;
import com.vim.exlauncher.data.MsgContentUtils;
import com.vim.exlauncher.data.Weather;

public class ExLauncher extends Activity {
    private static final String TAG = "ExLauncher";

    private RelativeLayout mRlMain;
    private RelativeLayout mRlLock;
    private TextView mTvLock;

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
    public static ImageView mIvShadowForFirst;

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
    
    // reg
    private RelativeLayout mRlReg;
    private EditText mEtEmail;
    private EditText mEtPhone;
    private Spinner mSpCountry;
    private EditText mEtCity;
    private Button mBtnReg;

    private ImageButtonOnClickListener mImageButtonListener;
    private OnFocusChangeListener mImageButtonOnFocusChangeListener;
    private OnFocusChangeListener mBottomButtonOnFocusChangeListener;
    private BottomImageButton mImageButtonFocus;
    private String mCurrentFirmwareVer;
    private Toast mToast;
    public static Context mContext;
    private ArrayList<Integer> mBottomButtonIdList;

    private static final String SAVED_ETH_MAC = "ubootenv.var.ethmac";
    private static final String SAVED_WIFI_MAC = "ubootenv.var.wifimac";
    private String mSavedMacWifi;
    private String mSavedMacEth;

    private String mRegCountry;

    private static final String TIME_FORMAT = "HH:mm";
    public static final String ETH_ADDRESS_PATH = "/sys/class/net/eth0/address";
    private static final String MAC_PARAM_ETH = "mac=";
    private static final String MAC_PARAM_WIFI = "mac2=";
    private static final String WEATHER_QUERY_PREFIX = "q=";

    private static final String FIRMWARE_VERSION_PROP = "otaupdater.otaver";

    private final static String DATA_URL = "/data/data/";
    private static final String EXTERNAL_STORAGE = "/storage/external_storage";
    private static final String SD_PATH = "/storage/external_storage/sdcard1";

    // http://mymobiletvhd.com/android/fitv.php?mac=00116d063cfc&mac2=a0f4594776de
    private static final String JSON_DATA_AD_URL = "http://mymobiletvhd.com/android/fitv.php";
    private static final String JSON_DATA_WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather?units=metric";
    private static final String LOGO_NAME = "logo.png";

    private static final String REGISTER_URL = "http://mymoviletvhd.com/registerbox.php?data=";

    public static final int MAX_AD_PIC_ROTATE_NUM = 4;
    private static final int MAX_VIDEO_PIC_ROTATE_NUM = 4;
    private static final int MAX_STATIC_AD_PIC_NUM = 5;

    private enum PicType {
        PIC_AD, PIC_VIDEO_AD, PIC_STATIC
    };

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
    private boolean mStartGetPic = false;

    // insure MSG_UI_DISPLAY_LATEST_HITS msg be sent only once
    private boolean mStartUpdateLatestHits = false;
    private DataHandler mDataHandler;
    private PicHandler mPicHandler;
    private Weather mWeather;
    private AlertDialog mUpdateDialog;
    private AlertDialog mRegisterDialog;

    private static final int MSG_DATA_QUIT = 0;
    private static final int MSG_DATA_GET_JSON = 1;
    private static final int MSG_DATA_GET_JSON_LOOP = 2;

    private static final int GET_JSON_DELAY = 15 * 60 * 1000;
    private static final int GET_JSON_DELAY_FOR_NETRECEIVER = 2 * 1000;

    public static final boolean S_GRID_LAYOUT = true;

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

            case MSG_DATA_GET_JSON_LOOP:
                this.sendEmptyMessageDelayed(MSG_DATA_GET_JSON_LOOP,
                        GET_JSON_DELAY);
                getJsonData();
                break;
            }
        }

    }

    private static final int MSG_PIC_QUIT = 0;
    private static final int MSG_PIC_GET_DATA = 1;
    private static final int MSG_PIC_CHECK_DATA = 2;

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
                mStartGetPic = false;
                break;

            case MSG_PIC_CHECK_DATA:
                checkLogoPic();
                checkLatestHitsPic();
                break;
            }
        }

    }

    private static final int MSG_UI_QUIT = 0;
    private static final int MSG_UI_DISPLAY_LATEST_HITS = 1;
    private static final int MSG_UI_DISPLAY_LOGO = 2;
    private static final int MSG_UI_CHECK_OTA_UPDATE_INFO = 3;
    private static final int MSG_UI_DISPLAY_RIGHT_SIDE = 4;
    private static final int MSG_UI_DISPLAY_BOTTOM_BUTTON = 5;
    private static final int MSG_UI_DISPLAY_LOCK = 6;
    private static final int MSG_UI_CHECK_REGISTER_STATUS = 7;

    private static final int MSG_UI_LAYTEST_HITS_UPDATE_INTERNAL = 5 * 1000;
    private static final int MSG_UI_DEALER_LOGO_UPDATE_INTERNAL = 5 * 1000;

    private Handler mUiHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_UI_QUIT:
                this.getLooper().quit();
                break;

            case MSG_UI_DISPLAY_LATEST_HITS:
                displayLatestHits();
                this.sendEmptyMessageDelayed(MSG_UI_DISPLAY_LATEST_HITS,
                        MSG_UI_LAYTEST_HITS_UPDATE_INTERNAL);
                break;

            case MSG_UI_DISPLAY_LOGO:
                displayLogo();
                this.sendEmptyMessageDelayed(MSG_UI_DISPLAY_LOGO,
                        MSG_UI_DEALER_LOGO_UPDATE_INTERNAL);
                break;

            case MSG_UI_CHECK_OTA_UPDATE_INFO:
                checkOtaUpdateInfo();
                break;

            case MSG_UI_DISPLAY_RIGHT_SIDE:
                displayRightSide();
                break;

            case MSG_UI_DISPLAY_BOTTOM_BUTTON:
                displayBottomButtom();
                break;

            case MSG_UI_DISPLAY_LOCK:
                displayLockStatus();
                break;

            case MSG_UI_CHECK_REGISTER_STATUS:
                checkRegisterStatus();
                break;
            }
        }
    };

    private void checkRegisterStatus() {
        boolean reg = false;
        if ((mJsonAdData != null) && mJsonAdData.isReg()) {
            reg = true;
        }

        if (!reg) {
//          if (mRegisterDialog == null) {
//              showRegisterDialog();
//          }
        }
        
        mRlReg.setVisibility(reg ? View.GONE : View.VISIBLE);
        mRlMain.setVisibility(reg ? View.VISIBLE : View.GONE);
    }

    private static final String EQUAL = "=";
    private static final String CONJUNCTION = "&";
    private static final String EMAIL = "email";
    private static final String PHONE = "phone";
    private static final String COUNTRY = "Country";
    private static final String CITY = "city";
    private static final String WIFI_MAC = "wifimac";
    private static final String ETH_MAC = "mac";

    private void registerToServer(String strEmail, String strPhone,
            String strCountry, String strCity) {
        String wifiMac = mSharedPreferences.getString(SAVED_WIFI_MAC, null);
        String ethMac = mSharedPreferences.getString(SAVED_ETH_MAC, null);

        if (TextUtils.isEmpty(wifiMac) || TextUtils.isEmpty(ethMac)) {
            loge("[registerToServer] wifiMac or ethMac is empty!");
            return;
        }

        // "email=useremail&phone=userphone&Country=userCountry&city=userCity&wifimac=userwifimac&mac=userEthernetmac"
        MCrypt mcrypt = new MCrypt();
        String encrypted = "";
        StringBuilder sb = new StringBuilder();
        sb.append(EMAIL + EQUAL + strEmail);
        sb.append(CONJUNCTION);
        sb.append(PHONE + EQUAL + strPhone);
        sb.append(CONJUNCTION);
        sb.append(COUNTRY + EQUAL + strCountry);
        sb.append(CONJUNCTION);
        sb.append(CITY + EQUAL + strCity);
        sb.append(CONJUNCTION);
        sb.append(WIFI_MAC + EQUAL + wifiMac);
        sb.append(CONJUNCTION);
        sb.append(ETH_MAC + EQUAL + ethMac);

        try {
            encrypted = MCrypt.bytesToHex(mcrypt.encrypt(sb.toString()));
        } catch (Exception e) {
            loge("[registerToServer] encrypt error!");
            encrypted = "";
        }

        if (TextUtils.isEmpty(encrypted)) {
            loge("[registerToServer] encrypted data is empty!");
            return;
        }

        String regUrl = REGISTER_URL + encrypted;
        
        logd("[registerToServer] regUrl : " + regUrl);
        
        InputStream result = HttpRequest.getStreamFromUrl(regUrl);
        logd("[registerToServer] result : " + result);
        if (result == null) {
            loge("[registerToServer] error when registerring on " + regUrl);
            return;
        } else {
            Toast.makeText(this, R.string.register_ok, Toast.LENGTH_LONG)
                    .show();
            
            // register successful, try to check lock status
            displayLockStatus();
            mRlReg.setVisibility(View.GONE);
        }
    }

    private void showErrorDailog(String errStr) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.error);
        builder.setMessage(errStr);
        builder.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub

                    }
                });
        builder.create().show();
    }

    private void showRegisterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.register_info);

        View view = this.getLayoutInflater().inflate(
                R.layout.layout_dlg_register, null);
        final EditText etEmail = (EditText) view.findViewById(R.id.et_email);
        final EditText etPhone = (EditText) view.findViewById(R.id.et_phone);
        final EditText etCity = (EditText) view.findViewById(R.id.et_city);
        final Spinner spCountry = (Spinner) view
                .findViewById(R.id.spinner_country);

        String[] regCountry = getResources()
                .getStringArray(R.array.reg_country);
        mRegCountry = regCountry[0];
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, regCountry);
        spCountry.setAdapter(adapter);
        spCountry
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent,
                            View view, int position, long id) {
                        // TODO Auto-generated method stub
                        mRegCountry = parent.getItemAtPosition(position)
                                .toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // TODO Auto-generated method stub

                    }

                });

        Button btnReg = (Button) view.findViewById(R.id.btn_register);

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (!Patterns.EMAIL_ADDRESS.matcher(
                        etEmail.getText().toString()).matches()) {
                    showErrorDailog(ExLauncher.this
                            .getString(R.string.error_email));
                    return;
                }

                if (!Patterns.PHONE.matcher(etPhone.getText().toString())
                        .matches()) {
                    showErrorDailog(ExLauncher.this
                            .getString(R.string.error_phone));
                    return;
                }

                if (TextUtils.isEmpty(etCity.getText().toString())) {
                    showErrorDailog(ExLauncher.this
                            .getString(R.string.error_city));
                    return;
                }

                registerToServer(etEmail.getText().toString(), etPhone
                        .getText().toString(), mRegCountry, etCity.getText()
                        .toString());
                mRegisterDialog.dismiss();
                mRegisterDialog = null;
            }
        });

        builder.setView(view);
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                // TODO Auto-generated method stub
                mRegisterDialog = null;
            }
        });

        mRegisterDialog = builder.create();
        mRegisterDialog.show();
    }

    public static void resetMsgStatusAndRefresh(boolean status) {
        if (mJsonAdData != null) {
            mJsonAdData.setMsgStatus(status);
        }

        displayBottomMsgInfo();
    }

    private void recyleOldBitmap(Map<String, Bitmap> mapOldBitmap) {
        // recyle the old resource
        if ((mapOldBitmap != null) && (mapOldBitmap.size() > 0)) {
            Set<String> key = mapOldBitmap.keySet();
            for (Iterator it = key.iterator(); it.hasNext();) {
                String s = (String) it.next();
                mapOldBitmap.get(s).recycle();
            }
        }

    }

    private void getLatestPic(final PicType picType) {
        int picMaxSize = -1;
        String picPrefix = null;
        if (picType == PicType.PIC_AD) {
            picMaxSize = MAX_AD_PIC_ROTATE_NUM;
            picPrefix = AD_PIC_PREFIX;
        } else if (picType == PicType.PIC_VIDEO_AD) {
            picMaxSize = MAX_VIDEO_PIC_ROTATE_NUM;
            picPrefix = PVAD_PIC_PREFIX;
        } else if (picType == PicType.PIC_STATIC) {
            picMaxSize = MAX_STATIC_AD_PIC_NUM;
            picPrefix = STATIC_AD_PIC_PREFIX;
        }

        synchronized (ExLauncher.this) {
            Map<String, String> picMap = new HashMap<String, String>();
            for (int i = 0; i < picMaxSize; i++) {
                String picKey = picPrefix + (i + 1);
                String picUrl = mSharedPreferences.getString(picKey, null);

                logd("[getLatestPic] picKey : " + picKey + ", picUrl : "
                        + picUrl);
                if (!TextUtils.isEmpty(picUrl)) {
                    picMap.put(picKey, picUrl);
                }
            }

            if (picMap.size() == 0) {
                logw("[getLatestPic] we don't get any picture url of type "
                        + picType + " from ad data!");
                return;
            }

            InputStream isBitmap = null;
            Map<String, Bitmap> mapBitmap = null;
            Map<String, Bitmap> mapOldBitmap = null;
            if (picType == PicType.PIC_AD) {
                mapOldBitmap = mAdvBitmapMap;
                mAdvBitmapMap = new HashMap<String, Bitmap>();
                mapBitmap = mAdvBitmapMap;
            } else if (picType == PicType.PIC_VIDEO_AD) {
                mapOldBitmap = mPVAdvBitmapMap;
                mPVAdvBitmapMap = new HashMap<String, Bitmap>();
                mapBitmap = mPVAdvBitmapMap;
            } else if (picType == PicType.PIC_STATIC) {
                mapOldBitmap = mStaticAdvBitmapMap;
                mStaticAdvBitmapMap = new HashMap<String, Bitmap>();
                mapBitmap = mStaticAdvBitmapMap;
            }

            // recyle the old resource
            recyleOldBitmap(mapOldBitmap);

            for (int i = 0; i < picMap.size(); i++) {
                String key = picPrefix + (i + 1);
                String url = picMap.get(key);
                isBitmap = HttpRequest.getStreamFromUrl(url);

                if (isBitmap == null) {
                    logw("[getLatestPic] error when getting logo on " + url
                            + " for key : " + key);
                    continue;
                }

                Bitmap bitmap = BitmapFactory.decodeStream(isBitmap);
                if (bitmap != null) {
                    mapBitmap.put(key, bitmap);
                    LauncherUtils.saveBitmap(ExLauncher.this, bitmap, key
                            + ".png");
                    logi("[getLatestPic] " + key + ".png"
                            + " has been downloaded.");
                }
            }
        }
    }

    private void getLatestHitsPic() {
        getLatestPic(PicType.PIC_VIDEO_AD);
        // loadAdvPic();

        getLatestPic(PicType.PIC_AD);
        // loadPVAdvPic();

        getLatestPic(PicType.PIC_STATIC);
        // loadStaticAdvPic();
    }

    private void checkLogoPic() {
        if (mLogoBitmap != null) {
            logi("[checkLogoPic] mLogoBitmap have been downloaded.");
            return;
        }

        if (!hasNetworkConnected()) {
            loge("[checkLogoPic] there is no any connection!");
            return;
        }

        synchronized (ExLauncher.this) {
            new Thread("checkLogoPic") {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    String logoUrlString = mSharedPreferences.getString(
                            JsonAdData.DEALER_LOGO, null);

                    if (TextUtils.isEmpty(logoUrlString)) {
                        logw("[checkLogoPic] logo url is empty!");
                        return;
                    }

                    logoUrlString = logoUrlString.replace("\\/", "/");

                    InputStream isBitmap = HttpRequest
                            .getStreamFromUrl(logoUrlString);
                    if (isBitmap == null) {
                        logw("[checkLogoPic] error when getting logo on "
                                + logoUrlString);
                        return;
                    }

                    Bitmap bitmap = BitmapFactory.decodeStream(isBitmap);

                    if (bitmap != null) {
                        if (mLogoBitmap != null) {
                            mLogoBitmap.recycle();
                        }

                        mLogoBitmap = bitmap;
                        LauncherUtils.saveBitmap(ExLauncher.this, mLogoBitmap,
                                LOGO_NAME);
                    }
                }
            }.start();
        }
    }

    private void checkLatestHitsPic() {
        checkPicAllDownloadedByType(PicType.PIC_VIDEO_AD);
        checkPicAllDownloadedByType(PicType.PIC_AD);
        checkPicAllDownloadedByType(PicType.PIC_STATIC);
    }

    private void getDealerLogo() {
        String logoUrlString = mSharedPreferences.getString(
                JsonAdData.DEALER_LOGO, null);

        if (TextUtils.isEmpty(logoUrlString)) {
            logw("[getDealerLogo] logo url is empty!");
            return;
        }

        logoUrlString = logoUrlString.replace("\\/", "/");

        InputStream isBitmap = HttpRequest.getStreamFromUrl(logoUrlString);
        if (isBitmap == null) {
            logw("[getDealerLogo] error when getting logo on " + logoUrlString);
            return;
        }

        Bitmap bitmap = BitmapFactory.decodeStream(isBitmap);

        if (bitmap != null) {
            if (mLogoBitmap != null) {
                mLogoBitmap.recycle();
            }

            mLogoBitmap = bitmap;
            LauncherUtils.saveBitmap(this, mLogoBitmap, LOGO_NAME);
        }
    }

    private void getAdData() {
        String macEth = LauncherUtils.getEthMac(ETH_ADDRESS_PATH);
        String macWifi = LauncherUtils.getWifiMac(this);

        logd("[getAdData] savedMacEth : " + mSavedMacEth + ", mSavedMacWifi : "
                + mSavedMacWifi + ", macEth : " + macEth + ", macWifi : "
                + macWifi);
        if (TextUtils.isEmpty(macEth)) {
            Log.e(TAG, "[getAdData] macEth is empty, try to use mSavedMacEth!");

            if (TextUtils.isEmpty(mSavedMacEth)) {
                Log.e(TAG, "[getAdData] mSavedMacEth is empty!");
                return;
            } else {
                macEth = mSavedMacEth;
            }
        } else {
            macEth = macEth.replace(":", "");
        }

        if (TextUtils.isEmpty(macWifi)) {
            Log.e(TAG,
                    "[getAdData] macWifi is empty, try to use mSavedMacWifi!");

            if (TextUtils.isEmpty(mSavedMacWifi)) {
                Log.e(TAG, "[getAdData] mSavedMacWifi is empty!");
                return;
            } else {
                macWifi = mSavedMacWifi;
            }
        } else {
            macWifi = macWifi.replace(":", "");
        }
        logd("[getAdData] macEth : " + macEth + ", macWifi : " + macWifi);

        if (TextUtils.isEmpty(mSavedMacEth)) {
            mSavedMacEth = macEth;
            logd("[getAdData] save mSavedMacEth : " + mSavedMacEth
                    + " to mSharedPreferences");
            mSharedPreferences.edit().putString(SAVED_ETH_MAC, mSavedMacEth)
                    .commit();
        }

        if (TextUtils.isEmpty(mSavedMacWifi)) {
            mSavedMacWifi = macWifi;
            logd("[getAdData] save mSavedMacWifi : " + mSavedMacWifi
                    + " to mSharedPreferences");
            mSharedPreferences.edit().putString(SAVED_WIFI_MAC, mSavedMacWifi)
                    .commit();
        }

        // String macEth = "00116d063cfc";
        // String macWifi = "a0f4594776de";

        StringBuilder adParamsSb = new StringBuilder();
        adParamsSb.append(MAC_PARAM_ETH + macEth);
        adParamsSb.append("&");
        adParamsSb.append(MAC_PARAM_WIFI + macWifi);
        String adUrl = JSON_DATA_AD_URL + "?" + adParamsSb.toString();
        logd("[getAdData] adUrl : " + adUrl);
        JSONObject jsonAdObj = HttpRequest.getDataFromUrl(adUrl);

        if (jsonAdObj == null) {
            logw("[getAdData] can not get ad data from " + adUrl);
            return;
        }

        // logd("jsonAdObj : " + jsonAdObj.toString());
        mJsonAdData = new JsonAdData(this, jsonAdObj);
        mJsonAdData.parseAndSaveData();
        logd(mJsonAdData.toString());
    }

    private void getWeatherData() {
        String city = mSharedPreferences.getString(JsonAdData.CITY, null);
        String country = mSharedPreferences.getString(JsonAdData.COUNTRY, null);
        if (TextUtils.isEmpty(city) || TextUtils.isEmpty(country)) {
            logw("[getWeatherData] we didn't get city and country from ad data yet!");
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
            logw("[getAdData] can not get weather data from " + weatherUrl);
            return;
        }

        // logd("jsonWeatherObj : " + jsonWeatherObj.toString());
        mJsonWeatherData = new JsonWeatherData(this, jsonWeatherObj);
        mJsonWeatherData.parseAndSaveData();
        logd(mJsonWeatherData.toString());
    }

    private void getJsonData() {
        // advertisement data
        if (!hasNetworkConnected()) {
            loge("[getJsonData] there is not any connection!");
            return;
        }

        getAdData();

        if (!mStartGetPic) {
            mStartGetPic = true;
            mPicHandler.sendEmptyMessage(MSG_PIC_GET_DATA);
        }

        // weather data
        getWeatherData();

        mUiHandler.sendEmptyMessage(MSG_UI_CHECK_OTA_UPDATE_INFO);
        mUiHandler.sendEmptyMessage(MSG_UI_DISPLAY_RIGHT_SIDE);
        mUiHandler.sendEmptyMessage(MSG_UI_DISPLAY_BOTTOM_BUTTON);
        mUiHandler.sendEmptyMessage(MSG_UI_DISPLAY_LOCK);
        mUiHandler.sendEmptyMessage(MSG_UI_CHECK_REGISTER_STATUS);
    }

    private boolean hasNetworkConnected() {
        boolean isnetworkConnected = false;
        boolean isWifiConnected = false;
        boolean isEthConnected = false;
        boolean isMobileConnected = false;
        try {
            ConnectivityManager connectManager = (ConnectivityManager) this
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connectManager.getActiveNetworkInfo();
            if (networkInfo != null) {
                State networkState = networkInfo.getState();
                isnetworkConnected = (networkState == State.CONNECTED);
            }
            logd("[hasNetworkConnected] isnetworkConnected : "
                    + isnetworkConnected);

            NetworkInfo wifiNetworkInfo = connectManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (wifiNetworkInfo != null) {
                State wifiState = wifiNetworkInfo.getState();
                isWifiConnected = (wifiState == State.CONNECTED);
            }
            logd("[hasNetworkConnected] isWifiConnected : " + isWifiConnected);

            NetworkInfo ethNetworkInfo = connectManager
                    .getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
            if (ethNetworkInfo != null) {
                State ethState = ethNetworkInfo.getState();
                isEthConnected = (ethState == State.CONNECTED);
            }
            logd("[hasNetworkConnected] isEthConnected : " + isEthConnected);

            NetworkInfo mobileNetworkInfo = connectManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mobileNetworkInfo != null) {
                State mobileState = mobileNetworkInfo.getState();
                isMobileConnected = (mobileState == State.CONNECTED);
            }
            logd("[hasNetworkConnected] isMobileConnected : "
                    + isMobileConnected);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isWifiConnected || isEthConnected || isnetworkConnected;
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
                            loge("Activity not found!");
                            anf.printStackTrace();

                            if (mToast != null) {
                                mToast.cancel();
                            }
                            mToast = Toast.makeText(ExLauncher.this,
                                    R.string.otaupdatecenter_not_found,
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
            logw("[checkOtaUpdateInfo] we haven't got firmware ver from ad data yet!");
            return;
        }

        String lockStatusStr = mSharedPreferences.getString(
                JsonAdData.LOCK_STATUS, "false");
        boolean lock = false;

        if (!TextUtils.isEmpty(lockStatusStr)
                && lockStatusStr.equalsIgnoreCase("true")) {
            lock = true;
        }

        if (lock) {
            logw("[checkOtaUpdateInfo] The system has been locked!");
            return;
        }

        try {
            int intCurrentVer = Integer.parseInt(mCurrentFirmwareVer);
            int intVerFromJson = Integer.parseInt(firmwareVer);

            if ((intVerFromJson > intCurrentVer) && (mUpdateDialog == null)) {
                showNewVersionUpdate();
            }
        } catch (NumberFormatException nfe) {
            loge("[checkOtaUpdateInfo] parse " + mCurrentFirmwareVer + " and "
                    + firmwareVer + " error!");
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

    private void displayLockStatus() {
        boolean lock = false;

        if ((mJsonAdData != null) && mJsonAdData.getLockStatus()) {
            lock = true;
        }

        if (lock) {
            mTvLock.setText(mJsonAdData.getLockReason());
        }

        mRlLock.setVisibility(lock ? View.VISIBLE : View.GONE);
        mRlMain.setVisibility(lock ? View.GONE : View.VISIBLE);
    }

    private void displayBottomButtom() {
        boolean show = true;
        if ((mJsonAdData != null) && !mJsonAdData.getMenuStatus()) {
            show = false;
        }

        BottomImageButton button = null;
        for (int i = 0; i < mBottomButtonIdList.size(); i++) {
            button = (BottomImageButton) findViewById(mBottomButtonIdList
                    .get(i));
            button.setFocusable(show);
            button.setClickable(show);
            button.setFocusableInTouchMode(show);
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

    private void checkPicAllDownloadedByType(final PicType picType) {
        int picMaxSize = -1;
        Map<String, Bitmap> mapBitmap = null;
        String picPrefix = null;
        if (picType == PicType.PIC_AD) {
            picMaxSize = MAX_AD_PIC_ROTATE_NUM;
            mapBitmap = mAdvBitmapMap;
            picPrefix = AD_PIC_PREFIX;
        } else if (picType == PicType.PIC_VIDEO_AD) {
            picMaxSize = MAX_VIDEO_PIC_ROTATE_NUM;
            mapBitmap = mPVAdvBitmapMap;
            picPrefix = PVAD_PIC_PREFIX;
        } else if (picType == PicType.PIC_STATIC) {
            picMaxSize = MAX_STATIC_AD_PIC_NUM;
            mapBitmap = mStaticAdvBitmapMap;
            picPrefix = STATIC_AD_PIC_PREFIX;
        }

        if ((picMaxSize == -1) || TextUtils.isEmpty(picPrefix)) {
            loge("[checkPicAllDownloaded] param picType : " + picType
                    + " error!");
            return;
        }

        if ((mapBitmap != null) && (mapBitmap.size() == picMaxSize)) {
            logi("[checkPicAllDownloaded] all picture for type " + picType
                    + " have been downloaded.");
            return;
        }

        if (!hasNetworkConnected()) {
            loge("[checkPicAllDownloadedByType] picType : " + picType
                    + ", there is not any connection!");
            return;
        }

        synchronized (ExLauncher.this) {
            final ArrayList<String> needToDownloadKey = new ArrayList<String>();
            final Map<String, String> needToDownloadUrl = new HashMap<String, String>();
            for (int i = 0; i < picMaxSize; i++) {
                String picKey = picPrefix + (i + 1);
                Bitmap bitmap = LauncherUtils.loadBitmap(this, picKey + ".png");
                if (bitmap == null) {
                    logw("[checkPicAllDownloaded] " + picKey + ".png"
                            + " has not been downloaded.");
                    String picUrl = mSharedPreferences.getString(picKey, null);
                    if (TextUtils.isEmpty(picUrl)) {
                        logw("[checkPicAllDownloaded] picUrl for " + picKey
                                + ".png" + " is empty. just continue.");
                        continue;
                    }

                    needToDownloadKey.add(picKey);
                    needToDownloadUrl.put(picKey, picUrl);
                } else {
                    bitmap.recycle();
                }
            }

            if (needToDownloadKey.size() > 0) {
                logd("[checkPicAllDownloaded] start to download the rest "
                        + needToDownloadKey.size() + " picture(s) for type "
                        + picType);
                if (mapBitmap == null) {
                    if (picType == PicType.PIC_AD) {
                        mAdvBitmapMap = new HashMap<String, Bitmap>();
                        mapBitmap = mAdvBitmapMap;
                    } else if (picType == PicType.PIC_VIDEO_AD) {
                        mPVAdvBitmapMap = new HashMap<String, Bitmap>();
                        mapBitmap = mPVAdvBitmapMap;
                    } else if (picType == PicType.PIC_STATIC) {
                        mStaticAdvBitmapMap = new HashMap<String, Bitmap>();
                        mapBitmap = mStaticAdvBitmapMap;
                    }
                }
                final Map<String, Bitmap> picBitmap = mapBitmap;

                new Thread("checkPicAllDownloaded_" + picType) {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        for (int i = 0; i < needToDownloadKey.size(); i++) {
                            String key = needToDownloadKey.get(i);
                            String url = needToDownloadUrl.get(key);
                            InputStream isBitmap = HttpRequest
                                    .getStreamFromUrl(url);

                            if (isBitmap == null) {
                                logw("[thread checkPicAllDownloaded_" + picType
                                        + "] error when getting picture on "
                                        + url);
                                continue;
                            }

                            Bitmap bitmap = BitmapFactory
                                    .decodeStream(isBitmap);
                            if (bitmap != null) {
                                LauncherUtils.saveBitmap(ExLauncher.this,
                                        bitmap, key + ".png");
                                picBitmap.put(key, bitmap);
                                logi("[checkPicAllDownloaded] " + key + ".png"
                                        + " has been downloaded.");
                            }
                        }
                    }
                }.start();
            }
        }
    }

    private void loadAdvPic() {
        recyleOldBitmap(mAdvBitmapMap);
        mAdvBitmapMap = new HashMap<String, Bitmap>();
        for (int i = 0; i < MAX_AD_PIC_ROTATE_NUM; i++) {
            String advKey = AD_PIC_PREFIX + (i + 1);
            Bitmap bitmap = LauncherUtils.loadBitmap(this, advKey + ".png");
            if (bitmap != null) {
                mAdvBitmapMap.put(advKey, bitmap);
            }
        }
    }

    private void displayAdvPic() {
        synchronized (mAdvBitmapMap) {
            if ((mAdvBitmapMap == null)
                    || (mAdvBitmapMap.size() < MAX_AD_PIC_ROTATE_NUM)) {
                logd("[displayAdvPic] mAdvBitmapMap is not full, try to load from the file");
                loadAdvPic();
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

    private void loadPVAdvPic() {
        recyleOldBitmap(mPVAdvBitmapMap);
        mPVAdvBitmapMap = new HashMap<String, Bitmap>();
        for (int i = 0; i < MAX_VIDEO_PIC_ROTATE_NUM; i++) {
            String pvadvKey = PVAD_PIC_PREFIX + (i + 1);
            Bitmap bitmap = LauncherUtils.loadBitmap(this, pvadvKey + ".png");
            if (bitmap != null) {
                mPVAdvBitmapMap.put(pvadvKey, bitmap);
            }
        }
    }

    private void displayPVAdvPic() {
        synchronized (mPVAdvBitmapMap) {
            if ((mPVAdvBitmapMap == null)
                    || (mPVAdvBitmapMap.size() < MAX_VIDEO_PIC_ROTATE_NUM)) {
                logd("[displayPVAdvPic] mPVAdvBitmapMap is not full, try to load from the file");
                loadPVAdvPic();
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

    private void loadStaticAdvPic() {
        recyleOldBitmap(mStaticAdvBitmapMap);
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

    private void displayStaticAdvPic() {
        synchronized (mStaticAdvBitmapMap) {
            if ((mStaticAdvBitmapMap == null)
                    || (mStaticAdvBitmapMap.size() < MAX_STATIC_AD_PIC_NUM)) {
                logd("[displayStaticAdvPic] mStaticAdvBitmapMap is not full, try to load from the file");
                loadStaticAdvPic();
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
            logw("[displayLogo] we don't get logo from ad data yet!");
            return;
        }

        mIvLogo.setBackgroundDrawable(new BitmapDrawable(mLogoBitmap));
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

    private void removeAllOldData() {
        logd("[removeAllOldData]");
        Editor editor = mSharedPreferences.edit();
        editor.remove(JsonAdData.CITY).commit();
        editor.remove(JsonAdData.COUNTRY).commit();
        editor.remove(JsonAdData.DEALER_LOGO).commit();
        editor.remove(JsonAdData.FIRMWARE_VERSION).commit();

        editor.remove(JsonAdData.ADV1).commit();
        editor.remove(JsonAdData.ADV2).commit();
        editor.remove(JsonAdData.ADV3).commit();
        editor.remove(JsonAdData.ADV4).commit();

        editor.remove(JsonAdData.URL_AD1).commit();
        editor.remove(JsonAdData.URL_AD2).commit();
        editor.remove(JsonAdData.URL_AD3).commit();
        editor.remove(JsonAdData.URL_AD4).commit();

        editor.remove(JsonAdData.PVADV1).commit();
        editor.remove(JsonAdData.PVADV2).commit();
        editor.remove(JsonAdData.PVADV3).commit();
        editor.remove(JsonAdData.PVADV4).commit();

        editor.remove(JsonAdData.VADV1).commit();
        editor.remove(JsonAdData.VADV2).commit();
        editor.remove(JsonAdData.VADV3).commit();
        editor.remove(JsonAdData.VADV4).commit();

        editor.remove(JsonAdData.S_ADV1).commit();
        editor.remove(JsonAdData.S_ADV2).commit();
        editor.remove(JsonAdData.S_ADV3).commit();
        editor.remove(JsonAdData.S_ADV4).commit();
        editor.remove(JsonAdData.S_ADV5).commit();

        editor.remove(JsonAdData.S_URL_AD1).commit();
        editor.remove(JsonAdData.S_URL_AD2).commit();
        editor.remove(JsonAdData.S_URL_AD3).commit();
        editor.remove(JsonAdData.S_URL_AD4).commit();
        editor.remove(JsonAdData.S_URL_AD5).commit();

        editor.remove(JsonAdData.BOTTOM_MSG).commit();

        editor.remove(JsonWeatherData.WEATHER_MAIN).commit();
        editor.remove(JsonWeatherData.ICON).commit();
        editor.remove(JsonWeatherData.TEMP).commit();
        editor.remove(JsonWeatherData.TEMP_MAX).commit();
        editor.remove(JsonWeatherData.TEMP_MIN).commit();
        editor.remove(JsonWeatherData.SPEED).commit();
        editor.remove(JsonWeatherData.DEG).commit();

        final String PIC_FILE_PATH_PREFIX = DATA_URL
                + getPackageName().toString() + "/files";
        int i = 0;
        for (; i < MAX_AD_PIC_ROTATE_NUM; i++) {
            LauncherUtils.deleteFile(PIC_FILE_PATH_PREFIX, AD_PIC_PREFIX
                    + (i + 1) + ".png");
        }

        i = 0;
        for (; i < MAX_VIDEO_PIC_ROTATE_NUM; i++) {
            LauncherUtils.deleteFile(PIC_FILE_PATH_PREFIX, PVAD_PIC_PREFIX
                    + (i + 1) + ".png");
        }

        i = 0;
        for (; i < MAX_STATIC_AD_PIC_NUM; i++) {
            LauncherUtils.deleteFile(PIC_FILE_PATH_PREFIX, STATIC_AD_PIC_PREFIX
                    + (i + 1) + ".png");
        }

        LauncherUtils.deleteFile(PIC_FILE_PATH_PREFIX, LOGO_NAME);

        MsgContentUtils.deleteAllMsg(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logd("[onCreate]");

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
        mSavedMacEth = mSharedPreferences.getString(SAVED_ETH_MAC, null);
        mSavedMacWifi = mSharedPreferences.getString(SAVED_WIFI_MAC, null);

        // mDataHandler.sendEmptyMessage(MSG_DATA_GET_JSON);

        mIbTv.requestFocus();

        // startService(new Intent(this, GroupService.class));

        removeAllOldData();

        registerStatusReceiver();
    }

    @Override
    protected void onStart() {
        super.onStart();
        logd("[onStart]");

        mDataHandler.sendEmptyMessageDelayed(MSG_DATA_GET_JSON_LOOP,
                GET_JSON_DELAY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        logd("[onResume]");
        displayLockStatus();
        checkRegisterStatus();
        displayBottomButtom();
        displayStatus();
        displayRightSide();

        if (!mStartUpdateLatestHits) {
            mUiHandler.sendEmptyMessage(MSG_UI_DISPLAY_LATEST_HITS);
            mUiHandler.sendEmptyMessage(MSG_UI_DISPLAY_LOGO);
            mStartUpdateLatestHits = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        logd("[onPause]");
    }

    @Override
    protected void onStop() {
        super.onStop();
        logd("[onStop]");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        logd("[onDestroy]");
        unregisterStatusReceiver();
        mDataHandler.sendEmptyMessage(MSG_DATA_QUIT);
        mPicHandler.sendEmptyMessage(MSG_PIC_QUIT);
        stopService(new Intent(this, GroupService.class));
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
            logd("netReceiver action : " + action);

            if (action.equals(Intent.ACTION_TIME_TICK)) {
                displayDateAndTime();
                mPicHandler.sendEmptyMessage(MSG_PIC_CHECK_DATA);
            } else {
                displayStatus();
                if (hasNetworkConnected()) {
                    logd("[netReceiver] hasNetworkConnected : true");
                    mDataHandler.sendEmptyMessageDelayed(MSG_DATA_GET_JSON,
                            GET_JSON_DELAY_FOR_NETRECEIVER);
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
        netFilter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(netReceiver, netFilter);
    }

    private void unregisterStatusReceiver() {
        unregisterReceiver(mediaReceiver);
        unregisterReceiver(netReceiver);
    }

    private void initRes() {
        mRlMain = (RelativeLayout) findViewById(R.id.rl_main);
        mRlLock = (RelativeLayout) findViewById(R.id.rl_lock);
        mRlReg = (RelativeLayout) findViewById(R.id.rl_reg);
        mTvLock = (TextView) findViewById(R.id.tv_lock);

        mImageButtonListener = new ImageButtonOnClickListener(this);
        mImageButtonOnFocusChangeListener = new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                Log.d(TAG, "view : " + view + ", hasFocus : " + hasFocus);

                if (hasFocus) {
                    ((BottomImageButton) view).setShadowEffect();
                    mImageButtonFocus = (BottomImageButton) view;
                    mAlShadow.setVisibility(View.VISIBLE);
                } else {
                    mAlShadow.setVisibility(View.GONE);
                }
            }
        };

        // mBottomButtonOnFocusChangeListener = new
        // ScaleAnimOnFocusChangeListener(
        // this);

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
        mIvShadowForFirst = (ImageView) findViewById(R.id.iv_shadow_focus_for_first);
        mAlShadow.setVisibility(View.GONE);

        initBottomButton();
        initLatestHits();
        initRightSide();
        initReg();
    }
    
    private void initReg() {
        mEtCity = (EditText) findViewById(R.id.et_city);
        mEtEmail = (EditText) findViewById(R.id.et_email);
        mSpCountry = (Spinner) findViewById(R.id.spinner_country);
        mEtPhone = (EditText) findViewById(R.id.et_phone);
        
        String[] regCountry = getResources()
                .getStringArray(R.array.reg_country);
        mRegCountry = regCountry[0];
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, regCountry);
        mSpCountry.setAdapter(adapter);
        mSpCountry
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent,
                            View view, int position, long id) {
                        // TODO Auto-generated method stub
                        mRegCountry = parent.getItemAtPosition(position)
                                .toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // TODO Auto-generated method stub

                    }
                });
        
        mBtnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (!Patterns.EMAIL_ADDRESS.matcher(
                        mEtEmail.getText().toString()).matches()) {
                    showErrorDailog(ExLauncher.this
                            .getString(R.string.error_email));
                    return;
                }

                if (!Patterns.PHONE.matcher(mEtPhone.getText().toString())
                        .matches()) {
                    showErrorDailog(ExLauncher.this
                            .getString(R.string.error_phone));
                    return;
                }

                if (TextUtils.isEmpty(mEtCity.getText().toString())) {
                    showErrorDailog(ExLauncher.this
                            .getString(R.string.error_city));
                    return;
                }

                registerToServer(mEtEmail.getText().toString(), mEtPhone
                        .getText().toString(), mRegCountry, mEtCity.getText()
                        .toString());
            }
        });
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

        // mIbTv.setOnFocusChangeListener(mImageButtonOnFocusChangeListener);
        // mIbMovies.setOnFocusChangeListener(mImageButtonOnFocusChangeListener);
        // mIbDrama.setOnFocusChangeListener(mImageButtonOnFocusChangeListener);
        // mIbYoutube.setOnFocusChangeListener(mImageButtonOnFocusChangeListener);
        // mIbGames.setOnFocusChangeListener(mImageButtonOnFocusChangeListener);
        // mIbRadio.setOnFocusChangeListener(mImageButtonOnFocusChangeListener);
        // mIbApps.setOnFocusChangeListener(mImageButtonOnFocusChangeListener);
        // mIbSetting.setOnFocusChangeListener(mImageButtonOnFocusChangeListener);

        // mIbTv.setOnFocusChangeListener(mBottomButtonOnFocusChangeListener);
        // mIbMovies.setOnFocusChangeListener(mBottomButtonOnFocusChangeListener);
        // mIbDrama.setOnFocusChangeListener(mBottomButtonOnFocusChangeListener);
        // mIbYoutube.setOnFocusChangeListener(mBottomButtonOnFocusChangeListener);
        // mIbGames.setOnFocusChangeListener(mBottomButtonOnFocusChangeListener);
        // mIbRadio.setOnFocusChangeListener(mBottomButtonOnFocusChangeListener);
        // mIbApps.setOnFocusChangeListener(mBottomButtonOnFocusChangeListener);
        // mIbSetting.setOnFocusChangeListener(mBottomButtonOnFocusChangeListener);

        // showViewLength(mIbTv);
        // showViewLength(mIbMovies);
        // showViewLength(mIbDrama);
        // showViewLength(mIbYoutube);
        // showViewLength(mIbYoutube);
        // showViewLength(mIbGames);
        // showViewLength(mIbRadio);
        // showViewLength(mIbApps);
        // showViewLength(mIbSetting);

        mBottomButtonIdList = new ArrayList<Integer>();
        mBottomButtonIdList.add(mIbTv.getId());
        mBottomButtonIdList.add(mIbMovies.getId());
        mBottomButtonIdList.add(mIbDrama.getId());
        mBottomButtonIdList.add(mIbYoutube.getId());
        mBottomButtonIdList.add(mIbGames.getId());
        mBottomButtonIdList.add(mIbRadio.getId());
        mBottomButtonIdList.add(mIbApps.getId());
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

        // mIbFirst.setOnKeyListener(mImageButtonOnKeyListener);
        // mIbSecond.setOnKeyListener(mImageButtonOnKeyListener);
        // mIbThird.setOnKeyListener(mImageButtonOnKeyListener);
        // mIbForth.setOnKeyListener(mImageButtonOnKeyListener);
        // mIbFifth.setOnKeyListener(mImageButtonOnKeyListener);
        // mIbSixth.setOnKeyListener(mImageButtonOnKeyListener);
        // mIbSeventh.setOnKeyListener(mImageButtonOnKeyListener);

        mIbFirst.setOnFocusChangeListener(mImageButtonOnFocusChangeListener);
        mIbSecond.setOnFocusChangeListener(mImageButtonOnFocusChangeListener);
        mIbThird.setOnFocusChangeListener(mImageButtonOnFocusChangeListener);
        mIbForth.setOnFocusChangeListener(mImageButtonOnFocusChangeListener);
        mIbFifth.setOnFocusChangeListener(mImageButtonOnFocusChangeListener);
        mIbSixth.setOnFocusChangeListener(mImageButtonOnFocusChangeListener);
        mIbSeventh.setOnFocusChangeListener(mImageButtonOnFocusChangeListener);

        // showViewLength(mIbFirst);
        // showViewLength(mIbSecond);
        // showViewLength(mIbThird);
        // showViewLength(mIbForth);
        // showViewLength(mIbFifth);
        // showViewLength(mIbSixth);
        // showViewLength(mIbSeventh);
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
        mBtnRead.setOnFocusChangeListener(mBottomButtonOnFocusChangeListener);

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

    private static void logi(String strs) {
        Log.i(TAG, strs);
    }

    private static void logd(String strs) {
        Log.d(TAG, strs);
    }

    private static void logw(String strs) {
        Log.w(TAG, strs);
    }

    private static void loge(String strs) {
        Log.e(TAG, strs);
    }
}
