package work.kozh.xutil.floatview;

import android.app.AppOpsManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;

import java.lang.reflect.Method;

import androidx.appcompat.app.AppCompatActivity;
import work.kozh.xutil.LogUtils;
import work.kozh.xutil.ToastUtils;

/**
 * 华为手机的权限设置
 */
public class HuaweiUtil {


    /**
     * 检测 Huawei 悬浮窗权限
     */
    public static boolean checkFloatWindowPermission(Context context) {
        int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            return checkOp(context, 24);//OP_SYSTEM_ALERT_WINDOW = 24;
        } else return true;
    }

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
     * 去华为权限申请页面
     */
    public static void applyPermission(Context context) {
        try {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //   ComponentName comp = new ComponentName("com.huawei.systemmanager","com.huawei.permissionmanager.ui.MainActivity");//华为权限管理
            //   ComponentName comp = new ComponentName("com.huawei.systemmanager",
            //      "com.huawei.permissionmanager.ui.SingleAppActivity");//华为权限管理，跳转到指定app的权限管理位置需要华为接口权限，未解决
            //悬浮窗管理页面
            ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.addviewmonitor.AddViewMonitorActivity");

            intent.setComponent(comp);
            if (RomUtil.getEmuiVersion() == 3.1) {
                //emui 3.1 的适配
                ((AppCompatActivity) context).startActivityForResult(intent, Constant.REQUEST_CODE_FLOAT_WINDOW);
            } else {
                //emui 3.0 的适配
                //悬浮窗管理页面
                comp = new ComponentName("com.huawei.systemmanager", "com.huawei.notificationmanager.ui.NotificationManagmentActivity");
                intent.setComponent(comp);
                ((AppCompatActivity) context).startActivityForResult(intent, Constant.REQUEST_CODE_FLOAT_WINDOW);
            }
        } catch (SecurityException e) {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //   ComponentName comp = new ComponentName("com.huawei.systemmanager","com.huawei.permissionmanager.ui.MainActivity");//华为权限管理
            //华为权限管理，跳转到本app的权限管理页面,这个需要华为接口权限，未解决
            ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");
            //      ComponentName comp = new ComponentName("com.huawei.systemmanager","com.huawei.systemmanager.addviewmonitor
            //      .AddViewMonitorActivity");//悬浮窗管理页面
            intent.setComponent(comp);
            ((AppCompatActivity) context).startActivityForResult(intent, Constant.REQUEST_CODE_FLOAT_WINDOW);
            e.printStackTrace();
        } catch (ActivityNotFoundException e) {
            /**
             * 手机管家版本较低 HUAWEI SC-UL10
             */
            //   Toast.makeText(MainActivity.this, "act找不到", Toast.LENGTH_LONG).show();
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //权限管理页面 android4.4
            ComponentName comp = new ComponentName("com.Android.settings", "com.android.settings.permission.TabItem");
            //   ComponentName comp = new ComponentName("com.android.settings","com.android.settings.permission.single_app_activity");
            // 此处可跳转到指定app对应的权限管理页面，但是需要相关权限，未解决
            intent.setComponent(comp);
            ((AppCompatActivity) context).startActivityForResult(intent, Constant.REQUEST_CODE_FLOAT_WINDOW);
            e.printStackTrace();
        } catch (Exception e) {
            //抛出异常时提示信息
            ToastUtils.error(context, "进入设置页面失败，请手动设置");
            e.printStackTrace();
        }
    }

}
