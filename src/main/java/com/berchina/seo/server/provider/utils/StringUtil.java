package com.berchina.seo.server.provider.utils;

import com.alibaba.fastjson.JSON;
import com.berchina.seo.server.configloader.exception.SeoException;
import com.berchina.seo.server.configloader.exception.server.ServerException;
import com.google.common.base.Splitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Package com.berchina.seo.server.provider.utils
 * @Description: TODO ( 字符串操作工具类 )
 * @Author 任小斌  renxiaobin@berchin.com
 * @Date 16/8/30 上午11:54
 * @Version V1.0
 */
public class StringUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(StringUtil.class);

    public static List<String> splitter(String splitter, String correlation) {

        return Splitter.on(splitter).omitEmptyStrings().omitEmptyStrings().trimResults().splitToList(correlation);
    }

//
//    /**
//     * 文件读取
//     *
//     * @param path 文件路径，绝对路径
//     * @return 字符流
//     */
//    public static Set<String> readBuffer(String path) {
//        Set<String> set = Sets.newLinkedHashSet();
//        long start = System.currentTimeMillis();
//        try {
//            RandomAccessFile file = new RandomAccessFile(path, "rw");
//            //  从 RandomAccessFile 获取文件通道 FileChannel
//            FileChannel channel = file.getChannel();
//            //  文件大小
//            long size = channel.size();
//            // 构建一个只读的MappedByteBuffer
//            MappedByteBuffer byteBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, size);
//            int allocate = 1024;
//            // 文件内容大时，可以采用多次方式读取, 每次取 1M 数据
//            byte[] bytes = new byte[allocate];
//            long cycle = size / allocate;
//            int mode = (int) (size % allocate);
//            for (int i = 0; i < cycle; i++) {
//                byteBuffer.get(bytes);
//                set.add(new String(bytes, "UTF-8"));
//            }
//            if (mode > 0) {
//                bytes = new byte[mode];
//                byteBuffer.get(bytes);
//                set.add(new String(bytes, "UTF-8"));
//            }
//            LOGGER.info("文件大小 (b): {}，耗时 (ms): {}", size, System.currentTimeMillis() - start);
//            // 关闭通道和文件流
//            channel.close();
//            file.close();
//            byteBuffer.clear();
//            unmap(byteBuffer);
//            return set;
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    private static void unmap(MappedByteBuffer buffer) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        // FileChannel在调用了map方法，进行内存映射得到MappedByteBuffer，但是没有提供unmap方法()，释放内存。
//        // 事实上，unmap方法是在FileChannelImpl类里实现的，是个私有方法。在finalize延迟的时候，unmap方法无法调用，
//        // 在删除文件的时候就会因为内存未释放而失败。不过可以通过显示的调用unmap方法来释放内存。
//        Method method = FileChannelImpl.class.getDeclaredMethod("unmap", MappedByteBuffer.class);
//        method.setAccessible(true);
//        method.invoke(FileChannelImpl.class, buffer);
//    }


    /**
     * 根据搜索渠道获取业务实例
     *
     * @param channel 搜索渠道
     * @return 业务实例名称
     */
    public static String getSolrInstance(final String channel) {

        if (!StringUtils.isEmpty(channel)) {
            return
                    channel.equals(Constants.SEO_GOODS_STATUS) ? Constants.SEO_GOODS_ :

                            channel.equals(Constants.SEO_GOODS_HOTWORDS_STATUS) ? Constants.SEO_GOODS_HOTWD_ :

                                    channel.equals(Constants.SEO_SHOP_HOTWORDS_STATUS) ? Constants.SEO_SHOP_HOTWD_ :

                                            channel.equals(Constants.SEO_SHOP_STATUS) ? Constants.SEO_SHOP_ :

                                                    channel.equals(Constants.SEO_CATEGORY_STATUS) ? Constants.SEO_CATEGORYS_SHOP_ :

                                                            channel.equals(Constants.SEO_GOODS_RECOMMEND_STATUS) ? Constants.SEO_GOODS_HOTWD_ : "";
        }
        return null;
    }

    /**
     * 处理业务请求
     *
     * @param channel 业务标示
     * @return 请求实例对象
     */
    public static String setBussinesRequestMethod(final String channel) {
        if (!StringUtils.isEmpty(channel)) {
            return
                    channel.equals(Constants.SEO_GOODS_STATUS) ? Constants.SEO_GOODS_SERVER :

                            channel.equals(Constants.SEO_GOODS_HOTWORDS_STATUS) ? Constants.SEO_HOT_WORDS_SERVER :

                                    channel.equals(Constants.SEO_SHOP_STATUS) ? Constants.SEO_SHOP_SERVER :

                                            channel.equals(Constants.SEO_SHOP_HOTWORDS_STATUS) ? Constants.SEO_HOT_WORDS_SERVER :

                                                    channel.equals(Constants.SEO_CATEGORY_STATUS) ? Constants.SEO_CATEGORY_SERVER :

                                                            channel.equals(Constants.SEO_GOODS_RECOMMEND_STATUS) ? Constants.SEO_GOODS_RECOMMEND_SERVER : "";
        }
        return null;
    }

    /**
     * 获取请求 URI 链接地址如: http://127.0.0.1:9000/seo/goods/, 则获取 /seo/goods/地址
     *
     * @param httpServletRequest 请求业务对象
     * @return 字符串 URI
     */
    public static String getRequestURI(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getRequestURI();
    }


    public static boolean notNull(List object){
        if (null != object && object.size() > 0)
        {
            return true;
        }
        return false;
    }

    /**
     * 类型转化
     *
     * @param var 转化类型
     * @return 字符串类型
     */
    public static String StringConvert(Object var) {
        if (StringUtils.isEmpty(var)) {
            return null;
        }
        return String.valueOf(var);
    }

    public static Integer IntegerConvert(Object var) {
        if (StringUtils.isEmpty(var)) {
            return 0;
        }
        return Integer.valueOf(StringConvert(var));
    }

    public static Integer StringConvert(String var) {
        if (StringUtils.isEmpty(var)) {
            return null;
        }
        return Integer.valueOf(var);
    }


    /**
     * 设置参数不能为空
     *
     * @param args
     */
    public static boolean notNull(String args) {
        if (StringUtils.isEmpty(args)) {
            return false;
        }
        return true;
    }


    /**
     * 设置参数不能为空
     *
     * @param args
     */
    public static boolean notNull(Object args) {
        if (!StringUtils.isEmpty(args)) {
            return true;
        }
        return false;
    }

    /**
     * 在请求字符流中获取输入参数
     *
     * @param request 请求输入参数
     * @return 字符串
     */
    public static String requestDataConvert(HttpServletRequest request) {
        StringBuilder builder = new StringBuilder();
        try {
            String line = null;
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    request.getInputStream()));
            while (!StringUtils.isEmpty((line = in.readLine()))) {
                builder.append(line);
            }
            return setRequestData(request, builder);
        } catch (IOException e) {
            LOGGER.error("[ 请求数据格式转化异常: {}]", e.getMessage());
            throw new SeoException(e.getMessage(), ServerException.SEO_REQ_STYLE_CONVERT_ERROR);
        }
    }

    /**
     * 解析请求资源
     *
     * @param request 请求参数
     * @param builder 组装解析字符串
     * @return JSON 字符串
     */
    private static String setRequestData(HttpServletRequest request,
                                         StringBuilder builder) {
        try {
            return !StringUtils.isEmpty(builder.toString()) ? String.valueOf(builder)
                    : JSON.toJSONString(request.getParameterMap()).replace("[", "").replace("]", "");
        } catch (Exception ex) {
            LOGGER.error("[ 请求数据格式转化异常: {}]", ex.getMessage());
            throw new SeoException(ex.getMessage(), ServerException.SEO_REQ_STYLE_CONVERT_ERROR);
        }
    }

    /*
     * 生成唯一的id
     */
    public static String uniqueId() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "");
    }

    /*
     * 是否是中文
     */
    private static final boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS;
    }

    public static final boolean isChinese(String strName) {
        char[] ch = strName.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (isChinese(c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 特殊字符替换
     *
     * @param str 输入字符串
     * @return
     */
    public static String replaceBlank(String str) {
        String dest = "";
        if (!StringUtils.isEmpty(str)) {
            Pattern p = Pattern.compile("\\s*|t|r|n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    //获取mac地址
//    public static String getLocalMac() {
//        try {
//            InetAddress ia = InetAddress.getLocalHost();
//            byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
//            StringBuffer sb = new StringBuffer();
//            for (int i = 0; i < mac.length; i++) {
//                if (i != 0) {
//                    sb.append("-");
//                }
//                int tmp = mac[i];
//                String str = Integer.toHexString(tmp & 0xFF);
//                if (str.length() == 1) {
//                    sb.append("0" + str);
//                } else {
//                    sb.append(str);
//                }
//            }
//            return sb.toString().toUpperCase();
//        } catch (SocketException e) {
//            LOGGER.error("[ 获取mac地址异常]", e.getMessage());
//        }
//        return uniqueId();//如果获取不到给一个 默认序列
//    }

    //获取项目的 热词log夹文件路径
    public static String getHwLogPath() {
        String path = StringUtil.class.getClassLoader().getResource("").getPath();
        int end = path.length() - ("WEB-INF" + File.separator + "classes" + File.separator).length();
        if ("\\".equals(File.separator)) {
            path = path.substring(1, end);
        }
        if ("/".equals(File.separator)) {
            path = path.substring(0, end);
        }
        path += File.separator + "seo-logs" + File.separator + "suggest" + File.separator;
        path = path.replaceAll("\\\\", "\\/");
        return path;
    }

    /*
     * 获取今天的年与日 字符串 以 - 分割
     */
    public static String getNowDayStr() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }
}
