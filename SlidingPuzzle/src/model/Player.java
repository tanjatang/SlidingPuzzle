package model;

import java.util.ArrayList;
import java.util.List;

import model.PuzzleGame.action;

public abstract class Player {
	

	/**
	 * Implement this method to solve a given PuzzleGame. This method should return a List of actions
	 *  that lead to the solution state.
	 * @return List of actions to solve the board.
	 * @param game The puzzle game to solve
	 * 
	 */
	//执行解决游戏的一系列动作，返回一些列解决的action
	//public abstract List<action> solve(PuzzleGame game){
	public abstract List<action> solve(PuzzleGame game);
	/*{
		List<action> result = new ArrayList<>();
		Integer[][] currentBoard = game.getGameBoard();
		boolean won=game.isSolution(currentBoard);
		while(!won) {
			for(int i=1;i<=20;i++) {
			action[] allowedActions = game.getPossibleActions(currentBoard);
			int nextAction=(int)Math.ceil(game.getHeuristicValue(currentBoard)* allowedActions.length)-1;	
			action next = allowedActions[nextAction];
			currentBoard=game.computeAction(next, currentBoard);
			result.add(next);
		}
		
			
		}
		return result;
		*/
		/*for (int i=0; i<20; ++i) {
			action[] allowedActions = game.getPossibleActions(currentBoard);
			int nextAction =(int)Math.ceil(Math.random()* allowedActions.length)-1;			
			action next = allowedActions[nextAction];
			currentBoard = game.computeAction(next, currentBoard);
			result.add(next);
			}*/
	
		
		
	//}
	
	
	/**
	 * Finds a solution and applies it to the board. Only used by the GUI. 
	 * @param game The board game.
	 */
	//找出solution并且应用于board。只能被GUI使用
	public void solveAndApply(PuzzleGame game) {
		List<action> solution = solve(game);
		for (int i=0;i< solution.size(); ++i) {
			game.performAction(solution.get(i));
		}
	}
	
	
}
