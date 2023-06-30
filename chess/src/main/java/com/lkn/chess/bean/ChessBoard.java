package com.lkn.chess.bean;

import com.lkn.chess.BitArray;
import com.lkn.chess.ChessTools;
import com.lkn.chess.bean.chess_piece.AbstractChessPiece;
import com.lkn.chess.bean.chess_piece.Cannons;
import com.lkn.chess.bean.chess_piece.Elephants;
import com.lkn.chess.bean.chess_piece.Horse;
import com.lkn.chess.bean.chess_piece.King;
import com.lkn.chess.bean.chess_piece.Mandarins;
import com.lkn.chess.bean.chess_piece.Pawns;
import com.lkn.chess.bean.chess_piece.Rooks;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 棋盘的实体bean
 * 
 * @author:likn1 Jan 5, 2016 2:10:49 PM
 */
public class ChessBoard {
	private Map<String, Position> positionMap = new HashMap<>(90); // 位置的map
	/** 存放全量的旗子，key为旗子的位置，val是对象 */
	private AbstractChessPiece[][] allPiece = new AbstractChessPiece[10][9];
	private byte[] redPieceArr = new byte[16];
	private int redPieceArrIndex = 0;
	private byte[] blackPieceArr = new byte[16];
	private int blackPieceArrIndex = 0;
	private static final int nextStepLen = 99;
	private int[][] redNextStepPositionArr = new int[nextStepLen][15];
	private int[][] blackNextStepPositionArr = new int[nextStepLen][15];

	/** 10行9列的棋盘 */
	private final byte[][] board = new byte[10][9];

	private final byte[][] invertBoard = new byte[9][10];

	public Map<String, Position> getPositionMap() {
		return positionMap;
	}

	public int[][] getRedNextStepPositionArr() {
		return redNextStepPositionArr;
	}

	public int[][] getBlackNextStepPositionArr() {
		return blackNextStepPositionArr;
	}

	public void genericNextStepPositionMap() {
		for (int i = 0; i < nextStepLen; i++) {
			redNextStepPositionArr[i][0] = 0;
			blackNextStepPositionArr[i][0] = 0;
		}
		for (int x = 0; x < allPiece.length; x++) {
			for (int y = 0; y < allPiece[x].length; y++) {
				AbstractChessPiece piece = allPiece[x][y];
				if (piece == null) {
					continue;
				}
				int piecePosition = ChessTools.toPosition(x, y);
				byte[] positionArr = piece.getReachablePositions(piecePosition, this, true, 10);
				byte size = positionArr[0];
				for (int i = 1; i <= size; i++) {
					int position = positionArr[i];
					if (hasPiece(allPiece, position)) {
						int[][] arr = piece.isRed() ? redNextStepPositionArr : blackNextStepPositionArr;
						int length = arr[position][0];
						arr[position][length + 1] = piece.getDefaultVal();
						arr[position][0] = length + 1;
					}
				}
			}
		}
	}

	private boolean hasPiece(AbstractChessPiece[][] arr, int position) {
		AbstractChessPiece piece = arr[position / 10][position % 10];
		return piece != null;
	}

	/**
	 * 克隆当前对象
	 */
	public ChessBoard clone() {
		ChessBoard clone = new ChessBoard();
		byte[][] original = this.board;
		for (int i = 0; i < original.length; i++) {
			clone.board[i] = Arrays.copyOf(original[i], original[i].length);
		}
		AbstractChessPiece[][] sourceArr = this.getAllPiece();
		AbstractChessPiece[][] targetArr = clone.getAllPiece();
		for (int i = 0; i < sourceArr.length; i++) {
			System.arraycopy(sourceArr[i], 0, targetArr[i], 0, sourceArr[i].length);
		}
		return clone;
	}


	/**
	 * 返回棋盘快照
	 * [1-90]   :  用来标记每个点是否有棋子
	 * [91-218] :  每个点的具体类型
	 *
	 * 车    1
	 * 马    2
	 * 象    3
	 * 士    4
	 * 帅    5
	 * 炮    6
	 * 兵    7
	 *
	 * 黑方
	 * 车    8
	 * 马    9
	 * 象    10
	 * 士    11
	 * 将    12
	 * 炮    13
	 * 兵    14
	 */
	public byte[] snapshot() {
		BitArray bitArray = new BitArray(218);
		int index = 0;
		int dataIndex = 90;
		for (int x = 0; x < 10; x++) {
			for (int y = 0; y < 9; y++) {
				AbstractChessPiece piece = allPiece[x][y];
				boolean hasPiece = piece != null;
				bitArray.set(index++, hasPiece);
				if (hasPiece) {
					storePieceType(bitArray, dataIndex, piece);
					dataIndex += 4;
				}
			}
		}
		return bitArray.toByteArray();
	}

	private byte[] tmpByte = new byte[90];
	public int toHashCode() {
		int index = 0;
		for (int x = 0; x < 10; x++) {
			System.arraycopy(board[x], 0, tmpByte, index, 9);
			index += 9;
		}
		return Arrays.hashCode(tmpByte);
	}

	public byte[] snapshotConvert() {
		BitArray bitArray = new BitArray(218);
		int index = 0;
		int dataIndex = 90;
		for (int x = 0; x < 10; x++) {
			for (int y = 0; y < 9; y++) {
				AbstractChessPiece piece = allPiece[x][8 - y];
				boolean hasPiece = piece != null;
				bitArray.set(index++, hasPiece);
				if (hasPiece) {
					storePieceType(bitArray, dataIndex, piece);
					dataIndex += 4;
				}
			}
		}
		return bitArray.toByteArray();
	}

	private void storePieceType(BitArray bitArray, int dataIndex, AbstractChessPiece piece) {
		byte type = piece.type();
		StringBuilder finalStr = new StringBuilder();
		String s = Integer.toBinaryString(type);
		if (s.length() < 4) {
			int gap = 4 - s.length();
			for (int i = 0; i < gap; i++) {
				finalStr.append('0');
			}
		}
		finalStr.append(s);
		char[] chars = finalStr.toString().toCharArray();
		for (char aChar : chars) {
			bitArray.set(dataIndex++, aChar == '1');
		}
	}

	/**
	 * 棋盘的初始化工作
	 * 
	 * @author:likn1 Jan 5, 2016 2:13:27 PM
	 */
	public void init() {
		initArr(allPiece);
		redPieceArrIndex = 0;
		blackPieceArrIndex = 0;

		initBoard();
//		initForTest();
	}

	private void initArr(AbstractChessPiece[][] arr) {
		for (AbstractChessPiece[] abstractChessPieces : arr) {
			Arrays.fill(abstractChessPieces, null);
		}
		Arrays.fill(redPieceArr, (byte) -1);
		Arrays.fill(blackPieceArr, (byte) -1);
		for (byte[] arrTmp : board) {
			Arrays.fill(arrTmp, (byte) -1);
		}
		for (byte[] arrTmp : invertBoard) {
			Arrays.fill(arrTmp, (byte) -1);
		}
	}

	private void initForTest() {
//		loadPieceFromStr("0_0_3_0_5_4_0_0_13_0_0_0_0_4_0_0_0_0_2_0_6_0_3_0_0_0_0_0_0_0_0_0_0_7_0_0_7_0_0_8_0_0_0_0_0_0_0_0_0_0_1_0_0_14_0_0_7_0_0_2_9_0_0_0_0_0_0_10_0_0_0_0_0_0_0_0_9_0_0_0_0_0_0_10_11_12_11_0_0_0");
		loadPieceFromStr("0_0_3_0_5_4_3_0_13_0_0_0_0_4_0_0_0_0_2_0_6_0_0_0_0_0_0_0_0_0_0_0_0_7_0_0_7_0_0_8_0_0_0_0_0_0_0_0_0_0_1_0_0_14_0_0_7_0_0_2_0_0_0_0_0_0_0_10_0_0_0_9_0_0_0_0_9_0_0_0_0_0_0_10_11_12_11_0_0_0");
//		int val = new GamePlayHigh().computeChessValue(this, Role.RED);
//		System.out.println("val is val is " + val);
	}


	private void initBoard() {
		putPiece(0, new Rooks(Role.RED));
		putPiece(1, new Horse(Role.RED));
		putPiece(2, new Elephants(Role.RED));
		putPiece(3, new Mandarins(Role.RED));
		putPiece(4, new King(Role.RED));
		putPiece(5, new Mandarins(Role.RED));
		putPiece(6, new Elephants(Role.RED));
		putPiece(7, new Horse(Role.RED));
		putPiece(8, new Rooks(Role.RED));


		putPiece(21, new Cannons(Role.RED));
		putPiece(27, new Cannons(Role.RED));


		putPiece(30, new Pawns(Role.RED));
		putPiece(32, new Pawns(Role.RED));
		putPiece(34, new Pawns(Role.RED));
		putPiece(36, new Pawns(Role.RED));
		putPiece(38, new Pawns(Role.RED));


		// --------------------------以下是后手的旗子----------------------------


		putPiece(60, new Pawns(Role.BLACK));
		putPiece(62, new Pawns(Role.BLACK));
		putPiece(64, new Pawns(Role.BLACK));
		putPiece(66, new Pawns(Role.BLACK));
		putPiece(68, new Pawns(Role.BLACK));


		putPiece(71, new Cannons(Role.BLACK));
		putPiece(77, new Cannons(Role.BLACK));


		putPiece(90, new Rooks(Role.BLACK));
		putPiece(91, new Horse(Role.BLACK));
		putPiece(92, new Elephants(Role.BLACK));
		putPiece(93, new Mandarins(Role.BLACK));
		putPiece(94, new King(Role.BLACK));
		putPiece(95, new Mandarins(Role.BLACK));
		putPiece(96, new Elephants(Role.BLACK));
		putPiece(97, new Horse(Role.BLACK));
		putPiece(98, new Rooks(Role.BLACK));
	}

	private void putPiece(int position, AbstractChessPiece piece) {
		setPieceToBoard(position, piece);
		ChessTools.putPiece(allPiece, position, piece);
		if (piece.isRed()) {
			redPieceArr[redPieceArrIndex] = (byte) position;
			piece.setPieceIndex(redPieceArrIndex);
			redPieceArrIndex++;
		} else {
			blackPieceArr[blackPieceArrIndex] = (byte) position;
			piece.setPieceIndex(blackPieceArrIndex);
			blackPieceArrIndex++;
		}
	}

	private void setPieceToBoard(int position, AbstractChessPiece piece) {
		byte val = -1;
		if (piece instanceof Cannons) {
			val = (byte) (piece.isRed() ? Cannons.RED_TYPE : Cannons.BLACK_TYPE);
		} else if (piece instanceof Elephants) {
			val = (byte) (piece.isRed() ? Elephants.RED_TYPE : Elephants.BLACK_TYPE);
		} else if (piece instanceof Horse) {
			val = (byte) (piece.isRed() ? Horse.RED_TYPE : Horse.BLACK_TYPE);
		} else if (piece instanceof King) {
			val = (byte) (piece.isRed() ? King.RED_TYPE : King.BLACK_TYPE);
		} else if (piece instanceof Mandarins) {
			val = (byte) (piece.isRed() ? Mandarins.RED_TYPE : Mandarins.BLACK_TYPE);
		} else if (piece instanceof Pawns) {
			val = (byte) (piece.isRed() ? Pawns.RED_TYPE : Pawns.BLACK_TYPE);
		} else if (piece instanceof Rooks) {
			val = (byte) (piece.isRed() ? Rooks.RED_TYPE : Rooks.BLACK_TYPE);
		}

		int x = ChessTools.fetchX(position);
		int y = ChessTools.fetchY(position);
		board[x][y] = val;
		invertBoard[y][x] = val;
	}

	/**
	 * 悔棋，walk的逆过程
	 */
	public void unWalk(int sourcePosition, int targetPosition, AbstractChessPiece eatenPiece) {
		AbstractChessPiece piece = ChessTools.getPiece(allPiece, targetPosition);
		changePiecePosition(piece, targetPosition, sourcePosition);
		if (eatenPiece != null) {
			addPiece(eatenPiece, targetPosition);
		}
	}

	/**
	 * 将某个棋子放入棋盘
	 *
	 * @param position	目标位置
	 */
	private void addPiece(AbstractChessPiece piece, int position) {
		ChessTools.putPiece(allPiece, position, piece);

		byte[] pieceArr = piece.isRed() ? redPieceArr : blackPieceArr;
		pieceArr[piece.getPieceIndex()] = (byte) position;

		int x = ChessTools.fetchX(position);
		int y = ChessTools.fetchY(position);
		board[x][y] = piece.type();
		invertBoard[y][x] = piece.type();
	}

	/**
	 * 执行走棋的动作
	 *
	 * @param sourcePosition	起始位置
	 * @param targetPosition	目标位置
	 * @return	如果发生了吃子儿行为，那么返回吃掉的子儿，否则返回null
	 */
	public AbstractChessPiece walk(int sourcePosition, int targetPosition) {
		AbstractChessPiece targetPiece = ChessTools.getPiece(allPiece, targetPosition);
		if (targetPiece != null) {
			removePiece(targetPosition);
		}

		AbstractChessPiece piece = ChessTools.getPiece(allPiece, sourcePosition);
		changePiecePosition(piece, sourcePosition, targetPosition);
		return targetPiece;
	}

	/**
	 * 将目标位置的棋子从棋盘上拿下
	 *
	 * @param removePosition    目标位置
	 */
	private void removePiece(int removePosition) {
		AbstractChessPiece removePiece = ChessTools.removePiece(allPiece, removePosition);

		int x = ChessTools.fetchX(removePosition);
		int y = ChessTools.fetchY(removePosition);
		board[x][y] = -1;
		invertBoard[y][x] = -1;

		if (removePiece.isRed()) {
			redPieceArr[removePiece.getPieceIndex()] = -1;
		} else {
			blackPieceArr[removePiece.getPieceIndex()] = -1;
		}
	}

	/**
	 * 更换棋子位置
	 *
	 * @param piece	目标棋子
	 * @param sourcePosition	原位置
	 * @param targetPosition	目标位置
	 */
	private void changePiecePosition(AbstractChessPiece piece, int sourcePosition, int targetPosition) {
		ChessTools.removePiece(allPiece, sourcePosition);
		ChessTools.putPiece(allPiece, targetPosition, piece);

		byte[] pieceArr = piece.isRed() ? redPieceArr : blackPieceArr;
		pieceArr[piece.getPieceIndex()] = (byte) targetPosition;

		int x = ChessTools.fetchX(targetPosition);
		int y = ChessTools.fetchY(targetPosition);
		board[x][y] = piece.type();
		invertBoard[y][x] = piece.type();

		x = ChessTools.fetchX(sourcePosition);
		y = ChessTools.fetchY(sourcePosition);
		board[x][y] = -1;
		invertBoard[y][x] = -1;
	}

	public void print() {
		for (int i = 9; i >= 0; i--) {
			for (int j = 0; j < 9; j++) {
				AbstractChessPiece piece = allPiece[i][j];
				if (piece != null) {
					System.out.print(piece.getName() + "\t");
				} else {
					System.out.print(" " + "\t");
				}
			}
			System.out.println("\r\n");
		}
	}

	public void boardPrintToConsole() {
		StringBuilder sb = new StringBuilder();
		for (int x = 0; x < 10; x++) {
			for (int y = 0; y < 9; y++) {
				AbstractChessPiece piece = allPiece[x][y];
				if (piece != null) {
					sb.append(piece.type());
				} else {
					sb.append("0");
				}
				sb.append("_");
			}
		}
		sb.deleteCharAt(sb.length() - 1);
		System.out.println(sb.toString());
	}

	public void loadPieceFromStr(String str) {
		String[] strArr = str.split("_");
		int index = 0;
		for (int x = 0; x < 10; x++) {
			for (int y = 0; y < 9; y++) {
				int type = Integer.parseInt(String.valueOf(strArr[index++]));
				if (type == 0) {
					allPiece[x][y] = null;
				} else {
					AbstractChessPiece piece = ChessTools.toPiece(type);
					putPiece(ChessTools.toPosition(x, y), piece);
				}
			}
		}
	}


	public AbstractChessPiece[][] getAllPiece() {
		return allPiece;
	}

	public byte[] getRedPieceArr() {
		return redPieceArr;
	}

	public void setRedPieceArr(byte[] redPieceArr) {
		this.redPieceArr = redPieceArr;
	}

	public byte[] getBlackPieceArr() {
		return blackPieceArr;
	}

	public void setBlackPieceArr(byte[] blackPieceArr) {
		this.blackPieceArr = blackPieceArr;
	}

	public byte[][] getBoard() {
		return board;
	}

	public byte[][] getInvertBoard() {
		return invertBoard;
	}
}
