package com.zhulong.eduvideo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.zhulong.eduvideo.entity.Message;

import java.util.ArrayList;
import java.util.List;

public class DBMessage {
	private DBManager dbManager;
	private final String TABLENAME = "message";
	private Context mContext;

	public DBMessage(Context context) {
		// TODO Auto-generated constructor stub
		dbManager = DBManager.getInstance();
		mContext = context;
	}

	public void close(){
		if(dbManager != null){
			dbManager.close();
		}
	}

	// private String message_id;
	// private String ic_avatar_default;
	// private String dateformat;
	// private long dateline;
	// private String message;
	// private String uid;
	// private String username;
	// private String author_url;
	// private String channel_id;
	// private String message_type; // 对话类型
	// private int state = 0; // 状态-1是发送失败， 0是还没发，1是正在发，2是上传成功
	// private String localPic; // 图片本地地址

	/**
	 * 插入一条对话
	 * 
	 * @param message
	 * @return
	 */
	public long insert(Message message) {

		try {
			dbManager.open(mContext);
			return dbManager.insert(TABLENAME, null, getContentValues(message));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * 根据message_id删除某条对话
	 * 
	 * @param message
	 * @return
	 */
	public long delete(Message message) {
		try {
			dbManager.open(mContext);
			return dbManager.delete(TABLENAME, "message_id",
					message.getMessage_id());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}
	/**
	 * 根据id删除某条对话
	 * 
	 * @param message
	 * @return
	 */
	public long deleteById(Message message) {
		try {
			dbManager.open(mContext);
			return dbManager.delete(TABLENAME, "id",
					message.getId()+"");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * 根据channel_id删除某个 对话
	 * 
	 */
	public long deleteBychannel_id(String channel_id) {
		try {
			dbManager.open(mContext);
			return dbManager.delete(TABLENAME, "channel_id", channel_id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * 根据channel_id查询某人的全部对话
	 */
	public List<Message> findAllByChannel_id(String channal_id) {
		List<Message> messages = null;
		try {
			dbManager.open(mContext);
			Cursor cursor = dbManager.findById(TABLENAME, "channel_id",
					channal_id, null);
			// 判断游标是否为空
			if (cursor.moveToFirst()) {
				messages = new ArrayList<Message>();
				// 遍历游标
				int len = cursor.getCount();
				for (int i = 0; i < len; i++) {
					Message msg = getMessage(cursor);
					messages.add(msg);
					cursor.moveToNext();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return messages;
	}

	/**
	 * 根据message_id查询某人的一条对话
	 * 
	 * @param message
	 * @return
	 */
	public List<Message> findAllByMessage_id(Message message) {
		List<Message> messages = null;
		try {
			dbManager.open(mContext);
			Cursor cursor = dbManager.findById(TABLENAME, "message_id",
					message.getChannel_id(), null);
			// 判断游标是否为空
			if (cursor.moveToFirst()) {
				messages = new ArrayList<Message>();
				// 遍历游标
				for (int i = 0; i < cursor.getCount(); i++) {
					Message msg = getMessage(cursor);
					messages.add(msg);
					cursor.moveToNext();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return messages;
	}

	/**
	 * 根据message_id更新某条对话
	 */
	public List<Message> findLimitByChannel_id(String channal_id, int from) {
		List<Message> messages = new ArrayList<Message>();
		try {
			dbManager.open(mContext);
			Cursor cursor = dbManager.find(TABLENAME,
					new String[] { "channel_id" }, new String[] { channal_id },
					null, "dateline desc", from + ",10");
			// 判断游标是否为空
			if (cursor.moveToFirst()) {
				messages = new ArrayList<Message>();
				// 遍历游标
				for (int i = 0; i < cursor.getCount(); i++) {
					Message msg = getMessage(cursor);
					messages.add(msg);
					cursor.moveToNext();
				}
			}

			return messages;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * 根据message_id更新某条对话
	 * 
	 * @param message
	 * @return
	 */
	public boolean updataByMessage_id(Message message) {
		ContentValues contentValues = getContentValues(message);
		String whereString = "message_id=" + message.getMessage_id();
		try {
			dbManager.open(mContext);
			return dbManager
					.update(TABLENAME, whereString, null, contentValues);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;

	}

	/**
	 * 根据message_id更新某条对话
	 * 
	 * @param message
	 * @return
	 */
	public boolean updateById(Message message) {
		ContentValues contentValues = getContentValues(message);
		String whereString = "id=" + message.getId();
		dbManager.open(mContext);
		try {
			return dbManager
					.update(TABLENAME, whereString, null, contentValues);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	private ContentValues getContentValues(Message message) {
		ContentValues contentValues = new ContentValues();
		contentValues.put("uid", message.getUid());
		contentValues.put("username", message.getUsername());
		contentValues.put("avatar", message.getAvatar());
		contentValues.put("dateline", message.getDateline());
		contentValues.put("dateformat", message.getDateformat());
		contentValues.put("message", message.getMessage());
		contentValues.put("localPic", message.getLocalPic());
		contentValues.put("state", message.getState());
		contentValues.put("audiotime", message.getTimeLong());
		contentValues.put("message_id", message.getMessage_id());
		contentValues.put("message_type", message.getMessage_type());
		contentValues.put("channel_id", message.getChannel_id());
		contentValues.put("author_url", message.getAuthor_url());
		contentValues.put("read", message.getIsRead());
		return contentValues;
	}

	private Message getMessage(Cursor cursor) {
		Message message = new Message();
		int id = cursor.getInt(cursor.getColumnIndex("id"));
		String uid = cursor.getString(cursor.getColumnIndex("uid"));
		String username = cursor.getString(cursor.getColumnIndex("username"));
		String avatar = cursor.getString(cursor.getColumnIndex("avatar"));
		long dateline = cursor.getLong(cursor.getColumnIndex("dateline"));
		String dateformat = cursor.getString(cursor
			.getColumnIndex("dateformat"));
		String message1 = cursor.getString(cursor.getColumnIndex("message"));
		int state = cursor.getInt(cursor.getColumnIndex("state"));
		int audiotime = cursor.getInt(cursor.getColumnIndex("audiotime"));
		String localPic = cursor.getString(cursor.getColumnIndex("localPic"));
		String message_id = cursor.getString(cursor
			.getColumnIndex("message_id"));
		String message_type = cursor.getString(cursor
			.getColumnIndex("message_type"));
		String channel_id = cursor.getString(cursor
			.getColumnIndex("channel_id"));
		String author_url = cursor.getString(cursor
			.getColumnIndex("author_url"));
		int read = cursor.getInt(cursor.getColumnIndex("read"));
		message.setId(id);
		message.setUid(uid);
		message.setUsername(username);
		message.setAvatar(avatar);
		message.setDateline(dateline);
		message.setDateformat(dateformat);
		message.setMessage(message1);
		message.setState(state);
		message.setTimeLong(audiotime);
		message.setLocalPic(localPic);
		message.setMessage_id(message_id);
		message.setMessage_type(message_type);
		message.setChannel_id(channel_id);
		message.setAuthor_url(author_url);
		message.setIsRead(read);
		return message;
	}

}
