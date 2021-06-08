package util;

import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

import pojo.Job;
import pojo.Satellite;
import pojo.TimeWindow;


public class PojoGenerator {

    /**����jobs.json����job�б�**/
    public static List<Job> JobsGeneratorWithJson(String jobs_json){
        //�ο�<https://blog.csdn.net/xuforeverlove/article/details/80842148>,

        List<Job> jobs = JSON.parseArray(jobs_json, Job.class);
        return jobs;
    }

    /**����satellites.json����satellite�б�**/
    public static List<Satellite> SatellitesGeneratorWithJson(String satellites_json){
        List<Satellite> satellites = JSON.parseArray(satellites_json, Satellite.class);
        return satellites;
    }

    /**Ϊ����������ʼʱ��ΪstartT������ʱ��ΪendT�ģ������ģ���С��һ��ʱ�䴰TimeWindows, ʱ�䴰��ΧΪminWindowSize ~ maxWindowSize(��λs)**/
    public static List<TimeWindow> timeWindowsGeneratorWithRandomSize(Date startT,Date endT,int maxWindowSize,int minWindowSize){

        Random r = new Random();

        List<TimeWindow> timeWindows = new ArrayList<TimeWindow>();

        Date current = startT;
        int k = 1; //t_id����
        while (current.compareTo(endT) < 0){  //�����ǰʱ����ڽ���ʱ�������

            TimeWindow timeWindow = new TimeWindow();

            int duration =  r.nextInt(maxWindowSize - minWindowSize) + minWindowSize;
            Date next = Utils.getEndT(current,duration);

            timeWindow.setStartT(Utils.Dateclone(current));  //ע�����,����û��ʹ�����л��ͷ����л������ǽ�Date��ת����string����ת����Date
            timeWindow.setEndT(Utils.Dateclone(next));
            timeWindow.setT_id(k);

            timeWindows.add(timeWindow);
            current = next;

            k++;
        }

        return timeWindows;
    }

    /**�������num���۲�λ�ã�<altitude,latitude,longitude>,ע�������ϱ�γ, ������**/
    public static List<List<String>> locationGeneratorWithRandomPlace(int num){

        List<String> NS_longitude = new ArrayList<String>(Arrays.asList("N", "S"));
        List<String> EW_latitude = new ArrayList<String>(Arrays.asList("E","W"));

        int maxAltitude = 100;  //���߶�
        int maxLongitude = 180; //��󾭶�
        int maxLatitude = 90;  //���γ��

        List<List<String>> locations = new ArrayList<List<String>>();
        Random r = new Random();

        //�ο�<https://www.cnblogs.com/Dtscal/p/3485405.html> ���췽�����ַ���ʽ�������С������2λ,����0����.
        DecimalFormat dformat = new DecimalFormat(".00");
        for (int i = 0; i < num; i++) {
            List<String> location = new ArrayList<String>();

            String altitude = dformat.format(maxAltitude * r.nextFloat() + 1);//format ���ص����ַ���
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
//     * Ϊÿ��������������ʼʱ��ΪstartT������ʱ��ΪendT�ģ���ɢ�ģ��۲�ʱ���С��һ��������Procedures��
//     *
//     */
//    public static List<Procedure> proceduresGeneratorWithRandomSize(Date startT,Date endT, int maxDuration, int minDuration, int maxInterval, int minInterval){
//
//        Random r = new Random();
//
//        List<Procedure> procedures = new ArrayList<Procedure>();
//
//        Date current = startT;
//        while (current.compareTo(endT) < 0){  //�����ǰʱ����ڽ���ʱ�������
//
//            Procedure procedure = new Procedure();
//
//            int duration =  r.nextInt(maxDuration - minDuration) + minDuration;
//
//            procedure.setStartT(Utils.Dateclone(current));  //ע�����,����û��ʹ�����л��ͷ����л������ǽ�Date��ת����string����ת����Date
//            procedure.setDuration(duration);
//            procedure.setEndT(Utils.Dateclone(Utils.getEndT(current,duration)));
//
//            int interval =  r.nextInt(maxInterval - minInterval) + minInterval;  //���ò�ͬ������֮���ʱ����
//
//            Date next = Utils.getEndT(current,duration + interval);  //��ȡ��һ��procedure�Ŀ�ʼʱ��
//            procedures.add(procedure);
//            current = next;
//        }
//
//        return procedures;
//    }

    /**@Today �������һ����Լ��������priority��resolution��sensor, duration, locations������Job,
     * ���������startT��endT��ͨ��startT_min,endT_max������ɵ�,
     * minDuration ~ maxDuration��ʾ�������С������ʱ�䣨��λs����minInterval ~ maxInterval��ʾ��ͬ����֮����С���ʱ����
     */
    public static Job jobGenerator(int id, String name, List<String> location, Date startT_min, Date endT_max, int maxDuration, int minDuration){

        Random r = new Random();

        Job job = new Job();
        job.setJob_Id(id);
        job.setName(name);
        job.setLocation(location);

        Integer priority = r.nextInt(5) + 1;  //�������priority,range = 1~5
        Integer resolution = (r.nextInt(3) * 5 + 8) ; //�������resolution,range = 8 ~ 18����Ϊ5��������
        Integer sensor = r.nextInt(4) + 1;  //�������sensor��range = 1 ~ 4, �ֱ����1-�ɼ��⡢2-SAR���ϳɿ׾��״��3-���⡢4-����ף������

        job.setPriority(priority);
        job.setResolution(resolution);
        job.setSensor(sensor);

        /**�ڸ���������ʼʱ����������ʱ�䣬��ȡʱ�������������startT��endT��duration*/
        //��ȡʱ���
        int diff_seconds = Utils.getSecondsByTimeDifference(startT_min,endT_max);

        //�����������s1�������i���������ʼʱ��
        int s1 = r.nextInt(diff_seconds / 2);
        Date startT_random = Utils.getEndT(startT_min,s1);

        //�������duration
        int duration =  r.nextInt(maxDuration - minDuration) + minDuration;

        //�����������s2�������i������Ľ���ʱ��
        Date end_min = Utils.getEndT(startT_random,duration);  //�������endT
        Date end_random = end_min;

        //���뱣֤end_random��startT_random + duration֮��, ��end_max֮ǰ
        while(end_random.compareTo(end_min) <= 0 || end_random.compareTo(endT_max) >= 0){
            int s2 = r.nextInt(duration) + duration / 2 ;  //�������ָ������ʱ������ԣ���СֵΪduration/2 + duration
            end_random = Utils.getEndT(startT_random,s2 + duration);
        }
        job.setStartT(startT_random);
        job.setEndT(end_random);
        job.setDuration(duration);

        return job;
    }

    /**�������һ����Լ��������sensors, MR, MTI������Satellite������װ��ʱ�䴰��timeWindows;
     * ��Ϊÿ��ʱ�䴰���������λ����Ϣlocation��
     */
    public static Satellite satelliteGenerator(Integer id, String name, List<TimeWindow> timeWindows,List<List<String>> locations){
        Random r = new Random();
        int locationSize = locations.size();

        Satellite satellite = new Satellite();
        satellite.setName(name);

        int MR = (r.nextInt(2) + 1) * 5 + 1; //�������MR, range = 1 ~ 11����Ϊ5��������
        int MTI = (r.nextInt(4) + 1) * 20;  //�������MTI, range = 20 ~ 80����Ϊ20��������

        //������ɲ������ȵ�sensor������rangeΪ 3 ~ 4, �ֱ����1-�ɼ��⡢2-SAR���ϳɿ׾��״��3-���⡢4-����ף������
        int sensorSize = r.nextInt(2) + 3;  //sensors�������

        Set<Integer> sensors = new HashSet<Integer>();
        for (int i = 0; i < sensorSize; i++) {
            sensors.add(r.nextInt(4) + 1);  //���sensorType
        }

        //Ϊÿ��ʱ�䴰��װ��location��satellite
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
     * @Alert ����λ��location������ͬ��jobs��satellites�������л������jobs1.json��satellite1.json�ļ�������ɹ�����True��
     * ע��procedure��timewindow��д���ģ�����java��֧�ֺ���Ĭ��ֵ���������ͼ�ʵ��һ�£������⿪��
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

        String json = Utils.object2Json(jobs);  //���л���json
        Utils.writeJsonFile(fileDir + "jobs_t1.json",json);

        List<Satellite> satellites = new ArrayList<Satellite>();
        for (int i = 1; i <= SatelliteSize; i++) {
            List<TimeWindow> timeWindows = PojoGenerator.timeWindowsGeneratorWithRandomSize(Utils.str2Date(earliest),
                    Utils.str2Date(latest), 500, 200);
            Satellite satellite = PojoGenerator.satelliteGenerator(i,"satellite" + i, timeWindows, locations);
            satellites.add(satellite);
            System.out.println(satellite + "\n");
        }

        json = Utils.object2Json(satellites);  //���л���json
        Utils.writeJsonFile(fileDir + "satellites_t1.json",json);

        return true;
    }

    /**�������JobSize������SatelliteSize�����ǣ�������python�ű�����Gantt**/
    public static void main(String[] args) throws IOException, InterruptedException {
        List<List<String>> locations = locationGeneratorWithRandomPlace(3); //λ�ü���
        String earliest = "2021-05-26 00:00:00"; //��������ִ��ʱ��
        String latest = "2021-05-26 1:00:00";  //�����������ʱ��
        String fileDir = "src/main/resources/";

        //˽�з���satellitesAndJobsGeneratorWithSameLocationSet()�������⿪��
        boolean ret = satellitesAndJobsGeneratorWithSameLocationSet(40,5,locations,earliest,latest,fileDir);
//        if(ret){
//            System.out.println("Generator and serialization succeed");
//
//            //ʹ��python.plotly����Gantt
//            Process pr;
//            pr = Runtime.getRuntime().exec(new String[]{"cmd", "/c", "python E:\\�о�������\\ѡ���㷨\\SatelliteScheduling\\drawGantt.py E:\\�о�������\\ѡ���㷨\\SatelliteScheduling\\src\\main\\resources\\jobs.json E:\\�о�������\\ѡ���㷨\\SatelliteScheduling\\src\\main\\resources\\satellites.json"});
//            pr.waitFor();
//        }
    }
}
