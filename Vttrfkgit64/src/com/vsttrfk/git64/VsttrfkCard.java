package com.vsttrfk.git64;

import java.io.File;
import java.io.IOException;

import android.nfc.TagLostException;
import android.nfc.tech.MifareClassic;

public class VsttrfkCard {

	private static enum BlockId {
		PURSE((byte) 0x85);

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
			mfc.connect();
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
	 */
	public VsttrfkCard(String path) {
		final File binDump = new File(path);
		final byte[] bytes = Util.getBytesFromFile(binDump);
		// split into blocks. it actually makes sense
		for (int i = 0; i < MifareClassic.SIZE_1K; i++) {
			data[i / data[0].length][i % data[0].length] = bytes[i];
		}
	}

	public byte[][] getData() {
		return data;
	}

	/**
	 * Save to file to use later
	 * 
	 * @return
	 */
	public boolean saveToFile() {

		return Util.writeBytesToFile(Util.matrixToArray(data));

	}

	public boolean anonymousExploit() {
		final int i = getBlock(BlockId.PURSE);
		
		
		if (Util.trimByte(Integer.toHexString(data[8][3])).charAt(1) == '8'
				&& data[i + 1][0] > data[i + 2][0]) {
			return false;
		}

		// switch places with the two balance blocks.
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

	public double getBalance() {
		final int i = getBlock(BlockId.PURSE);
		int value = 0;
		String blockSelect = Integer.toHexString(data[8][3]);
		String balanceBlock1 = Integer.toHexString(data[i + 1][6]);
		String balanceBlock2 = Integer.toHexString(data[i + 2][6]);

		blockSelect = Util.trimByte(blockSelect);
		balanceBlock1 = Util.trimByte(balanceBlock1);
		balanceBlock2 = Util.trimByte(balanceBlock2);

		if (blockSelect.charAt(1) == '8') {
			value = Util.byteToInt(new byte[] { data[i + 2][5],
							data[i + 2][4] }, 0);
			if (balanceBlock2.charAt(0) == '7') {
				value= value - 0xFFFF; 

			}
		} else if (blockSelect.charAt(1) == '4') {
			value = Util.byteToInt(new byte[] { data[i + 1][5], data[i + 1][4] }, 0);
			if (balanceBlock1.charAt(0) == '7') {
				value= value - 0xFFFF; 
			}
		}
		return value / 25.0;
	}
}
