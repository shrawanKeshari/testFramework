package com.gemini.solutions.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.testng.Reporter;

public class FileUtil {
	
	public File writeByte(byte[] contentBytes, String fileName) {
		File file = new File(fileName);

		try {

			// Initialize a pointer
			// in file using OutputStream
			OutputStream os = new FileOutputStream(file);

			// Starts writing the bytes in it
			os.write(contentBytes);
			Reporter.log("Successfully" + " byte inserted");

			// Close the file
			os.close();
		}

		catch (Exception e) {
			Reporter.log("Exception: " + e);
			return null;
		}

		return file;
	}
	
}
