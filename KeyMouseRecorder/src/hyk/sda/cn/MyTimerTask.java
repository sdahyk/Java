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
			// "ִ�����"
			/*
			 * Exception in thread "Thread-0" org.eclipse.swt.SWTException: Invalid thread
			 * access //
			 * ��д�û�������ͨ������̬�������һ���࣬��ΪUI�̡߳���ĳ����ť���¼���������һ���¼�����ʱ����һ���߳�ȥ��������¼�������߳̽з�UI�̡߳�
			 * ������ڷ�UI�߳�����Խ���ı������磺ĳ����ť��text�ȣ��ͻᱨ����Ĵ�����Ϊ�����Ҳ�����Ӧ��Display��
			 * ������˽�Display���ܾͻ���������������⣬Display�����þ��Ǹ���enent loop, font,color,UI�̺߳������̵߳�ͨ�š�
			 * // ��������� �ڷ�UI�̲߳�������UI�̵߳ĵط���������Ĵ���
			 */
			Display.getDefault().syncExec(new Runnable() {

				public void run() {

					// ��Ҫ������ui�̵߳Ĵ���
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
