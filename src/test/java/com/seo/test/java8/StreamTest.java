package com.seo.test.java8;

import java.util.Arrays;
import java.util.List;

/**
 * @Package com.seo.test.java8
 * @Description: TODO (  )
 * @Author rxbyes
 * @Date 2017 下午11:31
 * @Version V1.0
 */
public class StreamTest {

    public static void main(String[] args) {

         new Thread(() -> System.out.println("hello world !")).start();

        List features = Arrays.asList("Lambdas", "Default Method", "Stream API",
                "Date and Time API");
        features.forEach(n -> System.out.println(n));

    }
}
