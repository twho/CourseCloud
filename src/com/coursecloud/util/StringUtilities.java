package com.coursecloud.util;

public class StringUtilities {
	public static boolean isEmpty(String s) {
        return s.trim().length() == 0;
    }
	
	public static boolean stringToBoolean(String s){
		if(s.equalsIgnoreCase("true") || s.equalsIgnoreCase("t")){
			return true;
		}
		return false;
	}

	
}
