package edu.nctu.wirelab.sensinggo;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;


public class IntroActivity extends AppIntro {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Note here that we DO NOT use setContentView();

        // Add your slide fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
//        addSlide(firstFragment);
//        addSlide(secondFragment);
//        addSlide(thirdFragment);
//        addSlide(fourthFragment);



        // Instead of fragments, you can also use our default slide.
        // Just create a `SliderPage` and provide title, description, background and image.
        // AppIntro will do the rest.

//        SliderPage sliderPage = new SliderPage();
//
//        SliderPage sliderPage = new SliderPage();
//        sliderPage.setTitle(getString(R.string.app_intro_start_title));
//        sliderPage.setDescription(getString(R.string.app_intro_start_description));
//        sliderPage.setImageDrawable(R.drawable.intro_sg);
//        sliderPage.setBgColor(Color.parseColor("#27c591"));
//        addSlide(AppIntroFragment.newInstance(sliderPage));
//
//        sliderPage.setTitle("地圖頁面");
//        sliderPage.setDescription("四處移動，金幣在紅圈內即可收集");
//        sliderPage.setImageDrawable(R.drawable.intro_map);
//        sliderPage.setBgColor(Color.parseColor("#147fd0"));
//        addSlide(AppIntroFragment.newInstance(sliderPage));
//
        //old version 2020/5/4
//        addSlide(SampleSlide.newInstance(R.layout.app_intro_start, getString(R.string.app_intro_start_title), getString(R.string.app_intro_start_description)
//                , R.drawable.intro_sg, Color.parseColor("#54d0a7")));
//        addSlide(SampleSlide.newInstance(R.layout.app_intro, getString(R.string.app_intro_login_title), getString(R.string.app_intro_login_description)
//                , R.drawable.intro_account, Color.parseColor("#54d0a7")));
//        addSlide(SampleSlide.newInstance(R.layout.app_intro, getString(R.string.app_intro_map_title), getString(R.string.app_intro_map_description)
//                , R.drawable.intro_map, Color.parseColor("#54d0a7")));
//        addSlide(SampleSlide.newInstance(R.layout.app_intro, getString(R.string.app_intro_social_title), getString(R.string.app_intro_social_description)
//                , R.drawable.intro_social, Color.parseColor("#54d0a7")));
//        addSlide(SampleSlide.newInstance(R.layout.app_intro, getString(R.string.app_intro_upload_title), getString(R.string.app_intro_upload_description)
//                , R.drawable.intro_others, Color.parseColor("#54d0a7")));

        addSlide(SampleSlide.newInstance(R.layout.app_intro_new, R.drawable.intro_tutorial));
        addSlide(SampleSlide.newInstance(R.layout.app_intro_new, R.drawable.intro_create_account));
        addSlide(SampleSlide.newInstance(R.layout.app_intro_new, R.drawable.intro_collect_coin));
        addSlide(SampleSlide.newInstance(R.layout.app_intro_new, R.drawable.intro_edit_profile));
        addSlide(SampleSlide.newInstance(R.layout.app_intro_new, R.drawable.intro_upload_data));

        // OPTIONAL METHODS
        // Override bar/separator color.
        setBarColor(getResources().getColor(R.color.others_button_background_blue));

        // Hide Skip/Done button.
        //showSkipButton(false);
        //setProgressButtonEnabled(true);

        setFadeAnimation();
        //setZoomAnimation();
        //setFlowAnimation();
        //setSlideOverAnimation();

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permission in Manifest.
        //setVibrate(true);
        //setVibrateIntensity(30);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}
