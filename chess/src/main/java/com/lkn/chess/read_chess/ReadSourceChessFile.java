package com.lkn.chess.read_chess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class ReadSourceChessFile {
	private File sourceFile = new File("D:/棋谱");
	
	public Set<String> readFiles() throws Exception {
		Set<String> set = new HashSet<String>();
		File[] listFiles = sourceFile.listFiles();
		for (int i = 0; i < listFiles.length; i++) {
			String info = operateSingleFile(listFiles[i]);
			set.add(info);
			System.out.println("读取： " + listFiles.length + " : " + i);
		}
		return set;
	}

	/**
	 * 处理单个文件
	 * @param file
	 * @throws Exception 
	 */
	private String operateSingleFile(File file) throws Exception {
		Map<Integer, String> map = new TreeMap<Integer, String>();
		int result = -1;	// 结果有3种情况： 1-红胜     2-黑胜     3-平局
		int number = 1;
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = null;
		while((line = br.readLine()) != null){
			line = line.trim();
			if(line.startsWith(number + ".")){
				number = operateChessLine(line, number, map);	// 处理一行棋谱
			}
			if(line.startsWith("结果：")){
				String resultStr = line.substring(3);
				if(resultStr.equals("红胜")){
					result = 1;
				} else if(resultStr.equals("黑胜")){
					result = 2;
				} else if(resultStr.equals("和局")){
					result = 3;
				} else {
					result = -1;
					map.clear();
					break;
				}
			}
		}
		br.close();
		return mapConverToStr(map, result);
	}

	/**
	 * map转换String
	 * @param map
	 * @param result 
	 * @return
	 */
	private String mapConverToStr(Map<Integer, String> map, int result) {
		StringBuffer sb = new StringBuffer();
		if(result == 1 || result == 2 || result == 3){
			for (String str : map.values()) {
				sb.append(str);
			}
			sb.append(result);
		}
		return sb.toString();
	}

	private int operateChessLine(String line, int number, Map<Integer, String> map) {
		line = line.trim();
		line = line.replaceAll("\\s{1,}", " ");
		String[] splitArr = line.split(" ");
		for (int i = 0; i < splitArr.length; i++) {
			if(splitArr[i].equals(number + ".")){
			}else if(splitArr[i].equals("…………")){
				continue;
			} else {
				String str = map.get(number) == null ? "" : map.get(number);
				str = str + splitArr[i] + " ";
				map.put(number, str);
				if(str.length() == 10){
					number++;
				}
			}
		}
		return number;
	}
}
