package com.berchina.seo.server.provider.server.impl;

import com.berchina.seo.server.provider.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @Package com.berchina.seo.server.provider.server.impl
 * @Description: TODO ( 手动初始化分词库 )
 * @Author rxbyes
 * @Date 2017 上午11:01
 * @Version V1.0
 */
@Service
public class KeyWordsSplitterServer {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private Environment environment;

    /**
     * @param pcpType
     * @return
     */
    public void participle(String pcpType) throws IOException {
        if (pcpType.equals("stop")) {

            putOpsForHash(environment.getProperty(Constants.SEO_KEY_STOP), pcpType, "a", "\\s");
        } else if (pcpType.equals("core")) {

            putOpsForHash(environment.getProperty(Constants.SEO_KEY_CORE), pcpType, "b", "\\s");
        } else if (pcpType.equals("custom")) {

            putOpsForHash(environment.getProperty(Constants.SEO_KEY_CUSTOM), pcpType, "b", "\\s");
        } else if (pcpType.equals("table")) {

            putOpsForHash(environment.getProperty(Constants.SEO_KEY_TABLE), pcpType, "c", "=");
        }else if (pcpType.equals("pinyin")){

            putOpsForHash(environment.getProperty(Constants.SEO_KEY_PINYIN), pcpType, "c", "=");
        }
    }

    /**
     * 读取历史热词数据，直接入库存储 redis
     *
     * @param path     文件地址
     * @param type     词典类型 a ：核型词典，b：自定义词典，c：其他类型 char_table
     * @param operator 操作符 换行：\\s  等于 =
     * @param alias    redis Hash 名称
     * @throws IOException
     */
    void putOpsForHash(String path, String alias, String type, String operator) throws IOException {
        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
        String line;
        while (null != (line = reader.readLine())) {
            if (StringUtils.isEmpty(operator)) {

                hashOps.put(alias, line, line);
            } else {
                String[] param = line.split(operator);
                /**
                 * 停止分词类型
                 */
                if (type.equals("a")) {
                    hashOps.put(alias, param[0], param[0]);
                }
                /**
                 * 核心分词、自定义分词类型
                 */
                if (type.equals("b")) {
                    hashOps.put(alias, param[0], param[1] + "=" + param[2]);
                }
                /**
                 * 矫正分词类型
                 */
                if (type.equals("c")) {
                    hashOps.put(alias, param[0], param[1]);
                }
            }
        }
    }
}
