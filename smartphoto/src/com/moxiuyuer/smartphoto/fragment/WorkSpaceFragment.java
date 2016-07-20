package com.moxiuyuer.smartphoto.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.moxiuyuer.smartphoto.R;
import com.moxiuyuer.smartphoto.activity.EditActivity;
import com.moxiuyuer.smartphoto.activity.PhotoActivity;
import com.moxiuyuer.smartphoto.activity.ThemeActivity;
import com.moxiuyuer.smartphoto.activity.WorkSpaceActivity;
import com.moxiuyuer.smartphoto.adapter.WorkSpaceListViewAdapter;
import com.moxiuyuer.smartphoto.bean.WorkSpaceInfo;
import com.moxiuyuer.smartphoto.utils.SerializableUtils;

/**
 * 创作的选项卡
 * 描述：
 *
 * @author yuerting
 *  2016-7-14  - 上午10:41:53
 */
public class WorkSpaceFragment extends Fragment implements OnClickListener {

	protected static final int FINISH = 0;

	private Context context;

	protected View view;

	// 文件信息的集合
	private List<WorkSpaceInfo> fileInfoList;

	// 工作室文件集合
	private List<String> pathList;

	// 选中的工作室路径
	private int selectedWSNo = -1;

	private WorkSpaceListViewAdapter adapter;

	private ImageView wsBg;

	public WorkSpaceFragment(Context context, List<String> pathList) {
		this.context = context;
		this.pathList = pathList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup,
	 * android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.ml_fragment_workspace, container, false);

		
		// 初始化控件
		initView(view);

		/**
		 * 由于异步加载，先设listview 的数据，初始数据 当异步加载完成后更新ListView数据
		 */
		fileInfoList = new ArrayList<WorkSpaceInfo>();

		// 初始化数据，查找工作室文件.uer，开启子线程工作
		getData(pathList);

		// 初始ListView
		initListView();

		return view;
	}

	/**
	 * 初始化布局
	 */
	private void initView(View view) {
		wsBg = (ImageView) view.findViewById(R.id.iv_ws_bg);
		wsBg.setOnClickListener(this);

		ImageView ivPhoto = (ImageView) view.findViewById(R.id.iv_add_photo);
		ImageView ivTheme = (ImageView) view.findViewById(R.id.iv_add_theme);
		ImageView ivWorkspace = (ImageView) view.findViewById(R.id.iv_add_ws);

		ivPhoto.setOnClickListener(this);
		ivTheme.setOnClickListener(this);
		ivWorkspace.setOnClickListener(this);
	}

	/**
	 * 初始化数据，获取工作室文件.uer
	 */
	private void getData(List<String> pathList) {
		if (pathList != null && pathList.size() > 0) {
			// 清空旧列表
			if (fileInfoList != null) {
				fileInfoList.clear();
			}

			File file = null;
			// 添加新内容
			for (String path : pathList) {
				file = new File(path);
				WorkSpaceInfo mWorkSpaceInfo = (WorkSpaceInfo) SerializableUtils.file2FirstObject(file);
				// 为保证文件和路径排序一致，将路径存入文件
				mWorkSpaceInfo.setFilePath(path);
				fileInfoList.add(mWorkSpaceInfo);
			}
			// 排序，只能按升序排列
			Collections.sort(fileInfoList);
			// 反序
			Collections.reverse(fileInfoList);
		}
	}

	/**
	 * 初始化ListView控件，设置adapter
	 */
	private void initListView() {
		// 更改背景可视性
		changeBg();

		// 工作空间
		ListView vlv = (ListView) view.findViewById(R.id.list_view_v);
		adapter = new WorkSpaceListViewAdapter(context, fileInfoList);
		vlv.setAdapter(adapter);

		vlv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String path = fileInfoList.get(position).getFilePath();

				if (new File(path).exists()) {
					Intent intent = new Intent(context, WorkSpaceActivity.class);
					// 文件存在，则恢复工作室
					selectedWSNo = position;
					intent.putExtra("workSpacePath", path);
					startActivityForResult(intent, 0);
				} else {
					// 如果文件不存在， 则toast提示，文件已被移除, 并将信息从ListView中移除
					Toast.makeText(context, "文件不存在", 0).show();
					fileInfoList.remove(position);
					adapter.notifyDataSetChanged();
				}
			}
		});

		// 长按删除
		vlv.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

				// 提示
				showDialog(position);

				return true;
			}
		});
	}

	/**
	 * 提示刪除
	 * 
	 * @param p
	 */
	private void showDialog(final int p) {
		AlertDialog.Builder builder = new Builder(context);
		builder.setTitle("提示");
		builder.setMessage("		确定要删除吗？");

		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String path = fileInfoList.get(p).getFilePath();

				// 删除文件
				File file = new File(path);
				if (file.exists()) {
					file.delete();
				}
				// 更新显示
				fileInfoList.remove(p);
				adapter.notifyDataSetChanged();
			}
		});

		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// dialog.dismiss();
			}
		});

		builder.show();
	}

	/**
	 * 更改背景可视性
	 */
	private void changeBg() {
		// 是否有数据, 有数据则隐藏背景，没数据则显示背景
		if (fileInfoList != null && fileInfoList.size() > 0) {
			wsBg.setVisibility(View.GONE);
		} else {
			wsBg.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onResume() {
		// 编辑时返回数据
		WorkSpaceInfo mWorkSpaceInfo = EditActivity.mWorkSpaceInfo;
		if (mWorkSpaceInfo != null) {
			int selectedPosition = adapter.getSelectedPosition();
			// 更新数据
			fileInfoList.set(selectedPosition, mWorkSpaceInfo);

			// 更改背景可视性
			changeBg();

			adapter.notifyDataSetChanged();
			// mWorkSpaceInfo 置空
			EditActivity.mWorkSpaceInfo = null;
		}

		// System.out.println("mWorkSpaceInfo="+mWorkSpaceInfo);

		// 一定不能删
		super.onResume();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.iv_add_photo:
		case R.id.iv_ws_bg:
			Intent intent1 = new Intent(context, PhotoActivity.class);
			context.startActivity(intent1);
			break;
		case R.id.iv_add_theme:
			Intent intent2 = new Intent(context, ThemeActivity.class);
			context.startActivity(intent2);
			break;
		case R.id.iv_add_ws:
			Intent intent3 = new Intent(context, WorkSpaceActivity.class);
			startActivityForResult(intent3, 1);
			break;

		default:
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == 0) {
			String resultPath = data.getStringExtra("workSpacePath");
			if (resultPath != null) {
				if (selectedWSNo != -1) {
					// 清除fileInfoList中编辑之前的显示信息
					fileInfoList.remove(selectedWSNo);
					selectedWSNo = -1;
				}
				// 将新生成的文件置顶
				WorkSpaceInfo info = (WorkSpaceInfo) SerializableUtils.file2FirstObject(new File(resultPath));
				info.setFilePath(resultPath);
				fileInfoList.add(0, info);

				// 更改背景可视性
				changeBg();

				// 通知更新列表
				adapter.notifyDataSetChanged();
			}
		}

	}

}