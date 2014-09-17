package com.crrain.sortablecontactlist;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Telephony.Sms.Conversations;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.crrain.sortablecontactlist.bean.ContactBean;
import com.crrain.sortablecontactlist.util.ContactInfoService;
import com.crrain.sortablecontactlist.util.StringUtil;

public class MainActivity extends Activity {

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
					LayoutParams layoutParams = indexBar.getLayoutParams();
					layoutParams.width = sideBarWidth * 2;
					indexBar.setLayoutParams(layoutParams);
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
					LayoutParams layoutParams = indexBar.getLayoutParams();
					layoutParams.width = sideBarWidth;
					indexBar.setLayoutParams(layoutParams);
				}
				break;
			default:
				break;
			}
		}
	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ListView list = (ListView) findViewById(R.id.myListView);
		ArrayList<ContactBean> contactBeanList = InitListViewData();
		ArrayList<String> stringList =new ArrayList<String>();
		for (int i = 0; i < contactBeanList.size(); i++) {
			stringList.add(contactBeanList.get(i).getContactName());
		}
		MyAdapter adapter = new MyAdapter(this, contactBeanList,stringList);
		list.setAdapter(adapter);
		indexBar = (SideBar) findViewById(R.id.sideBar);
		sideBarWidth = indexBar.getLayoutParams().width;
		indexBar.setListView(list);
		indexBar.setHandler(handler);
		scrollTipView = (TextView) findViewById(R.id.tvScrollSectionShow);
		scrollTipView.setVisibility(View.INVISIBLE);
	}

	private ArrayList<ContactBean> InitListViewData() {
		ArrayList<String> stringList = new ArrayList<String>();
		// stringList.add("深圳");
		// stringList.add("深水埗");
		// stringList.add("Crrain");
		// stringList.add("啊");
		// stringList.add("八点");
		// stringList.add("ads");
		// stringList.add("abhor");
		// stringList.add("万分感");
		// stringList.add("abuse");
		// stringList.add("candidate");
		// stringList.add("eapture");
		// stringList.add("careful");
		// stringList.add("hatch");
		// stringList.add("kause");
		// stringList.add("kelebrate");
		// stringList.add("forever");
		// stringList.add("fable");
		// stringList.add("阿达");
		// stringList.add("fox");
		// stringList.add("funny");
		// stringList.add("fail");
		// stringList.add("jailor");
		// stringList.add("aazz");
		// stringList.add("zero");
		// stringList.add("zing");
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

}
