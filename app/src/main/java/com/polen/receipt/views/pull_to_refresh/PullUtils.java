package com.polen.receipt.views.pull_to_refresh;


import com.polen.receipt.utils.Utils;

public class PullUtils {
	public static void warnDeprecation(String depreacted, String replacement) {
		Utils.printDebug(null, "You're using the deprecated " + depreacted + " attr, please switch over to " + replacement);
	}

}