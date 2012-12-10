/*
 * Copyright 2012 Sparks Creative Limited
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package net.uk.sparks.webstack.compass.plugin;

import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static junit.framework.Assert.assertEquals;

public class FilePathHelperTest {

    FilePathHelper helper;

    @Before
    public void setUp() {
        helper = new FilePathHelper();
    }

    @Test
    public void testSamePath() throws Exception {
        assertEqualPaths(".", "/a/b/c", "/a/b/c");
    }

    @Test
    public void testChildPath() throws Exception {
        assertEqualPaths("d", "/a/b/c", "/a/b/c/d");
    }

    @Test
    public void testParentPath() throws Exception {
        assertEqualPaths("..", "/a/b/c/d", "/a/b/c");
    }

    @Test
    public void testLowerBranchedPath() throws Exception {
        assertEqualPaths("../x/y", "/a/b/c", "/a/b/x/y");
    }

    @Test
    public void testHigherBranchedPath() throws Exception {
        assertEqualPaths("../../x", "/a/b/c/d", "/a/b/x");
    }

    private void assertEqualPaths(String expected, String source, String target) {
        assertEquals(expected, helper.getRelativePath(new File(source), new File(target)));
    }
}
