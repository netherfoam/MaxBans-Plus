package org.maxgamer.maxbans.bridge;

import org.junit.Test;
import org.maxgamer.maxbans.PluginContextTest;
import org.maxgamer.maxbans.test.IntegrationTest;

import java.io.File;

/**
 * @author netherfoam
 */
public class MaxBansBridgeTest extends PluginContextTest implements IntegrationTest {
    @Test
    public void pullTest() throws Exception {
        File sqlFile = new File("target/test-classes/import", "maxbans.db");
        MaxBansBridge bridge = new MaxBansBridge(sqlFile);

        bridge.pull(getContext());
    }
}
