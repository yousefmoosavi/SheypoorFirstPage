package com.sheypoor.classes;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sheypoor.R;

@SuppressLint("NewApi")
public class CustomAdapterItem extends ArrayAdapter<ListItem> {

	Context context;
	List<ListItem> drawerItemList;
	ListItem data[] = null;
	int layoutResID;
	public ImageLoader imageLoader;

	public CustomAdapterItem(Context context,int layoutResourceID, List<ListItem> listItems) {
		super(context, layoutResourceID, listItems);
		this.context = context;
		this.drawerItemList = listItems;
		this.layoutResID = layoutResourceID;
		imageLoader = new ImageLoader(context);
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		HolderAgency Holder;
		View view = convertView;
		Typeface tf = Typeface.createFromAsset(this.context.getAssets(),
				"fonts/IranSans.ttf");

		if (view == null) {
			LayoutInflater inflater = ((ListActivity) context).getLayoutInflater();
			view = inflater.inflate(layoutResID, parent, false);
			Holder = new HolderAgency();
			Holder.textView1 = (TextView) view.findViewById(R.id.textView1);
			Holder.textView2 = (TextView) view.findViewById(R.id.textView2);
			Holder.textView3 = (TextView) view.findViewById(R.id.textView3);
			Holder.textView4 = (TextView) view.findViewById(R.id.textView4);
			Holder.imageView1 = (ImageView) view.findViewById(R.id.imageView1);

			view.setTag(Holder);

		} else {
			Holder = (HolderAgency) view.getTag();
		}

		ListItem Items = (ListItem) this.drawerItemList.get(position);
		
		Holder.textView1.setText(Items.getStr1());
		Holder.textView1.setTypeface(tf);
		
		Holder.textView2.setText(Items.getStr2());
		Holder.textView2.setTypeface(tf);
		
		Holder.textView3.setText(Items.getStr3());
		Holder.textView3.setTypeface(tf);
		
		Holder.textView4.setText(Items.getStr4());
		Holder.textView4.setTypeface(tf);

		imageLoader.DisplayImage(Items.getStr5().toString(), Holder.imageView1);
		
		return view;

	}

	private static class HolderAgency {
		TextView textView1, textView2, textView3, textView4;
		ImageView imageView1;
			
	}

}