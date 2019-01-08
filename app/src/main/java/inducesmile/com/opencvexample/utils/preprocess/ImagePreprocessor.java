package inducesmile.com.opencvexample.utils.preprocess;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class ImagePreprocessor {

    private static final String TAG = ImagePreprocessor.class.getSimpleName();


    public void changeImagePreviewOrientation(Mat src, Mat des, Mat forward){
        Core.transpose(src, des);
        Imgproc.resize(des, forward, forward.size(), 0, 0, 0);
        Core.flip(forward, src, 1 );
    }


}
