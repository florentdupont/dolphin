package com.dolphin.processor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
import com.dolphin.processor.metamodel.Method;
import com.dolphin.processor.metamodel.MethodRepository;
import com.dolphin.processor.metamodel.Rule;

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

	

	public void writeToCSV(String path) {

		File file = new File(path, "export.csv");

		try {
			// on flush le fichier s'il exsite;
			FileUtils.writeStringToFile(file, "", false);

		
			for(Method m : MethodRepository.instance().findAll()) {
				if(!m.hasImplementedRule()) {
					String data = m.getOwner() + ";" + m.getName() + ";" + " ; ;" + m.getStatus() + "\n";
					FileUtils.writeStringToFile(file, data, true);
				}
				else {
					for(Rule r : m.getImplementedRules()) {
						String data = m.getOwner() + ";" + m.getName() + ";" +r.getName() + ";" + r.getVersion() + ";"+ m.getStatus() + "\n";
						FileUtils.writeStringToFile(file, data, true);
					}
				}
			}
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}


	public void writeToConsole() {

		/*System.out.println("METHODS POINT OF VIEW : ");
		for(Method m : MethodRepository.instance().findAll()) {
			System.out.println(m);
			for(Rule r : m.getImplementedRules()) {
				System.out.println("  " + r);
			}
		}
		
		System.out.println("");
		System.out.println("RULES POINT OF VIEW : ");
		for(Rule r : RuleRepository.instance().findAll()) {
			System.out.println(r);
			for(Method m : r.getImplementedBy()) {
				System.out.println("  " + m);
			}
			
			System.out.println("general status : "  + r.getGeneralStatus());
		}*/
		
		for(Method m : MethodRepository.instance().findAllByStatus(StatusType.DONE)) {
			System.out.println(m);
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


		

		for(Method method : MethodRepository.instance().findAll()) {
			rownum++;
			row = sheet.createRow(rownum);

			cell = row.createCell((short)0, HSSFCell.CELL_TYPE_STRING);
			cell.setCellValue(method.getOwner());

			cell = row.createCell((short)1, HSSFCell.CELL_TYPE_STRING);
			cell.setCellValue(method.getName());

			if(!method.hasImplementedRule()) {

				cell = row.createCell((short)2, HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue("");

				cell = row.createCell((short)3, HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue("");

			}
			else {
				
				
				String ruleName = "";
				String version = "";
				
				for(Rule rule : method.getImplementedRules()) {
					ruleName += rule.getName() + ", ";
					version +=  rule.getName() + ", ";
				}
				
				ruleName = ruleName.substring(0, ruleName.length()-2);
				version = version.substring(0, version.length()-2);
				
				
				cell = row.createCell((short)2, HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue(ruleName);

				cell = row.createCell((short)3, HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue(version);
				
			}

			cell = row.createCell((short)4, HSSFCell.CELL_TYPE_STRING);
			cell.setCellValue(method.getStatus().toString());
			HSSFCellStyle cellStyle = wb.createCellStyle();

			if (method.getStatus() == StatusType.INTEGRATED) {
				cellStyle.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index);
			}
			else if(method.getStatus() == StatusType.TESTED) {
				cellStyle.setFillForegroundColor(HSSFColor.LIGHT_BLUE.index);
			}
			else if (method.getStatus() == StatusType.DONE) {
				cellStyle.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
			}
			else if (method.getStatus() == StatusType.ONGOING) {
				cellStyle.setFillForegroundColor(HSSFColor.LIGHT_ORANGE.index);
			}
			else if (method.getStatus() == StatusType.TODO) {
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
	 * Ici, on fait un suivi par ligne. Une ligne correspond à une opération sur laquelle est apposée : 
	 * un DevelopmentStatus ou un BusinessRule
	 * @param path
	 */
	public void writeToHtml(String path) {
		
		
		// pour l'ecriture dans le rapport HTML, on a pour l'instant un point de vue 
		// pas méthode.
		// class -> methode -> liste des règles implémentées -> avancement
		
		MethodRepository repo = MethodRepository.instance();
		
		// nombre de méthodes
		int nbTotal = repo.count();
		System.out.println("nb count : " + nbTotal);
		
		int nbTodo       = repo.findAllByStatus(StatusType.TODO).size(); 
		int nbOngoing    = repo.findAllByStatus(StatusType.ONGOING).size(); 
		int nbDone       = repo.findAllByStatus(StatusType.DONE).size(); 
		int nbTested     = repo.findAllByStatus(StatusType.TESTED).size();
		int nbIntegrated = repo.findAllByStatus(StatusType.INTEGRATED).size();
		
				
		// ici je dois m'assurer d'avoir un pourcentage == 100%
			
		double pcTodo    = (nbTodo * 100.0/ nbTotal);
		double pcOngoing = (nbOngoing *100.0/ nbTotal);
		double pcDone    = (nbDone * 100.0/ nbTotal);
		double pcTested  = (nbTested * 100.0/ nbTotal);
		double pcIntegrated  = (nbIntegrated * 100.0/ nbTotal);
		
				
		double totpc = pcTodo + pcOngoing + pcDone + pcTested + pcIntegrated;
		
		// System.out.println("total : " + totpc);
		
	//	double[] values = {pcTodo, pcOngoing, pcDone, pcTested, pcIntegrated};
	//	int[] stretchedVals = roundAndStretch(values);
					
		
		
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
				"img/dolphin.png",
				"img/export_excel.png",
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

			for(Method method : MethodRepository.instance().findAll()) {
				// s'il n'y a pas des règle apposée
				if(!method.hasImplementedRule()) {

					String className = method.getOwner();
					String methodName = method.getName();
					String status = method.getStatus().toString();

					String statusStyle = "label-success";
					if(method.getStatus() == StatusType.DONE) {
						statusStyle = "label-success";
					}
					if(method.getStatus() == StatusType.ONGOING) {
						statusStyle = "label-warning";
					}
					if(method.getStatus() == StatusType.TODO) {
						statusStyle = "label-important";
					}
					if(method.getStatus() == StatusType.TESTED) {
						statusStyle = "label-info";
					}
					if(method.getStatus() == StatusType.INTEGRATED) {
						statusStyle = "label-inverse";
					}

					String line = MessageFormat.format(lineFormat, className, methodName, "", "", statusStyle, status);

					jsonData.append("{");
					jsonData.append(line);
					jsonData.append("} ,\n");
				}
				else {
					// s'il y'a des règles
					
					String className = method.getOwner();
					String methodName = method.getName();
					String status = method.getStatus().toString();
					
					String statusStyle = "label-success";
					if(method.getStatus() == StatusType.DONE) {
						statusStyle = "label-success";
					}
					if(method.getStatus() == StatusType.ONGOING) {
						statusStyle = "label-warning";
					}
					if(method.getStatus() == StatusType.TODO) {
						statusStyle = "label-important";
					}
					if(method.getStatus() == StatusType.TESTED) {
						statusStyle = "label-info";
					}
					if(method.getStatus() == StatusType.INTEGRATED) {
						statusStyle = "label-inverse";
					}
					
					String ruleName = "";
					String version = "";
					// REFACTOR
					for(Rule rule : method.getImplementedRules()) {
						ruleName += rule.getName() + ", ";
						version +=  rule.getVersion() + ", ";
					}
					
					ruleName = ruleName.substring(0, ruleName.length()-2);
					version = version.substring(0, version.length()-2);
					
					String line = MessageFormat.format(lineFormat, className, methodName, ruleName, version, statusStyle, status);

					jsonData.append("{");
					jsonData.append(line);
					jsonData.append("} ,\n");
				}

			}

			
			
			// barre
			// si on a 0 pourcent alors, on n'affiche pas.
			
			StringBuilder barbuf = new StringBuilder();
			
			if(nbTodo > 0) {
				barbuf.append("<!-- TODO -->\n");
				barbuf.append("<div class=\"bar bar-danger\"  style=\"width: " + pcTodo + "%;\">" + nbTodo + "</div>\n");
			}
			
			if(nbOngoing > 0) {
				barbuf.append("<!-- ONGOING -->\n");
				barbuf.append("<div class=\"bar bar-warning\"  style=\"width: " + pcOngoing + "%;\">" + nbOngoing + "</div>\n");
			}
			
			if(nbDone > 0) {
				barbuf.append("<!-- DONE -->\n");
				barbuf.append("<div class=\"bar bar-success\"  style=\"width: " + pcDone + "%;\">" + nbDone + "</div>\n");
			}
			
			if(nbTested > 0) {
				barbuf.append("<!-- TESTED -->\n");
				barbuf.append("<div class=\"bar bar-info\"  style=\"width: " + pcTested + "%;\">" + nbTested + "</div>\n");
			}
			
			if(nbIntegrated > 0) {
				barbuf.append("<!-- INTEGRATED -->\n");
				barbuf.append("<div class=\"bar bar-inverse\"  style=\"width: " + pcIntegrated + "%;\">" + nbIntegrated + "</div>\n");
			}
			
			
			String content = sb.toString();

			content = content.replace("{{NBMETHOD}}", ""+nbTotal);
			content = content.replace("{{DATA}}", jsonData.toString());
			content = content.replace("{{PROGRESSBAR}}", barbuf.toString());

			String date = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
			content = content.replace("{{DATE}}", date);

			// je remplace les chaines
			final File indexFile = new File(path, "index.html");
			FileUtils.write(indexFile, content);


		} catch (IOException e) {
			e.printStackTrace();
		}
		
		

	}
	
	/**
	 * 
	 * arrondi chaque valeur (double->int) pour faire en sorte que chaque valeur entière arrondi corresponde
	 * à la somme globale avant l'arrondi.
	 *  fait en sorte que réparti le reste de la somme pour que la valeur attendu soit exactement la somme
	 * de tous les valeurs arrondies.
	 * 
	 * Exemple : 
	 * [46.15, 15.38, 15.38, 15.38, 7.69] -> en arrondissant, j'aurai : [46,15,15,15,7] = 98.
	 * il reste donc 2 que je dois répartir sur les valeurs restantes.
	 * je prend donc les valeurs dont la partie décimale initiale est la plus haute [46,15,15,15,8] = 99
	 * il me reste 1, je continue avec le reste...
	 * Dans mon cas, plusieurs valeurs sont identique, je prend arbitrairement la première pour avoir [46, 16, 15, 15, 8]]
	 * 
	 * Chaque case ne peux être incrémentée qu'une fois.
	 * 
	 * 
	 * @param initialValues
	 * @param expectedSum
	 * @return
	 */
	int[] roundAndStretch(double[] initialValues) {
		
		int length = initialValues.length;
		
		double initialSum = 0.0; // somme de toutes les valeurs initiales
		int expectedSum = 0;     // valeur entière attendue : correspond à initialSum arrondie en int
		int sum = 0;             // somme de toutes les valeurs initiales castées
		
		
		// resultat finaux 
		int[] result = new int[length];
		boolean[] alreadyDispatched = new boolean[length];
		
		
		for(int i = 0; i < initialValues.length; i++) {
			
			// Casting to an int implicitly drops any decimal
			result[i] = (int) initialValues[i];
			
			initialSum += initialValues[i];
			sum += result[i];
		}
		
		
		expectedSum = (int) initialSum;
		System.out.println("expected sum : " + expectedSum);
		System.out.println("actual rounded sum : " + sum);
		
		// calcule le reste de la différence entre la somme initiale arrondie, et la somme des valeurs arrondies.
		int rest =  expectedSum - sum;
		
		while(rest != 0) {
			
			// je trouve la case avec la partie décimale la plus haute
			double maxDec = -1.00;
			int maxIndex = -1;
			
			for(int i = 0; i < initialValues.length; i++) {
				double dec = initialValues[i] - ((int)initialValues[i]);
				// je ne prend pas en compte la valeur si elle a déjà été incrémentée.
				if(!alreadyDispatched[i] && dec > maxDec) {
					maxDec = dec;
					maxIndex = i;
				}
			}
			System.out.println("found value to add 1 to : " + initialValues[maxIndex]);
			result[maxIndex] = result[maxIndex] + 1;
			alreadyDispatched[maxIndex] = true;
			rest--;
			
		}
		
		
		return result;
	}

}
