package work.kozh.xutil.floatview;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Build;

import java.lang.reflect.Method;

import androidx.appcompat.app.AppCompatActivity;
import work.kozh.xutil.LogUtils;

/**
 * 360手机的权限设置
 */
public class QikuUtil {

    /**
     * 检测 360 悬浮窗权限
     */
    public static boolean checkFloatWindowPermission(Context context) {
        int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            return checkOp(context, 24); //OP_SYSTEM_ALERT_WINDOW = 24;
        } else return true;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static boolean checkOp(Context context, int op) {
        int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            try {
                Class clazz = AppOpsManager.class;
                Method method = clazz.getDeclaredMethod("checkOp", Integer.class, Integer.class, String.class);
                return AppOpsManager.MODE_ALLOWED == (int) method.invoke(manager, op, Binder.getCallingUid(), context.getPackageName());
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            LogUtils.i("Below API 19 cannot invoke!");
        }
        return false;
    }

    /**
     * 去360权限申请页面
     */
    public static void applyPermission(Context context) {
        Intent intent = new Intent();
        intent.setClassName("com.android.settings", "com.android.settings.Settings$OverlaySettingsActivity");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (isIntentAvailable(intent, context)) {
            ((AppCompatActivity) context).startActivityForResult(intent, Constant.REQUEST_CODE_FLOAT_WINDOW);
        } else {
            intent.setClassName("com.qihoo360.mobilesafe", "com.qihoo360.mobilesafe.ui.index.AppEnterActivity");
            if (isIntentAvailable(intent, context)) {
                ((AppCompatActivity) context).startActivityForResult(intent, Constant.REQUEST_CODE_FLOAT_WINDOW);
            } else {
                LogUtils.i("can't open permission page with particular name, please use " + "\"adb shell dumpsys activity\" command and tell me the" +
                        " name of the float window permission page");
            }
        }
    }

    private static boolean isIntentAvailable(Intent intent, Context context) {
        if (intent == null) {
            return false;
        } else return context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
    }


}

