package com.vim.exlauncher.data;

import java.util.HashMap;
import java.util.Map;

import com.vim.exlauncher.R;

public class Weather {
    private static Weather mWeather;
    
    private Map<String, Integer> mIconMap;
    
    protected Weather() {
        
    }
    
    public static Weather getInstance() {
        if (mWeather == null ){
            mWeather = new Weather();
        }
        
        return mWeather;
    }
    
    public void initIconMap(){
        mIconMap = new HashMap<String, Integer>();
        
        mIconMap.put("01d", R.drawable.icon_01d);
        mIconMap.put("01n", R.drawable.icon_01n);
        mIconMap.put("02d", R.drawable.icon_02d);
        mIconMap.put("02n", R.drawable.icon_02n);
        mIconMap.put("03d", R.drawable.icon_03d);
        mIconMap.put("03n", R.drawable.icon_03n);
        mIconMap.put("04d", R.drawable.icon_04d);
        mIconMap.put("04n", R.drawable.icon_04n);
        mIconMap.put("09d", R.drawable.icon_09d);
        mIconMap.put("09n", R.drawable.icon_09n);
        mIconMap.put("10d", R.drawable.icon_10d);
        mIconMap.put("10n", R.drawable.icon_10n);
        mIconMap.put("11d", R.drawable.icon_11d);
        mIconMap.put("11n", R.drawable.icon_11n);
        mIconMap.put("13d", R.drawable.icon_13d);
        mIconMap.put("13n", R.drawable.icon_13n);
        mIconMap.put("50d", R.drawable.icon_50d);
        mIconMap.put("50n", R.drawable.icon_50n);
    }
    
    public int getResIdFromIconName(String iconName){
        return mIconMap.get(iconName);
    }
}
