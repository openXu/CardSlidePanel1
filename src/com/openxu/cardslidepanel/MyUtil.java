package com.openxu.cardslidepanel;


import java.text.DecimalFormat;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Toast;

public class MyUtil {
	public static boolean isDebug = true;   
	private static String TAG = "MyUtil";
    /**
     * 唯一的toast
     */
    private static Toast mToast = null;

	public synchronized static void showToast(Context mContext, int srcId,
			String more) {
		String str = "";
		try {
			str = mContext.getResources().getString(srcId);
		} catch (Exception e) {
		}
		if (!TextUtils.isEmpty(more))
			str = str + more;
		
		 if (mToast != null) {
            //mToast.cancel();
        } else {
        	mToast = Toast.makeText(mContext, str, Toast.LENGTH_SHORT);
        }
        mToast.setText(str);
        mToast.show();
	}

	public synchronized static void showToast(Context mContext, String more,
			int srcId, String more1) {
		String str = "";
		try {
			str = mContext.getResources().getString(srcId);
		} catch (Exception e) {
		}
		if (!TextUtils.isEmpty(more))
			str = more + str;
		if (!TextUtils.isEmpty(more1))
			str = str + more1;
		 if (mToast != null) {
	            //mToast.cancel();
	        } else {
	        	mToast = Toast.makeText(mContext, str, Toast.LENGTH_SHORT);
	        }
	        mToast.setText(str);
	        mToast.show();
	}

	public static String getString(Context mContext, int srcId) {
		return mContext.getResources().getString(srcId);
	}

	public static void TOAST(Context context, String msg) {
		if (isDebug)
			Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	public static void LOG_V(String TAG, String msg) {
		if (isDebug)
			Log.v(TAG, msg);
	}

	public static void LOG_I(String TAG, String msg) {
		if (isDebug)
			Log.i(TAG, msg);
	}

	public static void LOG_D(String TAG, String msg) {
		if (isDebug)
			Log.d(TAG, msg);
	}

	public static void LOG_W(String TAG, String msg) {
		if (isDebug)
			Log.w(TAG, msg);
	}

	public static void LOG_E(String TAG, String msg) {
		if (isDebug)
			Log.e(TAG, msg);
	}

	public static void sysout(String clazz, String text) {
		if (isDebug)
			System.out.println(clazz + " " + text);
	}

	public static int dp2px(int dp, Context context) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				context.getResources().getDisplayMetrics());
	}


	public static String getFloatStr(float a, float b) {
		DecimalFormat decimalFormat = new DecimalFormat("0.0");// 构造方法的字符格式这里如果小数不足2位,会以0补足.
		float pro = (a * 100.0f) / (b * 1.0f);
		if (pro == 0)
			return "0 %";
		String str = decimalFormat.format(pro);
		if (str.endsWith("0")) {
			str = str.substring(0, str.lastIndexOf("0"));
			if (str.endsWith("0")) {
				str = str.substring(0, str.lastIndexOf("0"));
			}
			if (str.endsWith(".")) {
				str = str.substring(0, str.lastIndexOf("."));
			}
		}
		return str + " %";
	}

	public static String byteToHexString(int ib) {
		char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
				'B', 'C', 'D', 'E', 'F' };
		char[] ob = new char[2];
		ob[0] = Digit[(ib >>> 4) & 0X0F];
		ob[1] = Digit[ib & 0X0F];
		String s = new String(ob);
		return s;
	}
	
}
