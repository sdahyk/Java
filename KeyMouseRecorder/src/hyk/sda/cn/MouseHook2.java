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
	// 钩子回调函数
	private WinUser.LowLevelKeyboardProc mouseProc = new WinUser.LowLevelKeyboardProc() {

		@Override
		public LRESULT callback(int nCode, WPARAM wParam, WinUser.KBDLLHOOKSTRUCT event) {
			// 输出鼠标动作值，522移动 516右键按下 513左键按下 514左键抬起
			if (nCode >= 0) {
				// 鼠标右键按下退出
				if (wParam.intValue() == 516) {
					MainWindow.recordSign = false;
					Thread.currentThread().interrupt();
				}
				// 左键抬起记录坐标
				if (wParam.intValue() == 514) {
					POINT pt = new POINT();
					User32.INSTANCE.GetCursorPos(pt);
//					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					String fileName = df.format(new Date());
//					记录与上次点击或键盘输入之间的间隔
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
		// 安装钩子
		System.out.println("Mouse Hook On!");
		log.hyk.sda.cn.Log4J2.loggerMy.log(Level.INFO, "Mouse Hook On!");

		HMODULE hMod = Kernel32.INSTANCE.GetModuleHandle(null);
		hhk = User32.INSTANCE.SetWindowsHookEx(User32.WH_MOUSE_LL, mouseProc, hMod, 0);

		while (!Thread.currentThread().isInterrupted()) { // 非阻塞过程中通过判断中断标志来退出
//			int result = 0;
			MSG msg = new MSG();
			User32.INSTANCE.PeekMessage(msg, null, 0, 0, 0);//相比与GetMessage()，PeekMessage()能够让循环正常执行下去。故替换。原因如下：
			/*
			 * GetMessage 函数的作用是从当前线程的消息队列里获取一个消息并填入 MSG 结构 中。
			 * 该函数只能获取调用线程的消息，不能获得其他线程的消息。成功获取消息后，线程将从消息队列中删除该消息。 使用 GetMessage
			 * 函数，如果消息队列为空，函数会一直等待直到有消息到来才有返回值。如果希望函数立刻返回（无论是否获取消息），请使用 PeekMessage 函数。
			 */
			//			result = User32.INSTANCE.GetMessage(msg, null, 0, 0);
			// 经测试发现，代码执行到此处无法继续往下执行。原因不清楚，但钩子功能可以继续正常运行。
			// 只能靠在callback方法中直接调用setHookOff()移除钩子，但系统会认为run()未执行完成，因此即使程序退出，也不释放资源
			// 需在IDE环境console中强行停止
//			if (result == -1) {
//				break;
//			} else {
//				User32.INSTANCE.TranslateMessage(msg);
				User32.INSTANCE.DispatchMessage(msg);
//			}
//					Thread.sleep(5 * 1000);
// 如果有sleep这种阻塞过程，需要捕获InterruptedException中断异常来退出
		}
		
		//卸载钩子。run完成，线程结束。
		setHookOff();
	}

	protected void setHookOff() {
		User32.INSTANCE.UnhookWindowsHookEx(hhk);
		System.out.println("Mouse Hook Off!");
		log.hyk.sda.cn.Log4J2.loggerMy.log(Level.INFO, "Mouse Hook Off!");
	}

}
