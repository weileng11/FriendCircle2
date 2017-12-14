package com.fhw.activity.showPicture;

import android.animation.Animator;
import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.fhw.Logger;
import com.fhw.SystemConfig;
import com.fhw.activity.BaseFragment;
import com.fhw.util.BitmapUtil;
import com.fhw.util.NormalUtil;
import com.fhw.util.StringUtil;
import com.fhw.widget.photoview.PhotoViewAttacher;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import circle.fhw.com.friendcircle.R;

/**
 * Created by fhw on 2017/3/11 0011.
 * email 11044818@qq.com
 * 图片fragment
 */
public class ImageFragment extends BaseFragment {

	private ImageView photoView;
	private ProgressBar progressBar;
	private View v_parent;
	private PhotoViewAttacher mAttacher;

	/**
	 * 图片url
	 */
	private String imgData;

	/**
	 * 缩略图宽高
	 */
	private int width;
	private int hight;

	/**
	 * 缩略图左上角坐标
	 */
	private int x;
	private int y;
	/**
	 * 对应下标
	 */
	private int position;


	/**
	 * 动画时长
	 */
	private final int NORMAL_SCALE_DURATION = 300;

	/**
	 * 背景透明度渐变动画
	 */
	private AlphaAnimation bgAlphaAnimation;

	/**
	 *值变换动画
	 */
	private ValueAnimator valueAnimator;

	@Override
	public void onClick(View v) {

	}

	/**
	 * 构造方法
	 * @param imgData  图片url
	 * @param width    缩略图宽度
	 * @param hight    缩略图高度
	 * @param position 缩略图所在下标
	 * @param x		缩略图左上角x坐标
     * @param y		缩略图左上角y坐标
     * @return
     */
	public static ImageFragment newInstance(String imgData, int width,
											int hight,int position,int x,int y) {
		final ImageFragment f = new ImageFragment();
		final Bundle args = new Bundle();
		args.putSerializable("imgData", imgData);
		args.putSerializable("width", width);
		args.putSerializable("hight", hight);
		args.putSerializable("position", position);
		args.putSerializable("x", x);
		args.putSerializable("y", y);
		f.setArguments(args);

		return f;
	}



	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		showPhoto();
	}


	@Override
	protected void loadViewLayout(ViewGroup container) {
		rootView = inflater.inflate(R.layout.big_image_item, container, false);
	}
	@Override
	protected void findView() {
		v_parent = rootView.findViewById(R.id.head_big_image);
		photoView = (ImageView) rootView.findViewById(R.id.img_photoview);
		progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar1);

	}



	@Override
	protected void initOther() {

		imgData = getArguments() != null ? getArguments().getString("imgData") : null;
		width = getArguments() != null ? getArguments().getInt("width") : 50;
		hight = getArguments() != null ? getArguments().getInt("hight") : 50;
		position = getArguments() != null ? getArguments().getInt("position") : 0;
		x = getArguments() != null ? getArguments().getInt("x") : SystemConfig.getWidth()/2;
		y = getArguments() != null ? getArguments().getInt("y") : SystemConfig.getHight()/2;

		bitMapHight = hight;
		photoView.setDrawingCacheEnabled(true);
		mAttacher = new PhotoViewAttacher(photoView);

	}

	/**
	 * 展示图片
	 */
	public void showPhoto(){

		Logger.i(Tag, Tag+" showPhoto position:" + position);
		/**
		 * 第一张图未加载出来背景透明
		 */
		if (position == ((ShowPictureActivity)mContext).getPositon() && ((ShowPictureActivity)mContext).isFirst()){
			v_parent.setBackgroundColor(Color.TRANSPARENT);
		}else{
			v_parent.setBackgroundColor(Color.BLACK);
		}

		/**
		 * 设置photo的初始大小
		 */
		photoView.getLayoutParams().width = width;
		photoView.getLayoutParams().height = hight;


		RelativeLayout.LayoutParams progressBarParams = (RelativeLayout.LayoutParams) progressBar.getLayoutParams();
		progressBarParams.width = NormalUtil.dip2px(mContext, 40);
		progressBarParams.height = NormalUtil.dip2px(mContext, 40);
		progressBarParams.topMargin = (SystemConfig.getHight() - progressBarParams.height)/2;
		progressBarParams.leftMargin = (SystemConfig.getWidth()- progressBarParams.width)/2;
		progressBar.setLayoutParams(progressBarParams);
		mAttacher.setScaleType(ScaleType.CENTER_CROP);


		/**
		 * 判断加载大图还是缩略图
		 */
		boolean originalBitmapExist = BitmapUtil.checkImageExist(StringUtil.getOrg(imgData));
		if (originalBitmapExist) {
			RelativeLayout.LayoutParams photoViewParams = (RelativeLayout.LayoutParams) photoView
					.getLayoutParams();
			photoViewParams.topMargin = (int)y;
			photoViewParams.leftMargin = (int)x;
			photoView.setLayoutParams(photoViewParams);
			loadOrgImg(StringUtil.getOrg(imgData),false);

		}else {
			loadThumbImg(StringUtil.getThumb(imgData, width, hight));
		}

	}

	/**
	 * 加载缩略图
	 * @param url
     */
	public void loadThumbImg(final String url) {
		ImageLoader.getInstance().displayImage(url, photoView, new ImageLoadingListener() {
			@Override
			public void onLoadingStarted(String s, View view) {

			}

			@Override
			public void onLoadingFailed(String s, View view, FailReason failReason) {
				photoView.setImageResource(R.color.img_default_gray);
				centerView();
			}

			@Override
			public void onLoadingComplete(String s, View view, Bitmap bitmap) {
				photoView.setImageBitmap(bitmap);

				centerView();

			}

			@Override
			public void onLoadingCancelled(String s, View view) {
				/**
				 * imageLoader 会因 ImageAware is reused for another image 回调一个 onLoadingCancelled
				 * 这里再检测是否已近存在 重新加载
				 */
				if (BitmapUtil.checkImageExist(url) && errorTime < 3) {
					errorTime++;
					loadThumbImg(url);
				}
			}
		});
	}


	/**
	 * 加载原图
	 */
	private int closeScaleType = 1;
	private int errorTime = 0;
	private int bitMapHight;
	public void loadOrgImg(final String url,final boolean is_center) {

		ImageLoader.getInstance().loadImage(url,

				new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String arg0, View arg1) {
						// TODO Auto-generated method stub
						photoView.setVisibility(View.VISIBLE);
						Logger.i(Tag, Tag+" v_parent hight:"+v_parent.getHeight() );
						if (is_center) {
							//photoView.setBackgroundColor(mContext.getResources().getColor(R.color.black));
							progressBar.setProgress(0);
							progressBar.setVisibility(View.VISIBLE);
						}else {
							//photoView.setBackgroundColor(mContext.getResources().getColor(R.color.black));
						}

					}

					@Override
					public void onLoadingFailed(String arg0, View arg1, FailReason failReason) {
						// TODO Auto-generated method stub
						Logger.i(Tag,Tag + " loadImg onLoadingFailed position:" + position);
						Logger.i(Tag,Tag + " loadImg onLoadingFailed" + failReason.toString());
						progressBar.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingComplete(String url, View view, Bitmap bitmap) {
						// TODO Auto-generated method stub

						progressBar.setVisibility(View.GONE);
						/**
                         * 压缩
						 */
						bitmap = BitmapUtil.zoomImage(bitmap,2* SystemConfig.getHight());
						if (bitmap == null ) {
							if (errorTime < 3) {
								errorTime++;
								loadOrgImg(url,is_center);
							}else {
								finishFlash();
							}
							return;
						}
						bitMapHight = bitmap.getHeight() > SystemConfig.getHight()?SystemConfig.getHight():bitmap.getHeight();
						Logger.i(Tag, Tag+" onLoadingComplete position:"+position);
						Logger.i(Tag, Tag+" onLoadingComplete bitmap:"+bitmap.getWidth()+":"+bitmap.getHeight());
						//photoView.setBackgroundColor(mContext.getResources().getColor(R.color.black));

						if (is_center) {//缩略图在中心位置，原图加载出来后要替换缩略图，尺寸比例大多会发生变化（正方形变成矩形），这里要重新设置photo的一些值，使原图也是居中显示
							Point sizeData = new Point();
							sizeData.x = SystemConfig.getWidth();
							sizeData.y = bitMapHight;
							RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) photoView
									.getLayoutParams();
							params.topMargin = SystemConfig.getHight()/2-sizeData.y/2;
							params.height = sizeData.y;
							params.width = sizeData.x;
							params.leftMargin = (int) SystemConfig.getWidth()/2-sizeData.x/2;
						}


						if (SystemConfig.getHight() >= bitmap.getHeight()) {//长图
							closeScaleType = 1;
							mAttacher.setScaleType(ScaleType.CENTER_INSIDE );
						}else {
							closeScaleType = 2;
							mAttacher.setScaleType(ScaleType.CENTER_CROP);
						}

						/*if (scale > 1) {
							mAttacher.setMaxScale(scale*2);
							mAttacher.setMinScale(scale);
							mAttacher.zoomTo(scale, SystemConfig.getWidth()/2, SystemConfig.getHight()/2);
						}*/

						photoView.setImageBitmap(bitmap);

						if (is_center) {//动画从中心开始，获取缩略图当前的左上角坐标
							Logger.i(Tag, Tag+" center");
							int[] location = new int[2];
							photoView.getLocationInWindow(location);
							int x1 = location[0];
							int y1 = location[1];
							fullScreen(x1,y1,true);

						}else {//动画从默认位置开始
							Logger.i(Tag, Tag+" not center");
							fullScreen(x,y,false);
						}
					}

					@Override
					public void onLoadingCancelled(String url, View view) {
						// TODO Auto-generated method stub


						Logger.i(Tag, Tag+" onLoadingCancelled position:"+position);

						/**
						 * imageLoader 会因 ImageAware is reused for another image 回调一个 onLoadingCancelled
						 * 这里再检测是否已近存在 重新加载
						 */
						if (BitmapUtil.checkImageExist(url) && errorTime < 3){
							errorTime++;
							loadOrgImg(url,is_center);
						}else{
							progressBar.setVisibility(View.GONE);
						}

					}
				});


	}

	/**
	 * 第一次加载背景透明度渐变
	 */
	public  void setBgAlphaAnimation(){
		if (position == ((ShowPictureActivity)mContext).getPositon() && ((ShowPictureActivity)mContext).isFirst()){
			((ShowPictureActivity)mContext).setFirst(false);
			v_parent.setBackgroundColor(Color.BLACK);
			bgAlphaAnimation = new AlphaAnimation(0, (float) 1);
			bgAlphaAnimation.setDuration(NORMAL_SCALE_DURATION);
			bgAlphaAnimation.setFillAfter(true);
			v_parent.startAnimation(bgAlphaAnimation);

		}
	}


	/**
	 * 缩略图从默认位置到中心位置的动画
	 * 通过改变photoview 的 topMargin ，leftMargin
	 */
	public void centerView(){

		//第一张看到的图且第一次加载的动画
		if (((ShowPictureActivity)mContext).isFirst() && ((ShowPictureActivity)mContext).getPositon() == position) {
			setBgAlphaAnimation();
			valueAnimator = ValueAnimator.ofFloat(0,100);
			valueAnimator.addUpdateListener(new AnimatorUpdateListener() {
				//持有一个IntEvaluator对象，方便下面估值的时候使用
				private IntEvaluator mEvaluator = new IntEvaluator();
				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					float currentValue = (float) animation.getAnimatedValue();
					//计算当前进度占整个动画过程的比例，浮点型，0-1之间
					float fraction = currentValue / 100f;
					RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) photoView.getLayoutParams();

					/**
					 * 估算当前值
					 */
					params.topMargin = mEvaluator.evaluate(fraction, y, SystemConfig.getHight()/2-hight/2);//从当前y到居中
					params.leftMargin = mEvaluator.evaluate(fraction, x, SystemConfig.getWidth()/2-width/2);//从当前x到居中
					photoView.setLayoutParams(params);


				}
			});
			valueAnimator.setDuration((long) (NORMAL_SCALE_DURATION));
			valueAnimator.addListener(new Animator.AnimatorListener() {
				@Override
				public void onAnimationStart(Animator animator) {

				}

				@Override
				public void onAnimationEnd(Animator animator) {
					errorTime = 0;
					/**
					 * 居中动画结束，加载原图
					 */
					loadOrgImg(StringUtil.getOrg(imgData),true);
				}

				@Override
				public void onAnimationCancel(Animator animator) {

				}

				@Override
				public void onAnimationRepeat(Animator animator) {

				}
			});
			valueAnimator.start();


		}else {//不是第一张或第一次，直接显示居中位置
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) photoView
					.getLayoutParams();
			params.topMargin = (int) SystemConfig.getHight()/2-hight/2;
			params.leftMargin = (int) SystemConfig.getWidth()/2-width/2;
			photoView.setLayoutParams(params);
			errorTime = 0;
			loadOrgImg(StringUtil.getOrg(imgData),true);

		}
	}

	/**
	 * photoview当前位置放大到全屏的动画
	 * 通过改变photoview 的 topMargin ，leftMargin，height，width
	 * @param x   photoview的左上角x坐标
	 * @param y   photoview的左上角y坐标
	 * @param from_center 是否从中心来的
     */
	public void fullScreen(final int x,final int y,boolean from_center){//int x, int y,int width,int hight

		Logger.i(Tag, Tag+"fullScreenrx:"+x+"y:"+y);

		if (((ShowPictureActivity)mContext).isFirst() && ((ShowPictureActivity)mContext).getPositon() == position
				|| (from_center && ((ShowPictureActivity)mContext).getCurrentPosition() == position)) {//第一张要显示的图或者居中的且是当前看到的位置
			setBgAlphaAnimation();
			valueAnimator = ValueAnimator.ofFloat(0,100);
			valueAnimator.addUpdateListener(new AnimatorUpdateListener() {
				//持有一个IntEvaluator对象，方便下面估值的时候使用
				private IntEvaluator mEvaluator = new IntEvaluator();
				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					float currentValue = (float) animation.getAnimatedValue();
					//计算当前进度占整个动画过程的比例，浮点型，0-1之间
					float fraction = currentValue / 100f;
					RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) photoView.getLayoutParams();
					params.height = mEvaluator.evaluate(fraction, hight, SystemConfig.getHight());//从当前缩略图的高度到满屏
					params.width = mEvaluator.evaluate(fraction, width, SystemConfig.getWidth());//从当前缩略图的宽度到满屏
					params.topMargin = mEvaluator.evaluate(fraction, y, 0);//从photoview的y到0
					params.leftMargin = mEvaluator.evaluate(fraction, x, 0);//从photoview的x到0
					photoView.setLayoutParams(params);


				}
			});
			valueAnimator.setDuration((long) (NORMAL_SCALE_DURATION));
			valueAnimator.start();

		}else {
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) photoView
					.getLayoutParams();
			params.leftMargin = (int) 0;
			params.topMargin = (int) 0;
			params.height = (int) SystemConfig.getHight();
			params.width = (int) SystemConfig.getWidth();
			photoView.setLayoutParams(params);

		}

	}


	/**
	 * 关闭动画
	 */
	public void close(){
		/**
		 * 背景透明
		 */
		v_parent.setBackgroundColor(Color.TRANSPARENT);
		photoView.setBackgroundColor(Color.TRANSPARENT);
		mAttacher.setScaleType(ScaleType.CENTER_CROP);
		/*if (closeScaleType == 1) {
			mAttacher.setScaleType(ScaleType.CENTER_INSIDE);
		}else {
			mAttacher.setScaleType(ScaleType.CENTER_CROP);
		}*/
		final int closeHight = bitMapHight;//结束时的高度以photoView 加载的bitmap高度为准
		final int closeMarginTop = (SystemConfig.getHight() - bitMapHight)/2;

		valueAnimator = ValueAnimator.ofFloat(0,100);
		valueAnimator.addUpdateListener(new AnimatorUpdateListener() {
			//持有一个IntEvaluator对象，方便下面估值的时候使用
			private IntEvaluator mEvaluator = new IntEvaluator();
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				float currentValue = (float) animation.getAnimatedValue();
				//计算当前进度占整个动画过程的比例，浮点型，0-1之间
				float fraction = currentValue / 100f;
				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) photoView.getLayoutParams();
				params.height = mEvaluator.evaluate(fraction, closeHight, hight);//从当前满屏到缩略图的高度
				params.width = mEvaluator.evaluate(fraction, SystemConfig.getWidth(), width);//从当前满屏到缩略图的宽度
				params.topMargin = mEvaluator.evaluate(fraction, closeMarginTop, y);//从bitmap的左上角y坐标到缩略图的y
				params.leftMargin = mEvaluator.evaluate(fraction, 0, x);//从bitmap的左上角x坐标到缩略图的x
				photoView.setLayoutParams(params);


			}
		});
		valueAnimator.setDuration((long) (NORMAL_SCALE_DURATION));
		valueAnimator.addListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animator) {

			}

			@Override
			public void onAnimationEnd(Animator animator) {
				((ShowPictureActivity)mContext).finishAct();
			}

			@Override
			public void onAnimationCancel(Animator animator) {

			}

			@Override
			public void onAnimationRepeat(Animator animator) {

			}
		});

		valueAnimator.start();


	}

	@Override
	protected void setListener() {
		// TODO Auto-generated method stub
		mAttacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {

			@Override
			public void onPhotoTap(View view, float x, float y) {
				// TODO Auto-generated method stub
				finishFlash();
			}
		});
		mAttacher.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {

			@Override
			public void onViewTap(View view, float x, float y) {
				// TODO Auto-generated method stub
				finishFlash();
			}
		});

		//长按保存
		mAttacher.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				new Handler().post(new Runnable() {

					@Override
					public void run() {
						Logger.i(Tag,Tag + " onLongClick");
					}
				});
				return true;
			}
		});
	}

	public void finishFlash(){
		((ShowPictureActivity)mContext).setFinish(true);
		if (((ShowPictureActivity)mContext).isAnimation() || position == ((ShowPictureActivity)mContext).getPositon()) {
			close();
		}else{
			((ShowPictureActivity)mContext).finishAct();
		}
	}



}
