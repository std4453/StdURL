package test;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 *
 */
public class CountLines {
	public static void main(String[] args) {
		File root = new File("src/org");
		System.out.println(countLines(root));
	}

	private static int countLines(File file) {
		if (file == null || !file.exists()) return 0;

		if (file.isDirectory())
			//noinspection ConstantConditions
			return Arrays.stream(file.listFiles()).mapToInt(CountLines::countLines).sum();
		else {
			try {
				FileInputStream fis = new FileInputStream(file);
				byte[] data = new byte[(int) file.length()];
				int offset = 0;
				while (fis.available() > 0)
					offset += fis.read(data, offset, data.length - offset);

				String str = new String(data);
				return (int) Arrays.stream(str.split(Pattern.quote("\n")))
						.map(String::trim)
						.map(String::isEmpty).count();
			} catch (Exception e) {
				return 0;
			}
		}
	}
}
