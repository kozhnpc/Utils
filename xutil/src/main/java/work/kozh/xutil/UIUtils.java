package work.kozh.xutil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by 00115702 on 2018/12/20.
 */

public class UIUtils {

    private static float screenWidth;
    private static float screenHeight;

    public static int mainThreadId;
    private static Handler handler;

    @SuppressLint("StaticFieldLeak")
    private static Application sApplication;

    //用于获取一些与Acticity相关的方法和成员，作用于UI界面等等
    public static Context getContext() {
        return getApp().getApplicationContext();
    }

    //加载布局文件用
    public static View inflate(int id) {
        return View.inflate(getContext(), id, null);
    }


    /**
     * 根据id获取bitmap图片
     */
    public static Bitmap getBitmap(int id) {
        return BitmapFactory.decodeResource(getContext().getResources(), id);
    }

    /**
     * 根据id获取字符串
     */
    public static String getString(int id) {
        return getContext().getResources().getString(id);
    }

    /**
     * 根据id获取图片
     */
    public static Drawable getDrawable(int id) {
        return getContext().getResources().getDrawable(id);
    }

    /**
     * 根据id获取颜色值
     */
    public static int getColor(int id) {
        return getContext().getResources().getColor(id);
    }

    /**
     * 获取颜色状态集合
     */
    public static ColorStateList getColorStateList(int id) {
        return getContext().getResources().getColorStateList(id);
    }

    /**
     * 根据id获取尺寸
     */
    public static int getDimen(int id) {
        return getContext().getResources().getDimensionPixelSize(id);
    }

    /**
     * 根据id获取字符串数组
     */
    public static String[] getStringArray(int id) {
        return getContext().getResources().getStringArray(id);
    }

    /**
     * dp转px
     */
    public static int dp2px(float dp) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (density * dp + 0.5);
    }

    /**
     * px转dp
     */
    public static float px2dp(float px) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return px / density;
    }

    /**
     * sp转px
     */
    public static int sp2px(float spValue) {
        final float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }


    /**
     * @param activity 返回一个集合
     * @return dimen  0:screenHeight   1:screenWidth
     */
    public static float[] getScreenDimens(Activity activity) {
        float[] dimens = new float[2];
        // 获得手机屏幕的宽度和高度，单位像素
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;
        dimens[0] = screenHeight;
        dimens[1] = screenWidth;
        return dimens;
    }

    /**
     * 判断当前是 横屏还是竖屏
     *
     * @return
     */
    public static boolean isPortraitScreen() {
        Configuration configuration = getContext().getResources().getConfiguration();
        int orientation = configuration.orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //横屏
            return false;
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            //竖屏
            return true;
        }
        return true;
    }

    /**
     * 获取屏幕宽度
     *
     * @param context Context
     * @return 屏幕宽度（px）
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.getDefaultDisplay().getRealSize(point);
        } else {
            wm.getDefaultDisplay().getSize(point);
        }
        return point.x;
    }


    /**
     * 获取屏幕高度
     *
     * @param context Context
     * @return 屏幕高度（px）
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.getDefaultDisplay().getRealSize(point);
        } else {
            wm.getDefaultDisplay().getSize(point);
        }
        return point.y;
    }


    /**
     * 反射获取当前应用的Application
     *
     * @return
     */
    private static Application getApplicationByReflect() {
        try {
            @SuppressLint("PrivateApi")
            Class<?> activityThread = Class.forName("android.app.ActivityThread");
            Object thread = activityThread.getMethod("currentActivityThread").invoke(null);
            Object app = activityThread.getMethod("getApplication").invoke(thread);
            if (app == null) {
                throw new NullPointerException("u should init first");
            }
            return (Application) app;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        throw new NullPointerException("u should init first");
    }

    public static Application getApp() {
        if (sApplication != null) return sApplication;
        Application app = getApplicationByReflect();
//        init(app);
        return app;
    }


}
