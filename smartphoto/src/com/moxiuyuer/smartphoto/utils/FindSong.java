package com.moxiuyuer.smartphoto.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.moxiuyuer.smartphoto.bean.SongInfo;

/**
 * 根据内容提供者查找音乐文件
 * 描述：
 *
 * @author yuerting
 *  2016-7-17  - 下午12:07:27
 */
public class FindSong {


	public static List<SongInfo> find(Context context) {

		// 从sd卡中查找音乐文件
		Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
				null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);// 按歌名排序
		System.out.println("url=" + MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);

		if (cursor != null && cursor.getCount() > 0) {
			List<SongInfo> songList = new ArrayList<SongInfo>();
			SongInfo song = null;
			String name;
			String artist;
			String songUrl;
			int duration;
			while (cursor.moveToNext()) {
				name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
				artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
				songUrl = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
				duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));

				if (duration > 0) {
					if (songUrl.endsWith(".mp3") || songUrl.endsWith(".wma") || songUrl.endsWith(".m4a")
							|| songUrl.endsWith(".aac") || songUrl.endsWith(".wav")) {// 音乐格式并且有播放时间
						song = new SongInfo();
						song.setName(name);
						song.setArtist(artist);
						song.setSongUrl(songUrl);
						song.setDuration(duration);

						songList.add(song);
					}
				}
			}
			cursor.close();
			return songList;
		}
		return null;
	}
}
