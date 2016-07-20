package com.moxiuyuer.smartphoto.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.moxiuyuer.smartphoto.R;
import com.moxiuyuer.smartphoto.activity.PlayActivity;
import com.moxiuyuer.smartphoto.adapter.HomeHListViewAdapter;
import com.moxiuyuer.smartphoto.adapter.HomeVListViewAdapter;
import com.moxiuyuer.smartphoto.bean.SmartPhotoInfo;
import com.moxiuyuer.smartphoto.utils.HorizontialListView;
import com.moxiuyuer.smartphoto.utils.ScanFileUtils;
import com.moxiuyuer.smartphoto.utils.SerializableUtils;

/**
 * 首页的选项卡 描述：
 * 
 * @author yuerting 2016-7-14 - 上午10:41:24
 */
public class HomeFragment extends Fragment {

	protected static final int FINISH = 0;

	private Context context;

	// 本地电子相册文件信息
	private List<SmartPhotoInfo> localInfoList = new ArrayList<SmartPhotoInfo>();

	// 实例展示文件
	private List<SmartPhotoInfo> exampleInfoList = new ArrayList<SmartPhotoInfo>();

	// 电子相册文件路径
	private List<String> expPathList;
	// 电子相册文件路径
	private List<String> spPathList;

	// 实例展示的背景，没有时显示
	private ImageView exampleBg;

	// 本地电子相册列表的背景，没有时显示
	private ImageView localBg;

	private HomeVListViewAdapter adapter;

	private ProgressDialog mProgressDialog;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == FINISH) {
				// 更新列表
				adapter.notifyDataSetChanged();
				mProgressDialog.dismiss();
			}
		};
	};

	private HomeHListViewAdapter exampleAdapter;

	public HomeFragment(Context context, List<String> expPathList, List<String> spPathList) {
		this.context = context;
		this.expPathList = expPathList;
		this.spPathList = spPathList;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.ml_fragment_home, container, false);

		exampleBg = (ImageView) view.findViewById(R.id.iv_home_bg_example);

		localBg = (ImageView) view.findViewById(R.id.iv_home_bg_local);

		// 实例展示部分
		initExample(view);

		// 本地相册
		initLocal(view);

		// 扫描本地文件
		Button btnScan = (Button) view.findViewById(R.id.btn_home_scan);
		btnScan.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 扫描本地电子相册文件
				mProgressDialog = ProgressDialog.show(context, "扫描文件", "加载数据中。。。");
				// 开启子线程扫描文件，并保存路径到文件
				scanFile();
			}
		});
		return view;
	}

	/**
	 * 开启子线程扫描文件，并保存路径到文件
	 */
	private void scanFile() {
		// 耗时操作，子线程中处理
		new Thread(new Runnable() {
			@Override
			public void run() {
				String spPath = Environment.getExternalStorageDirectory().toString();
				spPathList.clear();
				spPathList = ScanFileUtils.findFileWithPath(spPath, "sp");
				// 反序列化，清空旧列表
				path2Info(spPathList, localInfoList);

				// 将全部查找的路径序列化到本地文件
				File file = new File(spPath + "/smartphoto/");
				if (!file.exists()) {
					file.mkdirs();
				}
				// 覆盖的方式写入
				File pathFile = new File(file, "pathfile.temp");
				// 序列化到本地
				SerializableUtils.object2File(spPathList, pathFile);
				Message msg = Message.obtain();
				msg.what = FINISH;
				handler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * 根据路径反序列化成信息对象，即取得头文件
	 */
	private void path2Info(List<String> pathList, List<SmartPhotoInfo> infoList) {
		if (pathList != null && pathList.size() > 0) {
			// 清空旧列表
			infoList.clear();

			SmartPhotoInfo info = null;
			File file = null;
			for (String path : pathList) {
				file = new File(path);
				file.lastModified();
				Object obj = SerializableUtils.file2FirstObject(new File(path));
				if (obj != null && obj instanceof SmartPhotoInfo) {
					info = (SmartPhotoInfo) obj;
					info.setPath(path);
					info.setName(path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf(".")));
					info.setLastModifyTime(file.lastModified());
					infoList.add(info);
				}
			}
			// 排序
			Collections.sort(infoList);
		}
	}

	/**
	 * 改变背景的可视性
	 */
	private void changeBgVisible(List<SmartPhotoInfo> infoList, View bg) {
		// 是否有数据, 有数据则隐藏背景，没数据则显示背景
		if (infoList != null && infoList.size() > 0) {
			bg.setVisibility(View.GONE);
		} else {
			bg.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 实例展示部分
	 * 
	 * @param view
	 */
	private void initExample(View view) {

		// 根据路径反序列化成头信息对象
		path2Info(expPathList, exampleInfoList);

		// 改变背景的可视性
		changeBgVisible(exampleInfoList, exampleBg);

		// 实例部分
		HorizontialListView hlv = (HorizontialListView) view.findViewById(R.id.list_view_h);
		exampleAdapter = new HomeHListViewAdapter(context, exampleInfoList);
		hlv.setAdapter(exampleAdapter);

		/**
		 * 实例展示的点击事件
		 */
		hlv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				openPalyActivity(exampleInfoList, exampleAdapter, position, true);
			}
		});
	}

	/**
	 * 本地电子相册列表
	 * 
	 * @param view
	 */
	private void initLocal(View view) {

		path2Info(spPathList, localInfoList);

		// 改变背景的可视性
		changeBgVisible(localInfoList, localBg);

		ListView vlv = (ListView) view.findViewById(R.id.list_view_v);
		adapter = new HomeVListViewAdapter(context, localInfoList);
		vlv.setAdapter(adapter);

		vlv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				openPalyActivity(localInfoList, adapter, position, false);
			}
		});

		// 长按删除
		vlv.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				// 选择操作方式
				selectOption(position);
				return true;
			}
		});
	}

	/**
	 * 选择操作
	 */
	private void selectOption(final int position) {
		AlertDialog.Builder builder = new Builder(context);
		View view = View.inflate(context, R.layout.ml_dialog_select_option, null);
		TextView tvDelete = (TextView) view.findViewById(R.id.tv_delete);
		TextView tvRename = (TextView) view.findViewById(R.id.tv_rename);
		TextView tvUpload = (TextView) view.findViewById(R.id.tv_upload);

		final AlertDialog dialog = builder.create();
		dialog.setView(view, 0, 0, 0, 0);

		tvDelete.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDeleteDialog(position);
				dialog.dismiss();
			}
		});
		tvRename.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showRenameDialog(position);
				dialog.dismiss();
			}
		});
		tvUpload.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	/**
	 * 打开PlayActivity
	 * 
	 * @param infoList
	 * @param adapter
	 * @param position
	 */
	protected void openPalyActivity(List<SmartPhotoInfo> infoList, BaseAdapter adapter, int position, boolean isAutoPlay) {
		String path = infoList.get(position).getPath();
		if (new File(path).exists()) {
			// 开启展示页面
			Intent intent = new Intent(context, PlayActivity.class);
			intent.putExtra("path", path);
			intent.putExtra("isAutoPlay", isAutoPlay);
			startActivity(intent);
		} else {
			Toast.makeText(context, "文件不存在", 0).show();
			infoList.remove(position);
			// 更新列表
			adapter.notifyDataSetChanged();
		}
	}

	/**
	 * 提示重命名, 自定义对话框风格
	 * 
	 * @param position
	 */
	private void showRenameDialog(final int position) {

		AlertDialog.Builder builder = new Builder(context);
		View view = View.inflate(context, R.layout.ml_dialog_rename, null);
		final EditText etRename = (EditText) view.findViewById(R.id.et_rename);
		etRename.setHint(localInfoList.get(position).getName());

		Button btnOk = (Button) view.findViewById(R.id.btn_ok);
		Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);

		final AlertDialog dialog = builder.create();// 此时只是创建一个dialog做其他事情，但是还没有show()
		dialog.setView(view, 0, 0, 0, 0);// 设置对话框的view与边沿的距离

		// 点击确定
		btnOk.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String newName = etRename.getText().toString();
				if ("".equals(newName)) {
					Toast.makeText(context, "名称不能为空", 0).show();
					return;
				} else {
					String oldPath = localInfoList.get(position).getPath();
					File oldFile = new File(oldPath);
					String newPath = oldPath.substring(0, oldPath.lastIndexOf("/") + 1) + newName + ".sp";
					File newFile = new File(newPath);
					if (newFile.exists()) {
						Toast.makeText(context, "名称已被使用", 0).show();
						return;
					} else {
						// 文件更名
						oldFile.renameTo(newFile);

						// 音乐文件更名，只需删除旧的音乐文件即可
						deleteMusicFile(oldPath);

						// 更新显示
						localInfoList.get(position).setPath(newPath);
						localInfoList.get(position).setName(newName);
						adapter.notifyDataSetChanged();
						Toast.makeText(context, "重命名成功", 0).show();
					}
				}

				dialog.dismiss();
			}
		});

		// 点击取消
		btnCancel.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.show();// 显示对话框
	}

	/**
	 * 提示刪除
	 * 
	 * @param p
	 */
	private void showDeleteDialog(final int p) {
		// 提示
		AlertDialog.Builder builder = new Builder(context);
		builder.setTitle("提示");
		builder.setMessage("		确定要删除吗？");

		builder.setPositiveButton("确定", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String path = localInfoList.get(p).getPath();

				// 删除文件
				File file = new File(path);
				if (file.exists()) {
					file.delete();
				}
				// 删除音乐缓存文件
				deleteMusicFile(path);

				// 更新显示
				localInfoList.remove(p);
				adapter.notifyDataSetChanged();
			}
		});

		builder.setNegativeButton("取消", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// dialog.dismiss();
			}
		});

		builder.show();

	}

	/**
	 * 删除音乐文件
	 * 
	 * @param path
	 */
	private void deleteMusicFile(String path) {
		String musicPath = path.substring(0, path.lastIndexOf("/") + 1) + "music";
		String musicName = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf(".")) + ".mp3";
		File musicFile = new File(musicPath, musicName);
		if (musicFile.exists()) {
			musicFile.delete();
		}
	}

	/**
	 * 更新数据
	 */
	@Override
	public void onResume() {

		// 扫描本地生成的电子相册文件
		String spPath = Environment.getExternalStorageDirectory().toString() + "/smartphoto/smartphotofile";
		File file = new File(spPath);
		// 文件夹在2分钟之内修改过，就进行扫描
		if (file.exists() && (System.currentTimeMillis() - file.lastModified()) < 30 * 1000) {
			// 区分smartphotoFile文件夹的电子文件和其他文件夹的文件
			deleteList(spPath);

			List<String> pathList = ScanFileUtils.findFileWithPath(spPath, "sp");
			if (pathList != null && pathList.size() > 0) {
				spPathList.addAll(pathList);
			}
			// 反序列化，清空旧列表
			path2Info(spPathList, localInfoList);
			// 设置背景可视性
			changeBgVisible(localInfoList, localBg);
			// 更新ListView
			adapter.notifyDataSetChanged();
		}
		if (!file.exists()) {
			// 反序列化，将不存在的路径删除
			path2Info(spPathList, localInfoList);
			// 设置背景可视性
			changeBgVisible(localInfoList, localBg);
			// 更新ListView
			adapter.notifyDataSetChanged();
		}
		super.onResume();
	}

	/**
	 * 区分smartphotoFile文件夹的电子文件和其他文件夹的文件
	 * 
	 * @param spPath
	 */
	private void deleteList(String spPath) {
		// 区分smartphotoFile文件和其他文件
		List<String> deleteList = new ArrayList<String>();
		for (String path : spPathList) {
			File f = new File(path);
			if (spPath.equals(f.getParent())) {
				deleteList.add(path);
			}
		}
		// 循环自身时不能删除删除自身元素
		if (deleteList.size() > 0) {
			spPathList.removeAll(deleteList);
		}
	}

}
