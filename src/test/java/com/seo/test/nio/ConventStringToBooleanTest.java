package com.seo.test.nio;

import org.junit.Assert;
import org.junit.Test;

/**
 * @Package com.seo.test.nio
 * @Description: TODO (  )
 * @Author rxbyes
 * @Date 2017 下午2:06
 * @Version V1.0
 */
public class ConventStringToBooleanTest {


    @Test
    public void toBoolean(){

        String leve = "true";

        Assert.assertEquals(true,Boolean.parseBoolean(leve));

    }
}
