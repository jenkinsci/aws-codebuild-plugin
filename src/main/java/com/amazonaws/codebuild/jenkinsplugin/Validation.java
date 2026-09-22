/*
 *  Copyright 2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance with the License.
 *     A copy of the License is located at
 *
 *         http://aws.amazon.com/apache2.0/
 *
 *     or in the "license" file accompanying this file.
 *     This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and limitations under the License.
 *
 *  Portions copyright Copyright 2004-2011 Oracle Corporation.
 *  Please see LICENSE.txt for applicable license terms and NOTICE.txt for applicable notices.
 */
package com.amazonaws.codebuild.jenkinsplugin;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.codebuild.model.InvalidInputException;

public class Validation {

    //AWSClientFactory
    public static final String invalidDefaultCredentialsError = "AWS credentials couldn't be loaded from the default provider chain";
    public static final String invalidCredTypeError = "Invalid credentialsType option; must be 'jenkins' or 'keys'";
    public static final String invalidSecretKeyError = "awsSecretKey cannot be null";
    public static final String stepCredentials = "Using credentials provided by the Jenkins step context for authorization";

    public static String sanitize(final String s) {
        if(s == null) {
            return "";
        } else {
            return escapeSql(escapeHtml(s.trim()));
        }
    }

    // Escapes the HTML metacharacters that matter for output safety.
    // Replaces the former commons-lang StringEscapeUtils.escapeHtml, which
    // Jenkins core stopped providing in 2.579; ampersand is escaped first so
    // the entities it inserts are not double-escaped.
    private static String escapeHtml(final String s) {
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    // Doubles single quotes, matching the former commons-lang
    // StringEscapeUtils.escapeSql.
    private static String escapeSql(final String s) {
        return s.replace("'", "''");
    }

    private static boolean isNotEmpty(final String s) {
        return s != null && !s.isEmpty();
    }

    public static Integer parseInt(String s) {
        if(s == null || s.isEmpty()) {
            return null;
        } else {
            return Integer.parseInt(s);
        }
    }

    // Fields rendered as True/False selects (enums.BooleanValue) only match
    // capitalized options. Normalize 'true'/'false' written via the API or
    // pipeline snippets so a form save cannot silently flip the value.
    public static String sanitizeBooleanSelect(final String s) {
        String v = sanitize(s);
        if(v.equalsIgnoreCase("true")) {
            return "True";
        }
        if(v.equalsIgnoreCase("false")) {
            return "False";
        }
        return v;
    }

    public static String sanitizeYAML(final String s) {
        if(s == null) {
            return "";
        } else {
            return s.replace("\t", " ");
        }
    }

    public static AwsCredentialsProvider getBasicCredentialsOrDefaultChain(String accessKey, String secretKey) {
        return getBasicCredentialsOrDefaultChain(accessKey, secretKey, "");
    }

    public static AwsCredentialsProvider getBasicCredentialsOrDefaultChain(String accessKey, String secretKey, String awsSessionToken) {
        AwsCredentialsProvider result;
        if (isNotEmpty(accessKey) && isNotEmpty(secretKey) && isNotEmpty(awsSessionToken)) {
            result = StaticCredentialsProvider.create(AwsSessionCredentials.create(accessKey, secretKey, awsSessionToken));
        }
        else if (isNotEmpty(accessKey) && isNotEmpty(secretKey)) {
            result = StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));
        } else {
            result = DefaultCredentialsProvider.create();
            try {
                result.resolveCredentials();
            } catch (SdkClientException e) {
                throw InvalidInputException.builder().message(invalidDefaultCredentialsError).build();
            }
        }
        return result;
    }

}
