package com.dolphin.processor;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.util.TraceClassVisitor;

import com.dolphin.api.StatusType;
import com.dolphin.processor.debug.MyClassVisitor;

/**
 * Analyseur d'annotations.
 * 
 * @author Florent Dupont
 */
public class DolphinAnalyzer {

	private BusinessReport businessReport;

	/** liste des chemins de JAR à scanner */
	private List<URL> jarPaths;
	
	private String namespacePrefixFilter;

	private String logPath;
	
	private boolean debug;
	
	private String type;
	
	
	
	public DolphinAnalyzer(List<URL> jarPaths, String namespacefilter, String logPath, String type, boolean debug) {
		this.jarPaths = jarPaths;
		this.namespacePrefixFilter = namespacefilter;
		this.logPath = logPath;
		this.businessReport = new BusinessReport();
		this.debug = debug;
		this.type = type;
	}
	
	
	/**
	 * 
	 */
	public void analyseClasses() {
		
		System.out.println(jarPaths);
		System.out.println(namespacePrefixFilter);
		System.out.println(debug);
		System.out.println(type);
		
		for (URL path : jarPaths) {
			try {
				final JarFile jar = new JarFile(path.getFile());
				final Enumeration<JarEntry> entries = jar.entries();
				while (entries.hasMoreElements()) {
					final JarEntry entry = entries.nextElement();
					if (isClass(entry)) {
						final String classname = getClassname(entry);
						if (classname.startsWith(namespacePrefixFilter)) {
							System.out.println("Analyzing class " + classname);
							try {
								InputStream jarIS =	jar.getInputStream(entry);
								ClassReader classReader = new ClassReader(jarIS);

								// plus facile avec cette API la
								ClassNode cn = new ClassNode(Opcodes.ASM4);
								
								classReader.accept(cn, 0);
								analyseClass(cn);
								
								if (debug) {
									PrintWriter printWriter = new PrintWriter(System.out);
									TraceClassVisitor traceClassVisitor = new TraceClassVisitor(printWriter);
									MyClassVisitor classVisitor = new MyClassVisitor(traceClassVisitor);
									classReader.accept(classVisitor, ClassReader.SKIP_DEBUG);
								}
																

							} catch (Throwable e) {
								e.printStackTrace();
								System.out.println("unable to load " + classname);
							}
						}
					}
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		
		
		
		if("all".equals(type)) {
			// ecrit en CSV
			businessReport.writeToCSV(logPath);

			businessReport.writeToHtml(logPath);
			
			businessReport.writeToXls(logPath);
		}
		else if("html".equals(type)) {
			businessReport.writeToHtml(logPath);
		}
		else if("xls".equals(type)) {
			businessReport.writeToXls(logPath);
		}
		else if("csv".equals(type)) {
			businessReport.writeToXls(logPath);
		}
		
		// mode debug
		if(debug) {
			businessReport.writeToConsole();
		}
	}

	private boolean isClass(JarEntry entry) {
		return entry.getName().endsWith(".class");
	}

	/**
	 * Renomme le nom de l'entrée 'com/dolphin/MyClass.class' en nom
	 * de class compréhensible par le class loader
	 * 'com.dolphin.MyClass'
	 * 
	 * @param entryName
	 * @return
	 */
	private String getClassname(JarEntry entry) {
		String name = entry.getName();
		return name.replace('/', '.').substring(0, name.length() - 6);
	}

	public void analyseClass(ClassNode cn) {
		
		StatusType classDevelopmentStatus = null;
		String classname = getClassName(cn.name);
		
		if(cn.visibleAnnotations != null) {
			// récupère la classe
			for (Object o : cn.visibleAnnotations) {
				final AnnotationNode an = (AnnotationNode) o;
				
				// seul le developmentStatus nous intéresse ici
				if (isDevelopmentAnnotation(an.desc)) {
					// ici, on considère que la taille est bonne sinon, on aurait
					// pas pu compiler !
					String[] arrs = (String[]) an.values.get(1);
					// com/dolphin/StatusType
					// valeur du statusType que l'on peux récupérer sur
					// l'énumération
					StatusType status = StatusType.valueOf(arrs[1]);
					classDevelopmentStatus = status;
				}
			}
		}
		
		// a ce stade, il peut s'agir d'une class annotée avec un DevStatus ou pas
		// et qui peut contenir des méthodes annotées ou pas.
		// donc il ne faut qu'on les ajoutes toutes

		// récupère chaque méthode
		for (Object om : cn.methods) {
			MethodNode mn = (MethodNode) om;
			
			String methodname = mn.name;
			
			if("<init>".equals(methodname))
				continue;
			
			// on ne prend la méthode en compte que si elle est publique
			if((mn.access & Opcodes.ACC_PUBLIC) == 0) {
				continue;
			}
			
			// si la classe est annoté...alors toutes les méthodes hériteront de 
			// ce status
			if(classDevelopmentStatus != null ) {
				// je met le status par défaut, hérité de la classe.
				// si le status existe dans l'annotation, il sera alors écrasé spécifiquement
				// a sa définition sur la méthode
				businessReport.addLine(classname, methodname, classDevelopmentStatus);
			}
			
			// s'il n'y a pas d'annotations, pas la peine de continuer,
			if(mn.visibleAnnotations == null) {
				continue;
			}

			// récupère toutes les annotations
			for (Object o : mn.visibleAnnotations) {
				final AnnotationNode an = (AnnotationNode) o;
				
				// le developmentStatus nous intéresse ici
				if (isDevelopmentAnnotation(an.desc)) {
					// ici, on considère que la taille est bonne sinon, on
					// aurait pas pu compiler !
					String[] arrs = (String[]) an.values.get(1);
					// Dans arrs[0] : com/dolphin/StatusType
					// Dans arrs[1] : valeur du statusType que l'on peux récupérer sur l'énumération
					StatusType status = StatusType.valueOf(arrs[1]);
					
					businessReport.addLine(classname, methodname, status);
				}
				

				// le developmentStatus nous intéresse ici
				if (isBusinessAnnotation(an.desc)) {

					// la taille est normallement de 4
					if (an.values != null && an.values.size() == 4) {

						// 0 => id
						// 1 => valeurs d'id
						// 2 => version
						// 3 => valeurs de version
						
						@SuppressWarnings("unchecked")
						List<String> ids = (List<String>) an.values.get(1);
						
						@SuppressWarnings("unchecked")
						List<String> versions = (List<String>) an.values.get(3);
						// je me base sur les ids
						for(int i=0; i < ids.size(); i++) {
							
							String id = ids.get(i);
							String version = "N/A";
							try {
								version = versions.get(i);	
							}catch (IndexOutOfBoundsException e) {
								e.printStackTrace();
								version = "N/A";
							}
									
							businessReport.addLine(classname, methodname, id, version);
							
							// s'il n'as pas déjà un devstatus, alors, on lui ajoute celui par défaut
							if(!businessReport.hasDevStatus(classname, methodname)) {
								if(classDevelopmentStatus != null) {
									businessReport.addLine(classname, methodname, classDevelopmentStatus);
								}
								else {
									// sinon j'en met un par défaut
									businessReport.addLine(classname, methodname, StatusType.TODO);
								}
							}
						}
					
					}
					
				}
			}
		}

	
		

	}
	
	
	/**
	 *  ici, on va vérifier qu'un nom "brut" comme Lcom/dolphin/BusinessRule;
	 *  est correspond à une classe d'annotation BusinessRule.
	 *  
	 *  Pour garder l'indépendance avec l'API, notamment pour les projets qui ne souhaitent pas que 
	 *  le projet tire une dépendance Dolphin, on ne fait pas un test complet sur le namespace mais seulement 
	 *  sur le nom de l'annotation.
	 *  Ainsi, une annotation com.dolphin.BusinessRule, tout comme une xxx.xxx.BusinessRule
	 *  sera considérée comme étant Business. 
	 *  
	 * @param rawName
	 * @return
	 */
	public static boolean isBusinessAnnotation(String rawName) {
		String[] splits = rawName.split("/");
		return splits[splits.length-1].equals("BusinessRule;");
	}
	
	/**
	 *  ici, on va vérifier qu'un nom "brut" comme Lcom/dolphin/DevelopmentStatus;
	 *  est correspond à une classe d'annotation DevelopmentStatus.
	 *  
	 *  Pour garder l'indépendance avec l'API Dolphin, notamment pour les projets  qui ne souhaitent pas que 
	 *  le projet tire une dépendance Dolphin, on ne fait pas un test complet sur le namespace mais seulement 
	 *  sur le nom de l'annotation.
	 *  Ainsi, une annotation com.dolphin.DevelopmentStatus, tout comme une xxx.xxx.DevelopmentStatus
	 *  sera considérée comme étant DevelopmentStatus. 
	 *  
	 * @param rawName
	 * @return
	 */
	public static boolean isDevelopmentAnnotation(String rawName) {
		String[] splits = rawName.split("/");
		return splits[splits.length-1].equals("DevelopmentStatus;");
	}
	
	public static String getClassName(String rawName) {
		return rawName.replace('/', '.');
	}

}