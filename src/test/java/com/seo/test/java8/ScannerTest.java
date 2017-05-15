package com.seo.test.java8;

import com.hankcs.hanlp.HanLP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Package com.seo.test.java8
 * @Description: TODO (  )
 * @Author rxbyes
 * @Date 2017 下午9:36
 * @Version V1.0
 */
@SpringBootApplication
public class ScannerTest implements CommandLineRunner {

    public static void main(String[] args) {

        SpringApplication.run(ScannerTest.class,args).close();
    }

    String files = "/Users/rxbyes/Documents/shopName.txt";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void run(String... strings) throws Exception {

        stream(System.currentTimeMillis());
    }

    //    @Test
    public void scanner() {

        String file = "/Users/rxbyes/Desktop/商品名称_20170313.rtf";

        long start = System.currentTimeMillis();

//        stream(files, start);
//        scanner(files, start);
//        reader(files, start);
//        buffer(files, start);

    }

    public void buffer(String files, long start) {
        try (BufferedReader reader = new BufferedReader(new FileReader(files))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            long end = System.currentTimeMillis() - start;
            System.out.printf("结束：" + end);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reader(String files, long start) {
        List<String> list = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(files))) {
            list = reader.lines().collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis() - start;
        list.forEach(System.out::println);
        System.out.printf("结束：" + end);
    }

    public void scanner(String files, long start) {
        try (Scanner scanner = new Scanner(new File(files))) {
            while (scanner.hasNext()) {
                System.out.println(scanner.nextLine());
            }
            long end = System.currentTimeMillis() - start;
            System.out.printf("结束：" + end);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void stream(long start) {

        HanLP.Config.setRedisTemplate(redisTemplate);

        try (Stream<String> stream = Files.lines(Paths.get(files), Charset.defaultCharset())) {

            stream.forEach((s) -> {
//                List<String> list = HanLP.extractKeyword(s,3);

                List<String> list = HanLP.extractPhrase(s,6);

                list.forEach(System.out::println);
            });

            long end = System.currentTimeMillis() - start;

            System.out.printf("结束：" + end);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
