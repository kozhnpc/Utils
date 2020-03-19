package work.kozh.xutil;

import android.os.CountDownTimer;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class TimeUtils {

    public static final long ONE_MINUTE_MILLIONS = 60 * 1000;
    public static final long ONE_HOUR_MILLIONS = 60 * ONE_MINUTE_MILLIONS;
    private static CountDownTimer mTimer;

    public static final List<TextView> textViews = new ArrayList<>();

    // -------------------   倒计时   --------------------- //

    public static void addTextView(TextView textView) {
        textViews.add(textView);
    }

    public static void removeTextView(TextView textView) {
        textViews.remove(textView);
    }

    //这是倒计时执行方法
    public static void runTimer(TextView textView, long timeInterval) {
        //先取消之前的任务
        cancleTimely();
        if (mTimer == null) {
            mTimer = new CountDownTimer(timeInterval + 500, 1000) {
                @Override
                public void onFinish() {
//                    MusicPlayer.exit();
                    for (TextView view : textViews) {
                        view.setText("定时关闭");
                    }
                    cancel();
                    mTimer = null;
                }

                @Override
                public void onTick(long millisUntilFinished) {

                    for (TextView view : textViews) {
                        view.setVisibility(View.VISIBLE);
                        view.setText("音乐自动关闭倒计时： " + secToTime((int) (millisUntilFinished / 1000)));
                    }
                }
            };
        }
        mTimer.start();
    }

    //判断当前是否正在开启定时任务
    public static boolean isCounting() {
        return mTimer != null;
    }

    //这个方法可以在activity或者fragment销毁的时候调用，防止内存泄漏
    public static void cancleTimely() {
        if (mTimer != null) {
            mTimer.cancel();
            for (TextView view : textViews) {
                view.setText("定时关闭");
            }
            mTimer = null;
        }
    }


    //-----------------转换视频时间-----------------

    /**
     * 将秒数转换成00:00的字符串，如 118秒 -> 01:58
     *
     * @param time
     * @return
     */
    public static String secToTime(int time) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            return "00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":"
                        + unitFormat(second);
            }
        }
        return timeStr;
    }

    public static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }

    //-----------------转换视频下载剩余时间-----------------

    /**
     * HH:mm:ss
     *
     * @param seconds 时间  秒
     * @return HH:mm:ss
     */
    public static String seconds2HH_mm_ss(long seconds) {

        long h = 0;
        long m = 0;
        long s = 0;
        long temp = seconds % 3600;

        if (seconds > 3600) {
            h = seconds / 3600;
            if (temp != 0) {
                if (temp > 60) {
                    m = temp / 60;
                    if (temp % 60 != 0) {
                        s = temp % 60;
                    }
                } else {
                    s = temp;
                }
            }
        } else {
            m = seconds / 60;
            if (seconds % 60 != 0) {
                s = seconds % 60;
            }
        }

        String dh = h < 10 ? "0" + h : h + "";
        String dm = m < 10 ? "0" + m : m + "";
        String ds = s < 10 ? "0" + s : s + "";

        return dh + ":" + dm + ":" + ds;
    }


    // ----------------  将时间戳转化为具体的时间  ---------------------------

    /**
     * 时间戳转日期 带时分秒
     *
     * @param
     * @return
     */
    public static String transForDate(long timeStamp) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        String time = sdf.format(new Date(Long.parseLong(String.valueOf(timeStamp))));
        return time;

    }

    /**
     * 时间戳转换时间  不带时分秒
     *
     * @param
     * @return
     */
    public static String transForDateymr(long timeStamp) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        String time = sdf.format(new Date(Long.parseLong(String.valueOf(timeStamp))));

        return time;
    }


    //------------获取当前月1号 ------------

    /**
     * 获取当前月1号  返回格式yyyy-MM-dd (eg: 2007-06-01)
     * 如现在是2017-08-08，调用此方法将获取到这个月的1号，即"2017-08-01"。
     *
     * @return
     */
    public static String getMonthBegin() {
        String yearMonth = new SimpleDateFormat("yyyy-MM").format(new Date());
        return yearMonth + "-01";
    }


    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    //------------------与当前时间对比------------------

    /**
     * <p>
     * 输入想要对比的时间，"yyyy-MM-dd HH:mm:ss"格式，将有三种结果
     * <p>
     * 小于0，输入的时间比当前时间小，即输入的时间在当前时间之前；
     * 等于0，输入的时间和当前时间一致(时间相同)；
     * 大于0，输入的时间比当前时间大，即输入的时间在当前时间之后
     *
     * @param time
     * @return
     */
    public static long compareTime(String time) {
        long timeLong = 0;
        long curTimeLong = 0;

        try {
            timeLong = sdf.parse(time).getTime();
            curTimeLong = sdf.parse(getCurrentTimeString())
                    .getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return curTimeLong - timeLong;// 当前时间减去传入的时间
    }

    /**
     * 返回yyyy-MM-dd HH:mm:ss类型的时间字符串
     */
    public static String getCurrentTimeString() {
        return new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
    }


    /**
     * 得到剩余天数
     * <p>
     * 输入结束的时间，格式yyyy-MM-dd，得到剩余的天数，如当前时间是2017-08-08，
     * 输入结束的时间为2017-08-12，则得到的结果是4，即到达期限时间剩余4天。
     *
     * @param endTime 结束时间
     * @return
     */
    public static int getDayLast(String endTime) {
        try {
            long nowtime = new Date().getTime();
            long lastTime = new SimpleDateFormat("yyyy-MM-dd").parse(endTime).getTime();

            long distance = lastTime - nowtime;
            if (distance <= 0) {
                //如果是小于或等于0，则为0
                return 0;
            }

            double rate = distance / (1.0f * 24 * 3600 * 1000);
            int day = (int) (rate + 0.5f);
            return day;
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }

//-----------------获取短时间一(新闻类，博客类App，评论功能常用)-----------------

    /**
     * 获取短时间格式
     * <p>
     * <p>
     * <p>
     * 如果是距离当前时间是10分钟内，则显示"刚刚"；
     * 如果小于一小时则显示"x分钟前"
     * 如果大于1小时小于24小时，则显示"x小时前"；
     * 如果大于24小时，则显示"昨天xx时xx分"；
     * 如果大于两天则显示"yyyy-MM-dd"；
     *
     * @return
     */
    public static String getShortTime(long millis) {
        Date date = new Date(millis);
        Date curDate = new Date();

        String str = "";
        long durTime = curDate.getTime() - date.getTime();

        int dayStatus = calculateDayStatus(date, new Date());

        if (durTime <= 10 * ONE_MINUTE_MILLIONS) {
            str = "刚刚";
        } else if (durTime < ONE_HOUR_MILLIONS) {
            str = durTime / ONE_MINUTE_MILLIONS + "分钟前";
        } else if (dayStatus == 0) {
            str = durTime / ONE_HOUR_MILLIONS + "小时前";
        } else if (dayStatus == -1) {
            str = "昨天" + DateFormat.format("HH:mm", date);
        } else if (isSameYear(date, curDate) && dayStatus < -1) {
            str = DateFormat.format("MM-dd", date).toString();
        } else {
            str = DateFormat.format("yyyy-MM", date).toString();
        }
        return str;
    }

    /**
     * 判断是否是同一年
     *
     * @param targetTime
     * @param compareTime
     * @return
     */
    public static boolean isSameYear(Date targetTime, Date compareTime) {
        Calendar tarCalendar = Calendar.getInstance();
        tarCalendar.setTime(targetTime);
        int tarYear = tarCalendar.get(Calendar.YEAR);

        Calendar compareCalendar = Calendar.getInstance();
        compareCalendar.setTime(compareTime);
        int comYear = compareCalendar.get(Calendar.YEAR);

        return tarYear == comYear;
    }


    /**
     * 判断是否处于今天还是昨天，0表示今天，-1表示昨天，小于-1则是昨天以前
     *
     * @param targetTime
     * @param compareTime
     * @return
     */
    public static int calculateDayStatus(Date targetTime, Date compareTime) {
        Calendar tarCalendar = Calendar.getInstance();
        tarCalendar.setTime(targetTime);
        int tarDayOfYear = tarCalendar.get(Calendar.DAY_OF_YEAR);

        Calendar compareCalendar = Calendar.getInstance();
        compareCalendar.setTime(compareTime);
        int comDayOfYear = compareCalendar.get(Calendar.DAY_OF_YEAR);

        return tarDayOfYear - comDayOfYear;
    }


    //-------------------------------  获取短时间二(IM类App常用)------------------

    /**
     * 第一个参数为long类型的时间，第二个参数为boolean类型的值：
     * 若为true，则是用于聊天界面显示的时间格式，显示比较具体的时间
     * 如果是今天则显示"时间段 时:分"，如："凌晨01:08"，"上午 09:58"，"下午15:30"，"晚上 19:05"等
     * 如果是昨天则显示"昨天 时:分"的格式，如："昨天 09:58"；
     * 如果是同在一个星期或前天之前的时间，则显示"星期x 时:分"，如："星期一 09:58"
     * <p>
     * 若为false，则是用于会话记录界面(相当于微信的"微信"模块)显示的时间格式，不需要展示很具体的时间
     * 如果是今天，则直接显示"时:分"，如"09:58"；
     * 如果不是今天，不需要展示时和分，直接显示昨天、前天、星期几
     *
     * @param milliseconds 时间值
     * @param isDetail     是否需要显示具体时间段 + 时分和星期 + 时分
     * @return
     */
    public static String getTimeShowString(long milliseconds, boolean isDetail) {
        String dataString = "";
        String timeStringBy24 = "";

        Date currentTime = new Date(milliseconds);
        Date today = new Date();
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        Date todaybegin = todayStart.getTime();
        Date yesterdaybegin = new Date(todaybegin.getTime() - 3600 * 24 * 1000);
        Date preyesterday = new Date(
                yesterdaybegin.getTime() - 3600 * 24 * 1000);

        if (!currentTime.before(todaybegin)) {
            dataString = "今天";
        } else if (!currentTime.before(yesterdaybegin)) {
            dataString = "昨天";
        } else if (!currentTime.before(preyesterday)) {
            dataString = "前天";
        } else if (isSameWeekDates(currentTime, today)) {
            dataString = getWeekOfDate(currentTime);
        } else {
            SimpleDateFormat dateformatter = new SimpleDateFormat("yyyy-MM-dd",
                    Locale.getDefault());
            dataString = dateformatter.format(currentTime);
        }

        SimpleDateFormat timeformatter24 = new SimpleDateFormat("HH:mm",
                Locale.getDefault());
        timeStringBy24 = timeformatter24.format(currentTime);

        if (isDetail) {//显示具体的时间
            //在聊天界面显示时间，如果是今天则显示当前时间段加上时和分  如上午 9:58
            if (!currentTime.before(todaybegin)) {//如果是今天
                return getTodayTimeBucket(currentTime);//根据时间段分为凌晨 上午 下午等
            } else {
                //如果是昨天 则是 昨天 9：58 如果是同在一个星期，前天之前的时间则显示 星期一 9：58
                return dataString + " " + timeStringBy24;
            }
        } else {
            //在会话记录界面不需要展示很具体的时间
            if (!currentTime.before(todaybegin)) {//如果是今天
                return timeStringBy24;//直接返回时和分 如 9:58
            } else {
                return dataString;//如果不是今天，不需要展示时和分 如 昨天 前天 星期一
            }
        }
    }


    /**
     * 判断两个日期是否在同一周
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean isSameWeekDates(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        int subYear = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
        if (0 == subYear) {
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2
                    .get(Calendar.WEEK_OF_YEAR))
                return true;
        } else if (1 == subYear && 11 == cal2.get(Calendar.MONTH)) {
            // 如果12月的最后一周横跨来年第一周的话则最后一周即算做来年的第一周
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2
                    .get(Calendar.WEEK_OF_YEAR))
                return true;
        } else if (-1 == subYear && 11 == cal1.get(Calendar.MONTH)) {
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2
                    .get(Calendar.WEEK_OF_YEAR))
                return true;
        }
        return false;
    }

    /**
     * 根据日期获得星期
     *
     * @param date
     * @return
     */
    public static String getWeekOfDate(Date date) {
        String[] weekDaysName = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五",
                "星期六"};
        // String[] weekDaysCode = { "0", "1", "2", "3", "4", "5", "6" };
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int intWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        return weekDaysName[intWeek];
    }

    /**
     * 根据不同时间段，显示不同时间
     *
     * @param date
     * @return
     */
    public static String getTodayTimeBucket(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        SimpleDateFormat timeformatter0to11 = new SimpleDateFormat("KK:mm",
                Locale.getDefault());
        SimpleDateFormat timeformatter1to12 = new SimpleDateFormat("hh:mm",
                Locale.getDefault());
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour >= 0 && hour < 5) {
            return "凌晨 " + timeformatter0to11.format(date);
        } else if (hour >= 5 && hour < 12) {
            return "上午 " + timeformatter0to11.format(date);
        } else if (hour >= 12 && hour < 18) {
            return "下午 " + timeformatter1to12.format(date);
        } else if (hour >= 18 && hour < 24) {
            return "晚上 " + timeformatter1to12.format(date);
        }
        return "";
    }


    /**
     * 获取当前的时间，并格式化
     */

    public static String getNowDate(String format) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

}
