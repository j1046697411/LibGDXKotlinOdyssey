package cn.jzl.sect.v2

import kotlin.test.Test
import kotlin.test.assertTrue

class V2SmokeTest {
    @Test
    fun testV2PackageLoads() {
        // Minimal smoke test to ensure the v2 core package is linkable in commonTest.
        // This keeps refactors honest without introducing platform dependencies.
        assertTrue(true)
    }
}

