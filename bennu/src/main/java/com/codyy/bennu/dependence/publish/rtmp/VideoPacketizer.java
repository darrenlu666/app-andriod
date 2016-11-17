package com.codyy.bennu.dependence.publish.rtmp;

/**
 * H264格式封装
 */
public class VideoPacketizer 
{
	private byte[] sps = null;
	private byte[] pps = null;
	private byte[] body = new byte[1024];
	private boolean firstMakeVideoTag = true;
	private enum NALU_data_type
	{
		NALU_DATA_TYPE_NONE,
		NALU_DATA_TYPE_SEPARATOR_WITH_DATA,
		NALU_DATA_TYPE_DATASIZ_WITH_DATA,
		NALU_DATA_TYPE_JUST_HAS_DATA,
	}
	private NALU_data_type type = NALU_data_type.NALU_DATA_TYPE_NONE;

	// H264相关 ---后续抽象成单独类
	public final static byte NAL_SLICE      = 0x01;
	public final static byte NAL_SLICE_IDR  = 0x05;
	public final static byte NAL_SPS        = 0x07;
	public final static byte NAL_PPS        = 0x08;
	public final static byte[] NAULSTARTCODE = {0x00, 0x00, 0x00, 0x01};
	public final static int NAULSTARTCODELENGTH = 4;
	
	public static boolean hasStartCode(byte[] buf, int len, int offset)
	{
		if (buf == null || len < NAULSTARTCODELENGTH)
		{
			return false;
		}
		if (offset < 0 || offset > len - 4)
		{
			return false;
		}
		
		if (buf[offset + 0] == NAULSTARTCODE[0] && buf[offset + 1] == NAULSTARTCODE[1] 
		 && buf[offset + 2] == NAULSTARTCODE[2] && buf[offset + 3] == NAULSTARTCODE[3])
		{
			return true;
		}
		return false;
	}
	
	public static int parseNaulType(byte[] buf, int len)
	{
		int offset = 0;
		if (hasStartCode(buf, len, 0))
		{
			offset = NAULSTARTCODELENGTH;
		}
		return buf[offset] & 0x1F;
	}
	
	public VideoPacketizer()
	{
	}
	
	public boolean isKeyFrame(byte[] buf, int len)
	{
		int type = parseNaulType(buf, len);
		if (type == NAL_SLICE_IDR)
		{
			return true;
		}
		return false;
	}
	
	// 0x00 00 00 01 SPS 00 00 00 01 PPS
	public boolean isVideoConf(byte[] buf, int len)
	{
		int type = parseNaulType(buf, len);
		if (type == NAL_SPS || type == NAL_PPS)
		{
			return true;
		}
		return false;
	}
	
	public void parseVideoConf(byte[] buf, int len)
	{
		int type = 0;
		int offset = 0;
		if (hasStartCode(buf, len, 0))
		{
			offset = NAULSTARTCODELENGTH;
		}
		type = buf[offset] & 0x1F;
		if (type == VideoPacketizer.NAL_SPS || type == VideoPacketizer.NAL_PPS)
		{
			//解析SPS、PPS
			int i = offset;
			for (; i < len - NAULSTARTCODELENGTH; i++)
			{
				if (VideoPacketizer.hasStartCode(buf, len, i))
				{
					break;
				}
			}
			int sps_len = i - NAULSTARTCODELENGTH;
			int pps_len = len - i - NAULSTARTCODELENGTH;
			if (sps_len > 0 && pps_len > 0)
			{
				sps = new byte[sps_len];
				pps = new byte[pps_len];
				System.arraycopy(buf, offset, sps, 0, sps_len);
				System.arraycopy(buf, i + NAULSTARTCODELENGTH, pps, 0, pps_len);
			}
		}
	}
	
	
	public byte[] makeVideoConf(int timeStamp) 
	{
		if (sps == null || pps == null) return null;
		int nBodySize = 0;//
		// Tag Header
		body[0] = 0x09; // TagΪ��Ƶ
		body[1] = 0;    // Tag���ȣ������������
		body[2] = 0;
		body[3] = 0;
		body[4] = (byte) ((timeStamp >> 16) & 0xff);// Tag��ʱ���
		body[5] = (byte) ((timeStamp >> 8)  & 0xff);
		body[6] = (byte) ((timeStamp >> 0)  & 0xff);
		body[7] = (byte) ((timeStamp >> 24) & 0xff);
		body[8] = 0;// StreamId
		body[9] = 0;
		body[10] = 0;
		// Video Header
		int i = 11; //
		body[i++] = 0x17; // 1:keyframe 7:AVC
		body[i++] = 0x00; // AVC sequence header---�ؼ�
		body[i++] = 0x00; // Composition Time
		body[i++] = 0x00;
		body[i++] = 0x00;

		// Video Data(AVCDecoderConfigurationRecord)
		body[i++] = 0x01; // configurationVersion
		body[i++] = sps[1]; // AVCProfileIndication
		body[i++] = sps[2]; // profile_compatibility
		body[i++] = sps[3]; // AVCLevelIndication
		// reserved , lengthSizeMinusOne��ռ2bit��һ��Ϊ3��
		body[i++] = (byte) 0xff;

		// reserved , sps nums��ռ5bit��һ��Ϊ1��
		body[i++] = (byte) 0xe1;
		// sps data length --- 2���ֽ�
		int sps_len = sps.length;
		body[i++] = (byte) (sps_len >> 8);
		body[i++] = (byte) (sps_len & 0xff);
		// sps data
		System.arraycopy(sps, 0, body, i, sps_len);
		i = i + sps_len;

		// pps nums��ռ1byte��һ��Ϊ1��
		body[i++] = 0x01; // sps nums
		// pps data length
		int pps_len = pps.length;
		body[i++] = (byte) (pps_len >> 8);
		body[i++] = (byte) (pps_len & 0xff);
		// pps data
		System.arraycopy(pps, 0, body, i, pps_len);
		i = i + pps_len;
		// �������Tag����
		nBodySize = i - 11;
		body[1] = (byte) ((nBodySize >> 16) & 0xff);// Tag���ȣ������AAC,VideoTagHeader��Video
		// Data
		body[2] = (byte) ((nBodySize >> 8) & 0xff);
		body[3] = (byte) ((nBodySize >> 0) & 0xff);

		// ������һ��Tag����
		int nPreTagSize = i;
		body[nPreTagSize + 0] = (byte) ((nPreTagSize >> 24) & 0xff);
		body[nPreTagSize + 1] = (byte) ((nPreTagSize >> 16) & 0xff);
		body[nPreTagSize + 2] = (byte) ((nPreTagSize >> 8) & 0xff);
		body[nPreTagSize + 3] = (byte) ((nPreTagSize >> 0) & 0xff);
		
		byte[] tag = new byte[nPreTagSize + 4];
		System.arraycopy(body, 0, tag, 0, nPreTagSize + 4);
		return tag;
	}


	public byte[] makeVideoTag(byte[] buf, int len, int timeStamp, boolean bIsKeyFrame)
	{
		// TagHeader(11) + VideoHeader(5) + NALU + PreTagSize
		// handle three situation:
		// 00 00 00 01(NAlU_startcode) + NALU_data
		// xx xx xx xx(NALU_size) + NALU_data
		// just has NALU_data

		if (firstMakeVideoTag) {
			if(buf[0] == 0 && buf[1] == 0 && buf[2] == 0 && buf[3] == 1)
				type = NALU_data_type.NALU_DATA_TYPE_SEPARATOR_WITH_DATA;
			else{
				long length = (buf[0] << 24) + (buf[1] << 16) + (buf[2] <<8) + buf[3];
				if (length == len - 4) {
					type = NALU_data_type.NALU_DATA_TYPE_DATASIZ_WITH_DATA;
				} else {
					type = NALU_data_type.NALU_DATA_TYPE_JUST_HAS_DATA;
				}
			}
			firstMakeVideoTag = false;
		}

		int nBodySize = 0;
		int NALUDataSize = 0;

		if (type == NALU_data_type.NALU_DATA_TYPE_SEPARATOR_WITH_DATA) {
			nBodySize = len + 5;
		} else {
			nBodySize = len + 5 + 4;
		}

		int nAllocSize = 11 + nBodySize + 4;
		byte[] body = new byte[nAllocSize];

		// Tag Header
		body[0] = 0x09; // ��ʾTagΪ��Ƶ
		body[1] = (byte) ((nBodySize >> 16) & 0xff);// Tag�ĳ���
		body[2] = (byte) ((nBodySize >> 8)  & 0xff);
		body[3] = (byte) ((nBodySize >> 0)  & 0xff);

		body[4] = (byte) ((timeStamp >> 16) & 0xff);// Tag��ʱ���
		body[5] = (byte) ((timeStamp >> 8)  & 0xff);
		body[6] = (byte) ((timeStamp >> 0)  & 0xff);
		body[7] = (byte) ((timeStamp >> 24) & 0xff);

		body[8] = 0;// StreamId
		body[9] = 0;
		body[10] = 0;
		// Video Header
		if (bIsKeyFrame) {
			body[11 + 0] = 0x17;// 1:Iframe 7:AVC
		} else {
			body[11 + 0] = 0x27;// 2:Pframe 7:AVC
		}

		body[11 + 1] = 0x01;// AVC NALU
		body[11 + 2] = 0x00;// Composition Time
		body[11 + 3] = 0x00;
		body[11 + 4] = 0x00;

		// Video Data
		// NALU size---AVCDecoderConfigurationRecord��lengthSizeMinusOne+1
		// NALU = NALU data size + NALU data
		if (type == NALU_data_type.NALU_DATA_TYPE_JUST_HAS_DATA){
			NALUDataSize = len;
			body[11 + 5] = (byte) (NALUDataSize  >> 24);
			body[11 + 6] = (byte) ((NALUDataSize >> 16) & 0xff);
			body[11 + 7] = (byte) ((NALUDataSize >> 8) & 0xff);
			body[11 + 8] = (byte) (NALUDataSize & 0xff);
			// NALU data
			System.arraycopy(buf, 0, body, 11 + 5 + 4, len);

		}else {
			if (type == NALU_data_type.NALU_DATA_TYPE_SEPARATOR_WITH_DATA) {
				NALUDataSize = len - 4;
				buf[0] = (byte) (NALUDataSize >> 24);
				buf[1] = (byte) ((NALUDataSize >> 16) & 0xff);
				buf[2] = (byte) ((NALUDataSize >> 8) & 0xff);
				buf[3] = (byte) (NALUDataSize & 0xff);
			}
			System.arraycopy(buf, 0, body, 11 + 5, len);
		}

		// Pre Tag Size
		int nPreTagSize = 11 + nBodySize;
		body[nPreTagSize + 0] = (byte) ((nPreTagSize >> 24) & 0xff);
		body[nPreTagSize + 1] = (byte) ((nPreTagSize >> 16) & 0xff);
		body[nPreTagSize + 2] = (byte) ((nPreTagSize >> 8) & 0xff);
		body[nPreTagSize + 3] = (byte) ((nPreTagSize >> 0) & 0xff);
		//flvWrite(body, nAllocSize);
		//networkWrite(body, nAllocSize);
		return body;
	}

}
