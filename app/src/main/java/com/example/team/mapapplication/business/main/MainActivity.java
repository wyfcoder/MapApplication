package com.example.team.mapapplication.business.main;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.Gradient;
import com.baidu.mapapi.map.HeatMap;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.blankj.utilcode.util.ToastUtils;
import com.example.team.mapapplication.R;
import com.example.team.mapapplication.base.BaseActivity;
import com.example.team.mapapplication.base.BaseModel;
import com.example.team.mapapplication.bean.InputValueInfo;
import com.example.team.mapapplication.business.background_functions.location.IntereactionForLocation;
import com.example.team.mapapplication.business.background_functions.location.LocationService;
import com.example.team.mapapplication.business.retrieve.RetrieveActivity;
import com.example.team.mapapplication.engine.QMUIEditTextDialogGenerator;
import com.example.team.mapapplication.engine.ViewLocationHelper;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;

import org.byteam.superadapter.OnItemClickListener;
import org.byteam.superadapter.OnItemLongClickListener;
import org.byteam.superadapter.SuperAdapter;
import org.byteam.superadapter.SuperViewHolder;


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

        mDrawerLayout.closeDrawer(Gravity.LEFT);
    }

    @Override
    public void removeHeatMap() {
        if (mHeatMap != null){
            mHeatMap.removeHeatMap();
        }
        mDrawerLayout.closeDrawer(Gravity.LEFT);
    }

    @Override
    public void refreshInfoList() {
        mAdapter.notifyDataSetChanged();
        mItemCountView.setText(mAdapter.getItemCount() + "");
    }

    @Override
    public void startLocate() {
//        mLocationClient.start();  abandon the previous way of locating. wyy
//        mLocationClient.requestLocation();

        if (mLocationService == null){
            // in case of unexpected disconnection between activity and service. wyy
            bindService(new Intent(this, LocationService.class), mConnection, BIND_AUTO_CREATE);
        }
        // call the onStartCommand() method by starting this service. wyy
        startService(new Intent(this, LocationService.class));
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
    public void hideFloatingViews() {
        if (mToolbar != null){
            animateToolbar(-mToolbar.getHeight());
            if (mModel.getModeStatus() == MainViewModel.EDIT_MODE) {
                hideEditViews();
            } else if (mModel.getModeStatus() == MainViewModel.WIFI_MODE) {
                hideWifiViews();
            }
        }
    }

    private void animateWifiModeViews(float value, float value2) {
        mStartPickBtn.animate().translationY(value);
        mEndPickBtn.animate().translationY(value2);
    }

    private void animateEditModeViews(float value, float value2) {
        mToEditBtn.animate().translationY(value); //TODO 待修改
        mLocateBtn.animate().translationY(value2);
    }

    private void animateToolbar(int value) {
        mToolbar.animate().translationY(value);
    }

    @Override
    public void showFloatingViews() {
        if (mToolbar != null){
            animateToolbar(0);
            if (mModel.getModeStatus() == MainViewModel.EDIT_MODE) {
                showEditViews();
            } else if (mModel.getModeStatus() == MainViewModel.WIFI_MODE) {
                showWifiViews();
            }
        }
    }


    @Override
    public void transferToEditModeView() {
        mToolbar.setSubtitle("编辑模式");
        hideWifiViews();
        showEditViews();
    }

    @Override
    public void transferToDisplayModeView() {

        mToolbar.setSubtitle("展示模式");

        hideEditViews();
        hideWifiViews();

        // animate to draw area. wyy
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(mModel.getDisplayData().get(0).getLatLng());
        mBaiduMap.animateMapStatus(mapStatusUpdate);
    }

    @Override
    public void transferToWifiModeView() {
        mToolbar.setSubtitle("信号模式");
        hideEditViews();
        showWifiViews();
    }

    @Override
    public void showWifiViews() {
        animateWifiModeViews(0, 0);
    }

    @Override
    public void hideWifiViews() {
        int spBtnY = ViewLocationHelper.getAbsoluteLocationOnScreen(mStartPickBtn)[1]; //使用getY()获得的是相对于父控件的坐标，这里直接用屏幕绝对坐标统一
        int epBtnY = ViewLocationHelper.getAbsoluteLocationOnScreen(mEndPickBtn)[1];
        animateWifiModeViews(mModel.getScreenHeight() - spBtnY, mModel.getScreenHeight() - epBtnY);
    }

    @Override
    public void showEditViews() {
        animateEditModeViews(0, 0);
    }

    @Override
    public void hideEditViews() {
        animateEditModeViews(mModel.getScreenHeight() - mToEditBtn.getY(), mModel.getScreenHeight() - mLocateBtn.getY());
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

//            mPresenter.sendLocateFinishedMessage();


        }

    }


    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mLocationService = ((LocationService.ServiceBinder) service).getService();
            mLocationService.setLocationInterface(new IntereactionForLocation() {
                @Override
                public void passLocation(LatLng latLng, double radius) {
                    mModel.setLatLng(latLng);
                    mModel.setRadius((float) radius);

                    mPresenter.setLocationData();

                    if (isFirstLoc){
                        mPresenter.animateToLoc();  // drawloc suffices to show the position, the animation will take place only after launch the app. wyy
                        isFirstLoc = false;
                    }


                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mLocationService = null;
        }
    };

    private LocationService mLocationService;


    private LinearLayout mWifiViewsContainer;
    private TextView mItemCountView;
    private Button mStartPickBtn;
    private Button mEndPickBtn;
    private ProgressBar mProgressBar;
    private DrawerLayout mDrawerLayout;
    private Button mRemoveHeatMapBtn;
    private Button mDrawHeatMapBtn;
    private Button mLocBtn;
    private FloatingActionButton mToEditBtn;
    private FloatingActionButton mLocateBtn;
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
    protected CharSequence getToolbarTitle() {
        return "热图绘制工具";
    }

    @Override
    protected CharSequence getSubTitle() {
        return "编辑模式";
    }

    @Override
    protected MainPresenter createPresenter() {
        return new MainPresenter();
    }

    @Override
    protected void onHomeIndicatorClicked() {
        mDrawerLayout.openDrawer(Gravity.LEFT);
        super.onHomeIndicatorClicked();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPresenter.attach(this);

        mModel.setScreenHeight(getResources().getDisplayMetrics().heightPixels);

        initialInit();

        initRecyclerView();

        initClickListener();

        initLocationClient();

        initiallyHideWifiViews();

        Intent intent = getIntent();
        if (intent != null && intent.getBooleanExtra("display_only", false)){
            mModel.setDisplayFileName(intent.getStringExtra("file_name"));
            mPresenter.transferToDisplayMode();
            return;
        }

        startLocate();

        bindService(new Intent(this, LocationService.class), mConnection, BIND_AUTO_CREATE);
        startService(new Intent(this, LocationService.class));
    }

    private void initiallyHideWifiViews() {
        int spBtnY = ViewLocationHelper.getAbsoluteLocationOnScreen(mStartPickBtn)[1]; //使用getY()获得的是相对于父控件的坐标，这里直接用屏幕绝对坐标统一
        int epBtnY = ViewLocationHelper.getAbsoluteLocationOnScreen(mEndPickBtn)[1];
        int screenHeight = mModel.getScreenHeight();
        mStartPickBtn.setY(screenHeight - spBtnY);
        mEndPickBtn.setY(screenHeight - epBtnY);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
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

        mItemCountView.setText(mAdapter.getItemCount() + "");

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

        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mPresenter.animateFloatingViews();
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });


        mStartPickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.startPick();
            }
        });

        mLocateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLocate();
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
            public void onClick(View v) {mPresenter.removeHeatMap();
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
        mWifiViewsContainer = findViewById(R.id.main_ll_wifi_mode);
        mItemCountView = findViewById(R.id.main_rl_data_description_tv_count);
        mStartPickBtn = findViewById(R.id.main_btn_start_pick);
        mEndPickBtn = findViewById(R.id.main_btn_end_pick);
        mLocateBtn = findViewById(R.id.main_fab_locate);
        mProgressBar = findViewById(R.id.main_pb_wait);
        mDrawerLayout = findViewById(R.id.main_dl);
        mReView = findViewById(R.id.main_rv_show_data);
        mToEditBtn = findViewById(R.id.main_fab_to_edit);
        mRemoveHeatMapBtn = findViewById(R.id.main_rl_ll_b_c_btn_clear);
        mDrawHeatMapBtn = findViewById(R.id.main_rl_ll_b_c_btn_draw);
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
        if (mModel.getModeStatus() != MainViewModel.DISPLAY_MODE){
            getMenuInflater().inflate(R.menu.input_topbar_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_edit_mode:
                if (mModel.getModeStatus() != MainViewModel.EDIT_MODE){
                    mPresenter.transferToEditMode();
                }
                break;
            case R.id.menu_item_wifi_mode:
                if (mModel.getModeStatus() != MainViewModel.WIFI_MODE){
                    mPresenter.transferToWifiMode();
                }
                break;
            case R.id.menu_item_data:
                startActivity(new Intent(this, RetrieveActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        if (mModel.getModeStatus() != MainViewModel.DISPLAY_MODE){
            if (mConnection != null){
                unbindService(mConnection);
            }
            stopService(new Intent(this, LocationService.class));
        }
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
