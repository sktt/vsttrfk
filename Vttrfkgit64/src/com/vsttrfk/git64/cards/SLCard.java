package com.vsttrfk.git64.cards;

import java.io.IOException;

public class SLCard extends AbstractRKFCard {

	private final String provider = "SL";
	public static final byte[][] KEYS_A_SL = {

			{ (byte) 0xfc, (byte) 0x00, (byte) 0x01, (byte) 0x87, (byte) 0x78,
					(byte) 0xf7 },
			{ (byte) 0xa6, (byte) 0x45, (byte) 0x98, (byte) 0xa7, (byte) 0x74,
					(byte) 0x78 },
			{ (byte) 0x26, (byte) 0x94, (byte) 0x0b, (byte) 0x21, (byte) 0xff,
					(byte) 0x5d } };

	public final static byte[][] KEYS_B_SL = {
			{ (byte) 0x00, (byte) 0x00, (byte) 0x0f, (byte) 0xfe, (byte) 0x24,
					(byte) 0x88 },
			{ (byte) 0x5c, (byte) 0x59, (byte) 0x8c, (byte) 0x9c, (byte) 0x58,
					(byte) 0xb5 },
			{ (byte) 0xe4, (byte) 0xd2, (byte) 0x77, (byte) 0x0a, (byte) 0x89,
					(byte) 0xbe } };

	public SLCard(byte[][] data) throws IOException {
		super(data);
	}

	public SLCard() {
		super();
	}

	public byte[][] getKeysA() {
		return KEYS_A_SL;
	}

	public byte[][] getKeysB() {
		return KEYS_B_SL;
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
