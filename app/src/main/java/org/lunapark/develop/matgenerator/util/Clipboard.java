package org.lunapark.develop.matgenerator.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.widget.TextView;

/** 
 * Class Clipboard - class for working with a clip board service
 *  Extends - none
 *  Implements - none
 *  @author Mikhail Malakhov
 * */

public class Clipboard {
	
	/**---------------------------------------------------------------------------*
	 * Private fields
	 **---------------------------------------------------------------------------*/
			
	/* Private field for store a Context value */
	private final Context fContext;
	
	/* Private field for store a link to the TextView object */
	private TextView fTextView = null;
	
	
	/**---------------------------------------------------------------------------*
	 * Constructors
	 **---------------------------------------------------------------------------*/
	
	/** 
	 * Constructor with one parameter (Context)
	 * 	@param context - process context
	 *  */
	public Clipboard(Context context) { this.fContext = context; }
	
	/** 
	 * Constructor with one parameter (TextView) 
	 * 	@param textView - TextView object that will be as text source
	 * */
	public Clipboard(TextView textView) { 	
		this(textView.getContext());
		fTextView = textView;		
	}
		
	
	/**---------------------------------------------------------------------------*
	 * Copy methods (all variations)
	 **---------------------------------------------------------------------------*/
	
	/** 
	 * Copy text (String) to clip board
	 * 	@param text - text that need to copy to clip board
	 * */
	public void copy(String text) { Clipboard.copy(fContext, text); }
	
	/** 
	 * Copy value (integer) to clip board
	 * 	@param value - value that need to copy to clip board
	 * */
	public void copy(int value) { Clipboard.copy(fContext, value); }	
	
	/** 
	 * Copy array of string (String) to clip board
	 * 	@param strArray - array of string that need to copy to clip board
	 * */
	public void copy(String[] strArray) { Clipboard.copy(fContext, strArray); }
	
	/** Copy text from view (textView) to clip board */
	public void copyView() { Clipboard.copyView(this.getTextView()); }	
	
	/**---------------------------------------------------------------------------*
	 * Paste methods (all variations)
	 **---------------------------------------------------------------------------*/	
			
	/** 
	 * Paste text (String) from clip board
	 * 	@return Text from a clip board, or an empty string, if clip board 
	 * 	does not contain a text
	 * */
	public String paste() { return Clipboard.paste(fContext); }
	
	/** Paste text to view (textView) from clip board */
	public void pasteView() { Clipboard.pasteView(this.getTextView()); }	
	
	
	/**---------------------------------------------------------------------------*
	 * Checking a type of clip board data 
	 **---------------------------------------------------------------------------*/	
	
	/** Checking a data type into clip board (PlainText or HTMLText) */	
	public boolean hasText() { return Clipboard.hasText(fContext); }	
	
	/** Checking a data type into clip board (PlainText) */	
	public boolean hasPlainText() { return Clipboard.hasPlainText(fContext); }	
	
	/** Checking a data type into clip board (HTMLText) */	
	public boolean hasHTMLText() { return Clipboard.hasHTMLText(fContext); }
		
	
	/**---------------------------------------------------------------------------*
	 * Property TextView (getter and setter) 
	 **---------------------------------------------------------------------------*/
	
	/** Getter for property TextView */
	public TextView getTextView() { return fTextView; }
	
	/** Setter for property TextView */
	public void setTextView(TextView textView) { fTextView = textView; }
	
	/** Checking a TextView object */
	public boolean hasTextView() { return this.getTextView() != null; }			
	
	
	/**---------------------------------------------------------------------------*
	 * Static methods for copy to clip board (all variations)
	 **---------------------------------------------------------------------------*/	
	
	/** 
	 * Static Method - Copy text (String) to clip board
	 * 	@param context - process context
	 * 	@param text - text that need to copy to clip board
	 * */
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public static void copy(Context context, String text) {
		
		/* Checking a SDK version and copy text to clip board */
		if(Clipboard.isNewSDK())
			Clipboard.getClipboardManager(context).setPrimaryClip(
					ClipData.newPlainText(SysConst.STR_EMPTY,text));
		else			
			((android.text.ClipboardManager) 
					context.getSystemService(Context.CLIPBOARD_SERVICE)).setText(text);		
	}	
		
	/** 
	 * Static Method - Copy value (integer) to clip board
	 * 	@param context - process context
	 * 	@param value - value that need to copy to clip board
	 * */	
	public static void copy(Context context, int value) {
		Clipboard.copy(context, String.valueOf(value));
	}	
	
	/** 
	 * Static Method - Copy array of string (String) to clip board
	 * 	@param context - process context
	 * 	@param strArray - array of string that need to copy to clip board
	 * */
	public static void copy(Context context, String[] strArray) {
		
		/* Create a StringBuilder Object */
		StringBuilder SBuilder = new StringBuilder();
		
		/* Fill a StringBuilder Object */
		for (int i = 0; i < strArray.length; i++)
			SBuilder.append(strArray[i] + SysConst.STR_NEWLN);					
		
		/* Copy to clip board */	
		Clipboard.copy(context, SBuilder.toString().trim());
		
	}	
	
	/**
	 * Static Method - Copy text from view (textView) to clip board
	 * 	@param textView - view that contains copying text
	 * */
	public static void copyView(TextView textView) {		
		/* Checking a textView and copy text from textView to clip board */		
		if (textView != null) 
			Clipboard.copy(textView.getContext(), textView.getText().toString());
	}
	
	
	/**---------------------------------------------------------------------------*
	 * Static methods for paste from clip board (all variations)
	 **---------------------------------------------------------------------------*/
	 
	/** 
	 * Static Method - Paste text (String) from clip board
	 * 	@param context - process context
	 * 	@return Text from a clip board, or an empty string, if clip board 
	 * 	does not contain a text
	 * */	
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public static String paste(Context context) {
		
		/* Variable for store a Paste Data (as String) */
		String pasteData = null;
		
		/* Checking a SDK version and try to getting a Paste Data (as String) */
		if(Clipboard.isNewSDK())								
			pasteData = Clipboard.getClipboardManager(context).getPrimaryClip()
					.getItemAt(0).getText().toString();						
		else
			pasteData =  ((android.text.ClipboardManager) 
					Clipboard.getClipboardService(context)).getText().toString();
					
		/* Checking a Paste Data and return value */
		if (pasteData != null) return pasteData; 
		return SysConst.STR_EMPTY;		
		
	}	
	
	/**
	 * Static Method - Paste text to view (textView) from clip board
	 * 	@param textView - view for paste a text
	 * */	
	public static void pasteView(TextView textView) {		
		/* Checking a textView and copy text from clip board to textView */
		if (textView != null) 
			textView.setText(Clipboard.paste(textView.getContext()));		
	}		
	
		
	/**---------------------------------------------------------------------------*
	 * Static Method for a checking a type of clip board data 
	 **---------------------------------------------------------------------------*/
	
	/**
	 * Static Method - Checking a data type into clip board (PlainText or HTMLText)
	 *  @param context - process context
	 * */	
	public static boolean hasText(Context context) {			
		return Clipboard.hasPlainText(context) & Clipboard.hasHTMLText(context);						
	}
	
	/** 
	 * Static Method -  Checking a data type into clip board (PlainText) 
	 * 	@param context - process context
	 * */
	@SuppressLint("InlinedApi")
	@SuppressWarnings("deprecation")	
	public static boolean hasPlainText(Context context) {		
		
		/* Checking a SDK version and checking data type into clip board */
		if(Clipboard.isNewSDK())						
			return Clipboard.hasMimeType(context, ClipDescription.MIMETYPE_TEXT_PLAIN);						
		else			
			return ((android.text.ClipboardManager) Clipboard.getClipboardService(context)).hasText();
		
	}
	
	/** 
	 * Static Method -  Checking a data type into clip board (HTMLText) 
	 * 	@param context - process context
	 * */
	@SuppressLint("InlinedApi")
	public static boolean hasHTMLText(Context context) {
		
		/* Checking a SDK version and checking data type into clip board */
		if(Clipboard.isNewSDK())						
			return Clipboard.hasMimeType(context, ClipDescription.MIMETYPE_TEXT_HTML);						
		else			
			return Clipboard.hasPlainText(context);
		
	}
			
	/** 
	 * Static Method -  Checking a MIME type of a data into clip board 
	 * 	@param context - process context
	 *  @param mimeType - MIME type that need to check
	 * */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	protected static boolean hasMimeType(Context context, String mimeType) {
		
		/* Gets a handle to the clip board manager (service) */
		ClipboardManager clpbrdMngr = Clipboard.getClipboardManager(context);
		
		/* Checking a MIME type of clip board data */
		if (clpbrdMngr.hasPrimaryClip())
			return clpbrdMngr.getPrimaryClipDescription().hasMimeType(mimeType);
		
		/* Return a value */
		return false;
		
	}
	
	
	/**---------------------------------------------------------------------------*
	 * Static Method for getting clip board service and clip board manager 
	 **---------------------------------------------------------------------------*/
	
	/**
	 * Static Method - Getting clip board manager
	 * 	@param context - process context  
	 * */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	protected static ClipboardManager getClipboardManager(Context context) {
		return (ClipboardManager) Clipboard.getClipboardService(context);
	}	
	
	/**
	 * Static Method - Getting clip board service
	 * 	@param context - process context  
	 * */
	@TargetApi(Build.VERSION_CODES.BASE)
	public static Object getClipboardService(Context context) {
		return context.getSystemService(Context.CLIPBOARD_SERVICE);
	}
	

	/**---------------------------------------------------------------------------*
	 * Static Method for a checking a version of SDK  
	 **---------------------------------------------------------------------------*/	
	
	/**
	 * Static Method - Checking a version of SDK   
	 * */	
	protected static boolean isNewSDK() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB; 
	}

}
