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

    private int epoch; //TS��������
    private List<Job> jobsSolution;  //Jobsÿ�ε����Ľ�
    private Map<Job,Map<TimeWindow,Integer>> tabuMaps = new ConcurrentHashMap<Job, Map<TimeWindow, Integer>>();  //ÿ��job�����Լ��Ľ��ɱ�
    private Map<Integer,Job> jobsMap = new HashMap<Integer, Job>();  //ͨ����������ȡjob
    private Map<List<Job>,Integer> jobsCandidateSolution = new HashMap<List<Job>,Integer>();  //������¼ÿһ�ֺ�ѡ���н�����
    private List<Job> optimalSolution = new ArrayList<Job>(); //���浱ǰ���Ž�
    private int MAX_CompletedNum = 0;  //���������ɸ���
    private String satellite_json = "";  //����Satellitesԭ����Ϣ

    /**��ʼ��jobsMap*/
    public TabuSearch(List<Job> jobs, String json){

        for (Job job : jobs) {
            int index = job.getJob_Id() - 1;
            jobsMap.put(index,job);
        }

        this.satellite_json = json;
    }

    /**Step1���õ���ʼ��*/
    public void initialSolution(List<Job> jobs){
        jobsSolution = Solution.permutateJobList(jobs);
        jobsSolution = Solution.orderJobsByPriority(jobsSolution);

    }

    /**Step2: ��ʼ�����ɱ�*/
    public void initialTabuMap(List<Job> jobs){
        for (Job job : jobs) {
            tabuMaps.put(job,new ConcurrentHashMap<TimeWindow, Integer>());
        }
    }

    /**
     * Step3: ��������������ѡ�⼯,���Ժ�ѡ�������е��������Լ���жϣ��ƽ������жϣ�����tabuMaps
     * @return: ��ǰ״̬�����Ž⣺�����ƽ�����Ľ⣬���û����������ɱ��еĽ⡣
     */
    public List<Job> getCandidatesSolution(List<Job> jobs,List<Satellite> satellites){

        //�õ������
        List<List<Integer>> candidatesIndex = PojoUtils.getCandidatesFromAreaSearch(jobs);

        //Ϊ��ѡ����װjobs
        List<List<Job>> jobsCandidates = new ArrayList<List<Job>>();

        int k = 0;
        for (List<Integer> indexList : candidatesIndex) {
            jobsCandidates.add(new ArrayList<Job>());

            for (Integer index : indexList) {
                jobsCandidates.get(k).add(jobsMap.get(index));
            }
            k++;
        }

        //�жϺ�ѡ�����Ƿ�����Լ���������Լ��ƽ����򣺼���ǰ���еĽ��Ƿ�����֮��õ��Ľ�
        for (List<Job> jobList : jobsCandidates){

            try {

                /**�÷����޸���jobs��start_random��end_random��isUsed���ԣ�satellites��jobs,startT,endT����*/
                //��ÿ��Ѱ�ҿ��ô���ʱ������ÿһ���������У���Ҫ���³�ʼ��ʱ�䴰��
                satellites = PojoGenerator.SatellitesGeneratorWithJson(satellite_json);
                List<Job> temp = Solution.getJobsSolution(jobList, satellites, tabuMaps,false);

                //��������ƽ���������½��ɱ����ɶ���Ϊĳ����ʹ�õ�ʱ�䴰�ڣ�
                if(temp.size() > MAX_CompletedNum){
                    MAX_CompletedNum  = temp.size();
                    optimalSolution = temp;

                    //Ϊÿ��job���½��ɱ�
                    //��ÿ��Ѱ�ҿ��ô���ʱ������ÿһ���������У���Ҫ���³�ʼ��ʱ�䴰��
                    satellites = PojoGenerator.SatellitesGeneratorWithJson(satellite_json);
                    updateTabuMap(temp,satellites);

                    //����ǰ�����Ž���Ϊ��һ�ε����ĳ�ʼ��
                    jobsSolution = optimalSolution;
                    return jobsSolution;

                }
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }

//        System.out.println("������ɸ���Ϊ = " + MAX_CompletedNum);
//        System.out.println("������������Ϊ = " + optimalSolution.get(MAX_CompletedNum));

        //�жϺ�ѡ�⼯�Ƿ��Ӧ����Ľ��������������ѡ���������������Ž�
        Map<Integer,List<Job>> optimalTabuSolution = new HashMap<Integer,List<Job>>();  //�������ŵĽ��ɽ�
        int MAX_TABUSOLUTION = 0;
        for (List<Job> jobList : jobsCandidates){

            try {

//                System.out.println("��ʼʹ�ý��ɱ�");
//                Set<Job> jobs1 = tabuMaps.keySet();
//                for (Job job : jobs1){
//                    System.out.println("key = " + job.getJob_Id() + " val = " + tabuMaps.get(job));
//                }
//                System.out.println("--------------------------");

                //��ÿ��Ѱ�ҿ��ô���ʱ������ÿһ���������У���Ҫ���³�ʼ��ʱ�䴰��
                satellites = PojoGenerator.SatellitesGeneratorWithJson(satellite_json);
                List<Job> temp  = Solution.getJobsSolution(jobList, satellites, tabuMaps, true);

//                System.out.println("------------------------------");

                //�����������Ҫ������Ž�
                if(temp.size() > MAX_CompletedNum){
                    MAX_TABUSOLUTION  = temp.size();
                    optimalTabuSolution.put(MAX_TABUSOLUTION,temp);

                    //����ǰ�����Ž���Ϊ��һ�ε����ĳ�ʼ��
                    jobsSolution = temp;

                    //����tabuMap
                    tabuMapAutoDecrease();

                    return jobsSolution;
                }

            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**����tabuMap*/
    public void updateTabuMap(List<Job> jobs,List<Satellite> satellites){

        for (Job job:jobs) {

            //������job���õ�ʱ�䴰��
            List<TimeWindow> timeWindows = Solution.getTimeWindowsMeetingJob(job,satellites);

            //�õ�job��timeWindow��Ӧ��job�ѷ��䣩
            Map<Job, TimeWindow> jobTimeWindowUsedMap = Solution.getJobTimeWindowUsedMap();
            TimeWindow tw = jobTimeWindowUsedMap.get(job);

            //timeWindows�г���tw���������ڶ���д�뵽job�Ľ��ɱ���
            for (TimeWindow t:timeWindows) {

//                System.out.println("����tabuMap");
                //����(7-���ȼ�)��Ϊ���ɳ��ȣ����ȼ�Խ�ߣ����ɳ���Խ��
                if(t != tw){
                    Map<TimeWindow, Integer> map = tabuMaps.get(job);
                    map.put(t,(int)Math.floor((float)(7-job.getPriority())*1.5));
                    tabuMaps.put(job,map);
                }
            }
        }
    }

    /**
     * ͨ�����õ�������������tabuSearch
     * this.satellite_json: ����ÿ�ε������޸�����ʱ�䴰�ڵ�startT��endT�������һ�ε���֮ǰ��ͨ��json�ַ��������µ���������
     */
    public List<Job> tabuSearchWithEpoch(int epoch, List<Job> jobs,List<Satellite> satellites){

        initialSolution(jobs);  //��ʼ���⣨�������ȼ�����
        initialTabuMap(jobs); //��ʼ�����ɱ�

        for(int i = 0; i < epoch; ++i){

//            satellites = PojoGenerator.SatellitesGeneratorWithJson(satellite_json);  //�����µ�satellite
            List<Job> candidatesSolution = getCandidatesSolution(jobs, satellites);
            System.out.println("������ɸ���Ϊ = " + MAX_CompletedNum);

            //ÿ�ε������������satellites�ϵ�TimeWindowռ�õ�jobs��Դ,�����������used��Ϊfalse
            PojoUtils.setJobsUsedFalse(jobs);
        }

        return optimalSolution;
    }

    /**ÿ����һ�ε��������ɱ��еĽ��ɶ����Ӧ�Ľ��ɳ��ȶ���ݼ�1*/
    public void tabuMapAutoDecrease(){

//        System.out.println("�ݼ�tabuMap----");
        Set<Job> jobs = tabuMaps.keySet();

        for (Job job : jobs){

            Map<TimeWindow,Integer> map_temp = tabuMaps.get(job);
            Set<TimeWindow> timeWindows = map_temp.keySet();

            for(TimeWindow t : timeWindows){

                map_temp.put(t,map_temp.get(t)-1);

                //������ɳ���Ϊ0���������timeWindow
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
