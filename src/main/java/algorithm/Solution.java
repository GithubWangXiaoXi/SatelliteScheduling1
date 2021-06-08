package algorithm;

import pojo.Job;
import pojo.Satellite;
import pojo.TimeWindow;
import util.Utils;
import util.PojoUtils;

import java.util.*;

/**
 * ������һ��Լ���½����������
 */
public class Solution {

    /**
     * ����Job�����ʱ�䴰��timeWindow��1�Զ�Ķ�Ӧ��ϵ����map���洢
     * @Note: job(1) - timeWindow(many), ��ʾ��job�����Ͽ��Է��䵽��ЩtimeWindow��
     */
    private static Map<Job,List<TimeWindow>> job_timeWindowMap = new HashMap<Job, List<TimeWindow>>();

    /**
     * ����ʱ�䴰��timeWindow������Job��1�Զ�Ķ�Ӧ��ϵ����map���洢
     * @Note: timeWindow(1) - job(many), ��ʾ��timeWindow�����Ͽ��Է������Щjob��
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
     * Step1: �����е��������������У�����һ������
     */
    public static List<Job> permutateJobList(List<Job> jobs){
        return PojoUtils.shuffleJobList(jobs);
    }

    /**
     * Step2: �ȶ���������������ȼ�����
     * @Constraint1: priority
     */
    public static List<Job> orderJobsByPriority(List<Job> jobs){
        jobs = PojoUtils.orderJobListByPriority(jobs);
        return jobs;
    }

    /**
     * Step3: ��˳�����ν������е�����ŵ����е�ʱ�䴰���У��ڷ����ʱ���ǳ�ͻ1��
     * @Goal: ����������ʱ�䴰�ĳ�ͻ
     */

    /**
     * Step3_1: �ж��Ƿ����ʱ�䴰���п���ʹ��һ�����ˣ���������ƶ���Χ֮��
     * @Constraint1: J_window �ཻ TW >= J_duration
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

                //�������ʱ��constraint��������������Ͽ��Է��䵽�����Ǵ�����
                if(isIntersection > job.getDuration()){

                    boolean ret = true;
                    ret = ret && isMeetingLocation(timeWindow,job);
                    ret = ret && isMeetingResolutionAndSensor(satellites,timeWindow,job);
                    if(ret){

                        /**@UpdateStable  ����ÿ�����ڵ�����ִ�ж�*/
                        Integer diff = Utils.getSecondsByTimeDifference(TW_startT,TW_endT);
                        Float taskPerformance = (float)job.getDuration() / diff;
                        timeWindow.setTaskPerformance(taskPerformance);

                        timeWindows.add(timeWindow);

                        //����job��timewindow�Ķ��1��ϵ������timewindow��startT�Ѹ��ģ���Ҫ���²���jobs��
                        List<Job> jobs = new ArrayList<Job>();

                        jobs.add(job);
                        timeWindow_jobMap.put(timeWindow,jobs);
                    }
//                    System.out.println("TW_startT = " + TW_startT + " TW_endT = " + TW_endT);
//                    System.out.println("job_startT = " + job_startT + " job_endT = " + job_endT + "\n");
                }
            }
        }
        /**@updateStable ��timeWindows��������ִ�жȣ�������Ҫ��ʱ����ʱ�䴰�ڵ�ռ�ȣ���������*/
        timeWindows = PojoUtils.orderTimeWindowsByPriority(timeWindows);

        //����job��timewindow��1�Զ��ϵ
        job_timeWindowMap.put(job,timeWindows);
        return timeWindows;
    }

    /**
     * Step 3_2: ����ĵ�ַ��ʱ�䴰�ڵĵ�ַ�Ƿ�һ�¡�
     * constraint2��Job_location == timeWindow_location
     */
    public static boolean isMeetingLocation(TimeWindow timeWindow,Job job){
        return PojoUtils.isLocationSame(timeWindow.getLocation(),job.getLocation());
    }

    /**
     * Step 3_3: ����Ĵ����������Ƿ���ʱ�䴰�ڵĴ����������С�
     * Step 3_4: ����ķֱ����Ƿ����ʱ�䴰����С�ֱ��ʡ�
     * constraint3��Job_sensor in SAT_sensors
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
     * Step4: ��˳�����ν������е�����ŵ����е�ʱ�䴰���У��ڷ����ʱ���ǳ�ͻ2��
     * @Goal: �Ե���ʱ�䴰Ϊ׼��������������֮��ĳ�ͻ��������
     * @return: ���غ��ʵ�job
     */
     public static List<Job> handleTwoJobsCollisionInSameTimeWindow(List<Satellite> satellites, TimeWindow timeWindow,Job job1,Job job2) throws CloneNotSupportedException {

         //�ж�job1��job2�Ƿ���Է��䵽�ô���
         List<Job> jobs = timeWindow_jobMap.get(timeWindow);
         if(!(jobs.contains(job1) && jobs.contains(job2))){
             System.out.println("job1��job2�����ڳ�ͻ");
             return null;
         }

         //�������������һ���Ѿ��������������ѣ���ֱ�ӽ�û�����ѵ������͸������Ǵ���
         if(job1.getUsed() && !job2.getUsed()){
             jobs.add(job2);
             return jobs;
         }else if(!job1.getUsed() && job2.getUsed()){
             jobs.add(job1);
             return jobs;
         }

         //�����ͻ���������ȼ��ߵ�job
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
      * Step4_1: �ж����������Ƿ���ڳ�ͻ
      * @Constraint��
      * 1) job1, job2�����ʸ����ô��ڣ�handleTwoJobsCollisionInSameTimeWindow���жϣ�
      *
      * timeWindow����ͼ��ʾ��|Job1|MTI|-|Job2|���±�ֱ�Ϊa,b,c,d,e)
      * 2) c <= d���򲻳�ͻ
      * 3) c > d����ͻ�������ȼ�ѡ��һ��job
      */
     public static boolean isJobsCollision(List<Satellite> satellites, TimeWindow timeWindow,Job job1,Job job2) throws CloneNotSupportedException {

         Date TW_startT = timeWindow.getStartT();
         Date TW_endT = timeWindow.getEndT();
         Date job1_startT = job1.getStartT();
         Date job1_endT = job1.getEndT();
         Date job2_startT = job2.getStartT();
         Date job2_endT = job2.getEndT();

         //��ȡjob1�ཻ����
         String job1_intersectionType = PojoUtils.getIntersectionType(TW_startT,TW_endT,job1_startT,job1_endT);
         //��ȡjob2�ཻ����
         String job2_intersectionType = PojoUtils.getIntersectionType(TW_startT,TW_endT,job2_startT,job2_endT);

         //clone����timeWindows���г�ͻģ��
         TimeWindow tw_clone1 = timeWindow.clone();
         TimeWindow tw_clone2 = timeWindow.clone();

         /**����job1,����tw_clone1��ʱ��*/
         //�� || ��������job1��������
         if(job1_intersectionType.equals('l') || job1_intersectionType.equals('m')){

             Date tw_endT = Utils.getEndT(TW_startT,job1.getDuration());
             tw_clone1.setEndT(Utils.getEndT(tw_endT,satellites.get(timeWindow.getSat_id()).getMTI())); //�������job1�󣬼���MTI
         }
         //�ҽ�����job1��������
         else if(job1_intersectionType.equals('r') ){
             Date tw_startT = Utils.getStartT(TW_endT,job1.getDuration());
             tw_clone1.setStartT(tw_startT);
         }

         /**����job2,����tw_clone2��ʱ��*/
         //�󽻣���job2��������
         if(job2_intersectionType.equals('l')){
             Date tw_endT = Utils.getEndT(TW_startT,job2.getDuration());
             tw_clone2.setEndT(Utils.getEndT(tw_endT,satellites.get(timeWindow.getSat_id()).getMTI())); //�������job2�󣬼���MTI
         }
         //�ҽ� || ��������job2��������
         else if(job2_intersectionType.equals('r') || job2_intersectionType.equals('m')){
             Date tw_startT = Utils.getStartT(TW_endT,job2.getDuration());
             tw_clone2.setStartT(tw_startT);
         }

         /**�ж��Ƿ�tw_clone1.endT <= tw_clone2.startT || tw_clone2.endT <= tw_clone1.startT, ����ǣ��򲻳�ͻ*/
         if(tw_clone1.getEndT().compareTo(tw_clone2.getStartT()) <=  0
                 || tw_clone2.getEndT().compareTo(tw_clone1.getStartT()) <= 0){
             return false;
         }
         return true;
     }

     /**
      * Step4_2: ������������i��ʱ�䴰��ʱ�����µ�i�������i+1�����ڵ�startT��endT
      * 1) ����MTI�󣬵�i��������Ȼ��ʣ�࣬����startT
      * 2) ����MTIʱ����i�����������꣬��Ҫ���µ�i+1�����ڵ�startT'
      * @param satellites: Ϊ�˻�ȡ��һ��ʱ�䴰����Ϣ
      * @param job: ��ǰҪ���������
      * @param timeWindow����ǰҪ���µ�
      */
     public static void updateTimeWindowStateWithAllocatedJob(List<Satellite> satellites, Job job,TimeWindow timeWindow){

         List<Job> jobs_completed = timeWindow.getJobs_completed();
         if(jobs_completed == null){
             jobs_completed = new ArrayList<Job>();
         }

         //�жϸô��ڸ�job֮�� + MTI���Ƿ���ʣ�ࣨ��startT�Ƿ�С��endT��
         Date startT = timeWindow.getStartT();
         Satellite satellite = satellites.get(timeWindow.getSat_id() - 1);
         int duration = job.getDuration();
         int MTI = satellite.getMTI();  //ע�������Sat_id��1��ʼ

         Date startT_updated = Utils.getEndT(startT,duration + MTI);
         timeWindow.setStartT(startT_updated);

//         System.out.println("startT_update = " + Utils.date2Str(startT_updated) + " endT = " + Utils.date2Str(timeWindow.getEndT()));

         /**startT >= endT, ��i��ʱ�䴰������, ���µ� i + 1��ʱ�䴰��Ϣ*/
         if(startT_updated.compareTo(timeWindow.getEndT()) >= 0){
            int index = timeWindow.getT_id();

            //�����ǰ���������һ�����ڣ��򲻲���
            if(!(index == satellite.getTimeWindows().size())){
                TimeWindow next_tw = satellite.getTimeWindows().get((index - 1) + 1); //��һ��ʱ�䴰
//                int offset = MTI + duration - (Utils.getSecondsByTimeDifference(startT, timeWindow.getEndT()));
//                next_tw.setStartT(Utils.getEndT(next_tw.getStartT(),offset));  //ͨ��ƫ����offset��������һ��ʱ�䴰startT
                next_tw.setStartT(Utils.Dateclone(startT_updated));
            }
         }
         //startT < endT, ��i��ʱ�䴰����ʣ��, ���µ�i��ʱ�䴰��Ϣ��ǰ����setStartT(startT)���¹��ˣ�����Ϳ����ˣ�
         else{

         }

         //����job��start_random,end_random
         job.setStart_random(startT); //ʱ�䴰����ʼʱ��
         job.setEnd_random(Utils.getEndT(startT,duration));
         job.setSat_id(timeWindow.getSat_id());
         job.setTW_id(timeWindow.getT_id());
         job.setUsed(true);

         //����ô��ڸ�job
         jobs_completed.add(job);

     }

    public static List<Job> getJobsSolution(List<Job> jobs, List<Satellite> satellites) throws CloneNotSupportedException {

        List<Job> jobs_result = new ArrayList<Job>();
        /**1����������job���жϵ�ǰjob�Ƿ����ʱ�䴰**/
        for (Job job:jobs) {

            job_timeWindowMap = new HashMap<Job, List<TimeWindow>>();//�����µ�job-timeWindows��Ӧ��ϵ
            Solution.getTimeWindowsMeetingJob(job, satellites);  //���践�أ��ᱣ������map
            List<TimeWindow> timeWindows = job_timeWindowMap.get(job);
            if (timeWindows.size() == 0) {//�����ǰ��job�����ڿ��õ�ʱ�䴰����continue
                continue;
            }

            //ʹ���ƽ�����

            /**2�����ѡ��һ��ʱ�䴰����ʹ��*/
            Random r = new Random();
            int index = r.nextInt(job_timeWindowMap.get(job).size());

            //����timeWindow��startT��endT��job��start_random,end_random
            Solution.updateTimeWindowStateWithAllocatedJob(satellites, job, timeWindows.get(index));
            jobs_result.add(job);

            //����job��ʱ�䴰�ڵĶ�Ӧ��ϵ
            jobTimeWindowUsedMap.put(job,timeWindows.get(index));
        }
        return jobs_result;
    }

    /**
     * Step5: �������н����ͻ���õ����н⣬�����ѷ����ʱ�䴰��Դ
     * @param tabuMaps ���ɱ�
     * @param mode ѡ���Ƿ�ʹ�ý��ɱ�
     * @return List<Job> Jobs
     */
    public static List<Job> getJobsSolution(List<Job> jobs, List<Satellite> satellites,Map<Job,Map<TimeWindow,Integer>> tabuMaps,boolean mode) throws CloneNotSupportedException {

        List<Job> jobs_result = new ArrayList<Job>();

        for (Job job:jobs) {

//            job_timeWindowMap = new HashMap<Job, List<TimeWindow>>();//�����µ�job-timeWindows��Ӧ��ϵ

            /**1����������job���жϵ�ǰjob�Ƿ����ʱ�䴰**/
            Solution.getTimeWindowsMeetingJob(job, satellites);  //���践�أ��ᱣ������map: job_timeWindowMap��timeWindow_jobMap

            List<TimeWindow> timeWindows = job_timeWindowMap.get(job);  //���뱣֤job_timeWindowMap��ʵʱ����

            if (timeWindows.size() == 0) {//�����ǰ��job�����ڿ��õ�ʱ�䴰����continue
                continue;
            }

            //ʹ���ƽ�����
            if(!mode){

//                /**2�����ѡ��һ��ʱ�䴰����ʹ��*/
//                Random r = new Random();
//                int index = r.nextInt(job_timeWindowMap.get(job).size());

                /**@updateStable Ĭ��ʹ�õ�һ��ʱ�䴰�ڣ�����ִ�ж���ߣ�*/
                int index = 0;

                //����timeWindow��startT��endT��job��start_random,end_random
                Solution.updateTimeWindowStateWithAllocatedJob(satellites, job, timeWindows.get(index));

                jobs_result.add(job);

                //����job��ʱ�䴰�ڵĶ�Ӧ��ϵ
                jobTimeWindowUsedMap.put(job,timeWindows.get(index));
            }
            //ʹ�ý��ɱ�
            else{

                Map<TimeWindow,Integer> tabuMap = tabuMaps.get(job);

                Random r = new Random();
                int size = job_timeWindowMap.get(job).size();

                int index = r.nextInt(size);
                TimeWindow t = timeWindows.get(index);
                for (int i = 0; i < 5 * size; ++i) {

//                    System.out.println("tabuMap used....");

                    //������job������tabuMap
                    Set<TimeWindow> tws = tabuMap.keySet();
                    for (TimeWindow temp : tws){

                        //����ô��ڱ����ɣ��򲻷���ô���
                        if(temp.getSat_id() == t.getSat_id() &&  temp.getT_id() == t.getT_id()){

                        }else{
                            //����timeWindow��startT��endT��job��start_random,end_random
                            Solution.updateTimeWindowStateWithAllocatedJob(satellites, job, t);
                            jobs_result.add(job);

                            //����job��ʱ�䴰�ڵĶ�Ӧ��ϵ
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

