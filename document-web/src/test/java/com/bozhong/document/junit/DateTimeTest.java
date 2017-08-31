package com.bozhong.document.junit;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by xiezg@317hu.com on 2017/7/31 0031.
 */
public class DateTimeTest {
    public static void main(String args[]) {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        System.out.println(calendar.getTime());
        System.out.println(calendar.getTimeInMillis());
        System.out.println(new Date().getTime());
    }
}
