package com.moxiuyuer.smartphoto.utils;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.widget.SeekBar;
import android.widget.Toast;

/**
 * 播放音乐的类 描述：
 * 
 * @author yuerting 2016-7-17 - 下午3:49:00
 */
public class PlayMusic {

	private MediaPlayer mediaPlayer;

	private Timer timer;
	private TimerTask task;

	public void startPlay(Context context, String src, final SeekBar sb, final int position) {
		mediaPlayer = new MediaPlayer();
		try {
			mediaPlayer.setDataSource(src);// 设置播放源
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);// 设置播放格式
			mediaPlayer.setOnPreparedListener(new OnPreparedListener() {// 在准备好之后播放

						@Override
						public void onPrepared(MediaPlayer mp) {
							mediaPlayer.start();// 开始播放
							mediaPlayer.seekTo(position);
							timer = new Timer();
							task = new TimerTask() {
								@Override
								public void run() {
									sb.setProgress(mediaPlayer.getCurrentPosition());
								}
							};
							timer.schedule(task, 0, 1000);// 延迟0毫秒，间隔1000毫秒
						}
					});
			mediaPlayer.prepareAsync();// 设置为异步播放
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(context, "播放异常", 0).show();
		}
	}

	/**
	 * 停止播放
	 */
	public void stopPlay() {
		if (mediaPlayer != null) {
			if (timer != null && task != null) {
				timer.cancel();
				task.cancel();
				timer = null;
				task = null;
			}
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}

	/**
	 * xuanh
	 */
	public void playMusic(Context context, String src) {
		mediaPlayer = new MediaPlayer();
		try {
			mediaPlayer.setDataSource(src);// 设置播放源
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);// 设置播放格式
			mediaPlayer.prepareAsync();// 设置为异步播放

			mediaPlayer.setOnPreparedListener(new OnPreparedListener() {// 在准备好之后播放
						@Override
						public void onPrepared(MediaPlayer mp) {
							final int duration = mediaPlayer.getDuration();
							mediaPlayer.start();// 开始播放

							if (mediaPlayer.getCurrentPosition() == duration) {
								mediaPlayer.seekTo(0);
							}
						}
					});
			//设置循环播放
//			mediaPlayer.setLooping(true);
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(context, "播放异常", 0).show();
		}
	}

}
