package pojo;

import util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**一个卫星Satellite对应多个时间窗TimeWindows**/
public class Satellite implements Cloneable{

    private Integer sat_id; //卫星id，为了和timeWindows绑定
    private String name;    //卫星名字
    private Set<Integer> sensors;  //星上载荷传感器类型, 1-可见光、2-SAR（合成孔径雷达）、3-红外、4-多光谱（多个）
    private Integer MR;  //星上载荷传感器最小分辨率（Minimum Resolution）,单位为m
    private Integer MTI;  //向上载荷传感器两次观测的最小准备时间（Minimum Time Interval)
    private List<TimeWindow> timeWindows;  //每个卫星对观测目标可见的时间窗口序列

    public Satellite() {

    }

    public Satellite(String name, Set<Integer> sensors, Integer MR, Integer MTI, List<TimeWindow> timeWindows) {
        this.name = name;
        this.sensors = sensors;
        this.MR = MR;
        this.MTI = MTI;
        this.timeWindows = timeWindows;
    }

    public Integer getSat_id() {
        return sat_id;
    }

    public void setSat_id(Integer sat_id) {
        this.sat_id = sat_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Integer> getSensors() {
        return sensors;
    }

    public void setSensors(Set<Integer> sensors) {
        this.sensors = sensors;
    }

    public List<TimeWindow> getTimeWindows() {
        return timeWindows;
    }

    public void setTimeWindows(List<TimeWindow> timeWindows) {
        this.timeWindows = timeWindows;
    }

    public Integer getMR() {
        return MR;
    }

    public void setMR(Integer MR) {
        this.MR = MR;
    }

    public Integer getMTI() {
        return MTI;
    }

    public void setMTI(Integer MTI) {
        this.MTI = MTI;
    }

    @Override
    public String toString() {
        return "Satellite{" +
                "sat_id=" + sat_id +
                ", name='" + name + '\'' +
                ", sensors=" + sensors +
                ", MR=" + MR +
                ", MTI=" + MTI +
                ", timeWindows=" + timeWindows +
                '}';
    }
}
