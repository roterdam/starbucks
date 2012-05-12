package edu.mit.compilers.opt.regalloc;

import java.util.HashSet;
import java.util.Set;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.opt.State;

public class WebState implements State<WebState> {

	private Set<Web> liveWebs;

	public WebState() {
		liveWebs = new HashSet<Web>();
	}

	public WebState(Set<Web> newLiveWebs) {
		liveWebs = newLiveWebs;
	}

	public Set<Web> getLiveWebs() {
		return liveWebs;
	}

	public WebState getInitialState() {
		return new WebState();
	}

	public WebState getBottomState() {
		return new WebState();
	}

	public WebState join(WebState s) {
		LogCenter.debug("RA", "Joining " + s + " with " + this);
		if (s == null) {
			return clone();
		}
		// A live web node is live in either both place.
		Set<Web> thisLiveWebs = new HashSet<Web>(getLiveWebs());
		Set<Web> thatLiveWebs = s.getLiveWebs();
		thisLiveWebs.addAll(thatLiveWebs);
		WebState out = new WebState(thisLiveWebs);
		return out;
	}

	@Override
	public WebState clone() {
		Set<Web> newLiveWebs = new HashSet<Web>(getLiveWebs());
		WebState out = new WebState(newLiveWebs);
		return out;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof WebState)) {
			return false;
		}
		WebState otherState = (WebState) o;
		return liveWebs.equals(otherState.getLiveWebs());
	}

	@Override
	public String toString() {
		return getLiveWebs().toString();
	}

	public void birthWeb(Web web) {
		assert web != null;
		LogCenter.debug("RA", "Birthing new web " + web);
		liveWebs.add(web);
	}

	public void killWeb(Web web) {
		LogCenter.debug("RA", "Killing web " + web);
		liveWebs.remove(web);
	}

	public void interfereWith(Web web) {
		for (Web liveWeb : liveWebs) {
			assert liveWeb != null;
			LogCenter.debug("RA", "Marking " + web + " as interfering with "
					+ liveWeb);
			liveWeb.addInterference(web);
		}
	}

}
