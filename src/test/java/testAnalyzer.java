
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.runner.RunWith;

import com.dolphin.processor.DolphinAnalyzer;

@RunWith(JUnit4ClassRunner.class)
public class testAnalyzer {

		
	@Test
	public void test2() {
		// recupère la ressource dans les tests
		URL url = this.getClass().getClassLoader().getResource("annotationSample.jar");
		List<URL> jarPaths = new ArrayList<URL>();
		jarPaths.add(url);
		
		DolphinAnalyzer analyzer = new DolphinAnalyzer(jarPaths, "com.rsi", "/Users/flo/projets/annotations/export", true);
		analyzer.analyseClasses();
		
	}
	
	
	
	
}
