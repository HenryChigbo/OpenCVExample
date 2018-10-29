package inducesmile.com.opencvexample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;

import org.opencv.android.JavaCameraView;

import java.io.FileOutputStream;
import java.util.List;

public class OpenCameraView extends JavaCameraView implements Camera.PictureCallback {

    private static final String TAG = OpenCameraView.class.getSimpleName();

    private String mPictureFileName;

    public static int minWidthQuality = 400;

    private Context context;


    public OpenCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }


    public List<String> getEffectList() {
        return mCamera.getParameters().getSupportedColorEffects();
    }


    public boolean isEffectSupported() {
        return (mCamera.getParameters().getColorEffect() != null);
    }


    public String getEffect() {
        return mCamera.getParameters().getColorEffect();
    }

    public void setEffect(String effect) {
        Camera.Parameters params = mCamera.getParameters();
        params.setColorEffect(effect);
        mCamera.setParameters(params);
    }


    public List<Camera.Size> getResolutionList() {
        return mCamera.getParameters().getSupportedPreviewSizes();
    }


    public void setResolution(Camera.Size resolution) {
        disconnectCamera();
        mMaxHeight = resolution.height;
        mMaxWidth = resolution.width;
        connectCamera(getWidth(), getHeight());
    }


    public Camera.Size getResolution() {
        return mCamera.getParameters().getPreviewSize();
    }


    public void takePicture(final String fileName) {
        Log.i(TAG, "Taking picture");
        this.mPictureFileName = fileName;
        mCamera.setPreviewCallback(null);

        mCamera.takePicture(null, null, this);
    }


    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        Log.i(TAG, "Saving a bitmap to file");
        // The camera preview was automatically stopped. Start it again.
        mCamera.startPreview();
        mCamera.setPreviewCallback(this);

        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        Uri uri = Uri.parse(mPictureFileName);

        Log.d(TAG, "selectedImage: " + uri);
        Bitmap bm = null;
        bm = rotate(bitmap, 90);

        // Write the image in a file (in jpeg format)
        try {
            FileOutputStream fos = new FileOutputStream(mPictureFileName);
            bm.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            fos.close();

        } catch (java.io.IOException e) {
            Log.e("PictureDemo", "Exception in photoCallback", e);
        }
    }


    private static Bitmap rotate(Bitmap bm, int rotation) {
        if (rotation != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            Bitmap bmOut = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
            return bmOut;
        }
        return bm;
    }
}
