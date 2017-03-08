package com.berchina.esb.server.provider.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.berchina.esb.server.provider.server.crud.SegmentIndexRepository;
import com.berchina.esb.server.provider.utils.Constants;

/**
 * @Package com.berchina.esb.server.provider.task
 * @Description: TODO (索引的定时任务处理 )
 * @Author yanhuiqing
 * @Date 2016年9月16日 下午3:05:28
 * @Version V1.0
 */
@Component
public class SolrIndexSheduleTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(SolrIndexSheduleTask.class);

    @Autowired
    private Environment environment;

    @Autowired
    private SegmentIndexRepository segmentIndex;

    //文件加载的list
    private static List<String> keyWordsList = new ArrayList<String>();

    public static List<String> getKeyWordsList() {
        return keyWordsList;
    }

    //	  @Scheduled(cron = "0/59 * * * * ?")
    @Scheduled(fixedDelay = 50000)
    public void solrIndexLoader() {
        LOGGER.info("====halley=====开始加载索引..." + new Date());
        long now = System.currentTimeMillis();
        //增量读取
        FileReaderExecutor reader = new FileReaderExecutor(
                Constants.UNREAD_LOAD, environment.getProperty(Constants.REDA_LOG_PATH));
        //全量读取
        //FileReaderExecutor reader = new FileReaderExecutor(Constants.FULL_LOAD);
        reader.read();

        for (String s : keyWordsList) {
            LOGGER.info("读取的文件内容为：[" + s + "]");
        }

        Map<String, List<String>> map = reader.classifyData(keyWordsList);
        //生成 商品历史搜索 索引
        //segmentIndex.createIndex(map.get(Constants.SEO_GOODS_HOTWD_HIS), Constants.SEO_GOODS_HOTWD_);
        //生成 店铺历史搜索 索引
        //segmentIndex.createIndex(map.get(Constants.SEO_SHOP_HOTWD_HIS), Constants.SEO_SHOP_HOTWD_);
        //生成 商品关键字索引
        segmentIndex.segThenCreateIndex(map.get(Constants.SEO_GOODS_HOTWD_), Constants.SEO_GOODS_HOTWD_);
        //生成 店铺关键字索引
        segmentIndex.segThenCreateIndex(map.get(Constants.SEO_SHOP_HOTWD_), Constants.SEO_SHOP_HOTWD_);

        keyWordsList.clear();
        reader = null;
        long end = System.currentTimeMillis();
        LOGGER.info("====halley=====结束加载索引..." + new Date() + ",总共耗时  ：" + (end - now) / 1000 + "秒");
    }
}
