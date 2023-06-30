package com.lkn.chess.read_chess;

import com.lkn.chess.Conf;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

public class WriteChessFile {
	
	private Set<String> chessSet = new HashSet<String>();
	
	public WriteChessFile(){
		try {
			readSourceFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void writeFileToSystem(Set<String> set) throws Exception {
		PrintWriter pw = new PrintWriter(new FileWriter(Conf.getChessFile(), true));
		int num = 0;
		for (String lineInfo : set) {
			if(lineInfo != null && !lineInfo.equals("") && !checkRepeat(lineInfo)){
				pw.println(lineInfo);
			}
			pw.flush();
			System.out.println("写入： " + set.size() + " : " + ++num);
		}
		pw.close();
	}

	private void readSourceFile() throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(Conf.getChessFile()));
		String line = null;
		while((line = br.readLine()) != null){
			System.out.println(" : " + line);
			chessSet.add(line);
		}
		br.close();
	}
	
	private boolean checkRepeat(String lineInfo) {
		boolean repeat = false;
		for (String str : chessSet) {
			if(str.startsWith(lineInfo)){
				repeat = true;
				break;
			}
		}
		return repeat;
	}
	
	public static void main(String[] args) throws Exception {
	}
}
