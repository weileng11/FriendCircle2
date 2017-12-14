package com.fhw.activity.friendCircle;


import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.fhw.ImageUrl;
import com.fhw.Logger;
import com.fhw.SystemConfig;
import com.fhw.activity.BaseActivity;
import com.fhw.bean.CircleData;
import com.fhw.util.NormalUtil;
import com.fhw.util.StringUtil;
import com.fhw.widget.MyGridView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import circle.fhw.com.friendcircle.R;
/**
 * Created by fhw on 2017/3/11 0011.
 * email 11044818@qq.com
 * 朋友圈
 */
public class FriendCirclesActivity extends BaseActivity {


	private ListView listView;
	private List<CircleData> list = new ArrayList<CircleData>();
	private FriendsCirclesAdapter friendsCirclesAdapter ;
	private int imageSize = 0;
	private Toolbar toolbar;
	@Override
	public void onClick(View view) {

	}

	@Override
	protected void loadViewLayout() {

		setContentView(R.layout.act_friend_circle);
	}

	@Override
	protected void findViewById() {
		listView = (ListView) findViewById(R.id.content_view);
		toolbar = (Toolbar) findViewById(R.id.toolbar);
	}
	@Override
	protected void initOther() {


		toolbar.setTitle(R.string.title_circle);

		setSupportActionBar(toolbar);


		getScreenData();
		imageSize = (SystemConfig.getWidth() - NormalUtil.dip2px(mContext, 90)
				- 2*NormalUtil.dip2px(mContext, 3))/3;

		friendsCirclesAdapter = new FriendsCirclesAdapter(mContext, list);
		listView.setAdapter(friendsCirclesAdapter);

		doInitData();
	}

	@Override
	protected void setListener() {

		//toolbar的menu点击事件的监听
		toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {

				if (item.getItemId() == R.id.menu_item_clean_cache) {
					ImageLoader.getInstance().clearDiskCache();
					ImageLoader.getInstance().clearMemoryCache();

					friendsCirclesAdapter.notifyDataSetChanged();
				}
				return true;
			}
		});

		listView.setOnScrollListener(new AbsListView.OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
					listView.setOnScrollListener(null);
					friendsCirclesAdapter.setScrolling(false);
					friendsCirclesAdapter.notifyDataSetChanged();
					listView.setOnScrollListener(this);
					// 到达底部
					if (view.getLastVisiblePosition() + 2 >= friendsCirclesAdapter.getCount()) {
						Logger.i(Tag, Tag+ "view.getLastVisiblePosition() + 2 >= adapter.getCo");
						getData(true);
					}
				}else {

					friendsCirclesAdapter.setScrolling(true);

				}
			}

			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
			}
		});


	}

	public void getScreenData(){

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenWidth = dm.widthPixels;
		int screenHeigh = dm.heightPixels;
		int screenTop = SystemConfig.getStatusHeight(mContext);

		SystemConfig.setHight(screenHeigh);
		SystemConfig.setWidth(screenWidth);
		SystemConfig.setTop(screenTop);


		Logger.i(Tag,Tag+" screenWidth:"+screenWidth+" screenHeigh:"+screenHeigh);
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}
	
	
	public void doInitData(){
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				getData(false);
			}
		}, 55);

	}



	public void getData(final boolean isMore) {
		
		if (mContext == null) {
			return;
		}
		List<CircleData> datas = new ArrayList<CircleData>();
		for (int i = 0 ;i < 10; i ++){
			CircleData circleData = new CircleData();
			Random random = new Random();
			int imageCount = random.nextInt(6+1);

			List<String> imageDatas = new ArrayList<String>();
			for (int j = 0; j < imageCount; j++){
				int imageIndex = random.nextInt(ImageUrl.imgUrls.length);
				String url = ImageUrl.imgUrls[imageIndex];
				imageDatas.add(url);
			}
			circleData.setImageDatas(imageDatas);
			datas.add(circleData);
		}
		list.addAll(datas);
		friendsCirclesAdapter.notifyDataSetChanged();


	}




	public class FriendsCirclesAdapter extends BaseAdapter {

		private LayoutInflater mInflater;
		private List<CircleData> list;

	    private Context context;
		private boolean scrolling = false;

		public FriendsCirclesAdapter(Context context, List<CircleData> collectInfos) {
			this.mInflater = LayoutInflater.from(context);
			list = collectInfos;
			this.context = context;
		}


		public int getCount() {
			return list == null ? 0 : list.size();
		}

		public Object getItem(int position) {
			return list.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			
			ViewHold viewHold = null;
			
			if (convertView == null) {
				viewHold = new ViewHold();
				convertView = mInflater.inflate(R.layout.friend_circle_item, null);

				viewHold.gvPic = (MyGridView)convertView.findViewById(R.id.gridview);

				convertView.setTag(viewHold);
			} else {
				viewHold = (ViewHold) convertView.getTag();
			}

			if (list.get(position).getImageDatas() == null ||list.get(position).getImageDatas().size() == 0) {
				viewHold.gvPic.setVisibility(View.GONE);
			}else {
				viewHold.gvPic.setVisibility(View.VISIBLE);
				if (list.get(position).getImageDatas().size() == 1) {

					viewHold.gvPic.setNumColumns(1);
					viewHold.gvPic.getLayoutParams().width = StringUtil
							.getThumbSize(list.get(position).getImageDatas().get(0) + "")
							.x;

				} else {
					viewHold.gvPic.setNumColumns(3);
					viewHold.gvPic.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
				}
				ImageAdapter adapter = new ImageAdapter(context, list.get(position).getImageDatas(),imageSize);

				adapter.setScrolling(isScrolling());
				viewHold.gvPic.setAdapter(adapter);
			}

			return convertView;
		}
		class ViewHold {

			MyGridView gvPic;

		}
		public boolean isScrolling() {
			return scrolling;
		}

		public void setScrolling(boolean scrolling) {
			this.scrolling = scrolling;
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.menu_item, menu);//加载menu文件到布局

		return true;
	}
}
