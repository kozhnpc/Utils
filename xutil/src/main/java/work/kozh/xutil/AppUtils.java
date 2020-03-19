package work.kozh.xutil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

/**
 * 关于应用相关的一些工具类  安装 卸载  应用信息等
 * <p>
 * registerAppStatusChangedListener  : 注册 App 前后台切换监听器
 * unregisterAppStatusChangedListener: 注销 App 前后台切换监听器
 * installApp                        : 安装 App（支持 8.0）
 * uninstallApp                      : 卸载 App
 * isAppInstalled                    : 判断 App 是否安装
 * isAppRoot                         : 判断 App 是否有 root 权限
 * isAppDebug                        : 判断 App 是否是 Debug 版本
 * isAppSystem                       : 判断 App 是否是系统应用
 * isAppForeground                   : 判断 App 是否处于前台
 * isAppRunning                      : 判断 App 是否运行
 * launchApp                         : 打开 App
 * relaunchApp                       : 重启 App
 * launchAppDetailsSettings          : 打开 App 具体设置
 * exitApp                           : 关闭应用
 * getAppIcon                        : 获取 App 图标
 * getAppPackageName                 : 获取 App 包名
 * getAppName                        : 获取 App 名称
 * getAppPath                        : 获取 App 路径
 * getAppVersionName                 : 获取 App 版本号
 * getAppVersionCode                 : 获取 App 版本码
 * getAppSignature                   : 获取 App 签名
 * getAppSignatureSHA1               : 获取应用签名的的 SHA1 值
 * getAppSignatureSHA256             : 获取应用签名的的 SHA256 值
 * getAppSignatureMD5                : 获取应用签名的的 MD5 值
 * getAppInfo                        : 获取 App 信息
 * getAppsInfo                       : 获取所有已安装 App 信息
 * getApkInfo                        : 获取 Apk 信息
 *
 *
 *
 * @author KOZH
 * @time 2020/1/17 11:19
 */
public class AppUtils {

    private AppUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 判断 App 是否是 Debug 版本
     * Return whether it is a debug application.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isAppDebug(Context context) {
        return isAppDebug(context, context.getPackageName());
    }

    /**
     * Return whether it is a debug application.
     *
     * @param packageName The name of the package.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isAppDebug(Context context, final String packageName) {
        if (TextUtils.isEmpty(packageName)) return false;
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(packageName, 0);
            return ai != null && (ai.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Launch the application.
     * 打开 App
     *
     * @param packageName The name of the package.
     */
    public static void launchApp(Context context, final String packageName) {
        if (TextUtils.isEmpty(packageName)) return;
        Intent launchAppIntent = getLaunchAppIntent(packageName, true, context);
        if (launchAppIntent == null) {
            Log.e("AppUtils", "Didn't exist launcher activity.");
            return;
        }
        context.startActivity(launchAppIntent);
    }


    /**
     * Launch the application.
     * 打开 App
     *
     * @param activity    The activity.
     * @param packageName The name of the package.
     * @param requestCode If &gt;= 0, this code will be returned in
     *                    onActivityResult() when the activity exits.
     */
    public static void launchApp(final Activity activity,
                                 final String packageName,
                                 final int requestCode) {
        if (TextUtils.isEmpty(packageName)) return;
        Intent launchAppIntent = getLaunchAppIntent(packageName, activity);
        if (launchAppIntent == null) {
            Log.e("AppUtils", "Didn't exist launcher activity.");
            return;
        }
        activity.startActivityForResult(launchAppIntent, requestCode);
    }

    private static Intent getLaunchAppIntent(final String packageName, Context context) {
        return getLaunchAppIntent(packageName, false, context);
    }

    private static Intent getLaunchAppIntent(final String packageName, final boolean isNewTask, Context context) {
        String launcherActivity = getLauncherActivity(packageName, context);
        if (!launcherActivity.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            ComponentName cn = new ComponentName(packageName, launcherActivity);
            intent.setComponent(cn);
            return isNewTask ? intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) : intent;
        }
        return null;
    }

    private static String getLauncherActivity(@NonNull final String pkg, Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setPackage(pkg);
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> info = pm.queryIntentActivities(intent, 0);
        int size = info.size();
        if (size == 0) return "";
        for (int i = 0; i < size; i++) {
            ResolveInfo ri = info.get(i);
            if (ri.activityInfo.processName.equals(pkg)) {
                return ri.activityInfo.name;
            }
        }
        return info.get(0).activityInfo.name;
    }

    /**
     * 重启APP
     * Relaunch the application.
     */
    public static void relaunchApp(Context context) {
        relaunchApp(false, context);
    }

    /**
     * Relaunch the application.
     *
     * @param isKillProcess True to kill the process, false otherwise.
     */
    public static void relaunchApp(final boolean isKillProcess, Context context) {
        Intent intent = getLaunchAppIntent(context.getPackageName(), true, context);
        if (intent == null) {
            Log.e("AppUtils", "Didn't exist launcher activity.");
            return;
        }
        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );
        context.startActivity(intent);
        if (!isKillProcess) return;
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }


    /**
     * Return the application's package name.
     * 获取应用的包名
     *
     * @return the application's package name
     */
    public static String getAppPackageName(Context context) {
        return context.getPackageName();
    }

    /**
     * Return the application's icon.
     * 获取应用的图标
     *
     * @return the application's icon
     */
    public static Drawable getAppIcon(Context context) {
        return getAppIcon(context.getPackageName(), context);
    }

    /**
     * Return the application's icon.
     *
     * @param packageName The name of the package.
     * @return the application's icon
     */
    public static Drawable getAppIcon(final String packageName, Context context) {
        if (TextUtils.isEmpty(packageName)) return null;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            return pi == null ? null : pi.applicationInfo.loadIcon(pm);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Return the application's name.
     * 获取应用的名称
     *
     * @return the application's name
     */
    public static String getAppName(Context context) {
        return getAppName(context.getPackageName(), context);
    }

    /**
     * Return the application's name.
     *
     * @param packageName The name of the package.
     * @return the application's name
     */
    public static String getAppName(final String packageName, Context context) {
        if (TextUtils.isEmpty(packageName)) return "";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            return pi == null ? null : pi.applicationInfo.loadLabel(pm).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }


    /**
     * Return the application's path.
     * 获取应用的路径
     *
     * @return the application's path
     */
    public static String getAppPath(Context context) {
        return getAppPath(context.getPackageName(), context);
    }

    /**
     * Return the application's path.
     *
     * @param packageName The name of the package.
     * @param context
     * @return the application's path
     */
    public static String getAppPath(final String packageName, Context context) {
        if (TextUtils.isEmpty(packageName)) return "";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            return pi == null ? null : pi.applicationInfo.sourceDir;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }


    /**
     * Return the application's version name.
     * 获取应用的版本名称
     *
     * @return the application's version name
     */
    public static String getAppVersionName(Context context) {
        return getAppVersionName(context.getPackageName(), context);
    }

    /**
     * Return the application's version name.
     *
     * @param packageName The name of the package.
     * @param context
     * @return the application's version name
     */
    public static String getAppVersionName(final String packageName, Context context) {
        if (TextUtils.isEmpty(packageName)) return "";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            return pi == null ? null : pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }


    /**
     * Return the application's version code.
     * 获取应用的版本码
     *
     * @return the application's version code
     */
    public static int getAppVersionCode(Context context) {
        return getAppVersionCode(context.getPackageName(), context);
    }

    /**
     * Return the application's version code.
     *
     * @param packageName The name of the package.
     * @param context
     * @return the application's version code
     */
    public static int getAppVersionCode(final String packageName, Context context) {
        if (TextUtils.isEmpty(packageName)) return -1;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            return pi == null ? -1 : pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }


    /**
     * Return the application's signature.
     * 获取应用的签名
     *
     * @param packageName The name of the package.
     * @return the application's signature
     */
    public static Signature[] getAppSignature(final String packageName, Context context) {
        if (TextUtils.isEmpty(packageName)) return null;
        try {
            PackageManager pm = context.getPackageManager();
            @SuppressLint("PackageManagerGetSignatures")
            PackageInfo pi = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            return pi == null ? null : pi.signatures;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Return the application's signature for SHA1 value.
     * 获取应用签名的SHA1
     *
     * @return the application's signature for SHA1 value
     */
    public static String getAppSignatureSHA1(Context context) {
        return getAppSignatureSHA1(context.getPackageName(), context);
    }

    /**
     * Return the application's signature for SHA1 value.
     *
     * @param packageName The name of the package.
     * @param context
     * @return the application's signature for SHA1 value
     */
    public static String getAppSignatureSHA1(final String packageName, Context context) {
        return getAppSignatureHash(packageName, "SHA1", context);
    }

    /**
     * Return the application's signature for SHA256 value.
     * 获取应用签名的SHA256
     *
     * @return the application's signature for SHA256 value
     */
    public static String getAppSignatureSHA256(Context context) {
        return getAppSignatureSHA256(context.getPackageName(), context);
    }

    /**
     * Return the application's signature for SHA256 value.
     *
     * @param packageName The name of the package.
     * @return the application's signature for SHA256 value
     */
    public static String getAppSignatureSHA256(final String packageName, Context context) {
        return getAppSignatureHash(packageName, "SHA256", context);
    }

    /**
     * Return the application's signature for MD5 value.
     * 获取应用签名的MD5
     *
     * @return the application's signature for MD5 value
     */
    public static String getAppSignatureMD5(Context context) {
        return getAppSignatureMD5(context.getPackageName(), context);
    }

    /**
     * Return the application's signature for MD5 value.
     *
     * @param packageName The name of the package.
     * @return the application's signature for MD5 value
     */
    public static String getAppSignatureMD5(final String packageName, Context context) {
        return getAppSignatureHash(packageName, "MD5", context);
    }

    private static String getAppSignatureHash(final String packageName, final String algorithm, Context context) {
        if (TextUtils.isEmpty(packageName)) return "";
        Signature[] signature = getAppSignature(packageName, context);
        if (signature == null || signature.length <= 0) return "";
        return bytes2HexString(hashTemplate(signature[0].toByteArray(), algorithm))
                .replaceAll("(?<=[0-9A-F]{2})[0-9A-F]{2}", ":$0");
    }

    private static String bytes2HexString(final byte[] bytes) {
        if (bytes == null) return "";
        int len = bytes.length;
        if (len <= 0) return "";
        char[] ret = new char[len << 1];
        for (int i = 0, j = 0; i < len; i++) {
            ret[j++] = HEX_DIGITS[bytes[i] >> 4 & 0x0f];
            ret[j++] = HEX_DIGITS[bytes[i] & 0x0f];
        }
        return new String(ret);
    }

    private static final char[] HEX_DIGITS =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private static byte[] hashTemplate(final byte[] data, final String algorithm) {
        if (data == null || data.length <= 0) return null;
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            md.update(data);
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Return the application's information.
     * 获取应用的信息  封装到一起了  外部通过get方法获取
     * <ul>
     * <li>name of package</li>    包名
     * <li>icon</li>            图标
     * <li>name</li>            名称
     * <li>path of package</li>  安装包路径
     * <li>version name</li>    版本名字
     * <li>version code</li>        版本码
     * <li>is system</li>  是否是系统应用
     * </ul>
     *
     * @param packageName The name of the package.
     * @return the application's information
     */
    public static AppInfo getAppInfo(final String packageName, Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            if (pm == null) return null;
            return getBean(pm, pm.getPackageInfo(packageName, 0));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static AppInfo getBean(final PackageManager pm, final PackageInfo pi) {
        if (pi == null) return null;
        ApplicationInfo ai = pi.applicationInfo;
        String packageName = pi.packageName;
        String name = ai.loadLabel(pm).toString();
        Drawable icon = ai.loadIcon(pm);
        String packagePath = ai.sourceDir;
        String versionName = pi.versionName;
        int versionCode = pi.versionCode;
        boolean isSystem = (ApplicationInfo.FLAG_SYSTEM & ai.flags) != 0;
        return new AppInfo(packageName, name, icon, packagePath, versionName, versionCode, isSystem);
    }


    /**
     * The application's information.
     */
    public static class AppInfo {

        private String packageName;
        private String name;
        private Drawable icon;
        private String packagePath;
        private String versionName;
        private int versionCode;
        private boolean isSystem;

        public Drawable getIcon() {
            return icon;
        }

        public void setIcon(final Drawable icon) {
            this.icon = icon;
        }

        public boolean isSystem() {
            return isSystem;
        }

        public void setSystem(final boolean isSystem) {
            this.isSystem = isSystem;
        }

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(final String packageName) {
            this.packageName = packageName;
        }

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public String getPackagePath() {
            return packagePath;
        }

        public void setPackagePath(final String packagePath) {
            this.packagePath = packagePath;
        }

        public int getVersionCode() {
            return versionCode;
        }

        public void setVersionCode(final int versionCode) {
            this.versionCode = versionCode;
        }

        public String getVersionName() {
            return versionName;
        }

        public void setVersionName(final String versionName) {
            this.versionName = versionName;
        }

        public AppInfo(String packageName, String name, Drawable icon, String packagePath,
                       String versionName, int versionCode, boolean isSystem) {
            this.setName(name);
            this.setIcon(icon);
            this.setPackageName(packageName);
            this.setPackagePath(packagePath);
            this.setVersionName(versionName);
            this.setVersionCode(versionCode);
            this.setSystem(isSystem);
        }

        @Override
        public String toString() {
            return "{" +
                    "\n    pkg name: " + getPackageName() +
                    "\n    app icon: " + getIcon() +
                    "\n    app name: " + getName() +
                    "\n    app path: " + getPackagePath() +
                    "\n    app v name: " + getVersionName() +
                    "\n    app v code: " + getVersionCode() +
                    "\n    is system: " + isSystem() +
                    "\n}";
        }
    }

    /**
     * Uninstall the app.
     * 卸载APP  获取具体的包名
     *
     * @param packageName The name of the package.
     */
    public static void uninstallApp(final String packageName, Context context) {
        if (TextUtils.isEmpty(packageName)) return;
        context.startActivity(getUninstallAppIntent(packageName, true));
    }

    /**
     * Uninstall the app.
     *
     * @param activity    The activity.
     * @param packageName The name of the package.
     * @param requestCode If &gt;= 0, this code will be returned in
     *                    onActivityResult() when the activity exits.
     */
    public static void uninstallApp(final Activity activity,
                                    final String packageName,
                                    final int requestCode) {
        if (TextUtils.isEmpty(packageName)) return;
        activity.startActivityForResult(getUninstallAppIntent(packageName), requestCode);
    }


    private static Intent getUninstallAppIntent(final String packageName) {
        return getUninstallAppIntent(packageName, false);
    }

    private static Intent getUninstallAppIntent(final String packageName, final boolean isNewTask) {
        Intent intent = new Intent(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:" + packageName));
        return isNewTask ? intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) : intent;
    }


    /**
     * Return whether the app is installed.
     * 检测APP是否已经安装   配合着卸载APP时使用，先检查应用是否已经安装，然后再卸载
     *
     * @param pkgName The name of the package.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isAppInstalled(@NonNull final String pkgName, Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            return packageManager.getApplicationInfo(pkgName, 0) != null;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Install the app.
     * 安装应用  已经支持到安卓 8.0
     * 不过需要额外添加下面的权限  允许应用安装
     * 还需要添加XML文件  详见SystemUtils中的方法
     *
     * <p>Target APIs greater than 25 must hold
     * {@code <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />}</p>
     *
     * @param filePath The path of file.
     */
    public static void installApp(final String filePath, Context context) {
        installApp(getFileByPath(filePath), context);
    }

    /**
     * Install the app.
     * <p>Target APIs greater than 25 must hold
     * {@code <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />}</p>
     *
     * @param file The file.
     */
    public static void installApp(final File file, Context context) {
        if (!isFileExists(file)) return;
        context.startActivity(getInstallAppIntent(file, true, context));
    }

    /**
     * Install the app.
     * <p>Target APIs greater than 25 must hold
     * {@code <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />}</p>
     *
     * @param activity    The activity.
     * @param filePath    The path of file.
     * @param requestCode If &gt;= 0, this code will be returned in
     *                    onActivityResult() when the activity exits.
     */
    public static void installApp(final Activity activity,
                                  final String filePath,
                                  final int requestCode) {
        installApp(activity, getFileByPath(filePath), requestCode);
    }


    /**
     * Install the app.
     * 安装APP
     * <p>Target APIs greater than 25 must hold
     * {@code <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />}</p>
     *
     * @param activity    The activity.
     * @param file        The file.
     * @param requestCode If &gt;= 0, this code will be returned in
     *                    onActivityResult() when the activity exits.
     */
    public static void installApp(final Activity activity,
                                  final File file,
                                  final int requestCode) {
        if (!isFileExists(file)) return;
        activity.startActivityForResult(getInstallAppIntent(file, activity), requestCode);
    }

    private static boolean isFileExists(final File file) {
        return file != null && file.exists();
    }

    private static File getFileByPath(final String filePath) {
        return TextUtils.isEmpty(filePath) ? null : new File(filePath);
    }

    private static Intent getInstallAppIntent(final File file, Context context) {
        return getInstallAppIntent(file, false, context);
    }

    private static Intent getInstallAppIntent(final File file, final boolean isNewTask, Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data;
        String type = "application/vnd.android.package-archive";
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            data = Uri.fromFile(file);
        } else {
            String authority = context.getPackageName() + ".provider";
            data = FileProvider.getUriForFile(context, authority, file);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        context.grantUriPermission(context.getPackageName(), data, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(data, type);
        return isNewTask ? intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) : intent;
    }

}
