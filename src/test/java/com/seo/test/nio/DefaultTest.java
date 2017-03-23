package com.seo.test.nio;

import java.util.function.Supplier;

/**
 * @Package com.seo.test.nio
 * @Description: TODO (  )
 * @Author rxbyes
 * @Date 2017 上午11:34
 * @Version V1.0
 */
public interface DefaultTest {

    static DefaultTest create(Supplier<DefaultTest> testSupplier){
        return testSupplier.get();
    }

    default String hello(){
        return "hello liming";
    }

    String print();

    class  DefaultTestImpl implements  DefaultTest{

        @Override
        public String print() {
            return "ok";
        }
    }

    class  DefaultTestImpls implements  DefaultTest{
        @Override
        public String hello(){
            return "你好啊？";
        }
        @Override
        public String print(){
           return "are you ok!";
        }
    }

    static void main(String[] args) {


        DefaultTest defaultTest = DefaultTest.create(DefaultTestImpl::new);

        System.out.println(defaultTest.hello());

        DefaultTest test = DefaultTest.create(DefaultTestImpls::new);

        System.out.println(test.print());

    }
}
