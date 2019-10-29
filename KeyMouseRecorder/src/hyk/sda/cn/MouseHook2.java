package hyk.sda.cn;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import org.apache.logging.log4j.Level;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinDef.HMODULE;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.POINT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinUser.MSG;

public class MouseHook2 implements Runnable {
	private WinUser.HHOOK hhk;
	// ���ӻص�����
	private WinUser.LowLevelKeyboardProc mouseProc = new WinUser.LowLevelKeyboardProc() {

		@Override
		public LRESULT callback(int nCode, WPARAM wParam, WinUser.KBDLLHOOKSTRUCT event) {
			// �����궯��ֵ��522�ƶ� 516�Ҽ����� 513������� 514���̧��
			if (nCode >= 0) {
				// ����Ҽ������˳�
				if (wParam.intValue() == 516) {
					MainWindow.recordSign = false;
					Thread.currentThread().interrupt();
				}
				// ���̧���¼����
				if (wParam.intValue() == 514) {
					POINT pt = new POINT();
					User32.INSTANCE.GetCursorPos(pt);
//					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					String fileName = df.format(new Date());
//					��¼���ϴε�����������֮��ļ��
					Instant currentTime = Instant.now();
					long interval = Duration.between(MainWindow.lastTime, currentTime).toMillis();
					MainWindow.lastTime = currentTime;
					BufferedWriter bw = null;
					File file = null;
					try {
//						file = new File(sbConf.toString().substring(26),fileName + ".KMS");
						file = new File("d:\\backup\\" + fileName + ".kms");
						if (!file.exists())
							file.createNewFile();
						bw = new BufferedWriter(new FileWriter(file, true));
						bw.write("Wait(" + interval + ")\n");
						bw.write("Click(" + pt.x + "," + pt.y + ")\n");
						bw.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}

				}
			}
			return User32.INSTANCE.CallNextHookEx(hhk, nCode, wParam, null);
		}

	};

	@Override
	public void run() {
		// ��װ����
		System.out.println("Mouse Hook On!");
		log.hyk.sda.cn.Log4J2.loggerMy.log(Level.INFO, "Mouse Hook On!");

		HMODULE hMod = Kernel32.INSTANCE.GetModuleHandle(null);
		hhk = User32.INSTANCE.SetWindowsHookEx(User32.WH_MOUSE_LL, mouseProc, hMod, 0);

		while (!Thread.currentThread().isInterrupted()) { // ������������ͨ���ж��жϱ�־���˳�
//			int result = 0;
			MSG msg = new MSG();
			User32.INSTANCE.PeekMessage(msg, null, 0, 0, 0);//�����GetMessage()��PeekMessage()�ܹ���ѭ������ִ����ȥ�����滻��ԭ�����£�
			/*
			 * GetMessage �����������Ǵӵ�ǰ�̵߳���Ϣ�������ȡһ����Ϣ������ MSG �ṹ �С�
			 * �ú���ֻ�ܻ�ȡ�����̵߳���Ϣ�����ܻ�������̵߳���Ϣ���ɹ���ȡ��Ϣ���߳̽�����Ϣ������ɾ������Ϣ�� ʹ�� GetMessage
			 * �����������Ϣ����Ϊ�գ�������һֱ�ȴ�ֱ������Ϣ�������з���ֵ�����ϣ���������̷��أ������Ƿ��ȡ��Ϣ������ʹ�� PeekMessage ������
			 */
			//			result = User32.INSTANCE.GetMessage(msg, null, 0, 0);
			// �����Է��֣�����ִ�е��˴��޷���������ִ�С�ԭ������������ӹ��ܿ��Լ����������С�
			// ֻ�ܿ���callback������ֱ�ӵ���setHookOff()�Ƴ����ӣ���ϵͳ����Ϊrun()δִ����ɣ���˼�ʹ�����˳���Ҳ���ͷ���Դ
			// ����IDE����console��ǿ��ֹͣ
//			if (result == -1) {
//				break;
//			} else {
//				User32.INSTANCE.TranslateMessage(msg);
				User32.INSTANCE.DispatchMessage(msg);
//			}
//					Thread.sleep(5 * 1000);
// �����sleep�����������̣���Ҫ����InterruptedException�ж��쳣���˳�
		}
		
		//ж�ع��ӡ�run��ɣ��߳̽�����
		setHookOff();
	}

	protected void setHookOff() {
		User32.INSTANCE.UnhookWindowsHookEx(hhk);
		System.out.println("Mouse Hook Off!");
		log.hyk.sda.cn.Log4J2.loggerMy.log(Level.INFO, "Mouse Hook Off!");
	}

}
