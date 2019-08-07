package com.pax.face.scancodec;

import android.content.Context;
import android.hardware.Camera;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

import com.pax.dal.entity.DecodeResult;
import com.synics.gymp.iface.IConditionSelect;

import java.io.FileOutputStream;

/**
 * Created by Administrator on 2019/7/7.
 */

public class CameraView extends SurfaceView implements SurfaceHolder.Callback, Camera.PictureCallback {
    private SurfaceHolder holder;
    private Camera camera;
    private boolean af;
    private byte[] data = null;
    Context mContext;
    IConditionSelect mCallback;

    public CameraView(Context context,IConditionSelect callback) {//构造函数
        super(context);
        mContext = context;
        mCallback = callback;
        holder= getHolder();//生成Surface Holder
        holder.addCallback(this);

        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//指定Push Buffer
    }

    public void surfaceCreated(SurfaceHolder holder) {//Surface生成事件的处理
        try {
            camera= Camera.open();//摄像头的初始化
            camera.addCallbackBuffer(data);
            camera.setPreviewCallback(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    if (data != null) {

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
                        camera.addCallbackBuffer(data);
                        if(decodeResult != null&&decodeResult.getContent() != null){
                            mCallback.conditionSelect(0,true,null,0,decodeResult.getContent());
                            camera.stopPreview();
                        }
                    }
                }
            });
            camera.addCallbackBuffer(data);
            camera.setPreviewDisplay(holder);
        }catch (Exception e) {
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder,int format,int width,int height) {//Surface改变事件的处理
        Camera.Parameters parameters= camera.getParameters();
        Camera.Size size = parameters.getPictureSize();
        //parameters.setPreviewSize(width, height);
        //double w = Math.floor(width/size.width*1.0);
        //parameters.setPreviewSize(size.width, size.height);
        parameters.setZoom(parameters.getZoom());
        camera.setParameters(parameters);//设置参数
        camera.setDisplayOrientation(90);
        camera.startPreview();//开始预览
    }

    public void surfaceDestroyed(SurfaceHolder holder) {//Surface销毁时的处理
        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
        camera= null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {//屏幕触摸事件
        if (event.getAction()== MotionEvent.ACTION_DOWN) {//按下时自动对焦
            camera.autoFocus(null);
            af= true;
        }
        if (event.getAction()== MotionEvent.ACTION_UP&&af== true) {//放开后拍照
            camera.takePicture(null,null,this);
            af= false;
        }
        return true;
    }

    public void onPictureTaken(byte[] data, Camera camera) {//拍摄完成后保存照片
        try {
            String path= Environment.getExternalStorageDirectory()+ "/test.jpg";
            data2file(data, path);
        }catch (Exception e) {
        }
        camera.startPreview();
    }

    private void data2file(byte[] w, String fileName) throws Exception {//将二进制数据转换为文件的函数
        FileOutputStream out= null;
        try {
            out= new FileOutputStream(fileName);
            out.write(w);
            out.close();
        }catch (Exception e) {
            if (out!= null)
                out.close();
            throw e;
        }
    }

}
