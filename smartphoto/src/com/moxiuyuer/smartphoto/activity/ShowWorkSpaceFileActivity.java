package com.moxiuyuer.smartphoto.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.moxiuyuer.smartphoto.R;
import com.moxiuyuer.smartphoto.adapter.MyBaseAdapter;
import com.moxiuyuer.smartphoto.bean.WorkSpaceInfo;
import com.moxiuyuer.smartphoto.utils.BitmapUtils;

/**
 * 展示工作文件的activity 描述： 选择一起输出电子相册时显示
 * 
 * @author yuerting 2016-7-14 - 下午5:28:14
 */
public class ShowWorkSpaceFileActivity extends Activity {

	private ArrayList<WorkSpaceInfo> fileInfoList;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.ml_activity_show_ws_file);

		context = ShowWorkSpaceFileActivity.this;

		Intent intent = getIntent();
		fileInfoList = (ArrayList<WorkSpaceInfo>) intent.getSerializableExtra("fileInfoList");

		ListView lvShowWSFile = (ListView) findViewById(R.id.lv_show_ws_file);
		lvShowWSFile.setAdapter(new MyBaseAdapter(context) {
			@Override
			public int getCount() {
				return fileInfoList.size();
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = null;
				ViewHolder holder;

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
				holder.llEdit.setVisibility(View.INVISIBLE);

				return view;
			}
		});

		lvShowWSFile.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String filePath = fileInfoList.get(position).getFilePath();
				Intent data = new Intent();
				data.putExtra("filePath", filePath);
				setResult(0, data);

				finish();
			}
		});
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
		//编辑
		LinearLayout llEdit;
	}
	
	
	@Override
	public void onBackPressed() {
		Intent data = new Intent();
		data.putExtra("filePath", "");
		setResult(0, data);

		super.onBackPressed();
	}

}
