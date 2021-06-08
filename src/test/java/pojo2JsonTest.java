import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import pojo.Job;
import pojo.Satellite;
import pojo.TimeWindow;

import java.util.*;

import util.PojoGenerator;
import util.Utils;

public class pojo2JsonTest {

    @Test
    public void json2JobsTest(){
        //参考<https://blog.csdn.net/xuforeverlove/article/details/80842148>
//        String str = "{\"location\":[0.0,30.5,120.5],\"name\":\"job1\",\"priority\":1,\"procedures\":[{\"duration\":240,\"p_id\":1,\"startT\":\"2021-05-26 00:49:57\"}],\"resolution\":5,\"sensor\":1}\n";

        //解析list对象会报can not cast to JSONObject, 应该转成JSONArray对象，参考<https://www.cnblogs.com/hd92/p/13554557.html>
//        JSONObject jsonObject = JSON.parseObject(str);

//        Job job = JSON.parseObject(str, Job.class);

        //System.out.println(job);

        String str1 = "[{\"location\":[0.0,30.5,120.5],\"name\":\"job1\",\"priority\":1,\"procedures\":[{\"duration\":240,\"p_id\":1,\"startT\":\"2021-05-26 01:21:43\"}],\"resolution\":5,\"sensor\":1},{\"location\":[0.0,30.5,120.5],\"name\":\"job2\",\"priority\":1,\"procedures\":[{\"duration\":240,\"p_id\":1,\"startT\":\"2021-05-26 01:21:43\"}],\"resolution\":5,\"sensor\":1}]";

        List<Job> jobs = JSON.parseArray(str1, Job.class);

        System.out.println(jobs.get(0));
    }

    @Test
    public void satellite2JsonTest(){

        List<Satellite> satellites = new ArrayList<Satellite>();

        //卫星1
        ArrayList<String> location = new ArrayList<String>();
        location.add(new String("0.0"));
        location.add(new String("30.5"));
        location.add(new String("120.5"));

        Set<Integer> sensors = new HashSet<Integer>();
        sensors.add(1);
        sensors.add(2);

        List<TimeWindow> timeWindows = new ArrayList<TimeWindow>();
        TimeWindow timeWindow = new TimeWindow(1,location,Utils.str2Date("2018-3-2 10:10:00"),Utils.str2Date("2018-3-2 10:15:00"));
        timeWindows.add(timeWindow);

        Satellite satellite = new Satellite("sat1",sensors,5,60,timeWindows);
        satellites.add(satellite);

        //卫星2
        ArrayList<String> location1 = new ArrayList<String>();
        location1.add(new String("0.0"));
        location1.add(new String("33.5"));
        location1.add(new String("126.5"));

        Set<Integer> sensors1 = new HashSet<Integer>();
        sensors1.add(1);
        sensors1.add(2);

        List<TimeWindow> timeWindows1 = new ArrayList<TimeWindow>();
        TimeWindow timeWindow1 = new TimeWindow(1,location,Utils.str2Date("2018-3-2 10:10:00"),Utils.str2Date("2018-3-2 10:15:00"));
        timeWindows1.add(timeWindow1);

        Satellite satellite1 = new Satellite("sat2",sensors,5,60,timeWindows1);
        satellites.add(satellite1);

        //转化成jsonObject
        String json = JSON.toJSONString(satellites);//将java对象转换为json对象
        System.out.println("\n satellites json = " + json);
    }

    @Test
    public void json2Satellites(){
        String str1 = "[{\"mR\":5,\"mTI\":60,\"name\":\"sat1\",\"sensors\":[1,2],\"timeWindows\":[{\"endT\":\"2018-03-02 10:15:00\",\"location\":[0.0,30.5,120.5],\"startT\":\"2018-03-02 10:10:00\",\"t_id\":1}]},{\"mR\":5,\"mTI\":60,\"name\":\"sat2\",\"sensors\":[1,2],\"timeWindows\":[{\"endT\":\"2018-03-02 10:15:00\",\"location\":[0.0,30.5,120.5],\"startT\":\"2018-03-02 10:10:00\",\"t_id\":1}]}]";

        List<Satellite> satellites = JSON.parseArray(str1, Satellite.class);

        System.out.println(satellites.get(0));
    }

    @Test
    public void json2JobsByUtil(){
        String jsonStr = Utils.readJsonFile("src/main/resources/jobs.json");
        List<Job> jobs = PojoGenerator.JobsGeneratorWithJson(jsonStr);
        System.out.println(jobs);
    }

    @Test
    public void json2SatellitesByUtil(){
        String jsonStr = Utils.readJsonFile("src/main/resources/satellites.json");
        List<Satellite> satellites = PojoGenerator.SatellitesGeneratorWithJson(jsonStr);
        System.out.println(satellites);
    }
}
