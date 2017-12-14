package com.fhw.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;


/** 
* @author 作者 fhw: 
* @version 创建时间：2015-11-2 上午10:21:18 
* 类说明 ：用于imgage的分栏
*/ 
public class ImageViewPaper extends ViewPager {


	public ImageViewPaper(Context context)
	{
	    super(context);
	}
	
	public ImageViewPaper(Context context, AttributeSet attrs)
	{
	    super(context, attrs);
	}
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev)
	{
		
		
	    try{
	       return super.onInterceptTouchEvent(ev);
	    }catch (IllegalArgumentException e){
	    	
	    }catch (ArrayIndexOutOfBoundsException e){
	
	    }
	    return false;
	}
	
	
}
