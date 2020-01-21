package application;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Label;
//import org.eclipse.ecf.ui.SWTResourceManager;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;

public class MergeExcelWin64SwtApp {

	protected Shell shlExcel;
	private Text folder;
	private Table tableFiles;
	private ScrolledComposite scrolledComposite;
	private Table tableData;
	private TableColumn tblclmnNewColumn_1;
	private TableColumn tblclmnNewColumn_2;
	private TableColumn tblclmnNewColumn_3;
	private TableColumn tableColumn;
	private TableColumn tableColumn_1;
	private TableColumn tableColumn_2;
	private TableColumn tableColumn_3;
	private TableColumn tableColumn_4;
	private TableColumn tableColumn_5;
	private TableColumn tableColumn_6;
	private ScrolledComposite scrolledComposite_1;
	private Button button_1;
	private Label labelSaveResult;
	private Button button;
	private Label lblMergeResult;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MergeExcelWin64SwtApp window = new MergeExcelWin64SwtApp();
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
		shlExcel.open();
		shlExcel.layout();
		shlExcel.setLocation(10, 10);

		while (!shlExcel.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlExcel = new Shell();
		shlExcel.setSize(754, 456);
		shlExcel.setText("Excel\u6587\u4EF6\u6570\u636E\u5408\u5E76\u5DE5\u5177 V1.1");
		shlExcel.setLayout(new GridLayout(5, false));

		folder = new Text(shlExcel, SWT.BORDER);
		GridData gd_folder = new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1);
		gd_folder.widthHint = 306;
		folder.setLayoutData(gd_folder);

		Button btnNewButton = new Button(shlExcel, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog directory = new DirectoryDialog(shlExcel);
				String selected = directory.open();
				if (selected != null) {
					tableFiles.removeAll();
					folder.setText(selected);
					File filePath = new File(selected);
					File[] fileList = filePath.listFiles();
					for (int i = 0; i < fileList.length; i++) {
						TableItem item = new TableItem(tableFiles, SWT.NONE, i);
						item.setText(0, Integer.toString(i + 1));
						item.setText(1, fileList[i].getName());
						item.setText(2, "待处理");
						item.setText(3, "NA");
					}
				}

			}
		});
		btnNewButton.setText("\u6D4F\u89C8");

		Button button_2 = new Button(shlExcel, SWT.NONE);
		GridData gd_button_2 = new GridData(SWT.CENTER, SWT.TOP, false, false, 1, 1);
		gd_button_2.widthHint = 119;
		button_2.setLayoutData(gd_button_2);
		button_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (int i = 0; i < tableFiles.getItemCount(); i++) {
					if (tableFiles.getItem(i).getChecked()) {
						tableFiles.remove(i);
						i--;
					}
				}
			}
		});
		button_2.setText("\u6392\u9664\u9009\u4E2D\u7684\u6587\u4EF6");

		scrolledComposite = new ScrolledComposite(shlExcel, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		GridData gd_scrolledComposite = new GridData(SWT.FILL, SWT.CENTER, false, false, 5, 1);
		gd_scrolledComposite.heightHint = 112;
		scrolledComposite.setLayoutData(gd_scrolledComposite);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		tableFiles = new Table(scrolledComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.CHECK);
		tableFiles.setHeaderVisible(true);
		tableFiles.setLinesVisible(true);

		TableColumn tblclmnNewColumn = new TableColumn(tableFiles, SWT.NONE);
		tblclmnNewColumn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (int i = 0; i < tableFiles.getItemCount(); i++) {
					tableFiles.getItem(i).setChecked(!tableFiles.getItem(i).getChecked());
				}
			}
		});
		tblclmnNewColumn.setWidth(50);
		tblclmnNewColumn.setText("\u5E8F\u53F7");

		tblclmnNewColumn_3 = new TableColumn(tableFiles, SWT.NONE);
		tblclmnNewColumn_3.setWidth(200);
		tblclmnNewColumn_3.setText("\u6587\u4EF6\u540D");

		tableColumn = new TableColumn(tableFiles, SWT.NONE);
		tableColumn.setWidth(100);
		tableColumn.setText("\u72B6\u6001");

		tableColumn_1 = new TableColumn(tableFiles, SWT.NONE);
		tableColumn_1.setWidth(100);
		tableColumn_1.setText("\u884C\u6570");
		scrolledComposite.setContent(tableFiles);
		scrolledComposite.setMinSize(tableFiles.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		button = new Button(shlExcel, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mergeData();
			}
		});
		button.setText("\u5F00\u59CB\u5408\u5E76");

		lblMergeResult = new Label(shlExcel, SWT.NONE);
		GridData gd_lblMergeResult = new GridData(SWT.LEFT, SWT.CENTER, true, false, 4, 1);
		gd_lblMergeResult.widthHint = 494;
		lblMergeResult.setLayoutData(gd_lblMergeResult);
		lblMergeResult.setText("\u5408\u5E76\u7ED3\u679C");

		scrolledComposite_1 = new ScrolledComposite(shlExcel, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		GridData gd_scrolledComposite_1 = new GridData(SWT.FILL, SWT.FILL, false, true, 5, 1);
		gd_scrolledComposite_1.widthHint = 0;
		gd_scrolledComposite_1.heightHint = 109;
		scrolledComposite_1.setLayoutData(gd_scrolledComposite_1);
		scrolledComposite_1.setExpandHorizontal(true);
		scrolledComposite_1.setExpandVertical(true);

		tableData = new Table(scrolledComposite_1, SWT.BORDER | SWT.FULL_SELECTION);
		tableData.setHeaderVisible(true);
		tableData.setLinesVisible(true);

		tblclmnNewColumn_1 = new TableColumn(tableData, SWT.NONE);
		tblclmnNewColumn_1.setWidth(100);
		tblclmnNewColumn_1.setText("\u6765\u6E90");

		tblclmnNewColumn_2 = new TableColumn(tableData, SWT.NONE);
		tblclmnNewColumn_2.setWidth(100);
		tblclmnNewColumn_2.setText("\u7B2C\u4E00\u5217");

		tableColumn_2 = new TableColumn(tableData, SWT.NONE);
		tableColumn_2.setWidth(100);
		tableColumn_2.setText("\u7B2C\u4E8C\u5217");

		tableColumn_3 = new TableColumn(tableData, SWT.NONE);
		tableColumn_3.setWidth(100);
		tableColumn_3.setText("\u7B2C\u4E09\u5217");

		tableColumn_4 = new TableColumn(tableData, SWT.NONE);
		tableColumn_4.setWidth(100);
		tableColumn_4.setText("\u7B2C\u56DB\u5217");

		tableColumn_5 = new TableColumn(tableData, SWT.NONE);
		tableColumn_5.setWidth(100);
		tableColumn_5.setText("\u7B2C\u4E94\u5217");

		tableColumn_6 = new TableColumn(tableData, SWT.NONE);
		tableColumn_6.setWidth(100);
		tableColumn_6.setText("\u7B2C\u516D\u5217");
		scrolledComposite_1.setContent(tableData);
		scrolledComposite_1.setMinSize(tableData.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		button_1 = new Button(shlExcel, SWT.NONE);
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					writeExcel();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		button_1.setText("\u4FDD\u5B58\u5408\u5E76\u540E\u7684\u6570\u636E");

		labelSaveResult = new Label(shlExcel, SWT.NONE);
		GridData gd_labelSaveResult = new GridData(SWT.LEFT, SWT.CENTER, true, false, 4, 1);
		gd_labelSaveResult.widthHint = 500;
		labelSaveResult.setLayoutData(gd_labelSaveResult);
		labelSaveResult.setText("\u7ED3\u679C");

		Label lblexcel = new Label(shlExcel, SWT.NONE);
		lblexcel.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 5, 1));
		lblexcel.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		lblexcel.setText(
				"\u4f7f\u7528\u8bf4\u660e\uff1a\u628a\u5f85\u5408\u5e76\u7684\u0045\u0078\u0063\u0065\u006c\u6587\u4ef6\u653e\u5165\u540c\u4e00\u4e2a\u76ee\u5f55\u4e2d\uff0c\u652f\u6301\u0032\u0030\u0030\u0033\u548c\u0032\u0030\u0030\u0037\u4e24\u79cd\u7248\u672c\u3002");
	}

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

				//写入TableItem
				TableItem item = new TableItem(tableData, SWT.NONE, rowCount);
				item.setText(0, excelFile.getName());
				for (int i = 0; i < cellNum2003; i++) {
					if (i == tableData.getColumns().length - 1) {
						TableColumn tableColumn_additional = new TableColumn(tableData, SWT.NONE);
						tableColumn_additional.setWidth(100);
						tableColumn_additional.setText("第" + i + "列");
					}
					if(value2003[i] != null) {
						item.setText(i + 1, value2003[i]);
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

					//写入TableItem
					TableItem item = new TableItem(tableData, SWT.NONE, rowCount);
					item.setText(0, excelFile.getName());
					for (int i = 0; i < cellNum2007; i++) {
						if (i == tableData.getColumns().length - 1) {
							TableColumn tableColumn_additional = new TableColumn(tableData, SWT.NONE);
							tableColumn_additional.setWidth(100);
							tableColumn_additional.setText("第" + i + "列");
						}
						if (value2007[i] != null) {
							item.setText(i + 1, value2007[i]);
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

	public void mergeData() {
		int rowCount = 0;
		for (int i = 0; i < tableFiles.getItemCount(); i++) {
			File excelFile = new File((folder.getText() + "\\" + tableFiles.getItem(i).getText(1)));
			String version = (tableFiles.getItem(i).getText(1).endsWith(".xls") ? "version2003" : "version2007");
			try {
				int tempRow = readExcel(excelFile, version);
				tableFiles.getItem(i).setText(2, "读取成功");
				tableFiles.getItem(i).setText(3, Integer.toString(tempRow));
				rowCount += tempRow;
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		lblMergeResult.setText("合计" + Integer.toString(rowCount) + "条数据");
	}

	public void writeExcel() throws Exception {

		int rowCount = tableData.getItemCount();
		if (rowCount <= 0)
			return;

		FileDialog outFile = new FileDialog(shlExcel);
		String[] filter = { "*.xls", "*.xlsx" };
		outFile.setFilterExtensions(filter);
		outFile.setText("注意：数据将被保存到指定的文件中，没有覆盖提示！");
		String selected = outFile.open();
		if (selected.endsWith(".xlsx")) {
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet spreadsheet = workbook.createSheet("合并后的数据");

			for (int i = 0; i < rowCount; i++) {
				XSSFRow row = spreadsheet.createRow(i);
				for (int j = 1; j < tableData.getColumnCount(); j++) {
					row.createCell(j - 1).setCellValue(tableData.getItem(i).getText(j));
				}
			}

			FileOutputStream outStream = new FileOutputStream(selected);
			workbook.write(outStream);
			outStream.close();
			workbook.close();
			labelSaveResult.setText(rowCount + "条数据成功写入" + selected.toString());
		} else if (selected.endsWith(".xls")) {
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet spreadsheet = workbook.createSheet("合并后的数据");

			for (int i = 0; i < rowCount; i++) {
				HSSFRow row = spreadsheet.createRow(i);
				for (int j = 1; j < tableData.getColumnCount(); j++) {
					row.createCell(j - 1).setCellValue(tableData.getItem(i).getText(j));
				}
			}

			FileOutputStream outStream = new FileOutputStream(selected);
			workbook.write(outStream);
			outStream.close();
			workbook.close();
			labelSaveResult.setText(rowCount + "条数据成功写入" + selected.toString());

		}
	}
}
