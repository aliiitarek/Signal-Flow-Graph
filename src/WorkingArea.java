import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.HashMap;
import java.util.Map;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

public class WorkingArea extends mxGraphComponent {
	private static mxGraph graph;
	private static WorkingArea instance;
	private static int numberofNodes = 0;

	public mxGraph getGraph() {
		return graph;
	}

	private static void applyEdgeDefaults() {
		// Settings for edges
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
	}

	public static WorkingArea getInstance() {
		if (instance == null) {
			graph = new mxGraph();
			graph.setAllowNegativeCoordinates(false);
			graph.setAllowDanglingEdges(false);
			graph.setAllowLoops(true);
			applyEdgeDefaults();
			instance = new WorkingArea(graph);

		}
		return instance;
	}

	public void clearGraph() {
		graph.selectAll();
		graph.removeCells(graph.getSelectionCells());
		numberofNodes = 0;

	}

	private WorkingArea(final mxGraph graph) {
		super(graph);
		this.setBackground(Color.WHITE);

		this.getGraphControl().addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				if (ProgramWindow.getCurrentAction() == ProgramWindow.getNode()) {
					graph.getModel().beginUpdate();
					try {
						numberofNodes++;
						graph.insertVertex(graph.getDefaultParent(), null, "y"
								+ numberofNodes, e.getX() - 15, e.getY() - 15,
								40, 40,
								"defaultVertex;shape=ellipse;strokeColor=black;fillColor=white");
						ProgramWindow.setCurrentAction(ProgramWindow.getNone());
					} finally {
						graph.getModel().endUpdate();
					}

				}

			}

		});

	}

}
