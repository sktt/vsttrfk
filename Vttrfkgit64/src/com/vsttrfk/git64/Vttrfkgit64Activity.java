package com.vsttrfk.git64;

import java.io.IOException;

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
import android.widget.TextView;

public class Vttrfkgit64Activity extends Activity {
	private MifareClassic mfcDevice;
	private TextView statusBox;
	private VsttrfkCard loadedCard;
	private EditText filePathEditText;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.design2); // print some haxor ascii..
											// *important*
		statusBox = (TextView) findViewById(R.id.statusBox);
		statusBox.requestFocus();
		filePathEditText = (EditText) findViewById(R.id.filePath);
		statusBox.setMovementMethod(new ScrollingMovementMethod());

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
			try {
				loadedCard = new VsttrfkCard(mfcDevice);
			} catch (IOException e) {
				statusBox.append("Fel vid läsning: \n" + e.getMessage());
				return;
			}
			statusBox.append("vstfk0rt inläst... Saldo: "
					+ loadedCard.getBalance() + "\n");

		} else {
			statusBox.append("yue put cardz on m3 first plx\n");
		}
	}

	public void writeFileAction(View view) {
		if (loadedCard == null) {
			statusBox.append("ing3t 1nl43s7 k0r7 -_-\n");
		} else {
			statusBox.append(loadedCard.saveToFile() ? "d0n3!\n"
					: "Failed to write f1l3\n");
		}
	}

	public void writeNfc(VsttrfkCard card) throws IOException {
		final byte[][] data = card.getData();
		if (!mfcDevice.isConnected()) {
			mfcDevice.connect();
		}
		IVsttrfkAuthable writeAuth = new WriteAuth(mfcDevice);
		// first 4 blocks are manufacturer's read-only
		for (int i = 4; i < data.length; i++) {
			// every 4th block is a new sector... try to auth..
			if (i % 4 == 0 && !writeAuth.authToSector(i / 4)) {
				statusBox.append("Unable to auth to sect0r: " + i + ".\n");
			}
			if (i % 4 != 3) {
				boolean success = true;
				try {
					mfcDevice.writeBlock(i, data[i]);
				} catch (IOException e) {
					success = false;
				}

			}
		}
	}

	public void writeNfcAction(View view) {
		if (loadedCard != null) {

			mfcDevice = getCardFromReader(getIntent());
			try {
				writeNfc(this.loadedCard);
			} catch (IOException e) {
				statusBox.append("Error connecting\n");
			}
			statusBox.append("d0n3\n");
		} else {
			statusBox.append("ing3t 1nl43s7 k0r7 -_-\n");

		}
	}

	public void readFileAction(View view) {
		loadedCard = new VsttrfkCard(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/" + filePathEditText.getText());
		statusBox.append("vstfk0rt inläst... Saldo: " + loadedCard.getBalance()
				+ "\nNu kan du skriva till nfc!\n");

	}

	public void anonymousExploitAction(View view) throws TagLostException,
			IOException {

		mfcDevice = getCardFromReader(getIntent());
		if (mfcDevice != null) {
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
	}

}