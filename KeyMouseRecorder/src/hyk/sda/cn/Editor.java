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
				// �����ļ��Ի���ı���
				filedlg.setText("��ѡ��Ҫ�򿪵�ScriptFile");
				// ���ó�ʼ·��
				filedlg.setFilterPath(defaultLocation);
				// ������չ������
				filedlg.setFilterExtensions(new String[] { "*.kms", "*.*" });
				// ���ļ��Ի��򣬷���ѡ���ļ��ľ���·��
				String selected = filedlg.open();
				if (selected != null) {
					// �򿪲���ȡѡ����ļ�
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
				// �����ļ��Ի���ı���
				filedlg.setText("��ѡ��Ҫ�����ScriptFile");
				// ���ó�ʼ·��
				filedlg.setFilterPath(defaultLocation);
				// ������չ������
				filedlg.setFilterExtensions(new String[] { "*.kms", "*.*" });
				// ���ļ��Ի��򣬷���ѡ���ļ��ľ���·��
				String selected = filedlg.open();

				// ȷ���Ƿ񸲸�
				if (selected != null) {
					File file = new File(selected);
					if (file.exists()) {
						MessageBox messagebox = new MessageBox(getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
						messagebox.setText("Editor����");
						messagebox.setMessage("��ȷ��Ҫ����ѡ����ļ���?");
						int message = messagebox.open();
						if (message == SWT.YES) {
							if (selected != null) {
								// �򿪲�д��ѡ����ļ�
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
