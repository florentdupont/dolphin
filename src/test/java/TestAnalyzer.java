
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.runner.RunWith;

import com.dolphin.processor.DolphinAnalyzer;

@RunWith(JUnit4ClassRunner.class)
public class TestAnalyzer {

	/**
	 * Ignoré dans un premier temps, car je ne comprend par pourquoi mon test-jar est vide
	 * @throws MalformedURLException
	 */
	@Ignore
	@Test
	public void test2() throws MalformedURLException {
		// recupère la ressource dans les tests
		//URL url = this.getClass().getClassLoader().getResource("test.jar-tests.jar");
		
		
		
		String userdir = System.getProperty("user.dir");
		System.out.println(userdir);
		String path = userdir + File.separator + "target" + File.separator + "test.jar-tests.jar";
		
		List<URL> jarPaths = new ArrayList<URL>();
		
		File f = new File(path);
		
		jarPaths.add(f.toURI().toURL());
		
		String export = userdir + File.separator + "target" + File.separator + "export";
			
		
		DolphinAnalyzer analyzer = new DolphinAnalyzer(jarPaths, "com.test", export, "html", true);
		analyzer.analyseClasses();
		
	}
	
	
	
	
}
