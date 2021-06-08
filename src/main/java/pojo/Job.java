package pojo;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;
import java.util.List;

/**一个卫星观测任务Job，包含多个Procedure子任务**/
public class Job{

    private Integer job_Id;  //为的是与procedure绑定，让procedure获取location，priority等基本信息
    private String name;  //任务名字或任务编号（Name）
    private List<String> location; //观测目标位置：三元组（高度，纬度，经度），eg:（0,30.5_E,120.5_N）
    private Integer priority; //任务优先级: 1最低，5最高
    private Integer sensor; //任务要求的星上载荷传感器类型：1-可见光、2-SAR（合成孔径雷达）、3-红外、4-多光谱（只选一种）
    private Integer resolution;  //任务对成像的分辨率要求

    @JSONField(name="start_random", format="yyyy-MM-dd HH:mm:ss")
    private Date start_random;  //任务的随机起始时间（在startT~endT之间）

    @JSONField(name="end_random", format="yyyy-MM-dd HH:mm:ss")
    private Date end_random;

    @JSONField(name="startT", format="yyyy-MM-dd HH:mm:ss")
    private Date startT; //任务完成的最早起始时间

    @JSONField(name="endT", format="yyyy-MM-dd HH:mm:ss")
    private Date endT; //任务完成的最晚结束时间

    private Integer duration;  //子任务要求的最小观测时间，单位为s

    private Boolean isUsed;  //该子任务是否被某个时间窗口接收

    private Integer Sat_id;  //卫星编号

    private Integer TW_id;  //时间窗口编号

    public Integer getSat_id() {
        return Sat_id;
    }

    public void setSat_id(Integer sat_id) {
        Sat_id = sat_id;
    }

    public Integer getTW_id() {
        return TW_id;
    }

    public void setTW_id(Integer TW_id) {
        this.TW_id = TW_id;
    }

    public Job() {
        this.isUsed = false;
    }

    public Job(String name, Integer priority, Integer sensor, Integer resolution) {
        this.name = name;
        this.priority = priority;
        this.sensor = sensor;
        this.resolution = resolution;
    }

    public Date getStart_random() {
        return start_random;
    }

    public void setStart_random(Date start_random) {
        this.start_random = start_random;
    }

    public Date getEnd_random() {
        return end_random;
    }

    public void setEnd_random(Date end_random) {
        this.end_random = end_random;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getLocation() {
        return location;
    }

    public void setLocation(List<String> location) {
        this.location = location;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getSensor() {
        return sensor;
    }

    public void setSensor(Integer sensor) {
        this.sensor = sensor;
    }

    public Integer getResolution() {
        return resolution;
    }

    public void setResolution(Integer resolution) {
        this.resolution = resolution;
    }

    public Integer getJob_Id() {
        return job_Id;
    }

    public void setJob_Id(Integer job_Id) {
        this.job_Id = job_Id;
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

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Boolean getUsed() {
        return isUsed;
    }

    public void setUsed(Boolean used) {
        isUsed = used;
    }

    @Override
    public String toString() {
        return "Job{" +
                "job_Id=" + job_Id +
                ", name='" + name + '\'' +
                ", location=" + location +
                ", priority=" + priority +
                ", sensor=" + sensor +
                ", resolution=" + resolution +
                ", start_random=" + start_random +
                ", end_random=" + end_random +
                ", startT=" + startT +
                ", endT=" + endT +
                ", duration=" + duration +
                ", isUsed=" + isUsed +
                '}';
    }
}
