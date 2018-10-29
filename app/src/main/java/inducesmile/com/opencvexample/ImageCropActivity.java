package inducesmile.com.opencvexample;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;


public class ImageCropActivity extends AppCompatActivity {

    private static final String TAG = ImageCropActivity.class.getSimpleName();

    private ImageView cropImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_crop);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        cropImage = (ImageView)findViewById(R.id.cropped_image);

        Bundle bundle = getIntent().getExtras();
        if(bundle == null){
            Toast.makeText(this, "Whoops! image not found", Toast.LENGTH_LONG).show();
        }else{
            String imagePath = bundle.getString("OPEN_IMAGE");
            Bitmap imageBitmap = BitmapFactory.decodeFile(imagePath);
            if(imageBitmap != null){
                cropImage.setImageBitmap(imageBitmap);
            }
        }
    }


}
