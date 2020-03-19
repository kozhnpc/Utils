package work.kozh.xutil.floatview;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;

import java.lang.reflect.Method;

import androidx.appcompat.app.AppCompatActivity;
import work.kozh.xutil.LogUtils;


/**
 * 小米手机的权限设置
 */
public class MiuiUtil {

    /**
     * 获取小米 rom 版本号，获取失败返回 -1
     *
     * @return miui rom version code, if fail , return -1
     */
    public static int getMuiVersion() {
        String version = RomUtil.getSystemProperty("ro.miui.ui.version.name");
        if (version != null) {
            try {
                return Integer.parseInt(version.substring(1));
            } catch (Exception e) {
                LogUtils.i("get miui version code error, version : " + version);
            }

        }
        return -1;
    }

    /**
     * 检测 miui 悬浮窗权限
     */
    public static boolean checkFloatWindowPermission(Context context) {
        int version = Build.VERSION.SDK_INT;

        if (version >= 19) {
            return checkOp(context, 24); //OP_SYSTEM_ALERT_WINDOW = 24;
        } else {
            //            if ((context.getApplicationInfo().flags & 1 << 27) == 1) {
            //                return true;
            //            } else {
            //                return false;
            //            }
            return true;
        }
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
     * 小米 ROM 权限申请
     */
    public static void applyMiuiPermission(Context context) {
        int versionCode = getMuiVersion();
        switch (versionCode) {
            case 5:
                goToMiuiPermissionActivity_V5(context);
                break;
            case 6:
                goToMiuiPermissionActivity_V6(context);
                break;
            case 7:
                goToMiuiPermissionActivity_V7(context);
                break;
            default:
                goToMiuiPermissionActivity_V8(context);
                break;
        }
    }

    private static boolean isIntentAvailable(Intent intent, Context context) {
        if (intent == null) {
            return false;
        } else return context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
    }

    /**
     * 小米 V5 版本 ROM权限申请
     */
    private static void goToMiuiPermissionActivity_V5(Context context) {
        String packageName = context.getPackageName();
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", packageName, null);
        intent.setData(uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (isIntentAvailable(intent, context)) {
//            (context as AppCompatActivity).startActivityForResult(intent, Constants.REQUEST_CODE_FLOAT_WINDOW)
            ((AppCompatActivity) context).startActivityForResult(intent, Constant.REQUEST_CODE_FLOAT_WINDOW);
        } else {
            LogUtils.i("intent is not available!");
        }

        //设置页面在应用详情页面
        //        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        //        PackageInfo pInfo = null;
        //        try {
        //            pInfo = context.getPackageManager().getPackageInfo
        //                    (HostInterfaceManager.getHostInterface().getApp().getPackageName(), 0);
        //        } catch (PackageManager.NameNotFoundException e) {
        //            AVLogUtils.e(TAG, e.getMessage());
        //        }
        //        intent.setClassName("com.android.settings", "com.miui.securitycenter.permission.AppPermissionsEditor");
        //        intent.putExtra("extra_package_uid", pInfo.applicationInfo.uid);
        //        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //        if (isIntentAvailable(intent, context)) {
        //            (context as AppCompatActivity).startActivityForResult(intent, Constants.REQUEST_CODE_FLOAT_WINDOW);
        //        } else {
        //            AVLogUtils.e(TAG, "Intent is not available!");
        //        }
    }

    /**
     * 小米 V6 版本 ROM权限申请
     */
    public static void goToMiuiPermissionActivity_V6(Context context) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
        intent.putExtra("extra_pkgname", context.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (isIntentAvailable(intent, context)) {
//            (context as AppCompatActivity).startActivityForResult(intent, Constants.REQUEST_CODE_FLOAT_WINDOW)
            ((AppCompatActivity) context).startActivityForResult(intent, Constant.REQUEST_CODE_FLOAT_WINDOW);
        } else {
            LogUtils.i("intent is not available!");
        }
    }

    /**
     * 小米 V7 版本 ROM权限申请
     */
    public static void goToMiuiPermissionActivity_V7(Context context) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
        intent.putExtra("extra_pkgname", context.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (isIntentAvailable(intent, context)) {
//            (context as AppCompatActivity).startActivityForResult(intent, Constants.REQUEST_CODE_FLOAT_WINDOW)
            ((AppCompatActivity) context).startActivityForResult(intent, Constant.REQUEST_CODE_FLOAT_WINDOW);
        } else {
            LogUtils.i("intent is not available!");
        }
    }

    /**
     * 小米 V8 版本 ROM权限申请
     */
    public static void goToMiuiPermissionActivity_V8(Context context) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
        //        intent.setPackage("com.miui.securitycenter");
        intent.putExtra("extra_pkgname", context.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (isIntentAvailable(intent, context)) {
//            (context as AppCompatActivity).startActivityForResult(intent, Constants.REQUEST_CODE_FLOAT_WINDOW)
            ((AppCompatActivity) context).startActivityForResult(intent, Constant.REQUEST_CODE_FLOAT_WINDOW);
        } else {
            intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
            intent.setPackage("com.miui.securitycenter");
            intent.putExtra("extra_pkgname", context.getPackageName());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            if (isIntentAvailable(intent, context)) {
//                (context as AppCompatActivity).startActivityForResult(intent, Constants.REQUEST_CODE_FLOAT_WINDOW)
                ((AppCompatActivity) context).startActivityForResult(intent, Constant.REQUEST_CODE_FLOAT_WINDOW);
            } else {
                LogUtils.i("intent is not available!");
            }
        }
    }

}
