package entity;

import java.util.Vector;

/**
 * @author nicolarusso
 * @param id is the identificator of a Node
 * @param checked is a flag that sign when a Node is inserted in solution
 */
public class Node {
	private int _id;
	private boolean _checked;
	java.util.Vector<Arch> _archList;

	public Node() {
		throw new UnsupportedOperationException();
	}

	public Node(int aId) {
		throw new UnsupportedOperationException();
	}

	public void setId(int aId) {
		this._id = aId;
	}

	public int getId() {
		return this._id;
	}

	public void setChecked(boolean aChecked) {
		this._checked = aChecked;
	}

	public boolean isChecked() {
		return this._checked;
	}

	public void setArchList(Vector<Arch> aArchList) {
		this._archList = aArchList;
	}

	public Vector<Arch> getArchList() {
		return this._archList;
	}
}