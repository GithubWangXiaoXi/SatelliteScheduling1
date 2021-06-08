package algorithm;

import pojo.Job;
import pojo.Satellite;
import pojo.TimeWindow;
import util.Utils;
import util.PojoUtils;

import java.util.*;

/**
 * 在满足一定约束下进行任务分配
 */
public class Solution {

    /**
     * 任务Job与可用时间窗口timeWindow的1对多的对应关系，用map来存储
     * @Note: job(1) - timeWindow(many), 表示该job理论上可以分配到这些timeWindow上
     */
    private static Map<Job,List<TimeWindow>> job_timeWindowMap = new HashMap<Job, List<TimeWindow>>();

    /**
     * 可用时间窗口timeWindow与任务Job的1对多的对应关系，用map来存储
     * @Note: timeWindow(1) - job(many), 表示该timeWindow理论上可以分配给这些job上
     */
    private static Map<TimeWindow,List<Job>> timeWindow_jobMap = new HashMap<TimeWindow, List<Job>>();

    private static Map<Job,TimeWindow> jobTimeWindowUsedMap = new HashMap<Job, TimeWindow>();

    public static Map<Job, List<TimeWindow>> getJob_timeWindowMap() {
        return job_timeWindowMap;
    }

    public static void setJob_timeWindowMap(Map<Job, List<TimeWindow>> job_timeWindowMap) {
        Solution.job_timeWindowMap = job_timeWindowMap;
    }

    public static Map<TimeWindow, List<Job>> getTimeWindow_jobMap() {
        return timeWindow_jobMap;
    }

    public static void setTimeWindow_jobMap(Map<TimeWindow, List<Job>> timeWindow_jobMap) {
        Solution.timeWindow_jobMap = timeWindow_jobMap;
    }

    public static Map<Job, TimeWindow> getJobTimeWindowUsedMap() {
        return jobTimeWindowUsedMap;
    }

    public static void setJobTimeWindowUsedMap(Map<Job, TimeWindow> jobTimeWindowUsedMap) {
        Solution.jobTimeWindowUsedMap = jobTimeWindowUsedMap;
    }

    /**
     * Step1: 对所有的任务进行随机排列，产生一个序列
     */
    public static List<Job> permutateJobList(List<Job> jobs){
        return PojoUtils.shuffleJobList(jobs);
    }

    /**
     * Step2: 先对所有任务进行优先级排序
     * @Constraint1: priority
     */
    public static List<Job> orderJobsByPriority(List<Job> jobs){
        jobs = PojoUtils.orderJobListByPriority(jobs);
        return jobs;
    }

    /**
     * Step3: 按顺序依次将序列中的任务放到可行的时间窗口中，在放入的时候考虑冲突1。
     * @Goal: 处理任务与时间窗的冲突
     */

    /**
     * Step3_1: 判断是否存在时间窗（有可能使用一部分了）在任务可移动范围之内
     * @Constraint1: J_window 相交 TW >= J_duration
     */
    public static List<TimeWindow> getTimeWindowsMeetingJob(Job job,List<Satellite> satellites){

        Date job_startT = job.getStartT();
        Date job_endT = job.getEndT();

        List<TimeWindow> timeWindows = new ArrayList<TimeWindow>();
        for (Satellite satellite : satellites) {

            for (TimeWindow timeWindow : satellite.getTimeWindows()) {
                Date TW_startT = timeWindow.getStartT();
                Date TW_endT = timeWindow.getEndT();

                int isIntersection = PojoUtils.isIntersection(TW_startT,TW_endT,job_startT,job_endT);
//                System.out.println("diff: " + Utils.getSecondsByTimeDifference(TW_startT,TW_endT) + " duration: " + job.getDuration());

                //如果满足时间constraint，则该任务理论上可以分配到该卫星窗口上
                if(isIntersection > job.getDuration()){

                    boolean ret = true;
                    ret = ret && isMeetingLocation(timeWindow,job);
                    ret = ret && isMeetingResolutionAndSensor(satellites,timeWindow,job);
                    if(ret){

                        /**@UpdateStable  设置每个窗口的任务执行度*/
                        Integer diff = Utils.getSecondsByTimeDifference(TW_startT,TW_endT);
                        Float taskPerformance = (float)job.getDuration() / diff;
                        timeWindow.setTaskPerformance(taskPerformance);

                        timeWindows.add(timeWindow);

                        //建立job，timewindow的多对1关系（由于timewindow中startT已更改，需要重新插入jobs）
                        List<Job> jobs = new ArrayList<Job>();

                        jobs.add(job);
                        timeWindow_jobMap.put(timeWindow,jobs);
                    }
//                    System.out.println("TW_startT = " + TW_startT + " TW_endT = " + TW_endT);
//                    System.out.println("job_startT = " + job_startT + " job_endT = " + job_endT + "\n");
                }
            }
        }
        /**@updateStable 对timeWindows按照任务执行度（任务需要的时间在时间窗口的占比）进行排序*/
        timeWindows = PojoUtils.orderTimeWindowsByPriority(timeWindows);

        //建立job，timewindow的1对多关系
        job_timeWindowMap.put(job,timeWindows);
        return timeWindows;
    }

    /**
     * Step 3_2: 任务的地址和时间窗口的地址是否一致。
     * constraint2：Job_location == timeWindow_location
     */
    public static boolean isMeetingLocation(TimeWindow timeWindow,Job job){
        return PojoUtils.isLocationSame(timeWindow.getLocation(),job.getLocation());
    }

    /**
     * Step 3_3: 任务的传感器类型是否在时间窗口的传感器集合中。
     * Step 3_4: 任务的分辨率是否大于时间窗的最小分辨率。
     * constraint3：Job_sensor in SAT_sensors
     * constraint4: J_resolution > SAT_resolution
     */
    public static boolean isMeetingResolutionAndSensor(List<Satellite> satellites,TimeWindow timeWindow,Job job){
        Satellite satellite = satellites.get(timeWindow.getSat_id() - 1);
        boolean ret = PojoUtils.isMatchingSensor(job.getSensor(),satellite.getSensors());
        ret = ret && PojoUtils.isMatchingResolution(job.getResolution(),satellite.getMR());
        return ret;
    }

    /**
     * @deprecated
     * Step4: 按顺序依次将序列中的任务放到可行的时间窗口中，在放入的时候考虑冲突2。
     * @Goal: 以单个时间窗为准，处理两个任务之间的冲突（争抢）
     * @return: 返回合适的job
     */
     public static List<Job> handleTwoJobsCollisionInSameTimeWindow(List<Satellite> satellites, TimeWindow timeWindow,Job job1,Job job2) throws CloneNotSupportedException {

         //判断job1，job2是否可以分配到该窗口
         List<Job> jobs = timeWindow_jobMap.get(timeWindow);
         if(!(jobs.contains(job1) && jobs.contains(job2))){
             System.out.println("job1和job2不存在冲突");
             return null;
         }

         //如果两个任务有一个已经被其他卫星消费，则直接将没被消费的任务发送给该卫星窗口
         if(job1.getUsed() && !job2.getUsed()){
             jobs.add(job2);
             return jobs;
         }else if(!job1.getUsed() && job2.getUsed()){
             jobs.add(job1);
             return jobs;
         }

         //如果冲突，分配优先级高的job
         List<Job> jobs_temp = new ArrayList<Job>();
         if(isJobsCollision(satellites,timeWindow,job1,job2)){

             jobs_temp.add(job1.getPriority() >= job2.getPriority() ? job1 : job2);
             return jobs_temp;

         }else{
             jobs_temp.add(job1);
             jobs_temp.add(job2);
             return jobs_temp;
         }
     }

     /**
      * @deprecated
      * Step4_1: 判断两个任务是否存在冲突
      * @Constraint：
      * 1) job1, job2都有资格放入该窗口（handleTwoJobsCollisionInSameTimeWindow已判断）
      *
      * timeWindow用右图表示：|Job1|MTI|-|Job2|（下标分别为a,b,c,d,e)
      * 2) c <= d，则不冲突
      * 3) c > d，冲突，按优先级选择一个job
      */
     public static boolean isJobsCollision(List<Satellite> satellites, TimeWindow timeWindow,Job job1,Job job2) throws CloneNotSupportedException {

         Date TW_startT = timeWindow.getStartT();
         Date TW_endT = timeWindow.getEndT();
         Date job1_startT = job1.getStartT();
         Date job1_endT = job1.getEndT();
         Date job2_startT = job2.getStartT();
         Date job2_endT = job2.getEndT();

         //获取job1相交类型
         String job1_intersectionType = PojoUtils.getIntersectionType(TW_startT,TW_endT,job1_startT,job1_endT);
         //获取job2相交类型
         String job2_intersectionType = PojoUtils.getIntersectionType(TW_startT,TW_endT,job2_startT,job2_endT);

         //clone两个timeWindows进行冲突模拟
         TimeWindow tw_clone1 = timeWindow.clone();
         TimeWindow tw_clone2 = timeWindow.clone();

         /**对于job1,更新tw_clone1的时间*/
         //左交 || 包含，则job1放在最左
         if(job1_intersectionType.equals('l') || job1_intersectionType.equals('m')){

             Date tw_endT = Utils.getEndT(TW_startT,job1.getDuration());
             tw_clone1.setEndT(Utils.getEndT(tw_endT,satellites.get(timeWindow.getSat_id()).getMTI())); //分配给该job1后，加上MTI
         }
         //右交，则job1放在最右
         else if(job1_intersectionType.equals('r') ){
             Date tw_startT = Utils.getStartT(TW_endT,job1.getDuration());
             tw_clone1.setStartT(tw_startT);
         }

         /**对于job2,更新tw_clone2的时间*/
         //左交，则job2放在最左
         if(job2_intersectionType.equals('l')){
             Date tw_endT = Utils.getEndT(TW_startT,job2.getDuration());
             tw_clone2.setEndT(Utils.getEndT(tw_endT,satellites.get(timeWindow.getSat_id()).getMTI())); //分配给该job2后，加上MTI
         }
         //右交 || 包含，则job2放在最右
         else if(job2_intersectionType.equals('r') || job2_intersectionType.equals('m')){
             Date tw_startT = Utils.getStartT(TW_endT,job2.getDuration());
             tw_clone2.setStartT(tw_startT);
         }

         /**判断是否tw_clone1.endT <= tw_clone2.startT || tw_clone2.endT <= tw_clone1.startT, 如果是，则不冲突*/
         if(tw_clone1.getEndT().compareTo(tw_clone2.getStartT()) <=  0
                 || tw_clone2.getEndT().compareTo(tw_clone1.getStartT()) <= 0){
             return false;
         }
         return true;
     }

     /**
      * Step4_2: 当任务分配给第i个时间窗口时，更新第i个，或第i+1个窗口的startT，endT
      * 1) 更新MTI后，第i个窗口仍然有剩余，更新startT
      * 2) 更新MTI时，第i个窗口已用完，需要更新第i+1个窗口的startT'
      * @param satellites: 为了获取下一个时间窗的信息
      * @param job: 当前要分配的任务
      * @param timeWindow：当前要更新的
      */
     public static void updateTimeWindowStateWithAllocatedJob(List<Satellite> satellites, Job job,TimeWindow timeWindow){

         List<Job> jobs_completed = timeWindow.getJobs_completed();
         if(jobs_completed == null){
             jobs_completed = new ArrayList<Job>();
         }

         //判断该窗口给job之后 + MTI，是否有剩余（即startT是否小于endT）
         Date startT = timeWindow.getStartT();
         Satellite satellite = satellites.get(timeWindow.getSat_id() - 1);
         int duration = job.getDuration();
         int MTI = satellite.getMTI();  //注意这里的Sat_id从1开始

         Date startT_updated = Utils.getEndT(startT,duration + MTI);
         timeWindow.setStartT(startT_updated);

//         System.out.println("startT_update = " + Utils.date2Str(startT_updated) + " endT = " + Utils.date2Str(timeWindow.getEndT()));

         /**startT >= endT, 第i个时间窗已用完, 更新第 i + 1个时间窗信息*/
         if(startT_updated.compareTo(timeWindow.getEndT()) >= 0){
            int index = timeWindow.getT_id();

            //如果当前窗口是最后一个窗口，则不操作
            if(!(index == satellite.getTimeWindows().size())){
                TimeWindow next_tw = satellite.getTimeWindows().get((index - 1) + 1); //下一个时间窗
//                int offset = MTI + duration - (Utils.getSecondsByTimeDifference(startT, timeWindow.getEndT()));
//                next_tw.setStartT(Utils.getEndT(next_tw.getStartT(),offset));  //通过偏移量offset来更新下一个时间窗startT
                next_tw.setStartT(Utils.Dateclone(startT_updated));
            }
         }
         //startT < endT, 第i个时间窗还有剩余, 更新第i个时间窗信息（前面已setStartT(startT)更新过了，这里就空着了）
         else{

         }

         //更新job的start_random,end_random
         job.setStart_random(startT); //时间窗的起始时间
         job.setEnd_random(Utils.getEndT(startT,duration));
         job.setSat_id(timeWindow.getSat_id());
         job.setTW_id(timeWindow.getT_id());
         job.setUsed(true);

         //分配该窗口给job
         jobs_completed.add(job);

     }

    public static List<Job> getJobsSolution(List<Job> jobs, List<Satellite> satellites) throws CloneNotSupportedException {

        List<Job> jobs_result = new ArrayList<Job>();
        /**1、遍历所有job，判断当前job是否存在时间窗**/
        for (Job job:jobs) {

            job_timeWindowMap = new HashMap<Job, List<TimeWindow>>();//创建新的job-timeWindows对应关系
            Solution.getTimeWindowsMeetingJob(job, satellites);  //无需返回，会保存两个map
            List<TimeWindow> timeWindows = job_timeWindowMap.get(job);
            if (timeWindows.size() == 0) {//如果当前的job不存在可用的时间窗，则continue
                continue;
            }

            //使用破禁规则

            /**2：随机选择一个时间窗口来使用*/
            Random r = new Random();
            int index = r.nextInt(job_timeWindowMap.get(job).size());

            //更新timeWindow的startT，endT，job的start_random,end_random
            Solution.updateTimeWindowStateWithAllocatedJob(satellites, job, timeWindows.get(index));
            jobs_result.add(job);

            //保存job和时间窗口的对应关系
            jobTimeWindowUsedMap.put(job,timeWindows.get(index));
        }
        return jobs_result;
    }

    /**
     * Step5: 从排列中解决冲突，得到可行解，返回已分配的时间窗资源
     * @param tabuMaps 禁忌表
     * @param mode 选择是否使用禁忌表
     * @return List<Job> Jobs
     */
    public static List<Job> getJobsSolution(List<Job> jobs, List<Satellite> satellites,Map<Job,Map<TimeWindow,Integer>> tabuMaps,boolean mode) throws CloneNotSupportedException {

        List<Job> jobs_result = new ArrayList<Job>();

        for (Job job:jobs) {

//            job_timeWindowMap = new HashMap<Job, List<TimeWindow>>();//创建新的job-timeWindows对应关系

            /**1、遍历所有job，判断当前job是否存在时间窗**/
            Solution.getTimeWindowsMeetingJob(job, satellites);  //无需返回，会保存两个map: job_timeWindowMap和timeWindow_jobMap

            List<TimeWindow> timeWindows = job_timeWindowMap.get(job);  //必须保证job_timeWindowMap有实时更新

            if (timeWindows.size() == 0) {//如果当前的job不存在可用的时间窗，则continue
                continue;
            }

            //使用破禁规则
            if(!mode){

//                /**2：随机选择一个时间窗口来使用*/
//                Random r = new Random();
//                int index = r.nextInt(job_timeWindowMap.get(job).size());

                /**@updateStable 默认使用第一个时间窗口（任务执行度最高）*/
                int index = 0;

                //更新timeWindow的startT，endT，job的start_random,end_random
                Solution.updateTimeWindowStateWithAllocatedJob(satellites, job, timeWindows.get(index));

                jobs_result.add(job);

                //保存job和时间窗口的对应关系
                jobTimeWindowUsedMap.put(job,timeWindows.get(index));
            }
            //使用禁忌表
            else{

                Map<TimeWindow,Integer> tabuMap = tabuMaps.get(job);

                Random r = new Random();
                int size = job_timeWindowMap.get(job).size();

                int index = r.nextInt(size);
                TimeWindow t = timeWindows.get(index);
                for (int i = 0; i < 5 * size; ++i) {

//                    System.out.println("tabuMap used....");

                    //遍历该job的整个tabuMap
                    Set<TimeWindow> tws = tabuMap.keySet();
                    for (TimeWindow temp : tws){

                        //如果该窗口被禁忌，则不分配该窗口
                        if(temp.getSat_id() == t.getSat_id() &&  temp.getT_id() == t.getT_id()){

                        }else{
                            //更新timeWindow的startT，endT，job的start_random,end_random
                            Solution.updateTimeWindowStateWithAllocatedJob(satellites, job, t);
                            jobs_result.add(job);

                            //保存job和时间窗口的对应关系
                            jobTimeWindowUsedMap.put(job,t);
                            break;
                        }

                        index = r.nextInt(size);
                        t = timeWindows.get(index);
                    }
                }
            }
        }
        return jobs_result;
    }
}

