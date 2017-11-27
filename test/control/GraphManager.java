package control;

import entity.Graph;
import entity.Color;
import java.util.ArrayList;
import entity.Node;

/**
 * @author nicolarusso
 * @param graph is the initial graph
 */
public class GraphManager {
	private int _colors;
	private int _components;
	private Graph _graph;
	public java.util.Vector<Color> _chosenColorList;
	public java.util.Vector<Color> _excludedColorList;

	public GraphManager() {
		throw new UnsupportedOperationException();
	}

	public void ReadGraph(String aPath) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Method that create subgraphs checking connected nodes
	 */
	public java.util.ArrayList<Graph> getSubGraphs(Graph aGraph) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Recursive function to check all node connection No memory consumption
	 */
	public void connectNodes(Graph aGraph, Node aNode) {
		throw new UnsupportedOperationException();
	}

	public void greedyDesc() {
		throw new UnsupportedOperationException();
	}

	public void greedyIvan(double aPercentage) {
		throw new UnsupportedOperationException();
	}

	public void randomGlobal(double aPercentage) {
		throw new UnsupportedOperationException();
	}

	public int countComponents(Graph aGraph) {
		throw new UnsupportedOperationException();
	}

	public void colorSplit() {
		throw new UnsupportedOperationException();
	}

	public void SAA(int aTemperature, double aCoolingRate) {
		throw new UnsupportedOperationException();
	}

	public void SAA(int aSwapColorRatio, int aTemperature, double aCoolingRate) {
		throw new UnsupportedOperationException();
	}

	public void SAA(double aKeepingRatio, double aSwapColorRatio, double aTemperature, double aCoolingRate) {
		throw new UnsupportedOperationException();
	}

	public Graph getGraph() {
		return this._graph;
	}
}