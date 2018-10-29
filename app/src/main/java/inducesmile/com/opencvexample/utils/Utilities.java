package inducesmile.com.opencvexample.utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utilities {


    private Utilities(){

    }


    public static String generateFilename(){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        return "scan" + sdf.format(new Date()) + ".jpg";
    }


    public static void convertBitmapToMat(Bitmap bitmap, Mat mat){
        Utils.bitmapToMat(bitmap, mat);
        //Core.ro
    }

}
