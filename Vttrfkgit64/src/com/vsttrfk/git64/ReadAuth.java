package com.vsttrfk.git64;

import java.io.IOException;

import android.nfc.tech.MifareClassic;

public class ReadAuth implements IVsttrfkAuthable {

	private MifareClassic mfcDevice;

	public MifareClassic getMfcDevice() {
		return mfcDevice;
	}

	public boolean authToSector(int sector) {
		int i = 0;
		boolean success = true;
		try {
			while (i < KEYS_A.length) {
				if (mfcDevice.authenticateSectorWithKeyA(sector, KEYS_A[i++])) {
					success = true;
					break;
				}

				success = false;
			}
		} catch (IOException e) {
			success = false;
		}
		return success;
	}

	public ReadAuth(MifareClassic mfcDevice) {
		this.mfcDevice = mfcDevice;
	}

}
