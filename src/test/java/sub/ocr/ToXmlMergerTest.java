package sub.ocr;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

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

	private File readFile(String file) throws FileNotFoundException {
		File dir = new File(System.getProperty("user.dir") + "/src/test/resources");
		return new File(dir, file);
	}

}
