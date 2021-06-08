import org.junit.Test;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import pojo.Job;
import pojo.Satellite;
import pojo.TimeWindow;
import util.*;

/**对PojoGenerator类方法进行调试：方法名 = 要调试的方法 + test**/
public class GeneratorTest {

    @Test
    public void locationGeneratorTest(){
        List<List<String>> locations = PojoGenerator.locationGeneratorWithRandomPlace(10);
        System.out.println(locations);
    }

    @Test
    public void timeWindowsGeneratorTest(){
        /**日期格式YYYY-MM-dd HH:mm:ss**/
        List<TimeWindow> timeWindows = PojoGenerator.timeWindowsGeneratorWithRandomSize(Utils.str2Date("2021-05-26 00:00:00"),
                Utils.str2Date("2021-05-26 1:00:00"), 600, 400);
        for (TimeWindow timeWindow : timeWindows) {
            System.out.println(timeWindow);
            System.out.println();
        }
    }

    @Test
    /**生成多个jobs，保存为list，并序列化成json**/
    public void jobGeneratorTest(){

        List<Job> jobs = new ArrayList<Job>();

        Random r = new Random();
        List<List<String>> locations = PojoGenerator.locationGeneratorWithRandomPlace(3);

        for (int i = 1; i <= 10; i++) {

            int index = r.nextInt(3);
            Job job = PojoGenerator.jobGenerator(i,"Job" + i,locations.get(index),Utils.str2Date("2021-05-26 00:00:00"),
                    Utils.str2Date("2021-05-26 1:00:00"), 400, 150);
            jobs.add(job);
            System.out.println(job + "\n");
        }

        String json = Utils.object2Json(jobs);  //序列化成json
        Utils.writeJsonFile("src/main/resources/jobs_t.json",json); //将json写入到json文件
    }

    @Test
    /**生成多个卫星，保存为list，并序列化成json**/
    public void SatelliteGeneratorTest(){

        List<Satellite> satellites = new ArrayList<Satellite>();

        Random r = new Random();
        List<List<String>> locations = PojoGenerator.locationGeneratorWithRandomPlace(3);

        for (int i = 1; i <= 4; i++) {
            List<TimeWindow> timeWindows = PojoGenerator.timeWindowsGeneratorWithRandomSize(Utils.str2Date("2021-05-26 00:00:00"),
                    Utils.str2Date("2021-05-26 1:00:00"), 500, 200);
            Satellite satellite = PojoGenerator.satelliteGenerator(i,"satellite" + i, timeWindows, locations);
            satellites.add(satellite);
            System.out.println(satellite + "\n");
        }

        String json = Utils.object2Json(satellites);  //序列化成json
        Utils.writeJsonFile("src/main/resources/satellites1.json",json); //将json写入到json文件
    }
}
