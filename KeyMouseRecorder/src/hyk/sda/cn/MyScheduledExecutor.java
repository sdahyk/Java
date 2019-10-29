package hyk.sda.cn;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Level;

import log.hyk.sda.cn.Log4J2;

public class MyScheduledExecutor implements Runnable {
	private String scriptFile;
	private String result;

	public MyScheduledExecutor(String sName) {
		this.scriptFile = sName;
		this.result = "";
	}

	@Override
	public void run() {
		System.out.println(scriptFile + " is running!");
		log.hyk.sda.cn.Log4J2.loggerMy.log(Level.INFO, scriptFile + " is running!");
		if (playScriptFile(scriptFile)) {
			result = "Played Successfully";
		} else {
			result = "Play Failed";
		}
		MainWindow.execResultMap.put(scriptFile, result);
		System.out.println(result);
		log.hyk.sda.cn.Log4J2.loggerMy.log(Level.INFO, result);
	}

	protected boolean playScriptFile(String scriptFile) {
		// 读取scriptFile文件
		Boolean result = true;
		FileReader reader;
		Map<Integer, Integer> keyMap = new HashMap<Integer, Integer>();
		keyMap.put(27, 27);
		keyMap.put(112, 112);
		keyMap.put(113, 113);
		keyMap.put(114, 114);
		keyMap.put(115, 115);
		keyMap.put(116, 116);
		keyMap.put(117, 117);
		keyMap.put(118, 118);
		keyMap.put(119, 119);
		keyMap.put(120, 120);
		keyMap.put(121, 121);
		keyMap.put(122, 122);
		keyMap.put(123, 123);
		keyMap.put(48, 48);
		keyMap.put(49, 49);
		keyMap.put(50, 50);
		keyMap.put(51, 51);
		keyMap.put(52, 52);
		keyMap.put(53, 53);
		keyMap.put(54, 54);
		keyMap.put(55, 55);
		keyMap.put(56, 56);
		keyMap.put(57, 57);
		keyMap.put(65, 65);
		keyMap.put(66, 66);
		keyMap.put(67, 67);
		keyMap.put(68, 68);
		keyMap.put(69, 69);
		keyMap.put(70, 70);
		keyMap.put(71, 71);
		keyMap.put(72, 72);
		keyMap.put(73, 73);
		keyMap.put(74, 74);
		keyMap.put(75, 75);
		keyMap.put(76, 76);
		keyMap.put(77, 77);
		keyMap.put(78, 78);
		keyMap.put(79, 79);
		keyMap.put(80, 80);
		keyMap.put(81, 81);
		keyMap.put(82, 82);
		keyMap.put(83, 83);
		keyMap.put(84, 84);
		keyMap.put(85, 85);
		keyMap.put(86, 86);
		keyMap.put(87, 87);
		keyMap.put(88, 88);
		keyMap.put(89, 89);
		keyMap.put(90, 90);
		keyMap.put(144, 144);
		keyMap.put(96, 96);
		keyMap.put(97, 97);
		keyMap.put(98, 98);
		keyMap.put(99, 99);
		keyMap.put(100, 100);
		keyMap.put(101, 101);
		keyMap.put(102, 102);
		keyMap.put(103, 103);
		keyMap.put(104, 104);
		keyMap.put(105, 105);
		keyMap.put(107, 107);
		keyMap.put(109, 109);
		keyMap.put(106, 106);
		keyMap.put(111, 111);
		keyMap.put(110, 110);
		keyMap.put(13, 10);
		keyMap.put(45, 155);
		keyMap.put(46, 127);
		keyMap.put(33, 33);
		keyMap.put(34, 34);
		keyMap.put(35, 35);
		keyMap.put(36, 36);
		keyMap.put(37, 37);
		keyMap.put(38, 38);
		keyMap.put(39, 39);
		keyMap.put(40, 40);
		keyMap.put(9, 9);
		keyMap.put(20, 20);
		keyMap.put(160, 16);
		keyMap.put(161, 16);
		keyMap.put(162, 17);
		keyMap.put(163, 17);
		keyMap.put(91, 524);
		keyMap.put(92, 524);
		keyMap.put(164, 18);
		keyMap.put(165, 18);
		keyMap.put(189, 45);
		keyMap.put(187, 61);
		keyMap.put(8, 8);
		keyMap.put(219, 91);
		keyMap.put(221, 93);
		keyMap.put(220, 92);
		keyMap.put(186, 59);
		keyMap.put(222, 222);
		keyMap.put(188, 44);
		keyMap.put(190, 46);
		keyMap.put(191, 47);
		keyMap.put(13, 10);
		keyMap.put(32, 32);
		try {
			reader = new FileReader(scriptFile);
			BufferedReader br = new BufferedReader(reader);
			String str = null, strCommand = null;
			Robot robot = new Robot();
//			robot.setAutoDelay(300);
			while ((str = br.readLine()) != null) {
				// 执行每一行
				// 判断括号之前的command
				strCommand = str.substring(0, str.indexOf("("));
				switch (strCommand) {
				case "Click":
					String strX = null, strY = null;
					strX = str.substring(str.indexOf("(") + 1, str.indexOf(","));
					strY = str.substring(str.indexOf(",") + 1, str.indexOf(")"));
					robot.mouseMove(Integer.valueOf(strX), Integer.valueOf(strY));
					robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
					robot.mouseRelease(InputEvent.BUTTON1_MASK);
					break;
				case "DoubleClick":
					String strXd = null, strYd = null;
					strXd = str.substring(str.indexOf("(") + 1, str.indexOf(","));
					strYd = str.substring(str.indexOf(",") + 1, str.indexOf(")"));
					robot.mouseMove(Integer.valueOf(strXd), Integer.valueOf(strYd));
					robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
					robot.mouseRelease(InputEvent.BUTTON1_MASK);
					robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
					robot.mouseRelease(InputEvent.BUTTON1_MASK);
					break;
				case "Key":
					// 转换为大写后，运行正常, 否则传的键不对。
					String strKeys = str.substring(str.indexOf("(") + 1, str.indexOf(")")).toUpperCase();
					// 如果是#开头，意味着是自动记录的vCode，转换成keyCode，然后KeyPress
					if (strKeys.startsWith("#")) {
						int k = 0;
						try {// vCode-keycode对应表里如果没有，k为null，会抛出NullPointer异常
							k = keyMap.get(Integer.valueOf(strKeys.substring(1)));
						} catch (Exception e) {
							Log4J2.loggerMy
									.error("没有找到#" + Integer.valueOf(strKeys.substring(1)) + "对应的keycode");
						}
						if (k > 0) {
							robot.keyPress(k);
							robot.keyRelease(k);
						}
					} else {
						// 手工输入的字符串
						for (int i = 0; i < strKeys.length(); i++) {
							robot.keyPress(strKeys.codePointAt(i));
							robot.keyRelease(strKeys.codePointAt(i));
						}
					}
					break;
				case "Wait":
					int delay = Integer.valueOf(str.substring(str.indexOf("(") + 1, str.indexOf(")")).toUpperCase());
					robot.delay(delay);
					break;
				default:// Unsupported Command
					break;
				}
			}
			br.close();
			reader.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			result = false;
		} catch (IOException e) {
			e.printStackTrace();
			result = false;
		} catch (AWTException e1) {
			e1.printStackTrace();
			result = false;
		}
		return result;
	}

	public String getScriptFile() {
		return scriptFile;
	}

	public void setScriptFile(String scriptFile) {
		this.scriptFile = scriptFile;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

}
