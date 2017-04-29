package com.cncom.lib.umeng;

import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.app.NotificationCompat;

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

    @Override
    public Notification getNotification(Context context, UMessage msg) {
        Notification notification= null;
//		    switch (msg.builder_id) {
//		    case 1:
////		        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
////		        RemoteViews myNotificationView = new RemoteViews(context.getPackageName(), R.layout.notification_view);
////		        myNotificationView.setTextViewText(R.id.notification_title, msg.title);
////		        myNotificationView.setTextViewText(R.id.notification_text, msg.text);
////		        myNotificationView.setImageViewBitmap(R.id.notification_large_icon, getLargeIcon(context, msg));
////		        myNotificationView.setImageViewResource(R.id.notification_small_icon, getSmallIconId(context, msg));
////		        builder.setContent(myNotificationView);
////		        Notification mNotification = builder.build();
////		        //由于Android v4包的bug，在2.3及以下系统，Builder创建出来的Notification，并没有设置RemoteView，故需要添加此代码
////		        mNotification.contentView = myNotificationView;
////		        return mNotification;
//		    default:
//		        //默认为0，若填写的builder_id并不存在，也使用默认。
//		    	notification = super.getNotification(context, msg);
//		    }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        int smallIconId = this.getSmallIconId(context, msg);
        Bitmap largeIconBitmap = this.getLargeIcon(context, msg);
        if(smallIconId > 0) {
            builder.setSmallIcon(smallIconId);
            builder.setLargeIcon(largeIconBitmap);
        }

        builder.setAutoCancel(true);
        builder.setTicker(msg.ticker);
        builder.setContentText(msg.text);
        builder.setContentTitle(msg.title);
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(msg.text));
        notification = builder.build();
        return notification;
    }
}
