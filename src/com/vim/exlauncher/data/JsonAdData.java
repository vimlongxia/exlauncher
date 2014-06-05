package com.vim.exlauncher.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

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
    private String mMsgNo;

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

    private String mSUrlAd1;
    private String mSUrlAd2;
    private String mSUrlAd3;
    private String mSUrlAd4;

    private String mSAdv1;
    private String mSAdv2;
    private String mSAdv3;
    private String mSAdv4;
    private String mSAdv5;

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
    public static final String MSG_NO = "Msg No";

    public static final String MSG_SHARED_PREF = "msg_shared_pref";
    private static final int MAX_RECENT_MSG = 5;
    private static final String BOTTOM_MSG_PRE_IN_SHARED_PREF = BOTTOM_MSG + "/";
 
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

    public static final String S_URL_AD1 = "SUrlAd1";
    public static final String S_URL_AD2 = "SUrlAd2";
    public static final String S_URL_AD3 = "SUrlAd3";
    public static final String S_URL_AD4 = "SUrlAd4";

    public static final String S_ADV1 = "SAdv1";
    public static final String S_ADV2 = "SAdv2";
    public static final String S_ADV3 = "SAdv3";
    public static final String S_ADV4 = "SAdv4";
    public static final String S_ADV5 = "SAdv5";

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

        editor.putString(CITY, mCity);
        editor.putString(COUNTRY, mCountry);
        editor.putString(DEALER_LOGO, mDealerLogo);
        editor.commit();

        saveMsg();
    }
    
    private void saveMsg(){
        SharedPreferences msgSp = mContext.getSharedPreferences(
                MSG_SHARED_PREF, Context.MODE_PRIVATE);
        
        Map<String, ?> allMsgs = msgSp.getAll();
        ArrayList<Integer> msgArrayList = new ArrayList<Integer>();
        
        // check if this is a new msg
        if (allMsgs.size() > 0) {
            for (Map.Entry<String, ?> entry : allMsgs.entrySet()) {
                String keyStr = entry.getKey().toString();
                msgArrayList.add(Integer.parseInt(keyStr));
            }
            
            if (msgArrayList.contains(Integer.parseInt(mMsgNo))) {
                Log.d(TAG, "[saveMsg] " + mMsgNo + " : " + mBottomMsg + " is not a new msg");
                return;
            }
        }
        
        
        if (allMsgs.size() < MAX_RECENT_MSG){
            // the message is not full
            msgSp.edit().putString(mMsgNo, mBottomMsg).commit();
        } else {
            // the message list is full, 
            Collections.sort(msgArrayList);
            String keyToDel = msgArrayList.get(0) + "";
            
            Log.d(TAG, "[saveMsg] no to Del : " + keyToDel + ", new msg no : " + mMsgNo);
            
            msgSp.edit().remove(keyToDel).commit();
            msgSp.edit().putString(mMsgNo, mBottomMsg).commit();
        }
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
            mMsgNo = personalJsonObject.getString(MSG_NO);

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

            mSUrlAd1 = photAdJsonObejct.getString(S_URL_AD1);
            mSUrlAd2 = photAdJsonObejct.getString(S_URL_AD2);
            mSUrlAd3 = photAdJsonObejct.getString(S_URL_AD3);
            mSUrlAd4 = photAdJsonObejct.getString(S_URL_AD4);

            mSAdv1 = photAdJsonObejct.getString(S_ADV1);
            mSAdv2 = photAdJsonObejct.getString(S_ADV2);
            mSAdv3 = photAdJsonObejct.getString(S_ADV3);
            mSAdv4 = photAdJsonObejct.getString(S_ADV4);
            mSAdv5 = photAdJsonObejct.getString(S_ADV5);

            mVadv1 = photAdJsonObejct.getString(VADV1);
            mVadv2 = photAdJsonObejct.getString(VADV2);
            mVadv3 = photAdJsonObejct.getString(VADV3);
            mVadv4 = photAdJsonObejct.getString(VADV4);

            mPvadv1 = photAdJsonObejct.getString(PVADV1);
            mPvadv2 = photAdJsonObejct.getString(PVADV2);
            mPvadv3 = photAdJsonObejct.getString(PVADV3);
            mPvadv4 = photAdJsonObejct.getString(PVADV4);
        } catch (Exception e) {
            Log.e(TAG,
                    "[parseAndSaveData] parse error! don't save any data here!");
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

        sb.append(S_URL_AD1 + " : " + mSUrlAd1 + "\n");
        sb.append(S_URL_AD2 + " : " + mSUrlAd2 + "\n");
        sb.append(S_URL_AD3 + " : " + mSUrlAd3 + "\n");
        sb.append(S_URL_AD4 + " : " + mSUrlAd4 + "\n");
        sb.append(S_ADV1 + " : " + mSAdv1 + "\n");
        sb.append(S_ADV2 + " : " + mSAdv2 + "\n");
        sb.append(S_ADV3 + " : " + mSAdv3 + "\n");
        sb.append(S_ADV4 + " : " + mSAdv4 + "\n");
        sb.append(S_ADV5 + " : " + mSAdv5 + "\n");

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
