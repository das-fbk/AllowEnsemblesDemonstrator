package eu.allowensembles.privacyandsecurity.controller;

import eu.allowensembles.utils.Alternative;

public interface IPrivacyAndSecurity {

	/**
	 * Return privacy and security parameters for
	 * a given journey alternative.
	 * @return USP (Unsatisfied security Preferences)
	 * and WSD (Willingness to Share Data) parameters
	 */
	//public double[] getPSParameters(Alternative alt);
	
	public double[] getPSParameters(String user);
}
