package com.lkn.chess.read_chess;

import java.util.Set;

public class ReadFileAndWrite {
	private ReadSourceChessFile read = new ReadSourceChessFile();
	private WriteChessFile write = new WriteChessFile();
	
	public static void main(String[] args) throws Exception {
		new ReadFileAndWrite().readAndWrite();
	}

	private void readAndWrite() throws Exception {
		Set<String> set = read.readFiles();
		write.writeFileToSystem(set);
	}

}
