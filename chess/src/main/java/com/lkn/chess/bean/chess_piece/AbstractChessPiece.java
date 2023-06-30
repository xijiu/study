package com.lkn.chess.bean.chess_piece;

import com.lkn.chess.ChessTools;
import com.lkn.chess.PubTools;
import com.lkn.chess.bean.ChessBoard;
import com.lkn.chess.bean.Position;
import com.lkn.chess.bean.Role;

import java.util.Arrays;

/**
 * 象棋中棋子的抽象类，每个旗子都设置一个唯一编号
 *
 *
 * 车10	马11 象12 士13 将14 士13 象12	马11 车10
 *
 *
 *
 *  	炮9 	 	 	 	 	炮9
 *
 *
 * 卒8	 	卒8 	卒8		卒8		卒8
 *
 *
 *
 *
 * 兵7	 	兵7 	兵7 	兵7	    兵7
 *
 *
 *     炮6	 	 	 	 	 	炮6
 *
 *
 *
 * 车1	马2	相3	仕4	帅5	仕4	相3	马2	车1
 */
public abstract class AbstractChessPiece implements Cloneable {
	private String id;			// 棋子唯一的编号
	private String name;		// 棋子名称
	private String showName;		// 棋子名称
	private Integer fightVal;	// 棋子战斗力（某个数值，战斗力会随着棋势的进行发生变更）
	protected Integer defaultVal;	// 棋子默认战斗力，不会发生改变
	private final Role PLAYER_ROLE;	// 象棋先后手
	private Position currPosition;	// 棋子当前的位置
	// 每种棋子对应唯一一个num值
	protected int num = 0;
	private int pieceIndex = 0;
	protected int VAL_BLACK[][] = {	// 先手方的位置与权值加成
			{  0,  0,  0,  0,  0,  0,  0,  0,  0},
			{  0,  0,  0,  0,  0,  0,  0,  0,  0},
			{  0,  0,  0,  0,  0,  0,  0,  0,  0},
			{  0,  0,  0,  0,  0,  0,  0,  0,  0},
			{  0,  0,  0,  0,  0,  0,  0,  0,  0},
			{  0,  0,  0,  0,  0,  0,  0,  0,  0},
			{  0,  0,  0,  0,  0,  0,  0,  0,  0},
			{  0,  0,  0,  0,  0,  0,  0,  0,  0},
			{  0,  0,  0,  0,  0,  0,  0,  0,  0},
			{  0,  0,  0,  0,  0,  0,  0,  0,  0}
		};
	protected int VAL_RED[][] = PubTools.arrChessReverse(VAL_BLACK);	// 后手方的位置与权值加成

	/**
	 * 迭代深度不超过10层
	 * 这个对象仅为了辅助使用，每一层提供一个byte[19]，避免重复创建byte数组
	 */
	protected byte[][] reachableHelper = new byte[50][19];

	public AbstractChessPiece(String id, Role PLAYER_ROLE) {
		this.id = id;
		this.PLAYER_ROLE = PLAYER_ROLE;
	}

	/**
	 * 初始化设置num属性
	 *
	 * @param role	当前旗子的角色
	 * @param redNum	先手的值
	 * @param blackNum	后手的值
	 */
	protected void initNum(Role role, int redNum, int blackNum) {
		this.num = role == Role.RED ? 6 : 9;
	}

	/** 第一个位置存放当前可达位置的总数量，后面的element存放具体的position */
	protected byte[] reachablePositions = null;
	protected int reachableNum = 0;


	/**
	 * 棋子可能被吃掉，例如一个马被4个卒夹击，那这个马的价值可能趋于0了
	 * 	 	卒
	 * 	  卒马卒
	 * 		卒
	 */
	protected int eatenValue(ChessBoard board, int currPosition, Role role) {
//		if (1 == 1) {
//			return 0;
//		}
		if (this.getPLAYER_ROLE() == role) {
			return 0;
		}
		int[] redValArr = board.getRedNextStepPositionArr()[currPosition];
		int[] blackValArr = board.getBlackNextStepPositionArr()[currPosition];

		int[] ownValArr = isRed() ? redValArr : blackValArr;
		int[] enemyValArr = isRed() ? blackValArr : redValArr;

		int ownLength = ownValArr[0];
		int enemyLength = enemyValArr[0];
		Arrays.sort(ownValArr, 1, ownLength + 1);
		Arrays.sort(enemyValArr, 1, enemyLength + 1);
		return calcEatenValue(ownValArr, enemyValArr);
	}

	private int calcEatenValue(int[] ownValArr, int[] enemyValArr) {
		int ownLength = ownValArr[0];
		int enemyLength = enemyValArr[0];
		int ownI = 1;
		int enemyI = 1;

		int enemyLossVal = 0;
		int ownLossVal = 0;
		int lastVal = this.getDefaultVal();
		while (true) {
			if (enemyI > enemyLength) {
				break;
			}
			int enemyVal = enemyValArr[enemyI++];
			ownLossVal += lastVal;
			lastVal = enemyVal;


			if (ownI > ownLength) {
				break;
			}
			int ownVal = ownValArr[ownI++];
			enemyLossVal += lastVal;
			lastVal = ownVal;
		}

		// 如果敌人损失的超过了我们自己损失的，那么认为当前棋子没有被吃的风险
		if (enemyLossVal >= ownLossVal) {
			return 0;
		} else {
			return ownLossVal - enemyLossVal;
		}
	}


	protected boolean isEnemy(AbstractChessPiece piece1, AbstractChessPiece piece2) {
		return piece1.getPLAYER_ROLE() != piece2.getPLAYER_ROLE();
	}

	protected boolean isEnemy(AbstractChessPiece piece, byte type) {
		if (piece.isRed()) {
			return type > 7;
		} else {
			return type > 0 && type <= 7;
		}
	}

	protected boolean isFriend(AbstractChessPiece piece1, AbstractChessPiece piece2) {
		return piece1.getPLAYER_ROLE() == piece2.getPLAYER_ROLE();
	}

	protected boolean isFriend(AbstractChessPiece piece, byte type) {
		if (piece.isRed()) {
			return type > 0 && type <= 7;
		} else {
			return type > 7;
		}
	}

	/**
	 * 目标棋子可达到的位置点
	 *
	 * @param currPosition	当前棋子位置
	 * @param board	棋盘
	 * @param containsProtectedPiece	返回的结果是否包含其可以保护的己方棋子
	 * @param level 从1开始
	 * @return	可达到的位置点
	 */
	public abstract byte[] getReachablePositions(int currPosition, ChessBoard board, boolean containsProtectedPiece, int level);

	/**
	 * 目标棋子可以吃到子的位置集合
	 *
	 * @param currPosition	当前位置
	 * @param board	棋盘
	 * @param level	层级，从1开始
	 * @return	可吃子的位置集合
	 */
	public byte[] getEatPositions(int currPosition, ChessBoard board, int level) {
		byte[] reachablePositions = getReachablePositions(currPosition, board, false, level);
		byte length = reachablePositions[0];
		int index = 1;
		for (int i = 1; i <= length; i++) {
			byte position = reachablePositions[i];
			if (ChessTools.getPiece(board.getAllPiece(), position) != null) {
				reachablePositions[index++] = position;
			}
		}
		reachablePositions[0] = (byte) index;
		return reachablePositions;
	}

	public abstract int valuation(ChessBoard board, int position, Role role);

	public abstract int walkAsManual(ChessBoard board, int startPos, char cmd1, char cmd2);

	public abstract byte type();

	/**
	 * 是否将军
	 */
	public abstract boolean kingCheck(ChessBoard board, int position);

	/**
	 * 寻找将的位置
	 */
	protected int findKingPos(AbstractChessPiece[][] allPiece, Role role) {
		if (role == Role.RED) {
			for (int x = 0; x < 3; x++) {
				for (int y = 3; y <= 5; y++) {
					AbstractChessPiece piece = allPiece[x][y];
					if (piece != null && piece.type() == 5) {
						return ChessTools.toPosition(x, y);
					}
				}
			}
		} else {
			for (int x = 7; x <= 9; x++) {
				for (int y = 3; y <= 5; y++) {
					AbstractChessPiece piece = allPiece[x][y];
					if (piece != null && piece.type() == 12) {
						return ChessTools.toPosition(x, y);
					}
				}
			}
		}
		throw new RuntimeException("can not find king");
	}

	/**
	 * 寻找将的位置
	 */
	protected int findKingPos(byte[][] board, Role role) {
		if (role == Role.RED) {
			for (int x = 0; x < 3; x++) {
				for (int y = 3; y <= 5; y++) {
					byte type = board[x][y];
					if (type == 5) {
						return ChessTools.toPosition(x, y);
					}
				}
			}
		} else {
			for (int x = 7; x <= 9; x++) {
				for (int y = 3; y <= 5; y++) {
					byte type = board[x][y];
					if (type == 12) {
						return ChessTools.toPosition(x, y);
					}
				}
			}
		}
		throw new RuntimeException("can not find king");
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShowName() {
		return showName;
	}

	public void setShowName(String showName) {
		this.showName = showName;
	}

	public Integer getFightVal() {
		return fightVal;
	}

	public void setFightVal(Integer fightVal) {
		this.fightVal = fightVal;
	}

	public Role getPLAYER_ROLE() {
		return PLAYER_ROLE;
	}

	public boolean isRed() {
		return PLAYER_ROLE == Role.RED;
	}

	/**
	 * 设置棋子的默认攻击力，同时为fightVal设置值，一般只在构造方法中调用
	 * @param defaultVal
	 */
	public void setDefaultVal(Integer defaultVal) {
		this.defaultVal = defaultVal;
		this.setFightVal(defaultVal);
	}
	
	public int getDefaultVal() {
		return defaultVal;
	}

	public int getPieceIndex() {
		return pieceIndex;
	}

	public void setPieceIndex(int pieceIndex) {
		this.pieceIndex = pieceIndex;
	}

	protected void recordReachablePosition(int position) {
		reachablePositions[++reachableNum] = (byte) position;
	}

	protected boolean isValid(int x, int y) {
		return x >= 0 && x < 10 && y >= 0 && y < 9;
	}

	protected int findKingPositionByName(byte[][] board) {
		if (isRed()) {
			for (int x = 7; x <= 9; x++) {
				for (int y = 3; y <= 5; y++) {
					byte type = board[x][y];
					if (type == 12) {
						return ChessTools.toPosition(x, y);
					}
				}
			}
		} else {
			for (int x = 0; x <= 2; x++) {
				for (int y = 3; y <= 5; y++) {
					byte type = board[x][y];
					if (type == 5) {
						return ChessTools.toPosition(x, y);
					}
				}
			}
		}

		System.out.println(" --------- ");
		throw new RuntimeException();
	}

	protected int findKingPositionByName(AbstractChessPiece[][] allPiece) {
		if (isRed()) {
			for (int x = 7; x <= 9; x++) {
				for (int y = 3; y <= 5; y++) {
					AbstractChessPiece piece = allPiece[x][y];
					if (piece != null && piece.type() == 12) {
						return ChessTools.toPosition(x, y);
					}
				}
			}
		} else {
			for (int x = 0; x <= 2; x++) {
				for (int y = 3; y <= 5; y++) {
					AbstractChessPiece piece = allPiece[x][y];
					if (piece != null && piece.type() == 5) {
						return ChessTools.toPosition(x, y);
					}
				}
			}
		}

		System.out.println(" --------- ");
		throw new RuntimeException();
	}
	
}
