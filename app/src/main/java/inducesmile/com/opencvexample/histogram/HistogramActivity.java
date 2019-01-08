package inducesmile.com.opencvexample.histogram;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Size;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import inducesmile.com.opencvexample.R;
import inducesmile.com.opencvexample.utils.ImageUtils;

public class HistogramActivity extends AppCompatActivity {

    private static final String TAG = HistogramActivity.class.getSimpleName();

    private ImageView srcImage, desImage;

    private TextView srcText;

    private static final int REQUEST_GET_SINGLE_FILE = 202;

    private Bitmap bitmap;

    private boolean isImageHistogram = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histogram);


        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            setTitle("Image Histogram");
        }

        srcImage = (ImageView)findViewById(R.id.source_image);

        srcText = (TextView)findViewById(R.id.source_text);


        Button openGalleryBtn = (Button)findViewById(R.id.open_gallery);
        openGalleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),REQUEST_GET_SINGLE_FILE);

            }
        });


        Button showHistogramButton = (Button)findViewById(R.id.show_histogram);
        showHistogramButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bitmap == null){
                    Toast.makeText(HistogramActivity.this, "You must upload image from gallery to show its histogram", Toast.LENGTH_SHORT).show();
                }else{
                    if(!isImageHistogram){
                        //add histogram code
                        Mat sourceMat = new Mat();
                        Utils.bitmapToMat(bitmap, sourceMat);

                        Size sourceSize = sourceMat.size();

                        int histogramSize = 256;
                        MatOfInt hisSize = new MatOfInt(histogramSize);

                        Mat destinationMat = new Mat();
                        List<Mat> channels = new ArrayList<>();

                        float []range = {0, 255};
                        MatOfFloat histoRange = new MatOfFloat(range);


                        Core.split(sourceMat, channels);


                        //set the isImageHistogram
                        isImageHistogram = true;
                    }
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK) {
                if (requestCode == REQUEST_GET_SINGLE_FILE) {
                    Uri selectedImageUri = Objects.requireNonNull(data).getData();
                    // Get the path from the Uri
                    final String path = getPathFromURI(selectedImageUri);
                    if (path != null) {
                        File f = new File(path);
                        selectedImageUri = Uri.fromFile(f);
                    }
                    // Set the image in ImageView
                    bitmap = ImageUtils.resizeBitmap(Objects.requireNonNull(selectedImageUri).getEncodedPath(), 300, 200);
                    if(bitmap != null){
                        srcImage.setImageBitmap(bitmap);
                        srcText.setVisibility(View.VISIBLE);
                    }
                    Log.d(TAG, "Lod image path " + selectedImageUri + " - " + selectedImageUri.getEncodedPath());
                }
            }
        } catch (Exception e) {
            Log.e("FileSelectorActivity", "File select error", e);
        }
    }


    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }
}
