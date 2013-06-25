package com.vsttrfk.git64.tools;

import java.io.IOException;

import android.nfc.TagLostException;
import android.nfc.tech.MifareClassic;
import android.util.Log;

import com.vsttrfk.git64.auth.AKeyAuth;
import com.vsttrfk.git64.auth.BKeyAuth;
import com.vsttrfk.git64.auth.IRKFAuthable;

public class MfcIO {
	private static MfcIO instance;

	private MfcIO(){}
	
	public static MfcIO getInstance(){
		if(instance == null){
			instance = new MfcIO();
		}
		return instance;
	}

	public byte[][] readMfc(MifareClassic mfc, byte[][] keys) throws IOException{
		byte[][] data = new byte[64][16];
		if (!mfc.isConnected()) {
			try{
				mfc.connect();
			} catch(IllegalStateException e){
				throw new TagLostException("card was removed");
			}
		}
		IRKFAuthable readAuth = new AKeyAuth(mfc);
		for (int i = 0; i < data.length; i++) {

			// every 4th block is a new sector... try to auth..
			if (i % 4 == 0 && !readAuth.authToSector(i / 4, keys)) {
				throw new IOException("Unable to auth to sector :" + i / 4
						+ ".");
			}

			data[i] = mfc.readBlock(i);
		}
		return data;
	}
	public int writeMfc(MifareClassic mfc, byte[][] data, byte[][] keys) throws IOException{
	
		if (!mfc.isConnected()) {
			mfc.connect();
		}
		int errors = 0;
		boolean success = false;
		// first 4 blocks are manufacturer's read-only
		for (int i = 4; i < data.length; i++) {
			success = false;
			try {
				success = writeMfc(mfc,i,data[i],keys);
			} catch(IOException e){
				Log.w("VSTTRFK", e.getMessage());
			} finally{
				if(!success){
					errors++;
				}
			}
		}
		return errors;
	}
	
	public static boolean writeMfc(MifareClassic mfc, int block, byte[] blockData, byte[][] keys) throws IOException{
		final IRKFAuthable writeAuth = new BKeyAuth(mfc);
		
		// every 4th block is a new sector... try to auth..
		if (block % 4 == 0 && !writeAuth.authToSector(block / 4, keys)) {
			return false;
		}
		
		if (block % 4 != 3) { // blcok 3 (fjärde blocket) håller i keys, vill ej skriva hit
			mfc.writeBlock(block, blockData);
		}
		return true;
	}
}
