package com.seo.test.sku;

import com.alibaba.fastjson.JSON;
import com.berchina.seo.server.provider.client.SeoRequest;
import com.berchina.seo.server.provider.utils.SolrUtils;
import com.berchina.seo.server.provider.utils.StringUtil;
import com.google.common.collect.Sets;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.DisMaxParams;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.junit.Test;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @Package com.seo.test.sku
 * @Description: TODO ( 商品属性与销售属性聚合 )
 * @Author rxbyes
 * @Date 2017 上午9:00
 * @Version V1.0
 */
public class SkuTest {

    public static final String SOLR_HOME = "http://127.0.0.1:8983/solr/";

    private SolrQuery query = new SolrQuery();

    private ModifiableSolrParams params = new ModifiableSolrParams();

    private final static String FACET_CATEGORY = "category";
    private final static String FACET_LOGISTICS = "logistics";
    private final static String FACET_VID = "vid";

    @Test
    public void sku()
    {
        HttpSolrClient solrClient = new HttpSolrClient.Builder(SOLR_HOME).build();

        sku(solrClient);
    }

    private void sku(HttpSolrClient solrClient)
    {
        SeoRequest request = new SeoRequest();

        request.setHotwords("手机");

        query.clear();
        params.clear();

        query.setQuery(request.getHotwords());
        query.add(CommonParams.DF, "hotwords");
//        query.setFields("hotwords", "category");
        // @rxbyes 新增 vid 分组
        query.addFacetField("category","logistics","vid");
        query.setFacet(true);
        query.setFacetLimit(10);

        query.add("synonyms","true");
        query.add("defType","synonym_edismax");
        query.add(DisMaxParams.MM,"100%");


        query.setStart(Integer.valueOf(request.getStart()));
        query.setRows(Integer.valueOf(request.getRows()));

        // 测试只返回商品名称
        query.setFields("hotwords");

        SolrUtils.setSolrPage(query, request);

        params.add(query);

        try {
            QueryResponse response =  solrClient.query("goods",params);

//            SolrDocumentList docGoods = response.getResults();

//            docGoods.forEach((d) -> {System.out.println(d.get("hotwords"));});
            /**
             *
             *  sku 属性聚合
             *
             *  实现思路：
             *
             *  1、根据商品名称 聚合 所属商品类目 （商品类目集合）
             *
             *  2、根据商品名称 聚合 所属SKU属性 （包含商品属性、销售属性）（SKU集合）
             *
             *  3、通过 1 商品类目集合 过滤 2 SKU集合
             *
             */

            /** 商品所属系统类目 （目前）取10个系统类目，根据所属商品最多的前 10 个系统类目 */

            List<String> categories = sysCategory(response,"category");

            // query sku collection solrquery
            skuQuery(skuFacetField(response,"vid"),query);

            // 根据商品 vid 获取 sku 列表
            SolrDocumentList skuDoc = solrClient.query("sku",query).getResults();

            //  过滤 sku 列表根据现有商品 category
            List<Sku> skuList = setSku(categories, skuDoc);

            // 终止开发，牛肉面走起
//            Set treeSet = new TreeSet();

            sku(skuList);

            // 根节点
//            Node root = null;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SolrServerException e) {
            e.printStackTrace();
        }
    }

    public void sku(List<Sku> list){

        Set<Sku> set = Sets.newHashSet();

        set.addAll(list);

//        set.forEach(s ->
//        {
//            SeoCateGory gory = new SeoCateGory();
//            LinkedList<SeoCateGory> attrList = Lists.newLinkedList();
//            list.forEach(u ->
//            {
//                if (s.getPk().equals(u.getPk()))
//                {
//                    SeoCateGory g = new SeoCateGory();
//                    g.setValue(u.getV());
//                    g.setKey(u.getVid());
//                    attrList.add(g);
//                }
//            });
//            gory.setKey(s.getPk());
//            gory.setValue(s.getPv());
//            gory.setChilds(attrList);
//            sku.add(gory);
//        });

        System.out.println(JSON.toJSONString(set));

//        List<SeoCateGory> lst = Lists.newLinkedList();

//        for (int i =0 ; i < list.size(); i++)
//        {
//            Sku sku = list.get(i);

//            System.out.println(sku.getPk());

//            if (sku.getPk().equals(sku.getK()))
//            {
//                SeoCateGory gory = new SeoCateGory();
//                gory.setKey(sku.getPk());
//                gory.setValue(sku.getPv());
//
//                LinkedList<SeoCateGory> childList = new LinkedList<>();
//
//                if (gory.getKey().equals(sku.getK()))
//                {
//                    SeoCateGory childGory = new SeoCateGory();
//
//                    childGory.setKey(sku.getVid());
//                    childGory.setValue(sku.getV());
//                    childList.add(childGory);
//                }
//                gory.setChilds(childList);
//                lst.add(gory);
//            }
//        }
//        set.addAll(lst);

//        System.out.println(JSON.toJSONString(set));
    }


    private List<Sku> setSku(List<String> categories, SolrDocumentList skuDoc) {
        List<Sku> skuList = new LinkedList<>();
        if (StringUtil.notNull(skuDoc)) {
            skuDoc.forEach( doc -> {
                String cat = StringUtil.StringConvert(doc.get("category"));
                categories.forEach(cate -> {
                    if (cat.equals(cate))
                    {
                        Sku sku = new Sku();
                        sku.setVid(StringUtil.StringConvert(doc.get("vid")));
                        sku.setCat(StringUtil.StringConvert(doc.get("category")));
                        sku.setPk(StringUtil.StringConvert(doc.get("propid")));
                        sku.setPv(StringUtil.StringConvert(doc.get("proName")));
                        sku.setV(StringUtil.StringConvert(doc.get("prvName")));
                        sku.setK(StringUtil.StringConvert(doc.get("propids")));
                        skuList.add(sku);
                    }
                });
            });
            System.out.println(" sku attr： "+ JSON.toJSONString(skuList));
        }
        return skuList;
    }

    private List<String> sysCategory(QueryResponse response,String facetName) {
        FacetField fields = response.getFacetField(facetName);
        List<FacetField.Count> catCounts = fields.getValues();
        List<String> catList = new LinkedList<>();
        // 过滤系统类目为空的 category
        if (StringUtil.notNull(catCounts))
        {
            catCounts.forEach(count ->
            {
                if (count.getCount() > 0)
                {
                    catList.add(count.getName());
                }
            });
        }
        System.out.println(" 系统类目：" + catList.toString());
        return catList;
    }

    // sku query
    private void skuQuery(StringBuilder builder,SolrQuery query) {
        query.clear();
        query.set("q", "*:*");
        query.add(CommonParams.DF, "vid");
        query.setRows(1024);
        query.setFilterQueries(new String(builder));
        query.setFields("vid","propid","proName","propids","prvName","category");
        System.out.println(" Sku query: "+query);
    }

    // 从商品搜索中获取sku编号
    private StringBuilder skuFacetField(QueryResponse response,String facetName) {
        FacetField fields = response.getFacetField(facetName);
        List<FacetField.Count> counts = fields.getValues();
        StringBuilder builder = new StringBuilder();
        for (int i =0 ; i < counts.size(); i++)
        {
            if (counts.get(i).getCount() > 0)
            {
                if (i > 0)
                {
                    builder.append(" OR ");
                }
                builder.append(counts.get(i).getName());
            }
        }
        System.out.println(" Sku 商品属性："+builder);
        return builder;
    }

    public class Sku{
        // 类目编号
        private String cat;
        // Sku k
        private String k;
        // Sku v
        private String v;
        // Sku 基类键
        private String pk;
        // Sku 基类值
        private String pv;
        // 商品 sku 编号
        private String vid;

        @Override
        public boolean equals(Object obj) {
            if (null == obj){
                return false;
            }
            if (this == obj){
                return true;
            }
            if (obj instanceof Sku){
                Sku sku = (Sku) obj;
                if (StringUtil.notNull(sku.getVid()) && StringUtil.notNull(sku.getV()) && StringUtil.notNull(sku.getCat()))
                {
                    if (Objects.equals(sku.getVid(), this.vid) && sku.getV().equals(this.v) && sku.getCat().equals(this.cat))
                    {
                        return true;
                    }
                }
            }
            return false;
        }


        @Override
        public int hashCode() {
            if (StringUtil.notNull(vid) && StringUtil.notNull(v) && StringUtil.notNull(cat))
            {
                return vid.hashCode() * v.hashCode() * cat.hashCode();
            }
            return 0;
        }

        public String getCat() {
            return cat;
        }

        public void setCat(String cat) {
            this.cat = cat;
        }

        public String getK() {
            return k;
        }

        public void setK(String k) {
            this.k = k;
        }

        public String getV() {
            return v;
        }

        public void setV(String v) {
            this.v = v;
        }

        public String getPk() {
            return pk;
        }

        public void setPk(String pk) {
            this.pk = pk;
        }

        public String getPv() {
            return pv;
        }

        public void setPv(String pv) {
            this.pv = pv;
        }

        public String getVid() {
            return vid;
        }

        public void setVid(String vid) {
            this.vid = vid;
        }
    }
}
