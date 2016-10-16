package service.test;

import org.junit.Test;

import com.berchina.esb.server.provider.segment.EasySeg;
import com.berchina.esb.server.provider.utils.SegmentUtil;
import com.hankcs.hanlp.HanLP;
/**
 * 
 * @author halley (yanhuiqing)
 *
 */
public class LZBankUtilTest {
	@Test
	public void customInsert(){
		
		//System.out.println(CustomDictionary.insert("核桃味"));
		//System.out.println(HanLP.segment("核桃味的瓜子不是核桃"));
	
		System.out.println(SegmentUtil.customDicInsert("核桃味的瓜子"));
		//System.out.println(LZBank.segment("核桃味的瓜子不是核桃"));
		System.out.println(EasySeg.newSegment().enableTranslatedNameRecognize(true).seg("核桃味的瓜子不是核桃"));
		System.out.println(HanLP.newSegment().enableTranslatedNameRecognize(true).seg("核桃味的瓜子不是核桃"));
		
		System.out.println(EasySeg.extractKeyword("核桃味的瓜子不是核桃", 5));
	}
}
