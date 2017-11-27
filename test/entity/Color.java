package entity;

/**
 * @author nicolarusso
 * @param color is the identificator of a color
 * @param checked is a boolean that sign when a color is used for label
 * combination
 */
public class Color {
	private int _color;
	private boolean _checked;
	private boolean _solution;
	private int _archs;

	public Color(int aColor) {
		throw new UnsupportedOperationException();
	}

	public void resetCounter() {
		throw new UnsupportedOperationException();
	}

	public void increaseCounter() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String toString() {
		throw new UnsupportedOperationException();
	}

	public int compareTo(Color aOtherColor) {
		throw new UnsupportedOperationException();
	}

	public void setColor(int aColor) {
		this._color = aColor;
	}

	public int getColor() {
		return this._color;
	}

	public void setChecked(boolean aChecked) {
		this._checked = aChecked;
	}

	public boolean isChecked() {
		return this._checked;
	}

	public void setSolution(boolean aSolution) {
		this._solution = aSolution;
	}

	public boolean isSolution() {
		return this._solution;
	}

	public int getArchs() {
		return this._archs;
	}
}