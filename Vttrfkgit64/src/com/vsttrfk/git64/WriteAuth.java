package com.vsttrfk.git64;

import java.io.IOException;

import android.nfc.tech.MifareClassic;

public class WriteAuth implements IVsttrfkAuthable {

	private MifareClassic mfcDevice;
	
	public MifareClassic getMfcDevice() {
		return mfcDevice;
	}

	public boolean authToSector(int sector) {
		int i = 0;
		boolean success = true;
		try {
			while(i < KEYS_B.length && !mfcDevice.authenticateSectorWithKeyB(sector, KEYS_B[i++])){
				success = false;
			}
		} catch(IOException e){
			success = false;
		}
		return success;
	}
	
	public WriteAuth(MifareClassic mfcDevice){
		this.mfcDevice = mfcDevice;
	}
}
