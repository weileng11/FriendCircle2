package com.fhw.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.fhw.Logger;

/**
 * Created by fhw on 2017/3/11 0011.
 * email 11044818@qq.com
 */
public abstract class BaseFragment extends Fragment implements OnClickListener {

	protected String Tag = this.getClass().getSimpleName();
	protected TextView textShopCarNum;
	public Activity mContext;

	private InputMethodManager manager;

	protected View rootView;// 根视图
	protected LayoutInflater inflater;


	/**
	 * Android生命周期回调方法-创建
	 */

	@Override
	public void onCreate(Bundle paramBundle) {
		
		super.onCreate(paramBundle);
		Logger.i(Tag, Tag + "life onCreate");
		mContext = getActivity();
		manager = (InputMethodManager) getActivity().getSystemService(
				Context.INPUT_METHOD_SERVICE);
		
	}
	
	public View getRootView() {
		return rootView;
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		Logger.i(Tag, Tag + "life onCreateView");
		super.onViewCreated(container, savedInstanceState);
		if (null != rootView) {
			mContext = getActivity();
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (null != parent) {
                parent.removeView(rootView);
            }
        } else {
        	mContext = getActivity();
    		this.inflater = inflater;
    		loadViewLayout(container);
    		initView();
        }
		
		return rootView;
	}

	
	@Override
	public void onResume() {

		super.onResume();
		mContext = getActivity();
		Logger.i(Tag, Tag + "life onResume");
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		hideKeyboard();
		super.onPause();
	}
	/**
	 * 初始化界面
	 */
	private void initView() {
		
		findView();

		initOther();
		setListener();
	}

	
	/**
	 * 加载布局
	 */
	protected abstract void loadViewLayout(ViewGroup container);

	/**
	 * find控件
	 */
	protected abstract void findView();

	/**
	 * 初始化其他
	 */
	protected abstract void initOther();

	/**
	 * 设置监听
	 */
	protected abstract void setListener();

	protected View findView(int id) {
		return rootView.findViewById(id);
	}


	 /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     * @param v
     * @param event
     * @return
     */
    protected boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],
                top = l[1],
                bottom = top + v.getHeight(),
                right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                //点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditText上，和用户用轨迹球选择其他的焦点
        return false;
    }


	
	/**
	 * 隐藏软键盘
	 */
	public void hideKeyboard() {
		if (getActivity() == null) {
			return;
		}
		if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getActivity().getCurrentFocus() != null && getActivity().getCurrentFocus().getWindowToken() != null){
				manager.hideSoftInputFromWindow(getActivity().getCurrentFocus()
						.getWindowToken(), 0);//
			}

		}
	}
	public void showKeyboard(final View view) {
		
		manager.showSoftInput(view,InputMethodManager.SHOW_FORCED);  
	}


	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Logger.i(Tag, Tag + "life onDestroy");

		rootView = null;
		super.onDestroy();
	}
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		Logger.i(Tag, Tag + "life onDestroyView");
		super.onDestroyView();
	}



}
