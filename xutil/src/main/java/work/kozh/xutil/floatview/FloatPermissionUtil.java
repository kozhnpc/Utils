package work.kozh.xutil.floatview;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import test.me.kozh.ones.utils.LogUtils;

/**
 * 桌面歌词 的工具类
 * 涉及到 悬浮窗权限管理  适配不同机型
 */
public class FloatPermissionUtil {

    public static void applyOrShowFloatWindow(Context context) {
        if (checkPermission(context)) {
        } else {
            applyPermission(context);
        }
    }


    public static void applyPermission(Context context) {
        if (Build.VERSION.SDK_INT < 23) {
            LogUtils.i("SDK版本低于23，开始申请权限...");
            if (RomUtil.checkIsMiuiRom()) {
                MiuiUtil.applyMiuiPermission(context);
            } else if (RomUtil.checkIsMeizuRom()) {
                MeizuUtil.applyPermission(context);
            } else if (RomUtil.checkIsHuaweiRom()) {
                HuaweiUtil.applyPermission(context);
            } else if (RomUtil.checkIs360Rom()) {
                QikuUtil.applyPermission(context);
            } else if (RomUtil.checkIsOppoRom()) {
                OppoUtil.applyOppoPermission(context);
            }
        } else {
            if (RomUtil.checkIsMeizuRom()) {
                MeizuUtil.applyPermission(context);
            } else {
                commonROMPermissionApplyInternal(context);
            }
        }
    }

    public static boolean checkPermission(Context context) {
        //6.0 版本之后由于 google 增加了对悬浮窗权限的管理，所以方式就统一了
        if (Build.VERSION.SDK_INT < 23) {
            if (RomUtil.checkIsMiuiRom()) {
                return miuiPermissionCheck(context);
            } else if (RomUtil.checkIsMeizuRom()) {
                return meizuPermissionCheck(context);
            } else if (RomUtil.checkIsHuaweiRom()) {
                LogUtils.i("这是华为rom");
                return huaweiPermissionCheck(context);
            } else if (RomUtil.checkIs360Rom()) {
                return qikuPermissionCheck(context);
            } else if (RomUtil.checkIsOppoRom()) {
                return oppoROMPermissionCheck(context);
            } else {
                //针对其他的ROM  直接返回true  可以直接添加悬浮窗   在6.0以下，不需要动态申请权限
                // 不过可能需要手动打开悬浮窗权限
                return true;
            }
        }

        return commonROMPermissionCheck(context);
    }

    public static void commonROMPermissionApplyInternal(Context context) {
        Class clazz = Settings.class;
        try {
            Field field = clazz.getDeclaredField("ACTION_MANAGE_OVERLAY_PERMISSION");
            Intent intent = new Intent(field.get(null).toString());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static boolean commonROMPermissionCheck(Context context) {
        //最新发现魅族6.0的系统这种方式不好用，天杀的，只有你是奇葩，没办法，单独适配一下
        if (RomUtil.checkIsMeizuRom()) {
            return meizuPermissionCheck(context);
        } else {
            boolean result = true;
            if (Build.VERSION.SDK_INT >= 23) {
                try {
                    Class clazz = Settings.class;
                    Method canDrawOverlays = clazz.getDeclaredMethod("canDrawOverlays", Context.class);
                    result = (boolean) canDrawOverlays.invoke(null, context);
                    return result;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return false;
        }

    }


    private static boolean meizuPermissionCheck(Context context) {
        return MeizuUtil.checkFloatWindowPermission(context);
    }

    private static boolean miuiPermissionCheck(Context context) {
        return MiuiUtil.checkFloatWindowPermission(context);
    }

    private static boolean huaweiPermissionCheck(Context context) {
        return HuaweiUtil.checkFloatWindowPermission(context);
    }

    private static boolean qikuPermissionCheck(Context context) {
        return QikuUtil.checkFloatWindowPermission(context);
    }

    private static boolean oppoROMPermissionCheck(Context context) {
        return OppoUtil.checkFloatWindowPermission(context);
    }

}
