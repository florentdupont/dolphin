package com.dolphin.processor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

import com.dolphin.api.StatusType;

/**
 *
 * Classe qui va constituer le rapport d'analyse.
 * Chaque ligne du rapport est constitué des infos suivantes : 
 * - Nom de la classe
 * - Nom de la méthode
 * - iD de Regle
 * - version de regle
 * - Status de développement
 * 
 * @author Florent Dupont
 */
public class BusinessReport {

	/** NOT AVAILABLE */
	public static final String NA = "";

	/**
	 * Une ligne du rapport
	 * @author flo
	 */
	class Line implements Comparable<Line>{
		Method _method;
		List<Rule> _rules = new ArrayList<Rule>();
		StatusType _status;

		Line(Method method, String rule, String version) {
			_method = method;
			_rules.add(new Rule(rule, version));
		}

		Line(Method method) {
			_method = method;
		}

		Line(Method method, StatusType status) {
			_method = method;
			_status = status;
		}

		@Override
		public int compareTo(Line o) {
			return _method._classname.compareTo(o._method._classname);
		}

	}

	class Method {
		String _classname;
		String _methodname;

		Method(String classname, String methodname) {
			this._classname = classname;
			this._methodname = methodname;
		}

		@Override
		public boolean equals(Object obj) {
			Method other = (Method)obj;
			return other._classname.equals(_classname) && other._methodname.equals(_methodname);
		}

		@Override
		public int hashCode() {
			return _classname.hashCode() + _methodname.hashCode();
		}
	}

	class Rule {
		String _name;
		String _version;

		public Rule(String name, String version) {
			_name = name;
			_version = version;
		}
	}

	/**
	 * Chaque ligne est indexé sur son nom
	 */
	private Map<Method, Line> lines = new HashMap<Method, Line>();

	public BusinessReport() {

	}

	public void addLine(String classname, String methodname, String rule, String version) {
		//System.out.println(classname + " " + methodname + " " + rule + " " + version);
		BusinessReport.Method method = new BusinessReport.Method(classname, methodname);
		if(lines.containsKey(method)) {
			lines.get(method)._rules.add(new Rule(rule, version));
		}
		else {
			lines.put(method, new Line(method, rule, version));	
		}

	}

	public void addLine(String classname, String methodname, StatusType status) {
		//System.out.println(classname + " " + methodname + " " + status);
		BusinessReport.Method method = new BusinessReport.Method(classname, methodname);
		if(lines.containsKey(method)) {
			lines.get(method)._status = status;
		}
		else {
			lines.put(method, new Line(method, status));	
		}
	}

	public boolean hasDevStatus(String classname, String methodname) {
		BusinessReport.Method method = new BusinessReport.Method(classname, methodname);
		if(lines.containsKey(method)) {
			return lines.get(method)._status != null;
		}
		return false;
	}



	public void writeToCSV(String path) {

		File file = new File(path, "export.csv");

		try {
			// on flush le fichier s'il exsite;
			FileUtils.writeStringToFile(file, "", false);

			List<Line> sortedLines = new ArrayList<Line>(lines.values());
			Collections.sort(sortedLines);

			for(Line l : sortedLines) {
				if(l._rules.isEmpty()) {
					String data = l._method._classname + ";" + l._method._methodname + ";" + " ; ;" + l._status.toString() + "\n";
					FileUtils.writeStringToFile(file, data, true);
				}
				else {
					for(Rule r : l._rules) {
						String data = l._method._classname + ";" + l._method._methodname + ";" +r._name + ";" + r._version + ";"+ l._status.toString() + "\n";
						FileUtils.writeStringToFile(file, data, true);
					}
				}
			}
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}


	public void writeToConsole() {

		List<Line> sortedLines = new ArrayList<Line>(lines.values());
		Collections.sort(sortedLines);

		for(Line l : sortedLines) {
			if(l._rules.isEmpty()) {
				String data = l._method._classname + ";" + l._method._methodname + ";" + " ; ;" + l._status.toString();
				System.out.println(data);
			}
			else {
				for(Rule r : l._rules) {
					String data = l._method._classname + ";" + l._method._methodname + ";" +r._name + ";" + r._version + ";"+ l._status.toString();
					System.out.println(data);
				}
			}
		}
	}

	public void writeToXls(String path) {

		File file = new File(path, "export.xls");


		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("results");







		int rownum = 0;
		HSSFRow row = sheet.createRow(rownum);
		HSSFCell cell = row.createCell((short)0, HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue("Classe");

		cell = row.createCell((short)1, HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue("Méthode");

		cell = row.createCell((short)2, HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue("Règle");

		cell = row.createCell((short)3, HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue("Version");

		cell = row.createCell((short)4, HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue("Status");


		List<Line> sortedLines = new ArrayList<Line>(lines.values());
		Collections.sort(sortedLines);

		for(Line l : sortedLines) {
			rownum++;
			row = sheet.createRow(rownum);

			cell = row.createCell((short)0, HSSFCell.CELL_TYPE_STRING);
			cell.setCellValue(l._method._classname);

			cell = row.createCell((short)1, HSSFCell.CELL_TYPE_STRING);
			cell.setCellValue(l._method._methodname);

			if(l._rules.isEmpty()) {

				cell = row.createCell((short)2, HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue("");

				cell = row.createCell((short)3, HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue("");

			}
			else {
				for(Rule r : l._rules) {

					cell = row.createCell((short)2, HSSFCell.CELL_TYPE_STRING);
					cell.setCellValue(r._name);

					cell = row.createCell((short)3, HSSFCell.CELL_TYPE_STRING);
					cell.setCellValue(r._version);


				}
			}

			cell = row.createCell((short)4, HSSFCell.CELL_TYPE_STRING);
			cell.setCellValue(l._status.toString());
			HSSFCellStyle cellStyle = wb.createCellStyle();

			if(l._status == StatusType.TESTED) {
				cellStyle.setFillForegroundColor(HSSFColor.LIGHT_BLUE.index);
			}
			else if (l._status == StatusType.DONE) {
				cellStyle.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
			}
			else if (l._status == StatusType.ONGOING) {
				cellStyle.setFillForegroundColor(HSSFColor.LIGHT_ORANGE.index);
			}
			else if (l._status == StatusType.TODO) {
				cellStyle.setFillForegroundColor(HSSFColor.RED.index);
			}
			cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			cell.setCellStyle(cellStyle);

		}

		sheet.autoSizeColumn(0);
		sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2);
		sheet.autoSizeColumn(3);
		sheet.autoSizeColumn(4);

		HSSFPalette palette = wb.getCustomPalette();

		palette.setColorAtIndex(HSSFColor.LIGHT_GREEN.index, (byte) 0x8a, (byte) 0xe2, (byte) 0x34);
		palette.setColorAtIndex(HSSFColor.LIGHT_BLUE.index, (byte) 0x72, (byte) 0x9f, (byte) 0xcf);
		palette.setColorAtIndex(HSSFColor.LIGHT_ORANGE.index, (byte) 0xe9, (byte) 0xb9, (byte) 0x6e);
		palette.setColorAtIndex(HSSFColor.LIGHT_TURQUOISE.index, (byte) 0xad, (byte) 0x7f, (byte) 0xa8);
		palette.setColorAtIndex(HSSFColor.RED.index, (byte) 0xef, (byte) 0x29, (byte) 0x29);

		FileOutputStream fileOut;
		try {
			fileOut = new FileOutputStream(file);
			wb.write(fileOut);
			fileOut.close();  
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @param path
	 */
	public void writeToHtml(String path) {
		// recupère les ressources que l'on va copier vers le répertoire de sortie.
		// récupère en inputStream et exporte dans un fichier.
		// Assez artisanal pour l'instant mais répond aux besoins.

		String resourcesPrefix = "gui/";
		// nom des ressources à récupérer
		String [] resourcesToCopy = {
				// CSS
				"css/fuelux-responsive.css",
				"css/fuelux.css",
				// PNG
				"img/glyphicons-halflings-white.png",
				"img/glyphicons-halflings.png",
				// JS
				"lib/bootstrap/js/bootstrap-affix.js",
				"lib/bootstrap/js/bootstrap-alert.js",
				"lib/bootstrap/js/bootstrap-button.js",
				"lib/bootstrap/js/bootstrap-carousel.js",
				"lib/bootstrap/js/bootstrap-collapse.js",
				"lib/bootstrap/js/bootstrap-dropdown.js",
				"lib/bootstrap/js/bootstrap-modal.js",
				"lib/bootstrap/js/bootstrap-popover.js",
				"lib/bootstrap/js/bootstrap-scrollspy.js",
				"lib/bootstrap/js/bootstrap-tab.js",
				"lib/bootstrap/js/bootstrap-tooltip.js",
				"lib/bootstrap/js/bootstrap-transition.js",
				"lib/bootstrap/js/bootstrap-typeahead.js",
				"lib/almond.js",
				"lib/jquery.js",
				"lib/require.js",
				// DATA
				"sample/datasource.js",
				// SRC
				"src/all.js",
				"src/combobox.js",
				"src/datagrid.js",
				"src/loader.js",
				"src/pillbox.js",
				"src/search.js",
				"src/spinner.js"
		};

		final ClassLoader classLoader = this.getClass().getClassLoader();
		for(String res : resourcesToCopy) {
			final InputStream is = classLoader.getResourceAsStream(resourcesPrefix + res);
			final File f = new File(path, res);

			try {
				FileUtils.copyInputStreamToFile(is, f);
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}


		// récupère le index.html
		final InputStream indexStream = classLoader.getResourceAsStream(resourcesPrefix + "index.html");


		try {
			List<String> strs = IOUtils.readLines(indexStream);

			String lineFormat = 
					"\"class\": \"{0}\", \"method\": \"{1}\", \"rule\": \"{2}\", \"version\": \"{3}\", \"status\": \"<span class=\\\"label {4}\\\">{5}</span>\"";

			StringBuffer sb = new StringBuffer();
			for(String str : strs) {
				sb.append(str);
				sb.append("\n");
			}

			StringBuffer jsonData = new StringBuffer("");


			List<Line> sortedLines = new ArrayList<Line>(lines.values());
			Collections.sort(sortedLines);

			for(Line l : sortedLines) {
				if(l._rules.isEmpty()) {

					String className = l._method._classname;
					String methodName = l._method._methodname;
					String status = l._status.toString();

					String statusStyle = "label-success";
					if(l._status == StatusType.DONE) {
						statusStyle = "label-success";
					}
					if(l._status == StatusType.ONGOING) {
						statusStyle = "label-warning";
					}
					if(l._status == StatusType.TODO) {
						statusStyle = "label-important";
					}
					if(l._status == StatusType.TESTED) {
						statusStyle = "label-info";
					}

					String line = MessageFormat.format(lineFormat, className, methodName, "", "", statusStyle, status);

					jsonData.append("{");
					jsonData.append(line);
					jsonData.append("} ,\n");
				}
				else {
					for(Rule r : l._rules) {

						String className = l._method._classname;
						String methodName = l._method._methodname;
						String ruleName = r._name;
						String version = r._version;
						String status = l._status.toString();

						String statusStyle = "label-success";
						if(l._status == StatusType.DONE) {
							statusStyle = "label-success";
						}
						if(l._status == StatusType.ONGOING) {
							statusStyle = "label-warning";
						}
						if(l._status == StatusType.TODO) {
							statusStyle = "label-important";
						}
						if(l._status == StatusType.TESTED) {
							statusStyle = "label-info";
						}

						String line = MessageFormat.format(lineFormat, className, methodName, ruleName, version, statusStyle, status);

						jsonData.append("{");
						jsonData.append(line);
						jsonData.append("} ,\n");
					}
				}

			}


			String content = sb.toString();

			//System.out.println(content);

			content = content.replace("{{DATA}}", jsonData.toString());

			String date = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
			content = content.replace("{{DATE}}", date);

			// je remplace les chaines
			final File indexFile = new File(path, "index.html");
			FileUtils.write(indexFile, content);


		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
