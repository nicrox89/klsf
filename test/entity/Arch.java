package entity;

/**
 * @author nicolarusso
 * @param fromNode is the first Node of a Arch
 * @param toNode is the second Node of a Arch
 * @param colorList is a list of the label's color
 */
public class Arch {
	private boolean _checked;
	private Node _fromNode;
	private Node _toNode;
	private Label _label;

	public Arch() {
		throw new UnsupportedOperationException();
	}

	public Arch(Node aFromNode, Node aToNode, Label aLabel) {
		throw new UnsupportedOperationException();
	}

	public void setChecked(boolean aChecked) {
		this._checked = aChecked;
	}

	public boolean isChecked() {
		return this._checked;
	}

	public void setFromNode(Node aFromNode) {
		this._fromNode = aFromNode;
	}

	public Node getFromNode() {
		return this._fromNode;
	}

	public void setToNode(Node aToNode) {
		this._toNode = aToNode;
	}

	public Node getToNode() {
		return this._toNode;
	}

	public void setLabel(Label aLabel) {
		this._label = aLabel;
	}

	public Label getLabel() {
		return this._label;
	}
}