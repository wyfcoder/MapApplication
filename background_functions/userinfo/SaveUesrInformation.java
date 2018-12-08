package com.example.team.mapapplication.business.background_functions.userinfo;

import android.content.Context;
import android.content.SharedPreferences;

public class SaveUesrInformation
{
    private  Context context;

    public   SaveUesrInformation(Context context)
    {
        this.context=context;
    }
    //sex : 0代表男性，1代表女性； height代表身高 单位厘米

    public     void saved(int sex,int height)
    {
        SharedPreferences sharedPreferences=context.getSharedPreferences("userInformation",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putFloat("length",countStepsLength(sex,height));
        editor.apply();
    }
    private   float countStepsLength(int sex,int height)
    {
        if(sex==0)
        {
            return (float) (0.415*0.01*height);
        }
        else if(sex==1)
        {
            return (float)(0.412*0.01*height);
        }
        else
        {
            return 0;
        }
    }
}
