package com.dailysee.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class PayUtils {
	public static final String PARTNER = "2088711660560521";
	public static final String SELLER = "ttl66868@163.com";
	public static final String RSA_PRIVATE = "MIICXQIBAAKBgQChLdGS0RLwINSkO/NgDL7JWNCV63EQ2lbQMg9sLshssNHQVfhkGaTTigSIAK3qAYFv7krIwkxCii/n73EOOut0tUJIXblWMVZWbTDyXT+HIzw2TIk+QFM2bqczOGGiFRnV7PU9v99DOArgjBuz2/HrUoHhYlAQQAMztMokapp6vQIDAQABAoGAWPZeP4gQyOOGGuRQL8q5H49bCfS7Io8w7ZdD9fVYgM2T8UcSY8XXuT7rw1mEpK2SEQLY2jiMOQnm6UC7CRyySodoA6po8AAWZIDfhU+pZpECRLQUGxB88nyYrKzJP0hPSaEdD7eWJIyTGACpdypzfaVAI3z6rHBhkpHB31GficECQQDMEh9MreeQkbVt55Z4k7tSTM328fkGkmpnp/cEZXlbh4MG0cAaO2w8ZoUgD7pN8qLLjzVrxAGtrLhVwvEg3eeRAkEAyjGTSKb6EzWIZTQnl5it5foBBwWzU7Kbict8SHA5Grx0STEi/BoY4T84o8v/wTsTugn/7MswVDql5SoVNPPCbQJBAKRlxQlE39O7Tmmns7SXbsSyUwqpjHAa69VtnrXP+aAe3xNTTmrTjDJsdLqLcQ7ydOuABw017hQ3nJiaXx6XJcECQC6aCIlJOwAsaXhx8eIl5YbI/W2h6bgTxXeX99g2TWMVJnQsKBUU6sEXsjVGcncV57EMVvk7x7LSJaRQ87xVpm0CQQCAlEF8T1UelOn43/iQ5+wFgSx+WQvJFAwS2u4kxrQo/LaslSAR7lV6bzFeMMBGcGs/RvmZiqXpNOrjC1ZeofKi";
	public static final String RSA_PUBLIC = "";

	/**
	 * create the order info. 创建订单信息
	 * 
	 */
	public static String getOrderInfo(String subject, String body, String price) {
		// 合作者身份ID
		String orderInfo = "partner=" + "\"" + PARTNER + "\"";

		// 卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

		// 商户网站唯一订单号
		orderInfo += "&out_trade_no=" + "\"" + getOutTradeNo() + "\"";

		// 商品名称
		orderInfo += "&subject=" + "\"" + subject + "\"";

		// 商品详情
		orderInfo += "&body=" + "\"" + body + "\"";

		// 商品金额
		orderInfo += "&total_fee=" + "\"" + price + "\"";

		// 服务器异步通知页面路径
		orderInfo += "&notify_url=" + "\"" + "http://120.24.77.231/ebp/cgi/payNotify.htm" + "\"";

		// 接口名称， 固定值
		orderInfo += "&service=\"mobile.securitypay.pay\"";

		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";

		// 参数编码， 固定值
		orderInfo += "&_input_charset=\"utf-8\"";

		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		orderInfo += "&it_b_pay=\"30m\"";

		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
		orderInfo += "&return_url=\"m.alipay.com\"";

		// 调用银行卡支付，需配置此参数，参与签名， 固定值
		// orderInfo += "&paymethod=\"expressGateway\"";

		return orderInfo;
	}

	/**
	 * get the out_trade_no for an order. 获取外部订单号
	 * 
	 */
	public static String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
		Date date = new Date();
		String key = format.format(date);

		Random r = new Random();
		key = key + r.nextInt();
		key = key.substring(0, 15);
		return key;
	}

	/**
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param content
	 *            待签名订单信息
	 */
	public static String sign(String content) {
		return SignUtils.sign(content, RSA_PRIVATE);
	}

	/**
	 * get the sign type we use. 获取签名方式
	 * 
	 */
	public static String getSignType() {
		return "sign_type=\"RSA\"";
	}
}
