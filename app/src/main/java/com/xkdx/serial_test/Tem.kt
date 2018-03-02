package com.xkdx.serial_test

import android.util.Log
import com.ximon.core.util.log.LatteLogger
import com.xkdx.serial_test.Constant.KEY_HUM
import com.xkdx.serial_test.Constant.KEY_LIGHT
import com.xkdx.serial_test.Constant.KEY_PM2_5
import com.xkdx.serial_test.Constant.KEY_TEM
import java.util.*

/**
 * Created by 11833 on 2017/12/29.
 */

object Tem {
    //01	03	16	0212	0032	0005	02f3	0003	0255	02ba	0001 8cb2	0000 0056 94	8C
    /** 16进制中的字符集  */
    private val HEX_CHAR = "0123456789ABCDEF"

    /** 16进制中的字符集对应的字节数组  */
    private val HEX_STRING_BYTE = HEX_CHAR.toByteArray()

    fun getDate(read_buff: ByteArray): HashMap<String, Float?> {
        val date:HashMap<String, Float?> = HashMap()
        Log.d("jian", CRCValidate.isCRCConfig(read_buff).toString() + "")
        if (CRCValidate.isCRCConfig(read_buff)) {
            date[KEY_TEM] = getDate(read_buff, 5, 2)//温度
            date[KEY_HUM] = getDate(read_buff, 3, 2)//湿度
            date[KEY_PM2_5] = getDate2(read_buff, 13, 2)//PM2.5
            date[KEY_LIGHT] = getDate2(read_buff, 23, 4)//亮度
        }
        return date
    }

    private fun getDate(read_buff: ByteArray?, dataTemOffset: Int, length: Int): Float? {
        var data: Float? = null
        if (read_buff != null) {
            // 长度是否正确，节点号是否正确，CRC是否正确
            val data_buff = ByteArray(length)//存放温度数据数组
            //抠出湿度数据，放进data_buff。（要拷贝的数组源，拷贝的开始位置，要拷贝的目标数组，填写的开始位置，拷贝的长度）
            System.arraycopy(read_buff, dataTemOffset, data_buff, 0, length)
            //解析数据data_buff（16进制转10进制）
            data = ByteToFloatUtil.hBytesToFloat(data_buff)
            /*********除以10返回数据 */
            data = data / 10.0f
            //在正常范围内才返回

                Log.d("date", data.toString() + "")
                return data

        }
        Log.d("date", data!!.toString() + "")
        return data// 返回数据
    }

    /**
     * 不需要除10
     * @param read_buff
     * @param dataTemOffset
     * @param length
     * @return
     */
    private fun getDate2(read_buff: ByteArray?, dataTemOffset: Int, length: Int): Float? {
        var data: Float? = null
        if (read_buff != null) {
            // 长度是否正确，节点号是否正确，CRC是否正确
            val data_buff = ByteArray(length)//存放温度数据数组
            //抠出湿度数据，放进data_buff。（要拷贝的数组源，拷贝的开始位置，要拷贝的目标数组，填写的开始位置，拷贝的长度）
            System.arraycopy(read_buff, dataTemOffset, data_buff, 0, length)
            //解析数据data_buff（16进制转10进制）
            data = ByteToFloatUtil.hBytesToFloat(data_buff)
            //在正常范围内才返回
            if (data > -100 && data < 100) {

                LatteLogger.d(data.toString() + "")
                return data
            }
        }

        return data// 返回数据
    }

}
