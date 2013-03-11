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
 * Fonctionne sur un projet final (artefact type JAR) ou sur un parent (qui crée des modules de types JAR
 * sur 1 ou plusieurs niveaux)
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
    * Mon paramètre.
    * @parameter expression="${logpath}"
    * @required
    */
    private String logpath;	
    
    /**
     * préfixe des namespace de classes à scanner
     * @parameter expression="${namespaceprefix}"
     * @required
     */
     private String namespaceprefix;	
     
  
    public void execute() throws MojoExecutionException {
    	
        List<URL> urls = new ArrayList<URL>();
        
        
        for(MavenProject reactproject : reactorProjects) {
        	
    		//System.out.println("Maven project : " + reactproject.getName());
    		//System.out.println("Maven project : " + reactproject.getArtifact().getType());
    		
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
        	System.out.println("artefacts tirés : " + url);
        }
        
        final DolphinAnalyzer analyzer = new DolphinAnalyzer(urls, namespaceprefix, logpath);
	    analyzer.analyseClasses();
        
    }
}