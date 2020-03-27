package view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import model.PuzzleGame;
import model.PuzzleGame.action;
import model.AII;
import model.Astarplayer;
import model.Player;

public class SlidingPuzzleGUI extends Application implements Observer {

	public List<Class<? extends Player>> getPlayers() {
		List<Class<? extends Player>> result = new ArrayList<>();
		result.add(Astarplayer.class);
		result.add(AII.class);
		//#---------------------------------------------------------------#
		//	add your player here
		// 	e.g. result.add(YOURPLAYER.class);
		//#---------------------------------------------------------------#
		
		return result;
	}
	
	//Nothing interesting is happening afterwards
	
	
	private PuzzleGame game;
	private double xOffset = 10;
	private double yOffset = 10;
	private Pane gamePane;
	private Thread updateThread = new Thread();
	private Thread solveThread = new Thread();
	
	private Label wonLabel;
	private StackPane[][] tiles;
	private ProgressIndicator pi;
	private int zX;
	private int zY;
	private double tileWidth;
	private double tileHeight;
	
	
	private Player player = null;
	
	
	@Override
	public void start(Stage primaryStage) {
		BorderPane bp = new BorderPane();
		gamePane = new Pane();
		Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
		double min = Math.min(screenBounds.getHeight(), screenBounds.getWidth());//比较返回小值
		primaryStage.setResizable(false);
		bp.setCenter(gamePane);
		gamePane.setMinWidth(min/1.5);
		gamePane.setMinHeight(min/1.5);
		gamePane.setPrefHeight(min/1.5);
		gamePane.setPrefWidth(min/1.5);
		
		//add players
		ComboBox<String> players = new ComboBox<>();
		players.getItems().add("Choose a player");
		List<Class<? extends Player>> availablePlayers = getPlayers();
		for (int i=0; i< availablePlayers.size(); ++i) {
			players.getItems().add(availablePlayers.get(i).getName());//取出list中的元素，即玩家，并取得名字
		}
		players.getSelectionModel().select(0);//model 0
		
		players.valueProperty().addListener((v, o, n) -> {//
			String className = n;
			if (className == "Choose a player") {
				player = null;
			} else {
				try {
					Object rawPlayer =  Class.forName(className).newInstance();
					if (rawPlayer instanceof Player) {
						player = (Player) rawPlayer;
					}
				} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		
		Button start = new Button("start");
		start.setOnAction((e) -> {
			if (player != null) {
				try {
					player = (Player) Class.forName(player.getClass().getName()).newInstance();
				} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e1) {
					e1.printStackTrace();
				}
				pi.setVisible(true);
				wonLabel.setTextFill(Color.BLACK);
				wonLabel.setText("Solving...");
				Task<Void> solveTask = new Task<Void>() {
					@Override
					protected Void call() throws Exception {

						player.solveAndApply(game);
						
						Platform.runLater(new Runnable() {					
							@Override
							public void run() {
								game.unsetReset();
								game.checkFinished();						
								pi.setVisible(false);
							}
						});
						return null;
					}
				};
				solveThread = new Thread(solveTask);
				solveThread.start();
			}
		});
		
		Button reset = new Button("reset");
		reset.setOnAction((e) -> {
			updateThread.interrupt();
			solveThread.interrupt();
			pi.setVisible(false);
			game.reset();
			wonLabel.setText("");
		});
		
		
		Label stateLabel = new Label("Game State: ");
		wonLabel = new Label("");
		
		pi = new ProgressIndicator();
		pi.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
		pi.setPrefSize(30, 30);
		pi.setVisible(false);
		
		HBox topContent = new HBox();
		topContent.getChildren().add(players);
		topContent.getChildren().add(start);
		topContent.getChildren().add(reset);
		topContent.getChildren().add(stateLabel);
		topContent.getChildren().add(wonLabel);
		topContent.getChildren().add(pi);
		
		topContent.setSpacing(10);
		topContent.setPadding(new Insets(5, 10, 5, 10));
		topContent.setAlignment(Pos.CENTER_LEFT);
		bp.setTop(topContent);
		
		
		Scene scene = new Scene(bp);
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setMinWidth(200);
		primaryStage.setOnCloseRequest((e) -> System.exit(0));
		
		game = new PuzzleGame();
		game.addObserver(this);
		Integer[][] board = game.getGameBoard();
		int x = board[0].length;
		int y = board.length;
		tiles = new StackPane[y][x];

		
		
		init(gamePane, game);

	}

	private void init(Pane p, PuzzleGame g) {
		Integer[][] board = g.getGameBoard();
		
		p.setStyle("-fx-background-color: SLATEGRAY;");
		p.getChildren().clear();
		
		
		double boardWidth = p.getWidth()-2*xOffset; //5p spacing on each side
		double boardHeight = p.getHeight()-2*yOffset;
		tileHeight = boardHeight/ board.length;
		tileWidth = boardWidth / board[0].length;		
		for (int i=0; i<board.length; ++i) {
			for (int j=0; j<board[i].length; ++j) {
				StackPane tile = new StackPane();
				tile.setLayoutX(xOffset+j*tileWidth);
				tile.setLayoutY(yOffset+i*tileHeight);
				
				
				
				Rectangle rect = new Rectangle(0,0, tileWidth,tileHeight);
				rect.widthProperty().bind(p.widthProperty().subtract(2*xOffset).divide(board[0].length));
				rect.heightProperty().bind(p.heightProperty().subtract(2*yOffset).divide(board.length));
				rect.setFill(Color.STEELBLUE);
				rect.setStroke(Color.DIMGRAY);
			
				tile.getChildren().add(rect);
				
				if (board[i][j]!=0) {
					Text text = new Text(String.valueOf(board[i][j]));
					text.setFont(Font.font("Verdana",  40));
					text.setFill(Color.WHITESMOKE);
					tile.getChildren().add(text);
					p.getChildren().add(tile);
					tiles[i][j]=tile;

				}
			}
		}
		zX = game.getZX();
		zY = game.getZY();	
		
		p.widthProperty().addListener((obs, oldvalue, newvalue) -> {
			int noFields = game.getGameBoard()[0].length;
			double oTileWidth = (oldvalue.doubleValue()-2*xOffset)/noFields;
			double nTileWidth = (newvalue.doubleValue()-2*xOffset)/noFields;		
			for (Node n: p.getChildren()) {
				double oldx = n.getLayoutX();
				double j=(oldx-xOffset)/oTileWidth;
				n.setLayoutX(nTileWidth*j+xOffset);				
					
			}
		});
		
		p.heightProperty().addListener((obs, oldvalue, newvalue) -> {
			int noFields = game.getGameBoard().length;
			double oTileWidth = (oldvalue.doubleValue()-2*xOffset)/noFields;
			double nTileWidth = (newvalue.doubleValue()-2*xOffset)/noFields;		
			for (Node n: p.getChildren()) {
			double oldx = n.getLayoutY();
			double j=(oldx-xOffset)/oTileWidth;
			n.setLayoutY(nTileWidth*j+xOffset);				
			
			}
		});
		
	}
	
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof PuzzleGame) {
			PuzzleGame g = (PuzzleGame)o;
			Vector<action> log = g.getLog();	
			if (g.wasReset()) {
				init(gamePane, game);
				return;
			}
	
			Task<Void> updateTask = new Task<Void>() {

				@Override
				protected Void call() throws Exception {
					for (int i=0; i < log.size(); ++i) {
						action a = log.get(i);
						
						switch(a) {
							case UP: {
							StackPane temp = tiles[zY-1][zX];
							tiles[zY][zX] = temp;
							
							for (int j=0; j < tileHeight; ++j) {
								Platform.runLater(new Runnable() {
									
									@Override
									public void run() {
										temp.setLayoutY(temp.getLayoutY()+1);									
									}
								});
								Thread.sleep(2);
							}
							

							zY-=1;
							break;
							}
							case DOWN: {
								StackPane temp = tiles[zY+1][zX];
								tiles[zY][zX] = temp;
								for (int j=0; j < tileHeight; ++j) {
									Platform.runLater(new Runnable() {
										
										@Override
										public void run() {
											temp.setLayoutY(temp.getLayoutY()-1);										
										}
									});
									Thread.sleep(2);
								}
								

								zY+=1;
								break;
								}
							case LEFT: {
								StackPane temp = tiles[zY][zX-1];
								tiles[zY][zX] = temp;
								for (int j=0; j<tileWidth; ++j) {
									Platform.runLater(new Runnable() {
										
										@Override
										public void run() {
											temp.setLayoutX(temp.getLayoutX()+1);										
										}
									});
									Thread.sleep(2);
								}
								

								zX-=1;
								break;
								}
							case RIGHT: {
								StackPane temp = tiles[zY][zX+1];
								tiles[zY][zX] = temp;
								for (int j=0; j<tileWidth; ++j) {
									Platform.runLater(new Runnable() {
										
										@Override
										public void run() {
											temp.setLayoutX(temp.getLayoutX()-1);										
										}
									});
									Thread.sleep(2);
								}
								
								zX+=1;
								break;
							}
						}
						Thread.sleep(200);
					}
					log.clear();
					return null;
				}	
			};
			updateThread = new Thread(updateTask);
			updateThread.start();
			if (game.isWon()) {
				wonLabel.setText("Won");
				wonLabel.setTextFill(Color.GREEN);
			} else {
				wonLabel.setText("Lost");
				wonLabel.setTextFill(Color.RED);
			}
		}
	}
}
