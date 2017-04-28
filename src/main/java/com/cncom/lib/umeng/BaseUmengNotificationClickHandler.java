package com.cncom.lib.umeng;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

/**
 * Created by bestjoy on 2017/4/28.
 */

public class BaseUmengNotificationClickHandler extends UmengNotificationClickHandler {
    @Override
    public void launchApp(Context context, UMessage uMessage) {
        if(!TextUtils.isEmpty(uMessage.activity)){
            super.launchApp(context, uMessage);
        } else {
            Bundle ymBundle = new Bundle();
            startYMessageListActivity(context, ymBundle);
        }
    }

    /**
     * 打开默认的消息列表Activity
     */
    public void startYMessageListActivity(Context context, Bundle bundle) {
        Intent intent = new Intent(context.getPackageName() + ".VIEW_UMENG_messageList");

        if (context instanceof Activity) {

        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        context.startActivity(intent);
    }

    @Override
    public void openActivity(Context context, UMessage uMessage) {
        if(!TextUtils.isEmpty(uMessage.activity) && uMessage.activity.equals("messageList")) {
            Bundle ymBundle = new Bundle();
            startYMessageListActivity(context, ymBundle);
        }
        super.openActivity(context, uMessage);

    }
}
