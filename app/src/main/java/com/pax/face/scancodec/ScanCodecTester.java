package com.pax.face.scancodec;

import android.content.Context;

import com.pax.dal.IScanCodec;
import com.pax.dal.entity.DecodeResult;
import com.synics.gymp.bluec.CIFace;

public class ScanCodecTester {

    private static ScanCodecTester codecTester;
    private IScanCodec scanCodec;

    private ScanCodecTester() {

    }

    public static synchronized ScanCodecTester getInstance() {
        if (codecTester == null) {
            codecTester = new ScanCodecTester();
        }
        codecTester.scanCodec = CIFace.getDal().getScanCodec();
        return codecTester;
    }

    public void disableFormat(int format) {
        scanCodec.disableFormat(format);
    }

    public void enableFormat(int format) {
        scanCodec.enableFormat(format);
    }

    public void init(Context context, int width, int height) {
        scanCodec.init(context, width, height);
    }

    public DecodeResult decode(byte[] data) {
        DecodeResult result = scanCodec.decode(data);
        return result;
    }

    public void release() {
        scanCodec.release();
    }
}
