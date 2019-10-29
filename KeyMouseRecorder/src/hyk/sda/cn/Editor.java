package hyk.sda.cn;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class Editor extends Shell {
	private Text textScript;
	private String defaultLocation;
	private String scriptFile;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			Display display = Display.getDefault();
			Editor shell = new Editor(display, null, null);
			shell.open();
			shell.layout();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the shell.
	 * 
	 * @param display
	 */
	public Editor(Display display, String fileLocation, String file) {
//		super(display, SWT.SHELL_TRIM);
//		
		ToolBar toolBar = new ToolBar(this, SWT.FLAT | SWT.RIGHT);
		toolBar.setBounds(0, 0, 434, 25);

		ToolItem tltmOpen = new ToolItem(toolBar, SWT.NONE);
		tltmOpen.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog filedlg = new FileDialog(getShell(), SWT.OPEN);
				// 设置文件对话框的标题
				filedlg.setText("请选择要打开的ScriptFile");
				// 设置初始路径
				filedlg.setFilterPath(defaultLocation);
				// 设置扩展名过滤
				filedlg.setFilterExtensions(new String[] { "*.kms", "*.*" });
				// 打开文件对话框，返回选中文件的绝对路径
				String selected = filedlg.open();
				if (selected != null) {
					// 打开并读取选择的文件
					StringBuffer sb = new StringBuffer("");
					FileReader reader;
					try {
						reader = new FileReader(selected);
						BufferedReader br = new BufferedReader(reader);
						String str = null;
						while ((str = br.readLine()) != null) {
							sb.append(str + "\n");
						}
						br.close();
						reader.close();
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (IOException e2) {
						e2.printStackTrace();
					}
					textScript.setText(sb.toString());
					getShell().setText(selected);
				}
			}
		});
		tltmOpen.setText("Open");

		ToolItem tltmInsert = new ToolItem(toolBar, SWT.NONE);
		tltmInsert.setText("Insert");

		ToolItem tltmDelete = new ToolItem(toolBar, SWT.NONE);
		tltmDelete.setText("Delete");

		ToolItem tltmSave = new ToolItem(toolBar, SWT.NONE);
		tltmSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog filedlg = new FileDialog(getShell(), SWT.OPEN);
				// 设置文件对话框的标题
				filedlg.setText("请选择要保存的ScriptFile");
				// 设置初始路径
				filedlg.setFilterPath(defaultLocation);
				// 设置扩展名过滤
				filedlg.setFilterExtensions(new String[] { "*.kms", "*.*" });
				// 打开文件对话框，返回选中文件的绝对路径
				String selected = filedlg.open();

				// 确认是否覆盖
				if (selected != null) {
					File file = new File(selected);
					if (file.exists()) {
						MessageBox messagebox = new MessageBox(getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
						messagebox.setText("Editor提醒");
						messagebox.setMessage("您确定要覆盖选择的文件吗?");
						int message = messagebox.open();
						if (message == SWT.YES) {
							if (selected != null) {
								// 打开并写入选择的文件
								FileWriter fwriter;
								try {
									fwriter = new FileWriter(selected);
									BufferedWriter br = new BufferedWriter(fwriter);
									String str = textScript.getText();
									br.write(str);
									br.close();
									fwriter.close();
								} catch (FileNotFoundException e1) {
									e1.printStackTrace();
								} catch (IOException e2) {
									e2.printStackTrace();
								}
							}
						} else {
						}
					}
				}
			}
		});
		tltmSave.setText("Save");

		textScript = new Text(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		textScript.setBounds(0, 31, 434, 230);
		createContents();

		if (file != null) {
			defaultLocation = fileLocation;
			scriptFile = file;
			StringBuffer sb = new StringBuffer("");
			FileReader reader;
			try {
				reader = new FileReader(scriptFile);
				BufferedReader br = new BufferedReader(reader);
				String str = null;
				while ((str = br.readLine()) != null) {
					sb.append(str + "\n");
				}
				br.close();
				reader.close();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			textScript.setText(sb.toString());
			getShell().setText(file);
		}
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("Script Editor");
		setSize(450, 300);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
