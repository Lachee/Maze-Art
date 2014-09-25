package com.voidpixel.maze.generation;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class MazeGenerator {
	int[][] map;
	int[][][] currentPath;
	ArrayList<int[]> listOfDeadEnds, listOfChanges, listOfAllChanges;
	int[][] diggerPos;
	int[] playerPos, startPos, finishPos;
	int noSteps[];
	int mazeWidth, mazeHeight;
	boolean[] isGenerated;
	boolean[] isBacktracking;
	Random random;
	long randomSeed;
	int noOfDiggers = 1;
	int currentDigger;
	int[] startPoint, endPoint;
	boolean playerPlaced = false;
	boolean diggersStartAtSamePoint = true;
	boolean showDeadEnds = true;
	ArrayList<int[]> lightMap, prevLightMap, wallLightMap;
	ArrayList<Point> visitedMap;
	int[][] knownMap;

	public MazeGenerator(int width, int height) {
		randomSeed = System.currentTimeMillis();
		random = new Random(randomSeed);
		this.initialiser(width, height);
	}

	public MazeGenerator(int width, int height, long seed) {
		this.randomSeed = seed;
		random = new Random(seed);
		this.initialiser(width, height);
	}

	public int[][] getMap() { return map; }
	
	public Point getStart() { return new Point(startPos[0], startPos[1]); }
	
	private void initialiser(int width, int height) {
		System.out.println("seed = " + randomSeed);
		this.mazeWidth = width;
		this.mazeHeight = height;
		this.listOfDeadEnds = new ArrayList<int[]>();
		this.listOfChanges = new ArrayList<int[]>();
		this.listOfAllChanges = new ArrayList<int[]>();
		this.map = new int[2 * width + 1][2 * height + 1];
		this.currentPath = new int[noOfDiggers][(2 * width + 1)
				* (2 * height + 1)][2];
		this.diggerPos = new int[noOfDiggers][2];
		// Puts all diggers in same starting spot.
		if (diggersStartAtSamePoint) {
			int[] diggerStart = {
					((int) (random.nextDouble() * (width))) * 2 + 1,
					((int) (random.nextDouble() * (height))) * 2 + 1 };
			for (int i = 0; i < noOfDiggers; i++) {
				diggerPos[i][0] = diggerStart[0];
				diggerPos[i][1] = diggerStart[1];
			}
		} else {
			// Puts all diggers in different starting spots.
			for (int i = 0; i < noOfDiggers; i++) {
				diggerPos[i][0] = ((int) (random.nextDouble() * (width))) * 2 + 1;
				diggerPos[i][1] = ((int) (random.nextDouble() * (height))) * 2 + 1;
			}
		}
		for (int i = 0; i < noOfDiggers; i++) {
			map[diggerPos[i][0]][diggerPos[i][1]] = 1;
			if (!listOfDeadEnds.contains(new int[] { diggerPos[i][0],
					diggerPos[i][1] })) {
				listOfDeadEnds
						.add(new int[] { diggerPos[i][0], diggerPos[i][1] });
			}
		}
		this.noSteps = new int[noOfDiggers];
		this.isBacktracking = new boolean[noOfDiggers];
		this.isGenerated = new boolean[noOfDiggers];
		this.lightMap = new ArrayList<int[]>();
		this.prevLightMap = new ArrayList<int[]>();
		this.wallLightMap = new ArrayList<int[]>();
		this.visitedMap = new ArrayList<Point>();
		this.knownMap = new int[2 * width + 1][2 * height + 1];
	}

	public void setShowDeadEnds(boolean foo) {
		this.showDeadEnds = foo;
	}

	public void display(Graphics g, int xOffset, int yOffset, int size) {
		// Offset is measured in the same way Java normally does it: positive x
		// is right, positive y down.
		int x, y;
		for (x = 0; x <= this.mazeWidth * 2; x++) {
			for (y = 0; y <= this.mazeHeight * 2; y++) {
				if (map[x][y] == 0) {
					g.setColor(Color.BLACK);
				}
				if (map[x][y] == 1) {
					g.setColor(Color.WHITE);
				}
				if (showDeadEnds) {
					for (int i = 0; i < diggerPos.length; i++) {
						if (diggerPos[i][0] == x && diggerPos[i][1] == y) {
							g.setColor(Color.red);
						}
					}
				}
				if (x % 2 == 0 && y % 2 == 0) {
					g.fillRect((x / 2) * (size + 1) + xOffset,
							((mazeHeight * 2 - y) / 2) * (size + 1) + yOffset,
							1, 1);
				}
				if (x % 2 == 1 && y % 2 == 1) {
					g.fillRect(((x - 1) / 2) * (size + 1) + 1 + xOffset,
							((mazeHeight * 2 - y - 1) / 2) * (size + 1) + 1
									+ yOffset, size, size);
				}
				if (x % 2 == 1 && y % 2 == 0) {
					g.fillRect(((x - 1) / 2) * (size + 1) + 1 + xOffset,
							((mazeHeight * 2 - y) / 2) * (size + 1) + yOffset,
							size, 1);
				}
				if (x % 2 == 0 && y % 2 == 1) {
					g.fillRect((x / 2) * (size + 1) + xOffset, ((mazeHeight * 2
							- y - 1) / 2)
							* (size + 1) + 1 + yOffset, 1, size);
				}
			}
		}
		if (showDeadEnds) {
			g.setColor(Color.BLUE);
			int deadEnds = listOfDeadEnds.size();
			for (int i = 0; i < deadEnds; i++) {
				int[] point = listOfDeadEnds.get(i);
				x = point[0];
				y = point[1];
				g.fillRect(((x - 1) / 2) * (size + 1) + 1 + xOffset,
						((mazeHeight * 2 - y - 1) / 2) * (size + 1) + 1
								+ yOffset, size, size);
			}
		}

		try {
			g.setColor(Color.GREEN);
			x = startPos[0];
			y = startPos[1];
			g.fillRect(((x - 1) / 2) * (size + 1) + 1 + xOffset, ((mazeHeight
					* 2 - y - 1) / 2)
					* (size + 1) + 1 + yOffset, size, size);
			g.setColor(Color.RED);
			x = finishPos[0];
			y = finishPos[1];
			g.fillRect(((x - 1) / 2) * (size + 1) + 1 + xOffset, ((mazeHeight
					* 2 - y - 1) / 2)
					* (size + 1) + 1 + yOffset, size, size);
			g.setColor(Color.CYAN);
			x = playerPos[0];
			y = playerPos[1];
			g.fillRect(((x - 1) / 2) * (size + 1) + 1 + xOffset, ((mazeHeight
					* 2 - y - 1) / 2)
					* (size + 1) + 1 + yOffset, size, size);
		} catch (NullPointerException ex) {
		}
	}

	public void displayRect(Graphics g, int xOffset, int yOffset, int size) {
		g.setColor(Color.black);
		g.fillRect(xOffset, yOffset, mazeWidth * (size + 1) + 1, mazeHeight
				* (size + 1) + 1);
	}

	public void clearRect(Graphics g, int xOffset, int yOffset, int size) {
		g.clearRect(xOffset, yOffset, mazeWidth * (size + 1) + 1, mazeHeight
				* (size + 1) + 1);
	}

	public void displayPoint(Graphics g, int xOffset, int yOffset, int size,
			Point point /* {x,y} */) {
		int x = point.x;
		int y = point.y;
		try {
			if (map[x][y] == 0) {
				g.setColor(Color.BLACK);
			}
			if (map[x][y] == 1) {
				g.setColor(Color.WHITE);
			}
			if (showDeadEnds) {
				int noOfDead = listOfDeadEnds.size();
				for (int i = 0; i < noOfDead; i++) {
					int[] space = listOfDeadEnds.get(i);
					if (space[0] == x && space[1] == y) {
						g.setColor(Color.blue);
					}
				}
				for (int i = 0; i < diggerPos.length; i++) {
					if (diggerPos[i][0] == x && diggerPos[i][1] == y) {
						g.setColor(Color.red);
					}
				}
			}
		} catch (IndexOutOfBoundsException ex) {
			return;
		}
		if (x % 2 == 0 && y % 2 == 0) {
			g.fillRect((x / 2) * (size + 1) + xOffset,
					((mazeHeight * 2 - y) / 2) * (size + 1) + yOffset, 1, 1);
		}
		if (x % 2 == 1 && y % 2 == 1) {
			g.fillRect(((x - 1) / 2) * (size + 1) + 1 + xOffset, ((mazeHeight
					* 2 - y - 1) / 2)
					* (size + 1) + 1 + yOffset, size, size);
		}
		if (x % 2 == 1 && y % 2 == 0) {
			g.fillRect(((x - 1) / 2) * (size + 1) + 1 + xOffset,
					((mazeHeight * 2 - y) / 2) * (size + 1) + yOffset, size, 1);
		}
		if (x % 2 == 0 && y % 2 == 1) {
			g.fillRect((x / 2) * (size + 1) + xOffset,
					((mazeHeight * 2 - y - 1) / 2) * (size + 1) + 1 + yOffset,
					1, size);
		}
	}
	
	public void displayPointWithoutWalls(Graphics g, int xOffset, int yOffset, int size, Point point, Color color) {
		int x = point.x;
		int y = point.y;
		g.setColor(color);
		
		if (x % 2 == 1 && y % 2 == 1) {
			//Tile / Block / What ever
			g.fillRect(((x - 1) / 2) * (size + 1) + 1 + xOffset - 1,  //X
					((mazeHeight * 2 - y - 1) / 2) * (size + 1) + 1 + yOffset - 1, //Y
					size + 2, size + 2);
		}
		
	}
	
	public void displayPoint(Graphics g, int xOffset, int yOffset, int size,
			Point point /* {x,y} */, Color color) {
		int x = point.x;
		int y = point.y;
		g.setColor(color);
		
		if (x % 2 == 0 && y % 2 == 0) {
			//Dots in the middle... them biatches!
			g.fillRect((x / 2) * (size + 1) + xOffset,
					((mazeHeight * 2 - y) / 2) * (size + 1) + yOffset, 1, 1);
		}
		
		if (x % 2 == 1 && y % 2 == 1) {
			//Tile / Block / What ever
			g.fillRect(((x - 1) / 2) * (size + 1) + 1 + xOffset, ((mazeHeight
					* 2 - y - 1) / 2)
					* (size + 1) + 1 + yOffset, size, size);
		}
		
		if (x % 2 == 1 && y % 2 == 0) {
			//Horizontal Wall
			g.fillRect(((x - 1) / 2) * (size + 1) + 1 + xOffset,
					((mazeHeight * 2 - y) / 2) * (size + 1) + yOffset, size, 1);
		}
		
		
		if (x % 2 == 0 && y % 2 == 1) {
			//Vertical Wall
			g.fillRect((x / 2) * (size + 1) + xOffset,
					((mazeHeight * 2 - y - 1) / 2) * (size + 1) + 1 + yOffset,
					1, size);
		}
	}

	public boolean isGenerated() {
		boolean generated = true;
		for (boolean i : isGenerated) {
			if (!i) {
				generated = false;
			}
		}
		return generated;
	}

	public void generate() {
		while (!this.isGenerated()) {
			this.iterate();
		}
	}

	public void iterate(int noOfTimes) {
		if (noOfTimes > 0) {
			for (int i = 0; i < noOfTimes; i++) {
				this.iterate();
			}
		}
	}

	public void iterate() {
		if (this.isGenerated()) {
			startPoint = listOfDeadEnds.get(random.nextInt(listOfDeadEnds
					.size()));
			endPoint = listOfDeadEnds
					.get(random.nextInt(listOfDeadEnds.size()));
			while (endPoint == startPoint) {
				endPoint = listOfDeadEnds.get(random.nextInt(listOfDeadEnds
						.size()));
			}
			return;
		}
		currentDigger++;
		currentDigger %= noOfDiggers;
		if (isGenerated[currentDigger]) {
			return;
		}
		if (map[diggerPos[currentDigger][0]][diggerPos[currentDigger][1]] == 0) {
			map[diggerPos[currentDigger][0]][diggerPos[currentDigger][1]] = 1;
			System.out.println("Fixed map[" + diggerPos[currentDigger][0]
					+ "][" + diggerPos[currentDigger][1] + "].");
		}
		ArrayList<String> possibleDirections = new ArrayList<String>();
		if (diggerPos[currentDigger][1] < 2 * mazeHeight - 1) {
			if (map[diggerPos[currentDigger][0]][diggerPos[currentDigger][1] + 2] == 0) {
				possibleDirections.add("North");
			}
		}
		if (diggerPos[currentDigger][0] < 2 * mazeWidth - 1) {
			if (map[diggerPos[currentDigger][0] + 2][diggerPos[currentDigger][1]] == 0) {
				possibleDirections.add("East");
			}
		}
		if (diggerPos[currentDigger][1] > 1) {
			if (map[diggerPos[currentDigger][0]][diggerPos[currentDigger][1] - 2] == 0) {
				possibleDirections.add("South");
			}
		}
		if (diggerPos[currentDigger][0] > 1) {
			if (map[diggerPos[currentDigger][0] - 2][diggerPos[currentDigger][1]] == 0) {
				possibleDirections.add("West");
			}
		}
		String direction;
		if (possibleDirections.size() == 0) {
			if (noSteps[currentDigger] == 0) {
				isGenerated[currentDigger] = true;
			} else {
				if (!isBacktracking[currentDigger]) {
					listOfDeadEnds.add(new int[] { diggerPos[currentDigger][0],
							diggerPos[currentDigger][1] });
					listOfChanges.add(new int[] { diggerPos[currentDigger][0],
							diggerPos[currentDigger][1] });
					isBacktracking[currentDigger] = true;
				}
				noSteps[currentDigger]--;
				this.listOfChanges.add(new int[] { diggerPos[currentDigger][0],
						diggerPos[currentDigger][1] });
				diggerPos[currentDigger][0] = currentPath[currentDigger][noSteps[currentDigger]][0];
				diggerPos[currentDigger][1] = currentPath[currentDigger][noSteps[currentDigger]][1];
				this.listOfChanges.add(new int[] { diggerPos[currentDigger][0],
						diggerPos[currentDigger][1] });
			}
		} else {
			isBacktracking[currentDigger] = false;
			currentPath[currentDigger][noSteps[currentDigger]][0] = diggerPos[currentDigger][0];
			currentPath[currentDigger][noSteps[currentDigger]][1] = diggerPos[currentDigger][1];
			direction = possibleDirections.get(random
					.nextInt(possibleDirections.size()));
			this.listOfChanges.add(new int[] { diggerPos[currentDigger][0],
					diggerPos[currentDigger][1] });
			if (direction.equals("North")) {
				map[diggerPos[currentDigger][0]][diggerPos[currentDigger][1] + 1] = 1;
				this.listOfChanges.add(new int[] { diggerPos[currentDigger][0],
						diggerPos[currentDigger][1] + 1 });
				map[diggerPos[currentDigger][0]][diggerPos[currentDigger][1] + 2] = 1;
				this.listOfChanges.add(new int[] { diggerPos[currentDigger][0],
						diggerPos[currentDigger][1] + 2 });
				diggerPos[currentDigger][1] += 2;
			}
			if (direction.equals("East")) {
				map[diggerPos[currentDigger][0] + 1][diggerPos[currentDigger][1]] = 1;
				this.listOfChanges.add(new int[] {
						diggerPos[currentDigger][0] + 1,
						diggerPos[currentDigger][1] });
				map[diggerPos[currentDigger][0] + 2][diggerPos[currentDigger][1]] = 1;
				this.listOfChanges.add(new int[] {
						diggerPos[currentDigger][0] + 2,
						diggerPos[currentDigger][1] });
				diggerPos[currentDigger][0] += 2;
			}
			if (direction.equals("South")) {
				map[diggerPos[currentDigger][0]][diggerPos[currentDigger][1] - 1] = 1;
				this.listOfChanges.add(new int[] { diggerPos[currentDigger][0],
						diggerPos[currentDigger][1] - 1 });
				map[diggerPos[currentDigger][0]][diggerPos[currentDigger][1] - 2] = 1;
				this.listOfChanges.add(new int[] { diggerPos[currentDigger][0],
						diggerPos[currentDigger][1] - 2 });
				diggerPos[currentDigger][1] -= 2;
			}
			if (direction.equals("West")) {
				map[diggerPos[currentDigger][0] - 1][diggerPos[currentDigger][1]] = 1;
				this.listOfChanges.add(new int[] {
						diggerPos[currentDigger][0] - 1,
						diggerPos[currentDigger][1] });
				map[diggerPos[currentDigger][0] - 2][diggerPos[currentDigger][1]] = 1;
				this.listOfChanges.add(new int[] {
						diggerPos[currentDigger][0] - 2,
						diggerPos[currentDigger][1] });
				diggerPos[currentDigger][0] -= 2;
			}
			noSteps[currentDigger]++;
		}
	}

	public void solve() {

	}

	public boolean isPlayerPlaced() {
		return playerPlaced;
	}

	public void placePlayer() {
		if (this.isGenerated() && !playerPlaced) {
			ArrayList<int[]> copyOfDeadEnds = listOfDeadEnds;
			int numberOfDeadEnds = copyOfDeadEnds.size();
			startPos = copyOfDeadEnds.get(random.nextInt(numberOfDeadEnds));
			copyOfDeadEnds.remove(startPos);
			numberOfDeadEnds = copyOfDeadEnds.size();
			finishPos = copyOfDeadEnds.get(random.nextInt(numberOfDeadEnds));
			playerPos = new int[2];
			playerPos[0] = startPos[0];
			playerPos[1] = startPos[1];
			playerPlaced = true;
		}
	}

	public void placeStartAndEnd() {
		if (this.isGenerated() && !playerPlaced) {
			ArrayList<int[]> copyOfDeadEnds = listOfDeadEnds;
			int numberOfDeadEnds = copyOfDeadEnds.size();
			startPos = copyOfDeadEnds.get(random.nextInt(numberOfDeadEnds));
			copyOfDeadEnds.remove(startPos);
			numberOfDeadEnds = copyOfDeadEnds.size();
			finishPos = copyOfDeadEnds.get(random.nextInt(numberOfDeadEnds));
			playerPlaced = true;
		}
	}

	public int[] getPlayerPos() {
		return playerPos;
	}

	public int movePlayerNorth() {
		if (playerPos[1] < 2 * mazeHeight - 1) {
			if (map[playerPos[0]][playerPos[1] + 1] == 1
					&& map[playerPos[0]][playerPos[1] + 2] == 1) {
				listOfChanges.add(playerPos);
				this.addToVisitedMap(playerPos);
				this.addToVisitedMap(new int[] { playerPos[0], playerPos[1] + 1 });
				this.addToVisitedMap(new int[] { playerPos[0], playerPos[1] + 2 });
				listOfChanges.add(new int[] { playerPos[0], playerPos[1] + 2 });
				playerPos[1] += 2;
				return 1; // succeeded in moving
			} else {
				return 2;
			}
		} else {
			return 2; // hit a wall
		}
	}

	public int movePlayerEast() {
		if (playerPos[0] < 2 * mazeWidth - 1) {
			if (map[playerPos[0] + 1][playerPos[1]] == 1
					&& map[playerPos[0] + 2][playerPos[1]] == 1) {
				listOfChanges.add(playerPos);
				this.addToVisitedMap(playerPos);
				this.addToVisitedMap(new int[] { playerPos[0] + 1, playerPos[1] });
				this.addToVisitedMap(new int[] { playerPos[0] + 2, playerPos[1] });
				listOfChanges.add(new int[] { playerPos[0] + 2, playerPos[1] });
				playerPos[0] += 2;
				return 1; // succeeded in moving
			} else {
				return 2;
			}
		} else {
			return 2; // hit a wall
		}
	}

	public int movePlayerSouth() {
		if (playerPos[1] > 1) {
			if (map[playerPos[0]][playerPos[1] - 1] == 1
					&& map[playerPos[0]][playerPos[1] - 2] == 1) {
				listOfChanges.add(playerPos);
				this.addToVisitedMap(playerPos);
				this.addToVisitedMap(new int[] { playerPos[0], playerPos[1] - 1 });
				this.addToVisitedMap(new int[] { playerPos[0], playerPos[1] - 2 });
				listOfChanges.add(new int[] { playerPos[0], playerPos[1] - 2 });
				playerPos[1] -= 2;
				return 1; // succeeded in moving
			} else {
				return 2;
			}
		} else {
			return 2; // hit a wall
		}
	}

	public int movePlayerWest() {
		if (playerPos[0] > 1) {
			if (map[playerPos[0] - 1][playerPos[1]] == 1
					&& map[playerPos[0] - 2][playerPos[1]] == 1) {
				listOfChanges.add(playerPos);
				this.addToVisitedMap(playerPos);
				this.addToVisitedMap(new int[] { playerPos[0] - 1, playerPos[1] });
				this.addToVisitedMap(new int[] { playerPos[0] - 2, playerPos[1] });
				listOfChanges.add(new int[] { playerPos[0] - 2, playerPos[1] });
				playerPos[0] -= 2;
				return 1; // succeeded in moving
			} else {
				return 2;
			}
		} else {
			return 2; // hit a wall
		}
	}

	public void addToKnownMap(int[] point) {
		knownMap[point[0]][point[1]] = 1;
	}

	public void addToVisitedMap(int[] point) {
		if (!visitedMap.contains(new Point(point[0], point[1])))
			visitedMap.add(new Point(point[0], point[1]));
	}

	public void displayKnownMap(Graphics g, int xOffset, int yOffset, int size) {
		int x, y;
		for (x = 0; x <= this.mazeWidth * 2; x++) {
			for (y = 0; y <= this.mazeHeight * 2; y++) {
				if (knownMap[x][y] == 0) {
					g.setColor(Color.BLACK);
				}
				if (knownMap[x][y] == 1) {
					g.setColor(Color.GRAY);
					if (startPos[0] == x && startPos[1] == y) {
						g.setColor(Color.GREEN);
					}
					if (finishPos[0] == x && finishPos[1] == y) {
						g.setColor(Color.RED);
					}
				}
				if (playerPos[0] == x && playerPos[1] == y) {
					g.setColor(Color.CYAN);
				}
				if (x % 2 == 0 && y % 2 == 0) {
					g.fillRect((x / 2) * (size + 1) + xOffset,
							((mazeHeight * 2 - y) / 2) * (size + 1) + yOffset,
							1, 1);
				}
				if (x % 2 == 1 && y % 2 == 1) {
					g.fillRect(((x - 1) / 2) * (size + 1) + 1 + xOffset,
							((mazeHeight * 2 - y - 1) / 2) * (size + 1) + 1
									+ yOffset, size, size);
				}
				if (x % 2 == 1 && y % 2 == 0) {
					if (y >= 1 && y <= 2 * mazeHeight - 1) {
						if (knownMap[x][y - 1] == 1 && knownMap[x][y + 1] == 1
								&& map[x][y] == 1) {
							g.setColor(Color.GRAY);
							knownMap[x][y] = 1;
						}
					}
					g.fillRect(((x - 1) / 2) * (size + 1) + 1 + xOffset,
							((mazeHeight * 2 - y) / 2) * (size + 1) + yOffset,
							size, 1);
				}
				if (x % 2 == 0 && y % 2 == 1) {
					if (x >= 1 && x <= 2 * mazeHeight - 1) {
						if (knownMap[x - 1][y] == 1 && knownMap[x + 1][y] == 1
								&& map[x][y] == 1) {
							g.setColor(Color.GRAY);
							knownMap[x][y] = 1;
						}
					}
					g.fillRect((x / 2) * (size + 1) + xOffset, ((mazeHeight * 2
							- y - 1) / 2)
							* (size + 1) + 1 + yOffset, 1, size);
				}
			}
		}
	}
}
