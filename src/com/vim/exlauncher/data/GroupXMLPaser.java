package com.vim.exlauncher.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.Log;

import com.vim.exlauncher.ui.AllApps;

public class GroupXMLPaser {
    private static final String TAG = "GroupXMLPaser";
    
    private static final String STR_VIDEO = "video";
    private static final String STR_GAMES = "games";
    private static final String STR_APPS = "apps";
    private static final String STR_MUSIC = "music";
    private static final String STR_LOCAL = "local";

    private static final String PREINSTALL_GROUP_TAG = "preinstall_groups";
    private static final String APKS_TAG = "apks";

    private static final String GROUP_PRO = "group";
    private static final String PKG_PRO = "pkg";

    public static List<ApkDataInGroup> parse(Context context, int xmlId) {
        List<ApkDataInGroup> apkList = new ArrayList<ApkDataInGroup>();

        try {
            ApkDataInGroup apkData = null;
            XmlResourceParser xmlParse = context.getResources().getXml(xmlId);
            beginDocument(xmlParse, PREINSTALL_GROUP_TAG);
            nextElement(xmlParse);

            // continue to end document
            while (xmlParse.getEventType() != XmlPullParser.END_DOCUMENT) {
                apkData = new ApkDataInGroup();
                apkData.setGroup(getGroupInt(xmlParse.getAttributeValue(null, GROUP_PRO)));
                apkData.setPkg(xmlParse.getAttributeValue(null, PKG_PRO));
                logd("[parse] apkData : " + apkData.getGroup() + ", " + apkData.getPkg());

                apkList.add(apkData);
                nextElement(xmlParse);
            }
        } catch (Exception e) {
            loge("[parse] error! e : " + e);
        }
        return apkList;
    }

    private static int getGroupInt(String group) {
        int groupInt = AllApps.INDEX_APPS;
        if (STR_VIDEO.equalsIgnoreCase(group)) {
            groupInt = AllApps.INDEX_VIDEO;
        } else if (STR_GAMES.equalsIgnoreCase(group)) {
            groupInt = AllApps.INDEX_GAMES;
        } else if (STR_MUSIC.equalsIgnoreCase(group)) {
            groupInt = AllApps.INDEX_MUSIC;
        } else if (STR_LOCAL.equalsIgnoreCase(group)) {
            groupInt = AllApps.INDEX_LOCAL;
        }

        return groupInt;
    }

    private static int nextElement(XmlPullParser xmlParse)
            throws XmlPullParserException, IOException {
        int type;
        while (((type = xmlParse.next()) != XmlPullParser.START_TAG)
                && (type != XmlPullParser.END_DOCUMENT)) {
            ;
        }

        return type;
    }

    private static void beginDocument(XmlPullParser xmlParse,
            String firstElementName) throws XmlPullParserException, IOException {
        int type = nextElement(xmlParse);

        if (type != XmlPullParser.START_TAG) {
            throw new XmlPullParserException("start tag not found!");
        }

        if (!xmlParse.getName().equals(firstElementName)) {
            throw new XmlPullParserException("firstElementName "
                    + firstElementName + " not found!");
        }
    }

    public static final class ApkDataInGroup {
        private int mGroup;
        private String mPkg;

        public ApkDataInGroup() {

        }

        public ApkDataInGroup(int group, String pkg) {
            mGroup = group;
            mPkg = pkg;
        }

        public void setGroup(int group) {
            mGroup = group;
        }

        public int getGroup() {
            return mGroup;
        }

        public void setPkg(String pkg) {
            mPkg = pkg;
        }

        public String getPkg() {
            return mPkg;
        }
    }

    private static void logd(String strs) {
        Log.d(TAG, strs);
    }

    private static void loge(String strs) {
        Log.e(TAG, strs);
    }
}
