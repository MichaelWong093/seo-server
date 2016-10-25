package com.berchina.esb.server.provider.utils;

/**
 * @Package com.berchina.esb.server.provider.utils
 * @Description: TODO ( 常量类 )
 * @Author 任小斌  renxiaobin@berchin.com
 * @Date 16/8/30 上午11:58
 * @Version V1.0
 */
public class Constants {

    /**
     * 商品搜索标示
     */
    public final static String SEO_GOODS_STATUS = "0000";

    /**
     * 普通商品热词搜索标示
     */
    public final static String SEO_GOODS_HOTWORDS_STATUS = "1000";
    /**
     * 普通商品热词推荐
     */
    public final static String SEO_GOODS_RECOMMEND_STATUS = "1010";

    /**
     * 商铺热词搜索标示
     */
    public final static String SEO_SHOP_HOTWORDS_STATUS = "1001";

    /**
     * 店铺搜索标示
     */
    public final static String SEO_SHOP_STATUS = "2000";

    /**
     * 类目标示
     */
    public final static String SEO_CATEGORY_STATUS = "3000";

    /**
     * 搜索热词
     */
    public final static String SEO_HOT_WORDS = "hotwords";

    /**
     * 起始条数
     */
    public final static String SEO_START = "start";

    /**
     * 排序规则: 默认 DESC
     */
    public final static String SEO_RULE = "rule";

    /**
     * 结束条数
     */
    public final static String SEO_ROWS = "rows";

    /**
     * 索引分页标示,输入 index 关键词标示分页,否则相反
     */
    public final static String SEO_INDEX = "index";

    /**
     * 搜索热词渠道
     */
    public final static String SEO_CHANNEL = "channel";

    /**
     * 品牌
     */
    public final static String SEO_BRAND = "brand";

    /**
     * 排序
     */
    public final static String SEO_SORT = "sort";


    /**
     * 排序字段: 销量
     */
    public final static String SEO_SALES = "sales";

    /**
     * 属性
     */
    public final static String SEO_ATTRIBUTE = "attr";


    /**
     * 设备来源
     */
    public final static String SEO_TERMINAL = "terminal";

    /**
     * 类目基类编号
     */
    public final static String SEO_PARENTID = "parentid";

    public final static String SEO_PROPID = "propid";

    public final static String SEO_SKU = "sku";

    /**
     * 其它
     */
    public final static String SEO_OTHE = "other";

    /**
     * 普通商品搜索  Collection 名称
     */
    public final static String SEO_GOODS_ = "goods";

    /**
     * 普通热词搜索 Collection 名称
     */
    public final static String SEO_GOODS_HOTWD_ = "gdhotwd";

    /**
     * 商铺热词搜索 Collection 名称
     */
    public final static String SEO_SHOP_HOTWD_ = "sphotwd";

    /**
     * 商铺搜索 Collection 名称
     */
    public final static String SEO_SHOP_ = "shop";


    /**
     * 前台类目搜索 Collection 名称
     */
    public final static String SEO_CATEGORYS_SHOP_ = "categorys";

    public final static String SEO_CATEGORY_SHOP_ = "category";

    public final static String SEO_CATEREV_SHOP_ = "caterev";


    /**
     * 后台展示类目 Collection 名称
     */
//    public final static String SEO_CATEGORY_SHOP_ = "category";


    /**
     * 商品实例类
     */
    public final static String SEO_GOODS_SERVER = "seoGoodsServer";

    /**
     * 商品推荐实例类
     */
    public final static String SEO_GOODS_RECOMMEND_SERVER = "seoGoodsRecommendServer";
    /**
     * 商铺热词联想实例类
     */
    public final static String SEO_HOT_WORDS_SERVER = "seoHotWordServer";

    /**
     * 类目实例类
     */
    public final static String SEO_CATEGORY_SERVER = "seoCategoryServer";

    /**
     * 商铺实例类
     */
    public final static String SEO_SHOP_SERVER = "seoShopServer";

    /**
     * 消费者调用服务端验证,允许访问的URL可以通过该地址访问,否则拒绝
     */
    public static final String SEO_SYSTEM_REQUEST_ADAPTER = "seo.system.request.adapter";

    /**
     * 搜索服务器地址
     */
    public static final String PROPERTY_NAME_SOLR_SERVER_URL = "spring.data.solr.host";

    /**
     * Solr 全量数据阀值,默认关闭
     */
    public static final String SEO_TASK_SYN = "seo.task.syn";

    /**
     * Solr 应用名称
     */
    public static final String SEO_TASK_APP = "seo.task.app";

    /**
     * Solr 应用端口
     */
    public static final String SEO_TASK_PORT = "seo.task.port";

    /**
     * Solr 服务地址
     */
    public static final String SEO_TASK_SERVER = "seo.task.server";

    /**
     * Solr Collection Core
     */
    public static final String SEO_TASK_COLLECTION = "seo.task.collection";

    /**
     * Solr 全量数据 URL 地址
     */
    public static final String SEO_TASK_ADDRESS = "seo.task.address";


    /*
     * 热词联想 搜索默认的返回记录数 为 6; 默认热词 查询词频高的前6条记录
     */
    public static final Integer DEFALT_ROWS = 6;

    /**
     * 条数
     */
    public static final Integer THIRTH = 30;

    /*
     * solr 建立的索引字段 商品名称
     */
    public static final String INDEX_GOODSNAME = "goodsName";

    /*
     * solr 建立的索引字段 店铺名称
     */
    public static final String INDEX_SHOPNAME = "shopName";

    /*
     * 不存在的 东东（^_^）
     */
    public static final String NOT_EXIST = "N/A";

    public static final String DESC = "desc";

    public static final String ASC = "asc";

    public static final String SPACE = " ";

    public static final Integer ZERO = 0;

    /*
     * 冒号
     */
    public static final String COLON = ":";

    public static final String ASTERISK = "*";

    public static final String COLON_ASTERISK = "*:*";

    public static final Integer SEG_LEN = 4;


    public static final String SEO_PAGE_SIZE = "pageSize";

    public static final String SEO_CURRENT_PAGE = "currentPage";

    /*
     * 热词搜索的索引字段
     */
    public static final String HW_SUGGEST = "suggest";
    /*
     * 文件已经读取的后缀标识
     */
    public static final String FINISH_READ = ".read";
    /*
     * 加载所有热词 包括历史的热词
     */
    public static final String FULL_LOAD = "full_load";
    /*
     * 只加载未加载过的热词
     */
    public static final String UNREAD_LOAD = "unread_load";

    public static final String REDA_LOG_PATH = "seo.task.logs";

    /*
     * log 文件后缀名
     */
    public static final String LOG_SUFFIX = ".log";

    /*
     * 商品 推荐 top 5
     */
    public static final Integer TOP_GOODS_RCM_NUM = 5;
}
