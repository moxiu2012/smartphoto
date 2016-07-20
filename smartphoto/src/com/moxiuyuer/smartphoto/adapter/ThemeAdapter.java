package com.moxiuyuer.smartphoto.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.moxiuyuer.smartphoto.R;
import com.moxiuyuer.smartphoto.bean.ThemeBean;

/**
 * 主题列表的adapter 描述：实现单选
 * 
 * @author moxiuyuer 2016-6-5 下午8:00:47
 */
public class ThemeAdapter extends BaseAdapter {
	private List<ThemeBean> list;
	protected LayoutInflater mInflater;
	private int selectedThemeNo = -1;
	Activity activity;
	boolean flag = false;

	/**
	 * 
	 * @param context
	 * @param list
	 * @param selectedThemeNo
	 *            标记被选中的id
	 */
	public ThemeAdapter(Context context, List<ThemeBean> list, int selectedThemeNo) {
		this.list = list;
		this.selectedThemeNo = selectedThemeNo;
		mInflater = LayoutInflater.from(context);
		this.activity = (Activity) context;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		ThemeBean themeBean = list.get(position);

		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_grid_theme, null);
			viewHolder.mImageView = (ImageView) convertView.findViewById(R.id.iv_theme_image);
			viewHolder.mTextViewTitle = (TextView) convertView.findViewById(R.id.tv_theme_title);

			viewHolder.mRadioButton = (RadioButton) convertView.findViewById(R.id.cb_theme_select);
			viewHolder.mRadioButton.setChecked(false);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		// 设置主题名称
		viewHolder.mTextViewTitle.setText(themeBean.getThemeName());
		// 设置主题封面
		if (themeBean.getCoverBitmap() != null) {
			viewHolder.mImageView.setImageBitmap(themeBean.getCoverBitmap());
		} else {
			viewHolder.mImageView.setImageResource(R.drawable.mi_bg_theme);
		}

		viewHolder.mRadioButton.setId(position);
		viewHolder.mRadioButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				if (isChecked) {
					if (selectedThemeNo != -1) {
						RadioButton tempRadioButton = (RadioButton) activity
								.findViewById(selectedThemeNo);
						if (tempRadioButton != null) {
							tempRadioButton.setChecked(false);
						}
					}
					selectedThemeNo = buttonView.getId();
				}
			}
		});

		if (selectedThemeNo != position) {
			viewHolder.mRadioButton.setChecked(false);
		} else {
			viewHolder.mRadioButton.setChecked(true);
		}
		return convertView;
	}

	public static class ViewHolder {
		public ImageView mImageView;
		public TextView mTextViewTitle;
		public RadioButton mRadioButton;
	}


	/**
	 * 返回选择的主题
	 * 
	 * @return
	 */
	public int getSelectedThemeNo() {
		return selectedThemeNo;
	}
}
