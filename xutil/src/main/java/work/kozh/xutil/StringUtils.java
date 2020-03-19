package work.kozh.xutil;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.Uri;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    private static ClipboardManager sClipboardManager;

    /**
     * 判断字符串是否有值，如果为null或者是空字符串或者只有空格或者为"null"字符串，则返回true，否则则返回false
     */
    public static boolean isEmpty(String value) {
        if (value != null && !"".equalsIgnoreCase(value.trim()) && !"null".equalsIgnoreCase(value.trim())) {
            return false;
        } else {
            return true;
        }
    }


    /**
     * 复制纯文本
     */
    public static void copySimpleText(String content) {
        sClipboardManager = (ClipboardManager) UIUtils.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(content, content);
        sClipboardManager.setPrimaryClip(clipData);
        ToastUtils.success(UIUtils.getContext(), "已复制文本");
    }

    /**
     * 复制网址
     */
    public static void copyUrl(Uri uri) {
        if (sClipboardManager == null) {
            sClipboardManager = (ClipboardManager) UIUtils.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        }
        ClipData clip = ClipData.newUri(UIUtils.getContext().getContentResolver(), "URI", uri);
        ToastUtils.success(UIUtils.getContext(), "已复制网址");
    }


    /**
     * 粘贴文本
     */
    public static String paste() {
        if (sClipboardManager == null) {
            sClipboardManager = (ClipboardManager) UIUtils.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        }
        ClipData clipData = sClipboardManager.getPrimaryClip();
        ClipData.Item item = clipData.getItemAt(0);
        //获取text
        String text = item.getText().toString();
        /*//获取uri
        Uri pasteUri = item.getUri();
        //获取intent:
        Intent intent = item.getIntent();*/
        return text;
    }


    /*
     * 判断字符串是否为数字
     * @param str 传入的字符串
     * @return 是整数返回true,否则返回false
     */
    public static boolean isNumber(String str) {
//        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }


    /**
     * 手机号码 的格式验证
     *
     * @param phoneNumber
     * @return
     */
    public static boolean phoneFormat(String phoneNumber) {
        Pattern pattern = Pattern
                .compile("((^(13|15|18)[0-9]{9}$)|(^0[1,2]{1}\\d{1}-?\\d{8}$)|(^0[3-9] {1}\\d{2}-?\\d{7,8}$)|(^0[1,2]{1}\\d{1}-?\\d{8}-(\\d{1,4})$)" +
                        "|(^0[3-9]{1}\\d{2}-?\\d{7,8}-(\\d{1,4})$))");
        Matcher matcher = pattern.matcher(phoneNumber);

//        Patterns.PHONE.matcher(phoneNumber).matches();

        return matcher.matches();
    }


    /**
     * 邮箱格式的验证
     *
     * @param email
     * @return
     */
    public static boolean emailFormat(String email) {
        Pattern pattern = Pattern
                .compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
        Matcher mc = pattern.matcher(email);

        //安卓SDK 提供的方法 内部已经实现了
//        boolean matches = Patterns.EMAIL_ADDRESS.matcher(email).matches();

        return mc.matches();
    }

    //<editor-fold desc="  ">
    //</editor-fold>
}
