package com.fhw.util;

import android.content.Context;
import android.text.TextUtils;
/**
 * Created by fhw on 2017/3/11 0011.
 * email 11044818@qq.com
 */
public class NormalUtil {

	public static boolean isNull(String string){
		if (TextUtils.isEmpty(string) || string.equals("null"))  {
			return true;
		}
		return false;
	}
	public static int dip2px(Context context, float dipValue){ 
        if (context == null) {
			return (int)(dipValue * 1 + 0.5f);
		}
		final float scale = context.getResources().getDisplayMetrics().density;
        
        return (int)(dipValue * scale + 0.5f);
	}

}