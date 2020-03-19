package work.kozh.xutil;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.Toast;

import com.kongzue.dialog.util.DialogSettings;
import com.kongzue.dialog.util.TextInfo;
import com.kongzue.dialog.v3.Notification;

import es.dmoral.toasty.Toasty;

/**
 * 需要额外引入jar包
 *
 * implementation com.github.GrenderG:Toasty:1.4.2
 *
 * implementation com.kongzue.dialog_v3:dialog:3.1.8
 *
 */
public class ToastUtils {

    //新建的toast类
    //分为两种类型，可通过下面的变量来选择
    public static final int TOAST_STYLE = 0;


    //信息通知类型  短时间
    public static void info(Context context, String msg) {
        switch (TOAST_STYLE) {
            case 0:
                Toast toast = Toasty.info(context, msg, Toast.LENGTH_SHORT, true);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                break;
            case 1:
                //通知类型设置为 STYLE_MATERIAL
                DialogSettings.style = DialogSettings.STYLE.STYLE_KONGZUE;
                //设置正文文本样式
                TextInfo msgTextInfo = new TextInfo();
                msgTextInfo.setFontColor(Color.rgb(18, 150, 219));
                DialogSettings.contentTextInfo = msgTextInfo;
                //设置标题文本样式
                TextInfo titleTextInfo = new TextInfo();
                titleTextInfo.setBold(true);
                DialogSettings.titleTextInfo = titleTextInfo;
                Notification.show(context, "ONES", msg, R.mipmap.icon_notification_info);
                break;
            default:
                break;
        }

    }

    //信息通知类型  长时间
    public static void info(Context context, String msg, boolean isTimeLong) {
        switch (TOAST_STYLE) {
            case 0:
                Toast toast = Toasty.info(context, msg, Toast.LENGTH_LONG, true);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                break;
            case 1:
                //通知类型设置为 STYLE_MATERIAL
                DialogSettings.style = DialogSettings.STYLE.STYLE_KONGZUE;
                //设置正文文本样式
                TextInfo msgTextInfo = new TextInfo();
                msgTextInfo.setFontColor(Color.rgb(18, 150, 219));
                DialogSettings.contentTextInfo = msgTextInfo;
                //设置标题文本样式
                TextInfo titleTextInfo = new TextInfo();
                titleTextInfo.setBold(true);
                DialogSettings.titleTextInfo = titleTextInfo;
                Notification.show(context, "ONES", msg, R.mipmap.icon_notification_info);
                break;
            default:
                break;

        }


    }

    //成功通知类型  短时间
    public static void success(Context context, String msg) {
        switch (TOAST_STYLE) {
            case 0:
                Toast toast = Toasty.success(context, msg, Toast.LENGTH_SHORT, true);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                break;
            case 1:
                //通知类型设置为 STYLE_MATERIAL
                DialogSettings.style = DialogSettings.STYLE.STYLE_KONGZUE;
                //设置正文文本样式
                TextInfo msgTextInfo = new TextInfo();
//                msgTextInfo.setFontColor(Color.rgb(96, 108, 87));
                msgTextInfo.setFontColor(Color.rgb(0, 255, 0));
                DialogSettings.contentTextInfo = msgTextInfo;
                //设置标题文本样式
                TextInfo titleTextInfo = new TextInfo();
                titleTextInfo.setBold(true);
                DialogSettings.titleTextInfo = titleTextInfo;
                Notification.show(context, "ONES", msg, R.mipmap.icon_notification_success);
                break;
            default:
                break;

        }

    }

    //成功通知类型  长时间
    public static void success(Context context, String msg, boolean isTimeLong) {
        switch (TOAST_STYLE) {
            case 0:
                Toast toast = Toasty.success(context, msg, Toast.LENGTH_LONG, true);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                break;
            case 1:
                //通知类型设置为 STYLE_MATERIAL
                DialogSettings.style = DialogSettings.STYLE.STYLE_KONGZUE;
                //设置正文文本样式
                TextInfo msgTextInfo = new TextInfo();
//                msgTextInfo.setFontColor(Color.rgb(96, 108, 87));
                msgTextInfo.setFontColor(Color.rgb(0, 255, 0));
                DialogSettings.contentTextInfo = msgTextInfo;
                //设置标题文本样式
                TextInfo titleTextInfo = new TextInfo();
                titleTextInfo.setBold(true);
                DialogSettings.titleTextInfo = titleTextInfo;
                Notification.show(context, "ONES", msg, R.mipmap.icon_notification_success);
                break;
            default:
                break;

        }

    }

    //错误通知类型  短时间
    public static void error(Context context, String msg) {
        switch (TOAST_STYLE) {
            case 0:
                Toast toast = Toasty.error(context, msg, Toast.LENGTH_SHORT, true);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                break;
            case 1:
                //通知类型设置为 STYLE_MATERIAL
                DialogSettings.style = DialogSettings.STYLE.STYLE_KONGZUE;
                //设置正文文本样式
                TextInfo msgTextInfo = new TextInfo();
                msgTextInfo.setFontColor(Color.rgb(255, 0, 0));
                DialogSettings.contentTextInfo = msgTextInfo;
                //设置标题文本样式
                TextInfo titleTextInfo = new TextInfo();
                titleTextInfo.setBold(true);
                DialogSettings.titleTextInfo = titleTextInfo;
                Notification.show(context, "ONES", msg, R.mipmap.icon_notification_error);
                break;
            default:
                break;

        }

    }

    //错误通知类型  长时间
    public static void error(Context context, String msg, boolean isTimeLong) {
        switch (TOAST_STYLE) {
            case 0:
                Toast toast = Toasty.error(context, msg, Toast.LENGTH_LONG, true);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                break;
            case 1:
                //通知类型设置为 STYLE_MATERIAL
                DialogSettings.style = DialogSettings.STYLE.STYLE_KONGZUE;
                //设置正文文本样式
                TextInfo msgTextInfo = new TextInfo();
                msgTextInfo.setFontColor(Color.rgb(255, 0, 0));
                DialogSettings.contentTextInfo = msgTextInfo;
                //设置标题文本样式
                TextInfo titleTextInfo = new TextInfo();
                titleTextInfo.setBold(true);
                DialogSettings.titleTextInfo = titleTextInfo;
                Notification.show(context, "ONES", msg, R.mipmap.icon_notification_error);
                break;
            default:
                break;

        }

    }

    //警告通知类型  短时间
    public static void warning(Context context, String msg) {
        switch (TOAST_STYLE) {
            case 0:
                Toast toast = Toasty.warning(context, msg, Toast.LENGTH_SHORT, true);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                break;
            case 1:
                //通知类型设置为 STYLE_MATERIAL
                DialogSettings.style = DialogSettings.STYLE.STYLE_KONGZUE;
                //设置正文文本样式
                TextInfo msgTextInfo = new TextInfo();
                msgTextInfo.setFontColor(Color.rgb(244, 234, 42));
                DialogSettings.contentTextInfo = msgTextInfo;
                //设置标题文本样式
                TextInfo titleTextInfo = new TextInfo();
                titleTextInfo.setBold(true);
                DialogSettings.titleTextInfo = titleTextInfo;
                Notification.show(context, "ONES", msg, R.mipmap.icon_notification_warn);
                break;
            default:
                break;

        }

    }

    //警告通知类型  长时间
    public static void warning(Context context, String msg, boolean isTimeLong) {
        switch (TOAST_STYLE) {
            case 0:
                Toast toast = Toasty.warning(context, msg, Toast.LENGTH_LONG, true);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                break;
            case 1:
                //通知类型设置为 STYLE_MATERIAL
                DialogSettings.style = DialogSettings.STYLE.STYLE_KONGZUE;
                //设置正文文本样式
                TextInfo msgTextInfo = new TextInfo();
                msgTextInfo.setFontColor(Color.rgb(244, 234, 42));
                DialogSettings.contentTextInfo = msgTextInfo;
                //设置标题文本样式
                TextInfo titleTextInfo = new TextInfo();
                titleTextInfo.setBold(true);
                DialogSettings.titleTextInfo = titleTextInfo;
                Notification.show(context, "ONES", msg, R.mipmap.icon_notification_warn);
                break;
            default:
                break;

        }

    }


    //普通通知类型  短时间
    public static void normal(Context context, String msg) {
        switch (TOAST_STYLE) {
            case 0:
                Toast toast = Toasty.normal(context, msg, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                break;
            case 1:
                //通知类型设置为 STYLE_MATERIAL
                DialogSettings.style = DialogSettings.STYLE.STYLE_KONGZUE;
                //设置正文文本样式
                TextInfo msgTextInfo = new TextInfo();
                msgTextInfo.setFontColor(Color.rgb(0, 0, 255));
                DialogSettings.contentTextInfo = msgTextInfo;
                //设置标题文本样式
                TextInfo titleTextInfo = new TextInfo();
                titleTextInfo.setBold(true);
                DialogSettings.titleTextInfo = titleTextInfo;
                Notification.show(context, "ONES", msg, R.mipmap.icon_notification_info);
                break;
            default:
                break;

        }

    }

    //普通通知类型  长时间
    public static void normal(Context context, String msg, boolean isTimeLong) {
        switch (TOAST_STYLE) {
            case 0:
                Toast toast = Toasty.normal(context, msg, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                break;
            case 1:
                //通知类型设置为 STYLE_MATERIAL
                DialogSettings.style = DialogSettings.STYLE.STYLE_KONGZUE;
                //设置正文文本样式
                TextInfo msgTextInfo = new TextInfo();
                msgTextInfo.setFontColor(Color.rgb(0, 0, 255));
                DialogSettings.contentTextInfo = msgTextInfo;
                //设置标题文本样式
                TextInfo titleTextInfo = new TextInfo();
                titleTextInfo.setBold(true);
                DialogSettings.titleTextInfo = titleTextInfo;
                Notification.show(context, "ONES", msg, R.mipmap.icon_notification_info);
                break;
            default:
                break;

        }

    }


}
