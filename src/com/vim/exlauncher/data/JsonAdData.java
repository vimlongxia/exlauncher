package com.vim.exlauncher.data;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

public class JsonAdData {
    private static final String TAG = "JsonData";

    private Context mContext;
    private JSONObject mJsonObj;

    private String mDealerLogo;
    private String mName;
    private String mExpiryDate;
    private String mMac;
    private String mBottomMsg;

    // whether
    private String mMenuStatus;
    private String mFirmwareVersion;
    private String mFirmwarePath;

    // photo ad
    private String mUrlAd1;
    private String mUrlAd2;
    private String mUrlAd3;
    private String mUrlAd4;

    private String mAdv1;
    private String mAdv2;
    private String mAdv3;
    private String mAdv4;

    private String mVadv1;
    private String mVadv2;
    private String mVadv3;
    private String mVadv4;

    private String mPvadv1;
    private String mPvadv2;
    private String mPvadv3;
    private String mPvadv4;

    private String mCountry;
    private String mCity;

    public static final String USERS = "Users";
    public static final String PERSONAL = "Personal";
    public static final String DEALER_LOGO = "Dealer_logo";
    public static final String NAME = "Name";
    public static final String EXPIRY_DATE = "Expiry_Date";
    public static final String MAC = "mac";
    public static final String BOTTOM_MSG = "Bottom_Msg";

    // whether start
    public static final String WHETHER = "Whether";
    public static final String MENU_STATUS = "Menu_Status";
    public static final String FIRMWARE_VERSION = "Firmware_Version";
    public static final String FIRMWARE_PATH = "Firmware_Path";

    // photos advertisment
    public static final String PHOTO_ADVERTISMENT = "phot_Advertisment";
    public static final String URL_AD1 = "UrlAd1";
    public static final String URL_AD2 = "UrlAd2";
    public static final String URL_AD3 = "UrlAd3";
    public static final String URL_AD4 = "UrlAd4";

    public static final String ADV1 = "Adv1";
    public static final String ADV2 = "Adv2";
    public static final String ADV3 = "Adv3";
    public static final String ADV4 = "Adv4";

    public static final String VADV1 = "VAdv1";
    public static final String VADV2 = "VAdv2";
    public static final String VADV3 = "VAdv3";
    public static final String VADV4 = "VAdv4";

    public static final String PVADV1 = "PVAdv1";
    public static final String PVADV2 = "PVAdv2";
    public static final String PVADV3 = "PVAdv3";
    public static final String PVADV4 = "PVAdv4";

    public static final String COUNTRY = "Country";
    public static final String CITY = "City";

    public JsonAdData(Context context, JSONObject jsonObj) {
        mContext = context;
        mJsonObj = jsonObj;
    }

    private void saveAdDataToSharedPref() {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(mContext);
        Editor editor = sp.edit();

        editor.putString(BOTTOM_MSG, mBottomMsg);
        editor.putString(CITY, mCity);
        editor.putString(COUNTRY, mCountry);
        editor.putString(DEALER_LOGO, mDealerLogo);

        editor.commit();
    }

    public void parseAndSaveData() {
        if (mJsonObj == null) {
            Log.e(TAG, "[parseAndSaveData] mJsonObj is null!");
            return;
        }
        
        try {
            JSONArray userJsonArray = mJsonObj.getJSONArray(USERS);
            JSONObject userJsonObject = userJsonArray.getJSONObject(0);
            String personalJsonString = userJsonObject.getString(PERSONAL);
            JSONObject personalJsonObject = new JSONObject(personalJsonString);

            // parse personal data
            mDealerLogo = personalJsonObject.getString(DEALER_LOGO);
            mName = personalJsonObject.getString(NAME);
            mExpiryDate = personalJsonObject.getString(EXPIRY_DATE);
            mMac = personalJsonObject.getString(MAC);
            mBottomMsg = personalJsonObject.getString(BOTTOM_MSG);

            // parse whether data
            String whetherJsonString = personalJsonObject.getString(WHETHER);
            JSONObject whetherJsonObject = new JSONObject(whetherJsonString);
            mMenuStatus = whetherJsonObject.getString(MENU_STATUS);
            mFirmwareVersion = whetherJsonObject.getString(FIRMWARE_VERSION);
            mFirmwarePath = whetherJsonObject.getString(FIRMWARE_PATH);
            mCountry = whetherJsonObject.getString(COUNTRY);
            mCity = whetherJsonObject.getString(CITY);

            // parse ad data
            String photAdJsonString = whetherJsonObject
                    .getString(PHOTO_ADVERTISMENT);
            JSONObject photAdJsonObejct = new JSONObject(photAdJsonString);
            mUrlAd1 = photAdJsonObejct.getString(URL_AD1);
            mUrlAd2 = photAdJsonObejct.getString(URL_AD2);
            mUrlAd3 = photAdJsonObejct.getString(URL_AD3);
            mUrlAd4 = photAdJsonObejct.getString(URL_AD4);

            mAdv1 = photAdJsonObejct.getString(ADV1);
            mAdv2 = photAdJsonObejct.getString(ADV2);
            mAdv3 = photAdJsonObejct.getString(ADV3);
            mAdv4 = photAdJsonObejct.getString(ADV4);

            mVadv1 = photAdJsonObejct.getString(VADV1);
            mVadv2 = photAdJsonObejct.getString(VADV2);
            mVadv3 = photAdJsonObejct.getString(VADV3);
            mVadv4 = photAdJsonObejct.getString(VADV4);

            mPvadv1 = photAdJsonObejct.getString(PVADV1);
            mPvadv2 = photAdJsonObejct.getString(PVADV2);
            mPvadv3 = photAdJsonObejct.getString(PVADV3);
            mPvadv4 = photAdJsonObejct.getString(PVADV4);
        } catch (Exception e) {
            Log.e(TAG, "[parseAndSaveData] parse error! don't save any data here!");
            e.printStackTrace();
            return;
        }

        saveAdDataToSharedPref();
    }

    // public String getBottomMsg() {
    // return mBottomMsg;
    // }
    //
    // public String getCountry() {
    // return mCountry;
    // }
    //
    // public String getCity() {
    // return mCity;
    // }
    //
    // public String getDealerLogo() {
    // return mDealerLogo;
    // }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(DEALER_LOGO + " : " + mDealerLogo + "\n");
        sb.append(NAME + " : " + mName + "\n");
        sb.append(EXPIRY_DATE + " : " + mExpiryDate + "\n");
        sb.append(MAC + " : " + mMac + "\n");
        sb.append(BOTTOM_MSG + " : " + mBottomMsg + "\n");

        sb.append(MENU_STATUS + " : " + mMenuStatus + "\n");
        sb.append(FIRMWARE_VERSION + " : " + mFirmwareVersion + "\n");
        sb.append(FIRMWARE_PATH + " : " + mFirmwarePath + "\n");

        sb.append(URL_AD1 + " : " + mUrlAd1 + "\n");
        sb.append(URL_AD2 + " : " + mUrlAd2 + "\n");
        sb.append(URL_AD3 + " : " + mUrlAd3 + "\n");
        sb.append(URL_AD4 + " : " + mUrlAd4 + "\n");
        sb.append(ADV1 + " : " + mAdv1 + "\n");
        sb.append(ADV2 + " : " + mAdv2 + "\n");
        sb.append(ADV3 + " : " + mAdv3 + "\n");
        sb.append(ADV4 + " : " + mAdv4 + "\n");
        sb.append(VADV1 + " : " + mVadv1 + "\n");
        sb.append(VADV2 + " : " + mVadv2 + "\n");
        sb.append(VADV3 + " : " + mVadv3 + "\n");
        sb.append(VADV4 + " : " + mVadv4 + "\n");
        sb.append(PVADV1 + " : " + mPvadv1 + "\n");
        sb.append(PVADV2 + " : " + mPvadv2 + "\n");
        sb.append(PVADV3 + " : " + mPvadv3 + "\n");
        sb.append(PVADV4 + " : " + mPvadv4 + "\n");

        sb.append(COUNTRY + " : " + mCountry + "\n");
        sb.append(CITY + " : " + mCity + "\n");

        return sb.toString();
    }
}
