package entity;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Vector;

/**
 * @author nicolarusso
 */
public class Graph {
	private java.util.HashMap<String, Label> _labels;
	private int _minLabelLength;
	private java.util.Vector<Node> _nodeList;
	private java.util.Vector<Arch> _archList;
	private java.util.Vector<Color> _colorList;
	private java.util.Vector<Label> _labelList;

	public Graph() {
		throw new UnsupportedOperationException();
	}

	public Graph(java.util.ArrayList<Node> aNodeList, java.util.ArrayList<Arch> aArchList, java.util.ArrayList<Color> aColorList) {
		throw new UnsupportedOperationException();
	}

	public void init(int aNodes, int aColors) {
		throw new UnsupportedOperationException();
	}

	public java.util.ArrayList<Color> getSelectedColorList() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Generate empty Nodes List
	 */
	public void makeEmptyNodes(int aNum) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Generate base Color List
	 */
	public void makeColors(int aNum) {
		throw new UnsupportedOperationException();
	}

	public int nodeUnchecked() {
		throw new UnsupportedOperationException();
	}

	public boolean allColorChecked(java.util.ArrayList<Color> aColorList) {
		throw new UnsupportedOperationException();
	}

	public void removeEqualsLabels() {
		throw new UnsupportedOperationException();
	}

	public boolean containLabel(java.util.ArrayList<Color> aColorList, java.util.ArrayList<Color> aLabel) {
		throw new UnsupportedOperationException();
	}

	public void loadLabels() {
		throw new UnsupportedOperationException();
	}

	/**
	 * comment
	 */
	public void resetNodesCheck() {
		throw new UnsupportedOperationException();
	}

	public void resetColorCheck() {
		throw new UnsupportedOperationException();
	}

	public void resetSolutionCheck() {
		throw new UnsupportedOperationException();
	}

	public void resetArchsCheck() {
		throw new UnsupportedOperationException();
	}

	public void randomize() {
		throw new UnsupportedOperationException();
	}

	public void sortIntAsc(java.util.ArrayList<Integer> aArr) {
		throw new UnsupportedOperationException();
	}

	public void setLabels(java.util.HashMap<String, Label> aLabels) {
		this._labels = aLabels;
	}

	public java.util.HashMap<String, Label> getLabels() {
		return this._labels;
	}

	public void setMinLabelLength(int aMinLabelLength) {
		this._minLabelLength = aMinLabelLength;
	}

	public int getMinLabelLength() {
		return this._minLabelLength;
	}

	public void setNodeList(Vector<Node> aNodeList) {
		this._nodeList = aNodeList;
	}

	public Vector<Node> getNodeList() {
		return this._nodeList;
	}

	public void setArchList(Vector<Arch> aArchList) {
		this._archList = aArchList;
	}

	public Vector<Arch> getArchList() {
		return this._archList;
	}

	public void setColorList(Vector<Color> aColorList) {
		this._colorList = aColorList;
	}

	public Vector<Color> getColorList() {
		return this._colorList;
	}

	public void setLabelList(Vector<Label> aLabelList) {
		this._labelList = aLabelList;
	}

	public Vector<Label> getLabelList() {
		return this._labelList;
	}
	public class IntComparatorAsc {

		public int compare(Integer aO1, Integer aO2) {
			throw new UnsupportedOperationException();
		}
	}
}