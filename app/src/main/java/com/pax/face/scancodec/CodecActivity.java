package com.pax.face.scancodec;

import java.io.IOException;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.pax.dal.entity.DecodeResult;
import com.synics.gymp.R;
import com.synics.gymp.activity.BaseActivity;

public class CodecActivity extends BaseActivity implements PreviewCallback, OnClickListener, SurfaceHolder.Callback {

    private Camera camera;
    private SurfaceView surfaceView;
    private Button startBt;
    private TextView textView;

    private boolean isOpen = false;
    private boolean isReturn = true;
    private byte[] data = null;
    private SurfaceHolder holder;
    private int WIDTH = 640, HEIGHT = 480;

    protected int getLayoutId(){
        return R.layout.fragment_scancodec;
    }
/*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.fragment_scancodec);
    }
*/
    @Override
    protected void initView() {
        surfaceView = (SurfaceView) findViewById(R.id.fragment_scancodec_surfaceview);
        startBt = (Button) findViewById(R.id.fragment_scancodec_start_bt);
        textView = (TextView) findViewById(R.id.fragment_scancodec_res_text);
        startBt.setOnClickListener(this);

        holder = surfaceView.getHolder();
        holder.addCallback(this);
        surfaceView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (isReturn && data != null) {
            //Log.i("Test", "dataLEN:" + data.length);
            // new DecodeThread().start();
            long startTime = System.currentTimeMillis();
            DecodeResult decodeResult = ScanCodecTester.getInstance().decode(data);
            long timeCost = System.currentTimeMillis() - startTime;
            String res = "timeCost:"
                    + timeCost
                    + " result:"
                    + ((decodeResult == null || decodeResult.getContent() == null) ? "null" : decodeResult.getContent());
            //Log.i("Test", res);

            textView.setText(res);
            camera.addCallbackBuffer(data);
            if(decodeResult != null&&decodeResult.getContent() != null){
                String code = decodeResult.getContent();
                //releaseRes();
                isReturn = false;
                close(RESULT_OK,new String[]{"code"},new String[]{code});
            }
        }
    }

    private void startPreview(){
        initCamera();
        camera.addCallbackBuffer(data);
        camera.setPreviewCallbackWithBuffer(CodecActivity.this);

        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.startPreview();
        isOpen = !isOpen;
    }
    @Override
    public void onClick(View v) {
        if (!isOpen) {
            startPreview();
        } else {
            //releaseRes();
            close();
            isOpen = !isOpen;
        }
    }

    public void initCamera() {
        ScanCodecTester.getInstance().init(this, WIDTH, HEIGHT);

        camera = Camera.open(0);
        // camera.setDisplayOrientation(90);
        Parameters parameters = camera.getParameters();
        Camera.Size size = parameters.getPictureSize();
        parameters.setPreviewSize(WIDTH, HEIGHT);
        parameters.setPictureSize(WIDTH, HEIGHT);

        camera.setDisplayOrientation(90);
        parameters.setZoom(parameters.getZoom());
        camera.setParameters(parameters);

        // For formats besides YV12, the size of the buffer is determined by multiplying the preview image width,
        // height, and bytes per pixel. The width and height can be read from Camera.Parameters.getPreviewSize(). Bytes
        // per pixel can be computed from android.graphics.ImageFormat.getBitsPerPixel(int) / 8, using the image format
        // from Camera.Parameters.getPreviewFormat().
        float bytesPerPixel = ImageFormat.getBitsPerPixel(parameters.getPreviewFormat()) / (float) 8;
        data = new byte[(int) (bytesPerPixel * WIDTH * HEIGHT)];

        Log.i("Test", "previewFormat:" + parameters.getPreviewFormat() + " bytesPerPixel:" + bytesPerPixel
                + " prewidth:" + parameters.getPreviewSize().width + " preheight:" + parameters.getPreviewSize().height);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        startPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i("Test", "format:" + format + "width:" + width + "height:" + height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if(camera!=null) {
            releaseRes();
        }
    }

    private void releaseRes() {
        ScanCodecTester.getInstance().release();
        camera.setPreviewCallbackWithBuffer(null);
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
