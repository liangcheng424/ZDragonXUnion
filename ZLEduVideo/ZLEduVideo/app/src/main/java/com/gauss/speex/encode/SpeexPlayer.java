/**
 * 
 */
package com.gauss.speex.encode;

import java.io.File;

/**
 * @author Gauss
 * 
 */
public class SpeexPlayer {
	private String fileName = null;
	private SpeexDecoder speexdec = null;
	private OnCompletionListener onCompletionListener;

	public SpeexPlayer(String fileName,
			OnCompletionListener onCompletionListener) {
		this.onCompletionListener = onCompletionListener;
		this.fileName = fileName;
		System.out.println(this.fileName);
		try {
			speexdec = new SpeexDecoder(new File(this.fileName));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void startPlay() {
		RecordPlayThread rpt = new RecordPlayThread();

		Thread th = new Thread(rpt);
		th.start();
	}

	boolean isPlay = true;
	public void stopPlay() {
		speexdec.stopPlay();
	}

	class RecordPlayThread extends Thread {

		public void run() {
			try {
				if (speexdec != null)
					speexdec.decode(onCompletionListener);

			} catch (Exception t) {
				t.printStackTrace();
			}
		}
	};

	public interface OnCompletionListener {
		void onCompletion();
	}

}
