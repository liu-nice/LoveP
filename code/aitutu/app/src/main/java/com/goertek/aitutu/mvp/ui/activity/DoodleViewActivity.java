package com.goertek.aitutu.mvp.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.goertek.aitutu.R;
import com.goertek.aitutu.mvp.ui.custom.doodle.DoodleView;
import com.goertek.arm.base.BaseActivity;
import com.goertek.arm.di.component.AppComponent;

/**
 * 用于展示 DoodleView 功能的 Activity
 * @author wanghong
 * @version 1.0
 * @since 2019-09-20
 */

public class DoodleViewActivity extends BaseActivity {

    private static final String TAG = "DoodleViewActivity";

    // 默认画笔尺寸
    private int defaultPaintSize = 5;
    // 中号画笔尺寸
    private int middlePaintSize = 10;
    // 大号画笔尺寸
    private int maxPaintSize = 15;

    private DoodleView mDoodleView;
    private AlertDialog mColorDialog;
    private AlertDialog mPaintDialog;
    private AlertDialog mShapeDialog;
    private ImageView ivImage;
    private Toolbar mToolbar;
    public static final String FILE_PATH = "file_path";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doodleview);
        Intent intent = getIntent();
        String photoPath = intent.getStringExtra(FILE_PATH);
        Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
        Drawable bitmapDrawable = new BitmapDrawable(bitmap);

        mDoodleView = ( DoodleView ) findViewById(R.id.doodle_doodleview);
        mToolbar = findViewById(R.id.activity_edit_revolve_toolbar);
        mDoodleView.setBackground(bitmapDrawable);
        ivImage = (ImageView) findViewById(R.id.iv_image);
        mDoodleView.setSize(dip2px(defaultPaintSize));

        initToolbar();
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setNavigationOnClickListener(view -> finish());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mDoodleView.onTouchEvent(event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.main_color:
                showColorDialog();
                break;
            case R.id.main_size:
                showSizeDialog();
                break;
            case R.id.main_action:
                mDoodleView.setMode(0);
                showShapeDialog();
                break;
            case R.id.main_eraser:
                mDoodleView.setMode(1);
                mDoodleView.eraser();
                break;
            case R.id.main_save:
                String path = mDoodleView.saveBitmap(mDoodleView);
                ivImage.setImageBitmap(mDoodleView.getBitmap());
                Log.d(TAG, "onOptionsItemSelected: " + path);
                Toast.makeText(this, "保存图片的路径为：" + path, Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 显示选择画笔颜色的对话框
     */
    private void showColorDialog() {
        if(mColorDialog == null){
            mColorDialog = new AlertDialog.Builder(this)
                    .setTitle("选择颜色")
                    .setSingleChoiceItems(new String[]{"蓝色", "红色", "黑色"}, 0,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which){
                                        case 0:
                                            mDoodleView.setColor("#0000ff");
                                            break;
                                        case 1:
                                            mDoodleView.setColor("#ff0000");
                                            break;
                                        case 2:
                                            mDoodleView.setColor("#272822");
                                            break;
                                        default:
                                            break;
                                    }
                                    dialog.dismiss();
                                }
                            }).create();
        }
        mColorDialog.show();
    }

    /**
     * 显示选择画笔粗细的对话框
     */
    private void showSizeDialog(){
        if(mPaintDialog == null){
            mPaintDialog = new AlertDialog.Builder(this)
                    .setTitle("选择画笔粗细")
                    .setSingleChoiceItems(new String[]{"细", "中", "粗"}, 0,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which){
                                        case 0:
                                            mDoodleView.setSize(dip2px(defaultPaintSize));
                                            break;
                                        case 1:
                                            mDoodleView.setSize(dip2px(middlePaintSize));
                                            break;
                                        case 2:
                                            mDoodleView.setSize(dip2px(maxPaintSize));
                                            break;
                                        default:
                                            break;
                                    }
                                    dialog.dismiss();
                                }
                            }).create();
        }
        mPaintDialog.show();
    }

    /**
     * 显示选择画笔形状的对话框
     */
    private void showShapeDialog(){
        if(mShapeDialog == null){
            mShapeDialog = new AlertDialog.Builder(this)
                    .setTitle("选择形状")
                    .setSingleChoiceItems(new String[]{"路径", "直线", "虚线", "圆形", "三角形"}, 0,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which){
                                        case 0:
                                            mDoodleView.setType(DoodleView.ActionType.Path);
                                            break;
                                        case 1:
                                            mDoodleView.setType(DoodleView.ActionType.Line);
                                            break;
                                        case 2:
                                            mDoodleView.setType(DoodleView.ActionType.DottedLine);
                                            break;
                                        case 3:
                                            mDoodleView.setType(DoodleView.ActionType.Circle);
                                            break;
                                        case 4:
                                            mDoodleView.setType(DoodleView.ActionType.Triangle);
                                            break;
                                        default:
                                            break;
                                    }
                                    dialog.dismiss();
                                }
                            }).create();
        }
        mShapeDialog.show();
    }

    private int dip2px(float dpValue){
        final float scale = getResources().getDisplayMetrics().density;
        return (int)(dpValue * scale + 0.5f);
    }

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {

    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return 0;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {

    }
}














