package work.kozh.xutil.floatview;


import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;

import java.lang.reflect.Method;

import androidx.appcompat.app.AppCompatActivity;
import work.kozh.xutil.LogUtils;

/**
 * 魅族手机的权限设置
 */
public class MeizuUtil {

    /**
     * 检测 meizu 悬浮窗权限
     */
    public static boolean checkFloatWindowPermission(Context context) {
        int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            return checkOp(context, 24);//OP_SYSTEM_ALERT_WINDOW = 24;
        } else return true;
    }

    /**
     * 去魅族权限申请页面
     */
    public static void applyPermission(Context context) {
        try {
            Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
            //            intent.setClassName("com.meizu.safe", "com.meizu.safe.security.AppSecActivity");//remove this line code for fix flyme6.3
            intent.putExtra("packageName", context.getPackageName());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ((AppCompatActivity) context).startActivityForResult(intent, Constant.REQUEST_CODE_FLOAT_WINDOW);
        } catch (Exception e) {
            try {
                LogUtils.i("获取悬浮窗权限, 打开AppSecActivity失败 ");
                e.printStackTrace();
                // 最新的魅族flyme 6.2.5 用上述方法获取权限失败, 不过又可以用下述方法获取权限了
                FloatPermissionUtil.commonROMPermissionApplyInternal(context);
            } catch (Exception eFinal) {
                LogUtils.i("获取悬浮窗权限失败, 通用获取方法失败 ");
                eFinal.printStackTrace();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static boolean checkOp(Context context, int op) {
        int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            try {
                Class clazz = AppOpsManager.class ;
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


}


