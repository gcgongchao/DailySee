package com.dailysee.bean;

import java.io.Serializable;

public class Push implements Serializable {
	public String messageId;//	String	是	消息ID	
	public String title;//	String	是	标题	
	public String msgType;//	String	是	消息类型	01.优惠,02.活动,03.讯息,04.预约提醒,05.处理评价,06.接单确认,07.接单通知,08.服务将到,09.退款处理,10.退款结果,11.续单确认
	public String body;//	String		消息内容	
	public long cnt;//	long	是	数量	 
}
