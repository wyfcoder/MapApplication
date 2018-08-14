package com.example.team.mapapplication.business.acquireinfo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.example.team.mapapplication.R;
import com.example.team.mapapplication.base.BaseModel;

/**
 * Created by Ellly on 2018/8/13.
 */

public class AcquireBodyInfoFragment extends Fragment implements IAcquireView {

    private View mRootView;

    private RelativeLayout mHeightView;
    private RelativeLayout mGenderView;

    private TextView mHeightTv;
    private TextView mGenderTv;

    private AcquirePresenter mPresenter = new AcquirePresenter();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter.attach(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.frag_accuquire, container, false);

        mHeightView = mRootView.findViewById(R.id.frag_acc_rl_height);
        mGenderView = mRootView.findViewById(R.id.frag_acc_rl_gender);

        mHeightView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                        .title("请填写身高")
                        .theme(Theme.LIGHT)
                        .content("/厘米")
                        .inputType(InputType.TYPE_CLASS_NUMBER)
                        .inputRange(0, 3)
                        .input("身高", "", new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                mPresenter.saveHeight(String.valueOf(input));
                                mHeightTv.setText(input);
                                dialog.dismiss();
                            }
                        }).show();
            }
        });

        mGenderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                        .title("请选择性别")
                        .theme(Theme.LIGHT)
                        .items("男", "女")
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                mGenderTv.setText(text);
                                mPresenter.saveGender((String) text);
                                dialog.dismiss();
                                return true;
                            }
                        })
                        .positiveText("确定")
                        .show();
            }
        });

        mHeightTv = mRootView.findViewById(R.id.frag_acc_tv_height);
        mGenderTv = mRootView.findViewById(R.id.frag_acc_tv_gender);

        return mRootView;
    }

    @Override
    public BaseModel getModel() {
        return new AcquireModel();
    }
}
