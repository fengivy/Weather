package com.ivy.setting;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;

import com.ivy.weather.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static android.content.pm.ShortcutInfo.SHORTCUT_CATEGORY_CONVERSATION;

/**
 * Created by ivy on 2017/9/22.
 * Description：
 */

public class SettingActivity extends AppCompatActivity {
    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ShortcutManager shortCutManager = (ShortcutManager) getSystemService(Context.SHORTCUT_SERVICE);
        //shortCutManager.removeAllDynamicShortcuts();
        Intent intent=new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setClass(this,SettingActivity.class);
        Set<String> category=new HashSet<>();
        category.add(SHORTCUT_CATEGORY_CONVERSATION);
        List<ShortcutInfo> shortCutList=new ArrayList<>();
        for(int i=0;i<shortCutManager.getMaxShortcutCountPerActivity()-1;i++){
            ShortcutInfo shortCutInfo=new ShortcutInfo.Builder(this,i+"xxx")
                    .setShortLabel("我是短"+i)
                    .setLongLabel("我是长"+i)
                    .setDisabledMessage("我不能用"+i)
                    .setIcon(Icon.createWithResource(this,R.mipmap.ic_launcher))
                    .setIntent(intent)
                    .setCategories(category).build();
            shortCutList.add(shortCutInfo);
        }
        shortCutManager.setDynamicShortcuts(shortCutList);
    }

    public void test(){
        ShortcutManager shortCutManager = (ShortcutManager) getSystemService(Context.SHORTCUT_SERVICE);
    }
}
