package service.test;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import com.hankcs.hanlp.collection.AhoCorasick.AhoCorasickDoubleArrayTrie;
import com.hankcs.hanlp.dictionary.BaseSearcher;
import com.hankcs.hanlp.dictionary.CoreDictionary;
import com.hankcs.hanlp.dictionary.CustomDictionary;
import com.hankcs.hanlp.tokenizer.IndexTokenizer;
import org.junit.Test;

import com.berchina.esb.server.provider.utils.IOUtills;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.CoreSynonymDictionary;
import com.hankcs.hanlp.dictionary.stopword.CoreStopWordDictionary;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.Dijkstra.DijkstraSegment;
import com.hankcs.hanlp.seg.NShort.NShortSegment;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.suggest.Suggester;
//import com.hp.hpl.sparta.Text;

/**
 * hot word test clazz
 *
 * @author halley (yanhuiqing)
 */

public class HotWordTest {

    @Test
    public void test() {

        // 动态增加
        CustomDictionary.add("牛");
// 强行插入
//        CustomDictionary.insert("码农", "nz 1024");
// 删除词语（注释掉试试）
// CustomDictionary.remove("码农");
//        System.out.println(CustomDictionary.add("裸婚", "v 2 nz 1"));
//        System.out.println(CustomDictionary.get("裸婚"));

        String text = "牛肉面很香";  // 怎么可能噗哈哈！

// AhoCorasickDoubleArrayTrie自动机分词
//		final char[] charArray = text.toCharArray();

//		CoreDictionary.trie.parseText(charArray, new AhoCorasickDoubleArrayTrie.IHit<CoreDictionary.Attribute>()
//
//		{
//			@Override
//			public void hit(int begin, int end, CoreDictionary.Attribute value)
//			{
//				System.out.printf("[%d:%d]=%s %s\n", begin, end, new String(charArray, begin, end - begin), value);
//			}
//		});


// trie树分词
        BaseSearcher searcher = CustomDictionary.getSearcher(text);
        Map.Entry entry;
        while ((entry = searcher.next()) != null) {
            System.out.println(" === " + entry);
        }

// 标准分词
        System.out.println(HanLP.segment(text));
    }

    //@Test
    public void distanceBetwween2Words() {
        String[] wordArray = new String[]
                {
                        "香蕉",
                        "苹果",
                        "白菜",
                        "水果",
                        "蔬菜",
                        "自行车",
                        "公交车",
                        "飞机",
                        "买",
                        "卖",
                        "购入",
                        "新年",
                        "春节",
                        "丢失",
                        "补办",
                        "办理",
                        "送给",
                        "寻找",
                        "孩子",
                        "教室",
                        "教师",
                        "会计",
                };
        for (String a : wordArray) {
            for (String b : wordArray) {
                System.out.println("教师你好" + "\t" + b + "\t之间的距离是\t" + CoreSynonymDictionary.distance("教师你好", b));
            }
        }
    }

    //@Test
    public void suggestTest() {
        Suggester suggester = new Suggester();
        String[] titleArray =
                (
                        "威廉王子发表演说 呼吁保护野生动物\n" +
                                "《时代》年度人物最终入围名单出炉 普京马云入选\n" +
                                "“黑格比”横扫菲：菲吸取“海燕”经验及早疏散\n" +
                                "日本保密法将正式生效 日媒指其损害国民知情权\n" +
                                "英报告说空气污染带来“公共健康危机”"
                ).split("\\n");
        for (String title : titleArray) {
            suggester.addSentence(title);
        }

        System.out.println(suggester.suggest("发言", 1));       // 语义
        System.out.println(suggester.suggest("危机公共", 1));   // 字符
        System.out.println(suggester.suggest("mayun", 1));      // 拼音
    }

    //	@Test
    public void suggestKeywd() {
        Suggester su = new Suggester();
        String[] title =
                (
                        "萨达姆牛肉面\n" +
                                "牛肉味的瓜子\n" +
                                "牛肉面\n" +
                                "牛肉面半价\n"
                ).split("\\n");
        for (String t : title) {
            su.addSentence(t);
        }
        System.out.println(su.suggest("牛肉面半", 1));
    }

    //	@Test
    public void pinyingConvert() {
        System.out.println(HanLP.convertToPinyinList("牛肉面"));
        Segment nShortSegment = new NShortSegment().enableCustomDictionary(false).enablePlaceRecognize(true).enableOrganizationRecognize(true);
        Segment shortestSegment = new DijkstraSegment().enableCustomDictionary(false).enablePlaceRecognize(true).enableOrganizationRecognize(true);
        String[] testCase = new String[]{
                "核桃味瓜子。",
                "结婚的和尚未结婚的",
        };
        for (String sentence : testCase) {
            System.out.println("N-最短分词：" + nShortSegment.seg(sentence) + "\n最短路分词：" + shortestSegment.seg(sentence));
        }
    }

    //@Test
    public void saveFile() throws Exception {
        //IOUtil.saveTxt("E:\\test.txt", "牛肉面");
        //IOUtil.saveTxt("E:\\test.txt", "茶叶111");
        //FileChannel fc;
        Path path = FileSystems.getDefault().getPath("E:\\test.txt");
        //Files.exists(path, options)
        //Set<PosixFilePermission> pers = PosixFilePermissions.fromString("rwxr-x---");
        //FileAttribute<Set<PosixFilePermission>> attrs = PosixFilePermissions.asFileAttribute(pers);
        //Files.createFile(path);
        //Files.write(path, "牛肉面".getBytes(), StandardOpenOption.APPEND);
        //Files.write(path, "牛肉面121212".getBytes(), StandardOpenOption.APPEND);

        IOUtills.saveTxtNewLine("E:\\test.txt", "牛肉面");
        IOUtills.saveTxtNewLine("E:\\test.txt", "牛肉面111");

    }

    //	@Test
    public void extractHw() {
        //System.out.println(HanLP.extractKeyword(new String("核桃味的瓜子吃起来像核桃"), 4));
        String line = "1\t核桃味的瓜子吃起来像核桃数据库接口".toString();
        String[] strSplits = line.split("\t");
        if (strSplits.length == 2) {
            int label = Integer.parseInt(strSplits[0]);
            String sentence = strSplits[1];
            Segment segment = HanLP.newSegment().enablePartOfSpeechTagging(true);
            List<Term> segWords = segment.seg(sentence);
            System.out.println(segWords);
            CoreStopWordDictionary.apply(segWords);

            String strSegWords = segWords.toString().replaceAll("[\\[\\]]", "").replaceAll("\\/[a-z]+,", "").replace("\\/n", "");
            System.out.println("---------------------------");
            System.out.println(segWords.size());
            System.out.println(strSegWords);
            System.out.println("---------------------------");
        }
    }
}
