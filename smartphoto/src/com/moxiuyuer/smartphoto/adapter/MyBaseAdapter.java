package com.moxiuyuer.smartphoto.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

public abstract class MyBaseAdapter extends BaseAdapter{

	protected Context context;
	protected LayoutInflater mInflater  = null;
	

	public MyBaseAdapter(Context context) {
		super();
		this.context = context;
		mInflater = LayoutInflater.from(context);
	}
	
	
	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

}
