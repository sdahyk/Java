package hyk.sda.cn;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.List;

public class MainWindow {

	protected Shell shlKeymouserecorderplayer;
	private Text textScriptFileLocation;
	static Table tableScriptFiles;
	static MouseHook2 mhook;
	static KeyboardHook2 khook;
	static Thread mThread, kThread;
	public volatile static boolean recordSign;
	public volatile static Instant lastTime;
	static protected ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);
	static Map<String, String> execResultMap = new HashMap<String, String>();
	static Timer timer = new Timer();
	List listLocale;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MainWindow window = new MainWindow();
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
		// 禁用掉了最大化按钮
		shlKeymouserecorderplayer = new Shell(display, SWT.SHELL_TRIM ^ SWT.MAX);
		// 取系统中预置的图标，省得测试运行时还得加个图标文件
		shlKeymouserecorderplayer.setImage(display.getSystemImage(SWT.ICON_WORKING));
		// 构造系统栏控件
		final Tray tray = display.getSystemTray();
		final TrayItem trayItem = new TrayItem(tray, SWT.NONE);
		
		// 读取config.ini文件，设置默认语言
		FileReader reader;
		try {
			reader = new FileReader("config.ini");
			BufferedReader br = new BufferedReader(reader);
			String str = null;
			String option = null, value = null;
			while ((str = br.readLine()) != null) {
				if (str.contains("=")) {
					option = str.substring(0, str.indexOf("="));
					value = str.substring(str.indexOf("=") + 1);
				}
				switch (option) {
				case "DefaultScriptFileLocation":
					break;
				case "Locale":
					if (value.contentEquals("default(English)")) {
						Locale.setDefault(new Locale("en", "US"));
					} else if (value.contentEquals("简体中文")) {
						Locale.setDefault(new Locale("zh", "CN"));
					}
					break;
				}
			}
			br.close();
			reader.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		trayItem.setToolTipText(Messages.getString("MainWindow.trayIconToolTip.text", "KeyMouse Recorder/Player")); //$NON-NLS-1$ //$NON-NLS-2$
		trayItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				toggleDisplay(shlKeymouserecorderplayer, tray);
			}
		});
		// 程序启动时，窗口是显示的，所以系统栏图标隐藏
		trayItem.setVisible(false);

		// 创建托盘菜单
		final Menu trayMenu = new Menu(shlKeymouserecorderplayer, SWT.POP_UP);
		MenuItem showMenuItem = new MenuItem(trayMenu, SWT.PUSH);
		showMenuItem.setText(Messages.getString("MainWindow.showMenuItem.text", "\u663E\u793A\u7A97\u53E3(&s)")); //$NON-NLS-1$ //$NON-NLS-2$
		// 点击菜单项时显示窗口，并隐藏系统栏中的图标
		showMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				toggleDisplay(shlKeymouserecorderplayer, tray);
			}
		});
		// 在系统栏图标点击鼠标右键时的事件，弹出系统栏菜单
		trayItem.addMenuDetectListener(new MenuDetectListener() {
			public void menuDetected(MenuDetectEvent e) {
				trayMenu.setVisible(true);
			}
		});
		trayMenu.setDefaultItem(showMenuItem);

		trayItem.setImage(shlKeymouserecorderplayer.getImage());
		// 注册窗口事件监听器
		shlKeymouserecorderplayer.addShellListener(new ShellAdapter() {

			// 点击窗口最小化按钮时，窗口隐藏，系统栏显示图标
			public void shellIconified(ShellEvent e) {
				toggleDisplay(shlKeymouserecorderplayer, tray);
			}

		});

		createContents();
		shlKeymouserecorderplayer.open();
		shlKeymouserecorderplayer.layout();
		while (!shlKeymouserecorderplayer.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * 窗口是可见状态时，则隐藏窗口，同时把系统栏中图标删除 窗口是隐藏状态时，则显示窗口，并且在系统栏中显示图标
	 * 
	 * @param shell 窗口
	 * @param tray  系统栏图标控件
	 */
	protected void toggleDisplay(Shell shell, Tray tray) {
		try {
			shell.setVisible(!shell.isVisible());
			tray.getItem(0).setVisible(!shell.isVisible());
			if (shell.getVisible()) {
				shell.setMinimized(false);
				shell.setActive();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
//		shlKeymouserecorderplayer = new Shell();
		shlKeymouserecorderplayer.setMinimumSize(new Point(640, 480));
		shlKeymouserecorderplayer.setSize(640, 480);
		shlKeymouserecorderplayer
				.setText(Messages.getString("MainWindow.shlKeymouserecorderplayer.text", "KeyMouseRecorder&Player")); //$NON-NLS-1$ //$NON-NLS-2$
		shlKeymouserecorderplayer.setLayout(null);
		shlKeymouserecorderplayer.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent e) {
				MessageBox messagebox = new MessageBox(shlKeymouserecorderplayer, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
				messagebox.setText("KeyMouseRecorder提醒");
				messagebox.setMessage("您确定要退出吗?");
				int message = messagebox.open();
				if (message == SWT.YES) {
					if (recordSign) {
//						mhook.setHookOff();
//						khook.setHookOff();
						mThread.interrupt();
						kThread.interrupt();
					}
					MainWindow.executor.shutdown();
					MainWindow.timer.cancel();
					e.doit = true;
				} else {
					e.doit = false;
				}
			}
		});

		Label lblStatus = new Label(shlKeymouserecorderplayer, SWT.NONE);
		lblStatus.setBounds(5, 424, 615, 17);
		lblStatus.setText(Messages.getString("MainWindow.lblStatus.text", "Status")); //$NON-NLS-1$ //$NON-NLS-2$

		TabFolder tabFolder = new TabFolder(shlKeymouserecorderplayer, SWT.NONE);
		tabFolder.setBounds(5, 5, 615, 413);

		TabItem tbtmRecorder = new TabItem(tabFolder, SWT.NONE);
		tbtmRecorder.setText(Messages.getString("MainWindow.tbtmRecorder.text", "Recorder")); //$NON-NLS-1$ //$NON-NLS-2$

		Composite composite = new Composite(tabFolder, SWT.NONE);
		tbtmRecorder.setControl(composite);

		Button btnRecord = new Button(composite, SWT.NONE);
		btnRecord.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mhook = new MouseHook2();
				khook = new KeyboardHook2();
				MainWindow.recordSign = true;
				MainWindow.lastTime = Instant.now();
				mThread = new Thread(mhook);
				kThread = new Thread(khook);
				mThread.start();
				kThread.start();
			}
		});
		btnRecord.setBounds(0, 10, 80, 27);
		btnRecord.setText(Messages.getString("MainWindow.btnRecord.text", "Record")); //$NON-NLS-1$ //$NON-NLS-2$

		Label lblf = new Label(composite, SWT.WRAP);
		lblf.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
		lblf.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 14, SWT.NORMAL));
		lblf.setBounds(10, 59, 587, 32);
		lblf.setText(Messages.getString("MainWindow.lblf.text", //$NON-NLS-1$
				"\u70B9\u51FB\u9F20\u6807\u53F3\u952E\uFF0C\u505C\u6B62\u8BB0\u5F55\u9F20\u6807\u3002")); //$NON-NLS-1$

		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
		lblNewLabel.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 14, SWT.NORMAL));
		lblNewLabel.setBounds(10, 113, 587, 32);
		lblNewLabel.setText(Messages.getString("MainWindow.lblNewLabel.text", //$NON-NLS-1$
				"\u6309\u4E0B\u952E\u76D8F12\u952E\uFF0C\u505C\u6B62\u8BB0\u5F55\u952E\u76D8\u3002")); //$NON-NLS-1$

		Button btnStopall = new Button(composite, SWT.NONE);
		btnStopall.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MainWindow.recordSign = false;
//				if (mhook != null) {
//					mhook.setHookOff();
//				}
//				if (khook != null) {
//					khook.setHookOff();
//				}
				if (mThread != null) {
					mThread.interrupt();
				}
				if (kThread != null) {
					kThread.interrupt();
				}
			}
		});
		btnStopall.setBounds(272, 10, 80, 27);
		btnStopall.setText(Messages.getString("MainWindow.btnStopall.text", (String) null)); //$NON-NLS-1$

		Label lblHint = new Label(composite, SWT.WRAP);
		lblHint.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		lblHint.setBounds(10, 200, 587, 67);
		lblHint.setText(Messages.getString("MainWindow.lblHint.text", //$NON-NLS-1$
				"\u8BF7\u6CE8\u610F\uFF1A\u6B64\u7248\u672C\u672A\u8003\u8651\u7EC4\u5408\u952E\u7684\u60C5\u51B5\uFF0C\u53EA\u6309\u987A\u5E8F\u8BB0\u5F55\u5355\u4E2A\u6309\u952E\u3002\u56E0\u6B64\u5982\u679C\u6709\u7EC4\u5408\u6309\u952E\uFF0C\u5728\u64AD\u653E\u65F6\u7684\u6548\u679C\u4E3A\u4F9D\u6B21\u9010\u4E2A\u6309\u4E0B\u5E76\u62AC\u8D77\u5355\u4E2A\u6309\u952E\u3002\u4E5F\u5E76\u672A\u8BB0\u5F55\u9F20\u6807\u62D6\u52A8\u7684\u52A8\u4F5C\uFF0C\u53EA\u8BB0\u5F55\u4E86\u5355\u51FB\u3001\u53CC\u51FB\u3002")); //$NON-NLS-1$

		TabItem tbtmPlayer = new TabItem(tabFolder, SWT.NONE);
		tbtmPlayer.setText(Messages.getString("MainWindow.tbtmPlayer.text", "Player")); //$NON-NLS-1$ //$NON-NLS-2$

		Composite composite_1 = new Composite(tabFolder, SWT.NONE);
		tbtmPlayer.setControl(composite_1);

		ToolBar toolBarPlayer = new ToolBar(composite_1, SWT.FLAT | SWT.RIGHT);
		toolBarPlayer.setBounds(0, 0, 607, 25);

		ToolItem tltmOpen = new ToolItem(toolBarPlayer, SWT.NONE);
		tltmOpen.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog filedlg = new FileDialog(shlKeymouserecorderplayer, SWT.OPEN);
				// 设置文件对话框的标题
				filedlg.setText("请选择要打开的ScriptFile");
				// 设置初始路径
				filedlg.setFilterPath(textScriptFileLocation.getText());
				// 设置扩展名过滤
				filedlg.setFilterExtensions(new String[] { "*.kms", "*.*" });
				// 打开文件对话框，返回选中文件的绝对路径
				String selected = filedlg.open();
				if (selected != null) {
					tableScriptFiles.removeAll();
					TableItem item = new TableItem(tableScriptFiles, 0);
					item.setText(0, selected);
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
					Instant insStop = Instant.now();
					insStop = insStop.plusSeconds(600);
					item.setText(1, formatter.format((new Date())));
					item.setText(2, formatter.format(Date.from(insStop)));
					item.setText(3, "0");
				}
			}
		});
		tltmOpen.setText(Messages.getString("MainWindow.tltmOpen.text", "Open")); //$NON-NLS-1$ //$NON-NLS-2$

		ToolItem tltmPlaySelected = new ToolItem(toolBarPlayer, SWT.NONE);
		tltmPlaySelected.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int itemsNum = tableScriptFiles.getItemCount();
				for (int i = 0; i < itemsNum; i++) {
					TableItem item = tableScriptFiles.getItem(i);
					if (item.getChecked()) {
						// 判断启动条件，设置定时器，条件满足的，启动执行
						String strStartTime = item.getText(1);
						String strStopTime = item.getText(2);
						String strRepeat = item.getText(3);
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
						ParsePosition pos = new ParsePosition(0);
						Date startTime = formatter.parse(strStartTime, pos);
//						恢复pos的位置，才能正常获取下面的stopTime
						pos.setIndex(0);
						Date stopTime = formatter.parse(strStopTime, pos);
						Date now = new Date();
						if (now.before(stopTime)) {// 尚未到结束时间，所以判断是否需要重复执行
							// 判断还要等多久开始
							Instant currentTime = now.toInstant();
							Instant insStartTime = startTime.toInstant();
							long interval = Duration.between(currentTime, insStartTime).toMillis();
							// 如果已过了开始时间，设为延迟3秒开始
							if (interval <= 0)
								interval = 3000;

							if (Integer.valueOf(strRepeat) > 1) {// 重复执行
								executor.scheduleAtFixedRate(new MyScheduledExecutor(item.getText(0)), interval / 1000,
										Integer.valueOf(strRepeat), TimeUnit.SECONDS);
							} else {// 单次执行，按据开始时间的间隔延迟后开始
								executor.schedule(new MyScheduledExecutor(item.getText(0)), interval / 1000,
										TimeUnit.SECONDS);
							}
							item.setText(4, "Scheduled:" + interval / 1000 + "Seconds Later");
							// 设置定时器去检查执行情况
							execResultMap.put(item.getText(0), "");
							setMonitor(item.getText(0), interval);
						} else if (now.after(stopTime)) {// 当前时间已经已经过了结束时间
							item.setText(4, "Already passed StopTime");
						}

					}
				}
			}
		});
		tltmPlaySelected.setText(Messages.getString("MainWindow.tltmPlaySelected.text", "Play Selected")); //$NON-NLS-1$ //$NON-NLS-2$

		ToolItem tltmEdit = new ToolItem(toolBarPlayer, SWT.NONE);
		tltmEdit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int itemsNum = tableScriptFiles.getItemCount();
				for (int i = 0; i < itemsNum; i++) {
					TableItem item = tableScriptFiles.getItem(i);
					if (item.getChecked()) {
						Editor shellEditor = new Editor(Display.getDefault(), textScriptFileLocation.getText(),
								item.getText(0));
						shellEditor.setVisible(true);
					}
				}
			}
		});
		tltmEdit.setText(Messages.getString("MainWindow.tltmEdit.text", "Edit")); //$NON-NLS-1$ //$NON-NLS-2$

		ToolItem tltmStop = new ToolItem(toolBarPlayer, SWT.NONE);
		tltmStop.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MainWindow.executor.shutdown();
				int itemsNum = tableScriptFiles.getItemCount();
				for (int i = 0; i < itemsNum; i++) {
					TableItem item = tableScriptFiles.getItem(i);
					if (item.getChecked()) {
						item.setText(4, "stoped");
					}
				}
			}
		});
		tltmStop.setText(Messages.getString("MainWindow.tltmStop.text", "Stop")); //$NON-NLS-1$ //$NON-NLS-2$

		tableScriptFiles = new Table(composite_1, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION);
		tableScriptFiles.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				if (tableScriptFiles.getSelectionCount() != 0) {
					final TableItem item = tableScriptFiles.getItem(tableScriptFiles.getSelectionIndex());
					// 获取鼠标点击的位置
					Point point = new Point(e.x, e.y);
					// 鼠标点击所在cell的列
					int tmpCol = 0;
					// 获取所点击的cell
					for (int i = 0; i < tableScriptFiles.getColumnCount(); i++) {
						Rectangle rect = item.getBounds(i);
						if (rect.contains(point)) {
							tmpCol = i;
							break;
						}
					}
					final int selectCol = tmpCol;
					final TableEditor editor = new TableEditor(tableScriptFiles);
					Control oldEditor = editor.getEditor();
					if (oldEditor != null) {
						oldEditor.dispose();
					}
					final Text changeItemText = new Text(tableScriptFiles, SWT.NONE);
					changeItemText.computeSize(SWT.DEFAULT, tableScriptFiles.getItemHeight());
					editor.grabHorizontal = true;
					editor.minimumHeight = changeItemText.getSize().y;
					editor.minimumWidth = changeItemText.getSize().x;
					editor.setEditor(changeItemText, item, selectCol);
					changeItemText.setText(item.getText(selectCol));
					changeItemText.forceFocus();

					// 编辑后变回表格样式
					changeItemText.addFocusListener(new FocusAdapter() {
						@Override
						public void focusLost(FocusEvent focusevent) {
							Control defaultEditor = editor.getEditor();
							editor.setEditor(defaultEditor, item, selectCol);
							item.setText(selectCol, changeItemText.getText());
							changeItemText.dispose();
						}
					});
				}
			}

			@Override
			public void mouseUp(MouseEvent e) {
				if (e.button == 3) {
					Menu popMenu = new Menu(shlKeymouserecorderplayer, SWT.POP_UP);
					MenuItem menuItemEdit = new MenuItem(popMenu, SWT.PUSH);
					menuItemEdit.setText("Edit This ScriptFile");
					menuItemEdit.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							Editor shellEditor = new Editor(Display.getDefault(), textScriptFileLocation.getText(),
									tableScriptFiles.getItem(tableScriptFiles.getSelectionIndex()).getText(0));
							shellEditor.open();
						}
					});
					popMenu.setVisible(true);
				}
				;
			}
		});
		tableScriptFiles.setBounds(10, 31, 587, 352);
		tableScriptFiles.setHeaderVisible(true);
		tableScriptFiles.setLinesVisible(true);

		TableColumn tblclmnScriptfile = new TableColumn(tableScriptFiles, SWT.NONE);
		tblclmnScriptfile.setWidth(100);
		tblclmnScriptfile.setText(Messages.getString("MainWindow.tblclmnScriptfile.text", "ScriptFile")); //$NON-NLS-1$ //$NON-NLS-2$

		TableColumn tblclmnStartdatetime = new TableColumn(tableScriptFiles, SWT.NONE);
		tblclmnStartdatetime.setWidth(119);
		tblclmnStartdatetime.setText(Messages.getString("MainWindow.tblclmnStartdatetime.text", "StartDateTime")); //$NON-NLS-1$ //$NON-NLS-2$

		TableColumn tblclmnEnddatetime = new TableColumn(tableScriptFiles, SWT.NONE);
		tblclmnEnddatetime.setWidth(127);
		tblclmnEnddatetime.setText(Messages.getString("MainWindow.tblclmnEnddatetime.text", "EndDateTime")); //$NON-NLS-1$ //$NON-NLS-2$

		TableColumn tblclmnRepeat = new TableColumn(tableScriptFiles, SWT.NONE);
		tblclmnRepeat.setWidth(122);
		tblclmnRepeat.setText(Messages.getString("MainWindow.tblclmnRepeat.text", "Repeat Interval(Seconds)")); //$NON-NLS-1$ //$NON-NLS-2$

		TableColumn tblclmnStatus = new TableColumn(tableScriptFiles, SWT.NONE);
		tblclmnStatus.setWidth(100);
		tblclmnStatus.setText(Messages.getString("MainWindow.tblclmnStatus.text", "Status")); //$NON-NLS-1$ //$NON-NLS-2$

		TableItem tableItem = new TableItem(tableScriptFiles, SWT.NONE);
		tableItem.setText(new String[] { "testFile1", "2019-10-20 10:15", "2019-10-20 10:15", "60" });
		tableItem.setChecked(true);

		TableItem tableItem_1 = new TableItem(tableScriptFiles, SWT.NONE);
		tableItem_1.setText(new String[] { "testFile2", "2019-11-20 11:20", "2019-11-22 12:30", "600" });

		TabItem tbtmSettings = new TabItem(tabFolder, SWT.NONE);
		tbtmSettings.setText(Messages.getString("MainWindow.tbtmSettings.text", "Settings")); //$NON-NLS-1$ //$NON-NLS-2$

		Composite composite_2 = new Composite(tabFolder, SWT.NONE);
		tbtmSettings.setControl(composite_2);

		Label lblScriptFileLocation = new Label(composite_2, SWT.NONE);
		lblScriptFileLocation.setBounds(10, 10, 162, 17);
		lblScriptFileLocation
				.setText(Messages.getString("MainWindow.lblScriptFileLocation.text", "Default ScriptFile Location :")); //$NON-NLS-1$ //$NON-NLS-2$

		textScriptFileLocation = new Text(composite_2, SWT.BORDER);
		textScriptFileLocation.setBounds(178, 10, 340, 23);

		Button btnSelect = new Button(composite_2, SWT.NONE);
		btnSelect.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog directory = new DirectoryDialog(shlKeymouserecorderplayer);
				String selected = directory.open();
				if (selected != null) {
					textScriptFileLocation.setText(selected);
				}
			}
		});
		btnSelect.setBounds(524, 10, 73, 27);
		btnSelect.setText(Messages.getString("MainWindow.btnSelect.text", "Select...")); //$NON-NLS-1$ //$NON-NLS-2$

		Label lblLocale = new Label(composite_2, SWT.NONE);
		lblLocale.setBounds(10, 66, 162, 17);
		lblLocale.setText(Messages.getString("MainWindow.lblLocal.text", "Local:")); //$NON-NLS-1$ //$NON-NLS-2$

		listLocale = new List(composite_2, SWT.BORDER);
		listLocale.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		listLocale.setItems(new String[] { "default(English)", "\u7B80\u4F53\u4E2D\u6587" });
		listLocale.setBounds(178, 62, 108, 68);

		Button btnSaveConfigration = new Button(composite_2, SWT.NONE);
		btnSaveConfigration.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileWriter fr;
				try {
					fr = new FileWriter("config.ini");
					fr.write("DefaultScriptFileLocation=" + textScriptFileLocation.getText() + "\n");
					fr.write("Locale=" + listLocale.getItem(listLocale.getSelectionIndex()) + "\n");
					fr.flush();
					fr.close();
					lblStatus.setText("Settings Saved :-)");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		btnSaveConfigration.setBounds(517, 346, 80, 27);
		btnSaveConfigration.setText(Messages.getString("MainWindow.btnSaveConfigration.text", "Save"));

		readConfigFile();

	}

	/**
	 * 监控器，用来查看任务是否已经执行完成，修改相应的状态。
	 * 
	 * @param String text：scriptFile
	 * @param long   delay：定时器启动的延迟时间，单位毫秒
	 */
	protected void setMonitor(String text, long delay) {
		MyTimerTask timerTask = new MyTimerTask(text, timer);
		timer.schedule(timerTask, delay, 5000);
	}

	/**
	 * 读取配置文件config.ini，设置相关选项。
	 * 
	 * @param null
	 */
	protected boolean readConfigFile() {
		// 读取config.ini文件
		FileReader reader;
		try {
			reader = new FileReader("config.ini");
			BufferedReader br = new BufferedReader(reader);
			String str = null;
			String option = null, value = null;
			while ((str = br.readLine()) != null) {
				if (str.contains("=")) {
					option = str.substring(0, str.indexOf("="));
					value = str.substring(str.indexOf("=") + 1);
				}
				switch (option) {
				case "DefaultScriptFileLocation":
					textScriptFileLocation.setText(value);
					break;
				case "Locale":
					if (value.contentEquals("default(English)")) {
						listLocale.select(0);
						Locale.setDefault(new Locale("en", "US"));
					} else if (value.contentEquals("简体中文")) {
						listLocale.select(1);
						Locale.setDefault(new Locale("zh", "CN"));
					}
					break;
				}
			}
			br.close();
			reader.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
}
