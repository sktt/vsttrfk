package com.vsttrfk.git64.cards;

import java.io.IOException;

import android.nfc.TagLostException;

import com.vsttrfk.git64.tools.BlockId;
import com.vsttrfk.git64.tools.Util;

public class JojoCard extends AbstractRKFCard {


	public static final byte[][] KEYS_A_JOJO = {
			{ (byte) 0x43, (byte) 0x4f, (byte) 0x4d, (byte) 0x4d, (byte) 0x4f,
					(byte) 0x41 },
			{ (byte) 0x47, (byte) 0x52, (byte) 0x4f, (byte) 0x55, (byte) 0x50,
					(byte) 0x41 },
			{ (byte) 0x50, (byte) 0x52, (byte) 0x49, (byte) 0x56, (byte) 0x54,
					(byte) 0x41 } };
	public static final byte[][] KEYS_B_JOJO = {
			{ (byte) 0x43, (byte) 0x4f, (byte) 0x4d, (byte) 0x4d, (byte) 0x4f,
					(byte) 0x42 },
			{ (byte) 0x47, (byte) 0x52, (byte) 0x4f, (byte) 0x55, (byte) 0x50,
					(byte) 0x42 },
			{ (byte) 0x50, (byte) 0x52, (byte) 0x49, (byte) 0x56, (byte) 0x54,
					(byte) 0x42 } };

	
	/**
	 * Create card object from nfc-source
	 * 
	 * @param mfc
	 */
	public JojoCard(byte[][] data) throws TagLostException, IOException {
		super(data);
	}

	public JojoCard() {
		super();
	}

	public String getProvider(){
		return "Jojo";
	}
	
	public boolean anonymousExploit() {
		final int i = getBlock(BlockId.PURSE);

		// determine if the card was already sploited

		// switch places between the two balance blocks.
		final byte[] temp = data[i + 1];
		data[i + 1] = data[i + 2];
		data[i + 2] = temp;

		return true;
	}
	
	protected int getBalanceBlock(){
		final int i = getBlock(BlockId.PURSE);
		final String blockSelect = Util.toHexString(data[12][3]);
		int result = 0 ;
		if (blockSelect.charAt(1) == '2') {
			result = i + 2;
		}
		if (blockSelect.charAt(1) == '1') {
			result = i + 1;
		}

		return result;
	}


	public byte[][] getKeysA() {
		return KEYS_A_JOJO;
	}


	public byte[][] getKeysB() {
		return KEYS_B_JOJO;
	}
	

}
