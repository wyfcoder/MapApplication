package com.example.team.mapapplication.business.welcome;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;

import com.example.team.mapapplication.R;
import com.example.team.mapapplication.business.acquireinfo.AcquireBodyInfoFragment;
import com.example.team.mapapplication.business.acquireinfo.AcquireModel;
import com.example.team.mapapplication.business.main.MainActivity;
import com.stephentuso.welcome.BasicPage;
import com.stephentuso.welcome.FragmentWelcomePage;
import com.stephentuso.welcome.WelcomeActivity;
import com.stephentuso.welcome.WelcomeConfiguration;

public class MyWelcomeActivity extends WelcomeActivity {



    @Override
    protected WelcomeConfiguration configuration() {

        return new WelcomeConfiguration.Builder(this)
                .page(new BasicPage(R.drawable.ic_edit_fab,
                        "热图绘制工具！",
                        "简单易用！").background(R.color.sky_blue))
                .page(new FragmentWelcomePage() {
                    @Override
                    protected Fragment fragment() {
                        return new AcquireBodyInfoFragment();
                    }
                }.background(R.color.colorPrimary))
                .swipeToDismiss(true)
                .exitAnimation(android.R.anim.fade_out)
                .build();
    }

    @Override
    protected void completeWelcomeScreen() {
        super.completeWelcomeScreen();

        startActivity(new Intent(this, MainActivity.class));
    }
}
