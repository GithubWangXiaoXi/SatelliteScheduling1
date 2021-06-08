package pojo;

import util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**һ������Satellite��Ӧ���ʱ�䴰TimeWindows**/
public class Satellite implements Cloneable{

    private Integer sat_id; //����id��Ϊ�˺�timeWindows��
    private String name;    //��������
    private Set<Integer> sensors;  //�����غɴ���������, 1-�ɼ��⡢2-SAR���ϳɿ׾��״��3-���⡢4-����ף������
    private Integer MR;  //�����غɴ�������С�ֱ��ʣ�Minimum Resolution��,��λΪm
    private Integer MTI;  //�����غɴ��������ι۲����С׼��ʱ�䣨Minimum Time Interval)
    private List<TimeWindow> timeWindows;  //ÿ�����ǶԹ۲�Ŀ��ɼ���ʱ�䴰������

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
