package com.huaji.galgamebyhuaji.myUtil;

import java.util.List;

public class ListUtil {
	public static boolean isNull (List list) {
		if ( list == null ) return true;
		return list.isEmpty();
	}
}