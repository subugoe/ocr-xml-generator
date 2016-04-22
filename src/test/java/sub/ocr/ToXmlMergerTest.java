package sub.ocr;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ToXmlMergerTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws Exception {
		ToXmlMerger merger = new ToXmlMerger();
		
		InputStream ocrText = readFile("test.txt");
		InputStream positionsText = readFile("test.pos");
		ByteArrayOutputStream outXml = new ByteArrayOutputStream();

		merger.merge(ocrText, positionsText, outXml);
		
		System.out.println(new String(outXml.toByteArray()));
	}
	
	private InputStream readFile(String file) throws FileNotFoundException {
		File dir = new File(System.getProperty("user.dir") + "/src/test/resources");
		return new FileInputStream(new File(dir, file));
	}


}
