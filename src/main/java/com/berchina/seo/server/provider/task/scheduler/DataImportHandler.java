package com.berchina.seo.server.provider.task.scheduler;

import com.berchina.seo.server.provider.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;

/**
 * @Package com.berchina.seo.server.provider.task.scheduler
 * @Description: TODO ( Solr 全量数据定时导入 )
 * @Author 任小斌  renxiaobin@berchin.com
 * @Date 16/10/12 下午9:29
 * @Version V1.0
 */
@Component
public class DataImportHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataImportHandler.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private Environment environment;

    /**
     * <p>
     *
     * @Scheduled(fixedRate = 5000) ：上一次开始执行时间点之后5秒再执行
     * <p>
     * @Scheduled(fixedDelay = 5000) ：上一次执行完毕时间点之后5秒再执行
     * <p>
     * @Scheduled(initialDelay=1000, fixedRate=5000) ：第一次延迟1秒后执行，之后按fixedRate的规则每5秒执行一次
     * <p>
     * @Scheduled(cron="* /5 * * * * *") ：通过cron表达式定义规则
     * <p>
     * "0 0 08 * * ?" 每天上午8点触发
     * "0 15 10 ? * *" 每天上午10:15触发
     * "0 15 10 * * ?" 每天上午10:15触发
     * "0 15 10 * * ? *" 每天上午10:15触发
     * "0 15 10 * * ? 2005" 2005年的每天上午10:15触发
     * "0 * 14 * * ?" 在每天下午2点到下午2:59期间的每1分钟触发
     * "0 0/5 14 * * ?" 在每天下午2点到下午2:55期间的每5分钟触发
     * "0 0/5 14,18 * * ?" 在每天下午2点到2:55期间和下午6点到6:55期间的每5分钟触发
     * "0 0-5 14 * * ?" 在每天下午2点到下午2:05期间的每1分钟触发
     * "0 10,44 14 ? 3 WED" 每年三月的星期三的下午2:10和2:44触发
     * "0 15 10 ? * MON-FRI" 周一至周五的上午10:15触发
     * "0 15 10 15 * ?" 每月15日上午10:15触发
     * "0 15 10 L * ?" 每月最后一日的上午10:15触发
     * "0 15 10 ? * 6L" 每月的最后一个星期五上午10:15触发
     * "0 15 10 ? * 6L 2009-2019" 2009年至2019年的每月的最后一个星期五上午10:15触发
     * "0 15 10 ? * 6#3" 每月的第三个星期五上午10:15触发
     */
//    @Scheduled(cron = "0 50 09 * * ?")
//    @Scheduled(fixedDelay = 100000)
    public void dataImport() {
        if (this.synSolr(environment)) {
            try {

                setDataImportURL(this.getCore(environment), this.getSolrHome(environment), this.getAddr(environment));

            } catch (IOException e) {
                LOGGER.error("Failed to connect to the specified URL while trying to send HTTP POST,{}", e.getMessage());
            }
        } else {
            LOGGER.info(" Solr 全量索引创建阀值已关闭, 请开启 application.yml 文件 属性值 syn : on ");
        }
    }


    private void setDataImportURL(String[] args, String home, String addr) throws IOException {
        for (String core : args) {
            sendHttpPost(home.concat(core).concat(addr));
        }
    }

    private void sendHttpPost(String solrUrl) throws IOException {
        LOGGER.info(" Solr 创建全量索引, {}", solrUrl);

        URL url = new URL(solrUrl);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("type", "submit");
        conn.setDoOutput(true);

        conn.connect();

        if (conn.getResponseCode() != 200) sendHttpPost(solrUrl);
        conn.disconnect();
    }

    private boolean synSolr(Environment env) {
        return env.getProperty(Constants.SEO_TASK_SYN).equals("true");
    }

    private String[] getCore(Environment env) {
        return env.getProperty(Constants.SEO_TASK_COLLECTION).split(",");
    }

    private String getSolrHome(Environment env) {
        return env.getProperty(Constants.PROPERTY_NAME_SOLR_SERVER_URL);
    }

    private String getAddr(Environment env) {
        return env.getProperty(Constants.SEO_TASK_ADDRESS);
    }

}
