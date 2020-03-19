package work.kozh.xutil;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import com.bumptech.glide.Glide;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * 文件操作相关
 * <p>
 * getFileByPath             : 根据文件路径获取文件
 * isFileExists              : 判断文件是否存在
 * rename                    : 重命名文件
 * isDir                     : 判断是否是目录
 * isFile                    : 判断是否是文件
 * createOrExistsDir         : 判断目录是否存在，不存在则判断是否创建成功
 * createOrExistsFile        : 判断文件是否存在，不存在则判断是否创建成功
 * createFileByDeleteOldFile : 判断文件是否存在，存在则在创建之前删除
 * copy                      : 复制文件或目录
 * move                      : 移动文件或目录
 * delete                    : 删除文件或目录
 * deleteAllInDir            : 删除目录下所有内容
 * deleteFilesInDir          : 删除目录下所有文件
 * deleteFilesInDirWithFilter: 删除目录下所有过滤的文件
 * listFilesInDir            : 获取目录下所有文件
 * listFilesInDirWithFilter  : 获取目录下所有过滤的文件
 * getFileLastModified       : 获取文件最后修改的毫秒时间戳
 * getFileCharsetSimple      : 简单获取文件编码格式
 * getFileLines              : 获取文件行数
 * getSize                   : 获取文件或目录大小
 * getLength                 : 获取文件或目录长度
 * getFileMD5                : 获取文件的 MD5 校验码
 * getFileMD5ToString        : 获取文件的 MD5 校验码
 * getDirName                : 根据全路径获取最长目录
 * getFileName               : 根据全路径获取文件名
 * getFileNameNoExtension    : 根据全路径获取文件名不带拓展名
 * getFileExtension          : 根据全路径获取文件拓展名
 * notifySystemToScan        : 通知系统扫描文件
 */
public class FileUtils {

    private static final int STOREUNIT = 1024;

    /**
     * 应用主文件夹
     *
     * @return
     */
    public static String getAppDir() {
        return Environment.getExternalStorageDirectory() + "/ONES/";
//        return "/storage/sdcard0/ONES";
    }

    /**
     * 音乐保存文件夹
     *
     * @return
     */
    public static String getMusicDir() {
        String dir = getAppDir() + "/Music/";
        return mkdirs(dir);
    }

    /**
     * 歌词保存文件夹
     *
     * @return
     */
    public static String getLrcDir() {
        String dir = getAppDir() + "/Lyric/";
        return mkdirs(dir);
    }

    /**
     * 图片保存文件夹
     *
     * @return
     */
    public static String getPicDir() {
        String dir = getAppDir() + "/pictures/";
        return mkdirs(dir);
    }

    /**
     * 下载文件夹
     *
     * @return
     */

    public static String getDownloadDir() {
        String dir = getAppDir() + "/download/";
        return mkdirs(dir);
    }

    /**
     * 视频下载文件夹
     *
     * @return
     */
    public static String getVideos() {
        String dir = getAppDir() + "/videos";
        return mkdirs(dir);
    }

    /**
     * 缓存文件夹
     *
     * @return
     */
    public static String getMusicCacheDir() {
        String dir = getAppDir() + "/caches";
        return mkdirs(dir);
    }


    private static String mkdirs(String dir) {
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdirs();
        }
        return dir;
    }


    //将Glide加载的图片保存下来
    public static void savePicToLocal(File source, File target) {
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileInputStream = new FileInputStream(source);
            fileOutputStream = new FileOutputStream(target);
            byte[] buffer = new byte[1024];
            while (fileInputStream.read(buffer) > 0) {
                fileOutputStream.write(buffer);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.i("error: " + e.getMessage());
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void savePicToLocal(String targetFileName, byte[] bytes) {
        try {
            //如果手机已插入sd卡,且app具有读写sd卡的权限
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                FileOutputStream output = null;
                output = new FileOutputStream(targetFileName);
                output.write(bytes);
                output.close();
            } else {

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 清理Gilide缓存
     *
     * @param
     */
    public static void clearGlideDiskCache(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Glide.get(context).clearDiskCache();
            }
        }).start();


    }

    /*
        从网址字符串中获取文件名
     */
    public static String getFileNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }


    /**
     * 转化文件单位.
     *
     * @param size 转化前大小(byte)
     * @return 转化后大小
     */
    public static String getFormatSize(double size) {
        double kiloByte = size / STOREUNIT;
        if (kiloByte < 1) {
            return size + " B";
        }

        double megaByte = kiloByte / STOREUNIT;
        if (megaByte < 1) {
            BigDecimal result = new BigDecimal(Double.toString(kiloByte));
            return result.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " KB";
        }

        double gigaByte = megaByte / STOREUNIT;
        if (gigaByte < 1) {
            BigDecimal result = new BigDecimal(Double.toString(megaByte));
            return result.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " MB";
        }

        double teraBytes = gigaByte / STOREUNIT;
        if (teraBytes < 1) {
            BigDecimal result = new BigDecimal(Double.toString(gigaByte));
            return result.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " GB";
        }
        BigDecimal result = new BigDecimal(teraBytes);
        return result.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " TB";
    }


    //将获取的文本写入到本地歌词文件中
    public static File writeLrcToLoc(String title, String artist, String lrcContext) {
        FileWriter writer = null;
        try {
            File file = new File(getLrcPath(title, artist));
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            writer = new FileWriter(getLrcPath(title, artist));
            String path = getLrcDir() + title + " - " + artist + ".lrc";
            LogUtils.i("写入时的歌词文件是：" + path);
            writer.write(lrcContext);
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (writer != null)
                    writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String getLrcPath(String title, String artist) {
        return getLrcDir() + title + " - " + artist + ".lrc";
    }


    //读取二进制文件，并存入byte数组
    public static byte[] readFromByteFile(String pathname) throws IOException {
        File filename = new File(pathname);
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(filename));
        ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
        byte[] temp = new byte[1024];
        int size = 0;
        while ((size = in.read(temp)) != -1) {
            out.write(temp, 0, size);
        }
        in.close();
        byte[] content = out.toByteArray();
        return content;
    }

    //读取文本文件，每次读取文件一行内容，并把结果存入字符串数组。
    public static ArrayList<String> readFromTextFile(String pathname) throws IOException {
        ArrayList<String> strArray = new ArrayList<String>();
        File filename = new File(pathname);
        InputStreamReader reader = new InputStreamReader(new FileInputStream(filename));
        BufferedReader br = new BufferedReader(reader);
        String line = "";
        line = br.readLine();
        while (line != null) {
            strArray.add(line);
            line = br.readLine();
        }
        return strArray;
    }


    /**
     * 复制文件
     *
     * @param resource 源文件
     * @param target   复制的目标文件
     */
    public static void copyFile(File resource, File target) throws Exception {
        // 输入流 --> 从一个目标读取数据
        // 输出流 --> 向一个目标写入数据

        long start = System.currentTimeMillis();

        // 文件输入流并进行缓冲
        FileInputStream inputStream = new FileInputStream(resource);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

        // 文件输出流并进行缓冲
        FileOutputStream outputStream = new FileOutputStream(target);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);

        // 缓冲数组
        // 大文件 可将 1024 * 2 改大一些，但是 并不是越大就越快
        byte[] bytes = new byte[1024 * 2];
        int len = 0;
        while ((len = inputStream.read(bytes)) != -1) {
            bufferedOutputStream.write(bytes, 0, len);
        }
        // 刷新输出缓冲流
        bufferedOutputStream.flush();
        //关闭流
        bufferedInputStream.close();
        bufferedOutputStream.close();
        inputStream.close();
        outputStream.close();

        long end = System.currentTimeMillis();

        System.out.println("耗时：" + (end - start) / 1000 + " s");

    }


    /**
     * 复制文件夹
     *
     * @param resource 源路径
     * @param target   目标路径
     */
    public static void copyFolder(String resource, String target) throws Exception {

        File resourceFile = new File(resource);
        if (!resourceFile.exists()) {
            throw new Exception("源目标路径：[" + resource + "] 不存在...");
        }
        File targetFile = new File(target);
        if (!targetFile.exists()) {
            throw new Exception("存放的目标路径：[" + target + "] 不存在...");
        }

        // 获取源文件夹下的文件夹或文件
        File[] resourceFiles = resourceFile.listFiles();

        for (File file : resourceFiles) {

            File file1 = new File(targetFile.getAbsolutePath() + File.separator + resourceFile.getName());
            // 复制文件
            if (file.isFile()) {
                System.out.println("文件" + file.getName());
                // 在 目标文件夹（B） 中 新建 源文件夹（A），然后将文件复制到 A 中
                // 这样 在 B 中 就存在 A
                if (!file1.exists()) {
                    file1.mkdirs();
                }
                File targetFile1 = new File(file1.getAbsolutePath() + File.separator + file.getName());
                copyFile(file, targetFile1);
            }
            // 复制文件夹
            if (file.isDirectory()) {// 复制源文件夹
                String dir1 = file.getAbsolutePath();
                // 目的文件夹
                String dir2 = file1.getAbsolutePath();
                copyFolder(dir1, dir2);
            }
        }

    }


    /**
     * 合并两个数组  支持泛型
     *
     * @param arrayLhs 左边的数组  即 放在前面的数组
     * @param arrayRhs 右边的数组  即 放在后面的数组
     * @return
     */
    public static Object combineArray(Object arrayLhs, Object arrayRhs) {
        Class<?> localClass = arrayLhs.getClass().getComponentType();
        //左边的数组长度
        int i = Array.getLength(arrayLhs);
        //合并后的数组长度
        int j = Array.getLength(arrayRhs) + i;
        //反射创建数组对象
        Object result = Array.newInstance(localClass, j);

        for (int k = 0; k < j; k++) {
            if (k < i) {
                //如果是左边的数组  直接复制
                Array.set(result, k, Array.get(arrayLhs, k));
            } else {
                //如果是开始读取右边的数组  需要判断一下位置 总长度k - 左边的长度  开始读取右边的数组数据
                Array.set(result, k, Array.get(arrayLhs, k - i));
            }
        }

        return result;
    }

    /**
     * 获取某个文件或文件夹下文件 的总大小
     *
     * @param dir 文件或文件夹
     * @return
     */
    public static long getAllFileSizeInDir(File dir) {
        long sum = 0;
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                //这里面用了递归的算法
                getAllFileSizeInDir(files[i]);
            } else {
                sum += files[i].length();
            }
        }
        return sum;
    }

    /**
     * 删除某个文件夹下的所有文件
     *
     * @param file
     */
    public static int deleteFile(File file) {

        int flag = 1;//用来判断文件是否删除成功

        //判断文件不为null或文件目录存在
        if (file == null || !file.exists()) {
            flag = 0;
            System.out.println("文件删除失败,请检查文件路径是否正确");
            return flag;
        }
        //取得这个目录下的所有子文件对象
        File[] files = file.listFiles();
        //遍历该目录下的文件对象
        for (File f : files) {
            //打印文件名
            String name = file.getName();
            System.out.println(name);
            //判断子目录是否存在子目录,如果是文件则删除
            if (f.isDirectory()) {
                deleteFile(f);
            } else {
                f.delete();
            }
        }
        //删除空文件夹  for循环已经把上一层节点的目录清空。
        file.delete();

        return flag;
    }


    //<editor-fold desc="输入流写入文件">

    /**
     * 将输入流写入文件
     *
     * @param filePath
     * @param is
     * @return
     */
    public static boolean writeFileFromIS(final String filePath, final InputStream is) {
        return writeFileFromIS(getFileByPath(filePath), is, false, null);
    }

    /**
     * 将输入流写入文件
     *
     * @param filePath
     * @param is
     * @param append   是否在原来文件的基础上加入
     * @return
     */
    public static boolean writeFileFromIS(final String filePath, final InputStream is, final boolean append) {
        return writeFileFromIS(getFileByPath(filePath), is, append, null);
    }

    /**
     * 将输入流写入文件
     *
     * @param file
     * @param is
     * @return
     */
    public static boolean writeFileFromIS(final File file, final InputStream is) {
        return writeFileFromIS(file, is, false, null);
    }

    /**
     * 将输入流写入文件
     *
     * @param file
     * @param is
     * @param append 是否在原来文件的基础上加入
     * @return
     */
    public static boolean writeFileFromIS(final File file, final InputStream is, final boolean append) {
        return writeFileFromIS(file, is, append, null);
    }

    /**
     * 将输入流写入文件  附带进度监听
     *
     * @param filePath
     * @param is
     * @param listener
     * @return
     */
    public static boolean writeFileFromIS(final String filePath, final InputStream is, final OnProgressUpdateListener listener) {
        return writeFileFromIS(getFileByPath(filePath), is, false, listener);
    }

    /**
     * 将输入流写入文件  附带进度监听
     *
     * @param filePath
     * @param is
     * @param append
     * @param listener 是否在原来文件的基础上加入
     * @return
     */
    public static boolean writeFileFromIS(final String filePath, final InputStream is, final boolean append,
                                          final OnProgressUpdateListener listener) {
        return writeFileFromIS(getFileByPath(filePath), is, append, listener);
    }

    /**
     * 将输入流写入文件  附带进度监听
     *
     * @param file
     * @param is
     * @param listener
     * @return
     */
    public static boolean writeFileFromIS(final File file, final InputStream is, final OnProgressUpdateListener listener) {
        return writeFileFromIS(file, is, false, listener);
    }

    /**
     * 将输入流写入文件  附带进度监听
     *
     * @param file
     * @param is
     * @param append   是否在原来文件的基础上加入
     * @param listener
     * @return
     */
    public static boolean writeFileFromIS(final File file, final InputStream is, final boolean append, final OnProgressUpdateListener listener) {
        if (is == null || !createOrExistsFile(file)) return false;
        OutputStream os = null;
        try {
            os = new BufferedOutputStream(new FileOutputStream(file, append), sBufferSize);
            if (listener == null) {
                byte[] data = new byte[sBufferSize];
                for (int len; (len = is.read(data)) != -1; ) {
                    os.write(data, 0, len);
                }
            } else {
                double totalSize = is.available();
                int curSize = 0;
                listener.onProgressUpdate(0);
                byte[] data = new byte[sBufferSize];
                for (int len; (len = is.read(data)) != -1; ) {
                    os.write(data, 0, len);
                    curSize += len;
                    listener.onProgressUpdate(curSize / totalSize);
                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //</editor-fold>

    //<editor-fold desc="工具方法">

    private static int sBufferSize = 524288;

    private static File getFileByPath(final String filePath) {
        return isSpace(filePath) ? null : new File(filePath);
    }

    private static boolean createOrExistsFile(final String filePath) {
        return createOrExistsFile(getFileByPath(filePath));
    }

    private static boolean createOrExistsFile(final File file) {
        if (file == null) return false;
        if (file.exists()) return file.isFile();
        if (!createOrExistsDir(file.getParentFile())) return false;
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean createOrExistsDir(final File file) {
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    private static boolean isFileExists(final File file) {
        return file != null && file.exists();
    }

    private static boolean isSpace(final String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private static byte[] is2Bytes(final InputStream is) {
        if (is == null) return null;
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            byte[] b = new byte[sBufferSize];
            int len;
            while ((len = is.read(b, 0, sBufferSize)) != -1) {
                os.write(b, 0, len);
            }
            return os.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //</editor-fold>


    //<editor-fold desc="文件进度监听">
    public interface OnProgressUpdateListener {

        void onProgressUpdate(double progress);

    }
    //</editor-fold>


    //<editor-fold desc="字节数组写入文件">

    /**
     * 字节数组写入文件
     *
     * @param filePath
     * @param bytes
     * @return
     */
    public static boolean writeFileFromBytesByStream(final String filePath, final byte[] bytes) {
        return writeFileFromBytesByStream(getFileByPath(filePath), bytes, false, null);
    }

    /**
     * 字节数组写入文件
     *
     * @param filePath
     * @param bytes
     * @param append   是否在原来文件基础上添加
     * @return
     */
    public static boolean writeFileFromBytesByStream(final String filePath, final byte[] bytes, final boolean append) {
        return writeFileFromBytesByStream(getFileByPath(filePath), bytes, append, null);
    }

    /**
     * 字节数组写入文件
     *
     * @param file
     * @param bytes
     * @return
     */
    public static boolean writeFileFromBytesByStream(final File file, final byte[] bytes) {
        return writeFileFromBytesByStream(file, bytes, false, null);
    }

    /**
     * 字节数组写入文件
     *
     * @param file
     * @param bytes
     * @param append 是否在原来文件基础上添加
     * @return
     */
    public static boolean writeFileFromBytesByStream(final File file, final byte[] bytes, final boolean append) {
        return writeFileFromBytesByStream(file, bytes, append, null);
    }


    /**
     * 字节数组写入文件  附带进度监听
     *
     * @param filePath
     * @param bytes
     * @param listener
     * @return
     */
    public static boolean writeFileFromBytesByStream(final String filePath,
                                                     final byte[] bytes,
                                                     final OnProgressUpdateListener listener) {
        return writeFileFromBytesByStream(getFileByPath(filePath), bytes, false, listener);
    }

    /**
     * 字节数组写入文件  附带进度监听
     *
     * @param filePath
     * @param bytes
     * @param append   是否在原来文件基础上添加
     * @param listener
     * @return
     */
    public static boolean writeFileFromBytesByStream(final String filePath,
                                                     final byte[] bytes,
                                                     final boolean append,
                                                     final OnProgressUpdateListener listener) {
        return writeFileFromBytesByStream(getFileByPath(filePath), bytes, append, listener);
    }

    /**
     * 字节数组写入文件  附带进度监听
     *
     * @param file
     * @param bytes
     * @param listener
     * @return
     */
    public static boolean writeFileFromBytesByStream(final File file,
                                                     final byte[] bytes,
                                                     final OnProgressUpdateListener listener) {
        return writeFileFromBytesByStream(file, bytes, false, listener);
    }

    /**
     * 字节数组写入文件  附带进度监听
     *
     * @param file
     * @param bytes
     * @param append   是否在原来文件基础上添加
     * @param listener
     * @return
     */
    public static boolean writeFileFromBytesByStream(final File file,
                                                     final byte[] bytes,
                                                     final boolean append,
                                                     final OnProgressUpdateListener listener) {
        if (bytes == null) return false;
        return writeFileFromIS(file, new ByteArrayInputStream(bytes), append, listener);
    }

    //</editor-fold>


    //<editor-fold desc="字符串写入文件">

    /**
     * 字符串写入文件
     *
     * @param filePath
     * @param content
     * @return
     */
    public static boolean writeFileFromString(final String filePath, final String content) {
        return writeFileFromString(getFileByPath(filePath), content, false);
    }

    /**
     * 字符串写入文件
     *
     * @param filePath
     * @param content
     * @param append   是否在原来文件基础上添加
     * @return
     */
    public static boolean writeFileFromString(final String filePath,
                                              final String content,
                                              final boolean append) {
        return writeFileFromString(getFileByPath(filePath), content, append);
    }

    /**
     * 字符串写入文件
     *
     * @param file
     * @param content
     * @return
     */
    public static boolean writeFileFromString(final File file, final String content) {
        return writeFileFromString(file, content, false);
    }

    /**
     * 字符串写入文件
     *
     * @param file
     * @param content
     * @param append
     * @return
     */
    private static boolean writeFileFromString(final File file,
                                               final String content,
                                               final boolean append) {
        if (file == null || content == null) return false;
        if (!createOrExistsFile(file)) return false;
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file, append));
            bw.write(content);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //</editor-fold>


    //<editor-fold desc="读取文件到字符串中">

    /**
     * 读取文件到字符串中
     *
     * @param filePath
     * @return
     */
    public static String readFile2String(final String filePath) {
        return readFile2String(getFileByPath(filePath), null);
    }

    /**
     * 读取文件到字符串中
     *
     * @param filePath
     * @param charsetName The name of charset.  格式  UTF-8
     * @return
     */
    public static String readFile2String(final String filePath, final String charsetName) {
        return readFile2String(getFileByPath(filePath), charsetName);
    }

    /**
     * 读取文件到字符串中
     *
     * @param file
     * @return
     */
    public static String readFile2String(final File file) {
        return readFile2String(file, null);
    }

    /**
     * 读取文件到字符串中
     *
     * @param file
     * @param charsetName
     * @return
     */
    public static String readFile2String(final File file, final String charsetName) {
        byte[] bytes = readFile2BytesByStream(file);
        if (bytes == null) return null;
        if (isSpace(charsetName)) {
            return new String(bytes);
        } else {
            try {
                return new String(bytes, charsetName);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return "";
            }
        }
    }


    //</editor-fold>

    //<editor-fold desc="读取文件到字节数组中">

    /**
     * 通过流 返回文件的字节数组
     *
     * @param filePath
     * @return
     */
    public static byte[] readFile2BytesByStream(final String filePath) {
        return readFile2BytesByStream(getFileByPath(filePath), null);
    }

    /**
     * 通过流 返回文件的字节数组
     *
     * @param file
     * @return
     */
    public static byte[] readFile2BytesByStream(final File file) {
        return readFile2BytesByStream(file, null);
    }

    /**
     * 通过流 返回文件的字节数组  进度监听
     *
     * @param filePath
     * @param listener
     * @return
     */
    public static byte[] readFile2BytesByStream(final String filePath,
                                                final OnProgressUpdateListener listener) {
        return readFile2BytesByStream(getFileByPath(filePath), listener);
    }

    /**
     * 通过流 返回文件的字节数组  进度监听
     *
     * @param file
     * @param listener
     * @return
     */
    public static byte[] readFile2BytesByStream(final File file,
                                                final OnProgressUpdateListener listener) {
        if (!isFileExists(file)) return null;
        try {
            ByteArrayOutputStream os = null;
            InputStream is = new BufferedInputStream(new FileInputStream(file), sBufferSize);
            try {
                os = new ByteArrayOutputStream();
                byte[] b = new byte[sBufferSize];
                int len;
                if (listener == null) {
                    while ((len = is.read(b, 0, sBufferSize)) != -1) {
                        os.write(b, 0, len);
                    }
                } else {
                    double totalSize = is.available();
                    int curSize = 0;
                    listener.onProgressUpdate(0);
                    while ((len = is.read(b, 0, sBufferSize)) != -1) {
                        os.write(b, 0, len);
                        curSize += len;
                        listener.onProgressUpdate(curSize / totalSize);
                    }
                }
                return os.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (os != null) {
                        os.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    //</editor-fold>

    //<editor-fold desc="获取文件行数">

    private static final String LINE_SEP = System.getProperty("line.separator");

    /**
     * 获取文件行数
     *
     * @param filePath
     * @return
     */
    public static int getFileLines(final String filePath) {
        return getFileLines(getFileByPath(filePath));
    }

    /**
     * 获取文件行数
     *
     * @param file
     * @return
     */
    public static int getFileLines(final File file) {
        int count = 1;
        InputStream is = null;
        try {
            is = new BufferedInputStream(new FileInputStream(file));
            byte[] buffer = new byte[1024];
            int readChars;
            if (LINE_SEP.endsWith("\n")) {
                while ((readChars = is.read(buffer, 0, 1024)) != -1) {
                    for (int i = 0; i < readChars; ++i) {
                        if (buffer[i] == '\n') ++count;
                    }
                }
            } else {
                while ((readChars = is.read(buffer, 0, 1024)) != -1) {
                    for (int i = 0; i < readChars; ++i) {
                        if (buffer[i] == '\r') ++count;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return count;
    }

    //</editor-fold>

    //<editor-fold desc="通知系统扫描文件">

    /**
     * 通知系统扫描文件
     *
     * @param file
     */
    public static void notifySystemToScan(final File file) {
        if (file == null || !file.exists()) return;
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        UIUtils.getContext().sendBroadcast(intent);
    }

    /**
     * 通知系统扫描文件
     *
     * @param filePath
     */
    public static void notifySystemToScan(final String filePath) {
        notifySystemToScan(getFileByPath(filePath));
    }

    //</editor-fold>

    //<editor-fold desc="根据全路径获取文件名不带拓展名">

    /**
     * 根据全路径获取文件名不带拓展名
     *
     * @param file
     * @return
     */
    public static String getFileNameNoExtension(final File file) {
        if (file == null) return "";
        return getFileNameNoExtension(file.getPath());
    }

    /**
     * 根据全路径获取文件名不带拓展名
     *
     * @param filePath
     * @return
     */
    public static String getFileNameNoExtension(final String filePath) {
        if (isSpace(filePath)) return "";
        int lastPoi = filePath.lastIndexOf('.');
        int lastSep = filePath.lastIndexOf(File.separator);
        if (lastSep == -1) {
            return (lastPoi == -1 ? filePath : filePath.substring(0, lastPoi));
        }
        if (lastPoi == -1 || lastSep > lastPoi) {
            return filePath.substring(lastSep + 1);
        }
        return filePath.substring(lastSep + 1, lastPoi);
    }

    //</editor-fold>


    //<editor-fold desc="根据全路径获取文件拓展名">

    /**
     * 根据全路径获取文件拓展名
     *
     * @param file
     * @return
     */
    public static String getFileExtension(final File file) {
        if (file == null) return "";
        return getFileExtension(file.getPath());
    }

    /**
     * 根据全路径获取文件拓展名
     *
     * @param filePath
     * @return
     */
    public static String getFileExtension(final String filePath) {
        if (isSpace(filePath)) return "";
        int lastPoi = filePath.lastIndexOf('.');
        int lastSep = filePath.lastIndexOf(File.separator);
        if (lastPoi == -1 || lastSep >= lastPoi) return "";
        return filePath.substring(lastPoi + 1);
    }

    //</editor-fold>


    //<editor-fold desc="  ">
    //</editor-fold>
}
