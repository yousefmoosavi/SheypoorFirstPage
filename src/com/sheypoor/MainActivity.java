package com.sheypoor;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sheypoor.classes.CustomAdapterItem;
import com.sheypoor.classes.ListItem;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends ListActivity {
	ArrayList<String> listItems = new ArrayList<String>();
	private int visibleThreshold = 0;
	private int pageNo = 0;
	Button button1;
	ListView listView1;
	CustomAdapterItem adapter;
	String[][] prgmNameList;
	String response, strURL;
	List<ListItem> dataList;
	ProgressDialog dialog;
	int clickCounter = 0;
	Context context;
	boolean isLoading = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		try {
			context = this;
			dataList = new ArrayList<ListItem>();

			adapter = new CustomAdapterItem(context,R.layout.activity_main_item, dataList);
			button1 = (Button) findViewById(R.id.button1);
			button1.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					Toast.makeText(context, "درج آگهی", Toast.LENGTH_SHORT).show();
					
				}
			});
			listView1 = (ListView) findViewById(android.R.id.list);
			listView1.setOnScrollListener(new OnScrollListener() {
				
				@Override
				public void onScrollStateChanged(AbsListView arg0, int arg1) {
					
				}
				
				@Override
				public void onScroll(AbsListView view, int firstVisibleItem,
						int visibleItemCount, int totalItemCount) {

					if (isLoading) return;
					
					if ((totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
						
						if (isOnline() && pageNo !=0){
							dialog = new ProgressDialog(context);
							strURL = "http://www.sheypoor.com/api/v2/offer/optimizedlist?taske=20&lang=fa-IR&skip="+(pageNo*20);
							dialog.setMessage("چند لحظه ...");
							dialog.setCancelable(true);
							dialog.show();
							isLoading = true;
							myAsyncTask MyTask = new myAsyncTask();
							MyTask.execute();
						}			
					}
				}
			});

			dialog = new ProgressDialog(context);
			dialog.setMessage("چند لحظه ...");
			dialog.setCancelable(true);
			dialog.show();
			strURL = "http://www.sheypoor.com/api/v2/offer/optimizedlist?taske=20&lang=fa-IR&skip="+(pageNo*20);
			myAsyncTask MyTask = new myAsyncTask();
			MyTask.execute();
			
		} catch (Exception e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	public boolean isOnline() {
		@SuppressWarnings("static-access")
		ConnectivityManager cm = (ConnectivityManager) getBaseContext()
				.getSystemService(getBaseContext().CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		return netInfo != null && netInfo.isConnectedOrConnecting();
	}

	private class myAsyncTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPostExecute(Void result) {

			super.onPostExecute(result);

			try {
				if (response.split("::")[0].equalsIgnoreCase("e")) {
					Toast.makeText(getBaseContext(), response.split("::")[1],
							Toast.LENGTH_SHORT).show();
					return;
				}

				try {
					JSONObject obj = new JSONObject(response);
					JSONArray arobj = obj.getJSONArray("items");				
					
					for (int i=0; i<arobj.length(); i++){
						if (!arobj.getJSONObject(i).getString("priceString").equalsIgnoreCase("general.negotiable")) //
						dataList.add(new ListItem(arobj.getJSONObject(i)
								.getString("name"), arobj.getJSONObject(i)
								.getString("locationName"), arobj.getJSONObject(i)
								.getString("date"), arobj.getJSONObject(i)
								.getString("priceString") + " تومان ", arobj.getJSONObject(i)
								.getString("images")));
						else
							dataList.add(new ListItem(arobj.getJSONObject(i)
									.getString("name"), arobj.getJSONObject(i)
									.getString("locationName"), arobj.getJSONObject(i)
									.getString("date"), " توافقی ", arobj.getJSONObject(i)
									.getString("images")));
							
					}
					
					if (pageNo==0){
						adapter = new CustomAdapterItem(context,R.layout.activity_main_item, dataList);
						setListAdapter(adapter);
					}
					else{
						adapter.notifyDataSetChanged();
					}
					
					pageNo++;
					
				} catch (Exception e) {
					Toast.makeText(getBaseContext(), e.toString(),
							Toast.LENGTH_LONG).show();
				}

			} catch (Exception e) {
				Toast.makeText(getBaseContext(),
						"لطفا اتصال اینترنت خود را بررسی کنید",
						Toast.LENGTH_LONG).show();

			}finally{
				if (dialog.isShowing()) dialog.dismiss();
				isLoading = false;
			}

		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			try {
				URL page = new URL(strURL);
				Log.d("async", strURL);
				StringBuffer text = new StringBuffer();
				HttpURLConnection conn = (HttpURLConnection) page
						.openConnection();
				conn.connect();
				InputStreamReader in = new InputStreamReader(
						(InputStream) conn.getContent());
				BufferedReader buff = new BufferedReader(in);
				String line;
				do {
					line = buff.readLine();
					text.append(line + "\n");
				} while (line != null);
				response = text.toString();
				return null;
			} catch (Exception e) {
				response = "e::" + e.getMessage();
				return null;
			}
		}
	}


}
