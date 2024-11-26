package gitlet;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Config {
    public static final String REPO_FILENAME = "repo";
    public static final String STAGING_AREA_FILENAME = "staged";
    public static final String INITIAL_COMMIT_MESSAGE = "initial commit";

    public static final String GITLET_DIR_NAME = ".gitlet";

    public static final Map<String, Set<Integer>> ARGUMENT_CONFIG = new HashMap<>();

    static {
        ARGUMENT_CONFIG.put("init", Set.of(1));
        ARGUMENT_CONFIG.put("log", Set.of(1));
        ARGUMENT_CONFIG.put("status", Set.of(1));
        ARGUMENT_CONFIG.put("global-log", Set.of(1));
        ARGUMENT_CONFIG.put("add", Set.of(2));
        ARGUMENT_CONFIG.put("branch", Set.of(2));
        ARGUMENT_CONFIG.put("rm-branch", Set.of(2));
        ARGUMENT_CONFIG.put("find", Set.of(2));
        ARGUMENT_CONFIG.put("rm", Set.of(2));
        ARGUMENT_CONFIG.put("reset", Set.of(2));
        ARGUMENT_CONFIG.put("merge", Set.of(2));
        ARGUMENT_CONFIG.put("commit", Set.of(1, 2));
        ARGUMENT_CONFIG.put("checkout", Set.of(2, 3, 4));
    }
}
