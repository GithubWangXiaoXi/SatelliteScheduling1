import algorithm.Result;
import algorithm.Solution;
import algorithm.TabuSearch;
import org.junit.Test;
import pojo.Job;
import pojo.Satellite;
import pojo.TimeWindow;
import util.PojoGenerator;
import util.Utils;

import java.util.*;

public class TabuSearchTest {

    @Test
    /***测试tabuSearch每次迭代过程*/
    public void getCandidatesSolutionTest(){

        String satellite_json = Utils.readJsonFile("src/main/resources/satellites_t1.json");
        List<Satellite> satellites = PojoGenerator.SatellitesGeneratorWithJson(satellite_json);

        String job_json = Utils.readJsonFile("src/main/resources/jobs_t1.json");
        List<Job> jobs = PojoGenerator.JobsGeneratorWithJson(job_json);


        jobs = Solution.permutateJobList(jobs);
        jobs = Solution.orderJobsByPriority(jobs);
        TabuSearch tabuSearch = new TabuSearch(jobs,satellite_json);
        List<Job> candidatesSolution = tabuSearch.getCandidatesSolution(jobs,satellites);
    }

    @Test
    /**测试tabuSearch*/
    public void tabuSearchWithEpochTest(){
        String satellite_json = Utils.readJsonFile("src/main/resources/satellites_t1.json");
        List<Satellite> satellites = PojoGenerator.SatellitesGeneratorWithJson(satellite_json);

        String job_json = Utils.readJsonFile("src/main/resources/jobs_t1.json");
        List<Job> jobs = PojoGenerator.JobsGeneratorWithJson(job_json);

        TabuSearch tabuSearch = new TabuSearch(jobs,satellite_json);
        List<Job> optimalSolution = tabuSearch.tabuSearchWithEpoch(50, jobs, satellites);

        for (Job job: optimalSolution) {
            System.out.println(job.getName() + "已完成，卫星编号：" + job.getSat_id()
                    + ", 时间窗口编号：" + job.getTW_id());
        }

        List<Result> results = new ArrayList<Result>();
        for (Job job: optimalSolution) {
            Result result = new Result();
            result.setSAT_id(job.getSat_id());
            result.setJob_id(job.getJob_Id());
            result.setTW_id(job.getTW_id());
            result.setJob_startT(job.getStartT());
            result.setJob_endT(job.getEndT());
            results.add(result);
            System.out.println(result);
        }

        String json = Utils.object2Json(optimalSolution);  //序列化成json
        Utils.writeJsonFile("src/main/resources/res1.json",json); //将json写入到json文件


//        System.out.print("任务id序列 = ");
//        for (Job job: optimalSolution) {
//            System.out.print(job.getJob_Id() + " ");
//        }
        //17 6 23 11 4 38 40 34 1 25 29 20 32 39 36
        //23 17 6 11 4 34 38 25 40 1 29 36 32 20 39
        //6 23 17 11 25 1 40 4 38 34 29 3 36 32 39
        //6 17 11 23 38 34 25 4 40 1 29 36 39 32 20
        //23 11 6 17 40 38 34 4 25 1 29 39 36 20 32
        //17 6 23 11 25 40 1 38 4 34 29 3 36 39 32
        //23 17 6 11 25 38 34 4 1 40 29 3 39 36 20 32
        //17 23 6 40 25 34 38 1 4 29 32 2 3 39 36
        //6 11 23 17 25 34 40 38 4 29 19 39 36 20 32
        //17 6 11 23 1 25 4 40 38 34 29 39 36 20 32

        //key = 卫星id, val = 时间窗口ids:任务ids
        Map<Integer,List<String>> satJobMap = new HashMap<Integer, List<String>>();
        System.out.println("卫星窗口分配情况");
        for (Job job: optimalSolution){

            List<String> twId_jobIdList = satJobMap.get(job.getSat_id());
            if(twId_jobIdList == null){
                twId_jobIdList = new ArrayList<String>();
            }
            String temp = job.getTW_id() + "-" + job.getJob_Id();
            twId_jobIdList.add(temp);
            satJobMap.put(job.getSat_id(),twId_jobIdList);
        }

        Set<Integer> sat_ids = satJobMap.keySet();
        for(Integer sat_id : sat_ids){
            System.out.print("卫星id = " + sat_id + " 时间窗口ids - 任务ids = {");

            int s = satJobMap.get(sat_id).size();
            int k = 1;
            for (String str : satJobMap.get(sat_id)){
                if(k == s){
                    System.out.print(str);
                }
                else{
                    System.out.print(str + ", ");
                }
                k++;
            }
            System.out.println("}");
        }
    }
}
