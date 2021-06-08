package util;

import com.alibaba.fastjson.JSON;
import exception.MyException;
import jdk.nashorn.internal.scripts.JO;
import pojo.Job;
import pojo.Satellite;
import pojo.TimeWindow;

import javax.management.ObjectName;
import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Utils {

    /**字符串转Date对象**/
    public static Date str2Date(String str) {

        //创建SimpleDateFormat对象实例并定义好转换格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**Date对象转字符串**/
    public static String date2Str(Date date){
        //参考<https://www.cnblogs.com/zhengwanmeixiansen/p/7391411.html>
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //注意使用yyyy，而不是YYYY
        String form = sdf.format(date);
        return  form;
    }

    /**读取json文件**/
    public static String readJsonFile(String fileName) {
        //参考<https://www.cnblogs.com/wkfvawl/p/11876107.html>
        String jsonStr = "";
        try {
            File jsonFile = new File(fileName);
            FileReader fileReader = new FileReader(jsonFile);
            Reader reader = new InputStreamReader(new FileInputStream(jsonFile),"utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            reader.close();
            jsonStr = sb.toString();
            return jsonStr;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**写入json文件*/
    public static boolean writeJsonFile(String fileName,String json){
        FileWriter fw;
        try {
            fw = new FileWriter(fileName);
            PrintWriter out = new PrintWriter(fw);
            out.write(json);
            out.println();
            fw.close();
            out.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**通过当前时间startT和秒数duration，得到结束时间endT, duration可取任意值**/
    public static Date getEndT(Date startT, int duration){

        int second = startT.getSeconds();

        int temp = second + duration;  //原始秒 + 持续时间

        Date endT = Utils.Dateclone(startT);  //深拷贝
        endT.setSeconds(temp);  //更新日期（会自动进位，不用自己处理）

        return endT;
    }

    /**通过当前时间endT和秒数duration，得到开始时间startT, duration可取任意值**/
    public static Date getStartT(Date endT, int duration){

        int second = endT.getSeconds();

        int temp = second - duration;  //原始秒 + 持续时间

        Date startT = Utils.Dateclone(endT);  //深拷贝
        startT.setSeconds(temp);  //更新日期（会自动进位，不用自己处理）

        return startT;
    }

    /**深拷贝Date对象：这里先转成字符串，再转成Date**/
    public static Date Dateclone(Date date){
        String str = date2Str(date);
        //System.out.println(str);
        return str2Date(str);
    }

    /**将对象转化成json,这个对象可以是单个，也可以是Array*/
    public static String object2Json(Object object){

        String json = JSON.toJSONString(object);
        return json;
    }

    /**
     * 计算两个日期的时间差. 参考<https://www.cnblogs.com/xiaojf/p/12795507.html>
     */
    public static Integer getSecondsByTimeDifference(Date startT,Date endT){

        //date1 > date2
        Integer seconds = null;
        try
        {
            long diff = endT.getTime() - startT.getTime();//这样得到的差值是微秒级别
            //System.out.println(diff);
            seconds = (int) diff/1000;
        }catch (Exception e)
        {
            System.out.println("日期时间差计算出错");
        }
        return seconds;
    }

    /**Date转long类型的时间戳*/
    public static long date2Timestamp(Date date){
        long timeStemp =  date.getTime();
        return timeStemp;
    }

    /**long类型的时间戳转Date*/
    public static Date timestamp2Date(long timestamp){
        SimpleDateFormat t = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = t.format(new Date(timestamp*1000));
        System.out.println(dateStr);
        Date date = Utils.str2Date(dateStr);
        return date;
    }
}
