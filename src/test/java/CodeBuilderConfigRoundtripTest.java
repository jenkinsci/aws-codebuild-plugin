/*
 *  Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *  SPDX-License-Identifier: Apache-2.0
 */

import hudson.model.FreeStyleProject;
import hudson.util.Secret;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

public class CodeBuilderConfigRoundtripTest {

    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    public void configRoundtripProjectSourceMode() throws Exception {
        FreeStyleProject p = j.createFreeStyleProject();
        CodeBuilder before = new CodeBuilder(
                "keys", "", "proxy.example.com", "8080", "AKIAEXAMPLE", Secret.fromString("s3cret"), "token",
                "us-west-2", "jenkins-plugin-test", "v1", "", "project", "", "", "5",
                "true", "[]", "[]", "S3", "loc", "name", "BUILD_ID",
                "ZIP", "path", "false", "True", "[]",
                "[{k, v}]", "[]", "buildspec-override.yml", "42", "S3",
                "bucket/key", "LINUX_CONTAINER", "img", "BUILD_GENERAL1_SMALL",
                "LOCAL", "cache-loc", "LOCAL_SOURCE_CACHE", "ENABLED", "grp", "stream",
                "ENABLED", "false", "s3-log-loc", "cert", "role",
                "true", "false", "false", "ENABLED", "true", "artifacts/");
        before.workspaceExcludes = "";
        before.workspaceIncludes = "";
        p.getBuildersList().add(before);

        j.configRoundtrip(p);

        j.assertEqualDataBoundBeans(before, p.getBuildersList().get(CodeBuilder.class));
    }

    @Test
    public void configRoundtripJenkinsSourceMode() throws Exception {
        FreeStyleProject p = j.createFreeStyleProject();
        CodeBuilder before = new CodeBuilder(
                "keys", "", "", "", "", Secret.fromString(""), "",
                "us-west-2", "jenkins-plugin-test-s3", "", "AES256", "jenkins", "src/local", "subdir", "",
                "", "[]", "[]", "", "", "", "",
                "", "", "", "", "[]",
                "[]", "[]", "", "", "",
                "", "", "", "",
                "", "", "", "", "", "",
                "", "", "", "", "",
                "", "", "", "", "true", "");
        before.workspaceExcludes = "target/**";
        before.workspaceIncludes = "**/*.java";
        p.getBuildersList().add(before);

        j.configRoundtrip(p);

        j.assertEqualDataBoundBeans(before, p.getBuildersList().get(CodeBuilder.class));
    }
}
