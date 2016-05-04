package sub.ocr;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class FilesExtractor {

	private static File inputDir;
	private static File outputDir;
	
	public static void main(String[] args) throws IOException {
		inputDir = new File(args[0]);
		outputDir = new File(args[1]);

		copyFiles(inputDir);
	}

	private static void copyFiles(File currentDir) throws IOException {
		File[] currentDirChildren = currentDir.listFiles();
		for (File child : currentDirChildren) {
			boolean isWantedFile = child.getName().endsWith(".txt") || child.getName().endsWith(".pos")
					|| child.getName().endsWith(".tif");
			if (child.isFile() && isWantedFile) {
				//System.out.println(child.getAbsolutePath());
				int inputDirLength = inputDir.getAbsolutePath().length();
				String subPath = currentDir.getAbsolutePath().substring(inputDirLength);
				
				File currentOutputDir = null;
				if ("".equals(subPath)) {
					currentOutputDir = outputDir;
				} else {
					currentOutputDir = new File(outputDir.getAbsolutePath() + subPath);
				}
				if (!currentOutputDir.exists()) {
					currentOutputDir.mkdirs();
				}
				File currentOutputFile = new File(currentOutputDir, child.getName());
				FileUtils.copyFile(child, currentOutputFile);
			} else if (child.isDirectory()) {
				copyFiles(child);
			}
		}
	}
}
