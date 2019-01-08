package inducesmile.com.opencvexample;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import inducesmile.com.opencvexample.histogram.HistogramActivity;
import inducesmile.com.opencvexample.utils.Constants;
import inducesmile.com.opencvexample.utils.ImagePicker;
import inducesmile.com.opencvexample.utils.Utilities;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int PICK_IMAGE_ID = 303;

    private String fileLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if(!report.areAllPermissionsGranted()){
                            Toast.makeText(MainActivity.this, "You need to grant all permission to use this app features", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                })
                .check();

        Button startButton = (Button)findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cvIntent = new Intent(MainActivity.this, OpenCVCamera.class);
                startActivity(cvIntent);
            }
        });


        Button scannerButton = (Button)findViewById(R.id.document_button);
        scannerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent openItent = ImagePicker.getPickImageIntent(MainActivity.this);
                //startActivityForResult(openItent, PICK_IMAGE_ID);
                Intent histogramIntent = new Intent(MainActivity.this, HistogramActivity.class);
                startActivity(histogramIntent);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //return selected image as bitmap
        switch(requestCode) {

            case PICK_IMAGE_ID:
                Bitmap bitmap = ImagePicker.getImageFromResult(this, resultCode, data);
                fileLocation = Constants.SCAN_IMAGE_LOCATION + File.separator + Utilities.generateFilename();
                try {

                    convertBitmapToImage(bitmap, fileLocation);
                    Toast.makeText(MainActivity.this, "File successfully stored ", Toast.LENGTH_LONG).show();
                    Intent openImage = new Intent(MainActivity.this, ImageCropActivity.class);
                    openImage.putExtra("OPEN_IMAGE", fileLocation);
                    startActivity(openImage);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }


    private void convertBitmapToImage(Bitmap bitmap, String filePath) throws IOException {
        if(!new File(Constants.SCAN_IMAGE_LOCATION).exists()){
            new File(Constants.SCAN_IMAGE_LOCATION).mkdir();
        }
        File outFile = new File(filePath);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(outFile));

        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bufferedOutputStream);
        bufferedOutputStream.close();
    }
}
