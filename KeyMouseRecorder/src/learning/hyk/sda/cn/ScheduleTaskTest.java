package learning.hyk.sda.cn;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import hyk.sda.cn.MyScheduledExecutor;

public class ScheduleTaskTest {

	public static void main(String[] args) {
		ScheduledExecutorService service = Executors.newScheduledThreadPool(10);
		long initialDelay = 1;
        long period = 1;
        // �����ڿ�ʼ1����֮��ÿ��1����ִ��һ��job1
        service.scheduleAtFixedRate(new MyScheduledExecutor("job1"), initialDelay, period, TimeUnit.SECONDS);
        
        // �����ڿ�ʼ2����֮��ÿ��2����ִ��һ��job2
        service.scheduleWithFixedDelay(new MyScheduledExecutor("job2"), initialDelay, period, TimeUnit.SECONDS);
	}

}
