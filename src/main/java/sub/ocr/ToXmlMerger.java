package sub.ocr;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;

public class ToXmlMerger {

	private File privatePosText;

	private static Map<String, String> specialChars = new HashMap<String, String>();

	static {
		specialChars.put("<", "&lt;");
		specialChars.put(">", "&gt;");
		specialChars.put("&", "&amp;");
		specialChars.put("\"", "&quot;");
		specialChars.put("'", "&apos;");
	}

	public void merge(File image, File ocrText, File positionsText, OutputStream xmlOutput) throws IOException {
		privatePosText = positionsText;

		BufferedImage tifBuffer = ImageIO.read(image);
		StringBuilder builder = new StringBuilder("");
		builder.append("<document><page width=\"" + tifBuffer.getWidth() + "\" height=\"" + tifBuffer.getHeight()
				+ "\"><block blockType=\"Text\"><text><par><line><formatting>\n");

		BufferedReader textReader = new BufferedReader(new InputStreamReader(new FileInputStream(ocrText)));
		BufferedReader posReader = new BufferedReader(new InputStreamReader(new FileInputStream(positionsText)));

		int c;
		String positionLine = "";
		int previousLeft = 0;
		int previousCharNumberInXml = -1;
		while ((c = textReader.read()) != -1) {

			if (c != '\r' && c != '\n' && c != 65279) {
				positionLine = posReader.readLine();
				if (positionLine == null || positionLine.trim().equals("")) {
					break;
				}
				boolean positionsAreInXmlTags = positionLine.startsWith("<R");

				if (positionsAreInXmlTags) {
					int currentCharNumber = Integer.parseInt(extract("\\ss=\"(.*?)\"", positionLine));
					for (int i = previousCharNumberInXml + 1; i < currentCharNumber; i++) {
						c = textReader.read();
					}
					previousCharNumberInXml = currentCharNumber;
				}

				int currentLeft = 0;
				if (positionsAreInXmlTags) {
					currentLeft = Integer.parseInt(extract("\\sl=\"(.*?)\"", positionLine));
				} else {
					currentLeft = getLeftCoordinate(positionLine);
				}
				boolean startNewLine = currentLeft < previousLeft;
				if (startNewLine) {
					builder.append("</formatting></line>\n");
					builder.append("<line><formatting>\n");
				}

				String coordinates = null;
				if (positionsAreInXmlTags) {
					coordinates = extract("\\s(l=.*)\\sc=", positionLine);
				} else {
					coordinates = construct(positionLine);
				}
				char character = (char) c;
				String charString = "" + character;
				if (specialChars.containsKey(charString)) {
					charString = specialChars.get(charString);
				}
				builder.append("  <charParams " + coordinates + ">" + charString + "</charParams>\n");

				previousLeft = currentLeft;
			}
		}

		textReader.close();
		posReader.close();

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

	private String construct(String line) {
		String[] coords = getCoordinates(line);
		return "l=\"" + coords[0] + "\" t=\"" + coords[1] + "\" r=\"" + coords[2] + "\" b=\"" + coords[3] + "\"";
	}

	private int getLeftCoordinate(String line) {
		String[] coords = getCoordinates(line);
		return Integer.parseInt(coords[0]);
	}

	private String[] getCoordinates(String line) {
		String[] coords = line.split(",");
		if (coords.length != 5) {
			// TODO: Warning
			throw new RuntimeException("Illegal line: " + line + " in " + privatePosText.getAbsolutePath());
		}
		return coords;
	}

}
