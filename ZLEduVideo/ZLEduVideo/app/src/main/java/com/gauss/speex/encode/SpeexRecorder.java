package com.gauss.speex.encode;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.zhulong.eduvideo.view.RecordButton;

public class SpeexRecorder implements Runnable {

	// private Logger log = LoggerFactory.getLogger(SpeexRecorder.class);
	private volatile boolean isRecording;
	private final Object mutex = new Object();
	private static final int frequency = 8000;
	private static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
	public static int packagesize = 160;
	private String fileName = null;
	private RecordButton.ThreadCallBack threadCallBack;

	public SpeexRecorder(String fileName) {
		super();
		this.fileName = fileName;
	}

	public void setThreadCallBack(RecordButton.ThreadCallBack threadCallBack) {
		this.threadCallBack = threadCallBack;
	}

	public void run() {

		// 启动编码线程
		SpeexEncoder encoder = new SpeexEncoder(this.fileName);
		Thread encodeThread = new Thread(encoder);
		encoder.setRecording(true);
		encodeThread.start();

		synchronized (mutex) {
			while (!this.isRecording) {
				try {
					mutex.wait();
				} catch (InterruptedException e) {
					throw new IllegalStateException("Wait() interrupted!", e);
				}
			}
		}
		android.os.Process
				.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

		int bufferRead = 0;
		int bufferSize = AudioRecord.getMinBufferSize(frequency,
				AudioFormat.CHANNEL_IN_MONO, audioEncoding);

		short[] tempBuffer = new short[packagesize];

		AudioRecord recordInstance = new AudioRecord(
				MediaRecorder.AudioSource.MIC, frequency,
				AudioFormat.CHANNEL_IN_MONO, audioEncoding, bufferSize);
		recordInstance.startRecording();
		while (this.isRecording) {
			// log.debug("start to recording.........");
			bufferRead = recordInstance.read(tempBuffer, 0, packagesize);
			// bufferRead = recordInstance.read(tempBuffer, 0, 320);
			if (bufferRead == AudioRecord.ERROR_INVALID_OPERATION) {   //没声音的话
				threadCallBack.failVoice();
				recordInstance.stop();
				// 释放资源
				recordInstance.release();
				recordInstance = null;
				// tell encoder to stop.
				encoder.setRecording(false);
				return;
			} else if (bufferRead == AudioRecord.ERROR_BAD_VALUE) {
				throw new IllegalStateException(
						"read() returned AudioRecord.ERROR_BAD_VALUE");
			} else if (bufferRead == AudioRecord.ERROR_INVALID_OPERATION) {
				throw new IllegalStateException(
						"read() returned AudioRecord.ERROR_INVALID_OPERATION");
			}
			// log.debug("put data into encoder collector....");
			encoder.putData(tempBuffer, bufferRead);

			// 下面计算音量大小
			// 将 buffer 内容取出，进行平方和运算
			long v = 0;
			for (int i = 0; i < tempBuffer.length; i++) {
				v += tempBuffer[i] * tempBuffer[i];
			}
			// 平方和除以数据总长度，得到音量大小。
			double mean = v / (double) bufferRead;
			double volume = 10 * Math.log10(mean);
			threadCallBack.onCurrentVoice(volume);
		}
		recordInstance.stop();
		// 释放资源
		recordInstance.release();
		recordInstance = null;
		// tell encoder to stop.
		encoder.setRecording(false);
		threadCallBack.finishVoice();
	}

	public void setRecording(boolean isRecording) {
		synchronized (mutex) {
			this.isRecording = isRecording;
			if (this.isRecording) {
				mutex.notify();
			}
		}
	}

	public boolean isRecording() {
		synchronized (mutex) {
			return isRecording;
		}
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	};
}
