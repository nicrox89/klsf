package entity;

import java.util.ArrayList;
import java.util.Vector;

/**
 * @author nicolarusso
 */
public class Label {
	private boolean _checked;
	private int _archs;
	private java.util.Vector<Color> _colors;

	public Label(java.util.ArrayList<Color> aLabel) {
		throw new UnsupportedOperationException();
	}

	public void increaseArchs() {
		throw new UnsupportedOperationException();
	}

	public void setChecked(boolean aChecked) {
		this._checked = aChecked;
	}

	public boolean isChecked() {
		return this._checked;
	}

	public void setArchs(int aArchs) {
		this._archs = aArchs;
	}

	public int getArchs() {
		return this._archs;
	}

	public void setColors(Vector<Color> aColors) {
		this._colors = aColors;
	}

	public Vector<Color> getColors() {
		return this._colors;
	}
}