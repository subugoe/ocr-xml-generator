package sub.ocr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

public class PosChecker {

	private static int count = 0;
	private static File inputDir;

	public static void main(String[] args) throws IOException {
		inputDir = new File(args[0]);

		checkFiles(inputDir);
	}

	private static void checkFiles(File currentDir) throws IOException {
		System.out.println(currentDir.getName());
		count++;
		System.out.println(count);
		File[] currentDirChildren = currentDir.listFiles();
		for (File child : currentDirChildren) {
			boolean isWantedFile = child.getName().endsWith(".pos");
			if (child.isFile() && isWantedFile) {
				checkCurrentFile(child);
			} else if (child.isDirectory()) {
				checkFiles(child);
			}
		}
	}

	private static void checkCurrentFile(File child) throws FileNotFoundException, IOException {
		FileInputStream stream = new FileInputStream(child);
		BufferedReader posReader = new BufferedReader(new InputStreamReader(stream));

		String positionLine = "";
		while ((positionLine = posReader.readLine()) != null) {
			String startNumber = extract("\\ss=\"(.*?)\"", positionLine);
			String endNumber = extract("\\se=\"(.*?)\"", positionLine);
			
			if ("".equals(startNumber) && "".equals(endNumber)) {
				posReader.close();
				return;
			}
			if (!startNumber.equals(endNumber)) {
				FileUtils.writeStringToFile(new File("/home/dennis/nl-hosting/bad-coords.txt"), child.getAbsolutePath() + "\n", true);
				System.out.println(child.getAbsolutePath());
				posReader.close();
				return;
			}
		}
		posReader.close();
	}

	private static String extract(String regex, String s) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(s);
		while (matcher.find()) {
			return matcher.group(1);
		}
		return "";
	}

}
