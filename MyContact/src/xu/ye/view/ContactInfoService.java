package xu.ye.view;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import xu.ye.bean.ContactBean;
import xu.ye.bean.PhoneLog;
import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;

public class ContactInfoService {

	private static final String TAG = "ConcactInfoService";
	private static ContactInfoService instance;
	Context context;

	public static final int COL_ID = 0;
	public static final int COL_NAME = 1;
	public static final int COL_HAS_PHONE = 2;

	final String[] selectCol = new String[] { ContactsContract.Contacts._ID,
			ContactsContract.Contacts.DISPLAY_NAME,
			ContactsContract.Contacts.HAS_PHONE_NUMBER

	};
	final String[] selPhoneCols = new String[] {
			ContactsContract.CommonDataKinds.Phone._ID,
			ContactsContract.CommonDataKinds.Phone.NUMBER,
			ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
			ContactsContract.CommonDataKinds.Phone.TYPE,
			ContactsContract.CommonDataKinds.Phone.LABEL,
			ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY };

	public ContactInfoService(Context context) {
		this.context = context;
	}

	public static ContactInfoService getInstance(Context context) {
		if (null == instance) {
			instance = new ContactInfoService(context);
		}
		return instance;
	}

	public ArrayList<PhoneLog> getCallLogs() {

		ArrayList<PhoneLog> lists = null;
		final String[] selCallLogs = new String[] { CallLog.Calls.CACHED_NAME,
				CallLog.Calls.NUMBER, CallLog.Calls.TYPE, CallLog.Calls.DATE };
		Cursor cur = context.getContentResolver().query(
				CallLog.Calls.CONTENT_URI, selCallLogs, null, null,
				CallLog.Calls.DEFAULT_SORT_ORDER);
		if (null == cur) {
			return lists;
		}
		lists = new ArrayList<PhoneLog>();
		PhoneLog p;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date date = new Date();
		while (cur.moveToNext()) {
			p = new PhoneLog();
			p.cachedName = cur.getString(0);
			p.number = cur.getString(1);
			p.type = cur.getShort(2);
			date.setTime(cur.getLong(3));
			p.callData = sdf.format(date);
			lists.add(p);
		}
		cur.close();
		return lists;
	}

	/**
	 * ���ݺ����ȡ��ϵ��
	 * 
	 * @param name
	 * @return
	 */
	public Cursor getContact(String name) {

		final String select = ContactsContract.CommonDataKinds.Phone.NUMBER
				+ " like '" + name + "%'";
		final Cursor cur = context.getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
				selPhoneCols, select, null, "sort_key_alt");
		return cur;
	}

	/**
	 * ��ȡȫ����ϵ�˵绰
	 * 
	 * @return
	 */
	public ArrayList<ContactBean> getContact() {
		ArrayList<ContactBean> peoples = null;
		final String select = "((" + Contacts.DISPLAY_NAME + " NOTNULL) AND ("
				+ Contacts.HAS_PHONE_NUMBER + "=1) AND ("
				+ Contacts.DISPLAY_NAME + " != '' ))";
		Cursor cur = context.getContentResolver().query(
				ContactsContract.Contacts.CONTENT_URI,
				selectCol,
				select,
				null,
				ContactsContract.Contacts.DISPLAY_NAME
						+ " COLLATE LOCALIZED ASC");
		if (null == cur) {
			return peoples;
		}
		peoples = new ArrayList<ContactBean>();
		if (cur.moveToFirst()) {
			do {
				ContactBean cb = new ContactBean();
				// ��ϵ�˵�ID
				String contactId = cur.getString(COL_ID);
				String name = cur.getString(COL_NAME);
				// �������� �鿴�ж��ٸ���ϵ����
				int numberCount = cur.getInt(COL_HAS_PHONE);
				if (numberCount > 0) {

					Cursor phone = context.getContentResolver().query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							selPhoneCols,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID
									+ "=" + contactId, null, null);

					if (null != phone && phone.moveToFirst()) {
						do {
							String phoneNumber = phone
									.getString(phone
											.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
							short phoneType = phone
									.getShort(phone
											.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
							if (phoneType == 1) {
//								cb.setContactHomePhone(phoneNumber);
								cb.setPhoneNum(phoneNumber);
							} else {
								cb.setPhoneNum(phoneNumber);
							}
							if (!name.equals(phoneNumber)) {
								cb.setDisplayName(name);
							}
						} while (phone.moveToNext());
					}
				}
				peoples.add(cb);
			} while (cur.moveToNext());
		}

		return peoples;
	}
}
