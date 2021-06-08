package algorithm;

import pojo.Job;
import pojo.Satellite;
import pojo.TimeWindow;
import util.PojoGenerator;
import util.PojoUtils;
import java.math.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TabuSearch {

    private int epoch; //TS迭代次数
    private List<Job> jobsSolution;  //Jobs每次迭代的解
    private Map<Job,Map<TimeWindow,Integer>> tabuMaps = new ConcurrentHashMap<Job, Map<TimeWindow, Integer>>();  //每个job都有自己的禁忌表
    private Map<Integer,Job> jobsMap = new HashMap<Integer, Job>();  //通过索引来获取job
    private Map<List<Job>,Integer> jobsCandidateSolution = new HashMap<List<Job>,Integer>();  //用来记录每一轮候选集中解的情况
    private List<Job> optimalSolution = new ArrayList<Job>(); //保存当前最优解
    private int MAX_CompletedNum = 0;  //任务最大完成个数
    private String satellite_json = "";  //保存Satellites原有信息

    /**初始化jobsMap*/
    public TabuSearch(List<Job> jobs, String json){

        for (Job job : jobs) {
            int index = job.getJob_Id() - 1;
            jobsMap.put(index,job);
        }

        this.satellite_json = json;
    }

    /**Step1：得到初始解*/
    public void initialSolution(List<Job> jobs){
        jobsSolution = Solution.permutateJobList(jobs);
        jobsSolution = Solution.orderJobsByPriority(jobsSolution);

    }

    /**Step2: 初始化禁忌表*/
    public void initialTabuMap(List<Job> jobs){
        for (Job job : jobs) {
            tabuMaps.put(job,new ConcurrentHashMap<TimeWindow, Integer>());
        }
    }

    /**
     * Step3: 邻域搜索产生候选解集,并对候选解中所有的情况进行约束判断，破禁规则判断，更新tabuMaps
     * @return: 当前状态的最优解：满足破禁规则的解，如果没有则满足禁忌表中的解。
     */
    public List<Job> getCandidatesSolution(List<Job> jobs,List<Satellite> satellites){

        //得到邻域解
        List<List<Integer>> candidatesIndex = PojoUtils.getCandidatesFromAreaSearch(jobs);

        //为侯选集封装jobs
        List<List<Job>> jobsCandidates = new ArrayList<List<Job>>();

        int k = 0;
        for (List<Integer> indexList : candidatesIndex) {
            jobsCandidates.add(new ArrayList<Job>());

            for (Integer index : indexList) {
                jobsCandidates.get(k).add(jobsMap.get(index));
            }
            k++;
        }

        //判断侯选集中是否满足约束条件，以及破禁规则：即当前序列的解是否优于之间得到的解
        for (List<Job> jobList : jobsCandidates){

            try {

                /**该方法修改了jobs的start_random，end_random，isUsed属性，satellites的jobs,startT,endT属性*/
                //在每次寻找可用窗口时，对于每一个任务序列，需要重新初始化时间窗口
                satellites = PojoGenerator.SatellitesGeneratorWithJson(satellite_json);
                List<Job> temp = Solution.getJobsSolution(jobList, satellites, tabuMaps,false);

                //如果满足破禁规则，则更新禁忌表（禁忌对象为某任务不使用的时间窗口）
                if(temp.size() > MAX_CompletedNum){
                    MAX_CompletedNum  = temp.size();
                    optimalSolution = temp;

                    //为每个job更新禁忌表
                    //在每次寻找可用窗口时，对于每一个任务序列，需要重新初始化时间窗口
                    satellites = PojoGenerator.SatellitesGeneratorWithJson(satellite_json);
                    updateTabuMap(temp,satellites);

                    //将当前的最优解作为下一次迭代的初始解
                    jobsSolution = optimalSolution;
                    return jobsSolution;

                }
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }

//        System.out.println("任务完成个数为 = " + MAX_CompletedNum);
//        System.out.println("任务最优排序为 = " + optimalSolution.get(MAX_CompletedNum));

        //判断候选解集是否对应对象的禁忌情况，从其中选择满足条件的最优解
        Map<Integer,List<Job>> optimalTabuSolution = new HashMap<Integer,List<Job>>();  //保存最优的禁忌解
        int MAX_TABUSOLUTION = 0;
        for (List<Job> jobList : jobsCandidates){

            try {

//                System.out.println("开始使用禁忌表");
//                Set<Job> jobs1 = tabuMaps.keySet();
//                for (Job job : jobs1){
//                    System.out.println("key = " + job.getJob_Id() + " val = " + tabuMaps.get(job));
//                }
//                System.out.println("--------------------------");

                //在每次寻找可用窗口时，对于每一个任务序列，需要重新初始化时间窗口
                satellites = PojoGenerator.SatellitesGeneratorWithJson(satellite_json);
                List<Job> temp  = Solution.getJobsSolution(jobList, satellites, tabuMaps, true);

//                System.out.println("------------------------------");

                //保存满足禁忌要求的最优解
                if(temp.size() > MAX_CompletedNum){
                    MAX_TABUSOLUTION  = temp.size();
                    optimalTabuSolution.put(MAX_TABUSOLUTION,temp);

                    //将当前的最优解作为下一次迭代的初始解
                    jobsSolution = temp;

                    //更新tabuMap
                    tabuMapAutoDecrease();

                    return jobsSolution;
                }

            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**更新tabuMap*/
    public void updateTabuMap(List<Job> jobs,List<Satellite> satellites){

        for (Job job:jobs) {

            //理论上job可用的时间窗口
            List<TimeWindow> timeWindows = Solution.getTimeWindowsMeetingJob(job,satellites);

            //得到job和timeWindow对应表（job已分配）
            Map<Job, TimeWindow> jobTimeWindowUsedMap = Solution.getJobTimeWindowUsedMap();
            TimeWindow tw = jobTimeWindowUsedMap.get(job);

            //timeWindows中除了tw，其他窗口对象都写入到job的禁忌表中
            for (TimeWindow t:timeWindows) {

//                System.out.println("更新tabuMap");
                //根据(7-优先级)作为禁忌长度，优先级越高，禁忌长度越短
                if(t != tw){
                    Map<TimeWindow, Integer> map = tabuMaps.get(job);
                    map.put(t,(int)Math.floor((float)(7-job.getPriority())*1.5));
                    tabuMaps.put(job,map);
                }
            }
        }
    }

    /**
     * 通过设置迭代次数，进行tabuSearch
     * this.satellite_json: 由于每次迭代会修改卫星时间窗口的startT，endT，因此下一次迭代之前先通过json字符串生成新的卫星数据
     */
    public List<Job> tabuSearchWithEpoch(int epoch, List<Job> jobs,List<Satellite> satellites){

        initialSolution(jobs);  //初始化解（任务按优先级排序）
        initialTabuMap(jobs); //初始化禁忌表

        for(int i = 0; i < epoch; ++i){

//            satellites = PojoGenerator.SatellitesGeneratorWithJson(satellite_json);  //生成新的satellite
            List<Job> candidatesSolution = getCandidatesSolution(jobs, satellites);
            System.out.println("任务完成个数为 = " + MAX_CompletedNum);

            //每次迭代首先清理掉satellites上的TimeWindow占用的jobs资源,将所有任务的used置为false
            PojoUtils.setJobsUsedFalse(jobs);
        }

        return optimalSolution;
    }

    /**每经过一次迭代，禁忌表中的禁忌对象对应的禁忌长度都会递减1*/
    public void tabuMapAutoDecrease(){

//        System.out.println("递减tabuMap----");
        Set<Job> jobs = tabuMaps.keySet();

        for (Job job : jobs){

            Map<TimeWindow,Integer> map_temp = tabuMaps.get(job);
            Set<TimeWindow> timeWindows = map_temp.keySet();

            for(TimeWindow t : timeWindows){

                map_temp.put(t,map_temp.get(t)-1);

                //如果禁忌长度为0，则清除该timeWindow
                Iterator<TimeWindow> iter = map_temp.keySet().iterator();
                while(iter.hasNext()){
                    if(map_temp.get(iter.next()) == 0){
                        iter.remove();
                    }
                }
            }
        }
    }
}
