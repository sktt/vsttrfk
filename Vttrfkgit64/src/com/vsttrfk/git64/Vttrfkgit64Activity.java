package com.vsttrfk.git64;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class Vttrfkgit64Activity extends Activity {
	private MifareClassic mfcDevice;
	private TextView statusBox;
	private VsttrfkCard loadedCard;

	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main); // print some haxor ascii.. *important*
		statusBox = (TextView) findViewById(R.id.statusBox);
	}

	private MifareClassic getCardFromReader(Intent intent) {
		MifareClassic result = null;
		if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
			result = MifareClassic.get((Tag) intent
					.getParcelableExtra(NfcAdapter.EXTRA_TAG));
		} else {
			Log.e("ERROR", "Unknown: " + intent + "\nAction: " + intent.getAction());
		}
		return result;
	}

	public void readAction(View view) {
		mfcDevice = getCardFromReader(getIntent());
		if (mfcDevice != null) {
			try {
				loadedCard = new VsttrfkCard(mfcDevice);
			} catch (IOException e) {
				statusBox.append("tappade anslutning, blev avbruten, eller misslyckades med auth..\n");
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
			statusBox.append(loadedCard.saveToFile()?
					"d0n3!\n":"Failed to write f1l3\n");
		}
	}

	public void writeNfc(VsttrfkCard card) throws IOException {
		final byte[][] data = card.getData();
		if (!mfcDevice.isConnected()) {
			mfcDevice.connect();
		}
		for (int i = 0; i < data.length; i++) {
			// every 4th block is a new sector... try to auth..
			if (i % 4 == 0 && !VsttrfkCard.authSector(mfcDevice, i/4, 1)) {
				statusBox.append("Unable to auth to sect0r: "+i+".\n");
			}
			if (i % 4 != 3) {
				boolean success = true;
				try {
					mfcDevice.writeBlock(i, data[i]);
				} catch (IOException e) {
					success = false;
				}
				statusBox.append((success?"wr0t3 t0 bl0ck ":"Ph41l3d to pwn block: ")
						+ i + "!\n");
			} 
		}
	}

	public void writeNfcAction(View view) {
		if (loadedCard != null) {
			try {
				writeNfc(this.loadedCard);
			} catch (IOException e) {
				statusBox.append("Something went wrong....sry\n");
				e.printStackTrace();
			}
			statusBox.append("d0n3\n");
		} else {
			statusBox.append("ing3t 1nl43s7 k0r7 -_-\n");

		}
	}
}