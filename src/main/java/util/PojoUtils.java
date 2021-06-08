package util;

import pojo.Job;
import pojo.Satellite;
import pojo.TimeWindow;

import java.util.*;

public class PojoUtils {

    /**�ж�������Ԫ���location���߶ȣ�γ�ȣ����ȣ��Ƿ���ȫ���**/
    public static boolean isLocationSame(List<String> location1, List<String> location2){

        for (int i = 0; i < location1.size(); i++) {
            if(!location1.get(i).equals(location2.get(i))){
                return false;
            }
        }
        return true;
    }

    /**�ж�sensor1�Ƿ���sensor2���Ӽ�*/
    public static boolean isMatchingSensor(Integer sensor1, Set<Integer> sensor2){

        if(sensor2.contains(sensor1)){
            return true;
        }

        return false;
    }


    /**�ж�resolution1�Ƿ����min_resolution*/
    public static boolean isMatchingResolution(Integer resolution1, Integer min_resolution){

        if(resolution1 >= min_resolution){
            return true;
        }
        return false;
    }


    /**
     * ��jobList�����ȼ�����
     * @java.lang.IllegalArgumentException: Comparison method violates its general contract!
     */
    public static List<Job> orderJobListByPriority(List<Job> jobList){

        //�����ڲ���ʵ��Comparator�ӿڣ�������jobs���ȼ���������Խ�����ȼ�Խ��
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
     * ��timeWindows������ִ�жȽ�������
     */
    public static List<TimeWindow> orderTimeWindowsByPriority(List<TimeWindow> timeWindows){

        //�����ڲ���ʵ��Comparator�ӿڣ�������jobs���ȼ���������Խ�����ȼ�Խ��
        Comparator<TimeWindow> comparator = new Comparator<TimeWindow>() {
            public int compare(TimeWindow t1, TimeWindow t2) {
                // TODO Auto-generated method stub
                if (t1.getTaskPerformance() <= t2.getTaskPerformance()) {  //����ִ�жȴ�������ִ��
                    return 1;
                } else {
                    return -1;
                }
            }
        };
        Collections.sort(timeWindows,comparator);
        return timeWindows;
    }


    /**��jobList���´���˳��(shuffle)���ο�<https://blog.csdn.net/qq_33256688/article/details/80304048>**/
    public static List<Job> shuffleJobList(List<Job> jobList){
        Collections.shuffle(jobList);
        return jobList;
    }

    /**��ȡ��i�����ǵ�j��ʱ�䴰��**/
    public static TimeWindow getTimeWindowWithIndex(List<Satellite> satellites, int i, int j){
        return satellites.get(i).getTimeWindows().get(j);
    }

    /**
     * �ж�ʱ�䴰��timeWindow������Job�Ƿ���ڽ���: �󽻣��������ҽ�
     * @return: �����ཻ��С����λs��
     */
    public static int isIntersection(Date TW_startT, Date TW_endT, Date job_startT, Date job_endT){

        /**�󽻼�: �����ڴ�����ߣ��Ҵ��ڽ���*/
        if(job_startT.compareTo(TW_startT) < 0 && job_endT.compareTo(TW_endT) < 0 && job_endT.compareTo(TW_startT) > 0){
            return Utils.getSecondsByTimeDifference(TW_startT,job_endT);
        }
        /**�ҽ���: �����ڴ����ұߣ��Ҵ��ڽ���*/
        else if(job_startT.compareTo(TW_startT) > 0 && job_endT.compareTo(TW_endT) > 0 && job_startT.compareTo(TW_endT) < 0){
            return Utils.getSecondsByTimeDifference(job_startT,TW_endT);
        }
        /**����: �����������ڣ��Ҵ��ڽ���*/
        else if(job_startT.compareTo(TW_startT) < 0 && job_endT.compareTo(TW_endT) > 0){
            return Utils.getSecondsByTimeDifference(TW_startT,TW_endT);
        }

        return 0; //�޽���
    }

    /**
     * �ж�ʱ�䴰��timeWindow������Job�Ƿ���ڽ���: �󽻣��������ҽ�
     * @return: �����ཻ���ͣ�r-�ҽ���l-��, m-������
     */
    public static String getIntersectionType(Date TW_startT, Date TW_endT, Date job_startT, Date job_endT){

        /**�󽻼�: �����ڴ�����ߣ��Ҵ��ڽ���*/
        if(job_startT.compareTo(TW_startT) < 0 && job_endT.compareTo(TW_endT) < 0 && job_endT.compareTo(TW_startT) > 0){
            return "l";
        }
        /**�ҽ���: �����ڴ����ұߣ��Ҵ��ڽ���*/
        else if(job_startT.compareTo(TW_startT) > 0 && job_endT.compareTo(TW_endT) > 0 && job_startT.compareTo(TW_endT) < 0){
            return "r";
        }
        /**����: �����������ڣ��Ҵ��ڽ���*/
        else if(job_startT.compareTo(TW_startT) < 0 && job_endT.compareTo(TW_endT) > 0){
            return "m";
        }

        return null; //�޽���
    }

    /**
     * �������������isUserdȫ����ΪFalse
     */
    public static void setJobsUsedFalse(List<Job> jobs){
        for (Job job : jobs) {
            job.setUsed(false);
        }
    }


    /**��������������ѡ�⼯������ && ��ת�����ﲻֱ��װJob��ֻ������֮��ı任��*/
    public static List<List<Integer>> getCandidatesFromAreaSearch(List<Job> jobs){

        List<Integer> initialIndex = new ArrayList<Integer>();
        List<List<Integer>> indexLists = new ArrayList<List<Integer>>();

        //��ʼ����
        for(Job job : jobs){
            initialIndex.add(job.getJob_Id() - 1);
        }

        //����
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

        //��ת
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
