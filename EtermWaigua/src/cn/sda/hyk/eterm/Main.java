package cn.sda.hyk.eterm;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;

import org.eclipse.swt.widgets.Button;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wb.swt.SWTResourceManager;

public class Main {

//	private static WinUser.INPUT input = new WinUser.INPUT();
	protected Shell shlEterm;
	private Text textInput;
	private Text textEtermTitle;
	private Table tableFlights;
	private TableColumn tblclmnFlightfrequency;
	private TableColumn tblclmnStatus;
	private TableItem tableItem;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Main window = new Main();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shlEterm.open();
		shlEterm.layout();
		while (!shlEterm.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlEterm = new Shell();
		shlEterm.setSize(648, 578);
		shlEterm.setText("ETerm窗口自动录入工具");

		textInput = new Text(shlEterm, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL);
		textInput.setToolTipText("请输入待发送到Eterm窗口的指令文本，每条指令一行");
		textInput.setText("AV TNACAN/+\r\nAV SHATNA/+");
		textInput.setBounds(10, 10, 612, 208);

		textEtermTitle = new Text(shlEterm, SWT.BORDER);
		textEtermTitle.setToolTipText("\u8BF7\u786E\u8BA4Eterm\u7A97\u53E3\u7684\u6807\u9898\u6587\u672C");
		textEtermTitle.setText("eTerm 3 -pek3.eterm.com.cn (Direct) - [SESSION 1]");
		textEtermTitle.setBounds(10, 224, 404, 23);

		Button btnTextSendToEterm = new Button(shlEterm, SWT.NONE);
		btnTextSendToEterm.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// 读取文本，并分行输入Eterm...
				String inputString = textInput.getText();
				boolean hasMoreLine = true;
				int i;
				while (hasMoreLine) {
					if (inputString.contains("\r\n")) {
						// 取回车换行的位置
						i = inputString.indexOf("\r\n");
						// 向eTerm窗口输入字符串, 并穿入。此处需要判断返回值，如果执行中出错误就终止循环
						if (sendTextToEterm(inputString.substring(0, i), true) == -1)
							break;
						// 取剩余的字符串
						inputString = inputString.substring(i + 2);
					} else {
						// 向eTerm窗口输入最后一行字符串以及结束标识
						sendTextToEterm(inputString, true);
						hasMoreLine = false;
						sendTextToEterm("######### AutoInput End #########", false);
					}
				}
			}
		});
		btnTextSendToEterm.setBounds(467, 222, 155, 27);
		btnTextSendToEterm.setText("向ETerm发送上述指令");

		tableFlights = new Table(shlEterm, SWT.BORDER | SWT.FULL_SELECTION);
		tableFlights.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if (e.button == 3) {// 鼠标右键菜单
					Menu popMenu = new Menu(shlEterm, SWT.POP_UP);
					MenuItem menuItemEdit = new MenuItem(popMenu, SWT.PUSH);
					menuItemEdit.setText("删除当前行");
					menuItemEdit.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							tableFlights.remove(tableFlights.getSelectionIndex());
						}
					});
					MenuItem menuItemClear = new MenuItem(popMenu, SWT.PUSH);
					menuItemClear.setText("清空所有数据");
					menuItemClear.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							tableFlights.removeAll();
						}
					});
					popMenu.setVisible(true);
				}
			}
		});
		tableFlights.setBounds(10, 286, 612, 210);
		tableFlights.setHeaderVisible(true);
		tableFlights.setLinesVisible(true);

		TableColumn tblclmnFlightnumber = new TableColumn(tableFlights, SWT.NONE);
		tblclmnFlightnumber.setWidth(100);
		tblclmnFlightnumber.setText("FlightNumber");

		tblclmnFlightfrequency = new TableColumn(tableFlights, SWT.NONE);
		tblclmnFlightfrequency.setWidth(114);
		tblclmnFlightfrequency.setText("FlightFrequency");

		TableColumn tblclmnBeginDate = new TableColumn(tableFlights, SWT.NONE);
		tblclmnBeginDate.setWidth(100);
		tblclmnBeginDate.setText("BeginDate");

		TableColumn tblclmnEndDate = new TableColumn(tableFlights, SWT.NONE);
		tblclmnEndDate.setWidth(100);
		tblclmnEndDate.setText("EndDate");

		tblclmnStatus = new TableColumn(tableFlights, SWT.NONE);
		tblclmnStatus.setWidth(100);
		tblclmnStatus.setText("Status");

		tableItem = new TableItem(tableFlights, SWT.NONE);
		tableItem.setText("New TableItem");

		Label lblStatus = new Label(shlEterm, SWT.NONE);
		lblStatus.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		lblStatus.setBounds(161, 263, 461, 17);
		lblStatus.setText("New Label");

		Button btnImportExcel = new Button(shlEterm, SWT.NONE);
		btnImportExcel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {// 打开指定的excel文件，读取待执行的航班信息
				FileDialog fd = new FileDialog(shlEterm, SWT.OPEN);
				fd.setText("请选择要打开的Excel文件");
				fd.setFilterExtensions(new String[] { "*.*", "*.xls", "*.xlsx" });
				String selected = fd.open();
				if (selected != null) {
					File fileSelected = new File(selected);
					String version = (fileSelected.getName().endsWith(".xls") ? "version2003" : "version2007");
					try {
						// 清空历史数据
						tableFlights.removeAll();

						int i = readExcel(fileSelected, version);
						lblStatus.setText("成功读取了" + i + "条数据！");
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		btnImportExcel.setBounds(10, 253, 145, 27);
		btnImportExcel.setText("从Excel文件导入数据");

		Button radioButtonSingle = new Button(shlEterm, SWT.RADIO);
		radioButtonSingle.setSelection(true);
		radioButtonSingle.setBounds(10, 512, 97, 17);
		radioButtonSingle.setText("不拆分日期");

		Button radioButtonMultiple = new Button(shlEterm, SWT.RADIO);
		radioButtonMultiple.setBounds(110, 512, 74, 17);
		radioButtonMultiple.setText("拆分日期");

		Composite composite = new Composite(shlEterm, SWT.NONE);
		composite.setBounds(194, 507, 253, 27);

		Spinner spinner = new Spinner(composite, SWT.BORDER);
		spinner.setBounds(10, 0, 49, 23);
		spinner.setMinimum(1);

		Button radioButtonDay = new Button(composite, SWT.RADIO);
		radioButtonDay.setBounds(67, 3, 49, 17);
		radioButtonDay.setText("天");

		Button radioButtonWeek = new Button(composite, SWT.RADIO);
		radioButtonWeek.setBounds(122, 3, 41, 17);
		radioButtonWeek.setText("周");

		Button radioButtonMonth = new Button(composite, SWT.RADIO);
		radioButtonMonth.setSelection(true);
		radioButtonMonth.setBounds(169, 3, 49, 17);
		radioButtonMonth.setText("月");

		Button btnGenerateSRA = new Button(shlEterm, SWT.NONE);
		btnGenerateSRA.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {// 生成指令文本
				int ic = tableFlights.getItemCount();
				if (ic == 0)
					return;
				textInput.setText("");
				DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
				LocalDate ldBegin = null, ldEnd = null, ldMiddle = null;
				String strCommand;
				List<String> listSraCommand = new ArrayList<String>();
				for (int i = 0; i < ic; i++) {
					String strFlightNumber = tableFlights.getItem(i).getText(0);
					String strFrequency = tableFlights.getItem(i).getText(1);
					if (strFrequency.equals("1234567")) {
						strFrequency = "D";
					}
					String strBegin = tableFlights.getItem(i).getText(2);
					String strEnd = tableFlights.getItem(i).getText(3);
					ldBegin = LocalDate.parse(strBegin, df);
					ldEnd = LocalDate.parse(strEnd, df);
					// 根据是否拆分来处理结束日期
					if (radioButtonSingle.getSelection()) {// 不拆分
						strBegin = ldBegin.getDayOfMonth()
								+ ldBegin.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
								+ (ldBegin.getYear() - 2000);
						strEnd = ldEnd.getDayOfMonth()
								+ ldEnd.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
								+ (ldEnd.getYear() - 2000);
						strCommand = "SRA:" + strFlightNumber + "/" + strBegin + "/" + strEnd + "/" + strFrequency
								+ "\r\n";
//						textInput.setText(textInput.getText() + strCommand);
						listSraCommand.add(strCommand);
						tableFlights.getItem(i).setText(4, "生成1条指令");
					} else {// 拆分
						int commandCount = 0;
						int splitter = spinner.getSelection();
						if (radioButtonDay.getSelection()) {// 按天拆分
							while (true) {
								commandCount++;
								ldMiddle = ldBegin.plusDays(splitter);
								if (ldEnd.isBefore((ldMiddle))) {
									if (ldBegin.isBefore(ldEnd) || ldBegin.isEqual(ldEnd)) {
										strBegin = ldBegin.getDayOfMonth()
												+ ldBegin.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
												+ (ldBegin.getYear() - 2000);
										strEnd = ldEnd.getDayOfMonth()
												+ ldEnd.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
												+ (ldEnd.getYear() - 2000);
										strCommand = "SRA:" + strFlightNumber + "/" + strBegin + "/" + strEnd + "/"
												+ strFrequency + "\r\n";
//									textInput.setText(textInput.getText() + strCommand);
										listSraCommand.add(strCommand);
									}
									tableFlights.getItem(i).setText(4, "生成" + commandCount + "条指令");
									break;
								}
								strBegin = ldBegin.getDayOfMonth()
										+ ldBegin.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
										+ (ldBegin.getYear() - 2000);
								strEnd = ldMiddle.getDayOfMonth()
										+ ldMiddle.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
										+ (ldMiddle.getYear() - 2000);
								strCommand = "SRA:" + strFlightNumber + "/" + strBegin + "/" + strEnd + "/"
										+ strFrequency + "\r\n";
//								textInput.setText(textInput.getText() + strCommand);
								listSraCommand.add(strCommand);
								ldBegin = ldMiddle.plusDays(1);
							}
						} else if (radioButtonWeek.getSelection()) {// 按周拆分
							while (true) {
								commandCount++;
								ldMiddle = ldBegin.plusWeeks(splitter);
								if (ldEnd.isBefore((ldMiddle))) {
									if (ldBegin.isBefore(ldEnd) || ldBegin.isEqual(ldEnd)) {
										strBegin = ldBegin.getDayOfMonth()
												+ ldBegin.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
												+ (ldBegin.getYear() - 2000);
										strEnd = ldEnd.getDayOfMonth()
												+ ldEnd.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
												+ (ldEnd.getYear() - 2000);
										strCommand = "SRA:" + strFlightNumber + "/" + strBegin + "/" + strEnd + "/"
												+ strFrequency + "\r\n";
//									textInput.setText(textInput.getText() + strCommand);
										listSraCommand.add(strCommand);
									}
									tableFlights.getItem(i).setText(4, "生成" + commandCount + "条指令");
									break;
								}
								strBegin = ldBegin.getDayOfMonth()
										+ ldBegin.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
										+ (ldBegin.getYear() - 2000);
								strEnd = ldMiddle.getDayOfMonth()
										+ ldMiddle.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
										+ (ldMiddle.getYear() - 2000);
								strCommand = "SRA:" + strFlightNumber + "/" + strBegin + "/" + strEnd + "/"
										+ strFrequency + "\r\n";
//								textInput.setText(textInput.getText() + strCommand);
								listSraCommand.add(strCommand);
								ldBegin = ldMiddle.plusDays(1);
							}
						} else {// 按月拆分
							while (true) {
								commandCount++;
								ldMiddle = ldBegin.plusMonths(splitter);
								if (ldEnd.isBefore((ldMiddle))) {
									if (ldBegin.isBefore(ldEnd) || ldBegin.isEqual(ldEnd)) {
										strBegin = ldBegin.getDayOfMonth()
												+ ldBegin.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
												+ (ldBegin.getYear() - 2000);
										strEnd = ldEnd.getDayOfMonth()
												+ ldEnd.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
												+ (ldEnd.getYear() - 2000);
										strCommand = "SRA:" + strFlightNumber + "/" + strBegin + "/" + strEnd + "/"
												+ strFrequency + "\r\n";
//									textInput.setText(textInput.getText() + strCommand);
										listSraCommand.add(strCommand);
									}
									tableFlights.getItem(i).setText(4, "生成" + commandCount + "条指令");
									break;
								}
								strBegin = ldBegin.getDayOfMonth()
										+ ldBegin.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
										+ (ldBegin.getYear() - 2000);
								strEnd = ldMiddle.getDayOfMonth()
										+ ldMiddle.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
										+ (ldMiddle.getYear() - 2000);
								strCommand = "SRA:" + strFlightNumber + "/" + strBegin + "/" + strEnd + "/"
										+ strFrequency + "\r\n";
//								textInput.setText(textInput.getText() + strCommand);
								listSraCommand.add(strCommand);
								ldBegin = ldMiddle.plusDays(1);
							}
						}
					}
				}
				// 去除完全相同的重复指令
				int irepeat = 0;
				for (int i = 0; i < listSraCommand.size() - 1; i++) {
					for (int j = listSraCommand.size() - 1; j > i; j--) {
						if (listSraCommand.get(j).equals(listSraCommand.get(i))) {
							System.out.println("发现重复记录，删除：" + listSraCommand.get(j));
							listSraCommand.remove(j);
							irepeat++;
						}
					}
				}
				lblStatus.setText("去除了" + irepeat + "条重复数据。保留了" + listSraCommand.size() + "条指令待发送至Eterm。");
				String result = "";
				for (String string : listSraCommand) {
					result += string;
				}
				textInput.setText(result);
			}
		});
		btnGenerateSRA.setBounds(467, 502, 155, 27);
		btnGenerateSRA.setText("生成SRA指令文本");

	}

	/**
	 * 向指定的窗口发送字符串    
	 * 
	 * @param strText  待发送的文本内容
	 * @param transmit 发送文本之后，是否发送F12键（是否执行命令）
	 * @return -1 发生错误，不要再继续了
	 * @return 0 成功执行
	 * @author hanyk
	 */
	private int sendTextToEterm(String strText, boolean transmit) {
		// 第一个参数是Windows窗体的窗体类，第二个参数是窗体的标题。
		HWND hwnd = User32.INSTANCE.FindWindow(null, textEtermTitle.getText());
		if (hwnd == null) {
			MessageBox msgBox = new MessageBox(shlEterm, SWT.ICON_INFORMATION | SWT.OK);
			msgBox.setMessage("未找到指定的Eterm窗口，请核实Eterm窗口标题文本。");
			msgBox.setText("Information");
			msgBox.open();
			return -1;
		} else {
			// 找到了eTerm窗口
			User32.INSTANCE.ShowWindow(hwnd, 9); // SW_RESTORE
			User32.INSTANCE.SetForegroundWindow(hwnd); // bring to front
			try {
				Robot robot = new Robot();

				// 先发一个三角号：键盘上的ESC，以便后续指令执行
				robot.keyPress(KeyEvent.VK_ESCAPE);
				robot.keyRelease(KeyEvent.VK_ESCAPE);

				// 找出每个字符对应的KeyCode
				strText = strText.toUpperCase();
				Map<String, Integer> keyMap = new HashMap<String, Integer>();
				keyMap.put("0", KeyEvent.VK_0);
				keyMap.put("1", KeyEvent.VK_1);
				keyMap.put("2", KeyEvent.VK_2);
				keyMap.put("3", KeyEvent.VK_3);
				keyMap.put("4", KeyEvent.VK_4);
				keyMap.put("5", KeyEvent.VK_5);
				keyMap.put("6", KeyEvent.VK_6);
				keyMap.put("7", KeyEvent.VK_7);
				keyMap.put("8", KeyEvent.VK_8);
				keyMap.put("9", KeyEvent.VK_9);
				keyMap.put("A", KeyEvent.VK_A);
				keyMap.put("B", KeyEvent.VK_B);
				keyMap.put("C", KeyEvent.VK_C);
				keyMap.put("D", KeyEvent.VK_D);
				keyMap.put("E", KeyEvent.VK_E);
				keyMap.put("F", KeyEvent.VK_F);
				keyMap.put("G", KeyEvent.VK_G);
				keyMap.put("H", KeyEvent.VK_H);
				keyMap.put("I", KeyEvent.VK_I);
				keyMap.put("J", KeyEvent.VK_J);
				keyMap.put("K", KeyEvent.VK_K);
				keyMap.put("L", KeyEvent.VK_L);
				keyMap.put("M", KeyEvent.VK_M);
				keyMap.put("N", KeyEvent.VK_N);
				keyMap.put("O", KeyEvent.VK_O);
				keyMap.put("P", KeyEvent.VK_P);
				keyMap.put("Q", KeyEvent.VK_Q);
				keyMap.put("R", KeyEvent.VK_R);
				keyMap.put("S", KeyEvent.VK_S);
				keyMap.put("T", KeyEvent.VK_T);
				keyMap.put("U", KeyEvent.VK_U);
				keyMap.put("V", KeyEvent.VK_V);
				keyMap.put("W", KeyEvent.VK_W);
				keyMap.put("X", KeyEvent.VK_X);
				keyMap.put("Y", KeyEvent.VK_Y);
				keyMap.put("Z", KeyEvent.VK_Z);
				keyMap.put("@", KeyEvent.VK_AT);
				keyMap.put("/", KeyEvent.VK_SLASH);
				keyMap.put(" ", KeyEvent.VK_SPACE);
				keyMap.put(":", KeyEvent.VK_COLON);
				keyMap.put("+", KeyEvent.VK_PLUS);
				keyMap.put("#", KeyEvent.VK_NUMBER_SIGN);
				for (int i = 0; i < strText.length(); i++) {
					String k = strText.substring(i, i + 1);
					int ik = keyMap.get(k);
					switch (ik) {
					case KeyEvent.VK_COLON:
						robot.keyPress(KeyEvent.VK_SHIFT);
						robot.keyPress(KeyEvent.VK_SEMICOLON);
						robot.keyRelease(KeyEvent.VK_SEMICOLON);
						robot.keyRelease(KeyEvent.VK_SHIFT);
						break;
					case KeyEvent.VK_PLUS:
						robot.keyPress(KeyEvent.VK_SHIFT);
						robot.keyPress(KeyEvent.VK_EQUALS);
						robot.keyRelease(KeyEvent.VK_EQUALS);
						robot.keyRelease(KeyEvent.VK_SHIFT);
						break;
					case KeyEvent.VK_NUMBER_SIGN:
						robot.keyPress(KeyEvent.VK_SHIFT);
						robot.keyPress(KeyEvent.VK_3);
						robot.keyRelease(KeyEvent.VK_3);
						robot.keyRelease(KeyEvent.VK_SHIFT);
						break;
					default:
						robot.keyPress(ik);
						robot.keyRelease(ik);
						break;
					}
				}
				// 最后穿入指令，键盘上的F12，空行不穿
				if (strText.length() > 1) {
					if (transmit) {
						robot.keyPress(KeyEvent.VK_F12);
						robot.keyRelease(KeyEvent.VK_F12);
						// 等待指令返回
						robot.delay(4000);
					}
				}
			} catch (AWTException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return -1;
			}
		}
		return 0;
	}

	/**
	 * 读取Excel文件中的数据
	 * 
	 * @param excelFile 要读取的Excel文件 @param version
	 *                  Excel文件的版本：version2003/version2007 @return
	 *                  成功读取的数据行数 @exception
	 */
	public int readExcel(File excelFile, String version) throws Exception {
		FileInputStream fis = new FileInputStream(excelFile);
		int rowCount = 0;
		switch (version) {
		case "version2003":
			HSSFWorkbook workbook2003 = new HSSFWorkbook(fis);
			HSSFSheet spreadsheet2003 = workbook2003.getSheetAt(0);
			Iterator<Row> rowIterator2003 = spreadsheet2003.iterator();
			HSSFRow row2003;

			while (rowIterator2003.hasNext()) {
				row2003 = (HSSFRow) rowIterator2003.next();

				// 获取总列数(包括空单元格)
				short cellNum2003 = row2003.getLastCellNum();
				String[] value2003 = new String[cellNum2003];
				Cell cell2003;
				CellType cellType = null;

				// 读取各单元格的值
				for (int i = 0; i < cellNum2003; i++) {
					cell2003 = row2003.getCell(i);
					try {
						cellType = cell2003.getCellType();
					} catch (NullPointerException e) {
//						System.out.println("Read Null Value Cell");
						value2003[i] = "";
						continue;
					}
					switch (cellType) {
					case NUMERIC:
						value2003[i] = Double.toString(cell2003.getNumericCellValue());
						if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell2003)) {
							Date theDate2003 = cell2003.getDateCellValue();
							SimpleDateFormat dff2003 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							value2003[i] = dff2003.format(theDate2003);
						}
						break;
					case STRING:
						value2003[i] = cell2003.getStringCellValue();
						break;
					case BOOLEAN:
						value2003[i] = Boolean.toString(cell2003.getBooleanCellValue());
						break;
					default:
						break;
					}
				}

				// 写入TableItem
				TableItem item = new TableItem(tableFlights, SWT.NONE, rowCount);
//				之前的代码，读取了全部cell，觉得以后会有用，不忍删除
//				item.setText(0, excelFile.getName());
//				for (int i = 0; i < cellNum2003; i++) {
//					if (i == tableFlights.getColumns().length - 1) {
//						TableColumn tableColumn_additional = new TableColumn(tableFlights, SWT.NONE);
//						tableColumn_additional.setWidth(100);
//						tableColumn_additional.setText("第" + (i+1) + "列");
//					}
//					if (value2003[i] != null) {
//						item.setText(i + 1, value2003[i]);
//					}
//				}

//				以下为新代码，只写需要的几个字段
				if (value2003[2] != null)
					item.setText(0, value2003[2]);// 写航班号
				if (value2003[5] != null) {
					if (value2003[5].endsWith(".0")) {// 班期中许多单元格为数字格式，需要去掉结尾的".0"
						value2003[5] = value2003[5].substring(0, value2003[5].length() - 2);
					}
					while (value2003[5].contains(" ")) {// 班期中许多包含空格，需要去掉
						int i = value2003[5].indexOf(" ");
						value2003[5] = value2003[5].substring(0, i)
								.concat(value2003[5].substring(i + 1, value2003[5].length()));
					}
					item.setText(1, value2003[5]);// 写班期
				}
				if (value2003[3] != null) {
					// 原始excel中日期用的自定义格式，因此无法自动识别为日期，需要进一步处理
					if (value2003[3].endsWith(".0")) {
						// 去掉结尾的.0
						value2003[3] = value2003[3].substring(0, value2003[3].length() - 2);
						int i = Integer.valueOf(value2003[3]) - 2;
						LocalDate ld = LocalDate.of(1900, 1, 1).plus(i, ChronoUnit.DAYS);
						item.setText(2, ld.toString());// 写起始日期
					} else {
						item.setText(2, value2003[3]);// 写起始日期
					}
				}
				if (value2003[4] != null) {
					// 原始excel中日期用的自定义格式，因此无法自动识别为日期，需要进一步处理
					if (value2003[4].endsWith(".0")) {
						// 去掉结尾的.0
						value2003[4] = value2003[4].substring(0, value2003[4].length() - 2);
						int i = Integer.valueOf(value2003[4]) - 2;
						LocalDate ld = LocalDate.of(1900, 1, 1).plus(i, ChronoUnit.DAYS);
						item.setText(3, ld.toString());// 写结束日期
					} else {
						item.setText(3, value2003[4]);// 写结束日期
					}
				}
				rowCount++;
			}

			workbook2003.close();
			fis.close();
			break;
		case "version2007":
			XSSFWorkbook workbook2007 = new XSSFWorkbook(fis);
			XSSFSheet spreadsheet2007 = workbook2007.getSheetAt(0);
			Iterator<Row> rowIterator2007 = spreadsheet2007.iterator();
			XSSFRow row2007;
			while (rowIterator2007.hasNext()) {
				row2007 = (XSSFRow) rowIterator2007.next();

				// 获取总列数(包括空单元格)
				short cellNum2007 = row2007.getLastCellNum();
				String[] value2007 = new String[cellNum2007];
				Cell cell2007;
				CellType cellType = null;

				// 读取各单元格的值
				for (int i = 0; i < cellNum2007; i++) {
					cell2007 = row2007.getCell(i);
					try {
						cellType = cell2007.getCellType();
					} catch (NullPointerException e) {
//							System.out.println("Read Null Value Cell");
						value2007[i] = "";
						continue;
					}
					switch (cellType) {
					case NUMERIC:
						value2007[i] = Double.toString(cell2007.getNumericCellValue());
						if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell2007)) {
							Date theDate2007 = cell2007.getDateCellValue();
							SimpleDateFormat dff2007 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							value2007[i] = dff2007.format(theDate2007);
						}
						break;
					case STRING:
						value2007[i] = cell2007.getStringCellValue();
						break;
					case BOOLEAN:
						value2007[i] = Boolean.toString(cell2007.getBooleanCellValue());
						break;
					default:
						break;
					}
				}

				// 写入TableItem
				TableItem item = new TableItem(tableFlights, SWT.NONE, rowCount);
//				之前的代码，读取了全部cell，觉得以后会有用，不忍删除
//				item.setText(0, excelFile.getName());
//				for (int i = 0; i < cellNum2007; i++) {
//					if (i == tableFlights.getColumns().length - 1) {
//						TableColumn tableColumn_additional = new TableColumn(tableFlights, SWT.NONE);
//						tableColumn_additional.setWidth(100);
//						tableColumn_additional.setText("第" + (i+1) + "列");
//					}
//					if (value2007[i] != null) {
//						item.setText(i + 1, value2007[i]);
//					}
//				}

//				以下为新代码，只写需要的几个字段
				if (value2007[2] != null)
					item.setText(0, value2007[2]);// 写航班号
				if (value2007[5] != null) {
					if (value2007[5].endsWith(".0")) {// 班期中许多单元格为数字格式，需要去掉结尾的".0"
						value2007[5] = value2007[5].substring(0, value2007[5].length() - 2);
					}
					while (value2007[5].contains(" ")) {// 班期中许多包含空格，需要去掉
						int i = value2007[5].indexOf(" ");
						value2007[5] = value2007[5].substring(0, i)
								.concat(value2007[5].substring(i + 1, value2007[5].length()));
					}
					item.setText(1, value2007[5]);// 写班期
				}
				if (value2007[3] != null) {
					// 原始excel中日期用的自定义格式，因此无法自动识别为日期，需要进一步处理
					if (value2007[3].endsWith(".0")) {
						// 去掉结尾的.0
						value2007[3] = value2007[3].substring(0, value2007[3].length() - 2);
						int i = Integer.valueOf(value2007[3]) - 2;
						LocalDate ld = LocalDate.of(1900, 1, 1).plus(i, ChronoUnit.DAYS);
						item.setText(2, ld.toString());// 写起始日期
					} else {
						item.setText(2, value2007[3]);// 写起始日期
					}
				}
				if (value2007[4] != null) {
					// 原始excel中日期用的自定义格式，因此无法自动识别为日期，需要进一步处理
					if (value2007[4].endsWith(".0")) {
						// 去掉结尾的.0
						value2007[4] = value2007[4].substring(0, value2007[4].length() - 2);
						int i = Integer.valueOf(value2007[4]) - 2;
						LocalDate ld = LocalDate.of(1900, 1, 1).plus(i, ChronoUnit.DAYS);
						item.setText(3, ld.toString());// 写结束日期
					} else {
						item.setText(3, value2007[4]);// 写结束日期
					}
				}
				rowCount++;
			}
			workbook2007.close();
			fis.close();
			break;
		default:
			break;
		}
		return rowCount;

	}
}
