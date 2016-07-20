package com.moxiuyuer.smartphoto.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.moxiuyuer.smartphoto.R;
import com.moxiuyuer.smartphoto.bean.ImageBean;
import com.moxiuyuer.smartphoto.utils.BitmapUtils;
import com.moxiuyuer.smartphoto.utils.NativeImageLoader;
import com.moxiuyuer.smartphoto.utils.NativeImageLoader.NativeImageCallBack;
import com.moxiuyuer.smartphoto.view.MyScaleImageView;
import com.moxiuyuer.smartphoto.view.MyScaleImageView.OnMeasureListener;

/**
 * 以GridView显示的图片文件夹的adapter
 * 描述：
 *
 * @author yuerting
 *  2016-7-14  - 下午12:01:36
 */
public class GroupImageAdapter extends BaseAdapter{
	private List<ImageBean> list;
	private Point mPoint = new Point(0, 0);//用来封装ImageView的宽和高的对象
	private GridView mGridView;
	protected LayoutInflater mInflater;
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}


	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public GroupImageAdapter(Context context, List<ImageBean> list, GridView mGridView){
		this.list = list;
		this.mGridView = mGridView;
		mInflater = LayoutInflater.from(context);
	}
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		ImageBean mImageBean = list.get(position);
		String path = mImageBean.getTopImagePath();
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_grid_group, null);
			viewHolder.mImageView = (MyScaleImageView) convertView.findViewById(R.id.group_image);
			viewHolder.mTextViewTitle = (TextView) convertView.findViewById(R.id.group_title);
			viewHolder.mTextViewCounts = (TextView) convertView.findViewById(R.id.group_count);
			
			//用来监听ImageView的宽和高
			viewHolder.mImageView.setOnMeasureListener(new OnMeasureListener() {
				
				@Override
				public void onMeasureSize(int width, int height) {
					mPoint.set(width, height);
//					System.out.println(mPoint.x+ "=========" + mPoint.y);
				}
			});
			
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
			viewHolder.mImageView.setImageResource(R.drawable.mi_friends_sends_pictures_no);
		}
		
		viewHolder.mTextViewTitle.setText(mImageBean.getFolderName());
		viewHolder.mTextViewCounts.setText(Integer.toString(mImageBean.getImageCounts()));
		//给ImageView设置路径Tag,这是异步加载图片的小技巧
		viewHolder.mImageView.setTag(path);
		
		
		//利用NativeImageLoader类加载本地图片，将图片缩放到适合给定区域的最大程度
		Bitmap bitmap = NativeImageLoader.getInstance().loadNativeImage(path, mPoint, new NativeImageCallBack() {
			
			@Override
			public void onImageLoader(Bitmap bitmap, String path) {
				ImageView mImageView = (ImageView) mGridView.findViewWithTag(path);
				if(bitmap != null && mImageView != null){
					//首次加载，这里回到函数返回bitmap，而这个方法loadNativeImage返回null
					//裁剪获得缩略图
					bitmap = BitmapUtils.centerSquareScaleBitmap(bitmap, 260, 200);
				mImageView.setImageBitmap(bitmap);
				}
			}
		});
		
		//首次加载如果没有加载到图片，NativeImageLoader返回值为null;
		//下次复用时就有图片了
		if(bitmap != null){
			//对于gridview 的item复用
			bitmap = BitmapUtils.centerSquareScaleBitmap(bitmap, 260, 200);
			viewHolder.mImageView.setImageBitmap(bitmap);
		}else{
			viewHolder.mImageView.setImageResource(R.drawable.mi_friends_sends_pictures_no);
		}
		return convertView;
	}
	
	
	public static class ViewHolder{
		public MyScaleImageView mImageView;
		public TextView mTextViewTitle;
		public TextView mTextViewCounts;
	}
}
