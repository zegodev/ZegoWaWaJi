//
//  ZegoAudioInOutput.h
//  zegoavkit
//
//  Copyright © 2017 Zego. All rights reserved.
//
#ifndef ZEGOAudioInOutput_h
#define ZEGOAudioInOutput_h

namespace AVE
{
	enum FrameType
	{
		TYPE_PCM                = 0x1001,           //PCM
		TYPE_AAC_STREAM         = 0x1003,           //AAC encode data
	};
	
    /*
     TYPE_PCM: Buffer data only contail pcm data.
     
     |-----------------------------------------buffer data(bufLen)-----------------------------------|
     |-----------------------------------------pcm data(pcmLen)--------------------------------------|
     
     configLen = 0.
     pcmLen = bufLen = 2 * samples * channels. (only need correct fill samples and channels, inner will calculate bufLen).
     

     TYPE_AAC_STREAM: Buffer data is made up of specail config and encode data.
     
     |-----------------------------------------buffer data(bufLen)-----------------------------------|
     |----special config(configLen)------|-----------------encode data(encodeLen)--------------------|
     
     encodeLen = bufLen - configLen.
     if configLen == 0: indicate this frame only contain aac encode data.
     if configLen == bufLen: indicate this frame only contain aac special config(encodeLen = bufLen - configLen = 0).
     if (config != 0) && (configLen < bufLen): indicate this frame contain special config and encode data.
     */
    struct AudioFrame
    {
        int frameType;              //refer to enum FrameType
        int samples;                //PCM:capture pcm samples at this input.  AAC:aac encode one frame need samples
        int bytesPerSample;         //bytes per sample = 2 * channels, current bit depth only support 16bit(2 bytes).
        int channels;               //channels, 1 or 2.
        int sampleRate;             //PCM: capture sample rate; AAC: encode sample rate. supported [16k/32k/44.1k/48k]
		double timeStamp;			//time stamp, PCM: 0; AAC: 0 or encode timeStamp, if buffer data only contain special config fill 0.
        int configLen;              //aac special config Len, PCM: 0; AAC: range [0-64]. 0 indicate this frame not contain spcial config.
        int bufLen;                 /*buffer Length, PCM bufLen = 2 * samples * channels(only need correct fill samples and channels, 
									not use filed bufLen). AAC encode data len = bufLen - configLen.*/
        unsigned char* buffer;      //data buffer, the caller is responsible for bufer allocate and release
    };

    class IAudioDataInOutput
    {
    public:
        virtual void startCapture() = 0;
        virtual void stopCapture() = 0;
        virtual void startRender() = 0;
        virtual void stopRender() = 0;
        virtual bool onRecordAudioFrame(const AudioFrame& audioFrame) = 0;
        virtual bool onPlaybackAudioFrame(AudioFrame& audioFrame) = 0;
    };

    
    struct ExtPrepSet
    {
        bool bEncode;           /*
                                 bEncode == false, external prep output PCM data.
                                 bEncode == true, external prep output AAC encode data(only support aac encode)
                                */
		int nSampleRate;        //pcm capture or encode sample rate, if 0 use sdk inner sample rate..
        int nChannel;           //pcm capture or encode channels. if 0 use sdk inner channels.
		int nSamples;           /*
                                 bEncode == false, if nSamples == 0. use sdk inner samples, push 20ms audio data to external prep module once.
								                   else push nSamples(nSamples >= 160 AND nSamples <= 2048) audio data to external prep module once,
												   some audio processing algorithm may need length not 20ms.
														
                                 bEncode == true, AAC encode one frame need samples(480/512/1024/1960/2048).
                                */
		
    };
    
    typedef void(*OnPrepCallback)(const AudioFrame& inFrame, AudioFrame& outFrame);

    /*
     const AudioFrame& inFrame：
     caller fill the input data to handle,refer to struct AudioFrame:
     AudioFrame.frameType = TYPE_PCM;
     AudioFrame.timeStamp = 0;
     AudioFrame.configLen = 0;
     AudioFrame.bufLen = AudioFrame.samples * AudioFrame.channels * 2(bitDepthInByte);
     
     AudioFrame& outFrame:
     outFrame is used for receiving data after called handle.
     caller(zego sdk)will allocate the outFrame.buffer and tell the outFrame.bufLen(allocated buffer length that store output data max length),
	 after the called handle. called will alert outFrame.configLen/outFrame.bufLen and write handled data in outFrame.buffer, needn't fill timeStamp.

     if AACEncodeSet.bEncode = true, data after handling must be TYPE_AAC_STREAM:
	 outFrame.frameType = TYPE_AAC_STREAM;
	 outFrame.timeStamp = 0;

     if AACEncodeSet.bEncode = false, data after handling must be TYPE_PCM:
     outFrame.frameType = TYPE_PCM;
	 AudioFrame.timeStamp = 0;
	 AudioFrame.configLen = 0;
	 AudioFrame.bufLen = AudioFrame.samples * AudioFrame.channels * 2(bitDepthInByte);
	 
	 PS：
     Even without any treatment, you need copy data from inFrame to outFrame,else the outFrame.buffer is empty data(all zeros/000000....0000);
     */
    
}

#endif
