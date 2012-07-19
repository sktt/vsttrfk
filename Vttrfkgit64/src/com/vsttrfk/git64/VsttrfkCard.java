package com.vsttrfk.git64;

import java.io.File;
import java.io.IOException;

import android.nfc.TagLostException;
import android.nfc.tech.MifareClassic;

public class VsttrfkCard {
	
	public static final byte[][] KEYS_A = {
		{(byte)0xFC, (byte)0x00, (byte)0x01, (byte)0x87, (byte)0x78, (byte)0xF7 },
		{(byte)0x02, (byte)0x97, (byte)0x92, (byte)0x7C, (byte)0x0F, (byte)0x77 },
		{(byte)0x54, (byte)0x72, (byte)0x61, (byte)0x76, (byte)0x65, (byte)0x6C}
	};
	public static final byte[][] KEYS_B = {
		{(byte)0x00, (byte)0x00, (byte)0x0F, (byte)0xFE, (byte)0x24, (byte)0x88},
		{(byte)0xEE, (byte)0x00, (byte)0x42, (byte)0xF8, (byte)0x88, (byte)0x40},
		{(byte)0x77, (byte)0x69, (byte)0x74, (byte)0x68, (byte)0x75, (byte)0x73}
	};
	
	private final byte[][] data = new byte[64][16]; // 16 sectors each with four lines, 16 bytes per line
	
	/**
	 * Create card object from nfc-source
	 * @param mfc
	 */
	public VsttrfkCard(MifareClassic mfc) throws TagLostException, IOException {
		if(!mfc.isConnected()){
			mfc.connect();
		}
		for (int i = 0 ;  i < data.length; i++){
			
			// every 4th block is a new sector... try to auth..
			if ( i % 4 == 0 && !authSector(mfc, i/4)){
				throw new IOException("Unable to auth to sector :"+i/4+".");
			}
			
			data[i] = mfc.readBlock(i);
		}
	
	}
	
	/**
	 * Create card object a saved dump
	 * @param path
	 */
	public VsttrfkCard(String path) {
		final File binDump = new File(path);
		final byte[] bytes = Util.getBytesFromFile(binDump);
		// split into blocks. it actually makes sense
		for (int i = 0 ; i < MifareClassic.SIZE_1K	; i++) {
			data[i / data[0].length][i % data[0].length] = bytes[i];
		}
	}
	
	public byte[][] getData(){
		return data;
	}
	/**
	 * Save to file to use later
	 * @return
	 */
	public boolean saveToFile(){
	
		return Util.writeBytesToFile(Util.matrixToArray(data));
		
	}

	public double getBalance() {
		final byte purseId = (byte)0x85;
		int i = 0;
		while(data[i][0] != purseId && i < data.length){
			i++;
		}
		int value = data[i+1][0] > data[i+2][0] ? 
				Util.byteToInt(new byte[]{data[i+1][5],data[i+1][4]},0) : 
				Util.byteToInt(new byte[]{data[i+2][5],data[i+2][4]},0);
		return value/25.0;
	}

	public static boolean authSector(MifareClassic mfcDevice, int sector) throws IOException{
		// first three always the first key.
		int j = sector / 4 < 3 ? 0 : 1; 
		// skip this if at sector 3.
		while (!mfcDevice.authenticateSectorWithKeyB(sector, KEYS_B[j++])) {
			if (j > KEYS_B.length) {
				// no a key worked..
				return false;
			}
		}
		return true;
	}
	
	
	
}
