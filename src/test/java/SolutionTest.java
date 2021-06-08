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

/**对Initialization类方法进行调试: 方法名 = 要调试的方法 + test**/
public class SolutionTest {

    @Test
    /**测试初始化Step1: 随机重排任务**/
    public void permutateJobListTest(){
        String job_json = Utils.readJsonFile("src/main/resources/jobs_t.json");
        List<Job> jobs = PojoGenerator.JobsGeneratorWithJson(job_json);

        jobs = Solution.permutateJobList(jobs);

        for (Job job:jobs) {
            System.out.println(job + "\n");
        }
    }

    @Test
    /**测试初始化Step2: 任务优先级排序**/
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
    /**测试初始化Step3: 判断是否存在时间窗（有可能使用一部分了）在任务可移动范围之内（满足多个约束）**/
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
    /**判断两个任务是否冲突，如果冲突，怎么处理*/
    public void handleTwoJobsCollisionInSameTimeWindowTest() throws CloneNotSupportedException {
        String satellite_json = Utils.readJsonFile("src/main/resources/satellites_t.json");
        List<Satellite> satellites = PojoGenerator.SatellitesGeneratorWithJson(satellite_json);

        String job_json = Utils.readJsonFile("src/main/resources/jobs_t.json");
        List<Job> jobs = PojoGenerator.JobsGeneratorWithJson(job_json);

//        List<TimeWindow> timeWindows = Solution.getTimeWindowsMeetingJob(jobs.get(0), satellites);

        //遍历所有job，对Initialization.job_timeWindowMap,timeWindow_jobMap进行填充
        Job job_temp = null;
        Job job1_temp = null;
        Job job2_temp = null;
        TimeWindow tw = null;
        for (Job job : jobs) {
            Solution.getTimeWindowsMeetingJob(job, satellites);
            if(job.getName().equals("Job39")){
                job_temp = job;
                job1_temp = job;  //为了后面验证job1，job2是否冲突
            }else if(job.getName().equals("Job23")){
                job2_temp = job;  //为了后面验证job1，job2是否冲突
            }
        }

        Map<Job, List<TimeWindow>> job_timeWindowMap = Solution.getJob_timeWindowMap();
        List<TimeWindow> timeWindows = job_timeWindowMap.get(job_temp);  //以Job39为例反向搜索TimeWindows

        int k = 1;
        for (TimeWindow timeWindow : timeWindows) {
            System.out.println(k + ": " + timeWindow + "\n");

            Map<TimeWindow, List<Job>> timeWindow_jobMap = Solution.getTimeWindow_jobMap();

            jobs = timeWindow_jobMap.get(timeWindow);

            if(timeWindow.getSat_id() == 4){
                tw = timeWindow; //为了后面验证job1，job2是否冲突
            }

            for (Job job : jobs){
                System.out.println("----" + job + "\n");
            }

            k++;
        }

        //处理冲突
        List<Job> jobs_res = Solution.handleTwoJobsCollisionInSameTimeWindow(satellites, tw, job1_temp, job2_temp);
        System.out.println("timewindow = " + tw);
        System.out.println("job1 = " + job1_temp);
        System.out.println("job2 = " + job2_temp);
        System.out.println("冲突解决方案：" + jobs_res);
    }

    @Test
    /**当任务分配给第i个时间窗口时，更新第i个，或第i+1个窗口的startT，endT**/
    public void updateTimeWindowStateWithAllocatedJobTest(){

        String satellite_json = Utils.readJsonFile("src/main/resources/satellites_t1.json");
        List<Satellite> satellites = PojoGenerator.SatellitesGeneratorWithJson(satellite_json);

        String job_json = Utils.readJsonFile("src/main/resources/jobs_t1.json");
        List<Job> jobs = PojoGenerator.JobsGeneratorWithJson(job_json);

        //测试对象：job4，job25，satellite4第2个时间窗口
        Job job4 = jobs.get(3);
        Job job25 = jobs.get(24);
        TimeWindow timeWindow = satellites.get(3).getTimeWindows().get(1);
        TimeWindow timeWindow_next = satellites.get(3).getTimeWindows().get(2);

        System.out.println("job4 = " + job4 + "\n");
        System.out.println("job25 = " + job25 + "\n");
        System.out.println("timeWindow = " + timeWindow + "\n");
        System.out.println("next timeWindow = " + timeWindow_next + "\n");

//        System.out.println("----------------------timewindow单分配给job4（更新i，i+1窗口）---------------------------------------");
//        Solution.updateTimeWindowStateWithAllocatedJob(satellites,job4,timeWindow);
//        System.out.println("update timeWindow = " + timeWindow + "\n");
//        System.out.println("update next timeWindow = " + timeWindow_next + "\n");

        System.out.println("----------------------timewindow单分配给job25（更新i+1窗口）---------------------------------------");
        //修改MTI
        satellites.get(timeWindow.getSat_id() - 1).setMTI(150);
        Solution.updateTimeWindowStateWithAllocatedJob(satellites,job25,timeWindow);
        System.out.println("update timeWindow = " + timeWindow + "\n");
        System.out.println("update next timeWindow = " + timeWindow_next + "\n");
    }

    @Test
    /**得到初始解*/
    public void getTimeWindowsSolutionTest() throws CloneNotSupportedException {

        String satellite_json = Utils.readJsonFile("src/main/resources/satellites_t1.json");
        List<Satellite> satellites = PojoGenerator.SatellitesGeneratorWithJson(satellite_json);

        String job_json = Utils.readJsonFile("src/main/resources/jobs_t1.json");
        List<Job> jobs = PojoGenerator.JobsGeneratorWithJson(job_json);

        List<Job> jobsSolution = Solution.getJobsSolution(jobs, satellites);
        System.out.println(jobsSolution.size());
        for (Job job: jobsSolution) {
            System.out.println(job.getName() + "已完成，卫星编号：" + job.getSat_id()
                    + ", 时间窗口编号：" + job.getTW_id());
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

        String json = Utils.object2Json(jobsSolution);  //序列化成json
        Utils.writeJsonFile("src/main/resources/res1.json",json); //将json写入到json文件
    }
}
