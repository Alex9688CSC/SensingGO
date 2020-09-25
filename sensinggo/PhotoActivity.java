package edu.nctu.wirelab.sensinggo;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;


public class PhotoActivity extends AppCompatActivity {

    ImageView imageViewFullScreen;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        imageViewFullScreen = findViewById(R.id.imageViewFullScreen);
        url = getIntent().getExtras().getString("url");

        ColorDrawable cd = new ColorDrawable(0x000000);

        Glide.with(this)
                .load(url)
                .placeholder(cd)
                .error(R.drawable.icon_manb) //load失敗的Drawable
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imageViewFullScreen);
    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

}
