package com.moxiuyuer.smartphoto.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.moxiuyuer.smartphoto.R;
import com.moxiuyuer.smartphoto.adapter.SelectMusicAdapter;
import com.moxiuyuer.smartphoto.bean.SongInfo;
import com.moxiuyuer.smartphoto.utils.FindSong;
import com.moxiuyuer.smartphoto.utils.MusicUtils;
import com.moxiuyuer.smartphoto.utils.PlayMusic;

/**
 * 选择音乐的activity 描述：
 * 
 * @author yuerting 2016-7-17 - 上午11:18:51
 */
public class SelectMusicActivity extends Activity implements OnClickListener {

	protected static final int SAVE_FINISH = 0;

	private List<SongInfo> songList;

	private SongInfo songInfo;

	private TextView tvMusicName;

	private TextView tvPlayTime;

	private TextView tvTotalTime;

	private SeekBar sbPlay;

	private ImageView ivPlay;

	private TextView tvStartCut;

	private TextView tvStopCut;

	private TextView tvStartCutTime;

	private TextView tvStopCutTime;

	private ImageView ivPlayCut;

	private Button btnSaveCut;

	private Button btnCancelCut;

	private PlayMusic player;

	private SelectMusicActivity context;

	// 标志是否在播放状态
	private boolean isPlay = false;
	private boolean isPlayCut = false;

	// 开始剪切的时间 毫秒
	private int startCutTime;
	// 停止剪切的时间 毫秒
	private int stopCutTime;

	private int position;

	private static int defaultColor = 0x220044ff;
	private static int clickColor = 0x99ff0000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ml_activity_select_music);

		context = SelectMusicActivity.this;

		// 初始化数据
		songList = FindSong.find(SelectMusicActivity.this);
		if (songList == null) {
			songList = new ArrayList<SongInfo>();
		}

		// 设置ListView
		initListView();

		// 初始化裁剪的控件
		initView();

		// 给seekBar设置监听
		setSeekbarListener();

	}

	/**
	 * seekBar设置监听
	 */
	private void setSeekbarListener() {
		sbPlay.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

				position = seekBar.getProgress();
				if (isPlay) {
					player.stopPlay();
					isPlay = false;
					ivPlay.setImageResource(R.drawable.mi_music_play);
					playMusic(false);
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

				int m = (int) (progress / 60 / 1000);
				int s = (int) (progress / 1000) % 60;
				String mm = m < 10 ? String.valueOf("0" + m) : String.valueOf(m);
				String ss = s < 10 ? String.valueOf("0" + s) : String.valueOf(s);
				tvPlayTime.setText(mm + ":" + ss);
				if (isPlay && progress == sbPlay.getMax()) {
					ivPlay.setImageResource(R.drawable.mi_music_play);
				}

				if (isPlayCut && progress >= stopCutTime) {
					if (player != null) {
						player.stopPlay();
					}
					isPlayCut = false;
					ivPlayCut.setImageResource(R.drawable.mi_music_play);
				}
			}
		});
	}

	/**
	 * 初始化裁剪的相关控件
	 */
	private void initView() {
		// 歌名
		tvMusicName = (TextView) findViewById(R.id.tv_music_name);

		// 已播放时间
		tvPlayTime = (TextView) findViewById(R.id.tv_play_time);
		// 进度条
		sbPlay = (SeekBar) findViewById(R.id.seekbar_play);
		// 总时长
		tvTotalTime = (TextView) findViewById(R.id.tv_total_time);
		// 原歌曲播放按钮
		ivPlay = (ImageView) findViewById(R.id.iv_music_play);
		ivPlay.setOnClickListener(this);

		// 开始剪切
		tvStartCut = (TextView) findViewById(R.id.tv_start_cut);
		tvStartCut.setOnClickListener(this);

		// 开始剪切时间
		tvStartCutTime = (TextView) findViewById(R.id.tv_start_cut_time);
		// 结束剪切时间
		tvStopCutTime = (TextView) findViewById(R.id.tv_stop_cut_time);

		// 结束剪切
		tvStopCut = (TextView) findViewById(R.id.tv_stop_cut);
		tvStopCut.setOnClickListener(this);

		// 播放剪切的音乐按钮
		ivPlayCut = (ImageView) findViewById(R.id.iv_play_cut);
		ivPlayCut.setOnClickListener(this);

		// 取消剪切
		btnCancelCut = (Button) findViewById(R.id.btn_cancel_cut_music);
		btnCancelCut.setOnClickListener(this);

		// 保存剪切
		btnSaveCut = (Button) findViewById(R.id.btn_save_cut_music);
		btnSaveCut.setOnClickListener(this);
	}

	/**
	 * 设置ListView
	 */
	private void initListView() {
		ListView lvSelectMusic = (ListView) findViewById(R.id.lv_select_music);
		SelectMusicAdapter adapter = new SelectMusicAdapter(SelectMusicActivity.this, songList);
		lvSelectMusic.setAdapter(adapter);

		lvSelectMusic.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// 歌曲信息
				songInfo = songList.get(position);

				// 重置
				reset();// 重置与songInfo无关项

				tvMusicName.setText(songInfo.getName());

				// 原歌曲播放控件
				playMusic(false);

			}
		});
	}

	@Override
	public void onClick(View v) {
		if (songInfo == null) {
			return;
		}
		switch (v.getId()) {
		case R.id.iv_music_play:
			// 原歌曲播放控件
			if (!isPlayCut) {
				playMusic(true);
			}

			break;
		case R.id.tv_start_cut:
			// 开始剪切
			startCutTime = sbPlay.getProgress();
			String startCutTimeStr = tvPlayTime.getText().toString();
			tvStartCutTime.setText(startCutTimeStr);
			tvStartCut.setBackgroundColor(clickColor);
			break;
		case R.id.tv_stop_cut:
			// 结束剪切
			stopCutTime = sbPlay.getProgress();
			String stopCutTimeStr = tvPlayTime.getText().toString();
			tvStopCutTime.setText(stopCutTimeStr);
			tvStopCut.setBackgroundColor(clickColor);
			break;
		case R.id.iv_play_cut:
			// 剪切后播放
			if (!isPlay) {
				playCutMusic();
			}

			break;
		case R.id.btn_save_cut_music:
			// 保存剪切歌曲
			saveCutMusic();

			break;
		case R.id.btn_cancel_cut_music:
			// 放弃剪切
			reset();
			break;

		default:
			break;
		}
	}

	/**
	 * 保存裁剪后的歌曲
	 */
	private void saveCutMusic() {
		if (startCutTime >= stopCutTime) {
			Toast.makeText(context, "裁剪时间不合理", 0).show();
			return;
		}
		
		if(player != null && (isPlay || isPlayCut) ) {
			player.stopPlay();
			player = null;
		}
		
		if(songInfo != null) {
			songInfo.setStartCutTime(startCutTime);
			songInfo.setStopCutTime(stopCutTime);
		}
		
		Intent data = new Intent();
		data.putExtra("songInfo", songInfo);
		setResult(1, data);
		//关闭界面
		finish();
	}

	/**
	 * 播放裁剪后的音乐
	 * 
	 * @param startCutTime2
	 * @param stopCutTime2
	 */
	private void playCutMusic() {

		String songUrl = songInfo.getSongUrl();
		if (songUrl != null && new File(songUrl).exists()) {
			// 将进度条设置在开始剪切的位置
			sbPlay.setProgress(startCutTime);
			if (!isPlayCut) {
				player = new PlayMusic();
				player.startPlay(context, songInfo.getSongUrl(), sbPlay, startCutTime);
				ivPlayCut.setImageResource(R.drawable.mi_music_pause);
				isPlayCut = true;
			} else {
				if (player != null) {
					player.stopPlay();
				}
				isPlayCut = false;
				ivPlayCut.setImageResource(R.drawable.mi_music_play);
			}
		} else {
			Toast.makeText(context, "文件没找到", 0).show();
		}
	}

	/**
	 * 播放原歌曲
	 */
	private void playMusic(boolean isResetCut) {

		String songUrl = songInfo.getSongUrl();
		if (songUrl != null && new File(songUrl).exists()) {
			if (!isPlay) {
				// 重置裁剪部分
				if (isResetCut) {
					resetCut();
				}
				player = new PlayMusic();
				player.startPlay(context, songInfo.getSongUrl(), sbPlay, position);
				ivPlay.setImageResource(R.drawable.mi_music_pause);
				isPlay = true;
			} else {
				if (player != null) {
					player.stopPlay();
				}
				isPlay = false;
				ivPlay.setImageResource(R.drawable.mi_music_play);
			}
		} else {
			Toast.makeText(context, "文件没找到", 0).show();
		}
	}

	/**
	 * 重置
	 */
	private void reset() {
		// 播放进度归零
		tvPlayTime.setText("00:00");
		tvTotalTime.setText(songInfo.getTime());

		sbPlay.setProgress(0);
		sbPlay.setMax(songInfo.getDuration());

		// 停止原歌曲播放
		if (player != null && isPlay) {
			player.stopPlay();
			player = null;
		}
		isPlay = false;
		ivPlay.setImageResource(R.drawable.mi_music_play);

		// 裁剪归零
		resetCut();

		// 停止裁剪歌曲播放
		if (player != null && isPlayCut) {
			player.stopPlay();
			player = null;
		}
		isPlayCut = false;
		ivPlayCut.setImageResource(R.drawable.mi_music_play);

	}

	/**
	 * 重置裁剪部分
	 */
	private void resetCut() {
		// 播放位置归零
		position = 0;
		// 裁剪归零
		tvStartCutTime.setText("00:00");
		startCutTime = 0;
		tvStartCut.setBackgroundColor(defaultColor);

		tvStopCutTime.setText(songInfo.getTime());
		stopCutTime = songInfo.getDuration();
		tvStopCut.setBackgroundColor(defaultColor);
	}

	@Override
	public void onBackPressed() {
		if(songInfo != null) {
			reset();
		}
//		Intent data = new Intent();
//		setResult(2, data);
		setResult(2);
		super.onBackPressed();
	}

}
