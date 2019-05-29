package flight_sim;

import java.util.Observable;
import java.util.Observer;

public class Var extends Observable implements Observer {
	double v;
	String name;
	String Loc;

	public Var(String loc) {
		super();
		Loc = loc;
	}

	@Override
	public void update(Observable o, Object arg) {
		if(this.v!=(double)arg)
			this.setV((double)arg);
	}

	public Var(double v) {
		this.v=v;
	}
	public Var() {

	}

	public double getV() {
		return v;
	}
	public void setV(double v) {
		this.v = v;
		setChanged();
		notifyObservers(v);

	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLoc() {
		return Loc;
	}
	public void setLoc(String loc) {
		Loc = loc;
	}
}
