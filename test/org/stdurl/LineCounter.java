package org.stdurl;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Counts lines of code under /src, runs as a JUnit test and outputs the number of lines
 * to the console.
 */
public class LineCounter {
	@Test
	public void countLines() {
		File root = new File("src");
		System.out.println(this.countLines(root) + " lines counted.");
	}

	private boolean isJavaFileOrDirectory(File file) {
		if (file.isDirectory()) return true;
		String name = file.getName();
		int lastPoint = name.lastIndexOf('.');
		return lastPoint != -1 && "JAVA".equalsIgnoreCase(name.substring(lastPoint + 1));
	}

	private int countLines(File file) {
		if (file == null || !file.exists()) return 0;

		if (file.isDirectory())
			//noinspection ConstantConditions
			return Arrays.stream(file.listFiles())
					.filter(this::isJavaFileOrDirectory)
					.mapToInt(this::countLines)
					.sum();
		else {
			try {
				FileInputStream fis = new FileInputStream(file);
				// who on earth can write code longer than 2147483648 characters?
				byte[] data = new byte[(int) file.length()];
				int offset = 0;
				while (fis.available() > 0)
					offset += fis.read(data, offset, data.length - offset);

				String str = new String(data);
				return (int) Arrays.stream(str.split(Pattern.quote("\n")))
						.map(String::trim)
						.map(String::isEmpty)
						.count();
			} catch (Exception e) {
				return 0;
			}
		}
	}
}
