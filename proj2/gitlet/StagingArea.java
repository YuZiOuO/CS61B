package gitlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static gitlet.Utils.*;

class StagingArea {
    static final File STAGING_AREA_DIR = join(Repository.GITLET_DIR, "staged");

    private static String stagingAreaPath;

    private static HashMap<String, HashFile> files;

    void add(String[] name) throws IOException {
        if (name == null) {
            return;
        }

        for (String n : name) {
            if (n.equals(".gitlet")) {
                continue;
            }

            File src = join(Repository.CWD, name);
            byte[] buf = readContents(src);
            String hash = sha1(buf);

            HashFile original = files.get(n);
            if (original != null) {
                if (original.getContentHash().equals(hash)) {
                    continue;
                }
                Files.delete(original.toPath());
                continue; //adjust for guide
            }
            File dest = join(STAGING_AREA_DIR, hash);
            Files.copy(src.toPath(), dest.toPath());
            files.put(n, new HashFile(dest));
        }
    }

    void addAll() throws IOException {
        List<String> allFiles = plainFilenamesIn(Repository.CWD);
        if (allFiles != null) {
            this.add(allFiles.toArray(new String[0]));
        }
    }

    void clear() {
        for (HashFile f : files.values()) {
            f.delete();
        }
        files.clear();
    }

    boolean isClear() {
        return files.isEmpty();
    }

    boolean contains(String name) {
        return files.containsKey(name);
    }

    Commit extract(String message, String parent, Date timestamp) throws IOException {
        clear();
        return new Commit(message, parent, timestamp, files);
    }

}
