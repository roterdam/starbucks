package edu.mit.compilers.opt.regalloc;

import java.util.List;
import java.util.Map;

import edu.mit.compilers.codegen.Reg;

/**
 * Simply indicates to WebProcessor that this guy cares about knowing which webs
 * are live at a given point in time. (As well as the mapping of those webs
 * later on.)
 * 
 * @author joshma
 * 
 */
public interface LiveWebsActivist {

	public void setLiveWebs(List<Web> liveWebs);
	public void applyAllocatedMapping(Map<Web, Reg> mapping);

}
