package com.vsttrfk.git64.tools;

import java.io.IOException;

import android.nfc.tech.MifareClassic;
import android.os.AsyncTask;
import android.util.Log;

import com.vsttrfk.git64.auth.AKeyAuth;
import com.vsttrfk.git64.auth.BKeyAuth;
import com.vsttrfk.git64.auth.IRKFAuthable;

public class MfcIO {
	private static MfcIO instance;

	private MfcIO(){}
	
	public static MfcIO getInstance(){
		if(instance == null){
			instance = new MfcIO();
		}
		return instance;
	}

	public byte[][] readMfc(MifareClassic mfc, byte[][] keys, CallbackHandler<?> callback){
		byte[][] data = new byte[64][16];
		if (!mfc.isConnected()) {
			try{
				mfc.connect();
			} catch (IOException e) {
				callback.updateStatus("Unable to connect: "+e.getMessage()+"\n");
			}
		}
		for (int i = 0; i < data.length; i++) {
			try{
				data[i] = readMfc(mfc, i, keys, callback);
			} catch (IOException e) {
				callback.updateStatus("BLOCK "+i+": "+e.getMessage()+"\n");
			}
		}
		return data;
	}
	private byte[] readMfc(MifareClassic mfc, int block, byte[][] keys, CallbackHandler<?> callback) throws IOException{
		IRKFAuthable readAuth = new AKeyAuth(mfc);

		String readStatus = "";
		// every 4th block is a new sector... try to auth..
		if (block % 4 == 0) {
			readStatus = "Sector "+block/4+" READ ";
			readStatus += (readAuth.authToSector(block / 4, keys) ? "OK!" : "FAILED!") + "\n";
			callback.updateStatus(readStatus);
		} 
		return mfc.readBlock(block);
		
	}
	public int writeMfc(MifareClassic mfc, byte[][] data, byte[][] keysA, byte[][] keysB) throws IOException{
	
		if (!mfc.isConnected()) {
			mfc.connect();
		}
		int errors = 0;
		boolean success = false;
		// first 4 blocks are manufacturer's read-only
		for (int i = 4; i < data.length; i++) {
			success = false;
			try {
				success = writeMfc(mfc,i,data[i],keysA, keysB);
			} catch(IOException e){
				Log.w("VSTTRFK", e.getMessage());
			} finally{
				if(!success){
					errors++;
				}
			}
		}
		return errors;
	}
	
	public boolean writeMfc(MifareClassic mfc, int block, byte[] blockData, byte[][] keysA, byte[][] keysB) throws IOException{
		final IRKFAuthable AAuth = new AKeyAuth(mfc);
		final IRKFAuthable BAuth = new BKeyAuth(mfc);
		// every 4th block is a new sector... try to auth..
		if (block % 4 == 0 && !BAuth.authToSector(block / 4, keysB) && !AAuth.authToSector(block / 4, keysA)) {
			return false;
		}
		
		if (block % 4 != 3) { // blcok 3 (fjärde blocket) håller i keys, vill ej skriva hit
			try { 
				mfc.writeBlock(block, blockData);
			} catch (IOException e){
				AAuth.authToSector((block) / 4, keysA); // quick and dirty fix for cards where auth is successful with B-key but Still requires A key to write.
				mfc.writeBlock(block, blockData);
			}
		}
		return true;
	}
	
	

	public void readMfcAsync(MifareClassic mfc, final RKFCard result,
			final CallbackHandler<RKFCard> callback) {
		final byte[][] keys = result.getKeysA();
		new AsyncTask<MifareClassic, String, RKFCard>() {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				callback.enableGUI(false);
			}
			@Override
			protected void onPostExecute(RKFCard result) {
				super.onPostExecute(result);
				callback.enableGUI(true);
				callback.updateStatus("Done...\n");
				callback.handleResult(result);
			}
			
			@Override
			protected RKFCard doInBackground(MifareClassic... params) {
				MifareClassic mfc = params[0];
				byte[][] data = new byte[64][16];
				if (!mfc.isConnected()) {
					try{
						mfc.connect();
					} catch (IOException e) {
						publishProgress("Unable to connect: "+e.getMessage()+"\n");
						return null;
					}
				}
				for (int i = 0; i < data.length; i++) {
					try{
						data[i] = readMfc(mfc, i, keys, callback);
					} catch (IOException e) {
						publishProgress("BLOCK "+i+": "+e.getMessage()+"\n");
					}
				}
				
				result.setData(data);
				return result;
			}
			
		
		}.execute(mfc);
		
	}

	public void writeMfcAsync(MifareClassic mfc, final byte[][] data,
			final byte[][] keysA, final byte[][] keysB, final CallbackHandler<?> callback) {
		new AsyncTask<MifareClassic, String, Integer>(){

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				callback.enableGUI(false);
			}
			
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				callback.enableGUI(true);
				callback.updateStatus("\nWrote "+result+" blocks!\n");
			}

			
			@Override
			protected Integer doInBackground(MifareClassic... params) {
				MifareClassic mfc = params[0];
				if (!mfc.isConnected()) {
					try {
						mfc.connect();
					} catch (IOException e) {
						publishProgress("Unable to connect!\n");
						return null;
					}
				}
				boolean success = false;
				int blocksWritten = 0;
				// first 4 blocks are manufacturer's read-only
				for (int i = 4; i < data.length; i++) {
					success = false;
					try {
						success = writeMfc(mfc,i,data[i],keysA,keysB);
					} catch(IOException e){
						Log.w("VSTTRFK", e.getMessage());
					}
					if(success){
						blocksWritten++;
						publishProgress(".");
					} else {
						publishProgress("\nWARNING: Block "+i+" write failed!\n");
					}
				
				}
				return blocksWritten;
			}
			
			@Override
			protected void onProgressUpdate(String... values) {
				super.onProgressUpdate(values);
				callback.updateStatus(values[0]);
			}
			
		}.execute(mfc);
	}
}
