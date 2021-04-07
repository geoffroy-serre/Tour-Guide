package com.tourGuide.helper;

public class InternalTestHelper {

	/**
	 * Number of user use for testing purposes.
	 */
	private  static int internalUserNumber = 10000;

	/**
	 * Set the number of user used for testing purposes.
	 * @param internalUserNumber int
	 */
	public static void setInternalUserNumber(int internalUserNumber) {
		InternalTestHelper.internalUserNumber = internalUserNumber;
	}

	/**
	 * Retrieve the setted number of InternalTestUser for testing purposes.
	 * @return int InternalUserNumber
	 */
	public static int getInternalUserNumber() {
		return internalUserNumber;
	}
}
