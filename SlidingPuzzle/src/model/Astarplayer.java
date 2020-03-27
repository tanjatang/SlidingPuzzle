package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import model.PuzzleGame.action;


public class Astarplayer extends Player{
	/**
	 * creat a game tree
	 * @author Tang & Wang
	 *
	 */
	//-----------------------------------------------------------
	public class Node {
		private Integer[][] board;		
		private action next_explore;
		private Node parent;
		private int h;
		private int g;
		public  Node() {			
			board = new Integer[3][3];
			parent = null;
			next_explore = null;
		}			

		public action get_next_explore() {
			return next_explore;
		}
		public void set_next_explore(action next_explore) {
			this.next_explore = next_explore;
		}
		public Node get_parent() {
			return parent;
		}
		public void set_parent(Node explored) {
			this.parent = explored;
		}
		public Integer[][] get_board(){
			return board;
		}
		public void set_board(Integer[][] board) {
			this.board = board;
		}
		public int get_HeuristicValue() {
			return h;
		}
		
		public void set_HeuristicValue(int h) {
			this.h = h;
		}
		public int get_g_value() {
			return g;
		}
		public void set_g_value(int node_deep) {
			this.g = node_deep;
		}
			
	};
	
	
	@Override
	public List<action> solve(PuzzleGame game){
		Integer[][] initial_board = game.getGameBoard();
		List<action> result = new ArrayList<>();
	
		//--------------------------------------
		Node root = new Node();
		root.set_board(initial_board);
		/**
		 * choose the smallest f(f=g+h) value of all of the frontiers to get next action path
		 */
		Comparator<Node> comparator =  new Comparator<Node>(){

			@Override
			public int compare(Node N1, Node N2) {
				// TODO Auto-generated method stub
				if(N1.get_HeuristicValue()+N1.get_g_value()>N2.get_HeuristicValue()+N2.get_g_value())
					return 1;
				if(N1.get_HeuristicValue()+N1.get_g_value()<N2.get_HeuristicValue()+N2.get_g_value())
					return -1;
				else
					return 0;
			}
			};
		Queue<Node> frontier_queue = new PriorityQueue<>(comparator);		
		List<Node> explored_list = new ArrayList<>();
		root.set_g_value(0);
		root.set_HeuristicValue(game.getHeuristicValue(initial_board));
		frontier_queue.add(root);
		explored_list.add(root);
		Node actual = root;
//-------------------------------------------------------------------------------------------------------
		  int cout=0;
		  do {
			 cout++;
			 System.out.println(cout);
			  actual=frontier_queue.poll();
			  action[] possible_actions=game.getPossibleActions(actual.get_board());
			  for(int i=0;i<possible_actions.length;i++) {	
				 Integer [][] child_board=game.computeAction(possible_actions[i], actual.get_board());
				 Node children=new Node();
				 children.set_board(child_board);
				 children.set_parent(actual);
				 children.set_next_explore(possible_actions[i]);
				 children.set_HeuristicValue(game.getHeuristicValue(child_board));
				 children.set_g_value(getDeep(child_board));
				 if(is_explored(explored_list,children )) {  //true if children are in explored list
					 continue;
	                 }
				 else {
				 frontier_queue.add(children);
				 explored_list.add( actual);			 
				 }
			  }		  
		  }
		  while(!game.isSolution(actual.get_board()));	
			result =solution_action(actual);
			System.out.println("minimum step to win : "+result.size());
			String string=game.boardToString(initial_board);
			System.out.println(string.charAt(6));
			return result;	
		}
	/**
	 * judge if all of the nodes are included in explored list
	 * @param explored list of the explored nodes
	 * @param node
	 * @return
	 */
	
    private boolean is_explored(List<Node> explored, Node node) {
    	if(explored.size()<1)
    		return false;
		Integer[][] node_board=node.get_board();
		Iterator<Node> iterator=explored.iterator();
		Node node1=null;
		 do {
				node1=(Node) iterator.next();
				boolean check=true;
				Integer[][] child_board=node1.get_board();
			for(int i=0;i<3;i++)
			   for(int j=0;j<3;j++){
				if(child_board[i][j]!=node_board[i][j]) {
					check=false;	
				}	
			}
			
		if(check==true) {	
		return true;
		}
		}
		 while(iterator.hasNext());
		return false;
	}
    
    /**
	 * find the deep value of a node
	 * @param board
	 * @return
	 */
	private int getDeep(Integer[][] board) {
		int deep = 0;
		Node node=new Node();
		board=node.get_board();
		while(node.get_parent()!=null);
		{
			deep+=1;
			node=node.get_parent();
		}
		return deep;
	}
    

	/**
	 * find a path to go back to initial board
	 * @param node
	 * @return list of solution actions
	 */
	private List<action> solution_action(Node node) {
		List<action> list = new ArrayList<>();
		Node parent=new Node();
		if(node.get_parent()!=null) {
			parent=node.get_parent();
			list = solution_action(parent);
		}			
		else {
			return list;		
		}	
		list.add(node.get_next_explore());
		return list;
	}
}
