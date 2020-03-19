package work.kozh.xutil.floatview;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;

import java.lang.reflect.Method;

import androidx.appcompat.app.AppCompatActivity;
import work.kozh.xutil.LogUtils;

/**
 * OPPO手机的权限设置
 */
public class OppoUtil {

    /**
     * 检测 360 悬浮窗权限
     */
    public static boolean checkFloatWindowPermission(Context context) {
        int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            return checkOp(context, 24);//OP_SYSTEM_ALERT_WINDOW = 24;
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
     * oppo ROM 权限申请
     */
    public static void applyOppoPermission(Context context) {
        //merge request from https://github.com/zhaozepeng/FloatWindowPermission/pull/26
        try {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //com.coloros.safecenter/.sysfloatwindow.FloatWindowListActivity
            //悬浮窗管理页面
            ComponentName comp = new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.sysfloatwindow.FloatWindowListActivity");

            intent.setComponent(comp);
            ((AppCompatActivity) context).startActivityForResult(intent, Constant.REQUEST_CODE_FLOAT_WINDOW);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
