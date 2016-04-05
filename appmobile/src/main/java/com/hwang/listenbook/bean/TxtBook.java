package com.hwang.listenbook.bean;

import net.tsz.afinal.annotation.sqlite.Id;

public class TxtBook {
	@Id(column="id")
	private int id;
	private String code;//唯一标识
	private String name;
	private String cover;
	private String category;
	private String production;
	private String description;
	private boolean isCollect =false;
}
