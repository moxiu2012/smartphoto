package com.moxiuyuer.smartphoto.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.moxiuyuer.smartphoto.R;
import com.moxiuyuer.smartphoto.utils.BitmapUtils;
import com.moxiuyuer.smartphoto.utils.HorizontialListView;
import com.moxiuyuer.smartphoto.utils.NativeImageLoader;
import com.moxiuyuer.smartphoto.utils.NativeImageLoader.NativeImageCallBack;

/**
 * 新建相册里面的横向ListView的adaper 描述：显示布局方式和图片
 * 
 * @author moxiuyuer 2016-6-7 下午9:18:59
 */
public class NewHListViewAdapter1 extends MyBaseAdapter {

	// 图片的路径列表
	private List<String> list;
	private HorizontialListView mlistView;
	// 保存选择的图片的位图
	private List<Bitmap> selectedBitmapList = new ArrayList<Bitmap>();
	// 选中的项目集合
	private List<Integer> itemList = new ArrayList<Integer>();
	
	public List<Integer> getItemList() {
		return itemList;
	}

	public List<Bitmap> getSelectedBitmapList() {
		return selectedBitmapList;
	}

	public NewHListViewAdapter1(Context context, List<String> list,
			HorizontialListView mlistView) {
		super(context);
		this.list = list;
		this.mlistView = mlistView;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final int p = position;
		ViewHolder holder;
		String path = list.get(position);

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_hlv_new, null);

			holder = new ViewHolder();
			holder.mImageView = (ImageView) convertView
					.findViewById(R.id.iv_new_listview_h);
			holder.mRelativeLayout = (RelativeLayout) convertView
					.findViewById(R.id.rl_new);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
			holder.mImageView.setImageResource(R.drawable.mi_img_no);
		}
		// 将ImageView的tag设置为图片的路径
		holder.mImageView.setTag(path);

		holder.mImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if(!itemList.contains(p)) {
					itemList.add(p);
					notifyDataSetChanged();
					
//					Message msg = Message.obtain();
//					msg.what = ConstantValues.SELECTED;
//					msg.obj = p;
					
				} 
			}
		});
		
		//设置选中item的背景颜色
		if(itemList.contains(position)) {
			holder.mRelativeLayout.setBackgroundColor(Color.RED);
		} else {
			holder.mRelativeLayout.setBackgroundColor(Color.GREEN);
		}
		
		// 利用NativeImageLoader类加载本地图片
		Bitmap bitmap = NativeImageLoader.getInstance().loadNativeImage(path,
				null, new NativeImageCallBack() {

					@Override
					public void onImageLoader(Bitmap bitmap, String path) {

						// 保存bitmap至selectedBitmapList
						selectedBitmapList.add(bitmap);

						// 找到对应的Image
						ImageView mImageView = (ImageView) mlistView
								.findViewWithTag(path);
						if (bitmap != null && mImageView != null) {
							// 获取对应的缩略图
							bitmap = BitmapUtils.centerSquareScaleBitmap(
									bitmap, 100, 100);
							mImageView.setImageBitmap(bitmap);
						}
					}
				});

		if (bitmap != null) {
			bitmap = BitmapUtils.centerSquareScaleBitmap(bitmap, 100, 100);
			holder.mImageView.setImageBitmap(bitmap);
		} else {
			holder.mImageView.setImageResource(R.drawable.mi_img_no);
		}

		return convertView;
	}

	/**
	 * view的容器
	 * 
	 * @author yuerting
	 * 
	 */
	class ViewHolder {
		public ImageView mImageView;
		public RelativeLayout mRelativeLayout;
	}

}
