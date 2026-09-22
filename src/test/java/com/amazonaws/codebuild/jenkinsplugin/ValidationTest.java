/*
 *  Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *  SPDX-License-Identifier: Apache-2.0
 */

package com.amazonaws.codebuild.jenkinsplugin;

import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ValidationTest {

    @Test
    public void sanitizeEscapesHtmlAndSqlMetacharacters() {
        assertEquals("", Validation.sanitize(null));
        assertEquals("plain-project", Validation.sanitize("  plain-project  "));
        assertEquals("a&amp;b", Validation.sanitize("a&b"));
        assertEquals("&lt;script&gt;", Validation.sanitize("<script>"));
        assertEquals("say &quot;hi&quot;", Validation.sanitize("say \"hi\""));
        assertEquals("O''Brien", Validation.sanitize("O'Brien"));
        // ampersand escaped once, not double-escaped through the inserted entities
        assertEquals("&lt;a&gt;&amp;&lt;/a&gt;", Validation.sanitize("<a>&</a>"));
    }

    // Jenkins core removed Apache Commons Lang 2 in 2.579. Depending on it made
    // the build step fail to load before logging anything. Guard against the
    // package creeping back into the plugin's own sources.
    @Test
    public void noSourceReferencesCommonsLang2() throws Exception {
        Path srcMain = Paths.get("src/main/java");
        try (Stream<Path> files = Files.walk(srcMain)) {
            files.filter(p -> p.toString().endsWith(".java")).forEach(p -> {
                try {
                    String body = new String(Files.readAllBytes(p));
                    assertFalse(p + " must not import org.apache.commons.lang (removed from Jenkins core in 2.579)",
                            body.contains("org.apache.commons.lang.") || body.contains("org.apache.commons.lang;"));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
