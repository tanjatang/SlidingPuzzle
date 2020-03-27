package model;

import java.awt.print.Printable;
import java.util.Arrays;
import java.util.Observable;
import java.util.Vector;

import javax.xml.stream.events.ProcessingInstruction;

public class PuzzleGame extends Observable {

	
	public enum action {
		UP, DOWN, LEFT, RIGHT;
	} 

	private Integer[][] gameBoard;
	private int zX;
	private int zY;
	private Vector<action> log;
	private boolean won;
	private boolean wasReset;
	
	/**
	 * Standard Constructor
	 */
	public PuzzleGame() {
		gameBoard = new Integer[3][3];
		log = new Vector<>();
		wasReset=false;
		
		reset();
	}
	
	/**
	 * Setter for the GUI.
	 */
	public void unsetReset() {
		wasReset=false;
	}
	
	/**
	 * Checks, if the current state of the game is won. Used by the GUI.
	 * @return True if won, false otherwise
	 */
	//检测当前状态是不是won
	public boolean isWon() {
		return won;
	}
	
	/**
	 * Computes the possible actions for a given board
	 * @param board The board
	 * @return Array of possible actions
	 */
	//计算可能的action，但不实现移动
	public action[] getPossibleActions(Integer[][] board) {
		int zY=0;
		int zX=0;
		
		for (int i=0; i<board.length; ++i) {
			for(int j=0; j<board[i].length; ++j) {
				if (board[i][j] == 0) {
					zY=i;
					zX=j;
				}
			}
		}	
		
		Vector<action> result = new Vector<>();
			
		if (zY < 2) {
			result.add(action.DOWN);
		}
		if (zY > 0) {
			result.add(action.UP);
		}
		if (zX < 2) {
			result.add(action.RIGHT);
		}
		if (zX > 0) {
			result.add(action.LEFT);
		}		
		return result.toArray(new action[result.size()]);		
	}
	
	/**
	 * Performs an action on the current board of the game. This will result in 
	 * an actual change of the game state.
	 * @param a Action to perform
	 */
	//移动位置
	public void performAction(action a) {
		gameBoard = computeAction(a, this.gameBoard);
		log.addElement(a);
		for (int i=0; i<gameBoard.length; ++i) {
			for(int j=0; j<gameBoard[i].length; ++j) {
				if (gameBoard[i][j] == 0) {
					zY=i;
					zX=j;
				}
			}
		}
	}
	
	/**
	 * Standard getter for GUI
	 * @return x-Coordinate of the zero-tile
	 */
	public int getZX() {
		return zX;
	}
	
	/**
	 * Standard getter for GUI
	 * @return y-Coordinate of the zero-tile
	 */
	public int getZY() {
		return zY;
	}
	
	/**
	 * Computes the impact of an action to a given board. This method
	 * will not change the state of the actual puzzle. Use this method
	 * to explore new paths.
	 * @param a The action to perform.
	 * @param board The board to perfom the action on-
	 * @return The changed board.
	 */
	//找路线,f返回空位移动位置
	
	public Integer[][] computeAction(action a, Integer[][] board) {
		Integer[][] result = Utility.deepCopyIntegerArray(board);	
		int zY=0;
		int zX=0;
		for (int i=0; i<result.length; ++i) {
			for(int j=0; j<result[i].length; ++j) {
				if (result[i][j] == 0) {
					zY=i;   //设置空位位置
					zX=j;
				}
			}
		}
			
		switch (a) {    //交换空位位置
		case UP: {
			int temp = result[zY-1][zX];
			result[zY-1][zX]=0;
			result[zY][zX]=temp;
			zY -=1;
			break;
		}
		case DOWN: {
			int temp = result[zY+1][zX];
			result[zY+1][zX]=0;
			result[zY][zX]=temp;
			zY +=1;
			break;	
		}
		case LEFT: {
			int temp = result[zY][zX-1];
			result[zY][zX-1]=0;
			result[zY][zX]=temp;
			zX -=1;
			break;	
		}
		case RIGHT: {
			int temp = result[zY][zX+1];
			result[zY][zX+1]=0;
			result[zY][zX]=temp;
			zX +=1;
			break;	
		}
		}
		return result;
		
	}
	
	/**
	 * Standard getter for the current board.
	 * @return The current board.
	 */
	//返还board
	public Integer[][] getGameBoard() {
		return gameBoard;
	}
	
	
	/**
	 * Standard getter for GUI
	 * @return The log of performed actions.
	 */
	//记录动作
	
	public Vector<action> getLog() {
		return log;
	}
	
	/**
	 * Creates a solution state. Used in the constructor.
	 * @return 
	 * 
	 */
	//创建目标状态
	public Integer[][] makeSolutionState() {
		for (int i=0; i<gameBoard.length; ++i) {
			for (int j=0; j<gameBoard[i].length; ++j) {
				gameBoard[i][j]=i*3+j+1; //设置位置
			}
		}
		gameBoard[2][2]=0; //空位在最后？？
		zX=2;
		zY=2;
		return gameBoard;
		
		//Txj,用于测试
		/*Integer[][] test = gameBoard;
        for (int i=0;i<3;i++)
            System.out.println ( Arrays.toString (test[i]));
		//System.out.println(gameBoard);*/
	}
	/** Resets the board to an (unsolved) state. Used by the GUI.
	 *
	 */
	//rest board
	public void reset() {
		makeSolutionState();
		while (isSolution(this.gameBoard)) {
			randomizeBoard();
		}
		log.clear();
		won =false;
		wasReset=true;
		
		setChanged();
		notifyObservers();		
	}
	
	/**
	 * Returns an heuristic distance to the solution state for the given
	 * board. The heuristic is the Manhatten distance.
	 * @param board The board thats heuristics should be calculated.
	 * @return The heuristic distance.
	 */
	//返回h值，用曼哈顿方法
	public int getHeuristicValue(Integer[][] board) {
		int result =0;
		for (int i=0; i<board.length; ++i) {
			for (int j=0; j<board[i].length; ++j) {
				int targetCol;   //目标列
				int targetRow;   //目标行
				if (board[i][j] == 0) {   //设置board为3行3列，如果为空位，将位置定在位置9
					targetCol=2;
					targetRow=2;
				} else {
					targetCol = (board[i][j]-1) % 3; //计算列的位置
					targetRow = (int)Math.floor((board[i][j]-1)/ 3.0);//floor 返回不大于的最大整数 。计算行的位置

				}
				result += Math.abs(targetCol-j) + Math.abs(targetRow-i);//abs返回绝对值，计算h值
			}
		}
		return result;
	}
	
	
	/**
	 * Checks if a given board is in a solution state.
	 * @param board The board
	 * @return True if solved, false otherwise
	 */
	//检查board的状态是不是需要找solution。返回false待解决，返回true已经是goal状态
	public boolean isSolution(Integer[][] board) {
		boolean result=true;
		for (int i=0; i<board.length; ++i) {
			for (int j=0; j<board[i].length; ++j) {
				if (((i ==2 && j ==2) && board[i][j]!=0) || ((i!=2 || j!=2) && board[i][j] != (i*3+j+1))) {
					result=false;
				}
			}
		}
		return result;
	}
	
	/**
	 * Checks if this board is solved. Used by the GUI.
	 */
	//检查board是不是解决好了
	public void checkFinished() {
		won = isSolution(gameBoard);

		setChanged();//用来设置一个内部标志位注明数据发生了变化
		notifyObservers(); //notifyObservers()方法会去调用一个列表中所有的Observer的update()方法，通知它们数据发生了变化。
	}
	
	
	/**
	 * Used to create a random (solvable) puzzle.
	 */
	//创建一个随机的，待解决的puzzle
	private void randomizeBoard() {
		for (int i=0; i<100; ++i) {
			action[] actions = getPossibleActions(gameBoard);
			int next =(int)Math.ceil(Math.random()* actions.length)-1;//ceil向上取整计算，它返回的是大于或等于函数参数，并且与之最接近的整数。
			performAction(actions[next]);
		}	

	}
	
	/**
	 * Computes a string representation of the given board.
	 * @param board The board to be presented as a string.
	 * @return The string representation of the board.
	 */
	//得出一个给出的board的representation string
	public String boardToString(Integer[][] board) {
		String result="";
		for (int i=0; i<board.length; ++i) {
			String l="";
			for (int j=0; j<board[i].length; ++j) {
				l+=" "+board[i][j];
			}
			result += l.substring(1)+"\n";//截取掉result从首字母起长度为1的字符串，将剩余字符串赋值给result；
		}
		return result;
		
	}
	
	
	/**
	 * Checks if the last action was a reset. Only used for GUI
	 * @return Reset status
	 */
	//检查是否按下reset健，返回是否result的结果
	public boolean wasReset() {
		return wasReset;
	}

//返回game board的string
	@Override
	public String toString() {
		return boardToString(gameBoard);
		//for(int i=0;i<3;i++);
		//System.out.println(gameBoard[i]);
	}
}
