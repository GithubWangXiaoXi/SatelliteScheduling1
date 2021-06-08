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

    /**�ַ���תDate����**/
    public static Date str2Date(String str) {

        //����SimpleDateFormat����ʵ���������ת����ʽ
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**Date����ת�ַ���**/
    public static String date2Str(Date date){
        //�ο�<https://www.cnblogs.com/zhengwanmeixiansen/p/7391411.html>
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //ע��ʹ��yyyy��������YYYY
        String form = sdf.format(date);
        return  form;
    }

    /**��ȡjson�ļ�**/
    public static String readJsonFile(String fileName) {
        //�ο�<https://www.cnblogs.com/wkfvawl/p/11876107.html>
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

    /**д��json�ļ�*/
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

    /**ͨ����ǰʱ��startT������duration���õ�����ʱ��endT, duration��ȡ����ֵ**/
    public static Date getEndT(Date startT, int duration){

        int second = startT.getSeconds();

        int temp = second + duration;  //ԭʼ�� + ����ʱ��

        Date endT = Utils.Dateclone(startT);  //���
        endT.setSeconds(temp);  //�������ڣ����Զ���λ�������Լ�����

        return endT;
    }

    /**ͨ����ǰʱ��endT������duration���õ���ʼʱ��startT, duration��ȡ����ֵ**/
    public static Date getStartT(Date endT, int duration){

        int second = endT.getSeconds();

        int temp = second - duration;  //ԭʼ�� + ����ʱ��

        Date startT = Utils.Dateclone(endT);  //���
        startT.setSeconds(temp);  //�������ڣ����Զ���λ�������Լ�����

        return startT;
    }

    /**���Date����������ת���ַ�������ת��Date**/
    public static Date Dateclone(Date date){
        String str = date2Str(date);
        //System.out.println(str);
        return str2Date(str);
    }

    /**������ת����json,�����������ǵ�����Ҳ������Array*/
    public static String object2Json(Object object){

        String json = JSON.toJSONString(object);
        return json;
    }

    /**
     * �����������ڵ�ʱ���. �ο�<https://www.cnblogs.com/xiaojf/p/12795507.html>
     */
    public static Integer getSecondsByTimeDifference(Date startT,Date endT){

        //date1 > date2
        Integer seconds = null;
        try
        {
            long diff = endT.getTime() - startT.getTime();//�����õ��Ĳ�ֵ��΢�뼶��
            //System.out.println(diff);
            seconds = (int) diff/1000;
        }catch (Exception e)
        {
            System.out.println("����ʱ���������");
        }
        return seconds;
    }

    /**Dateתlong���͵�ʱ���*/
    public static long date2Timestamp(Date date){
        long timeStemp =  date.getTime();
        return timeStemp;
    }

    /**long���͵�ʱ���תDate*/
    public static Date timestamp2Date(long timestamp){
        SimpleDateFormat t = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = t.format(new Date(timestamp*1000));
        System.out.println(dateStr);
        Date date = Utils.str2Date(dateStr);
        return date;
    }
}
