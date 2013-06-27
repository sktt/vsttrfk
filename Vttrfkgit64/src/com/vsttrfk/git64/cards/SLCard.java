package com.vsttrfk.git64.cards;

import java.io.IOException;

import com.vsttrfk.git64.auth.IRKFAuthable;

public class SLCard extends AbstractRKFCard {

	private final String provider = "SL";
	
	public SLCard(byte[][] data) throws IOException {
		super(data);
	}

	public SLCard() {
		super();
	}

	public byte[][] getKeysA() {
		return IRKFAuthable.KEYS_A_SL;
	}

	public byte[][] getKeysB() {
		return IRKFAuthable.KEYS_B_SL;
	}

	public boolean anonymousExploit() {
		return false; // not implemented
	}

	public String getProvider() {
		return provider;
	}

	@Override
	protected int getBalanceBlock() {
		return 0; // not implemented
	}

}
