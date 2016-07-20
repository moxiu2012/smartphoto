package com.moxiuyuer.smartphoto.adapter;

import java.util.List;

import com.moxiuyuer.smartphoto.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class PlaySPAdapter extends MyBaseAdapter{

	private List<Bitmap> bitmapList;
	
	private Context context;
	
	public PlaySPAdapter(Context context, List<Bitmap> bitmapList)  {
		super(context);
		this.context = context;
		this.bitmapList = bitmapList;
		
		if(bitmapList.size()%2 == 1) {
			//如果bitmapList的长度是奇数
			bitmapList.add(bitmapList.get(0));
		}
		
	}

	@Override
	public int getCount() {
		return bitmapList.size()/2;  //一次两页
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		
		if(convertView == null) {
			convertView = mInflater.inflate(R.layout.item_pw_play, null);
			holder = new ViewHolder();
			holder.ivLeft =  (ImageView) convertView.findViewById(R.id.iv_play_left);
			holder.ivRight =  (ImageView) convertView.findViewById(R.id.iv_play_right);
			holder.tvLeft = (TextView)convertView.findViewById(R.id.tv_play_left);
			holder.tvRight = (TextView)convertView.findViewById(R.id.tv_play_right);
			
			convertView.setTag(holder);
		}  else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		
		holder.ivLeft.setImageBitmap(bitmapList.get(position*2));
		holder.ivRight.setImageBitmap(bitmapList.get(position*2+1));
		holder.tvLeft.setText(position*2 + 1 + "");
		holder.tvRight.setText(position*2 + 2 + "");
		 
		if(position == bitmapList.size() -1) {
			Toast.makeText(context, "已经是最后一页了", 0).show();
		}
		
		return convertView;
	}
	
	
	class ViewHolder {
		ImageView ivLeft;
		ImageView ivRight;
		TextView tvLeft;
		TextView tvRight;
		
	}

}
