package com.lkn.chess;

import com.lkn.chess.bean.ChessBoard;

import java.util.Scanner;

public class Test {
	
	public static void main(String[] args) {
		new Test().personWalk();
	}

	private void personWalk() {
		ChessBoard board = new ChessBoard();
		board.init();

		GamePlayHigh play = new GamePlayHigh();
		Scanner sc = new Scanner(System.in);
		while (true) {
			board.print();
			System.out.println("请输入：");
			String line = sc.nextLine();
			String[] split = line.split(" ");
			if (split.length != 2) {
				System.out.println("输入有误");
				continue;
			}
			personWalk(board, Integer.parseInt(split[0]), Integer.parseInt(split[1]));
			play.computerWalk(board);
		}
	}
	
	private void personWalk(ChessBoard board, int beginPos, int endPos) {
		board.walk(beginPos, endPos);
		board.print();
	}

}
