package com.lkn.chess.bean;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.lkn.chess.Conf;
import com.lkn.chess.bean.chess_piece.AbstractChessPiece;
import com.lkn.chess.bean.chess_piece.Cannons;
import com.lkn.chess.ChessTools;
import com.lkn.chess.bean.chess_piece.Elephants;
import com.lkn.chess.bean.chess_piece.Horse;
import com.lkn.chess.bean.chess_piece.King;
import com.lkn.chess.bean.chess_piece.Mandarins;
import com.lkn.chess.bean.chess_piece.Pawns;
import com.lkn.chess.bean.chess_piece.Rooks;
import sun.jvm.hotspot.oops.BitData;
import sun.jvm.hotspot.utilities.BitMap;
import sun.jvm.hotspot.utilities.Bits;
import sun.security.util.BitArray;

/**
 * 棋盘的实体bean
 * 
 * @author:likn1 Jan 5, 2016 2:10:49 PM
 */
public class ChessBoard {
	private Map<String, Position> positionMap = new HashMap<String, Position>(90); // 位置的map
	/** 存放全量的旗子，key为旗子的位置，val是对象 */
	private Map<Integer, AbstractChessPiece> allPiece = new HashMap<>();
	private Map<Integer, AbstractChessPiece> redPiece = new HashMap<>();
	private Map<Integer, AbstractChessPiece> blackPiece = new HashMap<>();
	private Role currWalkRole = Role.RED; // 当前该走子的角色，默认为先手
	private StringBuffer chessRecordesBuffer = new StringBuffer();	// 记录棋谱
	private List<RoundTurn> chessRecordesList = new ArrayList<RoundTurn>();	// 棋谱记录的列表

	/** 10行9列的棋盘 */
	private final byte[][] board = new byte[10][9];

	public List<RoundTurn> getChessRecordesList() {
		return chessRecordesList;
	}

	public Map<String, Position> getPositionMap() {
		return positionMap;
	}

	public Role getCurrWalkRole() {
		return currWalkRole;
	}

	public void setCurrWalkRole(Role currWalkRole) {
		this.currWalkRole = currWalkRole;
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
				AbstractChessPiece piece = allPiece.get(ChessTools.toPosition(x, y));
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

	public byte[] snapshotConvert() {
		BitArray bitArray = new BitArray(218);
		int index = 0;
		int dataIndex = 90;
		for (int x = 0; x < 10; x++) {
			for (int y = 0; y < 9; y++) {
				AbstractChessPiece piece = allPiece.get(ChessTools.toPosition(x, 8 - y));
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

	public static void main(String[] args) {
		byte a = 14;
		String s = Integer.toBinaryString(a);
		System.out.println(s);
	}

	/**
	 * 棋盘的初始化工作
	 * 
	 * @author:likn1 Jan 5, 2016 2:13:27 PM
	 */
	public void init() {
		initBoard();
//		initForTest();
	}

	private void initForTest() {
		board[0][0] = Rooks.RED_NUM;
		putPiece(0, new Rooks(Role.RED));
		board[0][1] = Horse.RED_NUM;
		putPiece(1, new Horse(Role.RED));
		board[2][1] = Cannons.RED_NUM;
		putPiece(21, new Cannons(Role.RED));




		board[9][2] = Rooks.BLACK_NUM;
		putPiece(92, new Rooks(Role.BLACK));
		board[9][1] = Horse.BLACK_NUM;
		putPiece(91, new Horse(Role.BLACK));
		board[7][1] = Cannons.BLACK_NUM;
		putPiece(71, new Cannons(Role.BLACK));
	}

	private void initBoard() {
		board[0][0] = Rooks.RED_NUM;
		putPiece(0, new Rooks(Role.RED));
		board[0][1] = Horse.RED_NUM;
		putPiece(1, new Horse(Role.RED));
		board[0][2] = Elephants.RED_NUM;
		putPiece(2, new Elephants(Role.RED));
		board[0][3] = Mandarins.RED_NUM;
		putPiece(3, new Mandarins(Role.RED));
		board[0][4] = King.RED_NUM;
		putPiece(4, new King(Role.RED));
		board[0][5] = Mandarins.RED_NUM;
		putPiece(5, new Mandarins(Role.RED));
		board[0][6] = Elephants.RED_NUM;
		putPiece(6, new Elephants(Role.RED));
		board[0][7] = Horse.RED_NUM;
		putPiece(7, new Horse(Role.RED));
		board[0][8] = Rooks.RED_NUM;
		putPiece(8, new Rooks(Role.RED));


		board[2][1] = Cannons.RED_NUM;
		putPiece(21, new Cannons(Role.RED));
		board[2][7] = Cannons.RED_NUM;
		putPiece(27, new Cannons(Role.RED));


		board[3][0] = Pawns.RED_NUM;
		putPiece(30, new Pawns(Role.RED));
		board[3][2] = Pawns.RED_NUM;
		putPiece(32, new Pawns(Role.RED));
		board[3][4] = Pawns.RED_NUM;
		putPiece(34, new Pawns(Role.RED));
		board[3][6] = Pawns.RED_NUM;
		putPiece(36, new Pawns(Role.RED));
		board[3][8] = Pawns.RED_NUM;
		putPiece(38, new Pawns(Role.RED));


		// --------------------------以下是后手的旗子----------------------------


		board[6][0] = Pawns.BLACK_NUM;
		putPiece(60, new Pawns(Role.BLACK));
		board[6][2] = Pawns.BLACK_NUM;
		putPiece(62, new Pawns(Role.BLACK));
		board[6][4] = Pawns.BLACK_NUM;
		putPiece(64, new Pawns(Role.BLACK));
		board[6][6] = Pawns.BLACK_NUM;
		putPiece(66, new Pawns(Role.BLACK));
		board[6][8] = Pawns.BLACK_NUM;
		putPiece(68, new Pawns(Role.BLACK));


		board[7][1] = Cannons.BLACK_NUM;
		putPiece(71, new Cannons(Role.BLACK));
		board[7][7] = Cannons.BLACK_NUM;
		putPiece(77, new Cannons(Role.BLACK));


		board[9][0] = Rooks.BLACK_NUM;
		putPiece(90, new Rooks(Role.BLACK));
		board[9][1] = Horse.BLACK_NUM;
		putPiece(91, new Horse(Role.BLACK));
		board[9][2] = Elephants.BLACK_NUM;
		putPiece(92, new Elephants(Role.BLACK));
		board[9][3] = Mandarins.BLACK_NUM;
		putPiece(93, new Mandarins(Role.BLACK));
		board[9][4] = King.BLACK_NUM;
		putPiece(94, new King(Role.BLACK));
		board[9][5] = Mandarins.BLACK_NUM;
		putPiece(95, new Mandarins(Role.BLACK));
		board[9][6] = Elephants.BLACK_NUM;
		putPiece(96, new Elephants(Role.BLACK));
		board[9][7] = Horse.BLACK_NUM;
		putPiece(97, new Horse(Role.BLACK));
		board[9][8] = Rooks.BLACK_NUM;
		putPiece(98, new Rooks(Role.BLACK));
	}

	private void putPiece(int position, AbstractChessPiece piece) {
		allPiece.put(position, piece);
		if (piece.isRed()) {
			redPiece.put(position, piece);
		} else {
			blackPiece.put(position, piece);
		}
	}

	/**
	 * 悔棋，walk的逆过程
	 */
	public void unWalk(int sourcePosition, int targetPosition, AbstractChessPiece eatenPiece) {
//		if (1 == 1) {
//			return;
//		}
		AbstractChessPiece piece = allPiece.get(targetPosition);
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
		allPiece.put(position, piece);
		Map<Integer, AbstractChessPiece> pieceMap = piece.isRed() ? redPiece : blackPiece;
		pieceMap.put(position, piece);
	}

	/**
	 * 执行走棋的动作
	 *
	 * @param sourcePosition	起始位置
	 * @param targetPosition	目标位置
	 * @return	如果发生了吃子儿行为，那么返回吃掉的子儿，否则返回null
	 */
	public AbstractChessPiece walk(int sourcePosition, int targetPosition) {
//		if (1 == 1) {
//			return null;
//		}
		AbstractChessPiece piece = allPiece.get(sourcePosition);
		AbstractChessPiece targetPiece = allPiece.get(targetPosition);
		if (targetPiece != null) {
			removePiece(targetPosition);
		}
		changePiecePosition(piece, sourcePosition, targetPosition);
		return targetPiece;
	}

	/**
	 * 将目标位置的棋子从棋盘上拿下
	 *
	 * @param removePosition	目标位置
	 */
	private void removePiece(int removePosition) {
		AbstractChessPiece piece = allPiece.remove(removePosition);

		Map<Integer, AbstractChessPiece> pieceMap = piece.isRed() ? redPiece : blackPiece;
		pieceMap.remove(removePosition);
	}

	/**
	 * 更换棋子位置
	 *
	 * @param piece	目标棋子
	 * @param sourcePosition	原位置
	 * @param targetPosition	目标位置
	 */
	private void changePiecePosition(AbstractChessPiece piece, int sourcePosition, int targetPosition) {
		allPiece.remove(sourcePosition);
		allPiece.put(targetPosition, piece);

		Map<Integer, AbstractChessPiece> pieceMap = piece.isRed() ? redPiece : blackPiece;
		pieceMap.remove(sourcePosition);
		pieceMap.put(targetPosition, piece);
	}


	/**
	 * 克隆当前对象
	 */
	public ChessBoard clone() {
		ChessBoard clone = new ChessBoard();
		for (Position position : this.getPositionMap().values()) {
			clone.getPositionMap().put(position.getID(), position.clone());
		}
		clone.setCurrWalkRole(this.getCurrWalkRole());
		return clone;
	}


	/**
	 * 获取某一角色角色下的所有棋子
	 * 
	 * @param role
	 * @return
	 */
	public Set<AbstractChessPiece> getPiecesByPlayRole(Role role) {
		Set<AbstractChessPiece> set = new HashSet<AbstractChessPiece>();
		Collection<Position> collection = positionMap.values();
		for (Position position : collection) {
			if (position.isExistPiece() && position.getPiece().getPLAYER_ROLE().equals(role) && position.getPiece().isAlive()) {
				set.add(position.getPiece());
			}
		}
		return set;
	}

	/**
	 * 获取某一角色比对方角色高出多少
	 * 
	 * @param role
	 * @return
	 */
	public Integer getHigherFightValByRole(Role role) {
		int myselfTotal = 0; // 我方战斗力
		int opponentTotal = 0; // 对方战斗力
		Collection<Position> collection = positionMap.values();
		for (Position position : collection) {
			AbstractChessPiece piece = position.getPiece();
			if (piece != null) { // 此位置有棋子的前提下
				if (position.isExistPiece() && piece.isAlive()) {
					if (piece.getPLAYER_ROLE().equals(role)) {
						myselfTotal = myselfTotal + piece.getFightVal();
					} else {
						opponentTotal = opponentTotal + piece.getFightVal();
					}
				}
			}
		}
		return myselfTotal - opponentTotal;
	}

	/**
	 * 棋盘输出
	 * @author:likn1	Jan 12, 2016  10:07:53 AM
	 */
	public void show(){
		for (int y = 9; y >= 0; y--) {
			for (int x = 0; x <= 8; x++) {
				Position position = positionMap.get(ChessTools.getPositionID(x, y));
				if(position.isExistPiece()){
					System.out.print(position.getPiece().getName() + "\t");
				}else {
					System.out.print(" " + "\t");
				}
			}
			System.out.println("\r\n\r\n");
		}
	}

	public void print() {
		for (int i = 9; i >= 0; i--) {
			for (int j = 0; j < 9; j++) {
				int position = ChessTools.toPosition(i, j);
				AbstractChessPiece piece = allPiece.get(position);
				if (piece != null) {
					System.out.print(piece.getName() + "\t");
				} else {
					System.out.print(" " + "\t");
				}
			}
			System.out.println("\r\n");
		}
	}
	
	/**
	 * 记录棋谱
	 * @param begin
	 * @param end
	 */
	public void chessRecordes(Position begin, Position end){
		AbstractChessPiece piece = end.getPiece();
		String recorde = piece.chessRecordes(begin, end, this);
		chessRecordesBuffer.append(recorde).append(" ");
		recordesToList(begin, end);
	}

	/**
	 * 向list中加入走棋的路径
	 * @author:likn1	Feb 22, 2016  3:17:39 PM
	 * @param begin
	 * @param end
	 */
	private void recordesToList(Position begin, Position end) {
		Role role = null;
		if(begin.isExistPiece() && begin.getPiece().isAlive()){
			role = begin.getPiece().getPLAYER_ROLE();
		} else {
			role = end.getPiece().getPLAYER_ROLE();
		}
		if(role == Role.RED){
			RoundTurn turn = new RoundTurn(begin, end, null, null);
			chessRecordesList.add(turn);
		} else {
			RoundTurn turn = chessRecordesList.get(chessRecordesList.size() - 1);
			turn.setBlackPosition(begin, end);
		}
	}
	
	public StringBuffer getChessRecordesBuffer() {
		return chessRecordesBuffer;
	}
	
	/**
	 * 当前棋盘情况转换为一个长度为90的字符串
	 * @author:likn1	Feb 1, 2016  2:32:13 PM
	 * @return
	 */
	public String currPiecesStr(){
		StringBuffer sb = new StringBuffer();
		for (int x = 0; x <= 8; x++) {
			for (int y = 0; y <= 9; y++) {
				Position position = positionMap.get(ChessTools.getPositionID(x, y));
				if(position.isExistPiece() && position.getPiece().isAlive()){
					sb.append(Conf.getPieceNumberAndFlagMap().get(position.getPiece().getId()));
				} else {
					sb.append("0");
				}
			}
		}
		return sb.toString();
	}
	
	/**
	 * 判断将是否被吃掉
	 * 如果将被吃掉，意味着游戏结束
	 * @author:likn1	Feb 19, 2016  3:04:45 PM
	 * @return
	 */
	public boolean isKingEaten(){
		boolean isEaten = true;
		Set<AbstractChessPiece> set1 = ChessTools.getPieceByName(this, "帅", Role.RED);
		Set<AbstractChessPiece> set2 = ChessTools.getPieceByName(this, "将", Role.BLACK);
		if(set1 != null && set1.size() == 1 && set2 != null && set2.size() == 1){
			isEaten = false;
		}
		return isEaten;
	}

	public Map<Integer, AbstractChessPiece> getAllPiece() {
		return allPiece;
	}

	public Map<Integer, AbstractChessPiece> getRedPiece() {
		return redPiece;
	}

	public Map<Integer, AbstractChessPiece> getBlackPiece() {
		return blackPiece;
	}
}
