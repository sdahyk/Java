package learning.hyk.sda.cn;

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
import com.sun.jna.platform.win32.WinDef.HMODULE;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.WPARAM;

import hyk.sda.cn.MainWindow;

import com.sun.jna.platform.win32.WinUser;

public class KeyboardHook implements Runnable {

	private WinUser.HHOOK hhk;
	// ���ӻص�����
	private WinUser.LowLevelKeyboardProc keyboardProc = new WinUser.LowLevelKeyboardProc() {

		@Override
		public LRESULT callback(int nCode, WPARAM wParam, WinUser.KBDLLHOOKSTRUCT event) {
			// �������ֵ Alt����wParam=260�����£�257��̧��vkCode=164
			// �������ֵ ��������wParam=256�����£�257��̧��vkCode=���Լ�ֵ
			if (nCode >= 0) {
				// F12��̧���˳�
				if (wParam.intValue() == 257) {
					if (event.vkCode == 123) {
						MainWindow.recordSign = false;
						setHookOff();
					} else {
						SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
						String fileName = df.format(new Date());
						BufferedWriter bw = null;
						File file = null;
						try {
//							file = new File(sbConf.toString().substring(26),fileName + ".KMS");
							file = new File("d:\\backup\\" + fileName + ".kms");
							if (!file.exists())
								file.createNewFile();
//							��¼���ϴε�����������֮��ļ��
							Instant currentTime = Instant.now();
							long interval = Duration.between(MainWindow.lastTime, currentTime).toMillis();
							MainWindow.lastTime = currentTime;
							bw = new BufferedWriter(new FileWriter(file, true));
							bw.write("Wait(" + interval + ")\n");
							bw.write("Key(#" + event.vkCode + ")\n");
							bw.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
			return User32.INSTANCE.CallNextHookEx(hhk, nCode, wParam, null);
		}
	};

	@Override
	public void run() {
		setHookOn();
	}

	// ��װ����
	private void setHookOn() {
		System.out.println("Keyboard Hook On!");
		log.hyk.sda.cn.Log4J2.loggerMy.log(Level.INFO, "Keyboard Hook On!");

		HMODULE hMod = Kernel32.INSTANCE.GetModuleHandle(null);
		hhk = User32.INSTANCE.SetWindowsHookEx(User32.WH_KEYBOARD_LL, keyboardProc, hMod, 0);
		WinUser.MSG msg = new WinUser.MSG();
		while ((User32.INSTANCE.GetMessage(msg, null, 0, 0)) != 0) {
			if (!MainWindow.recordSign) {
				break;
			} else {
				User32.INSTANCE.TranslateMessage(msg);
				User32.INSTANCE.DispatchMessage(msg);
			}
		}
	}

	// �Ƴ����Ӳ��˳�
	public void setHookOff() {
		User32.INSTANCE.UnhookWindowsHookEx(hhk);
		System.out.println("Keyboard Hook Off!");
		log.hyk.sda.cn.Log4J2.loggerMy.log(Level.INFO, "Keyboard Hook Off!");
	}

}
