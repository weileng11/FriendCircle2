/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fhw;


import android.app.Application;
import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.FIFOLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import circle.fhw.com.friendcircle.R;
/**
 * Created by fhw on 2017/3/11 0011.
 * email 11044818@qq.com
 */
public class AppContext extends Application {

	protected String Tag = this.getClass().getSimpleName();

	@Override
	public void onCreate() {
	
		super.onCreate();

		Logger.i(Tag,Tag + " onCreate");
		SystemConfig.init(getApplicationContext());
	    initImageLoader(getApplicationContext());

	}


	
	private void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder() //
		.showImageForEmptyUri(R.color.img_default_gray) //
		.showImageOnFail(R.color.img_default_gray) //
		.cacheInMemory(true) //
		.cacheOnDisk(true) //
		.build();//
		ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(
				context);
		config.threadPriority(Thread.NORM_PRIORITY - 2);
		config.denyCacheImageMultipleSizesInMemory();
		config.memoryCache(new FIFOLimitedMemoryCache(2 * 1024 * 1024));
		config.defaultDisplayImageOptions(defaultOptions);//

		/*
		 * config.memoryCacheExtraOptions(720, 1280);
		 * config.diskCacheExtraOptions(720, 1280, null);
		 */
		config.diskCacheFileNameGenerator(new Md5FileNameGenerator());

		config.tasksProcessingOrder(QueueProcessingType.LIFO);
		// config.writeDebugLogs(); // Remove for release app

		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config.build());
	}

}
