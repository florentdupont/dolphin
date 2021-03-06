package com.dolphin.mojo;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import com.dolphin.processor.DolphinAnalyzer;

/**
 * 
 * Lancement se fait : 
 * mvn package -DskipTests=true com.dolphin:dolphin:dolphin
 * 
 * Deux possibilité de parametrage
 * 
 * Avec Configuration Maven : a définir dans le POM du projet que l'on souhaite analyser
 * 
 * <plugin>
 *	 <groupId>com.dolphin</groupId>
 *		<artifactId>dolphin</artifactId>
 *		<version>1.2</version>
 *		 <configuration>
 *  		<path>/Users/flo/projets/annotations/export</path>
 *  		<namespaceprefix>com.mypackage.test</namespaceprefix>
 *          <type>html</type>
 *          
 *  	</configuration>
 *  </plugin>
 * 
 * Sans configuration Maven :
 * Il faut dans ce cas définir les variables en ligne de commande
 * mvn package -DskipTests=true com.dolphin:dolphin-maven-plugin:dolphin -PdolphinPrefix=com.mypackage.test
 * 
 * ou
 * 
 * mvn package -DskipTests=true dolphin:dolphin -PdolphinPrefix=com.mypackage.test
 * 
 * L'export se fera dans le répertoire courant sans configuration manve.
 * 
 * 
 * 
 * Fonctionne sur un projet final (artefact type JAR) ou sur un parent (qui crée des modules de types JAR
 * sur 1 ou plusieurs niveaux)
 * 
 * 
 *
 * @goal dolphin
 * @phase package
 * @aggregator true
 *  
 */
public class DolphinMojo extends AbstractMojo {
    
    /** 
     * @parameter default-value="${project}" 
     **/
    private org.apache.maven.project.MavenProject mavenProject;
    
    
    /**
     * The projects in the reactor.
     *
     * @parameter expression="${reactorProjects}"
     * @readonly
     */
    private List<MavenProject> reactorProjects;
    
    /**
    * chemin d'export.
    * @parameter expression="${path}"
    * 
    */
    private String path;	
    
    /**
     * préfixe des namespace de classes à scanner.
     * @parameter expression="${namespaceprefix}"
     *
     */
     private String namespaceprefix;	
     
     
     /**
      * type d'export : all(default) xls, html, csv 
      * @parameter expression="${type}"
      *
      */
      private String type;	
      
      
      /**
       * mode debug 
       * @parameter expression="${debug}"
       *
       */
       private Boolean debug;	
     
  
    public void execute() throws MojoExecutionException {
    	
        List<URL> urls = new ArrayList<URL>();
        
        
        for(MavenProject reactproject : reactorProjects) {
        	 		
    		if("jar".equals(reactproject.getArtifact().getType()) || "ejb".equals(reactproject.getArtifact().getType())) {
        		//System.out.println("JAR : " + reactproject.getName());
    			try {
    				
    				Artifact artifact = reactproject.getArtifact();
    				System.out.println(artifact);
    				File artifactFile = artifact.getFile();
    				if(artifact.getFile() == null) {
    					System.out.println("no packaged file associated to the artifact. Execute this plugin in association with mvn package.");
    				}
    				else {
    					URL depUrl = artifactFile.toURI().toURL();
    					if(!urls.contains(depUrl)) {
   			    		 urls.add(depUrl);
   			    	 	}
    				}
	    			
    			 } catch (MalformedURLException e) {
    				e.printStackTrace();
    			}
    		}
        }
        
        
        for(URL url : urls) {
        	getLog().info("artefacts tirés : " + url);
        }
        
        
        // si le logpath n'est pas renseigné alors on prend le répertoire renseigné dans la variable dolphinPath
        if(path == null) {
        	path = System.getProperty("dolphinPath");
        }
        // si le path est toujours nul alors, on prend le répertoire courant.
        if(path == null) {
        	path = System.getProperty("user.dir") + File.separator + "target" + File.separator + "dolphin" + File.separator;
        }
        
        // si le namespace prefix n'est pas spécifié dans la config maven, alors on va le chercher dans 
        // la variable JVM dolphinPrefix
        if(namespaceprefix == null) {
        	namespaceprefix = System.getProperty("dolphinPrefix");
        }
        if(namespaceprefix == null) {
        	namespaceprefix = "";
        }
        
        if(type == null) {
        	type = System.getProperty("dolphinType");
        }
        if(type == null) {
        	type = "all";
        }
        
        if(debug == null) {
        	debug = Boolean.valueOf(System.getProperty("dolphinDebug"));
        }
        if(debug == null) {
        	debug = false;
        }
        
        
        getLog().info("Exporting to path: " + path);
        getLog().info("Export type: " + type);
        getLog().info("namespace prefix to analyze :" + namespaceprefix);
        if(debug)
        	getLog().info("MODE DEBUG");
        
        final DolphinAnalyzer analyzer = new DolphinAnalyzer(urls, namespaceprefix, path, type, debug);
	    analyzer.analyzeClasses();
        
    }
}
