package sub.ocr;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class Main {

	private static File inputDir;
	private static File outputDir;
	private static ToXmlMerger merger = new ToXmlMerger();
	private static int count = 0;

	public static void main(String[] args) throws IOException {
				
		if (args.length != 2) {
			System.out.println("Syntax: java -jar ocr-xml-generator.jar <input-dir> <output-dir>");
			System.exit(1);
		}
		
		inputDir = new File(args[0]);
		outputDir = new File(args[1]);

		processFiles(inputDir);
	}

	private static void processFiles(File currentDir) throws IOException {
		if (currentDir.getName().equals("navxml")) {
			return;
		}
		count++;
		System.out.println(count + ": " + currentDir.getAbsolutePath());
		File[] currentDirChildren = currentDir.listFiles();
		for (File child : currentDirChildren) {
			if (child.isFile() && child.getName().endsWith(".tif")) {
				File tifFile = child;
				File posFile = changeToPosFile(tifFile);
				File textFile = changeToTextFile(tifFile);

				File currentOutputDir = prepareOutputDir(currentDir);
				File currentOutputFile = new File(currentOutputDir, tifFile.getName().replace(".tif", ".xml"));
				if (currentOutputFile.exists()) {
					continue;
				}

				if (!posFile.exists() || !textFile.exists()) {
					String error = "Text or pos file not found: " + posFile.getAbsolutePath() + "\n";
					FileUtils.write(new File("/home/dennis/nl-hosting/errors2.txt"), error, true);
					continue;
				}

				merger.merge(tifFile, textFile, posFile, new FileOutputStream(currentOutputFile));

			} else if (child.isDirectory()) {
				processFiles(child);
			}
		}
	}

	private static File changeToPosFile(File tifFile) {
		File parentDir = tifFile.getParentFile();
		File posFile = new File(parentDir, tifFile.getName().replace(".tif", ".org_pos"));
		if (posFile.exists()) {
			return posFile;
		} else {
			return new File(parentDir, tifFile.getName().replace(".tif", ".pos"));
		}
	}

	private static File changeToTextFile(File tifFile) {
		File parentDir = tifFile.getParentFile();
		return new File(parentDir, tifFile.getName().replace(".tif", ".txt"));
	}

	private static File prepareOutputDir(File currentInputDir) {
		int inputDirLength = inputDir.getAbsolutePath().length();
		String subPath = currentInputDir.getAbsolutePath().substring(inputDirLength);

		File currentOutputDir = null;
		if ("".equals(subPath)) {
			currentOutputDir = outputDir;
		} else {
			currentOutputDir = new File(outputDir.getAbsolutePath() + subPath);
		}
		if (!currentOutputDir.exists()) {
			currentOutputDir.mkdirs();
		}
		return currentOutputDir;
	}

}
