import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

public class ResultsFrame extends JFrame {

	mxGraph graph;
	Object parent;
	String[] index;
	int[][] map;
	int lastx, lasty;
	mxGraphComponent graphComponent;

	public ResultsFrame(String[] index, int[][] map,
			ArrayList<LinkedList<Integer>> forwardPaths,
			ArrayList<LinkedList<Integer>> loops, int[] forwardPathsGains,
			int[] loopsGains, int[] deltas, int totalGain,
			LinkedList<LinkedList<Integer>>[] nonTouching) {
		setSize(1000, 650);
		setTitle("Solution");
		setVisible(true);
		graph = new mxGraph();
		parent = graph.getDefaultParent();
		this.index = index;
		this.map = map;

		graph.setCellsEditable(false);
		graph.setAllowDanglingEdges(false);
		// graph.setCellsBendable(false);
		graph.setCellsDeletable(false);
		// graph.setCellsLocked(true);
		// graph.setCellsSelectable(false);
		// graph.setCellsMovable(false);
		graph.setCellsDisconnectable(false);
		graph.setCellsCloneable(false);
		// graph.setAutoOrigin(false);
		graph.setAllowNegativeCoordinates(false);
		graph.setAllowLoops(true);

		Map<String, Object> edge = new HashMap<String, Object>();
		edge.put(mxConstants.STYLE_ROUNDED, true);
		edge.put(mxConstants.STYLE_ORTHOGONAL, false);
		edge.put(mxConstants.STYLE_EDGE, "elbowEdgeStyle");
		edge.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CONNECTOR);
		edge.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_CLASSIC);
		edge.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_MIDDLE);
		edge.put(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_CENTER);
		edge.put(mxConstants.STYLE_STROKECOLOR, "#000000"); // default is
															// #6482B9
		edge.put(mxConstants.STYLE_FONTCOLOR, "#000000");

		mxStylesheet edgeStyle = new mxStylesheet();
		edgeStyle.setDefaultEdgeStyle(edge);
		graph.setStylesheet(edgeStyle);

		graph.getModel().beginUpdate();
		try {

			lastx = 40;
			lasty = 40;

			graph.insertVertex(parent, null, "Total Gain = " + totalGain, 100,
					lasty, 40, 40,
					"defaultVertex;shape=ellipse;strokeColor=white;fillColor=white;fontSize=30");
			lasty += 70;
			for (int i = 0; i < deltas.length; i++) {
				graph.insertVertex(parent,null,"Delta " + i + " = " + deltas[i],100,lasty,40,40,
						"defaultVertex;shape=ellipse;strokeColor=white;fillColor=white;fontSize=30;fontColor:green");
				lasty += 70;
			}

			for (int i = 0; i < forwardPaths.size(); i++) {
				LinkedList<Integer> forwardPath = forwardPaths.get(i);
				Object[] nodexIndex = printGraph();
				paintPaths(forwardPath, nodexIndex);
				lasty += 100;
			}

			for (int i = 0; i < loops.size(); i++) {
				Object[] nodexIndex = printGraph();
				paintPaths(loops.get(i), nodexIndex);
				lasty += 100;
			}

			if (nonTouching.length > 1) {
				graph.insertVertex(
						parent,
						null,
						"Single Non-Touching Loops: ",
						100,
						lasty,
						40,
						40,
						"defaultVertex;shape=ellipse;strokeColor=white;fillColor=white;fontSize=30;fontColor:green");
				lasty += 70;
				Object[] nodexIndex = printGraph();
				lasty += 100;
				for (int i = 0; i < nonTouching[1].size(); i++) {
					for (int j = 0; j < nonTouching[i].size(); j++)
						paintPaths(loops.get(nonTouching[1].get(i).get(j)),
								nodexIndex);
				}
			}

			for (int i = 2; i < nonTouching.length; i++) {
				graph.insertVertex(
						parent,
						null,
						i + " Non-Touching Loops: ",
						100,
						lasty,
						40,
						40,
						"defaultVertex;shape=ellipse;strokeColor=white;fillColor=white;fontSize=30;fontColor:green");
				lasty += 70;

				for (int j = 0; j < nonTouching[i].size(); j++) {
					Object[] nodexIndex = printGraph();
					lasty += 100;
					for (int k = 0; k < nonTouching[i].get(j).size(); k++) {
						paintPaths(loops.get(nonTouching[i].get(j).get(k)),
								nodexIndex);
					}
				}
			}
		} finally {
			graph.getModel().endUpdate();
		}

		graphComponent = new mxGraphComponent(graph);
		graphComponent.setConnectable(false);
		graphComponent.getViewport().setOpaque(true);
		graphComponent.getViewport().setBackground(Color.WHITE);
		JScrollPane thePane = new JScrollPane(graphComponent);
		getContentPane().add(thePane);
		setLocationRelativeTo(null);

	}

	private Object[] printGraph() {
		Object[] nodesIndex = new Object[map.length];
		lastx = 40;
		for (int i = 0; i < index.length; i++) {
			nodesIndex[i] = graph
					.insertVertex(parent, null, index[i], lastx, lasty, 40, 40,
							"defaultVertex;shape=ellipse;strokeColor=black;fillColor=white");
			lastx += 100;
			if (lastx > 580) {
				lastx = 40;
				lasty += 100;
			}
		}

		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map.length; j++) {
				if (map[i][j] != 0) {
					graph.insertEdge(parent, null, map[i][j], nodesIndex[i],
							nodesIndex[j]);
				}
			}
		}
		return nodesIndex;
	}

	private void paintPaths(LinkedList<Integer> path, Object[] nodesIndex) {
		Object[] coloredEdges = new Object[path.size() - 1];
		for (int i = 0; i < path.size() - 1; i++) {
			coloredEdges[i] = (mxCell) graph.getEdgesBetween(
					nodesIndex[path.get(i)], nodesIndex[path.get(i + 1)])[0];
		}
		graph.setCellStyles(mxConstants.STYLE_STROKECOLOR, "red", coloredEdges);
		// graphComponent.refresh();

	}
}
