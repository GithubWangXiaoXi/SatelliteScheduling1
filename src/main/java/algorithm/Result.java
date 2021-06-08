package algorithm;

import java.util.Date;

public class Result {

    private Integer SAT_id;
    private Integer TW_id;
    private Integer job_id;
    private Date job_startT;
    private Date job_endT;

    public Integer getTW_id() {
        return TW_id;
    }

    public void setTW_id(Integer TW_id) {
        this.TW_id = TW_id;
    }

    public Integer getSAT_id() {
        return SAT_id;
    }

    public void setSAT_id(Integer SAT_id) {
        this.SAT_id = SAT_id;
    }

    public Integer getJob_id() {
        return job_id;
    }

    public void setJob_id(Integer job_id) {
        this.job_id = job_id;
    }

    public Date getJob_startT() {
        return job_startT;
    }

    public void setJob_startT(Date job_startT) {
        this.job_startT = job_startT;
    }

    public Date getJob_endT() {
        return job_endT;
    }

    public void setJob_endT(Date job_endT) {
        this.job_endT = job_endT;
    }

    @Override
    public String toString() {
        return "Result{" +
                "SAT_id=" + SAT_id +
                ", TW_id=" + TW_id +
                ", job_id=" + job_id +
                ", job_startT=" + job_startT +
                ", job_endT=" + job_endT +
                '}';
    }

    //基于优先级的任务完全率


    //基于任务数的任务完成率

    //基于时间的资源利用率

    //基于分辨率的资源利用率

}
