import exception.MyException;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import util.*;

/**对Utils类方法进行调试：方法名 = 要调试的方法 + test**/
public class UtilTest {

    @Test
    public void date2StrTest(){
        //参考<https://www.cnblogs.com/zhengwanmeixiansen/p/7391411.html>
        Date date = new Date();
        String form = Utils.date2Str(date);
        System.out.println(form);
    }

    @Test
    public void str2DateTest(){
        Date date = Utils.str2Date("2005-12-15 15:30:23");
        System.out.println(date);
    }

    @Test
    /**通过startT，duration计算endT； 测试用例勿删，测试用例勿删，测试用例勿删！！！**/
    public void getEndTTest() throws MyException {

        Date date = null;
        Date endT = null;

        date = Utils.str2Date("2005-12-30 22:56:23");
        endT = Utils.getEndT(date,30);  //只更新second; "2005-12-30 22:56:53"
//        endT = Utils.getEndT(date,50); //更新second, min; "2005-12-30 22:57:13"
//        endT = Utils.getEndT(date,217); //更新second, min，hour; "2005-12-30 23:00:00"

//
//        date = Utils.str2Date("2005-12-30 23:56:23");
//        endT = Utils.getEndT(date,217); //更新second, min，hour，day; "2005-12-31 00:00:00"
//
//        date = Utils.str2Date("2005-11-30 23:56:23");
//        endT = Utils.getEndT(date,217); //更新second, min，hour，day，month; "2005-12-01 00:00:00"
//
//        date = Utils.str2Date("2005-12-31 23:56:23");
//        endT = Utils.getEndT(date,217);  //更新second, min，hour，day，month，year;  "2006-01-01 00:00:00"

        /**错误输出：原因未知**/
//        date = Utils.str2Date("2021-12-27 00:05:38"); //2022-12-27 00:12:18计算出错（解决方案：Y -> y)
//        date = Utils.str2Date("2021-05-27 00:05:38"); //2021-12-27 00:12:18计算正确
//        date = Utils.str2Date("2021-05-31 23:58:38"); //2021-06-01 00:05:18计算正确
//        date = Utils.str2Date("2021-12-01 23:58:38"); //2021-12-02 00:05:18计算正确
//        date = Utils.str2Date("2021-12-24 23:58:38"); //2021-12-25 00:05:18计算正确
//        date = Utils.str2Date("2021-12-25 23:58:38"); //2022-12-26 00:05:18计算错误（解决方案：Y -> y)
//        date = Utils.str2Date("2018-12-25 23:58:38"); //2018-12-26 00:05:18计算错误（解决方案：Y -> y)
//        date = Utils.str2Date("2018-12-29 23:58:38"); //2019-12-30 00:05:18计算错误（解决方案：Y -> y)
        endT = Utils.getEndT(date,400);
        System.out.println(Utils.date2Str(endT));
    }

    @Test
    public void DateComparationTest(){
        Date date = Utils.str2Date("2021-05-26 00:06:09");
        Date date1 = Utils.str2Date("2021-05-26 00:07:02");
        System.out.println(date.compareTo(date1));
    }

    @Test
    public void getSecondsByTimeDifferenceTest(){
        Date date = Utils.str2Date("2021-05-26 00:06:09");
        Date date1 = Utils.str2Date("2021-05-26 00:07:15");
        int seconds = Utils.getSecondsByTimeDifference(date1,date);
        System.out.println(seconds);
    }

    @Test
    public void getStartTTest(){
        Date endT = Utils.str2Date("2021-05-26 00:06:09");
        int duration = 60;
        Date startT = Utils.getStartT(endT, duration);
        System.out.println(startT);
    }

    @Test
    public void date2TimestampTest(){
        Date date = Utils.str2Date("2021-12-25 23:58:38");

        long l = Utils.date2Timestamp(date);
        System.out.println("2021-12-25 23:58:38的时间戳 = " + l);

        Date endT = Utils.getEndT(date,400);
        long l1 = Utils.date2Timestamp(endT);
        System.out.println(Utils.date2Str(endT) + "的时间戳 = " + l1);

        System.out.println("两个时间戳相差的秒数 = " + (l1 - l) / 1000);

        System.out.println("Long类型最大值 = " + Long.MAX_VALUE);
    }
}
