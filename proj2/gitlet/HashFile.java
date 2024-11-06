package gitlet;

import java.io.File;
import java.io.IOException;

import static gitlet.Utils.readContents;
import static gitlet.Utils.sha1;

/* 用于内容不会变动的文件 */
class HashFile extends File {
    private String contentHash = null;

    public HashFile(File f) throws IOException {
        super(f.getPath());
        computeHash();
    }

//    public HashFile(String parent, String child) {
//        super(parent, child);
//    }

    void computeHash() throws IOException {
        byte[] buf = readContents(this);
        contentHash = sha1(buf);
    }

    public String getContentHash() {
        return contentHash;
    }
}
