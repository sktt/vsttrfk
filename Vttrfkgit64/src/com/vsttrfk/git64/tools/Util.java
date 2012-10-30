package com.vsttrfk.git64.tools;


public abstract class Util {


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
