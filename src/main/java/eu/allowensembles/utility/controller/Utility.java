package eu.allowensembles.utility.controller;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import eu.fbk.das.process.engine.api.domain.ProcessDiagram;
import eu.allowensembles.controller.MainController;
import eu.allowensembles.evoknowledge.controller.*;
import eu.allowensembles.robustness.controller.*;
import eu.allowensembles.utils.Alternative;
import eu.allowensembles.utils.UserData;
import eu.allowensembles.privacyandsecurity.controller.*;


public class Utility //implements IUtility{
{
	private static Comparator<Alternative> descUtility = new Comparator<Alternative>() {
        @Override
        public int compare(Alternative rt1, Alternative rt2){
            return (int) ((rt2.getUtility() - rt1.getUtility()) * 100);
        }
	};
	private double timeConst, costConst, reliabilityConst, walkConst, securityConst, privacyConst, nocConst;
	private ProcessDiagram processDiagram;
	
	public Utility(){
		timeConst = 0.5;
		costConst = 0.6;
		reliabilityConst = 0.01;
		walkConst = 0.1;
		securityConst = 3.0;
		privacyConst = 2.0;
		nocConst = 1.0;
		processDiagram=null;
	}
	
	public Utility(double tConst, double cConst, double rConst, double wConst, double sCost, double pConst, double nConst, ProcessDiagram pd){
		timeConst = tConst;
		costConst = cConst;
		reliabilityConst = rConst;
		walkConst = wConst;
		securityConst = sCost;
		privacyConst = pConst;
		nocConst = nConst;
		processDiagram = pd;
	}

	
	public List<Alternative> rankAlternatives(UserData userData, MainController controller) {
		Preferences prefs = new Preferences();
		List<Alternative> alternatives = new ArrayList<Alternative>();
		if(userData!=null){
			prefs = userData.getPreferences();
			double sum = prefs.getTTweight()
				    + prefs.getCweight()
				    + prefs.getNCweight()
				    + prefs.getRCweight()
				    + prefs.getUSPweight()
				    + prefs.getWSDweight()
				    + prefs.getWDweight();
			prefs.setTmax(prefs.getTmax());
			prefs.setTTweight(prefs.getTTweight() / sum);
			prefs.setTTweight(Math.round(prefs.getTTweight()*100.0)/100.0);		
			prefs.setCmax(prefs.getCmax());
			prefs.setCweight(prefs.getCweight() / sum);
			prefs.setCweight(Math.round(prefs.getCweight()*100.0)/100.0);	
			prefs.setNCweight(prefs.getNCweight() / sum);
			prefs.setNCweight(Math.round(prefs.getNCweight()*100.0)/100.0);	
			prefs.setNoCmax((int) prefs.getNoCmax());
			prefs.setRCweight(prefs.getRCweight() / sum);
			prefs.setRCweight(Math.round(prefs.getRCweight()*100.0)/100.0);	
			prefs.setUSPweight(prefs.getUSPweight() / sum);
			prefs.setUSPweight(Math.round(prefs.getUSPweight()*100.0)/100.0);	
			prefs.setWSDweight(prefs.getWSDweight() / sum);
			prefs.setWSDweight(Math.round(prefs.getWSDweight()*100.0)/100.0);	
			prefs.setWmax(prefs.getWmax());
			prefs.setWDweight(prefs.getWDweight() / sum);
			prefs.setWDweight(Math.round(prefs.getWDweight()*100.0)/100.0);	
			
			alternatives = userData.getAlternatives();
		}
		IUtilityParameterEstimator est=new EvoKnowledgeCRF();
	/*	for (Alternative al : alternatives) {
			al.setTravelTime((long) est.predictTravelTime(al)); 
			al.setUtility(computeUtility(prefs, al));
		}*/
		for (int i=0;i<alternatives.size();i++) {
			alternatives.get(i).setTravelTime((long) est.predictTravelTime(alternatives.get(i))); 
			alternatives.get(i).setUtility(computeUtility(prefs, alternatives.get(i), userData.getName(),controller));
		}
		alternatives.sort(descUtility);
		return alternatives;
	}
	
	public double computeUtility(Preferences prefs, Alternative al, String name, MainController controller) {
	
		double robustness = 0;
		double ps[] = new double[2];
	
		double u = prefs.getTTweight() * Math.exp((-timeConst*al.getTravelTime())/(prefs.getTmax()*60));
		
		u +=  prefs.getCweight() * (Math.exp((-costConst*al.getCost()) / prefs.getCmax()));
		/* Call robustness method to get robustness value for the specific alternative*/
		robustness = RobustnessCalculator.getRobustness (processDiagram, al);
		u += prefs.getRCweight() * Math.exp(-reliabilityConst*Math.pow(robustness/(1-robustness)-prefs.getCmax(), 2));
		
		u += prefs.getWDweight() * Math.exp(-walkConst*(Math.pow(al.getWalkingDistance()/prefs.getWmax(), 2)));
		
		/* Call privacy and security  method to get USP (unsatisfied safety preference) and WSD 
		 * (Willingness to Share Data)value for the specific alternative*/
		PrivacyAndSecurity p = new PrivacyAndSecurity(controller);
		ps = p.getPSParameters(name);
		u += prefs.getUSPweight() * Math.pow((1 - ps[0]),securityConst);
		u += prefs.getWSDweight() * (1 - Math.pow(ps[1],privacyConst));
		if (al.getNoOfChanges() <= prefs.getNoCmax()){
			if(al.getNoOfChanges()==0)
				al.setNoOfChanges(1);
			u += prefs.getNCweight()*(nocConst * (prefs.getNoCmax() - al.getNoOfChanges() +1)/prefs.getNoCmax());
		}
		if (u < 0 || u>1)
			u=0;
		return u;
	}
	
	public double getTimeConst() {
		return timeConst;
	}
	
	public double getCostConst() {
		return costConst;
	}
	
	public double getReliabilityConst() {
		return reliabilityConst;
	}
	
	public double getWalkConst() {
		return walkConst;
	}
	
	public double getSecurityConst() {
		return securityConst;
	}
	
	public double getPrivacyConst() {
		return privacyConst;
	}
	
	public double getNoCConst() {
		return nocConst;
	}
	
	public ProcessDiagram getProcessDiagram() {
		return processDiagram;
	}
	
	public void setTimeConst(double timeConst) {
		this.timeConst = timeConst;
	}
	
	public void setCostConst(double costConst) {
		this.costConst = costConst;
	}
	
	public void setReliabilityConst(double reliabilityConst) {
		this.reliabilityConst = reliabilityConst;
	}
	
	public void setWalkConst(double walkConst) {
		this.walkConst = walkConst;
	}
	
	public void setSecurityConst(double securityConst) {
		this.securityConst = securityConst;
	}
	
	public void setPrivacyConst(double privacyConst) {
		this.privacyConst = privacyConst;
	}
	
	public void setNoCConst(double nocConst) {
		this.nocConst = nocConst;
	}
	
	public void setProcessDiagram(ProcessDiagram pd) {
		this.processDiagram = pd;
	}
	
	
}
