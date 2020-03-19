package work.kozh.xutil;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

/**
 * 一个APP
 * 若要使用这些utils，则必须要让自己的App继承MyApp  取消该类
 */
public class MyApp extends Application {

    public static Context ctx;
    public static int mainThreadId;
    private static Handler handler;

    @Override
    public void onCreate() {
        super.onCreate();


    }

    public static Context getCtx() {
        return ctx;
    }


    public static int getMainThreadId() {
        return mainThreadId;
    }


    public static Handler getHandler() {
        return handler;
    }
}
