package com.dailysee.bean;

import java.io.Serializable;
import java.util.List;

public class Room implements Serializable {
	public List<String> imgs;
	public long roomId;
    public long merchantId;
    public String companyName;
    public String roomTypeName;
    public String name;
    public String roomNo;
    public int roomType;
    public int totalCnt;
    public String status;
    public String statusName;
    public int validCnt;
    public int price;
    public int ttPrice;
    public String roomDesc;
    public long creator;
    public long createDate;
    public long updator;
    public long updateDate;
}
