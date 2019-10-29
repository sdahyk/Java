package hyk.sda.cn;

import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableItem;

public class MyTimerTask extends TimerTask {
	private String text;
	Timer timer;

	public MyTimerTask(String text, Timer timer) {
		this.text = text;
		this.timer = timer;
	}

	@Override
	public void run() {
		String temp = MainWindow.execResultMap.get(text);
		if (temp != "") {
			// "执行完成"
			/*
			 * Exception in thread "Thread-0" org.eclipse.swt.SWTException: Invalid thread
			 * access //
			 * 在写用户界面是通常画静态界面放在一个类，称为UI线程。而某个按钮或事件触发的另一个事件发生时，起一个线程去处理这个事件，这个线程叫非UI线程。
			 * 如果你在非UI线程里面对界面的变量，如：某个按钮、text等，就会报上面的错误。因为程序找不到对应的Display。
			 * 如果您了解Display功能就会很容易理解这个问题，Display的作用就是负责enent loop, font,color,UI线程和其他线程的通信。
			 * // 解决方法： 在非UI线程操作操作UI线程的地方加上下面的代码
			 */
			Display.getDefault().syncExec(new Runnable() {

				public void run() {

					// 需要操作的ui线程的代码
					int itemsNum = MainWindow.tableScriptFiles.getItemCount();
					for (int i = 0; i < itemsNum; i++) {
						TableItem item = MainWindow.tableScriptFiles.getItem(i);
						if (item.getChecked()) {
							if (item.getText(0) == text) {
								item.setText(4, MainWindow.execResultMap.get(text));
							}
						}
					}
					MainWindow.execResultMap.remove(text);
					MainWindow.executor.shutdown();
					timer.cancel();
				}

			});
		}
	}

}
