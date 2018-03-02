package ad.jpush.util;

import com.google.gson.Gson;

import ad.jpush.receiver.Data;


/**
 * Created by 11833 on 2018/1/18.
 */

public class JsonDeal {
    public static Data getData(String str){
        str = str.replaceAll("\\\\","");
        Data date = new Gson().fromJson(str,Data.class);
        return date;
    }
}
