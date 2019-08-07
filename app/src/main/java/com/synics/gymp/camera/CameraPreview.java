package com.synics.gymp.camera;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.SoundPool;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.synics.gymp.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 摄像机预览类
 */
public class CameraPreview extends SurfaceView
        implements SurfaceHolder.Callback, Camera.AutoFocusCallback, Camera.PreviewCallback, Runnable {

    public static final String TAG = CameraPreview.class.getSimpleName();

    private Camera mCamera;                         // 摄像机

    private Subscription mSubscription;             // 自动聚焦
    private final MultiFormatReader mReader;        // 解码阅读器

    private Callback mCallback;                     // 回调方法

    private long count = 0;                         // 计数
    private boolean isStart = false;                // 是否开始扫描

    private Paint mRectPaint;                       // 矩形框画笔
    private Paint mOvalPaint;                       // 扫描线画笔

    private int mRectSize = 0;                      // 矩形框大小
    private int mRectLeft = 0;                      // 矩形框 left
    private int mRectTop = 0;                       // 矩形框 top

    private SoundPool mSoundPool;                   // 声音池，用于播放声音

    private boolean isTable = false;                // 当前设备是平板还是手机
    private int mRotate = 90;                       // 旋转的角度

    public interface Callback {
        void onResult(String barcode);
    }

    {
        // 阅读器
        mReader = new MultiFormatReader();
        // 缓存 1，系统声音，品质 5
        mSoundPool = new SoundPool(1, AudioManager.STREAM_SYSTEM, 0);
    }

    public CameraPreview(Context context) {
        super(context);

        // 判断当前设备是否为平板
        isTable = isTabletDevice(context);

        getHolder().addCallback(this);

        // 设置是否能够调用 onDraw 方法，如果不设置，将不会调用 onDraw 方法
        setWillNotDraw(false);

        // 初始化阅读器
        initMultiFormatReader();

        // 初始化声音池
        initSoundPool(context);

        // 初始化画笔
        initPaint();
    }

    /**
     * 初始化声音池
     *
     * @param context 上下文
     */
    private void initSoundPool(Context context) {
        // 加载提示音
        mSoundPool.load(context, R.raw.beep, 1);
    }

    /**
     * 初始化解码阅读器
     */
    private void initMultiFormatReader() {
        Map<DecodeHintType, Object> mHints = new HashMap<>();

        Collection<BarcodeFormat> formats = new ArrayList<>();
        formats.addAll(DecodeFormatManager.getAllFormats());

        mHints.put(DecodeHintType.POSSIBLE_FORMATS, formats);
        mReader.setHints(mHints);
    }

    /**
     * 判断是否平板设备
     *
     * @param context 上下文
     * @return true:平板,false:手机
     */
    private boolean isTabletDevice(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /**
     * 初始化摄像头参数
     */
    private void initCameraParameters() {
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO); // 闪光灯
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO); // 自动聚焦
        parameters.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO); // 场景
        parameters.setJpegQuality(100);
        parameters.setJpegThumbnailQuality(100);
        if (!isTable) {
            parameters.setRotation(mRotate);
        }
        mCamera.setParameters(parameters);
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        // 矩形框画笔
        mRectPaint = new Paint();
        mRectPaint.setAntiAlias(true);
        mRectPaint.setColor(0xffffeb3b);
        mRectPaint.setStyle(Paint.Style.STROKE);
        mRectPaint.setStrokeWidth(4f);

        // 扫描线画笔
        mOvalPaint = new Paint();
        mOvalPaint.setAntiAlias(true);
        mOvalPaint.setColor(0xddfff59d);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 获取矩形框长度
        initRectParam(getWidth(), getHeight());
    }

    /**
     * 初始化矩形框属性
     */
    private void initRectParam(int width, int height) {
        mRectSize = (int) ((width < height ? width : height) * 0.7);
        mRectLeft = (width - mRectSize) >> 1;   // 1 / 2
        mRectTop = (height - mRectSize) >> 1;   // 1 / 2
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 画矩形框
        drawRect(canvas);
        // 画扫描线
        drawLine(canvas, count);
    }

    /**
     * 画圆角矩形
     *
     * @param canvas 画布
     */
    private void drawRect(Canvas canvas) {
        // 圆角矩形
        RectF rectF = new RectF(mRectLeft,                          // left
                mRectTop,                           // top
                mRectLeft + mRectSize,              // right
                mRectTop + mRectSize                // bottom
        );

        canvas.drawRoundRect(rectF,                              // 矩形
                mRectSize >> 4,                     // 1 / 16
                mRectSize >> 4,                     // 1 / 16
                mRectPaint                          // 画笔
        );
    }

    /**
     * 画线
     *
     * @param canvas 画板
     */
    private void drawLine(Canvas canvas, long count) {
        int top = mRectTop;
        // 用来计算相对位置
        top += (count % 64) * (mRectSize >> 6);     // 2^6 = 64

        // 矩形
        RectF rectF = new RectF(mRectLeft,                          // left
                top,                                // top
                mRectLeft + mRectSize,              // right
                top + (mRectSize >> 5)              // bottom, 1 / 32
        );

        // 画椭圆
        canvas.drawOval(rectF,                              // 形状
                mOvalPaint                          // 画笔
        );
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            isStart = true;
            new Thread(this).start();

            if (mCamera == null) {
                mCamera = getCameraInstance();
                if (!isTable) {
                    // 手机
                    mCamera.setDisplayOrientation(mRotate);
                }
            }
            initCameraParameters();

            mCamera.setPreviewDisplay(getHolder());
            mCamera.startPreview();
        } catch (IOException e) {
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (getHolder() == null) {
            return;
        }

        try {
            isStart = false;

            mCamera.stopPreview();
        } catch (Exception e) {
        }

        try {
            isStart = true;
            new Thread(this).start();

            mCamera.setPreviewDisplay(getHolder());
            mCamera.startPreview();
        } catch (IOException e) {
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        try {
            isStart = false;
            stopFocus();

            if (mCamera == null) {
                return;
            }
            //holder.removeCallback(this) 这句解决有时返回异常：Camera is being used after Camera.release() was called
            holder.removeCallback(this);
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        } catch (Exception e) {
        }
    }

    /**
     * 开始自动对焦
     */
    public void startFocus() {
        // 自动对焦
        mSubscription = Observable.interval(1, TimeUnit.SECONDS, Schedulers.computation())
                .map(aLong -> mCamera)
                .filter(camera -> camera != null)
                .filter(camera -> mCallback != null)
                .filter(camera -> isStart)
                .subscribe(camera -> camera.autoFocus(this));
    }

    /**
     * 停止自动对焦
     */
    public void stopFocus() {
        if (mSubscription != null && mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
            mSubscription = null;
        }
    }

    @Override
    public void onAutoFocus(boolean success, Camera camera) {
        if (success) {
            mCamera.setOneShotPreviewCallback(this);
        }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        // 解码
        decode(data, camera.getParameters().getPreviewSize());
    }

    /**
     * 解码
     *
     * @param data 数据
     * @param size 大小
     */
    private void decode(final byte[] data, final Camera.Size size) {
        Observable.just(data)
                .subscribeOn(Schedulers.computation())
                .map(bytes -> createBitmap(bytes, size))
                .flatMap(bitmap -> {
                    try {
                        Result result = mReader.decode(bitmap);
                        return Observable.just(result);
                    } catch (NotFoundException e) {
                        return Observable.error(e);
                    }
                })
                .filter(result -> result != null)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Result>() {
                    @Override
                    public void onCompleted() {
                        stopFocus();
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Result result) {
                        if (mCallback != null) {
                            mCallback.onResult(result.getText());
                            mSoundPool.play(1, 1, 1, 0, 0, 1);
                            unsubscribe();
                        } else {
                            throw new NullPointerException("mCallback is null");
                        }
                    }
                });
    }

    /**
     * 创建解码所需要的 BinaryBitmap 对象
     *
     * @param bytes 数据
     * @param size  大小
     * @return BinaryBitmap 对象
     */
    private BinaryBitmap createBitmap(final byte[] bytes, final Camera.Size size) {

        int w, h;

        byte[] dst;
        if (isTable) {
            w = size.width;
            h = size.height;
            dst = Arrays.copyOf(bytes, bytes.length);
        } else {
            w = size.height;
            h = size.width;
            // 旋转
            dst = rotateYUV420Degree90(bytes, size.width, size.height);
        }

        PlanarYUVLuminanceSource source =
                new PlanarYUVLuminanceSource(dst, w, h, 0, 0, w, h, false);

        return new BinaryBitmap(new HybridBinarizer(source));
    }

    /**
     * 旋转 90 度
     *
     * @param data        数据
     * @param imageWidth  宽度
     * @param imageHeight 高度
     * @return 旋转结果
     */
    public static byte[] rotateYUV420Degree90(byte[] data, int imageWidth, int imageHeight) {
        byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];
        // Rotate the Y luma
        int i = 0;
        for (int x = 0; x < imageWidth; x++) {
            for (int y = imageHeight - 1; y >= 0; y--) {
                yuv[i] = data[y * imageWidth + x];
                i++;
            }
        }
        // Rotate the U and V color components
        i = imageWidth * imageHeight * 3 / 2 - 1;
        for (int x = imageWidth - 1; x > 0; x = x - 2) {
            for (int y = 0; y < imageHeight / 2; y++) {
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + x];
                i--;
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + (x - 1)];
                i--;
            }
        }
        return yuv;
    }

    /**
     * 获取回调方法
     *
     * @return 回调方法
     */
    public Callback getCallback() {
        return mCallback;
    }

    /**
     * 设置回调方法
     *
     * @param callback 回调方法
     */
    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    /**
     * 获取 {@link Camera} 对象
     *
     * @return Camera 对象
     */
    private static Camera getCameraInstance() {
        Camera c = null;

        try {
            c = Camera.open();
        } catch (Exception e) {
        }

        return c;
    }

    @Override
    public void run() {
        while (isStart) {
            try {
                count++;
                Thread.sleep(32);
                postInvalidate();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 解码管理器
     */
    private static final class DecodeFormatManager {
        private static final Set<BarcodeFormat> ALL_FORMATS;

        static {
            ALL_FORMATS = EnumSet.of(
                    // 以下是一维码
                    // PRODUCT_FORMATS
                    BarcodeFormat.UPC_A, BarcodeFormat.UPC_E, BarcodeFormat.EAN_13, BarcodeFormat.EAN_8,
                    BarcodeFormat.RSS_14, BarcodeFormat.RSS_EXPANDED,
                    // INDUSTRIAL_FORMATS
                    BarcodeFormat.CODE_39, BarcodeFormat.CODE_93, BarcodeFormat.CODE_128,
                    BarcodeFormat.ITF, BarcodeFormat.CODABAR,
                    // 以下是二维码
                    BarcodeFormat.QR_CODE
                    // 以下是特殊的编码
                    //                    BarcodeFormat.AZTEC,
                    //                    BarcodeFormat.PDF_417
            );
        }

        /**
         * 获取解码类型
         *
         * @return 解码类型
         */
        private static Set<BarcodeFormat> getAllFormats() {
            return ALL_FORMATS;
        }
    }
}
