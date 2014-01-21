package com.vsttrfk.git64.tools;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.nfc.tech.MifareClassic;
import android.os.Environment;

public class FileIO {
	private static FileIO instance;

	private FileIO(){}
	
	public static FileIO getInstance(){
		if(instance == null){
			instance = new FileIO();
		}
		return instance;
	}
	public byte[] getBytesFromFile(File binDump) throws FileNotFoundException, IOException{
		byte[] fileData = new byte[MifareClassic.SIZE_1K];
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InputStream inputStream = new FileInputStream(binDump);

		DataOutputStream dos = new DataOutputStream(baos);
		int count = inputStream.read(fileData);
		while (count != -1) {
			dos.write(fileData, 0, count);
			count = inputStream.read(fileData);
		}
		inputStream.close();
		dos.close();
		baos.close();

		return baos.toByteArray();
	}
	public boolean writeCardToFile(byte[][] cardData, String fileName) {
		final byte[] data = Util.matrixToArray(cardData);
		
		int copy = 0;
		File outputFile = new File(".");
		do{
		outputFile = new File(Environment
				.getExternalStorageDirectory().getAbsolutePath()
				+ "/"
				+ fileName 
				+ (copy != 0 ? "-"+copy : "")+ ".mfd"); 
				// if dupe, filename is appended "-1"
		copy++;
		} while (outputFile.exists());
		try {
			OutputStream outputStream = new FileOutputStream(outputFile);
			outputStream.write(data);
			outputStream.close();

		} catch (FileNotFoundException e1) {
			System.out.println("ERROR: " + outputFile.getAbsolutePath()
					+ " not found");
			return false;
		} catch (IOException e2) {
			System.out.println("Couldn't write data to file");
			return false;
		}
		return true;
	}
	
}
