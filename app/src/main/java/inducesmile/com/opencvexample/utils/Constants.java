package inducesmile.com.opencvexample.utils;

import android.os.Environment;

import java.io.File;

public class Constants {


    private Constants(){
        //cannot be instantiated
    }

    public static final String SCAN_IMAGE_LOCATION = Environment.getExternalStorageDirectory() + File.separator + "OpenScanner";
}
