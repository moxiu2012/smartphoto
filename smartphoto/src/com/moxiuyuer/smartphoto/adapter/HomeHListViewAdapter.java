package com.moxiuyuer.smartphoto.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.moxiuyuer.smartphoto.R;
import com.moxiuyuer.smartphoto.bean.SmartPhotoInfo;
import com.moxiuyuer.smartphoto.utils.BitmapUtils;

/**
 * 主页中实例展示的横向ListView的adapter 
 * 描述：
 *
 * @author yuerting
 *  2016-7-1  - 下午10:42:59
 */
public class HomeHListViewAdapter extends MyBaseAdapter {

	private List<SmartPhotoInfo> infoList;
	
	public HomeHListViewAdapter(Context context, List<SmartPhotoInfo> infoList) {
		super(context);
		this.infoList = infoList;
	}

	@Override
	public int getCount() {
		return infoList.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		ViewHolder holder;
		if (convertView == null) {
			view = mInflater.inflate(R.layout.item_hlv_home, null);
			holder = new ViewHolder();
			holder.iv = (ImageView) view.findViewById(R.id.iv_home_listview_h);
			holder.tvTitle = (TextView) view.findViewById(R.id.tv_home_title_h);
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}
		Bitmap bitmap = BitmapUtils.byte2Bitmap(infoList.get(position).getImageByte());
		bitmap = BitmapUtils.centerSquareScaleBitmap(bitmap, 200, 200);
		
		holder.iv.setImageBitmap(bitmap);
		holder.tvTitle.setText(infoList.get(position).getName());
		return view;
	}
	
	class ViewHolder { 
		ImageView iv;
		TextView tvTitle;
	}
}
