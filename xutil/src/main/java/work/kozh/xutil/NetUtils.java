package work.kozh.xutil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by 00115702 on 2018/12/12.
 */

public class NetUtils {
    static String url_test = "http://kozhnpc.gz01.bdysite.com/test/1.txt";
    static String response_stream = null;

    public static String connect() {

        //3    原始方法
        Thread thread = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL url = new URL(URLEncoder.encode(url_test, "utf-8").replaceAll("\\+", "%20").replaceAll("%3A", ":").replaceAll("%2F", "/"));
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setConnectTimeout(1000);
                            if (connection.getResponseCode() == 200) {
                                InputStream in = connection.getInputStream();
                                response_stream = NetUtils.stream2String(in);
                                }
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
//                            Looper.prepare();
//                            Toast.makeText(OnesApplication.getCtx(), "无法连接到服务器，请检查网络...", Toast.LENGTH_SHORT).show();
//                            Looper.loop();
                            e.printStackTrace();
                        }
                    }
                }
        );
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
/**
 * 为了解决安卓9.0对于网络访问的限制，在xml中新添了解决方案
 *
 */

/**
 * -----------------------------以下是使用okhttp的效果，实际测试时发现网络访问延迟较大，暂不考虑该方法-------------------------------------------------
 */

        //1  同步
     /*   new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url_test)
                        .get()
                        .build();
                Call call = client.newCall(request);
                try {
                    Response response = call.execute();
                    byte[] b = response.body().bytes(); //获取数据的bytes
                    response_stream = new String(b, "GB2312"); //然后将其转为gb2312

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();*/


        //2   异步
        /*OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url_test)
                .get()
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Context context = OnesApplication.getCtx();
                //为了防止报错，需要加上这两句代码
                Looper.prepare();
                Toast.makeText(context, "无法连接到服务器，请检查网络...", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //解决中文乱码的问题，其实还可以用以前的方法来解决，选择输入流，再转化为字符
                byte[] b = response.body().bytes(); //获取数据的bytes
                response_stream = new String(b, "GB2312"); //然后将其转为gb2312
            }
        });*/
/**
 * ---------------------------------------------------------------END-------------------------------------------------------------------------
 */
        return response_stream;
    }


    //读取文件，将流转换为字符串进行读取
    public static String stream2String(InputStream in) {
        String result = "";
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int length = -1;
            while ((length = in.read(b)) != -1) {
                out.write(b, 0, length);
                out.flush();
            }
            //解决中文乱码
            byte[] bytes = out.toByteArray();
            result = new String(bytes, "GB2312");

//            result = out.toString();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


}
