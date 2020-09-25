package edu.nctu.wirelab.sensinggo;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class SampleSlide extends Fragment {

    private static final String ARG_LAYOUT_RES_ID = "layoutResId";
    private int layoutResId;

    private TextView titleTextView;
    private ImageView imageview;
    private TextView descriptionTextView;
    private ConstraintLayout constraintLayout;

    private static final String TITLE_STRING = "titleString";
    private static final String IMAGE_ID = "ImageId";
    private static final String DESCRIPTION_STRING = "descriptionString";
    private static final String BG_COLOR = "bg_color";
    private static final String NEW_LAYOUT = "newlayout";
    private String titleString;
    private int imageId;
    private String descriptionString;
    private int bgColor;

    private boolean isNewlayout;

    public static SampleSlide newInstance(int layoutResId) {
        SampleSlide sampleSlide = new SampleSlide();

        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_RES_ID, layoutResId);
        sampleSlide.setArguments(args);

        return sampleSlide;
    }

    public static SampleSlide newInstance(int layoutResId, int imageId) {
        SampleSlide sampleSlide = new SampleSlide();

        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_RES_ID, layoutResId);
        args.putInt(IMAGE_ID, imageId);
        args.putBoolean(NEW_LAYOUT, true);
        sampleSlide.setArguments(args);

        return sampleSlide;
    }

    public static SampleSlide newInstance(int layoutResId, String title, String description, int imageId, int bgColor) {
        SampleSlide sampleSlide = new SampleSlide();

        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_RES_ID, layoutResId);
        args.putString(TITLE_STRING, title);
        args.putString(DESCRIPTION_STRING, description);
        args.putInt(IMAGE_ID, imageId);
        args.putInt(BG_COLOR, bgColor);
        args.putBoolean(NEW_LAYOUT, false);
        sampleSlide.setArguments(args);

        return sampleSlide;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(ARG_LAYOUT_RES_ID)) {
            layoutResId = getArguments().getInt(ARG_LAYOUT_RES_ID);

            if (getArguments().containsKey(NEW_LAYOUT)) {
                isNewlayout = getArguments().getBoolean(NEW_LAYOUT);
            }

            if (getArguments().containsKey(IMAGE_ID)) {
                imageId = getArguments().getInt(IMAGE_ID);
            }

            else if (getArguments().containsKey(TITLE_STRING) && getArguments().containsKey(DESCRIPTION_STRING)
                    && getArguments().containsKey(IMAGE_ID) && getArguments().containsKey(BG_COLOR)) {

                titleString = getArguments().getString(TITLE_STRING);
                descriptionString = getArguments().getString(DESCRIPTION_STRING);
                bgColor = getArguments().getInt(BG_COLOR);
            }
        }


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(layoutResId, container, false);

        if (isNewlayout && imageId != 0) {
            imageview = view.findViewById(R.id.intro_image);
            imageview.setImageResource(imageId);
        }

        else if (titleString != null && imageId != 0 && descriptionString != null) {
            titleTextView = view.findViewById(R.id.intro_title);
            imageview = view.findViewById(R.id.intro_image);
            descriptionTextView = view.findViewById(R.id.intro_description);
            constraintLayout = view.findViewById(R.id.intro_constraintLayout);

            titleTextView.setText(titleString);
            imageview.setImageResource(imageId);
            descriptionTextView.setText(descriptionString);
            constraintLayout.setBackgroundColor(bgColor);
        }

        return view;
    }
}