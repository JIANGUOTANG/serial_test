package ad.db;

import android.content.Context;
import android.content.SharedPreferences;


import ad.Constants;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by ying on 17-11-23.
 */

public class SharePeferenceUtil {

    public static int getCodeNumberId(Context context){
        //获取本地保存的唯一标志码
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.DB_NAME,MODE_PRIVATE);
        int code_number = sharedPreferences.getInt(Constants.CODE_NUMBER_ID,0);
        return code_number;

    }

    public static void setCodeNumberId(Context context,int codeNumber){
        //设置唯一标识码
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.DB_NAME,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Constants.CODE_NUMBER_ID,codeNumber);
        editor.commit();
    }
    public static void setCodeNumber(Context context,String codeNumber){
        //设置唯一标识码
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.DB_NAME,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.CODE_NUMBER,codeNumber);
        editor.commit();
    }

    public static String getCodeNumber(Context context){
        //获取本地保存的唯一标志码
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.DB_NAME,MODE_PRIVATE);
        String code_number = sharedPreferences.getString(Constants.CODE_NUMBER,"");
        return code_number;

    }


}
