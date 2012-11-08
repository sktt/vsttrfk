package com.vsttrfk.git64.cards;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.nfc.tech.MifareClassic;
import android.util.Log;

import com.vsttrfk.git64.auth.IRKFAuthable;
import com.vsttrfk.git64.tools.FileIO;
import com.vsttrfk.git64.tools.MfcIO;

public class RKFCardFactory {

	public static RKFCard createCard(MifareClassic mfc) throws IOException{
		if(!mfc.isConnected()){
			mfc.connect();
		}
		RKFCard result = null;
		if(mfc.authenticateSectorWithKeyA(0, IRKFAuthable.KEYS_A_VSTTRFK[0])){
			result = new VsttrfkCard(MfcIO.getInstance().readMfc(mfc,IRKFAuthable.KEYS_A_VSTTRFK));
		}
		if(mfc.authenticateSectorWithKeyA(0, IRKFAuthable.KEYS_A_JOJO[0])){
			result = new JojoCard(MfcIO.getInstance().readMfc(mfc,IRKFAuthable.KEYS_A_JOJO));
		}

		return result;
	}
	public static RKFCard createCard(String path) throws FileNotFoundException, IOException{
		RKFCard result = null;
		final byte[][] data = readPath(path);
		
		// MUST HAEV BESSER WEG ZU DETERMAIN TIS. cant find proper tcci bits
		if(data[1][10] == (byte)0x00){
			result = new VsttrfkCard(data);
		}
		if(data[1][10] == (byte)0x04){
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
		final byte[] bytes = FileIO.getInstance().getBytesFromFile(binDump);
		// split into blocks. it actually makes sense
		for (int i = 0; i < MifareClassic.SIZE_1K; i++) {
			data[i / data[0].length][i % data[0].length] = bytes[i];
		}
		return data;
	}
}
