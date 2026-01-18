package core.db.backup;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class HOZipTest {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private String generateTempZipFileName() {
        String customDir = System.getProperty("java.io.tmpdir");
        long tempFileName = System.currentTimeMillis();
        return customDir + File.separator + tempFileName + ".zip";
    }

    @Test
    void testAddFile() throws Exception {
        String fileName = generateTempZipFileName();
        HOZip hoZip = new HOZip(fileName);

        URL testFileUrl = this.getClass().getClassLoader().getResource("tools/sample.txt");
        Assertions.assertNotNull(testFileUrl);
        hoZip.addFile(new File(testFileUrl.getPath()));
        hoZip.closeArchive();

        File zipFile = new File(fileName);
        Assertions.assertTrue(zipFile.exists());
        Assertions.assertEquals(1, hoZip.getFileCount());

        // Clean up
        zipFile.delete();
    }

    @Test
    void testCreateZipFileName() {
        Assertions.assertEquals("test-" + formatter.format(LocalDateTime.now()) + ".zip", HOZip.createZipName("test-"));
    }
}
