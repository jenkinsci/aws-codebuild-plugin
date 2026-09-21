/*
 *  Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *  SPDX-License-Identifier: Apache-2.0
 */

import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import software.amazon.awssdk.services.codebuild.model.BuildPhase;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

public class CodeBuildActionRenderTest {

    @Rule
    public JenkinsRule rule = new JenkinsRule();

    @Test
    public void phaseTableRendersSdkV2PhaseFields() throws Exception {
        FreeStyleProject project = rule.createFreeStyleProject();
        FreeStyleBuild build = rule.buildAndAssertSuccess(project);

        CodeBuildAction action = new CodeBuildAction(build);
        action.setBuildId("proj:11111111-2222-3333-4444-555555555555");
        action.setPhases(new ArrayList<>(Arrays.asList(
                BuildPhase.builder()
                        .phaseType("SUBMITTED")
                        .phaseStatus("SUCCEEDED")
                        .startTime(Instant.parse("2026-09-18T16:24:00Z"))
                        .durationInSeconds(7L)
                        .build(),
                BuildPhase.builder()
                        .phaseType("COMPLETED")
                        .startTime(Instant.parse("2026-09-18T16:24:37Z"))
                        .build())));
        build.addAction(action);

        String html = rule.createWebClient()
                .getPage(build, action.getUrlName())
                .getWebResponse().getContentAsString();

        assertThat(html, containsString("SUBMITTED"));
        assertThat(html, containsString("SUCCEEDED"));
        assertThat(html, containsString("7 seconds"));
    }
}
