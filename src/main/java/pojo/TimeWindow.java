package pojo;

import com.alibaba.fastjson.annotation.JSONField;
import util.Utils;

import java.util.Date;
import java.util.List;

/**һ��ʱ�䴰��TimeWindow��Ӧһ������Satellite**/
public class TimeWindow implements Cloneable{

    private Integer t_id;  //��ʾ��t_id��ʱ�䴰��
    private Integer sat_id;  //�����ǰ�
    private List<String> location;  //�۲�Ŀ��λ�ã���Ԫ�飨�߶ȣ�γ�ȣ����ȣ���eg:��0,30.5_E,120.5_N��

    @JSONField(name="startT", format="yyyy-MM-dd HH:mm:ss")
    private Date startT;  //ʱ�䴰�Ŀ�ʼʱ��

    @JSONField(name="endT", format="yyyy-MM-dd HH:mm:ss")
    private Date endT;  //ʱ�䴰�Ľ���ʱ��

    private List<Job> jobs_completed = null;  //�������Ѵ��������

    /**@updateStable*/
    private Float taskPerformance;  //����ִ�жȣ�������Ҫ��ʱ����ʱ�䴰�ڵ�ռ�ȣ�

    public TimeWindow() {

    }

    public TimeWindow(Integer t_id, List<String> location, Date startT, Date endT) {
        this.t_id = t_id;
        this.location = location;
        this.startT = startT;
        this.endT = endT;
    }

    public Float getTaskPerformance() {
        return taskPerformance;
    }

    public void setTaskPerformance(Float taskPerformance) {
        this.taskPerformance = taskPerformance;
    }

    public Integer getSat_id() {
        return sat_id;
    }

    public void setSat_id(Integer sat_id) {
        this.sat_id = sat_id;
    }

    public Integer getT_id() {
        return t_id;
    }

    public void setT_id(Integer t_id) {
        this.t_id = t_id;
    }

    public List<String> getLocation() {
        return location;
    }

    public void setLocation(List<String> location) {
        this.location = location;
    }

    public Date getStartT() {
        return startT;
    }

    public void setStartT(Date startT) {
        this.startT = startT;
    }

    public Date getEndT() {
        return endT;
    }

    public void setEndT(Date endT) {
        this.endT = endT;
    }

    public List<Job> getJobs_completed() {
        return jobs_completed;
    }

    public void setJobs_completed(List<Job> jobs_completed) {
        this.jobs_completed = jobs_completed;
    }

    @Override
    public String toString() {
        return "TimeWindow{" +
                "t_id=" + t_id +
                ", sat_id=" + sat_id +
                ", location=" + location +
                ", startT=" + startT +
                ", endT=" + endT +
                '}';
    }

    @Override
    /**�ο�<https://www.cnblogs.com/xinruyi/p/11537963.html>*/
    public TimeWindow clone() throws CloneNotSupportedException {
        TimeWindow timeWindow = (TimeWindow) super.clone();
        timeWindow.setSat_id(this.sat_id);
        timeWindow.setLocation(this.location);
        timeWindow.setStartT(Utils.Dateclone(this.startT));
        timeWindow.setEndT(Utils.Dateclone(this.endT));
        return timeWindow;
    }
}
