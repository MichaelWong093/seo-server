package com.seo.test.java8;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
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

        final CompletableFuture<Integer> future = compute();

        new Client("client1",future);
        new Client("client2",future);

        System.out.println("waiting");

        future.complete(100);

        System.in.read();
    }


    class Client extends Thread{
        CompletableFuture<Integer> future;
        Client(String threadName, CompletableFuture<Integer> future){
            super(threadName);
            this.future = future;
        }
        @Override
        public void run(){
            try {
                System.out.println(this.getName() + " : " +future.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    public static CompletableFuture<Integer> compute() {
        final CompletableFuture<Integer> future = new CompletableFuture<>();
        return future;
    }

    public void ChannelFuture() {
        Bootstrap bootstrap = new Bootstrap();
        ChannelFuture future = bootstrap.connect(new InetSocketAddress("127.0.0.1", 8090));
        future.addListener((ChannelFutureListener) futures ->{
            if (futures.isSuccess()){
                System.out.println("s");
            }else {
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
