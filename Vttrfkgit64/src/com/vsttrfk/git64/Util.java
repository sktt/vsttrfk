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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.nfc.tech.MifareClassic;
import android.os.Environment;

public abstract class Util {
	public static byte[] getBytesFromFile(File binDump) {
		byte[] fileData = new byte[MifareClassic.SIZE_1K];
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
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

		} catch (FileNotFoundException e1) {
			System.out.println("FILE " + binDump.getAbsolutePath()
					+ " NOT FOUND");
			return null;
		} catch (IOException e2) {
			System.out.println("ERROR HANDLING FILE " + binDump.getName());
			return null;

		}

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

	public static boolean writeBytesToFile(byte[] data) {
		String uid = "";
		for (int i = 0; i < 5; i++) {
			uid += Integer.toHexString(data[i]).substring(0, 2).toUpperCase();
		}
		final File outputFile = new File(Environment
				.getExternalStorageDirectory().getAbsolutePath()
				+ "/"
				+ uid
				+ "-"
				+ new SimpleDateFormat("yyMMddHHmmss").format(Calendar
						.getInstance().getTime()) + ".mfd");
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

	public static String trimByte(String hexString) {

		if (hexString.length() > 2) {
			hexString = hexString.substring(6);
		}
		if (hexString.length() == 1) {
			hexString = "0" + hexString;
		}

		return hexString;
	}
}
