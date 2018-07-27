package com.example.team.mapapplication.business.main;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Gradient;
import com.baidu.mapapi.map.HeatMap;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Text;
import com.baidu.mapapi.model.LatLng;
import com.blankj.utilcode.util.ToastUtils;
import com.example.team.mapapplication.R;
import com.example.team.mapapplication.base.BaseActivity;
import com.example.team.mapapplication.base.BaseModel;
import com.example.team.mapapplication.base.IBaseViewModel;
import com.example.team.mapapplication.bean.InputValueInfo;
import com.example.team.mapapplication.engine.QMUIEditTextDialogGenerator;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogBuilder;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;

import org.byteam.superadapter.OnItemClickListener;
import org.byteam.superadapter.OnItemLongClickListener;
import org.byteam.superadapter.SuperAdapter;
import org.byteam.superadapter.SuperViewHolder;
import org.byteam.superadapter.animation.SlideInLeftAnimation;


import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class MainActivity extends BaseActivity<MainPresenter> implements IMainView{


    public static final int LOCATE_FINISHED = 1999;

    @Override
    public void animateToLocation() {
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(mModel.getLatLng());
        mBaiduMap.animateMapStatus(mapStatusUpdate);
    }

    @Override
    public void drawLoc(double latitude, double longitude, float radius) {
        MyLocationData data = new MyLocationData.Builder().accuracy(radius).direction(100).latitude(latitude).longitude(longitude).build();
        mBaiduMap.setMyLocationData(data);
        Log.d("MyLocData", data.latitude + " \n" + data.longitude);

    }

    @Override
    public void drawHeatMap(List<LatLng> dataList) {
        //设置渐变颜色值
        int[] DEFAULT_GRADIENT_COLORS = {Color.rgb(102, 225,  0), Color.rgb(255, 0, 0) };
        //设置渐变颜色起始值
        float[] DEFAULT_GRADIENT_START_POINTS = { 0.2f, 1f };
        //构造颜色渐变对象
        Gradient gradient = new Gradient(DEFAULT_GRADIENT_COLORS, DEFAULT_GRADIENT_START_POINTS);

        mHeatMap = new HeatMap.Builder()
                .data(dataList)
                .gradient(gradient)
                .build();
        mBaiduMap.addHeatMap(mHeatMap);
    }

    @Override
    public void removeHeatMap() {
        if (mHeatMap != null){
            mHeatMap.removeHeatMap();
        }
    }

    @Override
    public void refreshInfoList() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void startLocate() {
        mLocationClient.start();
        mLocationClient.requestLocation();
    }

    @Override
    public void drawMarkerOverlay(LatLng latLng, String info) {
        //太丑了，留作备选方案
/*
        TextView button = new Button(getApplicationContext());
        button.setLayoutParams(new DrawerLayout.LayoutParams(DrawerLayout.LayoutParams.WRAP_CONTENT, DrawerLayout.LayoutParams.WRAP_CONTENT));
        button.setText(info);
        InfoWindow infoWindow = new InfoWindow(button, latLng, -5);
        mBaiduMap.showInfoWindow(infoWindow);
*/
    }

    @Override
    public void notifyWaitStart() {
        mModel.setDataWaiting(true);
        ToastUtils.showShort("正在进行...");
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void notifyWaitFinished() {
        mModel.setDataWaiting(false);
        ToastUtils.showShort("完毕");
        mProgressBar.setVisibility(View.INVISIBLE);

    }

    @Override
    public void showDeletePopup(View itemView, final int position) {
        final QMUIPopup deletePopup = new QMUIPopup(this, QMUIPopup.DIRECTION_TOP);
        TextView mTextView = new TextView(getContext());
        mTextView.setLayoutParams(deletePopup.generateLayoutParam(QMUIDisplayHelper.dp2px(getContext(), WRAP_CONTENT),
                WRAP_CONTENT));
        mTextView.setLineSpacing(QMUIDisplayHelper.dp2px(getContext(), 4), 1.0f);
        int padding = QMUIDisplayHelper.dp2px(getContext(), 20);
        mTextView.setText("删除此值");
        mTextView.setPadding(padding, padding, padding, padding);
        mTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.qmui_btn_blue_text));
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mModel.getInputValueInfos().remove(position);
                mPresenter.refreshInfoList();
                deletePopup.dismiss();
            }
        });
        deletePopup.setContentView(mTextView);
        deletePopup.setAnimStyle(QMUIPopup.ANIM_GROW_FROM_CENTER);
        deletePopup.show(itemView);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public BaseModel getModel() {
        return mModel;
    }

    public class MyLocationListener extends BDAbstractLocationListener{

        @Override
        public void onLocDiagnosticMessage(int i, int i1, String s) {
            super.onLocDiagnosticMessage(i, i1, s);
            Log.d("LocDiaMessage", s + " \n i1: " + i1 + " \n i : " + i);
        }

        @Override
        public void onReceiveLocation(BDLocation location) {

            double latitude = location.getLatitude();    //获取纬度信息
            double longitude = location.getLongitude();    //获取经度信息
            float radius = location.getRadius();    //获取定位精度，默认值为0.0f

            //model负责记录数据，可以考虑一并封装至Presenter层
            mModel.setLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
            mModel.setRadius(radius);

            mPresenter.setLocationData();

            mPresenter.animateToLoc();

            mPresenter.sendLocateFinishedMessage();
        }

    }

    private ProgressBar mProgressBar;
    private DrawerLayout mDrawerLayout;
    private Button mRemoveHeatMapBtn;
    private Button mDrawHeatMapBtn;
    private Button mLocBtn;
    private FloatingActionButton mToEditBtn;
    private MapView mMapView;
    private RecyclerView mReView;

    private SuperAdapter<InputValueInfo> mAdapter;
    private BaiduMap mBaiduMap;
    private HeatMap mHeatMap;
    private LocationClient mLocationClient;
    private MyLocationListener mListener = new MyLocationListener();
    private boolean isFirstLoc = true;

    private MainViewModel mModel = new MainViewModel();

    @Override
    protected MainPresenter createPresenter() {
        return new MainPresenter();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        mPresenter.attach(this);

        initialInit();

        initRecyclerView();

        initClickListener();

        initLocationClient();


        mLocationClient.start();
        mLocationClient.requestLocation();
    }

    private void initRecyclerView() {
        mAdapter = new SuperAdapter<InputValueInfo>(this, mModel.getInputValueInfos(), R.layout.main_rv_item_input_value_infos) {

            @Override
            public void onBind(SuperViewHolder holder, int viewType, int layoutPosition, InputValueInfo item) {



                TextView latitudeTv = holder.findViewById(R.id.main_rv_item_ivi_latitude);
                TextView longitudeTv = holder.findViewById(R.id.main_rv_item_ivi_longitude);
                TextView valueTv = holder.findViewById(R.id.main_rv_item_ivi_value);

                latitudeTv.setText(item.getLatLng().latitude + "");
                longitudeTv.setText(item.getLatLng().longitude + "");
                valueTv.setText(item.getValue() + "");
            }
        };

        mAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View itemView, int viewType, int position) {
                mPresenter.deleteThisItem(itemView, viewType, position);
            }
        });

        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int viewType, int position) {
                List<InputValueInfo> infos = mModel.getInputValueInfos();
                final InputValueInfo info = infos.get(position);


                QMUIDialog dialog = new QMUIEditTextDialogGenerator(MainActivity.this, "请输入数据"){
                    @Override
                    protected void onPositiveClick(QMUIDialog dialog, int index, String text) {
                        info.setValue(Double.valueOf(text));
                        dialog.dismiss();

                        mPresenter.refreshInfoList();
                    }
                }.getDialog();

                dialog.show();

            }
        });

        mReView.setItemAnimator(new SlideInLeftAnimator()); // 由于还要设置Notify，所以暂时没有效果

        mReView.setNestedScrollingEnabled(false);

        mReView.setLayoutManager(new LinearLayoutManager(this));

        mReView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        mReView.setAdapter(mAdapter);
    }

    private void initLocationClient() {
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.setLocOption(createLocationOption());
        mLocationClient.registerLocationListener(mListener);
    }

    private void initClickListener() {
        mLocBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.animateToLoc();
            }
        });
        mDrawHeatMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.drawHeatMap();
            }
        });
        mRemoveHeatMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.removeHeatMap();
            }
        });

        mToEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final QMUIDialog.EditTextDialogBuilder editTextDialogBuilder = new QMUIDialog.EditTextDialogBuilder(getContext());

                editTextDialogBuilder.setTitle("输入此处数据").addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                }).addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        if (mModel.getIsDataWaiting()){
                            ToastUtils.showShort("请等待这一组数据获取完毕");
                            return;
                        }
                        String info = editTextDialogBuilder.getEditText().getText().toString();

                        mPresenter.saveInfo(info);
                        dialog.dismiss();
                    }
                });
                QMUIDialog dialog = editTextDialogBuilder.create();

                final EditText editText = editTextDialogBuilder.getEditText();
                editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

                dialog.show();
            }
        });
    }

    private void initialInit() {
        mProgressBar = findViewById(R.id.main_pb_wait);
        mDrawerLayout = findViewById(R.id.main_dl);
        mReView = findViewById(R.id.main_rv_show_data);
        mToEditBtn = findViewById(R.id.main_fab_to_edit);
        mRemoveHeatMapBtn = findViewById(R.id.main_btn_rhm);
        mDrawHeatMapBtn = findViewById(R.id.main_btn_dhm);
        mLocBtn = findViewById(R.id.main_btn_loc);
        mMapView = findViewById(R.id.main_mv);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
    }

    private LocationClientOption createLocationOption() {
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");
        option.setOpenGps(true);
        return option;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.input_topbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_open_drawer:
                mDrawerLayout.openDrawer(Gravity.LEFT);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }
}
