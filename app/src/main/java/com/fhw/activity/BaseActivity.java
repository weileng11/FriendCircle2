package com.fhw.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.fhw.Logger;
import com.fhw.SystemConfig;

import circle.fhw.com.friendcircle.R;

/**
 * Created by fhw on 2017/3/11 0011.
 * email 11044818@qq.com
 *
 */
public abstract class BaseActivity extends AppCompatActivity implements OnClickListener {

	protected String Tag = this.getClass().getSimpleName();

	protected Activity mContext;
	private InputMethodManager manager;
	protected boolean is_full_screen = false;
	protected boolean is_diy_anim = false;
	protected View rootView = null;
	protected Bundle savedInstanceState = null;
	

	/**
     * 在完整生命周期开始时调用
     * */
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        if(!isTaskRoot()){
            Intent intent = getIntent();
            String action = intent.getAction();
            if(intent.hasCategory(Intent.CATEGORY_LAUNCHER) && action.equals(Intent.ACTION_MAIN)){
                finish();
                return;
            }
        }
		Logger.i(Tag, Tag+" life onCreate");
		mContext = this;

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		/**
		 * 全屏不重绘
		 */
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

		initView();
		this.savedInstanceState = null;
    }
    /**
     * 在onCreate方法完成后调用，用于恢复UI状态
     * */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        Logger.i(Tag, Tag+" onRestoreInstanceState");
        //从savedInstanceState恢复UI状态
        //这个bundle也被传递给了onCreate
        //自Activity上次可见之后，只有当系统终止了该Activity时，才会被调用
        
        //在随后的Activity进程的onRestart可见生存期之前调 用
    }
    
    @Override
    protected void onRestart(){
        super.onRestart();
        Logger.i(Tag, Tag+" onRestart");
        //加装载改变，知道Activity在此进程中已经可见
    }
    
    /**
     * 在可见生存期的开始时调用
     * */
    @Override
    protected void onStart(){
        super.onStart();
        Logger.i(Tag, Tag+" onStart");
        //既然Activity可见,就应用任何要求的UI Change
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
    	// TODO Auto-generated method stub
    	super.onNewIntent(intent);
    	Logger.i(Tag, Tag+" onNewIntent");
    }
    /**
     * 在Activity状态生存期开始时调用
     * */
    @Override
    protected void onResume(){
        super.onResume();
        Logger.i(Tag, Tag+" life onresume");
    }
    
    /**
     * 把UI状态改变保存到saveInstanceState
     * */
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState){
        //如果进程被运行时终止并被重启，那么这个Bundle将被传递给onCreate和onRestoreInstanceState
        super.onSaveInstanceState(savedInstanceState);
        Logger.i(Tag, Tag+" onSaveInstanceState");
    }
    
    /**
     * 在Activity状态生存期结束时调用
     * */
    @Override
    protected void onPause(){
        //挂起不需要更新的UI更新、线程或者CPU密集的进程
        //当Activity不是前台的活动状态的Activity时
        super.onPause();
        Logger.i(Tag, Tag+" life onPause");
    }
    
    /**
     * 在可见生存期结束时调用
     * */
    @Override
    protected void onStop(){
        //挂起不需要的UI更新、线程或处理，当Activity不可见时，保存所有的编辑或者状态改变，因为在调用这个方法后，进程可能会被终止
        Logger.i(Tag, Tag+" life onStop");
		hideKeyboard();
		super.onStop();
    }
    /**
     * 在完整生存期结束时调用
     * */
    @Override
    protected void onDestroy(){
        //清理所有的资源，包括结束线程、关闭数据库连接等
    	Logger.i(Tag, Tag+" life onDestroy");
		hideKeyboard();

		rootView = null;
        super.onDestroy();
    }

	@Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideKeyboard(v, ev)) {
                hideKeyboard();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

	public void hideStatusBar() {
		WindowManager.LayoutParams attrs = getWindow().getAttributes();
		attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
		getWindow().setAttributes(attrs);

		is_full_screen = false;
		((FrameLayout.LayoutParams)rootView.getLayoutParams()).topMargin = 0;

	}
	public void showStatusBar() {
		WindowManager.LayoutParams attrs = getWindow().getAttributes();
		attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
		getWindow().setAttributes(attrs);
		is_full_screen = true;
		((FrameLayout.LayoutParams)rootView.getLayoutParams()).topMargin = SystemConfig.getTop();

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
	 * 初始化界面
	 */
	private void initView() {
		loadViewLayout();

		if (!is_full_screen){
			((FrameLayout.LayoutParams)rootView.getLayoutParams()).topMargin = SystemConfig.getTop();
		}
		if (!is_diy_anim) {
			overridePendingTransition(R.anim.activity_x_plus_anim, R.anim.activity_alpha1_anim);
		}
		findViewById();
		initOther();
		setListener();
	}

	@Override
	public void setContentView(int layoutResID) {
		// TODO Auto-generated method stub
		rootView = getLayoutInflater().inflate(layoutResID, null);

		super.setContentView(rootView);
	}

	/**
	 * 加载布局
	 */
	protected abstract void loadViewLayout();

	/**
	 * find控件
	 */
	protected abstract void findViewById();

	/**
	 * 初始化其他
	 */
	protected abstract void initOther();

	/**
	 * 设置监听
	 */
	protected abstract void setListener();

	/**
	 * 隐藏软键盘
	 */
	public void hideKeyboard() {
		Logger.i(Tag, Tag+"hideKeyboard 1");
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			Logger.i(Tag, Tag+"hideKeyboard 2");
			if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null){
				Logger.i(Tag, Tag+"hideKeyboard 3");
				manager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), 0);//
		}
		}
	}
	/**
	 * 强制退出软键盘
	 */
	public void hideKeyboard(View view) {
		manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}
	/**
	 * 显示软键盘
	 */
	public void showKeyboard() {

		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				manager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.SHOW_FORCED);
		}
	}
	public void showKeyboard(View view) {
		manager.showSoftInput(view,InputMethodManager.SHOW_FORCED);
	}
	
	




	/**
	 * TRIM_MEMORY_RUNNING_MODERATE
	 * 表示应用程序正常运行，并且不会被杀掉。但是目前手机的内存已经有点低了，系统可能会开始根据LRU缓存规则来去杀死进程了。
	 * TRIM_MEMORY_RUNNING_LOW
	 * 表示应用程序正常运行，并且不会被杀掉。但是目前手机的内存已经非常低了，我们应该去释放掉一些不必要的资源以提升系统的性能
	 * ，同时这也会直接影响到我们应用程序的性能。 
	 * TRIM_MEMORY_RUNNING_CRITICAL
	 * 表示应用程序仍然正常运行，但是系统已经根据LRU缓存规则杀掉了大部分缓存的进程了
	 * 。这个时候我们应当尽可能地去释放任何不必要的资源，不然的话系统可能会继续杀掉所有缓存中的进程
	 * ，并且开始杀掉一些本来应当保持运行的进程，比如说后台运行的服务。
	 * 以上是当我们的应用程序正在运行时的回调，那么如果我们的程序目前是被缓存的，则会收到以下几种类型的回调：
	 * 
	 * TRIM_MEMORY_BACKGROUND
	 * 表示手机目前内存已经很低了，系统准备开始根据LRU缓存来清理进程。这个时候我们的程序在LRU缓存列表的最近位置
	 * ，是不太可能被清理掉的，但这时去释放掉一些比较容易恢复的资源能够让手机的内存变得比较充足
	 * ，从而让我们的程序更长时间地保留在缓存当中，这样当用户返回我们的程序时会感觉非常顺畅，而不是经历了一次重新启动的过程。
	 * TRIM_MEMORY_MODERATE
	 * 表示手机目前内存已经很低了，并且我们的程序处于LRU缓存列表的中间位置，如果手机内存还得不到进一步释放的话，那么我们的程序就有被系统杀掉的风险了。
	 * TRIM_MEMORY_COMPLETE
	 * 表示手机目前内存已经很低了，并且我们的程序处于LRU缓存列表的最边缘位置，系统会最优先考虑杀掉我们的应用程序
	 * ，在这个时候应当尽可能地把一切可以释放的东西都进行释放。
	 */
	@Override
	public void onTrimMemory(int level) {
		super.onTrimMemory(level);
		switch (level) {
		case TRIM_MEMORY_UI_HIDDEN:

		case TRIM_MEMORY_RUNNING_MODERATE:
		case TRIM_MEMORY_RUNNING_LOW:
		case TRIM_MEMORY_RUNNING_CRITICAL:

		case TRIM_MEMORY_BACKGROUND:
		case TRIM_MEMORY_MODERATE:
		case TRIM_MEMORY_COMPLETE:
			break;
		}

	}
	
	@Override
	public void finish() {

		super.finish();
		if (!is_diy_anim) {
			overridePendingTransition(0, R.anim.activity_x_add_anim);
		}
	}


}
