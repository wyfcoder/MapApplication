package com.example.team.mapapplication.engine;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.team.mapapplication.business.acquireinfo.AcquireModel;
import com.example.team.mapapplication.business.main.MainActivity;
import com.example.team.mapapplication.business.main.MainPresenter;
import com.example.team.mapapplication.business.welcome.MyWelcomeActivity;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainPresenter.askForPermissions();

        if (AcquireModel.isFirstEnterApp()){
            AcquireModel.cancelIsFirstEnterApp();
            startActivity(new Intent(this, MyWelcomeActivity.class));
        }else {
            startActivity(new Intent(this, MainActivity.class));
        }
        finish();
    }
}
