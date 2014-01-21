package com.vsttrfk.git64;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.nfc.tech.MifareClassic;
import android.util.Log;

import com.vsttrfk.git64.cards.JojoCard;
import com.vsttrfk.git64.cards.SLCard;
import com.vsttrfk.git64.cards.VsttrfkCard;
import com.vsttrfk.git64.tools.CallbackHandler;
import com.vsttrfk.git64.tools.FileIO;
import com.vsttrfk.git64.tools.MfcIO;
import com.vsttrfk.git64.tools.RKFCard;

public class RKFCardFactory {

	public static void createCard(MifareClassic mfc, CallbackHandler<RKFCard> callback) throws IOException{
		if(!mfc.isConnected()){
			mfc.connect();
		}
		RKFCard result = null;
		// TODO: get a solution that makes sence. ie user input
		if(mfc.authenticateSectorWithKeyA(3, VsttrfkCard.KEYS_A_VSTTRFK[1])){
			result = new VsttrfkCard();
		}
		if(mfc.authenticateSectorWithKeyA(0, JojoCard.KEYS_A_JOJO[0])){
			result = new JojoCard();
		}
		if(mfc.authenticateSectorWithKeyA(3, SLCard.KEYS_A_SL[0])){
			result = new SLCard();
		}
		MfcIO.getInstance().readMfcAsync(mfc, result, callback);

	}
	public static RKFCard createCard(String path) throws FileNotFoundException, IOException{
		RKFCard result = null;
		final byte[][] data = readPath(path);
		
		// TODO: Better way to determine card provider here as well.
		// ie user input or meta data from storage
		if(data[1][2] == (byte)0x02){
			result = new VsttrfkCard(data);
		}
		if(data[1][2] == (byte)0x83){
			result = new JojoCard(data);
		}
		if(data[1][2] == (byte)0x44){
			result = new SLCard(data);
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
