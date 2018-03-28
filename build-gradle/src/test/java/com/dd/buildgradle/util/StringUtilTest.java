package com.dd.buildgradle.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StringUtilTest {

    @org.junit.Test
    public void isMavenArtifact() {
        assertTrue(StringUtil.isMavenArtifact("com.android.databinding:dataBinder:1.0-rc4@aar"));
        assertTrue(StringUtil.isMavenArtifact("com.android.databinding:dataBinder:1.0-rc4@jar"));
        assertTrue(StringUtil.isMavenArtifact("com.android.databinding:dataBinder:1.0-rc4"));
        assertTrue(StringUtil.isMavenArtifact("com.android.databinding:dataBinder:1.0"));
        assertTrue(StringUtil.isMavenArtifact("com.android.databinding:dataBinder"));
    }

    @org.junit.Test
    public void notMavenArtifact() {
        assertFalse(StringUtil.isMavenArtifact("app"));
        assertFalse(StringUtil.isMavenArtifact(":app"));
        assertFalse(StringUtil.isMavenArtifact(":modules:umeng_bbs"));
        assertFalse(StringUtil.isMavenArtifact("modules:umeng_bbs"));
    }

}
