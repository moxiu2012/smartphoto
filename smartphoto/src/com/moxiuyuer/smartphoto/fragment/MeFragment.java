package com.moxiuyuer.smartphoto.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.moxiuyuer.smartphoto.R;

public class MeFragment extends Fragment {
	
	private Context context;
	
	public MeFragment (Context context){
		this.context = context;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
		 ViewGroup container,  Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.ml_fragment_me, container, false);
		ImageView ivmy = (ImageView) view.findViewById(R.id.iv_my);
		ivmy.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				 Toast.makeText(context, "被点击了", 0).show();
			}
		});
		return view;
	}

}