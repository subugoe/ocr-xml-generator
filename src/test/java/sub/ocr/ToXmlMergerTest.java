package sub.ocr;

import java.io.File;
import java.io.FileNotFoundException;

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

		File tifImage = readFile("testFiles/Ab/Ab.tif");
		File ocrText = readFile("testFiles/Ab/Ab.txt");
		File positionsText = readFile("testFiles/Ab/Ab.org_pos");
		ByteArrayOutputStream outXml = new ByteArrayOutputStream();

		merger.merge(tifImage, ocrText, positionsText, outXml);

		System.out.println(new String(outXml.toByteArray()));
	}
	
	@Test
	public void shouldAcceptXmlCoords() throws Exception {
		ToXmlMerger merger = new ToXmlMerger();

		File tifImage = readFile("testFiles/Ab-with-xml-coords/Ab.tif");
		File ocrText = readFile("testFiles/Ab-with-xml-coords/Ab.txt");
		File positionsText = readFile("testFiles/Ab-with-xml-coords/Ab.pos");
		ByteArrayOutputStream outXml = new ByteArrayOutputStream();

		merger.merge(tifImage, ocrText, positionsText, outXml);

		System.out.println(new String(outXml.toByteArray()));
	}

	private File readFile(String file) throws FileNotFoundException {
		File dir = new File(System.getProperty("user.dir") + "/src/test/resources");
		return new File(dir, file);
	}

}
