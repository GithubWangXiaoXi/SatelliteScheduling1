package util;

import pojo.Job;
import pojo.Satellite;
import pojo.TimeWindow;

import java.util.*;

public class PojoUtils {

    /**判断两个三元组的location（高度，纬度，经度）是否完全相等**/
    public static boolean isLocationSame(List<String> location1, List<String> location2){

        for (int i = 0; i < location1.size(); i++) {
            if(!location1.get(i).equals(location2.get(i))){
                return false;
            }
        }
        return true;
    }

    /**判断sensor1是否是sensor2的子集*/
    public static boolean isMatchingSensor(Integer sensor1, Set<Integer> sensor2){

        if(sensor2.contains(sensor1)){
            return true;
        }

        return false;
    }


    /**判断resolution1是否大于min_resolution*/
    public static boolean isMatchingResolution(Integer resolution1, Integer min_resolution){

        if(resolution1 >= min_resolution){
            return true;
        }
        return false;
    }


    /**
     * 对jobList按优先级排序
     * @java.lang.IllegalArgumentException: Comparison method violates its general contract!
     */
    public static List<Job> orderJobListByPriority(List<Job> jobList){

        //匿名内部类实现Comparator接口，进而对jobs优先级排序（数字越大，优先级越大
        Comparator<Job> comparator = new Comparator<Job>() {
            public int compare(Job p1, Job p2) {
                // TODO Auto-generated method stub
                if (p1.getPriority() <= p2.getPriority()) {
                    return 1;
                } else {
                    return -1;
                }
            }
        };

        Collections.sort(jobList,comparator);
        return jobList;
    }

    /**
     * @updateStable
     * 对timeWindows按任务执行度进行排序
     */
    public static List<TimeWindow> orderTimeWindowsByPriority(List<TimeWindow> timeWindows){

        //匿名内部类实现Comparator接口，进而对jobs优先级排序（数字越大，优先级越大
        Comparator<TimeWindow> comparator = new Comparator<TimeWindow>() {
            public int compare(TimeWindow t1, TimeWindow t2) {
                // TODO Auto-generated method stub
                if (t1.getTaskPerformance() <= t2.getTaskPerformance()) {  //任务执行度大，则优先执行
                    return 1;
                } else {
                    return -1;
                }
            }
        };
        Collections.sort(timeWindows,comparator);
        return timeWindows;
    }


    /**对jobList重新打乱顺序(shuffle)，参考<https://blog.csdn.net/qq_33256688/article/details/80304048>**/
    public static List<Job> shuffleJobList(List<Job> jobList){
        Collections.shuffle(jobList);
        return jobList;
    }

    /**获取第i颗卫星第j个时间窗口**/
    public static TimeWindow getTimeWindowWithIndex(List<Satellite> satellites, int i, int j){
        return satellites.get(i).getTimeWindows().get(j);
    }

    /**
     * 判断时间窗口timeWindow和任务Job是否存在交集: 左交，包含，右交
     * @return: 返回相交大小（单位s）
     */
    public static int isIntersection(Date TW_startT, Date TW_endT, Date job_startT, Date job_endT){

        /**左交集: 任务在窗口左边，且存在交集*/
        if(job_startT.compareTo(TW_startT) < 0 && job_endT.compareTo(TW_endT) < 0 && job_endT.compareTo(TW_startT) > 0){
            return Utils.getSecondsByTimeDifference(TW_startT,job_endT);
        }
        /**右交集: 任务在窗口右边，且存在交集*/
        else if(job_startT.compareTo(TW_startT) > 0 && job_endT.compareTo(TW_endT) > 0 && job_startT.compareTo(TW_endT) < 0){
            return Utils.getSecondsByTimeDifference(job_startT,TW_endT);
        }
        /**包含: 窗口在任务内，且存在交集*/
        else if(job_startT.compareTo(TW_startT) < 0 && job_endT.compareTo(TW_endT) > 0){
            return Utils.getSecondsByTimeDifference(TW_startT,TW_endT);
        }

        return 0; //无交集
    }

    /**
     * 判断时间窗口timeWindow和任务Job是否存在交集: 左交，包含，右交
     * @return: 返回相交类型（r-右交，l-左交, m-包含）
     */
    public static String getIntersectionType(Date TW_startT, Date TW_endT, Date job_startT, Date job_endT){

        /**左交集: 任务在窗口左边，且存在交集*/
        if(job_startT.compareTo(TW_startT) < 0 && job_endT.compareTo(TW_endT) < 0 && job_endT.compareTo(TW_startT) > 0){
            return "l";
        }
        /**右交集: 任务在窗口右边，且存在交集*/
        else if(job_startT.compareTo(TW_startT) > 0 && job_endT.compareTo(TW_endT) > 0 && job_startT.compareTo(TW_endT) < 0){
            return "r";
        }
        /**包含: 窗口在任务内，且存在交集*/
        else if(job_startT.compareTo(TW_startT) < 0 && job_endT.compareTo(TW_endT) > 0){
            return "m";
        }

        return null; //无交集
    }

    /**
     * 将所有子任务的isUserd全都置为False
     */
    public static void setJobsUsedFalse(List<Job> jobs){
        for (Job job : jobs) {
            job.setUsed(false);
        }
    }


    /**邻域搜索产生候选解集：互换 && 翻转（这里不直接装Job，只是索引之间的变换）*/
    public static List<List<Integer>> getCandidatesFromAreaSearch(List<Job> jobs){

        List<Integer> initialIndex = new ArrayList<Integer>();
        List<List<Integer>> indexLists = new ArrayList<List<Integer>>();

        //初始索引
        for(Job job : jobs){
            initialIndex.add(job.getJob_Id() - 1);
        }

        //互换
        for(int i = 0; i < jobs.size(); ++i){
            indexLists.add(new ArrayList<Integer>());

            for(int j = i + 1; j < jobs.size(); ++j){
                if(i + 1 == j){
                    int temp = initialIndex.get(i);
                    indexLists.get(i).add(initialIndex.get(j));
                    indexLists.get(i).add(temp);
                }else{
                    indexLists.get(i).add(initialIndex.get(j));
                }
            }
        }

        //翻转
        int length = indexLists.size();
        for(int i = 0; i < jobs.size(); ++i){
            indexLists.add(new ArrayList<Integer>());

            for(int j = 0; j < jobs.size(); ++j){
                if(!(i == j)){
                   indexLists.get(i + length).add(initialIndex.get(j));
                }
            }
        }

        return indexLists;
    }
}
