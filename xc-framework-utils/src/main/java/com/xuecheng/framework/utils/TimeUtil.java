package com.xuecheng.framework.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @program: XcEduCode
 * @description:
 * @author: WhyWhatHow
 * @create: 2020-04-07 14:14
 **/
public class TimeUtil {

    public  static  String getNow(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        String date = simpleDateFormat.format(new Date());
        return date ;
    }
}
