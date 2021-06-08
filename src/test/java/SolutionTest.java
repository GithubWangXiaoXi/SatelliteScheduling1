import algorithm.Result;
import algorithm.Solution;
import org.junit.Test;
import pojo.Job;
import pojo.Satellite;
import pojo.TimeWindow;
import util.PojoGenerator;
import util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**��Initialization�෽�����е���: ������ = Ҫ���Եķ��� + test**/
public class SolutionTest {

    @Test
    /**���Գ�ʼ��Step1: �����������**/
    public void permutateJobListTest(){
        String job_json = Utils.readJsonFile("src/main/resources/jobs_t.json");
        List<Job> jobs = PojoGenerator.JobsGeneratorWithJson(job_json);

        jobs = Solution.permutateJobList(jobs);

        for (Job job:jobs) {
            System.out.println(job + "\n");
        }
    }

    @Test
    /**���Գ�ʼ��Step2: �������ȼ�����**/
    public void orderJobsByPriorityTest(){

        String job_json = Utils.readJsonFile("src/main/resources/jobs_t1.json");
        List<Job> jobs = PojoGenerator.JobsGeneratorWithJson(job_json);

        jobs = Solution.permutateJobList(jobs);
        jobs = Solution.orderJobsByPriority(jobs);
        for (Job job:jobs) {
            System.out.println(job + "\n");
        }
    }

    @Test
    /**���Գ�ʼ��Step3: �ж��Ƿ����ʱ�䴰���п���ʹ��һ�����ˣ���������ƶ���Χ֮�ڣ�������Լ����**/
    public void getTimeWindowsMeetingJobTest(){
        String satellite_json = Utils.readJsonFile("src/main/resources/satellites_t.json");
        List<Satellite> satellites = PojoGenerator.SatellitesGeneratorWithJson(satellite_json);

        String job_json = Utils.readJsonFile("src/main/resources/jobs_t.json");
        List<Job> jobs = PojoGenerator.JobsGeneratorWithJson(job_json);

        List<TimeWindow> timeWindows = Solution.getTimeWindowsMeetingJob(jobs.get(0), satellites);

        System.out.println("job = " + jobs.get(0) + "\n");
        int k = 0;
        for (TimeWindow timeWindow : timeWindows) {
            System.out.println(k + ": " + timeWindow + "\n");
            k++;
        }
    }

    @Test
    /**�ж����������Ƿ��ͻ�������ͻ����ô����*/
    public void handleTwoJobsCollisionInSameTimeWindowTest() throws CloneNotSupportedException {
        String satellite_json = Utils.readJsonFile("src/main/resources/satellites_t.json");
        List<Satellite> satellites = PojoGenerator.SatellitesGeneratorWithJson(satellite_json);

        String job_json = Utils.readJsonFile("src/main/resources/jobs_t.json");
        List<Job> jobs = PojoGenerator.JobsGeneratorWithJson(job_json);

//        List<TimeWindow> timeWindows = Solution.getTimeWindowsMeetingJob(jobs.get(0), satellites);

        //��������job����Initialization.job_timeWindowMap,timeWindow_jobMap�������
        Job job_temp = null;
        Job job1_temp = null;
        Job job2_temp = null;
        TimeWindow tw = null;
        for (Job job : jobs) {
            Solution.getTimeWindowsMeetingJob(job, satellites);
            if(job.getName().equals("Job39")){
                job_temp = job;
                job1_temp = job;  //Ϊ�˺�����֤job1��job2�Ƿ��ͻ
            }else if(job.getName().equals("Job23")){
                job2_temp = job;  //Ϊ�˺�����֤job1��job2�Ƿ��ͻ
            }
        }

        Map<Job, List<TimeWindow>> job_timeWindowMap = Solution.getJob_timeWindowMap();
        List<TimeWindow> timeWindows = job_timeWindowMap.get(job_temp);  //��Job39Ϊ����������TimeWindows

        int k = 1;
        for (TimeWindow timeWindow : timeWindows) {
            System.out.println(k + ": " + timeWindow + "\n");

            Map<TimeWindow, List<Job>> timeWindow_jobMap = Solution.getTimeWindow_jobMap();

            jobs = timeWindow_jobMap.get(timeWindow);

            if(timeWindow.getSat_id() == 4){
                tw = timeWindow; //Ϊ�˺�����֤job1��job2�Ƿ��ͻ
            }

            for (Job job : jobs){
                System.out.println("----" + job + "\n");
            }

            k++;
        }

        //�����ͻ
        List<Job> jobs_res = Solution.handleTwoJobsCollisionInSameTimeWindow(satellites, tw, job1_temp, job2_temp);
        System.out.println("timewindow = " + tw);
        System.out.println("job1 = " + job1_temp);
        System.out.println("job2 = " + job2_temp);
        System.out.println("��ͻ���������" + jobs_res);
    }

    @Test
    /**������������i��ʱ�䴰��ʱ�����µ�i�������i+1�����ڵ�startT��endT**/
    public void updateTimeWindowStateWithAllocatedJobTest(){

        String satellite_json = Utils.readJsonFile("src/main/resources/satellites_t1.json");
        List<Satellite> satellites = PojoGenerator.SatellitesGeneratorWithJson(satellite_json);

        String job_json = Utils.readJsonFile("src/main/resources/jobs_t1.json");
        List<Job> jobs = PojoGenerator.JobsGeneratorWithJson(job_json);

        //���Զ���job4��job25��satellite4��2��ʱ�䴰��
        Job job4 = jobs.get(3);
        Job job25 = jobs.get(24);
        TimeWindow timeWindow = satellites.get(3).getTimeWindows().get(1);
        TimeWindow timeWindow_next = satellites.get(3).getTimeWindows().get(2);

        System.out.println("job4 = " + job4 + "\n");
        System.out.println("job25 = " + job25 + "\n");
        System.out.println("timeWindow = " + timeWindow + "\n");
        System.out.println("next timeWindow = " + timeWindow_next + "\n");

//        System.out.println("----------------------timewindow�������job4������i��i+1���ڣ�---------------------------------------");
//        Solution.updateTimeWindowStateWithAllocatedJob(satellites,job4,timeWindow);
//        System.out.println("update timeWindow = " + timeWindow + "\n");
//        System.out.println("update next timeWindow = " + timeWindow_next + "\n");

        System.out.println("----------------------timewindow�������job25������i+1���ڣ�---------------------------------------");
        //�޸�MTI
        satellites.get(timeWindow.getSat_id() - 1).setMTI(150);
        Solution.updateTimeWindowStateWithAllocatedJob(satellites,job25,timeWindow);
        System.out.println("update timeWindow = " + timeWindow + "\n");
        System.out.println("update next timeWindow = " + timeWindow_next + "\n");
    }

    @Test
    /**�õ���ʼ��*/
    public void getTimeWindowsSolutionTest() throws CloneNotSupportedException {

        String satellite_json = Utils.readJsonFile("src/main/resources/satellites_t1.json");
        List<Satellite> satellites = PojoGenerator.SatellitesGeneratorWithJson(satellite_json);

        String job_json = Utils.readJsonFile("src/main/resources/jobs_t1.json");
        List<Job> jobs = PojoGenerator.JobsGeneratorWithJson(job_json);

        List<Job> jobsSolution = Solution.getJobsSolution(jobs, satellites);
        System.out.println(jobsSolution.size());
        for (Job job: jobsSolution) {
            System.out.println(job.getName() + "����ɣ����Ǳ�ţ�" + job.getSat_id()
                    + ", ʱ�䴰�ڱ�ţ�" + job.getTW_id());
        }

        List<Result> results = new ArrayList<Result>();
        for (Job job: jobsSolution) {
            Result result = new Result();
            result.setSAT_id(job.getSat_id());
            result.setJob_id(job.getJob_Id());
            result.setTW_id(job.getTW_id());
            result.setJob_startT(job.getStartT());
            result.setJob_endT(job.getEndT());
            results.add(result);
            System.out.println(result);
        }

        String json = Utils.object2Json(jobsSolution);  //���л���json
        Utils.writeJsonFile("src/main/resources/res1.json",json); //��jsonд�뵽json�ļ�
    }
}
