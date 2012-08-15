package com.shansun.sparrow.actor.sqlite;

import java.util.Date;

import com.shansun.sparrow.actor.api.Message;

/**
 * 存储到Sqlite里的消息格式
 * 
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-8-15
 */
public class MessageDO {

	private Message	message;

	private Date	gmtCreate;

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public Date getGmtCreate() {
		return gmtCreate;
	}

	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}
}
