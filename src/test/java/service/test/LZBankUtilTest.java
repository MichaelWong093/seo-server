package service.test;

import com.berchina.esb.server.provider.client.SeoRequest;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Before;
import org.junit.Test;

import com.berchina.esb.server.provider.segment.EasySeg;
import com.berchina.esb.server.provider.utils.SegmentUtil;
import com.hankcs.hanlp.HanLP;

import java.io.IOException;

/**
 * @author halley (yanhuiqing)
 */
public class LZBankUtilTest {

    private HttpSolrClient goods;

    private SolrQuery query = null;

    private SeoRequest request = null;

    @Before
    public void init() {
        goods = new HttpSolrClient.Builder("http://127.0.0.1:8080/solr/goods").build();
    }

    @Test
    public void customInsert() throws IOException, SolrServerException {

        query = new SolrQuery();



        query.set("q","");

        SolrDocumentList documents = goods.query(query).getResults();

        //System.out.println(CustomDictionary.insert("核桃味"));

        System.out.println(HanLP.segment("核桃味的瓜子不是核桃"));

//		System.out.println(SegmentUtil.customDicInsert("核桃味的瓜子"));
        //System.out.println(LZBank.segment("核桃味的瓜子不是核桃"));
//		System.out.println(EasySeg.newSegment().enableTranslatedNameRecognize(true).seg("核桃味的瓜子不是核桃"));
//		System.out.println(HanLP.newSegment().enableTranslatedNameRecognize(true).seg("核桃味的瓜子不是核桃"));

//		System.out.println(EasySeg.extractKeyword("核桃味的瓜子不是核桃", 5));
    }
}
