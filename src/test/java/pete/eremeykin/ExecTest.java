package pete.eremeykin;

import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.WithoutMojo;

import org.junit.Rule;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import pete.eremeykin.DumpMojo;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@RunWith(Parameterized.class)
public class ExecTest {
    @Rule
    public MojoRule rule = new MojoRule();

    @Test
    public void testExec() throws Exception {

    }
}