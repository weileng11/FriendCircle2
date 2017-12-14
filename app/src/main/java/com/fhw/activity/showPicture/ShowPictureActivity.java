package com.fhw.activity.showPicture;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;

import com.fhw.Logger;
import com.fhw.SystemConfig;
import com.fhw.activity.BaseActivity;
import com.fhw.util.BitmapUtil;
import com.fhw.util.StringUtil;
import com.fhw.widget.ImageViewPaper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import circle.fhw.com.friendcircle.R;

/**
 * Created by fhw on 2017/3/11 0011.
 * email 11044818@qq.com
 * 显示大图
 */
public class ShowPictureActivity extends BaseActivity implements ViewPager.OnPageChangeListener{

    /**
     * 重写 onInterceptTouchEvent解决与photoview的冲突
     */
    private ImageViewPaper viewPager;
    private List<String> imgDatas;

    /**
     * 点击的item对应下标
     */
    private int positon;
    private ImagePagerAdapter adapter;


    /**
     * 是否需要关闭时的动画，（在聊天页面图片个数无上限且不确定图片个数的时候应该关闭）
     */
    private boolean animation = true;
    private int item_width = 50;
    private int item_hight = 50;
    public int x;// 首item x坐标
    public int y;// 首item y坐标
    public int horizontal_space;//水平间距
    public int vertical_space;//垂直间距
    public int column_num;//每行个数


    /**
     * 是否第一次展示
     */
    private boolean isFirst = true;

    @Override
    public void onCreate(Bundle paramBundle) {
        // TODO Auto-generated method stub
        is_full_screen = true;
        is_diy_anim = true;

        super.onCreate(paramBundle);
    }
    @Override
    protected void loadViewLayout() {

        setContentView(R.layout.act_show_picture);
        overridePendingTransition(0, 0);

    }
    @Override
    protected void findViewById() {
        viewPager = (ImageViewPaper) findViewById(R.id.viewPager_showPhoto);

    }

    @Override
    protected void initOther() {
        if (rootView != null && rootView.getRootView() != null) {
            rootView.getRootView().setBackgroundColor(ContextCompat.getColor(mContext, R.color.transparent));
        }
        // TODO Auto-generated method stub
        if (getIntent() != null && getIntent().hasExtra("imgdatas")) {
            Gson gson = new Gson();

            imgDatas = gson.fromJson(getIntent().getStringExtra("imgdatas"), new TypeToken<List<String>>(){}.getType());

            item_width = getIntent().getIntExtra("width",
                    SystemConfig.getWidth() * 2 / 5);
            item_hight = getIntent().getIntExtra("hight",
                    SystemConfig.getWidth() * 2 / 5);
            column_num = getIntent().getIntExtra("column_num", 1);
            horizontal_space = getIntent().getIntExtra("horizontal_space", 1);
            vertical_space = getIntent().getIntExtra("vertical_space", 1);
            x = getIntent()
                    .getIntExtra("x", SystemConfig.getWidth() / 2);
            y = getIntent()
                    .getIntExtra("y", SystemConfig.getHight() / 2);
            positon = getIntent().getIntExtra("position", 0);
            currentPosition = positon;

            animation = getIntent().getBooleanExtra("animation", true);
            x = buildOriginXY(positon)[0];
            y = buildOriginXY(positon)[1];


            if (imgDatas == null ||imgDatas.size() == 0 ) {
                finishAct();
                return;
            }

            /**
             * 当前item缩略图没加载完成不能显示
             */
            if (!BitmapUtil.checkImageExist(StringUtil.getThumb(imgDatas.get(positon),item_hight, item_width))) {
                finishAct();
                return;
            }

            initViewPager();
        } else {
            finishAct();
        }
    }

    @Override
    protected void setListener() {
        viewPager.addOnPageChangeListener(this);

    }

    /**
     * 初始化viewpaper
     */
    private void initViewPager() {
        Logger.i(Tag,Tag + " initViewPager");
        adapter = new ImagePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(positon);


    }

    /**
     * 记录当前IamgeFragment以及上一个，下一个，为了按返回键的时候能够调用当前amgeFragment关闭的动画
     */
    private int currentPosition = -1;
    private ImageFragment imageFragmentCurrent;
    private ImageFragment imageFragmentPre;
    private ImageFragment imageFragmentNext;

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Logger.i(Tag,Tag +" onPageSelected position:"+position);
        if (position + 1 == currentPosition){
            imageFragmentNext = imageFragmentCurrent;
            imageFragmentCurrent = imageFragmentPre;
        }else if (position - 1 == currentPosition){
            imageFragmentPre = imageFragmentCurrent;
            imageFragmentCurrent = imageFragmentNext;
        }
        currentPosition = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    private class ImagePagerAdapter extends FragmentStatePagerAdapter {

        public ImagePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return imgDatas == null ? 0 : imgDatas.size();
        }
        @Override
        public int getItemPosition(Object object) {
            // TODO Auto-generated method stub
            return POSITION_NONE;
        }
        // ImageFragment.newInstance(imgDatas.get(position),item_width,item_hight,position,buildXY(position)[0],buildXY(position)[1]);
        @Override
        public Fragment getItem(int position) {
            Logger.i(Tag,Tag +" ImagePagerAdapter getItem position:"+position);
            ImageFragment imageFragment = ImageFragment.newInstance(imgDatas.get(position),
                    item_width, item_hight, position, buildXY(position)[0],
                    buildXY(position)[1]);

            /**
             * 记录最多3个imageFragment
             */
            if (position == viewPager.getCurrentItem()){
                imageFragmentCurrent = imageFragment;
            }else if (position + 1 == viewPager.getCurrentItem()){
                imageFragmentPre = imageFragment;
            }else if (position - 1 == viewPager.getCurrentItem()){
                imageFragmentNext = imageFragment;
            }
            return imageFragment;
            // return fragments.get(position);
        }
    }

    private  boolean isFinish = false;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            /**
             * 防止连续点击返回键
             */
            if (isFinish)return true;
            isFinish = true;

            if (imageFragmentCurrent != null){
                imageFragmentCurrent.finishFlash();
            }else{
                //showStatusBar();
                if (isAnimation() || currentPosition == getPositon()) {
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            finishAct();
                        }
                    }, 225);
                }else {
                    finishAct();
                }
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    public void finishAct() {

        finish();
        overridePendingTransition(0, 0);
    }


    /**
     * 当前位置获取首item x,y
     *
     * @param position
     * @return
     */
    public int[] buildOriginXY(int position) {
        int[] location = new int[2];
        int buildX = x - (position % column_num)
                * (item_width + horizontal_space);
        int buildY = y - (position / column_num)
                * (item_hight + vertical_space);
        location[0] = buildX;
        location[1] = buildY;
        return location;
    }

    /**
     * 根据首item x,y获取当前item 的x,y
     *
     * @param position
     * @return
     */
    public int[] buildXY(int position) {
        int[] location = new int[2];
        int buildX = x + (position % column_num)
                * (item_width + horizontal_space);
        int buildY = y + (position / column_num)
                * (item_hight + vertical_space);
        location[0] = buildX;
        location[1] = buildY;
        return location;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

    }


    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public void setFirst(boolean first) {
        isFirst = first;
    }

    public int getPositon() {
        return positon;
    }

    public void setPositon(int positon) {
        this.positon = positon;
    }


    public boolean isAnimation() {
        return animation;
    }

    public void setAnimation(boolean animation) {
        this.animation = animation;
    }

    public boolean isFinish() {
        return isFinish;
    }

    public void setFinish(boolean finish) {
        isFinish = finish;
    }
}

