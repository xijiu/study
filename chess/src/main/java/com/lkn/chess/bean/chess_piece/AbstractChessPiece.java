package com.lkn.chess.bean.chess_piece;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.lkn.chess.ChessTools;
import com.lkn.chess.PubTools;
import com.lkn.chess.bean.ChessBoard;
import com.lkn.chess.bean.Role;
import com.lkn.chess.bean.Position;

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
	private Integer fightVal;	// 棋子战斗力（某个数值，战斗力会随着棋势的进行发生变更）
	protected Integer fightDefaultVal;	// 棋子默认战斗力，不会发生改变
	private final Role PLAYER_ROLE;	// 象棋先后手
	private boolean isAlive;	// 是否在战斗，默认为true
	private Position currPosition;	// 棋子当前的位置
	// 每种棋子对应唯一一个num值
	protected int num = 0;
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
	protected byte[] reachablePositions = new byte[19];
	protected int reachableNum = 0;
	
	public AbstractChessPiece cloneImpl(Position position){
		AbstractChessPiece clone = clone();
		clone.setCurrPosition(position);
		return clone;
	}
	
	/**
	 * 实现对象克隆
	 */
	public AbstractChessPiece clone(){
		AbstractChessPiece clone = null;
		try {
			clone = (AbstractChessPiece)super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return clone;
	}

	protected boolean isEnemy(AbstractChessPiece piece1, AbstractChessPiece piece2) {
		return piece1.getPLAYER_ROLE() != piece2.getPLAYER_ROLE();
	}
	
	/**
	 * 获取当前子力能够到达的位置
	 * @author:likn1	Jan 5, 2016  5:20:28 PM
	 * @return
	 */
	public abstract Map<String, Position> getReachablePositions(ChessBoard board);

	public abstract byte[] getReachablePositions(int currPosition, ChessBoard board);

	public abstract int valuation(ChessBoard board, int position);

	public abstract int walkAsManual(ChessBoard board, int startPos, char cmd1, char cmd2);

	public abstract byte type();

//	public abstract boolean canEat(ChessBoard board, int currPos, int targetPos);

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

	public Integer getFightVal() {
		return fightVal;
	}

	public void setFightVal(Integer fightVal) {
		this.fightVal = fightVal;
	}

	public boolean isAlive() {
		return isAlive;
	}

	public void setAlive(boolean isFight) {
		this.isAlive = isFight;
	}

	public Role getPLAYER_ROLE() {
		return PLAYER_ROLE;
	}

	public boolean isRed() {
		return PLAYER_ROLE == Role.RED;
	}

	public Position getCurrPosition() {
		return currPosition;
	}
	
	/**
	 * 设置棋子的默认攻击力，同时为fightVal设置值，一般只在构造方法中调用
	 * @param fightDefaultVal
	 */
	public void setFightDefaultVal(Integer fightDefaultVal) {
		this.fightDefaultVal = fightDefaultVal;
		this.setFightVal(fightDefaultVal);
	}
	
	public Integer getFightDefaultVal() {
		return fightDefaultVal;
	}

	protected void recordReachablePosition(int position) {
		reachableNum++;
		reachablePositions[reachableNum] = (byte) position;
	}

	protected boolean isValid(int x, int y) {
		return x >= 0 && x < 10 && y >= 0 && y < 9;
	}

	protected boolean hasPiece(Map<Integer, AbstractChessPiece> allPiece, int x, int y) {
		return allPiece.get(ChessTools.toPosition(x, y)) != null;
	}

	protected int findPositionByName(Map<Integer, AbstractChessPiece> allPiece, String name) {
		for (Map.Entry<Integer, AbstractChessPiece> entry : allPiece.entrySet()) {
			if (entry.getValue().getName().equals(name)) {
				return entry.getKey();
			}
		}

		System.out.println(" --------- ");
		for (Map.Entry<Integer, AbstractChessPiece> entry : allPiece.entrySet()) {
			System.out.println(entry.getValue().getName());
		}
		throw new RuntimeException();
	}

	/**
	 * 设置棋子的位置
	 * @param currPosition
	 * @param changeFightValue	true:改变战斗力	false:不改变战斗力
	 */
	public void setCurrPosition(Position currPosition, boolean changeFightValue) {
		setCurrPosition(currPosition);
		if(changeFightValue){
			pieceValueChange();
		}
	}
	
	/**
	 * 设置棋子的位置
	 * @param currPosition
	 */
	public void setCurrPosition(Position currPosition) {
		this.currPosition = currPosition;
		if(currPosition != null){
			setAlive(true);
			currPosition.setExistPiece(true);
			currPosition.setPiece(this);
		}else {
			setAlive(false);
		}
	}

	/**
	 * 当前的位置变化，可能影响棋子的战斗力、权值变化
	 * @author:likn1	Jan 22, 2016  6:42:56 PM
	 */
	private void pieceValueChange() {
		int changeVal = 0;
		Integer x = currPosition.getX();
		Integer y = currPosition.getY();
		if(PLAYER_ROLE == Role.RED){	// 先手
			changeVal = VAL_RED[y][x];
		} else {	// 后手
			changeVal = VAL_BLACK[y][x];
		}
		setFightVal(fightDefaultVal + changeVal);	// 设置战斗力
	}
	
	/**
	 * 棋谱记录
	 * @param begin
	 * @param end
	 * @param board
	 * @return
	 */
	public abstract String chessRecordes(Position begin, Position end, ChessBoard board);
	
	/**
	 * 可以直来直去走的棋子，有“车”，“将”，“炮”，“兵”
	 * @param begin
	 * @param end
	 * @param board
	 * @return
	 */
	protected String chessRecordesStraight(Position begin, Position end, ChessBoard board){
		StringBuffer sb = new StringBuffer();
		appendFirstAndSecondWord(sb, begin, board);	// 拼接第一个、第二个棋谱字符
		/**第三个字*****************************************/
		String thirdWord = judgeForwardAndBackoff(begin, end);
		sb.append(thirdWord);
		/**第四个字*****************************************/
		String forthStr = null;
		if(begin.getX() == end.getX()){
			int forthNum = Math.abs(end.getY() - begin.getY());
			forthStr = this.getPLAYER_ROLE() == Role.RED ? ChessTools.getRedRecordesArr()[forthNum - 1] : ChessTools.getBlackRecordesArr()[forthNum - 1];
		} else {	// 平
			forthStr = ChessTools.getRecordeShowByX(this.getPLAYER_ROLE(), end.getX());	// 通过X坐标，返回该棋子的棋谱正常显示
		}
		sb.append(forthStr);
		return sb.toString();
	}

	/**
	 * 不能直来直去走的棋子，有“马”，“相”，“士”
	 * @param begin
	 * @param end
	 * @param board
	 * @return
	 */
	protected String chessRecordesCurved(Position begin, Position end, ChessBoard board){
		StringBuffer sb = new StringBuffer();
		appendFirstAndSecondWord(sb, begin, board);	// 拼接第一个、第二个棋谱字符
		/**第三个字*****************************************/
		String thirdWord = judgeForwardAndBackoff(begin, end);
		sb.append(thirdWord);
		/**第四个字*****************************************/
		String forthStr = ChessTools.getRecordeShowByX(this.getPLAYER_ROLE(), end.getX());	// 通过X坐标，返回该棋子的棋谱正常显示
		sb.append(forthStr);
		return sb.toString();
	}
	
	/**
	 * 返回棋谱中的第三个字，“进” 或 “退” 或 “平”
	 * @param begin
	 * @param end
	 * @return
	 */
	private String judgeForwardAndBackoff(Position begin, Position end) {
		String thirdWord = null;
		if(begin.getY() != end.getY()){
			thirdWord = end.getY() > begin.getY() ? (this.getPLAYER_ROLE() == Role.RED ? "进" : "退") : (this.getPLAYER_ROLE() == Role.RED ? "退" : "进");
		}else {
			thirdWord = "平";
		}
		return thirdWord;
	}
	
	/**
	 * 拼接4个字的棋谱中的第一个、第二个字符
	 * @param sb
	 * @param begin
	 * @param board 
	 */
	private void appendFirstAndSecondWord(StringBuffer sb, Position begin, ChessBoard board) {
		boolean existOtherPiece = false;
		Position currPosition = this.getCurrPosition();
		Role ROLE = this.getPLAYER_ROLE();
		Map<String, Position> map = ChessTools.getAllChess_Y(board.getPositionMap(), begin);
		for (Position position : map.values()) {
			AbstractChessPiece piece = position.getPiece();
			/**存在棋子、且在战斗、且与当前棋子同属一角色、且与当前棋子名字一样*/
			if(position.isExistPiece() && piece.isAlive && piece.PLAYER_ROLE == this.PLAYER_ROLE && piece.getName().equals(this.getName()) && !position.isSameXandY(currPosition)){
				String firstWord = position.getY() > currPosition.getY() ? (ROLE == Role.RED ? "后" : "前") : (ROLE == Role.RED ? "前" : "后");
				/**拼接第一个、第二个字符*/
				sb.append(firstWord);
				sb.append(this.getName());
				existOtherPiece = true;
				break;
			}
		}
		/**如果不存在两个相同类型的棋子在同一个X轴上*/
		if(!existOtherPiece){
			/**第一个字*****************************************/
			sb.append(this.getName());
			/**第二个字*****************************************/
			String secondWord = ChessTools.getRecordeShowByX(this.getPLAYER_ROLE(), begin.getX());	// 通过X坐标，返回该棋子的棋谱正常显示
			sb.append(secondWord);
		}
	}
	public abstract Position walkRecorde(ChessBoard board, String third, String forth);
	
	/**
	 * 直来直去的行走方式
	 * @author:likn1	Feb 4, 2016  3:18:10 PM
	 * @param board
	 * @param third
	 * @param forth
	 * @return
	 */
	protected Position walkRecordeStraight(ChessBoard board, String third, String forth){
		Position position = null;
		Integer currX = this.getCurrPosition().getX();
		Integer currY = this.getCurrPosition().getY();
		int X = currX;
		int Y = currY;
		if(third.equals("进")){
			int number = getForthNumber(String.valueOf(forth));
			Y = this.getPLAYER_ROLE() == Role.RED ? currY + number : currY - number;
		} else if(third.equals("退")){
			int number = getForthNumber(String.valueOf(forth));
			Y = this.getPLAYER_ROLE() == Role.RED ? currY - number : currY + number;
		} else if(third.equals("平")){
			X = ChessTools.getXByRecordeShow(this.getPLAYER_ROLE(), String.valueOf(forth));
		}
		position = isInReachableMap(board.getPositionMap().get(ChessTools.getPositionID(X, Y)), board);
		return position;
	}

	private int getForthNumber(String forth) {
		int returnNum = -1;
		for (int i = 0; i < ChessTools.getRedRecordesArr().length; i++) {
			if(ChessTools.getRedRecordesArr()[i].equals(forth)){
				returnNum = i + 1;
				break;
			}
		}
		for (int i = 0; i < ChessTools.getBlackRecordesArr().length; i++) {
			if(ChessTools.getBlackRecordesArr()[i].equals(forth)){
				returnNum = i + 1;
				break;
			}
		}
		return returnNum;
	}
	
	protected Position walkRecordeCurved(ChessBoard board, String third, String forth){
		Position returnPosition = null;
		int X = ChessTools.getXByRecordeShow(this.getPLAYER_ROLE(), String.valueOf(forth));;
		Set<Position> set = new HashSet<Position>();
		Map<String, Position> map = this.getReachablePositions(board);
		for (Position position : map.values()) {
			if(position.getX().equals(X)){
				set.add(position);
			}
		}
		if(set.size() == 0){
		}else if(set.size() == 1){
			Position tempPostion = PubTools.getSetIndexEle(set, 0);
			String direction = ChessTools.judgePieceDirection(this.getCurrPosition().getY(), tempPostion.getY(), this.getPLAYER_ROLE());
			if(direction.equals(third)){
				returnPosition = tempPostion;
			}
		}else {
			Position p1 = PubTools.getSetIndexEle(set, 0);
			Position p2 = PubTools.getSetIndexEle(set, 1);
			if(third.equals("进")){
				returnPosition = this.getPLAYER_ROLE() == Role.RED ? (p2.getY() > p1.getY() ? p2 : p1) : (p2.getY() > p1.getY() ? p1 : p2);
			} else if(third.equals("退")){
				returnPosition = this.getPLAYER_ROLE() == Role.RED ? (p2.getY() > p1.getY() ? p1 : p2) : (p2.getY() > p1.getY() ? p2 : p1);
			}
		}
		returnPosition = isInReachableMap(returnPosition, board);
		return returnPosition;
	}
	
	
	private Position isInReachableMap(Position target, ChessBoard board){
		Position returnPosition = null;
		if(target != null){
			for (Position position : this.getReachablePositions(board).values()) {
				if(position.getID().equals(target.getID())){
					returnPosition = position;
					break;
				}
			}
		}
		return returnPosition;
	}
	
}
