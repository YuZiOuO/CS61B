package gitlet;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Config {
    public static final String REPO_FILENAME = "repo";
    public static final String STAGING_AREA_FILENAME = "staged";
    public static final String INITIAL_COMMIT_MESSAGE = "initial commit";

    public static final String GITLET_DIR_NAME = ".gitlet";

    public static final Map<String, Set<Integer>> arguments = new HashMap<>();
    static{
        arguments.put("init",Set.of(1));
        arguments.put("log",Set.of(1));
        arguments.put("status",Set.of(1));
        arguments.put("global-log",Set.of(1));
        arguments.put("add",Set.of(2));
        arguments.put("branch",Set.of(2));
        arguments.put("rm-branch",Set.of(2));
        arguments.put("find",Set.of(2));
        arguments.put("rm",Set.of(2));
        arguments.put("reset",Set.of(2));
        arguments.put("merge",Set.of(2));
        arguments.put("commit",Set.of(1,2));
        arguments.put("checkout",Set.of(2,3,4));
    }
}
