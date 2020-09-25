package edu.nctu.wirelab.sensinggo;

import android.content.Context;
import androidx.viewpager.widget.PagerAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import edu.nctu.wirelab.sensinggo.Connect.Setting;

public class EventViewPagerAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;

    private String [] imagesURL = {Setting.BANNER_URL + "1.jpg",
            Setting.BANNER_URL + "2.jpg",
            Setting.BANNER_URL + "3.jpg"};



    public EventViewPagerAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return imagesURL.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = layoutInflater.inflate(R.layout.imageview_sliding, null);
        ImageView imageView = view.findViewById(R.id.imageViewSliding);

        Glide.with(context)
                .load(imagesURL[position])
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imageView);

        container.addView(view,0);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
