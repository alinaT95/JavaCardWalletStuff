package com.tonjubiterreactnativetest.api.nfc;


import android.content.Context;
import android.nfc.tech.IsoDep;

/**
 * Created by lxl on 2019/4/22.
 */
public class ImplNfc {
	private Context implContext;
	private NfcComm implCommunicateWithCard;
	private String ver = "v1.2.2";
	
	public ImplNfc(Context mContext) {
		implContext = mContext;
		implCommunicateWithCard = new NfcComm(implContext);
	}
	
	public void setNfcIsoDepTag(final IsoDep nfcIsoDepTag) {
		implCommunicateWithCard.setNfcIsoDepTag(nfcIsoDepTag);
	}
	
	public String getVersion() {
		return ver;
	}

	public int connect(){
		return implCommunicateWithCard.connect();
	}
	
	public int transInstructions(byte[] instructionBytes, byte[] result, int[] resultLen) {
		return implCommunicateWithCard.transInstructions(instructionBytes, result, resultLen);
	}

	public int disConnect(){
		return implCommunicateWithCard.disconnect();
	}
}
