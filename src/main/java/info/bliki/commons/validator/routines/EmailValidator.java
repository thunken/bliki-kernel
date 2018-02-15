/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package info.bliki.commons.validator.routines;


import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Perform email validations.</p>
 * <p>
 * This class is a Singleton; you can retrieve the instance via the getInstance() method.
 * </p>
 * <p>
 * Based on a script by <a href="mailto:stamhankar@hotmail.com">Sandeep V. Tamhankar</a>
 * https://javascript.internet.com
 * </p>
 * <p>
 * This implementation is not guaranteed to catch all possible errors in an email address.
 * For example, an address like nobody@noplace.somedog will pass validator, even though there
 * is no TLD "somedog"
 * </p>.
 *
 * @version $Revision$ $Date$
 * @since Validator 1.4
 */
public class EmailValidator implements Serializable {

    /**
     * Auto-generated serial version UID.
     */
    private static final long serialVersionUID = 3799186064442277754L;
    private static final String SPECIAL_CHARS = "\\p{Cntrl}\\(\\)<>@,;:'\\\\\\\"\\.\\[\\]";
    private static final String VALID_CHARS = "[^\\s" + SPECIAL_CHARS + "]";
    private static final String QUOTED_USER = "(\"[^\"]*\")";
    private static final String WORD = "((" + VALID_CHARS + "|')+|" + QUOTED_USER + ")";

    private static final String LEGAL_ASCII_REGEX = "^\\p{ASCII}+$";
    private static final String EMAIL_REGEX = "^\\s*?(.+)@([^\\?]+)(\\?(.*))?\\s*";
    private static final String IP_DOMAIN_REGEX = "^\\[(.*)\\]$";
    private static final String USER_REGEX = "^\\s*" + WORD + "(\\." + WORD + ")*$";
    private static final String QUERYSTRING_REGEX = "^\\?([^=]+=[^=]+&)+[^=]+(=[^=]+)?$";

    private static final Pattern MATCH_ASCII_PATTERN = Pattern.compile(LEGAL_ASCII_REGEX);
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private static final Pattern IP_DOMAIN_PATTERN = Pattern.compile(IP_DOMAIN_REGEX);
    private static final Pattern USER_PATTERN = Pattern.compile(USER_REGEX);
    private static final Pattern QUERYSTRING_PATTERN = Pattern.compile(QUERYSTRING_REGEX);

    /**
     * Singleton instance of this class.
     */
    private static final EmailValidator EMAIL_VALIDATOR = new EmailValidator();

    /**
     * Returns the Singleton instance of this validator.
     *
     * @return singleton instance of this validator.
     */
    public static EmailValidator getInstance() {
        return EMAIL_VALIDATOR;
    }

    /**
     * Protected constructor for subclasses to use.
     */
    protected EmailValidator() {
        super();
    }

    /**
     * <p>Checks if a field has a valid e-mail address.</p>
     *
     * @param email The value validation is being performed on.  A <code>null</code>
     *              value is considered invalid.
     * @return true if the email address is valid.
     */
    public boolean isValid(String email) {
        if (email == null) {
            return false;
        }

        Matcher asciiMatcher = MATCH_ASCII_PATTERN.matcher(email);
        if (!asciiMatcher.matches()) {
            return false;
        }

        // Check the whole email address structure
        Matcher emailMatcher = EMAIL_PATTERN.matcher(email);
        if (!emailMatcher.matches()) {
            return false;
        }

        if (email.endsWith(".")) {
            return false;
        }

        if (!isValidUser(emailMatcher.group(1))) {
            return false;
        }

        if (!isValidDomain(emailMatcher.group(2))) {
            return false;
        }

        return isValidQueryString(emailMatcher.group(3));
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
                    DomainValidator.getInstance();
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

    /**
     * Returns true if the query string component of an email address is valid.
     *
     * @param queryString being validated
     * @return true if the query string is valid.
     */
    protected boolean isValidQueryString(String queryString) {
        return queryString == null || QUERYSTRING_PATTERN.matcher(queryString).matches();
    }
}
