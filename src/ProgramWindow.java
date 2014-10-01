import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxICell;
import com.mxgraph.view.mxGraph;

public class ProgramWindow extends JFrame {
	private static ProgramWindow instance;
	private JButton nodeButton, evalButton, delButton, newButton;
	private static int currentAction;
	private static final int none = 0;
	private static final int node = 1;
	private static final int edge = 2;
	private Hashtable<String, Integer> hash;
	private static String[] Index;
	private static int[][] map;
	private static JMenuBar menuBar;

	public static int getNone() {
		return none;
	}

	public static int getCurrentAction() {
		return currentAction;
	}

	public static int getNode() {
		return node;
	}

	public static int getEdge() {
		return edge;
	}

	private ProgramWindow() {
		init();

	}

	private static void traverse(int[][] map, int current, boolean[] visited) {
		visited[current] = true;
		for (int i = 0; i < map.length; i++) {

			if ((map[current][i] != 0 || map[i][current] != 0) && !visited[i])
				traverse(map, i, visited);

		}
	}

	private static boolean isDisconnected(int[][] map) {
		boolean[] visited = new boolean[map.length];
		traverse(map, 0, visited);
		for (int i = 0; i < visited.length; i++) {
			if (!visited[i])
				return true;
		}
		return false;
	}

	private void init() {
		setSize(1000, 650);
		setTitle("Signal Flow Gragh Solver");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		currentAction = 0;
		WorkingArea.getInstance().getViewport().setOpaque(true);
		WorkingArea.getInstance().getViewport().setBackground(Color.WHITE);

		JScrollPane thePane = new JScrollPane(WorkingArea.getInstance());
		getContentPane().add(thePane);
		menuBar = new JMenuBar();
		initButtons();
		setJMenuBar(menuBar);
		setLocationRelativeTo(null);
		this.setVisible(true);
	}

	public static void setCurrentAction(int currentAction) {
		ProgramWindow.currentAction = currentAction;
	}

	private void initButtons() {

		newButton = new JButton();

		ImageIcon newlIcon = new ImageIcon(ResourceLoader.getImage("images/new.png"));

		newButton.setIcon(newlIcon);
		newButton.setOpaque(false);
		newButton.setContentAreaFilled(false);
		newButton.setBorderPainted(false);
		newButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				currentAction = 0;
				WorkingArea.getInstance().clearGraph();

			}
		});
		menuBar.add(newButton);

		nodeButton = new JButton();
		ImageIcon nodeIcon = new ImageIcon(
				ResourceLoader.getImage("images/node.png"));
		nodeButton.setIcon(nodeIcon);
		nodeButton.setOpaque(false);
		nodeButton.setContentAreaFilled(false);
		nodeButton.setBorderPainted(false);
		nodeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				currentAction = node;
			}
		});
		menuBar.add(nodeButton);

		evalButton = new JButton();
		ImageIcon evalIcon = new ImageIcon(
				ResourceLoader.getImage("images/eval.png"));
		evalButton.setIcon(evalIcon);
		evalButton.setOpaque(false);
		evalButton.setContentAreaFilled(false);
		evalButton.setBorderPainted(false);
		evalButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				currentAction = none;
				evaluate();
			}
		});
		menuBar.add(evalButton);

		delButton = new JButton();
		ImageIcon delIcon = new ImageIcon(ResourceLoader.getImage("images/del.png"));
		delButton.setIcon(delIcon);
		delButton.setOpaque(false);
		delButton.setContentAreaFilled(false);
		delButton.setBorderPainted(false);
		delButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				mxGraph g = WorkingArea.getInstance().getGraph();
				g.removeCells(g.getSelectionCells());
				currentAction = none;

			}
		});

		menuBar.add(delButton);

	}

	private void evaluate() {
		mxGraph g = WorkingArea.getInstance().getGraph();

		if (g.getSelectionCount() != 2) {
			if (g.getSelectionCount() == 0) {
				JOptionPane.showMessageDialog(this,
						"No Input And Output Nodes Selected!");
				return;
			} else if (g.getSelectionCount() == 1) {
				JOptionPane.showMessageDialog(this, "No Output Node Selected!");
				return;
			} else if (g.getSelectionCount() > 2) {
				JOptionPane.showMessageDialog(this,
						"Too many selected Elements!");
				return;
			}

		}
		mxCell inputNode = (mxCell) g.getSelectionCells()[0];
		mxCell outputNode = (mxCell) g.getSelectionCells()[1];
		if (inputNode.getEdgeCount() == 0 || outputNode.getEdgeCount() == 0) {
			JOptionPane.showMessageDialog(this,
					"Invalid Selection For Input And Output Nodes!");
			return;
		}

		Object[] edges = g.getChildEdges(g.getDefaultParent());
		Object[] nodes = g.getChildVertices(g.getDefaultParent());
		Index = new String[nodes.length];
		hash = new Hashtable<String, Integer>();
		map = new int[nodes.length][nodes.length];

		for (int i = 0; i < nodes.length; i++) {
			mxCell cell = (mxCell) nodes[i];
			for (int j = i + 1; j < nodes.length; j++) {
				mxCell newcell = (mxCell) nodes[j];
				if (cell.getValue().toString()
						.equalsIgnoreCase(newcell.getValue().toString())) {
					JOptionPane.showMessageDialog(this,
							"Node Duplicates Found!\n"
									+ newcell.getValue().toString()
									+ " Exist More Than Once.");
					return;
				}

			}
			Index[i] = cell.getValue().toString();
			hash.put(cell.getValue().toString(), i);
		}
		HashMap<String, Integer> notInputCandidates = new HashMap<String, Integer>();

		for (int i = 0; i < edges.length; i++) {
			mxCell edge = (mxCell) edges[i];
			if (edge.getValue().toString().equals(null)
					|| edge.getValue().toString().isEmpty())
				edge.setValue("1");
			mxICell source = edge.getSource();
			mxICell target = edge.getTarget();
			String s = source.getValue().toString();
			String t = target.getValue().toString();
			notInputCandidates.put(t, 0);
			try {
				map[hash.get(s)][hash.get(t)] +=

				Integer.parseInt(edge.getValue().toString());

			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "Invalid Edge Value!\n\""
						+ edge.getValue().toString()
						+ "\" is Not Allowd As An Edge Value.");
				return;
			}
		}
		if (isDisconnected(map)) {
			JOptionPane.showMessageDialog(this, "Invalid Disconnected Graph!");
			return;
		}

		LinkedList<String> candidates = new LinkedList<String>();
		for (int i = 0; i < nodes.length; i++) {
			mxCell c = (mxCell) nodes[i];
			if (!notInputCandidates.containsKey(c.getValue().toString()))
				candidates.add(c.getValue().toString());

		}
		if (candidates.isEmpty()) {
			JOptionPane.showMessageDialog(this, "System Has No Input Nodes!");
			return;
		} else if (candidates.size() > 1) {
			JOptionPane
					.showMessageDialog(this,
							"Maltuiple Valid Input Nodes!\nSystem MustHave Only One Input Node.");
			return;
		}

		if (candidates.contains(outputNode.getValue().toString())) {
			JOptionPane.showMessageDialog(this, "Invalid Output Node!");
			return;
		}

		int input = hash.get(inputNode.getValue().toString());
		int output = hash.get(outputNode.getValue().toString());
		try {
			TheBrain brain = new TheBrain(map, input, output);
			brain.Main();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "ERROR!");
			return;
		}

	}

	public static ProgramWindow getInstance() {
		if (instance == null)
			instance = new ProgramWindow();

		return instance;

	}

	public static void showResults(ArrayList<LinkedList<Integer>> forwarpaths,
			int[] forwardgains, ArrayList<LinkedList<Integer>> loops,
			int[] loopsgains, int[] deltas, int totalGain,
			LinkedList<LinkedList<Integer>>[] non_touching) {

		new ResultsFrame(Index, map, forwarpaths, loops, forwardgains,
				loopsgains, deltas, totalGain, non_touching);

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		getInstance();
	}

}
