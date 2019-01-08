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

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import inducesmile.com.opencvexample.R;
import inducesmile.com.opencvexample.utils.ImageUtils;

public class HistogramActivity extends AppCompatActivity {

    private static final String TAG = HistogramActivity.class.getSimpleName();

    private ImageView srcImage, showGraph;

    private TextView srcText;

    private static final int REQUEST_GET_SINGLE_FILE = 202;

    private Bitmap bitmap;

    private boolean isImageHistogram = false;

    private BaseLoaderCallback baseLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };


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
        showGraph = (ImageView)findViewById(R.id.show_graph);

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

                        MatOfFloat range = new MatOfFloat(0f, 255f);
                        MatOfFloat histRange = new MatOfFloat(range);

                        Core.split(sourceMat, channels);

                        MatOfInt[] allChannel = new MatOfInt[]{new MatOfInt(0), new MatOfInt(1), new MatOfInt(2)};
                        Scalar[] colorScalar = new Scalar[]{new Scalar(220, 0, 0, 255), new Scalar(0, 220, 0, 255), new Scalar(0, 0, 220, 255)};

                        Mat matB = new Mat(sourceSize, sourceMat.type());
                        Mat matG = new Mat(sourceSize, sourceMat.type());
                        Mat matR = new Mat(sourceSize, sourceMat.type());

                        Imgproc.calcHist(channels, allChannel[0], new Mat(), matB, hisSize, histRange);
                        Imgproc.calcHist(channels, allChannel[1], new Mat(), matG, hisSize, histRange);
                        Imgproc.calcHist(channels, allChannel[2], new Mat(), matR, hisSize, histRange);


                        int graphHeight = 1000;
                        int graphWidth = 800;
                        int binWidth = 3;

                        Mat graphMat = new Mat(graphHeight, graphWidth, CvType.CV_8UC3, new Scalar(0, 0, 0));

                        Core.normalize(matB, matB, graphMat.height(), 0, Core.NORM_INF);
                        Core.normalize(matG, matG, graphMat.height(), 0, Core.NORM_INF);
                        Core.normalize(matR, matR, graphMat.height(), 0, Core.NORM_INF);


                        for(int i = 0; i < histogramSize; i++){
                            Point bPoint1 = new Point(binWidth * (i - 1), graphHeight - Math.round(matB.get(i - 1, 0)[0]));
                            Point bPoint2 = new Point(binWidth * i, graphHeight - Math.round(matB.get(i, 0)[0]));
                            Core.line(graphMat, bPoint1, bPoint2, new Scalar(220, 0, 0, 255), 3, 8, 0);

                            Point gPoint1 = new Point(binWidth * (i - 1), graphHeight - Math.round(matG.get(i - 1, 0)[0]));
                            Point gPoint2 = new Point(binWidth * i, graphHeight - Math.round(matG.get(i, 0)[0]));
                            Core.line(graphMat, gPoint1, gPoint2, new Scalar(0, 220, 0, 255), 3, 8, 0);

                            Point rPoint1 = new Point(binWidth * (i - 1), graphHeight - Math.round(matR.get(i - 1, 0)[0]));
                            Point rPoint2 = new Point(binWidth * i, graphHeight - Math.round(matR.get(i, 0)[0]));
                            Core.line(graphMat, rPoint1, rPoint2, new Scalar(0, 0, 220, 255), 3, 8, 0);
                        }

                        //Display graph
                        Bitmap graphBitmap = Bitmap.createBitmap(graphMat.cols(), graphMat.rows(), Bitmap.Config.ARGB_8888);
                        Utils.matToBitmap(graphMat, graphBitmap);

                        showGraph.setImageBitmap(graphBitmap);
                        //set the isImageHistogram
                        //isImageHistogram = true;
                    }
                }
            }
        });
    }


    @Override
    public void onResume(){
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_11, this, baseLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            baseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
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
