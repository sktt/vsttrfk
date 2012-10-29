package com.vsttrfk.git64.cards;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.vsttrfk.git64.auth.IRKFAuthable;
import com.vsttrfk.git64.auth.ReadAuth;
import com.vsttrfk.git64.tools.Util;

import android.nfc.TagLostException;
import android.nfc.tech.MifareClassic;
import android.util.Log;

public class RKFCardFactory {

	public static RKFCard createCard(MifareClassic mfc) throws IOException{
		RKFCard result = null;
		if(mfc.authenticateSectorWithKeyA(0, IRKFAuthable.KEYS_A_VSTTRFK[0])){
			result = new VsttrfkCard(readMfc(mfc,IRKFAuthable.KEYS_A_VSTTRFK));
		}
		if(mfc.authenticateSectorWithKeyA(0, IRKFAuthable.KEYS_A_JOJO[0])){
			result = new JojoCard(readMfc(mfc,IRKFAuthable.KEYS_A_JOJO));
		}
		return result;
	}
	public static RKFCard createCard(String path) throws FileNotFoundException, IOException{
		RKFCard result = null;
		final byte[][] data = readPath(path);
		final byte[] firstAKey = new byte[6];
		for(int i = 0; i < firstAKey.length; i++){
			firstAKey[i] = data[3][i];
		}
		if(firstAKey.equals(IRKFAuthable.KEYS_A_VSTTRFK)){
			result = new VsttrfkCard(data);
		}
		if(firstAKey.equals(IRKFAuthable.KEYS_A_JOJO)){
			result = new JojoCard(data);
		}
		return result;
	}
	private static byte[][] readPath(String path) throws FileNotFoundException, IOException{
		final byte[][] data = new byte[64][16];
		final File binDump = new File(path);
		if(!binDump.exists()){
			Log.e("IO Error", "File: "+ binDump.getAbsolutePath() + " does not exsist!!");
		}
		final byte[] bytes = Util.getBytesFromFile(binDump);
		// split into blocks. it actually makes sense
		for (int i = 0; i < MifareClassic.SIZE_1K; i++) {
			data[i / data[0].length][i % data[0].length] = bytes[i];
		}
		return data;
	}
	private static byte[][] readMfc(MifareClassic mfc, byte[][] keys) throws IOException{
		byte[][] data = new byte[64][16];
		if (!mfc.isConnected()) {
			try{
				mfc.connect();
			} catch(IllegalStateException e){
				throw new TagLostException("card was removed");
			}
		}
		IRKFAuthable readAuth = new ReadAuth(mfc);
		for (int i = 0; i < data.length; i++) {

			// every 4th block is a new sector... try to auth..
			if (i % 4 == 0 && !readAuth.authToSector(i / 4, IRKFAuthable.KEYS_A_JOJO)) {
				throw new IOException("Unable to auth to sector :" + i / 4
						+ ".");
			}

			data[i] = mfc.readBlock(i);
		}
		return data;
	}
}
