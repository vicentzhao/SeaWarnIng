package com.crrain.sortablecontactlist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.crrain.sortablecontactlist.bean.ContactBean;
import com.crrain.sortablecontactlist.util.ContactInfoService;
import com.crrain.sortablecontactlist.util.StringUtil;

public class MainActivity extends Activity implements OnItemClickListener{

	public static final int UPDATE_VIEW_TIPS = 1;
	public static final int HIDDEN_VIEW_TIPS = 2;

	private int sideBarWidth = 80;
	private long timeToDelay = 1500;
	private TextView scrollTipView;
	private SideBar indexBar;
	private java.util.Timer timer;
	private TimerTask task;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UPDATE_VIEW_TIPS:
				if (scrollTipView != null) {
					
					/*LayoutParams layoutParams = indexBar.getLayoutParams();
					layoutParams.width = sideBarWidth * 2;
					indexBar.setLayoutParams(layoutParams);*/
					
					String tips = msg.obj.toString();
					scrollTipView.setText(tips);
					scrollTipView.setVisibility(View.VISIBLE);

					if (task != null) {
						task.cancel();
					}
					if (timer != null) {
						timer.cancel();
					}

					task = new TimerTask() {
						public void run() {
							handler.sendEmptyMessage(HIDDEN_VIEW_TIPS);
						}
					};
					timer = new java.util.Timer(true);
					timer.schedule(task, timeToDelay);
				}
				break;
			case HIDDEN_VIEW_TIPS:
				if (scrollTipView != null) {
					scrollTipView.setVisibility(View.INVISIBLE);
				/*	LayoutParams layoutParams = indexBar.getLayoutParams();
					layoutParams.width = sideBarWidth;
					indexBar.setLayoutParams(layoutParams);*/
				}
				break;
			default:
				break;
			}
		}
	};
	private ArrayList<ContactBean> contactBeanList;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ListView list = (ListView) findViewById(R.id.myListView);
		list.setOnItemClickListener(this);
		contactBeanList = InitListViewData();
		ArrayList<String> stringList =new ArrayList<String>();
		for (int i = 0; i < contactBeanList.size(); i++) {
			stringList.add(contactBeanList.get(i).getContactName());
		}
		MyAdapter adapter = new MyAdapter(this, contactBeanList,stringList);
		list.setAdapter(adapter);
		
		indexBar = (SideBar) findViewById(R.id.sideBar);
		sideBarWidth = indexBar.getLayoutParams().width;
		indexBar.setListView(list);
		indexBar.setHandler(handler);         //设置侧边栏放大
		scrollTipView = (TextView) findViewById(R.id.tvScrollSectionShow);
		scrollTipView.setVisibility(View.INVISIBLE);
	}

	private ArrayList<ContactBean> InitListViewData() {
		ArrayList<String> stringList = new ArrayList<String>();
		ContactInfoService servic = new ContactInfoService(MainActivity.this);
		ArrayList<ContactBean> contactName = servic.getContact();
		for (int i = 0; i < contactName.size(); i++) {
			stringList.add(contactName.get(i).getContactName());
		}
		Collections.sort(stringList, new Comparator<String>() {
			@Override
			public int compare(String lhs, String rhs) {
				return StringUtil.cn2py(lhs).toUpperCase()
						.compareTo(StringUtil.cn2py(rhs).toUpperCase());
			}
		});
		ArrayList<ContactBean> myContactNameLists = new ArrayList<ContactBean>();
        for (int i = 0; i < stringList.size(); i++) {
        	for (int j = 0; j < contactName.size(); j++) {
        		 String name = contactName.get(j).getContactName();
        		 if(name.equals(stringList.get(i))){
        			ContactBean bean = new ContactBean();
        			bean.setContactName(stringList.get(i));
        			bean.setContactPhone(contactName.get(j).getContactPhone());
        			myContactNameLists.add(bean);
        			contactName.remove(j);
        			break;
        		 }
			}
		}
		return myContactNameLists;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.myListView:
			String contactPhoneNum = contactBeanList.get(arg2).getContactPhone();
			  //创建一个意图，意图是重点，会在今后重点讲到
            Intent intent = new Intent();
            //该意图的动作是拨打电话
            intent.setAction(intent.ACTION_CALL);
            //设置该意图要操作的数据，这里也就是电话号码，注意书写的格式
            intent.setData(Uri.parse("tel:"+contactPhoneNum));
            //开始
            startActivity(intent);
			break;

		default:
			break;
		}
		
	}

}
