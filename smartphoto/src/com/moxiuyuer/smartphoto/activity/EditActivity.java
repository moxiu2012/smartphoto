package com.moxiuyuer.smartphoto.activity;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.moxiuyuer.smartphoto.R;
import com.moxiuyuer.smartphoto.bean.SmartPhotoFile;
import com.moxiuyuer.smartphoto.bean.SmartPhotoInfo;
import com.moxiuyuer.smartphoto.bean.SongInfo;
import com.moxiuyuer.smartphoto.bean.WorkSpaceFile;
import com.moxiuyuer.smartphoto.bean.WorkSpaceInfo;
import com.moxiuyuer.smartphoto.utils.MusicUtils;
import com.moxiuyuer.smartphoto.utils.SerializableUtils;

/**
 * 编辑窗口 描述：编辑工作室文件名称和描述信息， 以及给工作室文件夹音乐，生成电子相册和视频
 * 
 * @author yuerting 2016-6-28 - 上午10:58:58
 */
public class EditActivity extends Activity implements OnClickListener {

	public static WorkSpaceInfo mWorkSpaceInfo;
	private EditText etName;
	private EditText etDesc;
	private boolean isChangeDesc;
	private File newfile;
	private String name;
	private File oldFile;
	private EditText etOutpoutDesc;
	private String newFilePath;
	private String oldFilePath;
	private Button btnSave;
	private Button btnOutput;
	private TextView tvOutpoutTogetherName;
	private ArrayList<WorkSpaceInfo> fileInfoList;
	private String filePath;
	private TextView tvBgMusicName;
	// 选择的歌曲信息
	private SongInfo songInfo;
	//歌曲的byte[]
	private byte[] musicByte;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ml_activity_edit);

		// 获取传输内容
		Intent intent = getIntent();
		mWorkSpaceInfo = (WorkSpaceInfo) intent.getSerializableExtra("mWorkSpaceInfo");
		fileInfoList = (ArrayList<WorkSpaceInfo>) intent.getSerializableExtra("fileInfoList");

		// 初始化控件
		initView();  

	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		// 标题
		etName = (EditText) findViewById(R.id.et_edit_name);
		etName.setHint(mWorkSpaceInfo.getName());
		// 描述
		etDesc = (EditText) findViewById(R.id.et_edit_desc);
		etDesc.setHint(mWorkSpaceInfo.getDesc());
		// 背景音乐
		tvBgMusicName = (TextView) findViewById(R.id.tv_output_bgMusic);
		TextView tvSelectMusic = (TextView) findViewById(R.id.tv_select_music);
		tvSelectMusic.setOnClickListener(this);

		// 输出时相册的描述
		etOutpoutDesc = (EditText) findViewById(R.id.et_output_desc);
		// 一起输出的相册名称
		tvOutpoutTogetherName = (TextView) findViewById(R.id.tv_output_together);
		// 一起输出
		TextView tvOutpoutTogether = (TextView) findViewById(R.id.tv_output_together_select);
		tvOutpoutTogether.setOnClickListener(this);

		// 控件的点击事件
		btnSave = (Button) findViewById(R.id.btn_edit_save);
		btnOutput = (Button) findViewById(R.id.btn_edit_output);

		btnSave.setOnClickListener(this);
		btnOutput.setOnClickListener(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			if (resultCode == 0) {
				filePath = data.getStringExtra("filePath");
				if (filePath != null && filePath != "") {
					tvOutpoutTogetherName.setText(filePath.substring(filePath.lastIndexOf("/") + 1));
				}
			} else if (resultCode == 1) {
				Serializable obj = data.getSerializableExtra("songInfo");
				if (obj != null) {
					songInfo = (SongInfo) obj;
					tvBgMusicName.setText(songInfo.getName());

					// 将歌曲转成byte[]
					musicByte = MusicUtils.musicFile2Byte(songInfo.getSongUrl(), songInfo.getStartCutTime(),
							songInfo.getStopCutTime(), songInfo.getDuration());
				}
			}
		}
	}

	/**
	 * 控件的点击事件
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 选择音乐
		case R.id.tv_select_music:
			Intent intent1 = new Intent(EditActivity.this, SelectMusicActivity.class);
			startActivityForResult(intent1, 0);

			break;
		// 选择一起输出的相册
		case R.id.tv_output_together_select:
			Intent intent2 = new Intent(EditActivity.this, ShowWorkSpaceFileActivity.class);
			intent2.putExtra("fileInfoList", fileInfoList);
			startActivityForResult(intent2, 1);

			break;
		// 保存
		case R.id.btn_edit_save:
			// 设置数据
			setData();
			// 保存编辑
			saveEdit();
			// 关闭当前界面
			finish();
			break;
		// 输出
		case R.id.btn_edit_output:
			// 设置数据
			setData();
			// 输出相册
			output();
			break;
		default:
			break;
		}
	}

	/**
	 * 输出相册
	 */
	private void output() {
		// 序列化到本地文件；
		String path = getExternalCacheDir().getPath();
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			path = Environment.getExternalStorageDirectory() + "/smartphoto/smartphotofile";
			File file = new File(path);
			if (!file.exists()) {
				file.mkdirs();
			}
		}

		String savePath = path + "/" + name + ".sp";
		File saveFile = new File(savePath);
		if (saveFile.exists()) {
			// 删除旧文件在创建
			saveFile.delete();
			saveFile = new File(savePath);
		}

		// 获取编辑完成的页面图片
		List<Object> file2Objects = SerializableUtils.file2Objects(oldFile);
		WorkSpaceInfo mWorkSpaceInfo = (WorkSpaceInfo) file2Objects.get(0);
		WorkSpaceFile mWorkSpaceFile = (WorkSpaceFile) file2Objects.get(1);
		List<byte[]> finishEditList = mWorkSpaceFile.getFinishEditList();

		// 如果是一起输出
		if (filePath != null && !oldFile.getAbsolutePath().equals(filePath)) {
			File file2 = new File(filePath);
			if (file2.exists()) {
				WorkSpaceFile mWorkSpaceFile2 = (WorkSpaceFile) SerializableUtils.file2Objects(file2).get(1);
				finishEditList.addAll(mWorkSpaceFile2.getFinishEditList());
			}
		}

		// 保存头信息
		SmartPhotoInfo mSmartPhotoInfo = new SmartPhotoInfo();
		mSmartPhotoInfo.setDesc(etOutpoutDesc.getText().toString());
		// 保存封面
		mSmartPhotoInfo.setImageByte(mWorkSpaceInfo.getCoverImage());

		// 保存内容
		SmartPhotoFile mSmartPhotoFile = new SmartPhotoFile();
		mSmartPhotoFile.setImageByteList(finishEditList);

		// 序列化到本地
		List<Object> objectList = new ArrayList<Object>();
		objectList.add(mSmartPhotoInfo);
		objectList.add(mSmartPhotoFile);
		objectList.add(musicByte);
		SerializableUtils.objects2File(objectList, saveFile);

		// 提示导出完成
		Toast.makeText(EditActivity.this, "电子相册已导出", 0).show();
		btnOutput.setBackgroundColor(0x66ff0000);
	}

	/**
	 * 保存编辑
	 */
	private void saveEdit() {
		// 判断是否是只改描述，不改名称
		String desc = etDesc.getText().toString();
		desc = "".equals(desc) ? etDesc.getHint().toString() : desc;
		isChangeDesc = name.equals(mWorkSpaceInfo.getName()) && !"".equals(desc);

		// 如果文件名称未被使用
		if (!newfile.exists() || isChangeDesc) {
			mWorkSpaceInfo.setName(name);
			mWorkSpaceInfo.setDesc(desc);

			// 保存新路径
			mWorkSpaceInfo.setFilePath(newFilePath);

			// 更新工作室文件
			List<Object> file2Objects = SerializableUtils.file2Objects(oldFile);
			// 删除旧文件
			oldFile.delete();

			file2Objects.set(0, mWorkSpaceInfo);
			SerializableUtils.objects2File(file2Objects, newfile);
		} else {
			// 如果文件名称已被使用, 则将mWorkSpaceInfo置空
			mWorkSpaceInfo = null;
		}
	}

	/**
	 * 设置数据
	 */
	private void setData() {
		name = etName.getText().toString();
		if (name.trim().length() == 0) {
			name = etName.getHint().toString();
		}
		// 更新路径
		oldFilePath = mWorkSpaceInfo.getFilePath();
		newFilePath = oldFilePath.substring(0, oldFilePath.lastIndexOf("/")) + "/" + name + ".uer";

		newfile = new File(newFilePath);
		oldFile = new File(oldFilePath);
	}

}
