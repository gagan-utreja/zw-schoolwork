/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.gatech.oad.rocket.findmythings.server.util.validation;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Perform email validations against a string.
 *
 * This class is a Singleton that can be retrieved via the getInstance() method.
 *
 * This implementation is not guaranteed to catch all possible errors in an email address.
 */
public class EmailValidator implements Serializable {

	private static final long serialVersionUID = 1705927040799295880L;

	private static final String SPECIAL_CHARS = "\\p[\\x00-\\x1F\\x7F]\\(\\)<>@,;:'\\\\\"\\.\\[\\]";
	private static final String VALID_CHARS = "[^\\s" + SPECIAL_CHARS + "]";
	private static final String QUOTED_USER = "(\"[^\"]*\")";
	private static final String WORD = String.format("((%s|')+|%s)", VALID_CHARS, QUOTED_USER);

	private static final String LEGAL_ASCII_REGEX = "^\\p{ASCII}+$";
	private static final String EMAIL_REGEX = "^\\s*?(.+)@(.+?)\\s*$";
	private static final String IP_DOMAIN_REGEX = "^\\[(.*)\\]$";

	private static final Pattern MATCH_ASCII_PATTERN = Pattern.compile(LEGAL_ASCII_REGEX);
	private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
	private static final Pattern IP_DOMAIN_PATTERN = Pattern.compile(IP_DOMAIN_REGEX);
	private static final Pattern USER_PATTERN = Pattern.compile("^\\s*" + WORD + "(\\." + WORD + ")*$");

	private final boolean allowLocal;

	/**
	 * Singleton instance of this class, which
	 *  doesn't consider local addresses as valid.
	 */
	private static final EmailValidator EMAIL_VALIDATOR = new EmailValidator(false);

	/**
	 * Singleton instance of this class, which does
	 *  consider local addresses valid.
	 */
	private static final EmailValidator EMAIL_VALIDATOR_WITH_LOCAL = new EmailValidator(true);

	/**
	 * Returns the Singleton instance of this validator.
	 *
	 * @return singleton instance of this validator.
	 */
	public static EmailValidator getInstance() {
		return getInstance(false);
	}

	/**
	 * Returns the Singleton instance of this validator,
	 *  with local validation as required.
	 *
	 * @param allowLocal Should local addresses be considered valid?
	 * @return singleton instance of this validator
	 */
	public static EmailValidator getInstance(boolean allowLocal) {
		if(allowLocal) {
			return EMAIL_VALIDATOR_WITH_LOCAL;
		}
		return EMAIL_VALIDATOR;
	}

	/**
	 * Protected constructor for subclasses to use.
	 *
	 * @param allowLocal Should local addresses be considered valid?
	 */
	protected EmailValidator(boolean allowLocal) {
		super();
		this.allowLocal = allowLocal;
	}

	/**
	 * <p>Checks if a field has a valid e-mail address.</p>
	 *
	 * @param email The value validation is being performed on.  A <code>null</code>
	 *              value is considered invalid.
	 * @return true if the email address is valid.
	 */
	public boolean isValid(String email) {
		Matcher emailMatcher = EMAIL_PATTERN.matcher(email);
		return email != null &&
				MATCH_ASCII_PATTERN.matcher(email).matches() &&
				emailMatcher.matches() &&
				!email.endsWith(".") &&
				isValidUser(emailMatcher.group(1)) &&
				isValidDomain(emailMatcher.group(2));
	}

	/**
	 * Returns true if the domain component of an email address is valid.
	 *
	 * @param domain being validated.
	 * @return true if the email address's domain is valid.
	 */
	protected boolean isValidDomain(String domain) {
		// see if domain is an IP address in brackets
		Matcher ipDomainMatcher = IP_DOMAIN_PATTERN.matcher(domain);

		if (ipDomainMatcher.matches()) {
			InetAddressValidator inetAddressValidator =
					InetAddressValidator.getInstance();
			return inetAddressValidator.isValid(ipDomainMatcher.group(1));
		} else {
			// Domain is symbolic name
			DomainValidator domainValidator =
					DomainValidator.getInstance(allowLocal);
			return domainValidator.isValid(domain);
		}
	}

	/**
	 * Returns true if the user component of an email address is valid.
	 *
	 * @param user being validated
	 * @return true if the user name is valid.
	 */
	protected boolean isValidUser(String user) {
		return USER_PATTERN.matcher(user).matches();
	}

}
