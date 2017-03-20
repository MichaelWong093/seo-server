package com.berchina.seo.server.provider.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

import org.assertj.core.util.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.berchina.seo.server.configloader.exception.SeoException;
import com.berchina.seo.server.configloader.exception.server.ServerException;
import com.hankcs.hanlp.corpus.util.StringUtils;

/**
 * 读写文件的工具类
 *
 * @author halley (yanhuiqing)
 */
public class IOUtills extends com.hankcs.hanlp.corpus.io.IOUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(IOUtills.class);
	private static final Logger hwLogger = LoggerFactory.getLogger("logUtils");

    private IOUtills() {
    }//工具类 不让new

    /**
     * @param path    文件路径，如果文件不存在会自动创建
     * @param content 要写入的内容 按行写入
     * @return
     */
    public static boolean saveTxtNewLine(String path, String content) {
        try {
            File ifFile = new File(path);
            Path p = FileSystems.getDefault().getPath(path);
            if (!ifFile.exists()) {
                Files.createFile(p);
            }
            if (!Files.isWritable(p)) {
                throw new SeoException(ServerException.SEO_FILE_CANNOT_WRITE);
            }

            Files.write(p, (content + "\r\n").getBytes(), StandardOpenOption.APPEND);

            return true;
        } catch (IOException e) {

            e.printStackTrace();
            return false;
        }

    }
    
    /* add by yhq
     * 记录 搜索的关键字到日志文件
     */
    public static void recordKw2Log(String hotWords,String channel){
    	if(!StringUtils.isBlankOrNull(hotWords)){
    		hwLogger.info(channel+Constants.COLON+hotWords);
    	}
    }
    // add by yhq 获取文件的内容
    public static List<String> getFileContents(String fileName){
    	Path p = FileSystems.getDefault().getPath(fileName);
    	List<String> contents = null;
    	try {
    		contents = Files.readAllLines(p);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return contents;
    }
    
    //add by yhq 每行的字数较多时 此方法效率高
	public static int getFileLineCount(String fileName){
    	int cnt = 0;
    	LineNumberReader reader = null;
    	try {
			reader = new LineNumberReader(new FileReader(fileName));
			@SuppressWarnings("unused")
			String lineRead = "";
			while((lineRead = reader.readLine())!=null){
			}
			cnt = reader.getLineNumber();
		} catch (Exception e) {
			cnt = -1;
			e.printStackTrace();
		} finally{
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    	return cnt;
    }
    
  //add by yhq 每行的字数较少时 此方法效率高
    public static int getFileLineCounts(String fileName){
    	int cnt = 0;
    	InputStream is = null;
    	try {
			is = new BufferedInputStream(new FileInputStream(fileName));
			byte[] c = new byte[1024];
			int readChars = 0;
			while((readChars = is.read(c))!=-1){
				for(int i=0;i<readChars;++i){
					if(c[i]=='\n'){
						++cnt;
					}
					
				}
			}
		} catch (Exception e) {
			cnt = -1;
			e.printStackTrace();
		}finally{
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    	
    	return cnt;
    }
    /**
     * add by yhq 是否为指定的文件后缀名
     * @param fileName 全路径
     * @param suffix
     * @return
     */
    public static boolean isThisSuffix(String fileName,String suffix){
    	if(StringUtils.isBlankOrNull(fileName))
    		throw new RuntimeException("文件名为空：IOUtills-->isThisSuffix(String fileName,String suffix)");
    	int cnt = fileName.lastIndexOf(".");
    	String fileSuffix = "";
    	if(cnt>0){
    		fileSuffix = fileName.substring(cnt);
    	}
    	return fileSuffix.equals(suffix);
    }
    /**
     *  add by yhq 重命名文件
     * @param oldFileName 原来的文件名
     * @param newFileName 新的文件名
     */
    public static String renameFile(String oldFileName,String newFileName){
    	if(!oldFileName.equals(newFileName)){
    		File oFile = new File(oldFileName);
    		File nFile = new File(newFileName);
    		if(!oFile.exists()){
    			LOGGER.error("重命名的文件：["+oldFileName+"]不存在");
    			return "-1";
    		}
    		if(nFile.exists()){
    			LOGGER.warn("目录下已经有一个文件和新文件相同，不允许重命名");
    			return "-2";
    		}
    		if(oFile.renameTo(nFile)){
    			return "1";//重命名 成功
    		}
    		return "0";//重命名 失败，可以有其他进程访问该资源
    	}else{
    		LOGGER.warn("新文件名：["+newFileName+"]和旧文件名：["+oldFileName+"]相同");
    		return "-3";
    	}
    }
    /**
     * 根据文件路径 获取下面的所有文件名称（文件名称为全路径）
     * @param path 路径最后必须跟 路径分隔符。windows '\\'  linux '/'
     * @return
     */
	public static List<String> listFileWithPath(String path) {
		File file = new File(path);
		String[] fileNames = file.list();
		List<String> fullNameList = Lists.newArrayList();
		for(String fileName : fileNames){
			fullNameList.add(path+fileName);
			LOGGER.info("正在加载的文件名为："+path+fileName);
		}
		return fullNameList;
	}
}
