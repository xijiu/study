package com.lkn.chess;

import com.lkn.chess.bean.Role;
import com.lkn.chess.bean.chess_piece.AbstractChessPiece;
import com.lkn.chess.bean.chess_piece.Cannons;
import com.lkn.chess.bean.chess_piece.Elephants;
import com.lkn.chess.bean.chess_piece.Horse;
import com.lkn.chess.bean.chess_piece.King;
import com.lkn.chess.bean.chess_piece.Mandarins;
import com.lkn.chess.bean.chess_piece.Pawns;
import com.lkn.chess.bean.chess_piece.Rooks;

/**
 * 象棋运子的工具类
 * @author:likn1	Jan 6, 2016  9:49:08 AM
 */
public class ChessTools {

	private final static String[] RED_RECORDES_ARR = {"一","二","三","四","五","六","七","八","九"};
	private final static String[] BLACK_RECORDES_ARR = {"１","２","３","４","５","６","７","８","９"};

	public static String[] getRedRecordesArr() {
		return RED_RECORDES_ARR;
	}

	public static String[] getBlackRecordesArr() {
		return BLACK_RECORDES_ARR;
	}

	public static int transToNum(char str) {
		switch (str) {
			case '一':
			case '1':
			case '１':
				return 1;
			case '二':
			case '2':
			case '２':
				return 2;
			case '三':
			case '3':
			case '３':
				return 3;
			case '四':
			case '4':
			case '４':
				return 4;
			case '五':
			case '5':
			case '５':
				return 5;
			case '六':
			case '6':
			case '６':
				return 6;
			case '七':
			case '7':
			case '７':
				return 7;
			case '八':
			case '8':
			case '８':
				return 8;
			case '九':
			case '9':
			case '９':
				return 9;
		}

		throw new RuntimeException();
	}

	public static int transLineToY(char line, Role role) {
		if (role == Role.RED) {
			switch (line) {
				case '一':
				case '1':
				case '１':
					return 8;
				case '二':
				case '2':
				case '２':
					return 7;
				case '三':
				case '3':
				case '３':
					return 6;
				case '四':
				case '4':
				case '４':
					return 5;
				case '五':
				case '5':
				case '５':
					return 4;
				case '六':
				case '6':
				case '６':
					return 3;
				case '七':
				case '7':
				case '７':
					return 2;
				case '八':
				case '8':
				case '８':
					return 1;
				case '九':
				case '9':
				case '９':
					return 0;
			}
		} else {
			switch (line) {
				case '一':
				case '1':
				case '１':
					return 0;
				case '二':
				case '2':
				case '２':
					return 1;
				case '三':
				case '3':
				case '３':
					return 2;
				case '四':
				case '4':
				case '４':
					return 3;
				case '五':
				case '5':
				case '５':
					return 4;
				case '六':
				case '6':
				case '６':
					return 5;
				case '七':
				case '7':
				case '７':
					return 6;
				case '八':
				case '8':
				case '８':
					return 7;
				case '九':
				case '9':
				case '９':
					return 8;
			}
		}

		throw new RuntimeException("invalid line " + line);
	}


	public static int fetchX(int position) {
		return position / 10;
	}

	public static int fetchY(int position) {
		return position % 10;
	}

	public static AbstractChessPiece getPiece(AbstractChessPiece[][] arr, int position) {
		return arr[position / 10][position % 10];
	}

	public static AbstractChessPiece getPiece(AbstractChessPiece[][] arr, int x, int y) {
		return arr[x][y];
	}

	public static void putPiece(AbstractChessPiece[][] arr, int position, AbstractChessPiece piece) {
		arr[position / 10][position % 10] = piece;
	}


	public static AbstractChessPiece removePiece(AbstractChessPiece[][] arr, int position) {
		AbstractChessPiece removePiece = arr[fetchX(position)][fetchY(position)];
		arr[fetchX(position)][fetchY(position)] = null;
		return removePiece;
	}

	public static int toPosition(int x, int y) {
		return x * 10 + y;
	}

	public static AbstractChessPiece toPiece(int type) {
		switch (type) {
			case 5:
				return new King(Role.RED);
			case 12:
				return new King(Role.BLACK);
			case 6:
				return new Cannons(Role.RED);
			case 13:
				return new Cannons(Role.BLACK);
			case 3:
				return new Elephants(Role.RED);
			case 10:
				return new Elephants(Role.BLACK);
			case 2:
				return new Horse(Role.RED);
			case 9:
				return new Horse(Role.BLACK);
			case 4:
				return new Mandarins(Role.RED);
			case 11:
				return new Mandarins(Role.BLACK);
			case 7:
				return new Pawns(Role.RED);
			case 14:
				return new Pawns(Role.BLACK);
			case 1:
				return new Rooks(Role.RED);
			case 8:
				return new Rooks(Role.BLACK);
		}

		throw new RuntimeException();
	}
}
