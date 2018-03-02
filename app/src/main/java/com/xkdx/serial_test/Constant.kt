package com.xkdx.serial_test

/**
 * Created by 11833 on 2018/1/9.
 */

object Constant {
    val openLight = "FF 01 01 AA"
    val openLightSucceed = "FF 0A 0A AA"
    val closeLight = "FF 00 00 AA"
    val closeLightSucceed  = "FF 0B 0B AA"
    val openCharge = "FF 02 02 AA"
    val openChargeSucceed  = "FF 0C 0C AA"
    val closeCharge = "FF 03 03 AA"
    val closeChargeSucceed  = "FF 0D 0D AA  "
    val KEY_TEM = "tem"
    val KEY_HUM = "hum"
    val KEY_PM2_5 = "pm2_5"
    val KEY_LIGHT = "light"
}
