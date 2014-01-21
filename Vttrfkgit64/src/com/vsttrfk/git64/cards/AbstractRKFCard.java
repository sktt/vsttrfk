package com.vsttrfk.git64.cards;

import java.io.IOException;

import com.vsttrfk.git64.tools.BlockId;
import com.vsttrfk.git64.tools.RKFCard;
import com.vsttrfk.git64.tools.Util;

public abstract class AbstractRKFCard implements RKFCard {

	protected byte[][] data;

	public AbstractRKFCard(){
		// Empty constructor
	}
	
	public AbstractRKFCard(byte[][] data) throws IOException {
		this.data = data;
	}

	public byte[][] getData() {
		return data;
	}

	public void setData(byte[][] data){
		this.data = data;
	}
	public byte[] getId() {
		byte[] result = new byte[4];

		for (int i = 0; i < 4; i++) {
			result[i] = data[0][i];
		}
		return result;
	}

	public double getOldBalance() {
		int bBlock = getBalanceBlock();
		int purseStart = getBlock(BlockId.PURSE);
		return getBalanceFromBlock(
				data[purseStart + 1 + (bBlock - purseStart)% 2]);
	}

	public double getBalance() {
		return getBalanceFromBlock(data[getBalanceBlock()]);
	}

	private double getBalanceFromBlock(byte[] balanceBlock) {
		final String balancePosNeg = Util.toHexString(balanceBlock[6]);
		int value = 0;
		value = Util.byteToInt(new byte[] { balanceBlock[5], balanceBlock[4] },
				0);
		if (balancePosNeg.charAt(0) == '7') {
			value -= 0xFFFF;
		}
		return value / 25.0;
	}

	protected abstract int getBalanceBlock();

	protected int getBlock(BlockId idEnum) {
		final byte purseId = idEnum.getId();
		int i = 0;
		while (data[i][0] != purseId && i < data.length) {
			i++;
		}
		return i;
	}

}
