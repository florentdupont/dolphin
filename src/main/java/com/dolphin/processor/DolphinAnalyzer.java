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
import com.dolphin.processor.metamodel.Method;
import com.dolphin.processor.metamodel.MethodRepository;
import com.dolphin.processor.metamodel.Rule;
import com.dolphin.processor.metamodel.RuleRepository;

/**
 * Analyseur d'annotations.
 * 
 * @author Florent Dupont
 */
public class DolphinAnalyzer {

	private BusinessReport businessReport;

	/** liste des chemins de JAR à scanner */
	private List<URL> jarPaths;
	
	/** prefixe à scanner*/
	private String namespacePrefixFilter;

	/** répertoire d'export*/
	private String logPath;
	
	/** mode debug */
	private boolean debug;
	
	/** type d'export */
	private String type;
	
	
	
	public DolphinAnalyzer(List<URL> jarPaths, String namespacefilter, String logPath, String type, boolean debug) {
		this.jarPaths = jarPaths;
		this.namespacePrefixFilter = namespacefilter;
		this.logPath = logPath;
		this.businessReport = new BusinessReport();
		this.debug = debug;
		this.type = type;
	}
	
	
	public void analyzeClasses() {
		
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
			// en HTML, on sort également le XLS
			businessReport.writeToXls(logPath);
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
		
	//	StatusType classDevelopmentStatus = null;
		String classname = getClassName(cn.name);
		
		// a ce stade, il peut s'agir d'une class qui peut contenir des méthodes annotées ou pas.
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
					
					//
					// businessReport.addLine(classname, methodname, status);
					Method m = MethodRepository.instance().create(classname, methodname);
					
					m.setStatus(status);
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
						String version = "N/A";
						for(int i=0; i < ids.size(); i++) {
							
							String id = ids.get(i);
							
							try {
								version = versions.get(i);	
							}catch (IndexOutOfBoundsException e) {
								//e.printStackTrace();
								//version = "N/A";
								// si c'est pas renseigné, alors on prend la valeur précédente. 
							}
									
							Method m = MethodRepository.instance().create(classname, methodname);
							Rule rule = RuleRepository.instance().create(id, version);
							m.addRule(rule);
						}
					
					}
					
				}
			}
		}

	}
	
	
	/**
	 *  ici, on vérifie qu'un nom "brut" comme Lcom/dolphin/BusinessRule;
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