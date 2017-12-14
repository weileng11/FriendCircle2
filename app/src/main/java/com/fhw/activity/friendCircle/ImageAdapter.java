package com.fhw.activity.friendCircle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.fhw.activity.showPicture.ShowPictureActivity;
import com.fhw.util.BitmapUtil;
import com.fhw.util.NormalUtil;
import com.fhw.util.StringUtil;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import circle.fhw.com.friendcircle.R;
/**
 * Created by fhw on 2017/3/11 0011.
 * email 11044818@qq.com
 */
public class ImageAdapter extends ArrayAdapter<String> {
	 	private boolean scrolling = false;
	 	private Context mContext;
	 	private List<String> imgs;
	 	private int itemSize;

	 	public boolean isScrolling() {
	 		return scrolling;
	 	}

	 	public void setScrolling(boolean scrolling) {
	 		this.scrolling = scrolling;
	 	}
       
        public ImageAdapter(Context context, List<String> imgs, int itemSize) {
            super(context, 0, imgs);
            this.mContext = context;
            this.imgs = imgs;
            this.itemSize= itemSize;

        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            Activity activity = (Activity) getContext();

            ViewHolder viewHold;
            if (convertView == null) {
            	viewHold = new ViewHolder();
                LayoutInflater inflater = activity.getLayoutInflater();
                convertView = inflater.inflate(R.layout.imageview, null);
                viewHold.imageView = (ImageView) convertView.findViewById(R.id.image);
                convertView.setTag(viewHold);
            } else {
                viewHold = (ViewHolder) convertView.getTag();
            }

			String imageUrl = getItem(position);
			final int width;
			final int hight;

			if (getCount() == 1) {
				Point sizeData = StringUtil.getThumbSize(imageUrl);
				width = sizeData.x;
				hight = sizeData.y;

			}else {
				width = itemSize;
				hight = itemSize;
			}
            viewHold.imageView.getLayoutParams().width = width;
            viewHold.imageView.getLayoutParams().height = hight;

            imageUrl = StringUtil.getThumb(imageUrl, hight, width);
            if (!NormalUtil.isNull(imageUrl)  && (!scrolling || BitmapUtil.checkImageExist(imageUrl))) {
           	 	ImageLoader.getInstance().displayImage( imageUrl, viewHold.imageView);
	   		}else {
	   			viewHold.imageView.setImageResource(R.color.img_default_gray);
	   		}

            convertView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(mContext,ShowPictureActivity.class);
					Bundle bundle = new Bundle();
					int[] location = new int[2];
				    v.getLocationInWindow(location);
				    int x = location[0];
				    int y = location[1];
				    bundle.putInt("x", x);
					bundle.putInt("y", y);
					
					bundle.putInt("width", width);
					bundle.putInt("hight", hight);
					Gson gson = new Gson();
					bundle.putString("imgdatas", gson.toJson(imgs));
					bundle.putInt("position", position);
					bundle.putInt("column_num", 3);
					bundle.putInt("horizontal_space",  NormalUtil.dip2px(mContext, 3));
					bundle.putInt("vertical_space", NormalUtil.dip2px(mContext, 3));

					intent.putExtras(bundle);
					mContext.startActivity(intent);
				}
			});
            return convertView;
        }
        
        public class ViewHolder{
        	ImageView imageView;
        }

}