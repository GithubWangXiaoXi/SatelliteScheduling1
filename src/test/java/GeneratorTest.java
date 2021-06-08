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

/**��PojoGenerator�෽�����е��ԣ������� = Ҫ���Եķ��� + test**/
public class GeneratorTest {

    @Test
    public void locationGeneratorTest(){
        List<List<String>> locations = PojoGenerator.locationGeneratorWithRandomPlace(10);
        System.out.println(locations);
    }

    @Test
    public void timeWindowsGeneratorTest(){
        /**���ڸ�ʽYYYY-MM-dd HH:mm:ss**/
        List<TimeWindow> timeWindows = PojoGenerator.timeWindowsGeneratorWithRandomSize(Utils.str2Date("2021-05-26 00:00:00"),
                Utils.str2Date("2021-05-26 1:00:00"), 600, 400);
        for (TimeWindow timeWindow : timeWindows) {
            System.out.println(timeWindow);
            System.out.println();
        }
    }

    @Test
    /**���ɶ��jobs������Ϊlist�������л���json**/
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

        String json = Utils.object2Json(jobs);  //���л���json
        Utils.writeJsonFile("src/main/resources/jobs_t.json",json); //��jsonд�뵽json�ļ�
    }

    @Test
    /**���ɶ�����ǣ�����Ϊlist�������л���json**/
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

        String json = Utils.object2Json(satellites);  //���л���json
        Utils.writeJsonFile("src/main/resources/satellites1.json",json); //��jsonд�뵽json�ļ�
    }
}
