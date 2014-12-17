package com.dailysee.bean;

import java.io.Serializable;
import java.util.List;

public class CityEntity implements Serializable {
	public int cityId;//				城市编码
	public String name;//				城市名称
	public int parentId;//				所属上级城市编码
	public int levelType;//				1: 省，2: 市，3: 区，4: 街道
	public List<CityEntity> cityChilds;
}
