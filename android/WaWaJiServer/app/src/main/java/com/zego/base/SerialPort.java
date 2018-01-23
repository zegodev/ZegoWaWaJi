/*
 * Copyright 2009 Cedric Priscal
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

/**
 * 特别注意：
 *
 * 如果使用 Demo 中提供的 libserial_port_api.so，请不要修改此文件的包名，否则会导致串口通信异常。
 * 如果你想使用自定义的包名甚至类名，请自行修改并编译 src/main/cpp 中的代码。
 */

package com.zego.base;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.util.Log;

public class SerialPort {

	private static final String TAG = "SerialPort";

	/*
	 * Do not remove or rename the field mFd: it is used by native method close();
	 */
	private FileDescriptor mFd;
	protected FileInputStream mFileInputStream;
	protected FileOutputStream mFileOutputStream;

	public SerialPort(File device, int baudrate, int flags) throws SecurityException, IOException {

		/* Check access permission */
		if (!device.canRead() || !device.canWrite()) {
			try {
				/* Missing read/write permission, trying to chmod the file */
				Process su;
				su = Runtime.getRuntime().exec("/system/bin/su");
				String cmd = "chmod 666 " + device.getAbsolutePath() + "\n"
						+ "exit\n";
				su.getOutputStream().write(cmd.getBytes());
				if ((su.waitFor() != 0) || !device.canRead()
						|| !device.canWrite()) {
					throw new SecurityException();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new SecurityException("can't open serial port on " + device.getAbsolutePath());
			}
		}

		mFd = open(device.getAbsolutePath(), baudrate, flags);
		if (mFd == null) {
			Log.e(TAG, "native open returns null");
			throw new IOException("native open returns null");
		}
		mFileInputStream = new FileInputStream(mFd);
		mFileOutputStream = new FileOutputStream(mFd);
	}

	protected void close() {
		close(mFd);
	}

	// JNI
	private native static FileDescriptor open(String path, int baudrate, int flags);

	/**
	 *
	 * @param path 串口设备路径
	 * @param baudrate 波特率
	 * @param flags 端口参数
	 * @param databits 数据位，取值 7 或 8
	 * @param stopbits 停止位 取值 1 或 2
	 * @param parity 校验位，取值 'N', 'O', 'E', 'S'，其中 N 无校验位；O 奇校验位
	 * @return
	 */
	private native static FileDescriptor open_ex(String path, int baudrate, int flags, int databits, int stopbits, char parity);
	private native void close(FileDescriptor fd);

	static {
		System.loadLibrary("serial_port_api");
	}
}
