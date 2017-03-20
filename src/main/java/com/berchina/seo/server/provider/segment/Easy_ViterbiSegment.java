package com.berchina.seo.server.provider.segment;

import java.util.LinkedList;
import java.util.List;

import com.hankcs.hanlp.collection.trie.DoubleArrayTrie;
import com.hankcs.hanlp.collection.trie.bintrie.BaseNode;
import com.hankcs.hanlp.dictionary.CoreDictionary;
import com.hankcs.hanlp.recognition.nr.JapanesePersonRecognition;
import com.hankcs.hanlp.recognition.nr.PersonRecognition;
import com.hankcs.hanlp.recognition.nr.TranslatedPersonRecognition;
import com.hankcs.hanlp.recognition.ns.PlaceRecognition;
import com.hankcs.hanlp.recognition.nt.OrganizationRecognition;
import com.hankcs.hanlp.seg.WordBasedGenerativeModelSegment;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.seg.common.Vertex;
import com.hankcs.hanlp.seg.common.WordNet;
/**
 * 分词器适配器
 * @author halley (yanhuiqing)
 *
 */
public class Easy_ViterbiSegment extends WordBasedGenerativeModelSegment {
	/**
     * 使用用户词典合并粗分结果
     * @param vertexList 粗分结果
     * @return 合并后的结果
     */
    protected static List<Vertex> combineByLZBankCustomDictionary(List<Vertex> vertexList)
    {
        Vertex[] wordNet = new Vertex[vertexList.size()];
        vertexList.toArray(wordNet);
        // DAT合并
        DoubleArrayTrie<CoreDictionary.Attribute> dat = EasyCustomDictionary.dat;
        for (int i = 0; i < wordNet.length; ++i)
        {
            int state = 1;
            state = dat.transition(wordNet[i].realWord, state);
            if (state > 0)
            {
                int start = i;
                int to = i + 1;
                int end = to;
                CoreDictionary.Attribute value = dat.output(state);
                for (; to < wordNet.length; ++to)
                {
                    state = dat.transition(wordNet[to].realWord, state);
                    if (state < 0) break;
                    CoreDictionary.Attribute output = dat.output(state);
                    if (output != null)
                    {
                        value = output;
                        end = to + 1;
                    }
                }
                if (value != null)
                {
                    StringBuilder sbTerm = new StringBuilder();
                    for (int j = start; j < end; ++j)
                    {
                        sbTerm.append(wordNet[j]);
                        wordNet[j] = null;
                    }
                    wordNet[i] = new Vertex(sbTerm.toString(), value);
                    i = end - 1;
                }
            }
        }
        // BinTrie合并
        if (EasyCustomDictionary.trie != null)
        {
            for (int i = 0; i < wordNet.length; ++i)
            {
                if (wordNet[i] == null) continue;
                BaseNode<CoreDictionary.Attribute> state = EasyCustomDictionary.trie.transition(wordNet[i].realWord.toCharArray(), 0);
                if (state != null)
                {
                    int start = i;
                    int to = i + 1;
                    int end = to;
                    CoreDictionary.Attribute value = state.getValue();
                    for (; to < wordNet.length; ++to)
                    {
                        if (wordNet[to] == null) continue;
                        state = state.transition(wordNet[to].realWord.toCharArray(), 0);
                        if (state == null) break;
                        if (state.getValue() != null)
                        {
                            value = state.getValue();
                            end = to + 1;
                        }
                    }
                    if (value != null)
                    {
                        StringBuilder sbTerm = new StringBuilder();
                        for (int j = start; j < end; ++j)
                        {
                            if (wordNet[j] == null) continue;
                            sbTerm.append(wordNet[j]);
                            wordNet[j] = null;
                        }
                        wordNet[i] = new Vertex(sbTerm.toString(), value);
                        i = end - 1;
                    }
                }
            }
        }
        vertexList.clear();
        for (Vertex vertex : wordNet)
        {
            if (vertex != null) vertexList.add(vertex);
        }
        return vertexList;
    }

	@Override
	protected List<Term> segSentence(char[] sentence) {

//      long start = System.currentTimeMillis();
      WordNet wordNetAll = new WordNet(sentence);
      ////////////////生成词网////////////////////
      GenerateWordNet(wordNetAll);
      ///////////////生成词图////////////////////
//      System.out.println("构图：" + (System.currentTimeMillis() - start));
      if (EasySeg.Config.DEBUG)
      {
          System.out.printf("粗分词网：\n%s\n", wordNetAll);
      }
//      start = System.currentTimeMillis();
      List<Vertex> vertexList = viterbi(wordNetAll);
//      System.out.println("最短路：" + (System.currentTimeMillis() - start));

      if (config.useCustomDictionary)
      {
    	  combineByLZBankCustomDictionary(vertexList);
      }

      if (EasySeg.Config.DEBUG)
      {
          System.out.println("粗分结果" + convert(vertexList, false));
      }

      // 数字识别
      if (config.numberQuantifierRecognize)
      {
          mergeNumberQuantifier(vertexList, wordNetAll, config);
      }

      // 实体命名识别
      if (config.ner)
      {
          WordNet wordNetOptimum = new WordNet(sentence, vertexList);
          int preSize = wordNetOptimum.size();
          if (config.nameRecognize)
          {
              PersonRecognition.Recognition(vertexList, wordNetOptimum, wordNetAll);
          }
          if (config.translatedNameRecognize)
          {
              TranslatedPersonRecognition.Recognition(vertexList, wordNetOptimum, wordNetAll);
          }
          if (config.japaneseNameRecognize)
          {
              JapanesePersonRecognition.Recognition(vertexList, wordNetOptimum, wordNetAll);
          }
          if (config.placeRecognize)
          {
              PlaceRecognition.Recognition(vertexList, wordNetOptimum, wordNetAll);
          }
          if (config.organizationRecognize)
          {
              // 层叠隐马模型——生成输出作为下一级隐马输入
              vertexList = viterbi(wordNetOptimum);
              wordNetOptimum.clear();
              wordNetOptimum.addAll(vertexList);
              preSize = wordNetOptimum.size();
              OrganizationRecognition.Recognition(vertexList, wordNetOptimum, wordNetAll);
          }
          if (wordNetOptimum.size() != preSize)
          {
              vertexList = viterbi(wordNetOptimum);
              if (EasySeg.Config.DEBUG)
              {
                  System.out.printf("细分词网：\n%s\n", wordNetOptimum);
              }
          }
      }

      // 如果是索引模式则全切分
      if (config.indexMode)
      {
          return decorateResultForIndexMode(vertexList, wordNetAll);
      }

      // 是否标注词性
      if (config.speechTagging)
      {
          speechTagging(vertexList);
      }

      return convert(vertexList, config.offset);
	}
	
	 private static List<Vertex> viterbi(WordNet wordNet)
	    {
	        // 避免生成对象，优化速度
	        LinkedList<Vertex> nodes[] = wordNet.getVertexes();
	        LinkedList<Vertex> vertexList = new LinkedList<Vertex>();
	        for (Vertex node : nodes[1])
	        {
	            node.updateFrom(nodes[0].getFirst());
	        }
	        for (int i = 1; i < nodes.length - 1; ++i)
	        {
	            LinkedList<Vertex> nodeArray = nodes[i];
	            if (nodeArray == null) continue;
	            for (Vertex node : nodeArray)
	            {
	                if (node.from == null) continue;
	                for (Vertex to : nodes[i + node.realWord.length()])
	                {
	                    to.updateFrom(node);
	                }
	            }
	        }
	        Vertex from = nodes[nodes.length - 1].getFirst();
	        while (from != null)
	        {
	            vertexList.addFirst(from);
	            from = from.from;
	        }
	        return vertexList;
	    }
}
