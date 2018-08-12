package com.example.team.mapapplication.business.retrieve;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.team.mapapplication.R;
import com.example.team.mapapplication.base.BaseActivity;
import com.example.team.mapapplication.base.BaseModel;
import com.example.team.mapapplication.bean.DataDisplayInfo;
import com.example.team.mapapplication.business.main.MainActivity;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogBuilder;

import org.byteam.superadapter.OnItemClickListener;
import org.byteam.superadapter.SuperAdapter;
import org.byteam.superadapter.SuperViewHolder;

import java.util.Calendar;
import java.util.concurrent.CancellationException;

public class RetrieveActivity extends BaseActivity<RetrievePresenter> implements IRetrieveView{

    private RecyclerView mReView;
    private RetrieveViewModel mModel = new RetrieveViewModel();

    private SuperAdapter<DataDisplayInfo> mAdapter;

    @Override
    protected RetrievePresenter createPresenter() {
        return new RetrievePresenter();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_retrieve;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPresenter.attach(this);

        mPresenter.requireData();

        initViews();

        initRv();
    }

    private void initRv() {
        mAdapter = new SuperAdapter<DataDisplayInfo>(this, mModel.getDisplayInfos(), R.layout.retrieve_rv_item_display_infos){

            @Override
            public void onBind(SuperViewHolder holder, int viewType, int layoutPosition, DataDisplayInfo item) {
                TextView name = holder.findViewById(R.id.retrieve_rv_item_tv_name);
                TextView time = holder.findViewById(R.id.retrieve_rv_item_tv_time);

                name.setText(item.getFileName());

                Calendar calendar = item.getCalendar();
                time.setText(calendar.get(Calendar.YEAR) + ": " + calendar.get(Calendar.MONTH) + ": " + calendar.get(Calendar.DAY_OF_MONTH));
            }
        };

        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int viewType, final int position) {
                QMUIDialog.MessageDialogBuilder builder = new QMUIDialog.MessageDialogBuilder(getContext());
                builder.addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                }).addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        Intent toShowIntent = new Intent(getContext(), MainActivity.class);
                        toShowIntent.putExtra("display_only", true);
                        toShowIntent.putExtra("file_name", mModel.getDisplayInfos().get(position).getFileName());
                        startActivity(toShowIntent);
                        dialog.dismiss();
                    }
                });
                builder.setMessage("点击确定展示该组数据");
                builder.create().show();

            }
        });

        mReView.setLayoutManager(new GridLayoutManager(this, 2));

        mReView.setAdapter(mAdapter);


    }

    private void initViews() {
        mReView = findViewById(R.id.retrieve_rv);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public BaseModel getModel() {
        return mModel;
    }
}
