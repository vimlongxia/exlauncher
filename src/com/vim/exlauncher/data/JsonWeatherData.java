package com.vim.exlauncher.data;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

public class JsonWeatherData {
    private static final String TAG = "JsonWeatherData";
    private JSONObject mJsonObj;
    private Context mContext;

    // coord
    public static final String COORD = "coord";
    public static final String LON = "lon";
    public static final String LAT = "lat";
    private double mLon;
    private double mLat;

    // sys
    public static final String SYS = "sys";
    public static final String MESSAGE = "message";
    public static final String COUNTRY = "country";
    public static final String SUNRISE = "sunrise";
    public static final String SUNSET = "sunset";

    private double mMessage;
    private String mCountry;
    private long mSunrise;
    private long mSunset;

    // weather
    public static final String WEATHER = "weather";
    public static final String WEATHER_ID = "id";
    public static final String WEATHER_MAIN = "main";
    public static final String DESCRIPTION = "description";
    public static final String ICON = "icon";

    private int mWeatherId;
    private String mWeatherMain;
    private String mDescription;
    private String mIcon;

    // base
    public static final String BASE = "base";
    private String mBase;

    // main
    public static final String MAIN = "main";
    public static final String TEMP = "temp";
    public static final String TEMP_MIN = "temp_min";
    public static final String TEMP_MAX = "temp_max";
    public static final String PRESSURE = "pressure";
    public static final String SEALEVEL = "sea_level";
    public static final String GRND_LEVEL = "grnd_level";
    public static final String HUMIDITY = "humidity";

    private double mTemp;
    private double mTempMin;
    private double mTempMax;
    private double mPressure;
    private double mSeaLevel;
    private double mGrndLevel;
    private int mHumidity;

    // wind
    public static final String WIND = "wind";
    public static final String SPEED = "speed";
    public static final String DEG = "deg";

    private double mSpeed;
    private double mDeg;

    // clouds
    public static final String CLOUDS = "clouds";
    public static final String ALL = "all";
    private int mAll;

    public static final String DT = "dt";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String COD = "cod";

    private long mDt;
    private int mId;
    private String mName;
    private int mCod;

    public JsonWeatherData(Context context, JSONObject jsonObj) {
        mContext = context;
        mJsonObj = jsonObj;
    }

    private void saveToSharedPref() {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(mContext);
        Editor editor = sp.edit();

        editor.putString(WEATHER_MAIN, mWeatherMain);
        editor.putFloat(TEMP, (float) mTemp);
        editor.putFloat(TEMP_MAX, (float) mTempMax);
        editor.putFloat(TEMP_MIN, (float) mTempMin);
        editor.putFloat(SPEED, (float) mSpeed);

        editor.commit();
    }

    public void parseAndSaveToSharedPref() {
        if (mJsonObj == null) {
            Log.e(TAG, "[parse] JSON object must not be empty!");
            return;
        }

        try {
            mBase = mJsonObj.getString(BASE);
            mDt = mJsonObj.getLong(DT);
            mId = mJsonObj.getInt(ID);
            mName = mJsonObj.getString(NAME);
            mCod = mJsonObj.getInt(COD);

            // parse coord data
            String coordJsonString = mJsonObj.getString(COORD);
            JSONObject coordJsonObject = new JSONObject(coordJsonString);
            mLon = coordJsonObject.getDouble(LON);
            mLat = coordJsonObject.getDouble(LAT);

            // parse sys data
            String sysJsonString = mJsonObj.getString(SYS);
            JSONObject sysJsonObject = new JSONObject(sysJsonString);
            mMessage = sysJsonObject.getDouble(MESSAGE);
            mCountry = sysJsonObject.getString(COUNTRY);
            mSunrise = sysJsonObject.getLong(SUNRISE);
            mSunset = sysJsonObject.getLong(SUNSET);

            // parse weather data
            JSONArray weatherJsonArray = mJsonObj.getJSONArray(WEATHER);
            JSONObject weatherJsonObject = weatherJsonArray.getJSONObject(0);
            mWeatherId = weatherJsonObject.getInt(WEATHER_ID);
            mWeatherMain = weatherJsonObject.getString(WEATHER_MAIN);
            mDescription = weatherJsonObject.getString(DESCRIPTION);
            mIcon = weatherJsonObject.getString(ICON);

            // parse main data
            String mainJsonString = mJsonObj.getString(MAIN);
            JSONObject mainJsonObject = new JSONObject(mainJsonString);
            mTemp = mainJsonObject.getDouble(TEMP);
            mTempMin = mainJsonObject.getDouble(TEMP_MIN);
            mTempMax = mainJsonObject.getDouble(TEMP_MAX);
            mPressure = mainJsonObject.getDouble(PRESSURE);
            // mSeaLevel = mainJsonObject.getDouble(SEALEVEL);
            // mGrndLevel = mainJsonObject.getDouble(GRND_LEVEL);
            mHumidity = mainJsonObject.getInt(HUMIDITY);

            // parse wind data
            String windJsonString = mJsonObj.getString(WIND);
            JSONObject windJsonObject = new JSONObject(windJsonString);
            mSpeed = windJsonObject.getDouble(SPEED);
            mDeg = windJsonObject.getDouble(DEG);

            // parse clouds data
            String cloudsJsonString = mJsonObj.getString(CLOUDS);
            JSONObject cloudsJsonObject = new JSONObject(cloudsJsonString);
            mAll = cloudsJsonObject.getInt(ALL);
        } catch (Exception e) {
            Log.e(TAG, "parse error! don't save any thing here.");
            e.printStackTrace();
            return;
        }

        saveToSharedPref();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(LON + " : " + mLon + "\n");
        sb.append(LAT + " : " + mLat + "\n");

        sb.append(MESSAGE + " : " + mMessage + "\n");
        sb.append(COUNTRY + " : " + mCountry + "\n");
        sb.append(SUNRISE + " : " + mSunrise + "\n");
        sb.append(SUNSET + " : " + mSunset + "\n");

        sb.append(WEATHER_ID + " : " + mWeatherId + "\n");
        sb.append(WEATHER_MAIN + " : " + mWeatherMain + "\n");
        sb.append(DESCRIPTION + " : " + mDescription + "\n");
        sb.append(ICON + " : " + mIcon + "\n");

        sb.append(BASE + " : " + mBase + "\n");

        sb.append(TEMP + " : " + mTemp + "\n");
        sb.append(TEMP_MAX + " : " + mTempMax + "\n");
        sb.append(TEMP_MIN + " : " + mTempMin + "\n");
        sb.append(PRESSURE + " : " + mPressure + "\n");
        sb.append(SEALEVEL + " : " + mSeaLevel + "\n");
        sb.append(GRND_LEVEL + " : " + mGrndLevel + "\n");
        sb.append(HUMIDITY + " : " + mHumidity + "\n");

        sb.append(SPEED + " : " + mSpeed + "\n");
        sb.append(DEG + " : " + mDeg + "\n");

        sb.append(ALL + " : " + mAll + "\n");

        sb.append(DT + " : " + mDt + "\n");
        sb.append(ID + " : " + mId + "\n");
        sb.append(NAME + " : " + mName + "\n");
        sb.append(COD + " : " + mCod + "\n");

        return sb.toString();
    }
}
