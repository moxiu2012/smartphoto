package com.moxiuyuer.smartphoto.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moxiuyuer.smartphoto.R;
import com.moxiuyuer.smartphoto.activity.EditActivity;
import com.moxiuyuer.smartphoto.bean.WorkSpaceInfo;
import com.moxiuyuer.smartphoto.utils.BitmapUtils;

public class WorkSpaceListViewAdapter extends MyBaseAdapter {


	private List<WorkSpaceInfo> fileInfoList;
	
	private int selectedPosition ;

	public int getSelectedPosition() {
		return selectedPosition;
	}

	public void setSelectedPosition(int selectedPosition) {
		this.selectedPosition = selectedPosition;
	}


	public WorkSpaceListViewAdapter(Context context, List<WorkSpaceInfo> fileInfoList) {
		//先调用父类构造方法
		super(context);
		
		this.fileInfoList = fileInfoList;
	}
	

	@Override
	public int getCount() {
		return fileInfoList.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		View view = null;
		ViewHolder holder;
		final int p = position;
		
		if (convertView == null) {
			view = mInflater.inflate(R.layout.item_lv_workspace, null);

			holder = new ViewHolder();
			holder.iv = (ImageView) view.findViewById(R.id.iv_ws_listview);
			holder.title = (TextView) view.findViewById(R.id.tv_ws_title);
			holder.time = (TextView) view.findViewById(R.id.tv_ws_time);
			holder.desc = (TextView) view.findViewById(R.id.tv_ws_desc);
			holder.llEdit = (LinearLayout) view.findViewById(R.id.ll_ws_edit);
			
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}
		
		WorkSpaceInfo info = fileInfoList.get(position);
		
		holder.iv.setImageBitmap(BitmapUtils.byte2Bitmap(info.getCoverImage()));
		holder.title.setText(info.getName());
		String modifyTime = info.getModifyTime();
		holder.time.setText(modifyTime.substring(0, modifyTime.lastIndexOf(":")));
		holder.desc.setText(info.getDesc());
		
		holder.llEdit.setTag(position);
		
		final WorkSpaceInfo mWorkSpaceInfo = info;
		holder.llEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//设置已选位置
				setSelectedPosition(p);
				
				Intent intent = new Intent(context, EditActivity.class);
				intent.putExtra("mWorkSpaceInfo", mWorkSpaceInfo);
				intent.putExtra("fileInfoList", (ArrayList<WorkSpaceInfo>)fileInfoList);
				context.startActivity(intent);
			}
		});
		
		return view;
	}

	
	class ViewHolder {
		// 封面图片
		ImageView iv;
		// 相册标题
		TextView title;
		// 最后编辑时间
		TextView time;
		// 描述
		TextView desc;
		
		//编辑入口
		LinearLayout  llEdit;
	}
}
