package com.cncom.lib.umeng;

import android.content.Context;

import com.umeng.message.UmengMessageHandler;
import com.umeng.message.entity.UMessage;

/**
 * Created by bestjoy on 2017/4/28.
 */

public class BaseUmengMessageHandler extends UmengMessageHandler {
    @Override
    public void dealWithCustomMessage(Context arg0, UMessage mesasge) {
        super.dealWithCustomMessage(arg0, mesasge);
        saveYmengMessageAsync(mesasge);
    }

    @Override
    public void dealWithNotificationMessage(Context arg0, UMessage mesasge) {
//			if (mesasge.extra != null) {
//				String type = mesasge.extra.get("type");
//				if (!TextUtils.isEmpty(type) && "1000".equals(type)) {
//					verifyDeviceTokenAsync(mesasge);
//					return;
//				}
//			}
        super.dealWithNotificationMessage(arg0, mesasge);
        saveYmengMessageAsync(mesasge);
    }


    public void saveYmengMessageAsync(UMessage mesasge) {

    }
}
