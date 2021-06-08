package util;

import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

import pojo.Job;
import pojo.Satellite;
import pojo.TimeWindow;


public class PojoGenerator {

    /**根据jobs.json生成job列表**/
    public static List<Job> JobsGeneratorWithJson(String jobs_json){
        //参考<https://blog.csdn.net/xuforeverlove/article/details/80842148>,

        List<Job> jobs = JSON.parseArray(jobs_json, Job.class);
        return jobs;
    }

    /**根据satellites.json生成satellite列表**/
    public static List<Satellite> SatellitesGeneratorWithJson(String satellites_json){
        List<Satellite> satellites = JSON.parseArray(satellites_json, Satellite.class);
        return satellites;
    }

    /**为卫星生成起始时间为startT，结束时间为endT的，连续的，大小不一的时间窗TimeWindows, 时间窗范围为minWindowSize ~ maxWindowSize(单位s)**/
    public static List<TimeWindow> timeWindowsGeneratorWithRandomSize(Date startT,Date endT,int maxWindowSize,int minWindowSize){

        Random r = new Random();

        List<TimeWindow> timeWindows = new ArrayList<TimeWindow>();

        Date current = startT;
        int k = 1; //t_id自增
        while (current.compareTo(endT) < 0){  //如果当前时间大于结束时间则结束

            TimeWindow timeWindow = new TimeWindow();

            int duration =  r.nextInt(maxWindowSize - minWindowSize) + minWindowSize;
            Date next = Utils.getEndT(current,duration);

            timeWindow.setStartT(Utils.Dateclone(current));  //注意深拷贝,这里没有使用序列化和反序列化，而是将Date先转化成string，再转化成Date
            timeWindow.setEndT(Utils.Dateclone(next));
            timeWindow.setT_id(k);

            timeWindows.add(timeWindow);
            current = next;

            k++;
        }

        return timeWindows;
    }

    /**随机生成num个观测位置：<altitude,latitude,longitude>,注意区分南北纬, 东西经**/
    public static List<List<String>> locationGeneratorWithRandomPlace(int num){

        List<String> NS_longitude = new ArrayList<String>(Arrays.asList("N", "S"));
        List<String> EW_latitude = new ArrayList<String>(Arrays.asList("E","W"));

        int maxAltitude = 100;  //最大高度
        int maxLongitude = 180; //最大经度
        int maxLatitude = 90;  //最大纬度

        List<List<String>> locations = new ArrayList<List<String>>();
        Random r = new Random();

        //参考<https://www.cnblogs.com/Dtscal/p/3485405.html> 构造方法的字符格式这里如果小数不足2位,会以0补足.
        DecimalFormat dformat = new DecimalFormat(".00");
        for (int i = 0; i < num; i++) {
            List<String> location = new ArrayList<String>();

            String altitude = dformat.format(maxAltitude * r.nextFloat() + 1);//format 返回的是字符串
            String latitude = dformat.format(maxLatitude * r.nextFloat()) + "_"
                                            + EW_latitude.get(r.nextInt(2));
            String longitude = dformat.format(maxLongitude * r.nextFloat()) + "_"
                                            + NS_longitude.get(r.nextInt(2));

            location.add(altitude);
            location.add(latitude);
            location.add(longitude);

            locations.add(location);
        }

        return locations;
    }

//    /**
//     * 为每个总任务生成起始时间为startT，结束时间为endT的，离散的，观测时间大小不一的子任务Procedures，
//     *
//     */
//    public static List<Procedure> proceduresGeneratorWithRandomSize(Date startT,Date endT, int maxDuration, int minDuration, int maxInterval, int minInterval){
//
//        Random r = new Random();
//
//        List<Procedure> procedures = new ArrayList<Procedure>();
//
//        Date current = startT;
//        while (current.compareTo(endT) < 0){  //如果当前时间大于结束时间则结束
//
//            Procedure procedure = new Procedure();
//
//            int duration =  r.nextInt(maxDuration - minDuration) + minDuration;
//
//            procedure.setStartT(Utils.Dateclone(current));  //注意深拷贝,这里没有使用序列化和反序列化，而是将Date先转化成string，再转化成Date
//            procedure.setDuration(duration);
//            procedure.setEndT(Utils.Dateclone(Utils.getEndT(current,duration)));
//
//            int interval =  r.nextInt(maxInterval - minInterval) + minInterval;  //设置不同子任务之间的时间间隔
//
//            Date next = Utils.getEndT(current,duration + interval);  //获取下一个procedure的开始时间
//            procedures.add(procedure);
//            current = next;
//        }
//
//        return procedures;
//    }

    /**@Today 随机生成一个含约束条件：priority，resolution，sensor, duration, locations的任务Job,
     * 其中任务的startT，endT是通过startT_min,endT_max随机生成的,
     * minDuration ~ maxDuration表示任务的最小最大持续时间（单位s），minInterval ~ maxInterval表示不同任务之间最小最大时间间隔
     */
    public static Job jobGenerator(int id, String name, List<String> location, Date startT_min, Date endT_max, int maxDuration, int minDuration){

        Random r = new Random();

        Job job = new Job();
        job.setJob_Id(id);
        job.setName(name);
        job.setLocation(location);

        Integer priority = r.nextInt(5) + 1;  //随机生成priority,range = 1~5
        Integer resolution = (r.nextInt(3) * 5 + 8) ; //随机生成resolution,range = 8 ~ 18，且为5的整数倍
        Integer sensor = r.nextInt(4) + 1;  //随机生成sensor，range = 1 ~ 4, 分别代表1-可见光、2-SAR（合成孔径雷达）、3-红外、4-多光谱（多个）

        job.setPriority(priority);
        job.setResolution(resolution);
        job.setSensor(sensor);

        /**在给定最早起始时间和最晚结束时间，获取时间差，接着随机生成startT，endT和duration*/
        //获取时间差
        int diff_seconds = Utils.getSecondsByTimeDifference(startT_min,endT_max);

        //随机产生秒数s1，计算第i个任务的起始时间
        int s1 = r.nextInt(diff_seconds / 2);
        Date startT_random = Utils.getEndT(startT_min,s1);

        //随机生成duration
        int duration =  r.nextInt(maxDuration - minDuration) + minDuration;

        //随机产生秒数s2，计算第i个任务的结束时间
        Date end_min = Utils.getEndT(startT_random,duration);  //最起码的endT
        Date end_random = end_min;

        //必须保证end_random在startT_random + duration之后, 在end_max之前
        while(end_random.compareTo(end_min) <= 0 || end_random.compareTo(endT_max) >= 0){
            int s2 = r.nextInt(duration) + duration / 2 ;  //尽量保持该任务的时间灵活性，最小值为duration/2 + duration
            end_random = Utils.getEndT(startT_random,s2 + duration);
        }
        job.setStartT(startT_random);
        job.setEndT(end_random);
        job.setDuration(duration);

        return job;
    }

    /**随机生成一个含约束条件：sensors, MR, MTI的卫星Satellite；并组装上时间窗口timeWindows;
     * 并为每个时间窗口随机生成位置信息location。
     */
    public static Satellite satelliteGenerator(Integer id, String name, List<TimeWindow> timeWindows,List<List<String>> locations){
        Random r = new Random();
        int locationSize = locations.size();

        Satellite satellite = new Satellite();
        satellite.setName(name);

        int MR = (r.nextInt(2) + 1) * 5 + 1; //随机生成MR, range = 1 ~ 11，且为5的整数倍
        int MTI = (r.nextInt(4) + 1) * 20;  //随机生成MTI, range = 20 ~ 80，且为20的整数倍

        //随机生成不定长度的sensor，长度range为 3 ~ 4, 分别代表1-可见光、2-SAR（合成孔径雷达）、3-红外、4-多光谱（多个）
        int sensorSize = r.nextInt(2) + 3;  //sensors随机长度

        Set<Integer> sensors = new HashSet<Integer>();
        for (int i = 0; i < sensorSize; i++) {
            sensors.add(r.nextInt(4) + 1);  //随机sensorType
        }

        //为每个时间窗口装上location和satellite
        for (TimeWindow timeWindow: timeWindows) {
            int index = r.nextInt(locationSize);
            timeWindow.setLocation(locations.get(index));
            timeWindow.setSat_id(id);
        }

        satellite.setMR(MR);
        satellite.setMTI(MTI);
        satellite.setSensors(sensors);
        satellite.setTimeWindows(timeWindows);

        return satellite;
    }

    /**
     * @Alert 生成位置location集合相同的jobs，satellites，并序列化输出到jobs1.json，satellite1.json文件，如果成功返回True。
     * 注意procedure，timewindow是写死的，由于java不支持函数默认值，因此这里就简单实现一下，不对外开放
     */
    private static boolean satellitesAndJobsGeneratorWithSameLocationSet(int JobSize, int SatelliteSize, List<List<String>> locations, String earliest, String latest, String fileDir){

        List<Job> jobs = new ArrayList<Job>();

        Random r = new Random();
        for (int i = 1; i <= JobSize; i++) {

            int index = r.nextInt(3);
            Job job = PojoGenerator.jobGenerator(i,"Job" + i,locations.get(index),Utils.str2Date(earliest),
                    Utils.str2Date(latest), 400, 150);
            jobs.add(job);
            System.out.println(job + "\n");
        }

        String json = Utils.object2Json(jobs);  //序列化成json
        Utils.writeJsonFile(fileDir + "jobs_t1.json",json);

        List<Satellite> satellites = new ArrayList<Satellite>();
        for (int i = 1; i <= SatelliteSize; i++) {
            List<TimeWindow> timeWindows = PojoGenerator.timeWindowsGeneratorWithRandomSize(Utils.str2Date(earliest),
                    Utils.str2Date(latest), 500, 200);
            Satellite satellite = PojoGenerator.satelliteGenerator(i,"satellite" + i, timeWindows, locations);
            satellites.add(satellite);
            System.out.println(satellite + "\n");
        }

        json = Utils.object2Json(satellites);  //序列化成json
        Utils.writeJsonFile(fileDir + "satellites_t1.json",json);

        return true;
    }

    /**随机生成JobSize个任务，SatelliteSize个卫星，并运行python脚本绘制Gantt**/
    public static void main(String[] args) throws IOException, InterruptedException {
        List<List<String>> locations = locationGeneratorWithRandomPlace(3); //位置集合
        String earliest = "2021-05-26 00:00:00"; //任务最早执行时间
        String latest = "2021-05-26 1:00:00";  //任务最晚完成时间
        String fileDir = "src/main/resources/";

        //私有方法satellitesAndJobsGeneratorWithSameLocationSet()，不对外开放
        boolean ret = satellitesAndJobsGeneratorWithSameLocationSet(40,5,locations,earliest,latest,fileDir);
//        if(ret){
//            System.out.println("Generator and serialization succeed");
//
//            //使用python.plotly绘制Gantt
//            Process pr;
//            pr = Runtime.getRuntime().exec(new String[]{"cmd", "/c", "python E:\\研究生任务\\选星算法\\SatelliteScheduling\\drawGantt.py E:\\研究生任务\\选星算法\\SatelliteScheduling\\src\\main\\resources\\jobs.json E:\\研究生任务\\选星算法\\SatelliteScheduling\\src\\main\\resources\\satellites.json"});
//            pr.waitFor();
//        }
    }
}
