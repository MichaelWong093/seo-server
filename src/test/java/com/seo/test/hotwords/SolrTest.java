package com.seo.test.hotwords;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class SolrTest {
	private SolrClient server;
	private static final String DEFAULT_URL = "http://127.0.0.1:8080/solr/gdhotwd";

	@Before
	public void init() {
		try {
			server = new HttpSolrClient(DEFAULT_URL);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@After
	public void destory() {
		server = null;
		System.runFinalization();
		System.gc();

	}

	public final void print(Object o) {
		System.out.println(o);

	}

	@Test
	public void server() {
		print(server);
	}
	
	//@Test
	public void queryTop6() {
	    ModifiableSolrParams params = new ModifiableSolrParams();
	    // 查询关键词，*:*代表所有属性、所有值，即所有index
	    params.set("q", "*:*");
	    // 分页，start=0就是从0开始，，rows=5当前返回5条记录，第二页就是变化start这个值为5就可以了。
	    params.set("start", 0);
	   //params.set("rows", Integer.MAX_VALUE);
	   params.set("rows", 6);
	    
	    // 排序，，如果按照id 排序，，那么将score desc 改成 id desc(or asc)
	   params.set("sort", "goodsname asc");
	 
	    // 返回信息 * 为全部 这里是全部加上score，如果不加下面就不能使用score
	    params.set("fl", "*,goodsname");
	    
	    try {
	        QueryResponse response = server.query(params);
	        
	        SolrDocumentList list = response.getResults();
	        for (int i = 0; i < list.size(); i++) {
	            print(list.get(i));
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	@Test
	public void queryCase() {
	    //AND 并且
	    SolrQuery params = new SolrQuery();
	    params.setQuery("suggest:*牛肉面*");
	    //OR 或者
	    //params.setQuery("hotwords:测试 OR hotwords:气温");
	    //空格 等同于 OR
	    //params.setQuery("name:server manu:dell");
	    
	    //params.setQuery("hotwords:牛肉面 + shopname:魔兽世界");
	    //params.setQuery("name:server + manu:dell");
	    
	    //查询name包含solr apple
	    //params.setQuery("name:solr,apple");
	    //manu不包含inc
	    //params.setQuery("name:solr,apple NOT manu:inc");
	    
	    //50 <= price <= 200
	    //params.setQuery("price:[50 TO 200]");
	    //params.setQuery("popularity:[5 TO 6]");
	    //params.setQuery("price:[50 TO 200] - popularity:[5 TO 6]");
	    //params.setQuery("price:[50 TO 200] + popularity:[5 TO 6]");
	    
	    //50 <= price <= 200 AND 5 <= popularity <= 6
	   // params.setQuery("price:[50 TO 200] AND popularity:[5 TO 6]");
	    //params.setQuery("price:[50 TO 200] OR popularity:[5 TO 6]");
	    
	    //过滤器查询，可以提高性能 filter 类似多个条件组合，如and
	    //params.addFilterQuery("goodsname:牛肉面 OR shopname:魔兽世界");
	    //params.addFilterQuery("price:[50 TO 200]");
	    //params.addFilterQuery("popularity:[* TO 5]");
	    //params.addFilterQuery("weight:*");
	    //0 < popularity < 6  没有等于
	    //params.addFilterQuery("popularity:{0 TO 6}");
	    
	    //排序
	    params.addSort("frequency", ORDER.desc);
	    
	    //分页：start开始页，rows每页显示记录条数
	    params.add("start", "0");
	    params.add("rows", "6");
	    //params.setStart(0);
	    //params.setRows(200);
	    
	    //设置高亮
	  /*  params.setHighlight(true); // 开启高亮组件
	    params.addHighlightField("hotwords");// 高亮字段
	    params.setHighlightSimplePre("<font color='red'>");//标记，高亮关键字前缀
	    params.setHighlightSimplePost("</font>");//后缀
	    params.setHighlightSnippets(1);//结果分片数，默认为1
	    params.setHighlightFragsize(1000);//每个分片的最大长度，默认为100
*/	 
	    //分片信息
	    params.setFacet(true)
	        .setFacetMinCount(1)
	        .setFacetLimit(6)//段
	        .addFacetField("goodsName")//分片字段
	        /*.addFacetField("shopname")*/; 
	    
	    //params.setQueryType("");
	    
	    try {
	        QueryResponse response = server.query(params);
	        
	        /*List<Index> indexs = response.getBeans(Index.class);
	        for (int i = 0; i < indexs.size(); i++) {
	            fail(indexs.get(i));
	        }*/
	        
	        //输出查询结果集
	        SolrDocumentList list = response.getResults();
	        print("query result nums: " + list.getNumFound());
	        for (int i = 0; i < list.size(); i++) {
	            print(list.get(i));
	        }
	        
	        //输出分片信息
	        List<FacetField> facets = response.getFacetFields();
	        for (FacetField facet : facets) {
	            print(facet);
	            List<Count> facetCounts = facet.getValues();
	            for (Count count : facetCounts) {
	                System.out.println(count.getName() + ": " + count.getCount());
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	//@Test
	public void facetQueryCase() {
	    SolrQuery params = new SolrQuery("*:*");

	    //排序
	    params.addSort("goodsname", ORDER.asc);

	    params.setStart(0);
	    params.setRows(200);

	    //Facet为solr中的层次分类查询
	    //分片信息
	    params.setFacet(true)
	        .setQuery("*:*")
	        .setFacetMinCount(1)
	        .setFacetLimit(5)//段
	        .setFacetPrefix("牛肉面")//查询goodsname、shopname中关键字前缀是“牛肉面”的
	        .addFacetField("goodsname")
	        .addFacetField("shopname");//分片字段

	    try {
	        QueryResponse response = server.query(params);

	        //输出查询结果集
	        SolrDocumentList list = response.getResults();
	        print("Query result nums: " + list.getNumFound());

	        for (int i = 0; i < list.size(); i++) {
	            print(list.get(i));
	        }

	        print("All facet filed result: ");
	        //输出分片信息
	        List<FacetField> facets = response.getFacetFields();
	        for (FacetField facet : facets) {
	            print(facet);
	            List<Count> facetCounts = facet.getValues();
	            for (Count count : facetCounts) {
	                //关键字 - 出现次数
	                print(count.getName() + ": " + count.getCount());
	            }
	        }
	        
	        print("Search facet [goodsname] filed result: ");
	        //输出分片信息
	        FacetField facetField = response.getFacetField("goodsname");
	        List<Count> facetFields = facetField.getValues();
	        for (Count count : facetFields) {
	            //关键字 - 出现次数
	            print(count.getName() + ": " + count.getCount());
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    } 
	}


}
