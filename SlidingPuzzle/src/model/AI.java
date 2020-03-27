package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import javax.swing.text.AsyncBoxView.ChildState;
import javax.xml.bind.ParseConversionEvent;

import model.Astarplayer.Node;
import model.PuzzleGame.action;


public class AI extends Player{
	 PuzzleGame game=new PuzzleGame();
	 public class Node {
	        Integer[][] board;
	        Node parent;// 父亲节点，便于回溯
	        action next_action;// 表示父亲节点到达该节点所做出的action
	        int cost;// cost值，这里是步数
	        int heuristic; // heuristic值
	        public Node(Integer[][] B, Node parent, action a, int c, int h){
	            this.board = B;
	            this.parent = parent;
	            this.next_action = a;
	            this.cost = c;
	            this.heuristic = h;
	        }
	    }

	@Override
	public List<action> solve(PuzzleGame game) {
		// TODO Auto-generated method stub
		
		 PriorityQueue<Node>  frontier_queue = new PriorityQueue<Node>(11, new Comparator<Node>(){
	            public int compare(Node n1, Node n2){
	                return (n1.cost + n1.heuristic) - (n2.cost + n2.heuristic);
	            }
	        });
	 //  Hashtable<String, Node>explored=new Hashtable<>();
		 ArrayList<Node>explored=new ArrayList<>();
	 Integer[][] initial_board=game.getGameBoard();
	 for(int i=0;i<next_action(null, initial_board).size();i++) {
		 
	 }
	   Node root=new Node(initial_board, null,null,0, game.getHeuristicValue(initial_board));
		frontier_queue.add(root);
		//explored.put(game.boardToString(initial_board),root);
		explored.add(root);
		Node current = root;
		
		 int cout=0;
		  do {
			 cout++;
			 System.out.println(cout);
		current=frontier_queue.poll();
		action[] possible_actions;
		possible_actions=game.getPossibleActions(current.board);
		
		for(int i=0;i<possible_actions.length;i++) {
			
			Integer[][] children_board=game.computeAction(possible_actions[i], current.board);
			
			Node child=new Node(children_board, current, possible_actions[i], getDeep(current.board,current)+1, game.getHeuristicValue(children_board));
		//	System.out.println(getDeep(current.board,current));
			
			
			
			System.out.println(is_explored(explored, child));
		  //  if(((check_solvbar(child)==true)&&(is_explored(explored, child)==false)))
		    	if(!is_explored(explored, child))
		    	{frontier_queue.add(child);
		  explored.add(current);
		
		//explored.put(game.boardToString(current.board),current);
		}
		    else
		    	continue;
		  
		}System.out.println(666);
		}while(!game.isSolution(current.board));
		  
		 // ArrayList<action> path=new ArrayList<>();
		  List<action> result = solution_action(current);
			System.out.println("minimum step to win : "+result.size());
			String string=game.boardToString(initial_board);
		
			return result;	
		
		
	}
	
	public ArrayList<model.AI.Node> next_action(Node parent,Integer[][] board) {
		//Node next_node=null;
		action[] possible_actions;
		ArrayList<Node>solverbar_node=new ArrayList<>();
		//node=new Node(board, parent, a, c, h)
		if(!game.isSolution(board)) {
		possible_actions=game.getPossibleActions(board);
		for(int i=0;i<possible_actions.length;i++) {
			Integer[][] children_board=game.computeAction(possible_actions[i], board);
			Node child=new Node(children_board, parent, possible_actions[i], getDeep(board,parent)+1, game.getHeuristicValue(children_board));
		    //if(!check_solvbar(child))
		    	solverbar_node.add(child);
		}
		/*Iterator<Node> iterator=solverbar_node.iterator();
		for(int j=0;j<solverbar_node.size();j++) {
			Node node=(Node)iterator.next();
			Hashtable<Integer, action>f_value_of_action=new Hashtable<>();
			f_value_of_action.put(node.cost+node.heuristic, node.next_action);
		}*/
		
		}
		return solverbar_node;
		
		
	}
	/**
	 * 
	 * @param board
	 * @param parent
	 * @param current_action
	 * @return
	 */
	private int getDeep(Integer[][] board,Node node) {
		int deep = 0;
		board=node.board;
		node=new Node(board, node.parent, node.next_action, node.cost, node.heuristic);
		
		while(node.parent!=null);
		{
			deep+=1;
			node=node.parent;
		}
		
		return deep;
		
	}
	
	/**when the board is unsolverbar,return false
	 * 
	 * @param node
	 * @return
	 */
	/*public boolean check_solvbar(Node node) {
		boolean flag =true;
		boolean flag1=true;
		boolean flag2=true;
		boolean flag3=true;
		Integer [][]board=node.board;
		int soll_value=1;
		String check=game.boardToString(board);
		for(int i=0;i<5;i=i+2) {
			
			String unsolver_1=String .valueOf(unsolverbar_value_1(soll_value));
			String unsolver_2=String.valueOf(unsolverbar_value_2(soll_value));
			char unsolver_11=unsolver_1.charAt(0);
			char unsolver_22=unsolver_2.charAt(0);
			if((unsolver_11)==check.charAt(i)||(unsolver_22)==check.charAt(i))
				flag1= false;
			soll_value ++;
		}
	
		for(int j=6;j<11;j=j+2) {
			
			String unsolver_1=String .valueOf(unsolverbar_value_1(soll_value));
			String unsolver_2=String.valueOf(unsolverbar_value_2(soll_value));
			char unsolver_11=unsolver_1.charAt(0);
			char unsolver_22=unsolver_2.charAt(0);
			if((unsolver_11)==check.charAt(j)||(unsolver_22)==check.charAt(j))
				flag1= false;
			soll_value ++;
					
				}
		for(int k=12;k<17;k=k+2) {
			
			String unsolver_1=String .valueOf(unsolverbar_value_1(soll_value));
			String unsolver_2=String.valueOf(unsolverbar_value_2(soll_value));
			char unsolver_11=unsolver_1.charAt(0);
			char unsolver_22=unsolver_2.charAt(0);
			if((unsolver_11)==check.charAt(k)||(unsolver_22)==check.charAt(k))
				flag1= false;
			soll_value ++;
				}
		if((flag1||flag2||flag3)==false)
		flag=false;
		return flag;
		
	}
	/**
	 * 
	 * @param soll_value
	 * @return
	 */
	
	public int  unsolverbar_value_1(int soll_value) {
		int not_fit_value1=soll_value+1;
		return not_fit_value1;
	}
	public int  unsolverbar_value_2(int soll_value) {
		int not_fit_value2=soll_value-1;
		return not_fit_value2;
	}
	
    private boolean is_explored(List<Node> explored, Node node) {
    	if(explored.size()<1)
    		return false;
		Integer[][] node_board=node.board;
		Iterator<Node> iterator=explored.iterator();
		Node node1=null;
		 do {
				node1=(Node) iterator.next();
				boolean check=true;
				Integer[][] child_board=node1.board;
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
/*	private boolean is_explored(Hashtable<String, Node> explored, Node node) {
    	if(explored.size()<1)
    		return false;
		Iterator<Node> iterator=explored.values().iterator();
		Node node1=null;
		 do {
				node1=(Node)iterator.next();
				boolean check=true;
				Integer[][] child_board=node1.board;
				if(game.boardToString(node1.board)!=game.boardToString(node.board)) {
					check=false;	
			}
			
		if(check==true) {	
		return true;
		}
		}
		 while(iterator.hasNext());
		return false;
	}
	 /*
	  * Iterator it = ht.values().iterator();

while (it.hasNext()) {
    String value =(String) it.next();
    System.out.println(value);
}
	  */
	 
	
		private List<action> solution_action(Node node) {
			List<action> list = new ArrayList<>();
			Node parent=new Node(node.parent.board, node.parent.parent, node.parent.next_action,node. parent.cost,node.parent.heuristic);
			if(parent!=null) {
				parent=node.parent;
				list = solution_action(parent);
			}			
			else {
				return list;		
			}	
			list.add(node.next_action);
			return list;
		}
	
	 
	 
	 
	 
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*-----------------------------------------------------------
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
	
	public AII() {
	
	}
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
		/*Comparator<Node> comparator =  new Comparator<Node>(){

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
			return result;	
		}
	/**
	 * judge if all of the nodes are included in explored list
	 * @param explored list of the explored nodes
	 * @param node
	 * @return
	 */
	
   /* private boolean is_explored(List<Node> explored, Node node) {
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
	/*private int getDeep(Integer[][] board) {
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
/*	private List<action> solution_action(Node node) {
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
}*/
