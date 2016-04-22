package sub.ocr;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

public class ToXmlMerger {

	public void merge(InputStream ocrText, InputStream positionsText, OutputStream xmlOutput) throws IOException {
		StringBuilder builder = new StringBuilder("");
		builder.append("<document><page width=\"4086\" height=\"4914\"><block blockType=\"Text\"><text><par><line><formatting>\n");

		BufferedReader textReader = new BufferedReader(new InputStreamReader(ocrText));
		BufferedReader posReader = new BufferedReader(new InputStreamReader(positionsText));

		int c;
		String positionLine = "";
		int previousLeft = 0;
		while ((c = textReader.read()) != -1 && (positionLine = posReader.readLine()) != null) {
			char character = (char) c;
			if (c != '\r' && c != '\n') {
				
				int currentLeft = Integer.parseInt(extract("\\sl=\"(.*?)\"", positionLine));
				boolean startNewLine = currentLeft < previousLeft;
				if (startNewLine) {
					builder.append("</formatting></line>\n");
					builder.append("<line><formatting>\n");					
				}
				
				String coordinates = extract("\\s(l=.*b=\".*?\")", positionLine);
				builder.append("  <charParams " + coordinates + ">" + character + "</charParams>\n");

				previousLeft = currentLeft;
			}
		}

		
		
		
		builder.append("</formatting></line></par></text></block></page></document>");

		IOUtils.write(builder.toString(), xmlOutput);
	}

	private String extract(String regex, String s) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(s);
		while (matcher.find()) {
			return matcher.group(1);
		}
		return "";
	}

}
