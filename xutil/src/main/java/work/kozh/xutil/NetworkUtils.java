package work.kozh.xutil;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.ACCESS_WIFI_STATE;
import static android.Manifest.permission.CHANGE_WIFI_STATE;
import static android.Manifest.permission.INTERNET;
import static android.content.Context.WIFI_SERVICE;

/**
 * 网络相关
 * <p>
 * penWirelessSettings                  : 打开网络设置界面
 * isConnected                           : 判断网络是否连接
 * isAvailable[Async]                    : 判断网络是否可用
 * isAvailableByPing[Async]              : 用 ping 判断网络是否可用
 * isAvailableByDns[Async]               : 用 DNS 判断网络是否可用
 * getMobileDataEnabled                  : 判断移动数据是否打开
 * isMobileData                          : 判断网络是否是移动数据
 * is4G                                  : 判断网络是否是 4G
 * getWifiEnabled                        : 判断 wifi 是否打开
 * setWifiEnabled                        : 打开或关闭 wifi
 * isWifiConnected                       : 判断 wifi 是否连接状态
 * isWifiAvailable[Async]                : 判断 wifi 数据是否可用
 * getNetworkOperatorName                : 获取移动网络运营商名称
 * getNetworkType                        : 获取当前网络类型
 * getIPAddress[Async]                   : 获取 IP 地址
 * getDomainAddress[Async]               : 获取域名 IP 地址
 * getIpAddressByWifi                    : 根据 WiFi 获取网络 IP 地址
 * getGatewayByWifi                      : 根据 WiFi 获取网关 IP 地址
 * getNetMaskByWifi                      : 根据 WiFi 获取子网掩码 IP 地址
 * getServerAddressByWifi                : 根据 WiFi 获取服务端 IP 地址
 * registerNetworkStatusChangedListener  : 注册网络状态改变监听器
 * unregisterNetworkStatusChangedListener: 注销网络状态改变监听器
 */
public class NetworkUtils {

    /**
     * 获取活动网络信息
     *
     * @param context 上下文
     * @return NetworkInfo
     */
    private static NetworkInfo getActiveNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }


    //判断是否有网络
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) {
            return false;
        }
        boolean available = info.isAvailable();
        return available;
    }

    /**
     * 判断网络是否是4G
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>}</p>
     *
     * @param context 上下文
     * @return {@code true}: 是<br>{@code false}: 不是
     */
    public static boolean is4G(Context context) {
        NetworkInfo info = getActiveNetworkInfo(context);
        return info != null && info.isAvailable() && info.getSubtype() == TelephonyManager.NETWORK_TYPE_LTE;
    }

    /**
     * 判断wifi是否连接状态
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>}</p>
     *
     * @param context 上下文
     * @return {@code true}: 连接<br>{@code false}: 未连接
     */
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm != null && cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * 判断wifi是否可用
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>}</p>
     *
     * @param context 上下文
     * @return {@code true}: 连接<br>{@code false}: 未连接
     */
    public static boolean isWifiAvaliable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm != null && cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;
    }


    //************************    新增   **************************************
    private NetworkUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public enum NetworkType {
        NETWORK_ETHERNET,
        NETWORK_WIFI,
        NETWORK_4G,
        NETWORK_3G,
        NETWORK_2G,
        NETWORK_UNKNOWN,
        NETWORK_NO
    }

    /**
     * Open the settings of wireless.
     */
    public static void openWirelessSettings() {
        UIUtils.getContext().startActivity(
                new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        );
    }

    /**
     * Return whether network is connected.
     * <p>Must hold {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />}</p>
     *
     * @return {@code true}: connected<br>{@code false}: disconnected
     */
    @RequiresPermission(ACCESS_NETWORK_STATE)
    public static boolean isConnected() {
        NetworkInfo info = getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    /**
     * Return whether network is available.
     * <p>Must hold {@code <uses-permission android:name="android.permission.INTERNET" />}</p>
     *
     * @param callback The callback.
     * @return the task
     */
    @RequiresPermission(INTERNET)
    public static Utils.Task<Boolean> isAvailableAsync(@NonNull final Utils.Callback<Boolean> callback) {
        return Utils.doAsync(new Utils.Task<Boolean>(callback) {
            @RequiresPermission(INTERNET)
            @Override
            public Boolean doInBackground() {
                return isAvailable();
            }
        });
    }

    /**
     * Return whether network is available.
     * <p>Must hold {@code <uses-permission android:name="android.permission.INTERNET" />}</p>
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    @RequiresPermission(INTERNET)
    public static boolean isAvailable() {
        return isAvailableByDns() || isAvailableByPing(null);
    }

    /**
     * Return whether network is available using ping.
     * <p>Must hold {@code <uses-permission android:name="android.permission.INTERNET" />}</p>
     * <p>The default ping ip: 223.5.5.5</p>
     *
     * @param callback The callback.
     */
    @RequiresPermission(INTERNET)
    public static void isAvailableByPingAsync(final Utils.Callback<Boolean> callback) {
        isAvailableByPingAsync("", callback);
    }

    /**
     * Return whether network is available using ping.
     * <p>Must hold {@code <uses-permission android:name="android.permission.INTERNET" />}</p>
     *
     * @param ip       The ip address.
     * @param callback The callback.
     * @return the task
     */
    @RequiresPermission(INTERNET)
    public static Utils.Task<Boolean> isAvailableByPingAsync(final String ip,
                                                             @NonNull final Utils.Callback<Boolean> callback) {
        return Utils.doAsync(new Utils.Task<Boolean>(callback) {
            @RequiresPermission(INTERNET)
            @Override
            public Boolean doInBackground() {
                return isAvailableByPing(ip);
            }
        });
    }

    /**
     * Return whether network is available using ping.
     * <p>Must hold {@code <uses-permission android:name="android.permission.INTERNET" />}</p>
     * <p>The default ping ip: 223.5.5.5</p>
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    @RequiresPermission(INTERNET)
    public static boolean isAvailableByPing() {
        return isAvailableByPing("");
    }

    /**
     * Return whether network is available using ping.
     * <p>Must hold {@code <uses-permission android:name="android.permission.INTERNET" />}</p>
     *
     * @param ip The ip address.
     * @return {@code true}: yes<br>{@code false}: no
     */
    @RequiresPermission(INTERNET)
    public static boolean isAvailableByPing(final String ip) {
        final String realIp = TextUtils.isEmpty(ip) ? "223.5.5.5" : ip;
        ShellUtils.CommandResult result = ShellUtils.execCmd(String.format("ping -c 1 %s", realIp), false);
        return result.result == 0;
    }

    /**
     * Return whether network is available using domain.
     * <p>Must hold {@code <uses-permission android:name="android.permission.INTERNET" />}</p>
     *
     * @param callback The callback.
     */
    @RequiresPermission(INTERNET)
    public static void isAvailableByDnsAsync(final Utils.Callback<Boolean> callback) {
        isAvailableByDnsAsync("", callback);
    }

    /**
     * Return whether network is available using domain.
     * <p>Must hold {@code <uses-permission android:name="android.permission.INTERNET" />}</p>
     *
     * @param domain   The name of domain.
     * @param callback The callback.
     * @return the task
     */
    @RequiresPermission(INTERNET)
    public static Utils.Task isAvailableByDnsAsync(final String domain,
                                                   @NonNull final Utils.Callback<Boolean> callback) {
        return Utils.doAsync(new Utils.Task<Boolean>(callback) {
            @RequiresPermission(INTERNET)
            @Override
            public Boolean doInBackground() {
                return isAvailableByDns(domain);
            }
        });
    }

    /**
     * Return whether network is available using domain.
     * <p>Must hold {@code <uses-permission android:name="android.permission.INTERNET" />}</p>
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    @RequiresPermission(INTERNET)
    public static boolean isAvailableByDns() {
        return isAvailableByDns("");
    }

    /**
     * Return whether network is available using domain.
     * <p>Must hold {@code <uses-permission android:name="android.permission.INTERNET" />}</p>
     *
     * @param domain The name of domain.
     * @return {@code true}: yes<br>{@code false}: no
     */
    @RequiresPermission(INTERNET)
    public static boolean isAvailableByDns(final String domain) {
        final String realDomain = TextUtils.isEmpty(domain) ? "www.baidu.com" : domain;
        InetAddress inetAddress;
        try {
            inetAddress = InetAddress.getByName(realDomain);
            return inetAddress != null;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Return whether mobile data is enabled.
     *
     * @return {@code true}: enabled<br>{@code false}: disabled
     */
    public static boolean getMobileDataEnabled() {
        try {
            TelephonyManager tm =
                    (TelephonyManager) Utils.getApp().getSystemService(Context.TELEPHONY_SERVICE);
            if (tm == null) return false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return tm.isDataEnabled();
            }
            @SuppressLint("PrivateApi")
            Method getMobileDataEnabledMethod =
                    tm.getClass().getDeclaredMethod("getDataEnabled");
            if (null != getMobileDataEnabledMethod) {
                return (boolean) getMobileDataEnabledMethod.invoke(tm);
            }
        } catch (Exception e) {
            Log.e("NetworkUtils", "getMobileDataEnabled: ", e);
        }
        return false;
    }

    /**
     * Return whether using mobile data.
     * <p>Must hold {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />}</p>
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    @RequiresPermission(ACCESS_NETWORK_STATE)
    public static boolean isMobileData() {
        NetworkInfo info = getActiveNetworkInfo();
        return null != info
                && info.isAvailable()
                && info.getType() == ConnectivityManager.TYPE_MOBILE;
    }

    /**
     * Return whether using 4G.
     * <p>Must hold {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />}</p>
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    @RequiresPermission(ACCESS_NETWORK_STATE)
    public static boolean is4G() {
        NetworkInfo info = getActiveNetworkInfo();
        return info != null
                && info.isAvailable()
                && info.getSubtype() == TelephonyManager.NETWORK_TYPE_LTE;
    }

    /**
     * Return whether wifi is enabled.
     * <p>Must hold {@code <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />}</p>
     *
     * @return {@code true}: enabled<br>{@code false}: disabled
     */
    @RequiresPermission(ACCESS_WIFI_STATE)
    public static boolean getWifiEnabled() {
        @SuppressLint("WifiManagerLeak")
        WifiManager manager = (WifiManager) Utils.getApp().getSystemService(WIFI_SERVICE);
        if (manager == null) return false;
        return manager.isWifiEnabled();
    }

    /**
     * Enable or disable wifi.
     * <p>Must hold {@code <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />}</p>
     *
     * @param enabled True to enabled, false otherwise.
     */
    @RequiresPermission(CHANGE_WIFI_STATE)
    public static void setWifiEnabled(final boolean enabled) {
        @SuppressLint("WifiManagerLeak")
        WifiManager manager = (WifiManager) Utils.getApp().getSystemService(WIFI_SERVICE);
        if (manager == null) return;
        if (enabled == manager.isWifiEnabled()) return;
        manager.setWifiEnabled(enabled);
    }

    /**
     * Return whether wifi is connected.
     * <p>Must hold {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />}</p>
     *
     * @return {@code true}: connected<br>{@code false}: disconnected
     */
    @RequiresPermission(ACCESS_NETWORK_STATE)
    public static boolean isWifiConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) Utils.getApp().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * Return whether wifi is available.
     * <p>Must hold {@code <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />},
     * {@code <uses-permission android:name="android.permission.INTERNET" />}</p>
     *
     * @return {@code true}: available<br>{@code false}: unavailable
     */
    @RequiresPermission(allOf = {ACCESS_WIFI_STATE, INTERNET})
    public static boolean isWifiAvailable() {
        return getWifiEnabled() && isAvailable();
    }

    /**
     * Return whether wifi is available.
     * <p>Must hold {@code <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />},
     * {@code <uses-permission android:name="android.permission.INTERNET" />}</p>
     *
     * @param callback The callback.
     * @return the task
     */
    @RequiresPermission(allOf = {ACCESS_WIFI_STATE, INTERNET})
    public static Utils.Task<Boolean> isWifiAvailableAsync(@NonNull final Utils.Callback<Boolean> callback) {
        return Utils.doAsync(new Utils.Task<Boolean>(callback) {
            @RequiresPermission(allOf = {ACCESS_WIFI_STATE, INTERNET})
            @Override
            public Boolean doInBackground() {
                return isWifiAvailable();
            }
        });
    }

    /**
     * Return the name of network operate.
     *
     * @return the name of network operate
     */
    public static String getNetworkOperatorName() {
        TelephonyManager tm =
                (TelephonyManager) Utils.getApp().getSystemService(Context.TELEPHONY_SERVICE);
        if (tm == null) return "";
        return tm.getNetworkOperatorName();
    }

    /**
     * Return type of network.
     * <p>Must hold {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />}</p>
     *
     * @return type of network
     * <ul>
     * <li>{@link NetworkUtils.NetworkType#NETWORK_ETHERNET} </li>
     * <li>{@link NetworkUtils.NetworkType#NETWORK_WIFI    } </li>
     * <li>{@link NetworkUtils.NetworkType#NETWORK_4G      } </li>
     * <li>{@link NetworkUtils.NetworkType#NETWORK_3G      } </li>
     * <li>{@link NetworkUtils.NetworkType#NETWORK_2G      } </li>
     * <li>{@link NetworkUtils.NetworkType#NETWORK_UNKNOWN } </li>
     * <li>{@link NetworkUtils.NetworkType#NETWORK_NO      } </li>
     * </ul>
     */
    @RequiresPermission(ACCESS_NETWORK_STATE)
    public static NetworkType getNetworkType() {
        if (isEthernet()) {
            return NetworkType.NETWORK_ETHERNET;
        }
        NetworkInfo info = getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                return NetworkType.NETWORK_WIFI;
            } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                switch (info.getSubtype()) {
                    case TelephonyManager.NETWORK_TYPE_GSM:
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        return NetworkType.NETWORK_2G;

                    case TelephonyManager.NETWORK_TYPE_TD_SCDMA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        return NetworkType.NETWORK_3G;

                    case TelephonyManager.NETWORK_TYPE_IWLAN:
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        return NetworkType.NETWORK_4G;

                    default:
                        String subtypeName = info.getSubtypeName();
                        if (subtypeName.equalsIgnoreCase("TD-SCDMA")
                                || subtypeName.equalsIgnoreCase("WCDMA")
                                || subtypeName.equalsIgnoreCase("CDMA2000")) {
                            return NetworkType.NETWORK_3G;
                        } else {
                            return NetworkType.NETWORK_UNKNOWN;
                        }
                }
            } else {
                return NetworkType.NETWORK_UNKNOWN;
            }
        }
        return NetworkType.NETWORK_NO;
    }

    /**
     * Return whether using ethernet.
     * <p>Must hold
     * {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />}</p>
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    @RequiresPermission(ACCESS_NETWORK_STATE)
    private static boolean isEthernet() {
        final ConnectivityManager cm =
                (ConnectivityManager) Utils.getApp().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        final NetworkInfo info = cm.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
        if (info == null) return false;
        NetworkInfo.State state = info.getState();
        if (null == state) return false;
        return state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING;
    }

    @RequiresPermission(ACCESS_NETWORK_STATE)
    private static NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager cm =
                (ConnectivityManager) Utils.getApp().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return null;
        return cm.getActiveNetworkInfo();
    }

    /**
     * Return the ip address.
     * <p>Must hold {@code <uses-permission android:name="android.permission.INTERNET" />}</p>
     *
     * @param useIPv4  True to use ipv4, false otherwise.
     * @param callback The callback.
     * @return the task
     */
    public static Utils.Task<String> getIPAddressAsync(final boolean useIPv4,
                                                       @NonNull final Utils.Callback<String> callback) {
        return Utils.doAsync(new Utils.Task<String>(callback) {
            @RequiresPermission(INTERNET)
            @Override
            public String doInBackground() {
                return getIPAddress(useIPv4);
            }
        });
    }

    /**
     * Return the ip address.
     * <p>Must hold {@code <uses-permission android:name="android.permission.INTERNET" />}</p>
     *
     * @param useIPv4 True to use ipv4, false otherwise.
     * @return the ip address
     */
    @RequiresPermission(INTERNET)
    public static String getIPAddress(final boolean useIPv4) {
        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            LinkedList<InetAddress> adds = new LinkedList<>();
            while (nis.hasMoreElements()) {
                NetworkInterface ni = nis.nextElement();
                // To prevent phone of xiaomi return "10.0.2.15"
                if (!ni.isUp() || ni.isLoopback()) continue;
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    adds.addFirst(addresses.nextElement());
                }
            }
            for (InetAddress add : adds) {
                if (!add.isLoopbackAddress()) {
                    String hostAddress = add.getHostAddress();
                    boolean isIPv4 = hostAddress.indexOf(':') < 0;
                    if (useIPv4) {
                        if (isIPv4) return hostAddress;
                    } else {
                        if (!isIPv4) {
                            int index = hostAddress.indexOf('%');
                            return index < 0
                                    ? hostAddress.toUpperCase()
                                    : hostAddress.substring(0, index).toUpperCase();
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Return the ip address of broadcast.
     *
     * @return the ip address of broadcast
     */
    public static String getBroadcastIpAddress() {
        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            LinkedList<InetAddress> adds = new LinkedList<>();
            while (nis.hasMoreElements()) {
                NetworkInterface ni = nis.nextElement();
                if (!ni.isUp() || ni.isLoopback()) continue;
                List<InterfaceAddress> ias = ni.getInterfaceAddresses();
                for (int i = 0, size = ias.size(); i < size; i++) {
                    InterfaceAddress ia = ias.get(i);
                    InetAddress broadcast = ia.getBroadcast();
                    if (broadcast != null) {
                        return broadcast.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Return the domain address.
     * <p>Must hold {@code <uses-permission android:name="android.permission.INTERNET" />}</p>
     *
     * @param domain   The name of domain.
     * @param callback The callback.
     * @return the task
     */
    @RequiresPermission(INTERNET)
    public static Utils.Task<String> getDomainAddressAsync(final String domain,
                                                           @NonNull final Utils.Callback<String> callback) {
        return Utils.doAsync(new Utils.Task<String>(callback) {
            @RequiresPermission(INTERNET)
            @Override
            public String doInBackground() {
                return getDomainAddress(domain);
            }
        });
    }

    /**
     * Return the domain address.
     * <p>Must hold {@code <uses-permission android:name="android.permission.INTERNET" />}</p>
     *
     * @param domain The name of domain.
     * @return the domain address
     */
    @RequiresPermission(INTERNET)
    public static String getDomainAddress(final String domain) {
        InetAddress inetAddress;
        try {
            inetAddress = InetAddress.getByName(domain);
            return inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Return the ip address by wifi.
     *
     * @return the ip address by wifi
     */
    @RequiresPermission(ACCESS_WIFI_STATE)
    public static String getIpAddressByWifi() {
        @SuppressLint("WifiManagerLeak")
        WifiManager wm = (WifiManager) Utils.getApp().getSystemService(WIFI_SERVICE);
        if (wm == null) return "";
        return Formatter.formatIpAddress(wm.getDhcpInfo().ipAddress);
    }

    /**
     * Return the gate way by wifi.
     *
     * @return the gate way by wifi
     */
    @RequiresPermission(ACCESS_WIFI_STATE)
    public static String getGatewayByWifi() {
        @SuppressLint("WifiManagerLeak")
        WifiManager wm = (WifiManager) Utils.getApp().getSystemService(WIFI_SERVICE);
        if (wm == null) return "";
        return Formatter.formatIpAddress(wm.getDhcpInfo().gateway);
    }

    /**
     * Return the net mask by wifi.
     *
     * @return the net mask by wifi
     */
    @RequiresPermission(ACCESS_WIFI_STATE)
    public static String getNetMaskByWifi() {
        @SuppressLint("WifiManagerLeak")
        WifiManager wm = (WifiManager) Utils.getApp().getSystemService(WIFI_SERVICE);
        if (wm == null) return "";
        return Formatter.formatIpAddress(wm.getDhcpInfo().netmask);
    }

    /**
     * Return the server address by wifi.
     *
     * @return the server address by wifi
     */
    @RequiresPermission(ACCESS_WIFI_STATE)
    public static String getServerAddressByWifi() {
        @SuppressLint("WifiManagerLeak")
        WifiManager wm = (WifiManager) Utils.getApp().getSystemService(WIFI_SERVICE);
        if (wm == null) return "";
        return Formatter.formatIpAddress(wm.getDhcpInfo().serverAddress);
    }

    /**
     * Register the status of network changed listener.
     *
     * @param listener The status of network changed listener
     */
    public static void registerNetworkStatusChangedListener(OnNetworkStatusChangedListener listener) {
        NetworkChangedReceiver.getInstance().registerListener(listener);
    }

    /**
     * unregister the status of network changed listener.
     *
     * @param listener The status of network changed listener
     */
    public static void unregisterNetworkStatusChangedListener(OnNetworkStatusChangedListener listener) {
        NetworkChangedReceiver.getInstance().unregisterListener(listener);
    }

    public static final class NetworkChangedReceiver extends BroadcastReceiver {

        private static NetworkChangedReceiver getInstance() {
            return LazyHolder.INSTANCE;
        }

        private NetworkType mType;
        private Set<OnNetworkStatusChangedListener> mListeners = new HashSet<>();

        void registerListener(final OnNetworkStatusChangedListener listener) {
            if (listener == null) return;
            Utils.runOnUiThread(new Runnable() {
                @SuppressLint("MissingPermission")
                @Override
                public void run() {
                    int preSize = mListeners.size();
                    mListeners.add(listener);
                    if (preSize == 0 && mListeners.size() == 1) {
                        mType = getNetworkType();
                        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
                        Utils.getApp().registerReceiver(NetworkChangedReceiver.getInstance(), intentFilter);
                    }
                }
            });
        }

        void unregisterListener(final OnNetworkStatusChangedListener listener) {
            if (listener == null) return;
            Utils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int preSize = mListeners.size();
                    mListeners.remove(listener);
                    if (preSize == 1 && mListeners.size() == 0) {
                        Utils.getApp().unregisterReceiver(NetworkChangedReceiver.getInstance());
                    }
                }
            });
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                // debouncing
                Utils.runOnUiThreadDelayed(new Runnable() {
                    @Override
                    public void run() {
                        NetworkType networkType = NetworkUtils.getNetworkType();
                        if (mType == networkType) return;
//                        LogUtils.e(networkType);
                        mType = networkType;
                        if (networkType == NetworkType.NETWORK_NO) {
                            for (OnNetworkStatusChangedListener listener : mListeners) {
                                listener.onDisconnected();
                            }
                        } else {
                            for (OnNetworkStatusChangedListener listener : mListeners) {
                                listener.onConnected(networkType);
                            }
                        }
                    }
                }, 1000);
            }
        }

        private static class LazyHolder {
            private static final NetworkChangedReceiver INSTANCE = new NetworkChangedReceiver();
        }
    }

    public interface OnNetworkStatusChangedListener {
        void onDisconnected();

        void onConnected(NetworkType networkType);
    }


    private static class ShellUtils {

        private static final String LINE_SEP = System.getProperty("line.separator");

        private ShellUtils() {
            throw new UnsupportedOperationException("u can't instantiate me...");
        }

        /**
         * Execute the command asynchronously.
         *
         * @param command  The command.
         * @param isRooted True to use root, false otherwise.
         * @param callback The callback.
         * @return the task
         */
        public static Utils.Task<CommandResult> execCmdAsync(final String command,
                                                             final boolean isRooted,
                                                             final Utils.Callback<CommandResult> callback) {
            return execCmdAsync(new String[]{command}, isRooted, true, callback);
        }

        /**
         * Execute the command asynchronously.
         *
         * @param commands The commands.
         * @param isRooted True to use root, false otherwise.
         * @param callback The callback.
         * @return the task
         */
        public static Utils.Task<CommandResult> execCmdAsync(final List<String> commands,
                                                             final boolean isRooted,
                                                             final Utils.Callback<CommandResult> callback) {
            return execCmdAsync(commands == null ? null : commands.toArray(new String[]{}), isRooted, true, callback);
        }

        /**
         * Execute the command asynchronously.
         *
         * @param commands The commands.
         * @param isRooted True to use root, false otherwise.
         * @param callback The callback.
         * @return the task
         */
        public static Utils.Task<CommandResult> execCmdAsync(final String[] commands,
                                                             final boolean isRooted,
                                                             final Utils.Callback<CommandResult> callback) {
            return execCmdAsync(commands, isRooted, true, callback);
        }

        /**
         * Execute the command asynchronously.
         *
         * @param command         The command.
         * @param isRooted        True to use root, false otherwise.
         * @param isNeedResultMsg True to return the message of result, false otherwise.
         * @param callback        The callback.
         * @return the task
         */
        public static Utils.Task<CommandResult> execCmdAsync(final String command,
                                                             final boolean isRooted,
                                                             final boolean isNeedResultMsg,
                                                             final Utils.Callback<CommandResult> callback) {
            return execCmdAsync(new String[]{command}, isRooted, isNeedResultMsg, callback);
        }

        /**
         * Execute the command asynchronously.
         *
         * @param commands        The commands.
         * @param isRooted        True to use root, false otherwise.
         * @param isNeedResultMsg True to return the message of result, false otherwise.
         * @param callback        The callback.
         * @return the task
         */
        public static Utils.Task<CommandResult> execCmdAsync(final List<String> commands,
                                                             final boolean isRooted,
                                                             final boolean isNeedResultMsg,
                                                             final Utils.Callback<CommandResult> callback) {
            return execCmdAsync(commands == null ? null : commands.toArray(new String[]{}),
                    isRooted,
                    isNeedResultMsg,
                    callback);
        }

        /**
         * Execute the command asynchronously.
         *
         * @param commands        The commands.
         * @param isRooted        True to use root, false otherwise.
         * @param isNeedResultMsg True to return the message of result, false otherwise.
         * @param callback        The callback.
         * @return the task
         */
        public static Utils.Task<CommandResult> execCmdAsync(final String[] commands,
                                                             final boolean isRooted,
                                                             final boolean isNeedResultMsg,
                                                             @NonNull final Utils.Callback<CommandResult> callback) {
            return Utils.doAsync(new Utils.Task<CommandResult>(callback) {
                @Override
                public CommandResult doInBackground() {
                    return execCmd(commands, isRooted, isNeedResultMsg);
                }
            });
        }

        /**
         * Execute the command.
         *
         * @param command  The command.
         * @param isRooted True to use root, false otherwise.
         * @return the single {@link CommandResult} instance
         */
        public static CommandResult execCmd(final String command, final boolean isRooted) {
            return execCmd(new String[]{command}, isRooted, true);
        }

        /**
         * Execute the command.
         *
         * @param commands The commands.
         * @param isRooted True to use root, false otherwise.
         * @return the single {@link CommandResult} instance
         */
        public static CommandResult execCmd(final List<String> commands, final boolean isRooted) {
            return execCmd(commands == null ? null : commands.toArray(new String[]{}), isRooted, true);
        }

        /**
         * Execute the command.
         *
         * @param commands The commands.
         * @param isRooted True to use root, false otherwise.
         * @return the single {@link CommandResult} instance
         */
        public static CommandResult execCmd(final String[] commands, final boolean isRooted) {
            return execCmd(commands, isRooted, true);
        }

        /**
         * Execute the command.
         *
         * @param command         The command.
         * @param isRooted        True to use root, false otherwise.
         * @param isNeedResultMsg True to return the message of result, false otherwise.
         * @return the single {@link CommandResult} instance
         */
        public static CommandResult execCmd(final String command,
                                            final boolean isRooted,
                                            final boolean isNeedResultMsg) {
            return execCmd(new String[]{command}, isRooted, isNeedResultMsg);
        }

        /**
         * Execute the command.
         *
         * @param commands        The commands.
         * @param isRooted        True to use root, false otherwise.
         * @param isNeedResultMsg True to return the message of result, false otherwise.
         * @return the single {@link CommandResult} instance
         */
        public static CommandResult execCmd(final List<String> commands,
                                            final boolean isRooted,
                                            final boolean isNeedResultMsg) {
            return execCmd(commands == null ? null : commands.toArray(new String[]{}),
                    isRooted,
                    isNeedResultMsg);
        }

        /**
         * Execute the command.
         *
         * @param commands        The commands.
         * @param isRooted        True to use root, false otherwise.
         * @param isNeedResultMsg True to return the message of result, false otherwise.
         * @return the single {@link CommandResult} instance
         */
        public static CommandResult execCmd(final String[] commands,
                                            final boolean isRooted,
                                            final boolean isNeedResultMsg) {
            int result = -1;
            if (commands == null || commands.length == 0) {
                return new CommandResult(result, "", "");
            }
            Process process = null;
            BufferedReader successResult = null;
            BufferedReader errorResult = null;
            StringBuilder successMsg = null;
            StringBuilder errorMsg = null;
            DataOutputStream os = null;
            try {
                process = Runtime.getRuntime().exec(isRooted ? "su" : "sh");
                os = new DataOutputStream(process.getOutputStream());
                for (String command : commands) {
                    if (command == null) continue;
                    os.write(command.getBytes());
                    os.writeBytes(LINE_SEP);
                    os.flush();
                }
                os.writeBytes("exit" + LINE_SEP);
                os.flush();
                result = process.waitFor();
                if (isNeedResultMsg) {
                    successMsg = new StringBuilder();
                    errorMsg = new StringBuilder();
                    successResult = new BufferedReader(
                            new InputStreamReader(process.getInputStream(), "UTF-8")
                    );
                    errorResult = new BufferedReader(
                            new InputStreamReader(process.getErrorStream(), "UTF-8")
                    );
                    String line;
                    if ((line = successResult.readLine()) != null) {
                        successMsg.append(line);
                        while ((line = successResult.readLine()) != null) {
                            successMsg.append(LINE_SEP).append(line);
                        }
                    }
                    if ((line = errorResult.readLine()) != null) {
                        errorMsg.append(line);
                        while ((line = errorResult.readLine()) != null) {
                            errorMsg.append(LINE_SEP).append(line);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (os != null) {
                        os.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (successResult != null) {
                        successResult.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (errorResult != null) {
                        errorResult.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (process != null) {
                    process.destroy();
                }
            }
            return new CommandResult(
                    result,
                    successMsg == null ? "" : successMsg.toString(),
                    errorMsg == null ? "" : errorMsg.toString()
            );
        }

        /**
         * The result of command.
         */
        public static class CommandResult {
            public int result;
            public String successMsg;
            public String errorMsg;

            public CommandResult(final int result, final String successMsg, final String errorMsg) {
                this.result = result;
                this.successMsg = successMsg;
                this.errorMsg = errorMsg;
            }

            @Override
            public String toString() {
                return "result: " + result + "\n" +
                        "successMsg: " + successMsg + "\n" +
                        "errorMsg: " + errorMsg;
            }
        }
    }


}
