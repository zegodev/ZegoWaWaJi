#ifndef ZEGOVideoFormat_h
#define ZEGOVideoFormat_h

namespace AVE
{
    enum VideoPixelFormat
    {
        PIXEL_FORMAT_UNKNOWN = 0,
        PIXEL_FORMAT_I420 = 1,
        PIXEL_FORMAT_NV12 = 2,
        PIXEL_FORMAT_NV21 = 3,
        PIXEL_FORMAT_BGRA32 = 4,
        PIXEL_FORMAT_RGBA32 = 5,
        PIXEL_FORMAT_ARGB32 = 6,
        PIXEL_FORMAT_ABGR32 = 7,
		PIXEL_FORMAT_I422 = 8,
        PIXEL_FORMAT_AVC_AVCC = 9,
        PIXEL_FORMAT_AVC_ANNEXB = 10,
    };
    
    struct VideoDataFormat
    {
        VideoDataFormat()
        {
            width = 0;
            height = 0;
            strides[0] = strides[1] = strides[2] = strides[3] = 0;
            rotation = 0;
            pixel_format = PIXEL_FORMAT_UNKNOWN;
        }
        
        VideoDataFormat(int width, int height, VideoPixelFormat pixel_format)
        {
            this->width = width;
            this->height = height;
            strides[0] = strides[1] = strides[2] = strides[3] = 0;
            rotation = 0;
            this->pixel_format = pixel_format;
        }
        
        bool operator==(const VideoDataFormat& other)
        {
            return (width == other.width && height == other.height &&
                    strides[0] == other.strides[0] && strides[1] == other.strides[1] &&
                    strides[2] == other.strides[2] && strides[3] == other.strides[3] &&
                    rotation == other.rotation && pixel_format == other.pixel_format);
        }
        
        bool operator!=(const VideoDataFormat& other)
        {
            return !(*this == other);
        }
        
        int width;
        int height;
        int strides[4];
        int rotation;
        VideoPixelFormat pixel_format;
    };
}
#endif /* ZEGOVideoFormat_h */
