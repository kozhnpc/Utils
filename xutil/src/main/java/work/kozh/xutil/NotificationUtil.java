package work.kozh.xutil;

import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


/**
 * 通知的构建
 * 适配到安卓8.0
 */
public class NotificationUtil extends ContextWrapper {

    private NotificationManager mManager;
    public static final String sID = "ones_download_notification";
    public static final String sName = "ONES下载通知";
    public Context mContext;

    public NotificationUtil(Context context) {
        super(context);
        this.mContext = context;
    }


    // ------------------  对外方法  -------------------------------

    /**
     * 创建一个通知工具类
     * 为了兼容过去的方法
     * 这里如果需要调用发送通知的方法 那么就需要先调用这个方法
     *
     * @param context
     * @return
     */
    public static NotificationUtil getInstance(Context context) {
        return new NotificationUtil(context);
    }

    /**
     * 发送通知  适配到了安卓8.0
     *
     * @param title
     * @param content
     */
    public void sendNotification(String title, String content) {

//        Notification notification = getNotification_25(title, content).build();
//        getmManager().notify(1, notification);

        //取消目标编译版本8.0   26
        if (Build.VERSION.SDK_INT >= 26) {
            LogUtils.i("发送了一条通知>26");
            createNotificationChannel();
            Notification notification_26 = getNotification_26(title, content).build();
            getmManager().notify(1, notification_26);
        } else {
            LogUtils.i("发送了一条通知<26");
            Notification notification_25 = getNotification_25(title, content).build();
            getmManager().notify(1, notification_25);
        }
    }


    /**
     * 检测通知栏的开启情况 并弹出框
     * <p>
     * 暂不实现  为避免工具类的过度耦合  这里的逻辑交由外部去实现
     *
     * @param context
     */
    public static void checkNotificationEnable(Context context) {

    }

    /**
     * 判断该app是否打开了通知
     * <p>
     * 可以通过NotificationManagerCompat 中的 areNotificationsEnabled()来判断是否开启通知权限。NotificationManagerCompat 在 android.support.v4.app包中，是API 22.1.0
     * 中加入的。而 areNotificationsEnabled()则是在 API 24.1.0之后加入的。
     * areNotificationsEnabled 只对 API 19  Android 4.4 及以上版本有效，低于API 19 会一直返回true
     *
     * @param context
     * @return
     */
    public static boolean isNotificationEnabled(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            boolean areNotificationsEnabled = notificationManagerCompat.areNotificationsEnabled();
            return areNotificationsEnabled;
        }

        String CHECK_OP_NO_THROW = "checkOpNoThrow";
        String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

        AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;

        Class appOpsClass = null;
        /* Context.APP_OPS_MANAGER */
        try {
            appOpsClass = Class.forName(AppOpsManager.class.getName());
            Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE,
                    String.class);
            Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);

            int value = (Integer) opPostNotificationValue.get(Integer.class);
            return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }


    /**
     * 如果发现通知被关闭  提示进入设置界面打开通知
     * 假设没有开启通知权限，点击之后就需要跳转到 APP的通知设置界面，
     * 对应的Action是：Settings.ACTION_APP_NOTIFICATION_SETTINGS, 这个Action是 API 26 后增加的
     * <p>
     * 如果在部分手机中无法精确的跳转到 APP对应的通知设置界面，
     * 那么我们就考虑直接跳转到 APP信息界面，对应的Action是：Settings.ACTION_APPLICATION_DETAILS_SETTINGS
     *
     * @param context
     */
    public static void gotoSettingActivity(Context context) {

        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= 26) {
            // android 8.0引导
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("android.provider.extra.APP_PACKAGE", context.getPackageName());

        } else if (Build.VERSION.SDK_INT >= 21) {
            // android 5.0-7.0
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", context.getPackageName());
            intent.putExtra("app_uid", context.getApplicationInfo().uid);

        } else {
            // 其他   跳转到APP信息界面
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    //-----------------   内部调用方法  -------------------------

    /**
     * 获取NotificationManager 对象  单例
     *
     * @return
     */
    private NotificationManager getmManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        return mManager;
    }

    /**
     * 针对安卓8.0的适配  创建 NotificationChannel  在通知设置页面中会显示
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(sID, sName, NotificationManager.IMPORTANCE_HIGH);
        getmManager().createNotificationChannel(channel);
    }

    /**
     * 安卓8.0 以前的展示通知 的方法
     * 这里写了一个默认的设置  就是点击事件
     *
     * @param title
     * @param content
     * @return
     */
    public NotificationCompat.Builder getNotification_25(String title, String content) {

        // 以下是展示大图的通知
        NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle();
        style.setBigContentTitle("BigContentTitle");
        style.setSummaryText("SummaryText");

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        style.bigPicture(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_notification_success, options));

        // 以下是展示多文本通知
        NotificationCompat.BigTextStyle style1 = new NotificationCompat.BigTextStyle();
        style1.setBigContentTitle(title);
        style1.bigText(content);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.mipmap.icon_notification_info)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_notification_error))
                .setDefaults(Notification.DEFAULT_SOUND)
//                .setStyle(style)
                .setAutoCancel(true);

        //在这里写死了 跳转逻辑 后期可以修改  在builder中添加修改
        Intent intent = new Intent(mContext, null);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent
                .FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //需要携带什么参数就在的intent包裹即可，NotificationClickReceiver可以接收到发送过来的intent
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        return builder;
    }

    /**
     * 安卓8.0 以前的展示通知 的方法
     * 这里写了一个默认的设置  就是点击事件
     *
     * @param title
     * @param content
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getNotification_26(String title, String content) {
        Notification.Builder builder = new Notification.Builder(mContext, sID)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.mipmap.icon_notification_info)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_notification_success))
//                .setStyle(new Notification.BigPictureStyle().bigPicture(BitmapFactory.decodeResource(getResources(), R.mipmap.sunday)))
                .setNumber(1)
                .setAutoCancel(true);
        //需要携带什么参数就在的intent包裹即可，NotificationClickReceiver可以接收到发送过来的intent
        Intent intent = new Intent(mContext, null);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent
                .FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        //在这里写死了 跳转逻辑 后期可以修改  在builder中添加修改
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        return builder;
    }


}
