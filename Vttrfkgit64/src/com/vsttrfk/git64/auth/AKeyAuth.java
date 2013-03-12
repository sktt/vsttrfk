package com.vsttrfk.git64.auth;

import java.io.IOException;

import android.nfc.tech.MifareClassic;

public class AKeyAuth extends AbstractAuther {

	private MifareClassic mfcDevice;

	public AKeyAuth(MifareClassic mfcDevice) {
		this.mfcDevice = mfcDevice;
	}
	
	@Override
	public boolean auth(int sector, byte[] tryKey) throws IOException {
		return mfcDevice.authenticateSectorWithKeyA(sector, tryKey);
	}


}
