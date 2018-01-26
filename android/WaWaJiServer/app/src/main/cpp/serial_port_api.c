/*
 * Copyright 2009-2011 Cedric Priscal
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

#include <termios.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <string.h>
#include <jni.h>

#include "serial_port_api.h"

#include "android/log.h"
static const char *TAG="serial_port";
#define LOGI(fmt, args...) __android_log_print(ANDROID_LOG_INFO,  TAG, fmt, ##args)
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, fmt, ##args)
#define LOGW(fmt, args...) __android_log_print(ANDROID_LOG_WARN, TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, TAG, fmt, ##args)

/**
 * 通过波特率获得速率
 * @param baudrate 波特率
 */
static speed_t getBaudrate(jint baudrate)
{
	switch(baudrate) {
	case 0: return B0;
	case 50: return B50;
	case 75: return B75;
	case 110: return B110;
	case 134: return B134;
	case 150: return B150;
	case 200: return B200;
	case 300: return B300;
	case 600: return B600;
	case 1200: return B1200;
	case 1800: return B1800;
	case 2400: return B2400;
	case 4800: return B4800;
	case 9600: return B9600;
	case 19200: return B19200;
	case 38400: return B38400;
	case 57600: return B57600;
	case 115200: return B115200;
	case 230400: return B230400;
	case 460800: return B460800;
	case 500000: return B500000;
	case 576000: return B576000;
	case 921600: return B921600;
	case 1000000: return B1000000;
	case 1152000: return B1152000;
	case 1500000: return B1500000;
	case 2000000: return B2000000;
	case 2500000: return B2500000;
	case 3000000: return B3000000;
	case 3500000: return B3500000;
	case 4000000: return B4000000;
	default: return -1;
	}
}

/**
 * 设置串口数据，校验位,速率，停止位
 * @param fd
 * @param nBits 数据位,取值 7 或 8
 * @param nEvent 校验类型, 取值'N', 'E', 'O', 'S'
 * @param mStop 停止位, 取值 1 或者 2
 */
int set_opt(int fd, jint nBits, jchar nEvent, jint nStop)
{
    LOGE("set_opt:databits=%d, parity=%c, stopbits=%d", nBits, nEvent, nStop);

    struct termios newtio;
    if (tcgetattr(fd, &newtio) != 0) {
        LOGE("setup serial failure");
        return -1;
    }

    bzero(&newtio, sizeof(newtio));

    //c_cflag标志可以定义CLOCAL和CREAD，这将确保该程序不被其他端口控制和信号干扰，同时串口驱动将读取进入的数据。CLOCAL和CREAD通常总是被是能的
    newtio.c_cflag |= CLOCAL | CREAD;

    switch (nBits) { //设置数据位数
    case 7:
        newtio.c_cflag &= ~CSIZE;
        newtio.c_cflag |= CS7;
        break;

    case 8:
        newtio.c_cflag &= ~CSIZE;
        newtio.c_cflag |= CS8;
        break;

    default:
        break;
    }

    switch (nEvent) { //设置校验位
    case 'O':
        newtio.c_cflag |= PARENB; //enable parity checking
        newtio.c_cflag |= PARODD; //奇校验位
        newtio.c_iflag |= (INPCK | ISTRIP);
        break;

    case 'E':
        newtio.c_cflag |= PARENB; //
        newtio.c_cflag &= ~PARODD; //偶校验位
        newtio.c_iflag |= (INPCK | ISTRIP);
        break;

    case 'N':
        newtio.c_cflag &= ~PARENB; //清除校验位
        break;

    default:
        break;
    }

    switch (nStop) { //设置停止位
    case 1:
        newtio.c_cflag &= ~CSTOPB;
        break;

    case 2:
        newtio.c_cflag |= CSTOPB;
        break;

    default:
        LOGW("invalid param nStop:%d", nStop);
        break;
    }

    newtio.c_cc[VTIME] = 0; //设置等待时间
    newtio.c_cc[VMIN] = 0; //设置最小接收字符
    tcflush(fd, TCIFLUSH);

    if (tcsetattr(fd, TCSANOW, & newtio) != 0) {
        LOGE("options set error");
        return -1;
    }

    LOGE("options set success");
    return 1;
}

/**
 * @parma env
 * @param thiz
 * @param path
 * @param baudrate
 * @param flags
 * @param databits
 * @param databits 数据位,取值 7 或 8
 * @param stopbits 停止位, 取值 1 或者 2
 * @param parity 校验类型, 取值'N', 'E', 'O', 'S'
 */
jobject _open(JNIEnv *env, jclass thiz, jstring path, jint baudrate, jint flags,
            jint databits, jint stopbits, jchar parity) {
    int fd;
    speed_t speed;
    jobject mFileDescriptor;

    /* Check arguments */
    {
        speed = getBaudrate(baudrate);
        if (speed == -1) {
            /* TODO: throw an exception */
            LOGE("Invalid baudrate: %d", baudrate);
            return NULL;
        }
    }

    /* Opening device */
    {
        jboolean iscopy;
        const char *path_utf = (*env)->GetStringUTFChars(env, path, &iscopy);
        LOGD("Opening serial port %s with flags 0x%x", path_utf, O_RDWR | flags);
        fd = open(path_utf, O_RDWR | flags);
        LOGD("open() fd = %d", fd);
        (*env)->ReleaseStringUTFChars(env, path, path_utf);
        if (fd == -1) {
            /* Throw an exception */
            LOGE("Cannot open port");
            /* TODO: throw an exception */
            return NULL;
        }
    }

    /* Configure device */
    {
        struct termios cfg;
        LOGD("Configuring serial port");
        if (tcgetattr(fd, &cfg)) {
            LOGE("tcgetattr() failed");
            close(fd);
            /* TODO: throw an exception */
            return NULL;
        }

        cfmakeraw(&cfg);
        cfsetispeed(&cfg, speed);
        cfsetospeed(&cfg, speed);

        if (tcsetattr(fd, TCSANOW, &cfg)) {
            LOGE("tcsetattr() failed");
            close(fd);
            /* TODO: throw an exception */
            return NULL;
        }

        int result = set_opt(fd, databits, parity, stopbits);
        if (result == -1) {
            close(fd);
            /* TODO: throw an exception */
            return NULL;
        }
    }

    /* Create a corresponding file descriptor */
    {
        jclass cFileDescriptor = (*env)->FindClass(env, "java/io/FileDescriptor");
        jmethodID iFileDescriptor = (*env)->GetMethodID(env, cFileDescriptor, "<init>", "()V");
        jfieldID descriptorID = (*env)->GetFieldID(env, cFileDescriptor, "descriptor", "I");
        mFileDescriptor = (*env)->NewObject(env, cFileDescriptor, iFileDescriptor);
        (*env)->SetIntField(env, mFileDescriptor, descriptorID, (jint)fd);
    }

    return mFileDescriptor;
}

/*
 * Class:     com_zego_base_SerialPort
 * Method:    open
 * Signature: (Ljava/lang/String;II)Ljava/io/FileDescriptor;
 */
JNIEXPORT jobject JNICALL Java_com_zego_base_SerialPort_open
  (JNIEnv *env, jclass thiz, jstring path, jint baudrate, jint flags)
{
    return _open(env, thiz, path, baudrate, flags, 8, 1, 'N');
}

/*
 * Class:     com_zego_base_SerialPort
 * Method:    open_ex
 * Signature: (Ljava/lang/String;IIIIC)Ljava/io/FileDescriptor;
 */
JNIEXPORT jobject JNICALL Java_com_zego_base_SerialPort_open_1ex
  (JNIEnv *env, jclass thiz, jstring path, jint baudrate, jint flags,
    jint databits, jint stopbits, jchar parity)
{
    return _open(env, thiz, path, baudrate, flags, databits, stopbits, parity);
}

/*
 * Class:     com_zego_base_SerialPort
 * Method:    close
 * Signature: (Ljava/io/FileDescriptor)V
 */
JNIEXPORT void JNICALL Java_com_zego_base_SerialPort_close
  (JNIEnv *env, jobject thiz, jobject fd)
{
	jclass FileDescriptorClass = (*env)->FindClass(env, "java/io/FileDescriptor");

	jfieldID descriptorID = (*env)->GetFieldID(env, FileDescriptorClass, "descriptor", "I");

	jint descriptor = (*env)->GetIntField(env, fd, descriptorID);

	LOGD("close(fd = %d)", descriptor);
	close(descriptor);
}

