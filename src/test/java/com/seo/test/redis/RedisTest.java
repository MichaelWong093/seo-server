package com.seo.test.redis;

import com.google.common.base.Joiner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.Assert;

import java.util.Set;

/**
 * @Package com.seo.test.redis
 * @Description: TODO ( redis test )
 * @Author rxbyes
 * @Date 2017 下午5:07
 * @Version V1.0
 */
@SpringBootApplication
public class RedisTest implements CommandLineRunner {

    private static final String LINE_FEED = "\r\n";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void run(String... strings) throws Exception {

        ValueOperations<String, String> vOps = redisTemplate.opsForValue();

        vOps.set("棒棒"," nz 234");
    }

    private void appendKeyWordToRedis(ValueOperations<String, String> ops, String key, String value) {
        Assert.notNull(key, " this key must not be empty");
        Assert.notNull(value, " this value must not be empty");
        //在 redis 中追加文本内容，方便热词手动维护
        ops.append(key, value.concat(LINE_FEED));
    }

    private void readCoreStopWord(ValueOperations<String, String> ops, String path, String key) {
        // 读取固定文件内容
//        Set<String> set = StringUtil.readBuffer(path);
        // 从固定文件保存到 redis 缓存中
//        setJoiner(ops, set, key);
    }


    private void setJoiner(ValueOperations<String, String> ops, Set<String> set, String key) {
        Assert.notNull(key, " this key must not be empty");
        ops.set(key, Joiner.on("\n").skipNulls().join(set.iterator()));
    }

    private void readCoreStopWord(ValueOperations<String, String> ops, Set<String> set, String key) {
        setJoiner(ops, set, key);
    }

    public static void main(String[] args) {

        SpringApplication.run(RedisTest.class, args).close();
    }
}
