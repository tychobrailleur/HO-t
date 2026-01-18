package core.db.backup;

import core.util.HOLogger;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class HOZip extends File {

    private static final int COMPRESSION_LEVEL = 5;
    private static final int COMPRESSION_METHOD = ZipOutputStream.DEFLATED;
    public static final String ZIP_EXT = "zip";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private final ZipOutputStream zOut;
    private int fileCount = 0;

    /**
     * Creates a new HOZip object.
     */
    public HOZip(String filename) throws IOException {
        super(filename);
        HOLogger.instance().info(getClass(), "Create Backup: " + filename);
        zOut = new ZipOutputStream(new FileOutputStream(this));
        zOut.setMethod(COMPRESSION_METHOD);
        zOut.setLevel(COMPRESSION_LEVEL);
    }

    public int getFileCount() {
        return fileCount;
    }

    public void addFile(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            zOut.putNextEntry(new ZipEntry(file.getName()));
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) >= 0) {
                zOut.write(buffer, 0, length);
            }
            zOut.closeEntry();
        }
        fileCount++;
    }

    public void closeArchive() throws IOException {
        zOut.finish();
        zOut.close();
    }

    public static String createZipName(String prefix) {
        return prefix + sdf.format(new Date()) + "." + ZIP_EXT;
    }
}
