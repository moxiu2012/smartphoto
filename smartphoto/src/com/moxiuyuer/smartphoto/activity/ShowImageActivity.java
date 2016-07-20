package com.moxiuyuer.smartphoto.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.moxiuyuer.smartphoto.R;
import com.moxiuyuer.smartphoto.adapter.ChildImageAdapter;
import com.moxiuyuer.smartphoto.utils.SelectedImagePath;

/**
 * 以列表显示每个文件夹图片activity
 * 描述：
 *
 * @author yuerting
 *  2016-7-14  - 上午10:36:38
 */
public class ShowImageActivity extends Activity implements OnClickListener {

	private GridView mGridView;
	private List<String> list;
	private ChildImageAdapter adapter;
	private TextView tvSelectedNum;
	private Button btnSelectedAll;
	private Button btnSelectedCancel;
	/**
	 * 用来存储图片的选中情况
	 */
	private HashMap<Integer, Boolean> mSelectMap;
	private int checkNum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ml_activity_show_image);

		mGridView = (GridView) findViewById(R.id.child_grid);
		tvSelectedNum = (TextView) findViewById(R.id.tv_selected_num);
		btnSelectedAll = (Button) findViewById(R.id.btn_selected_all);
		btnSelectedCancel = (Button) findViewById(R.id.btn_selected_cancel);

		btnSelectedAll.setOnClickListener(this);
		btnSelectedCancel.setOnClickListener(this);

		list = getIntent().getStringArrayListExtra("data");

		// 初始化图片的选中状态
		initSelectMap();

		adapter = new ChildImageAdapter(this, list, mGridView, mSelectMap,tvSelectedNum);
		mGridView.setAdapter(adapter);
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
					Intent intent = new Intent(ShowImageActivity.this, ShowItemImageActivity.class);
					intent.putExtra("path", list.get(position));
					ShowImageActivity.this.startActivity(intent);// 不能使用getApplicationContext
				}
		});
		
 		
	}

	/**
	 * 初始化图片的选择状态
	 */
	private void initSelectMap() {
		List<String> selectList = SelectedImagePath. getInstance().getImagePathList();
		mSelectMap = new HashMap<Integer, Boolean>();
		for (int i = 0; i < list.size(); i++) {
			if(selectList.contains(list.get(i))) {
				mSelectMap.put(i, true);
				//清除上次的记录
				selectList.remove(list.get(i));
			} else  {
				mSelectMap.put(i, false);
			}
		}
	}

	@Override
	public void onBackPressed() {
		List<String> selectedPathList = new ArrayList<String>();
		List<Integer> selectedList = adapter.getSelectItems();
		for (Integer integer : selectedList) {
			String path = list.get(integer);
			if (!selectedPathList.contains(path)) {
				selectedPathList.add(path);
			}
		}

		Intent data = getIntent();
		data.putStringArrayListExtra("selectedPathList", (ArrayList<String>) selectedPathList);
		this.setResult(0, data);

		super.onBackPressed();
	}


	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.btn_selected_all) {

			// 遍历list的长度，将MyAdapter中的map值全部设为true
			for (int i = 0; i < list.size(); i++) {
				mSelectMap.put(i, true);
			}
			
			adapter.setmSelectMap(mSelectMap);
			// 通知listView刷新
			adapter.notifyDataSetChanged();
		} else if (view.getId() == R.id.btn_selected_cancel) {

			// 遍历list的长度，将已选的按钮设为未选
			for (int i = 0; i < list.size(); i++) {
				if (mSelectMap.get(i)) {
					mSelectMap.put(i, false);
				}
			}
			
			adapter.setmSelectMap(mSelectMap);
			// 通知listView刷新
			adapter.notifyDataSetChanged();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
