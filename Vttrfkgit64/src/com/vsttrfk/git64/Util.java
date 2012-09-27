package com.vsttrfk.git64;

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

public abstract class Util {
	public static byte[] getBytesFromFile(File binDump) throws FileNotFoundException, IOException{
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

	/**
	 * 
	 * @param data
	 *            a rectangular matrix
	 * @return array
	 */
	public static byte[] matrixToArray(byte[][] data) {
		final byte[] result = new byte[data.length * data[0].length];
		for (int i = 0; i < result.length; i++) {
			result[i] = data[i / data[0].length][i % data[0].length];
		}
		return result;
	}

	public static boolean writeCardToFile(VsttrfkCard card) {
		final byte[] data = matrixToArray(card.getData());
		String uid = "";
		for (int i = 0; i < 2; i++) { // 5 is full id.
			uid += Util.toHexString(data[i]).toUpperCase();
		}
		
		int copy = 0;
		File outputFile = new File(".");
		do{
		outputFile = new File(Environment
				.getExternalStorageDirectory().getAbsolutePath()
				+ "/"
				+ uid
				+ "-"+(int)card.getBalance()+"kr"+(copy != 0 ? "-"+copy : "")+ ".mfd");
//				+ new SimpleDateFormat("yyMMddHHmmss").format(Calendar
//						.getInstance().getTime()) + ".mfd");
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

	public static int byteToInt(byte[] bytes, int offset) {
		int ret = 0;
		for (int i = 0; i < 4 && i + offset < bytes.length; i++) {
			ret <<= 8;
			ret |= (int) bytes[i] & 0xFF;
		}
		return ret;
	}

	public static String toHexString(int i){
		String result = Integer.toHexString(i);
		
		
		if (i < 0) {
			result= result.substring(6);
		}
		if (i >= 0 && i < 10) {
			result= "0" + result;
		}
		return result;
	}

	public static boolean arrayEqual(byte[] id, byte[] id2) {
		if(id.length == id2.length){
			for(int i = 0 ; i < id.length; i++){
				if(id[i] != id2[i]){
					return false;
				}
			}
			return true;
		}
		return false;
	}

	public static boolean isInt(String text){
			for(int i = 0 ; i < text.length(); i++){
				if(!Character.isDigit(text.charAt(i))){
					return false;
				}
			}
		return true;
	}
}
