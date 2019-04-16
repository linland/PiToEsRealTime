package com.link.entity;

public enum PIvaluetype {

	PI_Type_null(0),
	PI_Type_bool(1),
	PI_Type_uint8(2),
	PI_Type_int8(3),
	PI_Type_char(4),
	PI_Type_uint16(5),
	Int16(6),
	PI_Type_uint32(7),
	Int32(8),
	PI_Type_uint64(9),
	PI_Type_int64(10),
	Float16(11),//Float16
	Float32(12),
	Float64(13),
	PI_Type_PI2(14),
	Digital(101),
	Blob(102),
	Timestamp(104),
	String(105),
	PI_Type_bad(255);
	
	public 	int value;
	
	private PIvaluetype(int value){
		this.value=value;
	}

	public  int getValue(){
		return value;
	}
	
	
	 

	   

	
	/*
	public void setValue(int value){
		this.value=value;
	}
	*/
	
	public static PIvaluetype getByValue(int value) {
		for(PIvaluetype typex : PIvaluetype.values()) {
			if(typex.value == value) {
				return typex;
			}
		}
		throw new IllegalArgumentException("No element matches " + value);
	}



}
