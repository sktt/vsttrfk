package com.vsttrfk.git64;

import android.nfc.tech.MifareClassic;

public interface IVsttrfkAuthable {

	public static final byte[][] KEYS_A = {
		{(byte)0xFC, (byte)0x00, (byte)0x01, (byte)0x87, (byte)0x78, (byte)0xF7 },
		{(byte)0x02, (byte)0x97, (byte)0x92, (byte)0x7C, (byte)0x0F, (byte)0x77 },
		{(byte)0x54, (byte)0x72, (byte)0x61, (byte)0x76, (byte)0x65, (byte)0x6C}
	};
	public static final byte[][] KEYS_B = {
		{(byte)0x00, (byte)0x00, (byte)0x0F, (byte)0xFE, (byte)0x24, (byte)0x88},
		{(byte)0xEE, (byte)0x00, (byte)0x42, (byte)0xF8, (byte)0x88, (byte)0x40},
		{(byte)0x77, (byte)0x69, (byte)0x74, (byte)0x68, (byte)0x75, (byte)0x73}
	};
	
	public MifareClassic getMfcDevice();
	public boolean authToSector(int sector);
}