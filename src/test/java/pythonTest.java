import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class pythonTest {

    @Test
    public void executePy() throws IOException, InterruptedException {

        //参考 https://www.cnblogs.com/tohxyblog/p/6501396.html
        // Windows下： Runtime.getRuntime().exec(new String[]{ "cmd", "/c", cmds});
        Process pr;
        pr = Runtime.getRuntime().exec(new String[]{"cmd", "/c", "python E:\\研究生任务\\选星算法\\SatelliteScheduling\\drawGantt.py E:\\研究生任务\\选星算法\\SatelliteScheduling\\src\\main\\resources\\jobs.json E:\\研究生任务\\选星算法\\SatelliteScheduling\\src\\main\\resources\\satellites.json"});
        pr.waitFor();
    }
}
