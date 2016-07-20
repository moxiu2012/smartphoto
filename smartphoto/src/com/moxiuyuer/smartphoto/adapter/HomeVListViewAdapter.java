package com.moxiuyuer.smartphoto.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.moxiuyuer.smartphoto.R;
import com.moxiuyuer.smartphoto.activity.PlayActivity;
import com.moxiuyuer.smartphoto.activity.VideoPlayActivity;
import com.moxiuyuer.smartphoto.bean.SmartPhotoInfo;
import com.moxiuyuer.smartphoto.utils.BitmapUtils;

public class HomeVListViewAdapter extends MyBaseAdapter{

	//电子相册文件信息
	private List<SmartPhotoInfo> infoList;
	
	public HomeVListViewAdapter(Context context, List<SmartPhotoInfo> infoList) {
		super(context);
		this.infoList = infoList;
	}

	@Override
	public int getCount() {
		return infoList.size();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = null;
		ViewHolder holder;
		
		if (convertView == null) {
			view = mInflater.inflate(R.layout.item_vlv_home, null);
			
			holder = new ViewHolder();
			holder.iv = (ImageView) view.findViewById(R.id.iv_home_listview_v);
			holder.title = (TextView) view.findViewById(R.id.tv_home_title);
			holder.desc = (TextView) view.findViewById(R.id.tv_home_desc);
			holder.ivVideoPlay = (ImageView) view.findViewById(R.id.iv_home_videoplay);
			view.setTag(holder);
			
		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}
		SmartPhotoInfo info = infoList.get(position);
		holder.iv.setImageBitmap(BitmapUtils.byte2Bitmap(info.getImageByte()));
		holder.title.setText(info.getName());
		holder.desc.setText(info.getDesc());
		
		/**
		 * 设置图片的点击事件
		 */
		holder.ivVideoPlay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				//开启视频展示页面
				Intent intent = new Intent(context, PlayActivity.class);
				intent.putExtra("path", infoList.get(position).getPath());
				intent.putExtra("isAutoPlay", true);
				context.startActivity(intent);
			}
		});
		
		return view;
	}
	
	/**
	 * 组件的容器
	 * 描述：
	 * @author moxiuyuer
	 * 2016-5-20 下午4:09:10
	 */
	class ViewHolder { 
		ImageView iv;
		TextView title;
		TextView desc;
		ImageView ivVideoPlay;
	}
}
