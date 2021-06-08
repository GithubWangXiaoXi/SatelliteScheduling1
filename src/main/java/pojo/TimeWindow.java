package pojo;

import com.alibaba.fastjson.annotation.JSONField;
import util.Utils;

import java.util.Date;
import java.util.List;

/**一个时间窗口TimeWindow对应一个卫星Satellite**/
public class TimeWindow implements Cloneable{

    private Integer t_id;  //表示第t_id个时间窗口
    private Integer sat_id;  //与卫星绑定
    private List<String> location;  //观测目标位置：三元组（高度，纬度，经度），eg:（0,30.5_E,120.5_N）

    @JSONField(name="startT", format="yyyy-MM-dd HH:mm:ss")
    private Date startT;  //时间窗的开始时间

    @JSONField(name="endT", format="yyyy-MM-dd HH:mm:ss")
    private Date endT;  //时间窗的结束时间

    private List<Job> jobs_completed = null;  //该卫星已处理的任务

    /**@updateStable*/
    private Float taskPerformance;  //任务执行度（任务需要的时间在时间窗口的占比）

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
    /**参考<https://www.cnblogs.com/xinruyi/p/11537963.html>*/
    public TimeWindow clone() throws CloneNotSupportedException {
        TimeWindow timeWindow = (TimeWindow) super.clone();
        timeWindow.setSat_id(this.sat_id);
        timeWindow.setLocation(this.location);
        timeWindow.setStartT(Utils.Dateclone(this.startT));
        timeWindow.setEndT(Utils.Dateclone(this.endT));
        return timeWindow;
    }
}
