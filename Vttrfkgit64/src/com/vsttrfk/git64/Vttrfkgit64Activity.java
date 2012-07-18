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
				statusBox.append("tappade anslutning ell3r blev avbruten..\n");
				return;
			}
			statusBox.append("vstfk0rt inläst... Saldo: "
					+ loadedCard.getBalance() + "\n");

		} else {
			statusBox.append("yue put cardz on m3 first plx\n");
		}
	}

	public void writeFileAction(View view) {
		if (loadedCard != null) {
			loadedCard.saveToFile();
			statusBox.append("d0n3!\n");
		} else {
			statusBox.append("ing3t 1nl43s7 k0r7 -_-\n");
		}
	}

	public void authenticate() {
		if (mfcDevice == null) {
			throw new RuntimeException("Damn, must resolveIntent.");
		}
		for (int i = 0; i < 16; i++) {
			int j = 0;
			try {
				while (!mfcDevice.authenticateSectorWithKeyA(i,
						VsttrfkCard.KEYS_A[j])) {
					j++;
					if (j >= VsttrfkCard.KEYS_A.length) {
						throw new RuntimeException("Expected VsttrfkCard..");
					}
				}
			} catch (IOException e) {
				// I/O failure or the operation is canceled
			}
		}
	}

	public void writeNfc(VsttrfkCard card) throws IOException {
		final byte[][] data = card.getData();
		// authenticate();
		// for (int i = 63; i < data.length; i++) {
		// mfcDevice.writeBlock(i, data[i]);
		// statusBox.append("sector " + i + " success\n");
		// }
		if (!mfcDevice.isConnected()) {
			mfcDevice.connect();
		}
		for (int i = 0; i < data.length; i++) {
			System.out.println("block:" + i + "\t sector:" + i / 4);

			// every 4th block is a new sector... try to auth..
			if (i % 4 == 0) {
				boolean authed = false;
				int j = i / 4 < 3 ? 0 : 1; // first three always the first key.
											// skip this if at sector 3.
				while (!authed) {
					authed = mfcDevice.authenticateSectorWithKeyB(i / 4,
							VsttrfkCard.KEYS_B[j++]);
					Log.e("info", "authed to sector :" + i / 4 + ":" + authed);
					if (j > VsttrfkCard.KEYS_B.length) {
						// no a key worked..
						throw new IOException(
								"This is n0 v4lid vstrfkcrad..n0 w0rk1ng k3y :/");
					}
				}
			}
			if (i % 4 != 3) {
				try {
					mfcDevice.writeBlock(i, data[i]);
				} catch (IOException e) {
					Log.d("VARNING", "Skrev inte till block:" + i);
				}
				statusBox.append("wr0t3 t0 bl0ck " + i + "!!!!\n");
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