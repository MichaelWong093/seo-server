package com.berchina.seo.server.provider.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.berchina.seo.server.provider.utils.Constants;
import com.berchina.seo.server.provider.utils.IOUtills;
import com.berchina.seo.server.provider.utils.StringUtil;
import com.hankcs.hanlp.corpus.util.StringUtils;

/**
 * @Package com.berchina.seo.server.provider.task
 * @Description: TODO (专门处理读取文件的执行器 )
 * @Author yanhuiqing
 * @Date 2016年9月16日 下午3:46:54
 * @Version V1.0
 */
public class FileReaderExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileReaderExecutor.class);
    //文件名称包含路径
    private List<String> fileNames;

    //加载文件的方式 ，全部加载 还是 ，只加载未读的
    private String loadStyle;

    private boolean isLoadToday = true;//默认不加载当天生成的关键字 文件

    //private static String path = "E:\\workspace\\seo-server\\seo-logs\\suggest\\";

    //    private static String path = StringUtil.getHwLogPath();
    private static String path;

    public FileReaderExecutor(String loadStyle, String paths) {
        this.loadStyle = loadStyle;
        this.path = paths;
        this.fileNames = IOUtills.listFileWithPath(path);
        proccessFileName();
    }

    //处理文件名称
    private void proccessFileName() {
        if (this.loadStyle.equals(Constants.FULL_LOAD)) {
            for (int i = 0; i < fileNames.size(); i++) {//重命名已读的文件为未读文件
                String fileName = fileNames.get(i);
                if (IOUtills.isThisSuffix(fileName, Constants.FINISH_READ)) {
                    String tmp = fileName.substring(0, fileName.indexOf(Constants.FINISH_READ));
                    IOUtills.renameFile(fileName, tmp);

                }
            }
            this.fileNames.clear();
            this.fileNames = IOUtills.listFileWithPath(path);//再次加载文件 保证文件读取的正确性
            if (!isLoadToday) {
                removeTodayFile(fileNames);
            }
            return;
        } else if (this.loadStyle.equals(Constants.UNREAD_LOAD)) {
            for (int i = 0; i < fileNames.size(); i++) {//只读 未读的文件
                String fileName = fileNames.get(i);
                if (IOUtills.isThisSuffix(fileName, Constants.FINISH_READ)) {
                    fileNames.remove(i);
                }
            }
            this.fileNames.clear();
            this.fileNames = IOUtills.listFileWithPath(path);//再次加载文件 保证文件读取的正确性
            if (!isLoadToday) {
                removeTodayFile(fileNames);
            }
            return;
        } else {
        }
    }

    /*
     * 去除 今天的生成的文件
     */
    private void removeTodayFile(List<String> fileNames) {
        for (int i = 0; i < fileNames.size(); i++) {//重命名已读的文件为未读文件
            String fileName = fileNames.get(i);
            if (fileName.indexOf(StringUtil.getNowDayStr()) > 0) {
                fileNames.remove(i);
            }
        }

    }

    //读取文件内容
    public List<String> readFile(String fileName) {
        if (StringUtils.isBlankOrNull(fileName)) throw new RuntimeException("文件找不到：" + fileName);
        LOGGER.info("正在读取文件内容的 文件是：" + path + fileName);
        return IOUtills.readLineList(fileName);
    }

    //开始读取的逻辑
    public void read() {

        if (null != fileNames && fileNames.size() > 0) {
            for (String fileName : fileNames) {
                if (IOUtills.isThisSuffix(fileName, Constants.LOG_SUFFIX)) {
                    SolrIndexSheduleTask.getKeyWordsList().addAll(readFile(fileName));
                    IOUtills.renameFile(fileName, fileName + Constants.FINISH_READ);//标记为已读
                }

            }
        }
    }

    //将文件中的内容分类为 商品关键字和 店铺关键字
    public Map<String, List<String>> classifyData(List<String> keyWordsList) {
        List<String> gdkeyWords = new ArrayList<String>();
        List<String> spkeyWords = new ArrayList<String>();
        //历史搜索 包含mac地址
        //List<String> gdkeyWordsHis = new ArrayList<String>();
        //List<String> spkeyWordsHis = new ArrayList<String>();
        Map<String, List<String>> map = Maps.newHashMap();
        for (String words : keyWordsList) {
            if (words.indexOf(Constants.COLON) > 0) {
                String[] _array = words.split(Constants.COLON);
                if (Constants.SEO_GOODS_HOTWD_.equals(_array[0])) {

                    gdkeyWords.add(_array[1].trim());
                    //if(_array.length<3)continue;//防止获取mac为空的情况
                    //gdkeyWordsHis.add(_array[2].trim()+Constants.COLON+_array[1].trim());
                }
                if (Constants.SEO_SHOP_HOTWD_.equals(_array[0])) {
                    spkeyWords.add(_array[1].trim());
                    //if(_array.length<3)continue;
                    //spkeyWordsHis.add(_array[2].trim()+Constants.COLON+_array[1].trim());
                }
            }
        }
        map.put(Constants.SEO_GOODS_HOTWD_, gdkeyWords);
        map.put(Constants.SEO_SHOP_HOTWD_, spkeyWords);

        //map.put(Constants.SEO_GOODS_HOTWD_HIS, gdkeyWordsHis);
        //map.put(Constants.SEO_SHOP_HOTWD_HIS, spkeyWordsHis);
        return map;
    }
}
