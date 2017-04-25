package com.seo.test.java8;

import com.google.common.collect.Maps;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;

/**
 * @Package com.seo.test.java8
 * @Description: TODO ( 异步编程 )
 * @Author rxbyes
 * @Date 2017 下午10:05
 * @Version V1.0
 */
public class CompletableFutureTest {



    @Test
    public void future() throws ExecutionException, InterruptedException, IOException {


    }


    private static Random random = new Random();
    private static long t = System.currentTimeMillis();

    static String hello() {

        return "hello word!";
    }

    static int getMoreData() {
        System.out.println("begin to start compute");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("end to start compute. passed " + (System.currentTimeMillis() - t) / 1000 + "seconds");
        return random.nextInt(1000);
    }

    public void setTest() {
        //        Random rand = new Random();
//        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
//            try {
//                Thread.sleep(10 + rand.nextInt(1000));
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            return 100;
//        });
//        CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(() -> {
//            try {
//                Thread.sleep(10 + rand.nextInt(1000));
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            return 200;
//        });
//        CompletableFuture<String> f =  future.applyToEither(future2,i -> i.toString());
//
//        System.out.println(f.get());


//        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> 100);
//
//        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> "abc");
//
//        CompletableFuture<String> cf = future.thenCombine(future1,(x,y) -> y  +"-" + x);
//
//        System.out.println(cf.get());


//        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
//            return 100;
//        });
//        CompletableFuture<String> f =  future.thenCompose( i -> {
//            return CompletableFuture.supplyAsync(() -> {
//                return (i * 10) + "";
//            });
//        });
//        System.out.println(f.get()); //100


//        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> 100);
//
//        CompletableFuture<Void> cf = future.thenAcceptBoth(CompletableFuture.completedFuture(10), (x, y) -> System.out.print(x * y));
//
//        cf.get();

//        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> 1000);

//        CompletableFuture<String> f = future.thenApplyAsync(i -> i * 10).thenApply(i -> i.toString());

//        System.out.println(f.get()); //"1000"


//        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> 100*2);
//        future1.thenAccept(System.out::println);


//        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(CompletableFutureTest::hello);

//        CompletableFuture<String> fs = future2.thenApplyAsync(a -> "liming").thenApply(b -> "zhangsan");

//       CompletableFuture<String> ss = future2.whenCompleteAsync((v,e) -> {
//            System.out.println(v);
//            System.out.println(e);
//        });

//        System.out.println(ss.get());
//        System.out.println(fs.get());

//        System.out.println(future1.get());

//        whenComplete();
    }

    public void whenComplete() throws InterruptedException, ExecutionException {
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync((CompletableFutureTest::getMoreData));

        Future<Integer> f = future.whenComplete((v, e) -> {

            System.out.println(v);
            System.out.println(e);
        });

        System.out.println(f.get());
//        System.in.read();
    }


    public static CompletableFuture<Integer> compute() {
        final CompletableFuture<Integer> future = new CompletableFuture<>();
        return future;
    }

    public void ChannelFuture() {
        Bootstrap bootstrap = new Bootstrap();
        ChannelFuture future = bootstrap.connect(new InetSocketAddress("127.0.0.1", 8090));
        future.addListener((ChannelFutureListener) futures -> {
            if (futures.isSuccess()) {
                System.out.println("s");
            } else {
                System.out.println("f");
            }
        });
    }

    public void BasicFuture() throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        Future<Integer> future = executorService.submit(() -> {

            Thread.sleep(500);
            return 100;
        });

        System.out.println(future.get());
    }

}
