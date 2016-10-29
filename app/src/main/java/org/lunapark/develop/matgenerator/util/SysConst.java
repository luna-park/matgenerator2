/**
 * 
 * Mikhail Malakhov
 *  
 * */

package org.lunapark.develop.matgenerator.util;

/**
 * 
 * Class for store Constants that using in others classes
 * 
 * */

public class SysConst {	
	
	/** List (or other) Index Constants */
	public static final int INDEX_EMPTY = -1;
	public static final int INDEX_FIRST = 0;

	/** I/O results Constants */
	public static final int IOEX_SUCCESSFULLY = 0;
	public static final int IOEX_FILENOTFOUND = 1;
	public static final int IOEX_UNEXPECTED = 2;	
	public static final int IOEX_STORAGE_NOT_AVAILABLE = 3;
	
	/** Connection results Constants */
	public static final int CON_SUCCESSFULLY = 0;
	public static final int CON_NOT_FOUND = 1;
	public static final int CON_UNEXPECTED = 2;
	
	/** Strings' Constants */
	public static final String STR_EMPTY = "";
	public static final String STR_BLANK = " ";
	public static final String STR_SEPRT = ",";
	public static final String STR_EQUAL = "=";
	public static final String STR_NODAT = "nd";
	public static final String STR_NEWLN = "\n";
		
	public static final String STR_WEB_REQUEST_START = "?";
	public static final String STR_WEB_REQUEST_SEPRT = "&";
	public static final String STR_WEB_REQUEST_BLANK = "%20";
	public static final String STR_WEB_REQUEST_UNICODE = "%";
	public static final String STR_WEB_REQUEST_EQUAL = SysConst.STR_EQUAL;
		
}
