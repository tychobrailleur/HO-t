package core.db.backup;

import core.db.user.UserManager;
import core.util.HOLogger;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * HSQL DB zipper
 * 
 * @author Thorsten Dietz
 */
public final class BackupHelper {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private static final List<String> EXTENSIONS = Arrays.asList("script", "data", "backup", "log", "properties");

    private BackupHelper() {
        // Private constructor to prevent instantiation
    }

    // zip and delete db
    public static void backup(File dbDirectory) {
        if (!dbDirectory.exists()) {
            return;
        }

        File[] filesToBackup = getFilesToBackup(dbDirectory);
        if (filesToBackup.length == 0) {
            return;
        }

        try {
            String zipName = dbDirectory + File.separator + "db_"
                    + UserManager.instance().getCurrentUser().getTeamName() + "-" + sdf.format(new Date()) + "."
                    + HOZip.ZIP_EXT;
            HOZip zOut = new HOZip(zipName);

            for (File file : filesToBackup) {
                zOut.addFile(file);
            }

            zOut.closeArchive();
        } catch (Exception e) {
            HOLogger.instance().log(BackupHelper.class, e);
        }

        deleteOldFiles(dbDirectory);
    }

    /**
     * Deletes old zip files in the directory <code>dbDirectory</code>.
     *
     * @param dbDirectory Directory where to find the zip files to be deleted.
     */
    private static void deleteOldFiles(File dbDirectory) {
        File[] filesArr = dbDirectory.listFiles(file -> file.isFile() && file.getName().endsWith("." + HOZip.ZIP_EXT));

        if (filesArr != null) {
            Arrays.stream(filesArr)
                    .sorted(Comparator.comparingLong(File::lastModified).reversed())
                    .skip(UserManager.instance().getCurrentUser().getNumberOfBackups())
                    .forEach(File::delete);
        }
    }

    private static File[] getFilesToBackup(File dbDirectory) {
        File[] files = dbDirectory.listFiles(
                file -> file.isFile() && EXTENSIONS.stream().anyMatch(suffix -> file.getName().endsWith("." + suffix)));
        return files != null ? files : new File[0];
    }
}
