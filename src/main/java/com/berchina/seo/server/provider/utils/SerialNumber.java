package com.berchina.seo.server.provider.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * @Package com.berchina.seo.server.utils
 * @Description: TODO(流水号生产类)
 * @Author 任小斌  renxiaobin@berchin.com
 * @Date 16/2/25 下午4:07
 * @Version V1.0
 */
public class SerialNumber {

    private static SerialNumber serialNumber = null;

    private SerialNumber() {
    }

    /**
     * 取得serialNumber的单例实现
     *
     * @return
     */
    public static SerialNumber getInstance() {
        if (serialNumber == null) {
            synchronized (SerialNumber.class) {
                if (serialNumber == null) {
                    serialNumber = new SerialNumber();
                }
            }
        }
        return serialNumber;
    }

    /**
     * 生成下一个编号
     */
    public synchronized String generaterNextNumber() {
        Date date = new Date();
        Random r = new Random();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return formatter.format(date) + Math.abs(r.nextInt() % 100);
    }
}
