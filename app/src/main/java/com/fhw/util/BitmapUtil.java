package com.fhw.util;


import android.graphics.Bitmap;
import android.text.TextUtils;

import com.fhw.SystemConfig;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
/**
 * Created by fhw on 2017/3/11 0011.
 * email 11044818@qq.com
 */
public class BitmapUtil {

	private final static String Tag = BitmapUtil.class.getSimpleName();


	public static Bitmap zoomImage(Bitmap bgimage, int maxHight) {
		// 获取这个图片的宽和高
		float width = bgimage.getWidth();
		float height = bgimage.getHeight();
		try {
			// 计算宽高缩放率
			float scaleWidth = ((float) SystemConfig.getWidth()) / width;
			if (maxHight < (int) (height * scaleWidth)) {
				scaleWidth = maxHight / height;
				Bitmap returnBitmap = Bitmap.createScaledBitmap(bgimage,
						(int) (scaleWidth * width), maxHight, true);

				return returnBitmap;
			} else {
				Bitmap returnBitmap = Bitmap.createScaledBitmap(bgimage,
						SystemConfig.getWidth(),
						(int) (height * scaleWidth), true);

				return returnBitmap;
			}
			// 缩放图片动作

		} catch (OutOfMemoryError e) {
			// TODO: handle exception
			return bgimage;
		}

	}


	/**
	 * 图片是否已缓存
	 * 
	 * @param url_thumb
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static boolean checkImageExist(String url_thumb) {

		if (!TextUtils.isEmpty(url_thumb) && !url_thumb.equals("null")) {

			if (ImageLoader.getInstance().getMemoryCache().get(url_thumb) != null) {
				return true;
			}
			/*
			 * if (ImageLoader.getInstance().getMemoryCache().keys()
			 * .contains(url_thumb)) { return true; }
			 */

			File file = ImageLoader.getInstance().getDiskCache().get(url_thumb);
			if (file != null && file.exists()) {
				return true;
			}

		}
		return false;
	}

}
