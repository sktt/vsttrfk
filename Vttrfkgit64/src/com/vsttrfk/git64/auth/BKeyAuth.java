package com.vsttrfk.git64.auth;

import java.io.IOException;

import android.nfc.tech.MifareClassic;

public class BKeyAuth extends AbstractAuther {

	private MifareClassic mfcDevice;

	public BKeyAuth(MifareClassic mfcDevice) {
		this.mfcDevice = mfcDevice;
	}

	@Override
	public boolean auth(int sector, byte[] tryKey) throws IOException {
		return mfcDevice.authenticateSectorWithKeyB(sector, tryKey);
	}
}
