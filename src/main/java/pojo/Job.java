package pojo;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;
import java.util.List;

/**һ�����ǹ۲�����Job���������Procedure������**/
public class Job{

    private Integer job_Id;  //Ϊ������procedure�󶨣���procedure��ȡlocation��priority�Ȼ�����Ϣ
    private String name;  //�������ֻ������ţ�Name��
    private List<String> location; //�۲�Ŀ��λ�ã���Ԫ�飨�߶ȣ�γ�ȣ����ȣ���eg:��0,30.5_E,120.5_N��
    private Integer priority; //�������ȼ�: 1��ͣ�5���
    private Integer sensor; //����Ҫ��������غɴ��������ͣ�1-�ɼ��⡢2-SAR���ϳɿ׾��״��3-���⡢4-����ף�ֻѡһ�֣�
    private Integer resolution;  //����Գ���ķֱ���Ҫ��

    @JSONField(name="start_random", format="yyyy-MM-dd HH:mm:ss")
    private Date start_random;  //����������ʼʱ�䣨��startT~endT֮�䣩

    @JSONField(name="end_random", format="yyyy-MM-dd HH:mm:ss")
    private Date end_random;

    @JSONField(name="startT", format="yyyy-MM-dd HH:mm:ss")
    private Date startT; //������ɵ�������ʼʱ��

    @JSONField(name="endT", format="yyyy-MM-dd HH:mm:ss")
    private Date endT; //������ɵ��������ʱ��

    private Integer duration;  //������Ҫ�����С�۲�ʱ�䣬��λΪs

    private Boolean isUsed;  //���������Ƿ�ĳ��ʱ�䴰�ڽ���

    private Integer Sat_id;  //���Ǳ��

    private Integer TW_id;  //ʱ�䴰�ڱ��

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
