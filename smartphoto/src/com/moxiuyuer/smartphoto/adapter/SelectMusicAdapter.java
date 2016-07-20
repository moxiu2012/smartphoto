package com.moxiuyuer.smartphoto.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moxiuyuer.smartphoto.R;
import com.moxiuyuer.smartphoto.bean.SongInfo;

/**
 * 选择音乐的适配器
 * 描述：
 *
 * @author yuerting
 *  2016-7-17  - 下午12:04:25
 */
public class SelectMusicAdapter extends MyBaseAdapter{

	private List<SongInfo> songList;
	public SelectMusicAdapter(Context context, List<SongInfo> songList) {
		super(context);
		this.songList = songList;
	}

	@Override
	public int getCount() {
		return songList.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_lv_select_muisc, null);
			holder.tvSong = (TextView) convertView.findViewById(R.id.tv_song);
			holder.tvSinger = (TextView) convertView.findViewById(R.id.tv_singer);
			holder.tvSongId = (TextView) convertView.findViewById(R.id.tv_song_duration);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tvSong.setText(songList.get(position).getName());
		holder.tvSinger.setText(songList.get(position).getArtist());
		holder.tvSongId.setText(songList.get(position).getTime());

		return convertView;
	}
	
	
	class ViewHolder{
		TextView tvSong;
		TextView tvSinger;
		TextView tvSongId;
	}

	

}
