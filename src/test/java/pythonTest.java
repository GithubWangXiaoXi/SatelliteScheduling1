import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class pythonTest {

    @Test
    public void executePy() throws IOException, InterruptedException {

        //�ο� https://www.cnblogs.com/tohxyblog/p/6501396.html
        // Windows�£� Runtime.getRuntime().exec(new String[]{ "cmd", "/c", cmds});
        Process pr;
        pr = Runtime.getRuntime().exec(new String[]{"cmd", "/c", "python E:\\�о�������\\ѡ���㷨\\SatelliteScheduling\\drawGantt.py E:\\�о�������\\ѡ���㷨\\SatelliteScheduling\\src\\main\\resources\\jobs.json E:\\�о�������\\ѡ���㷨\\SatelliteScheduling\\src\\main\\resources\\satellites.json"});
        pr.waitFor();
    }
}
