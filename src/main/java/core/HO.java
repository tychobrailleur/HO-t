package core;

import clojure.java.api.Clojure;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.main;
import core.db.DBManager;
import core.db.backup.BackupHelper;
import core.db.user.UserManager;
import core.gui.HOMainFrame;
import core.gui.SplashFrame;
import core.gui.model.UserColumnController;
import core.gui.theme.ImageUtilities;
import core.gui.theme.ThemeManager;
import core.jmx.StatementCacheMonitor;
import core.model.HOVerwaltung;
import core.model.TranslationFacility;
import core.model.UserParameter;
import core.training.TrainingManager;
import core.util.ExceptionHandler;
import core.util.HOLogger;
import core.util.OSUtils;

import javax.imageio.ImageIO;
import javax.management.*;
import javax.swing.*;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import static java.awt.event.KeyEvent.VK_1;

public class HO {

    public static double VERSION;  // Version is set in build.gradle and exposed to HO via the manifest
	public static int RevisionNumber;
    private static String versionType;
	private static OSUtils.OS platform;
	private static boolean portable_version; // Used to determine the location of the DB
	public static String getVersionType() {
		return versionType;
	}
	public static int getRevisionNumber() {
		return RevisionNumber;
	}
	public static OSUtils.OS getPlatform() {return platform; }
	public static boolean isDevelopment() {
		return "DEV".equalsIgnoreCase(versionType);
	}
	public static boolean isBeta() {
		return "BETA".equalsIgnoreCase(versionType);
	}
	public static boolean isRelease() {
		return "RELEASE".equalsIgnoreCase(versionType);
	}

	public static String getVersionString() {
		NumberFormat nf = NumberFormat.getInstance(Locale.US);
		nf.setMinimumFractionDigits(1);
		String txt = nf.format(VERSION);

		if (isBeta()) {
			txt += " BETA (r" + RevisionNumber + ")";
		}
		else if (isDevelopment()) {
			txt += " DEV (r" + RevisionNumber + ")";
		}

		return txt;
	}

	public static boolean isPortableVersion() {
		return portable_version;
	}

	// Only used to run test
	public static void setPortable_version(boolean portable_version) {
		HO.portable_version = portable_version;
	}

	/**
	 *  HO entry point
	 */
	public static void main(String[] args) {
		portable_version = true;
		platform = OSUtils.getOS();

		if (platform == OSUtils.OS.MAC) {
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty("apple.awt.showGroupBox", "true");
			System.setProperty("apple.eawt.quitStrategy", "CLOSE_ALL_WINDOWS");
		}

		System.setProperty("sun.awt.exception.handler", ExceptionHandler.class.getName());
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());

		if (args != null) {
			args = Arrays.stream(args).map(String::toUpperCase).toArray(String[]::new);
			if(Arrays.asList(args).contains("INSTALLED")) {portable_version = false;}
			String arg;
			for (String _arg : args) {
				arg = _arg.trim().toUpperCase();
				switch (arg) {
					case "INFO" -> HOLogger.instance().setLogLevel(HOLogger.INFORMATION);
					case "DEBUG" -> HOLogger.instance().setLogLevel(HOLogger.DEBUG);
					case "WARNING" -> HOLogger.instance().setLogLevel(HOLogger.WARNING);
					case "ERROR" -> HOLogger.instance().setLogLevel(HOLogger.ERROR);
				}
			}
		}

		// Get HO version from manifest
		String sVERSION = HO.class.getPackage().getImplementationVersion();
		if (sVERSION != null) {
			String[] aVersion = sVERSION.split("\\.");

			VERSION = Double.parseDouble(aVersion[0] + "." + aVersion[1]);
			RevisionNumber = Integer.parseInt(aVersion[2]);
			switch (aVersion[3]) {
				case "0" -> versionType = "DEV";
				case "1" -> versionType = "BETA";
				default -> versionType = "RELEASE";
			}
			HOLogger.instance().info(HO.class, "VERSION: " + VERSION + "   versionType:  " + versionType + "   RevisionNumber: " + RevisionNumber );
        } else {
        	HOLogger.instance().error(HO.class, "Launched from IDE otherwise there is a bug !");
        	VERSION = 0d;
        	versionType = "DEV";
        }

		// Login selection in case of multi-users DB
		try {
			if (!UserManager.instance().isSingleUser()) {

				var options = createOptionsArray();
				var choice = JOptionPane.showOptionDialog(
						null,
						"",
						"Login",
						JOptionPane.DEFAULT_OPTION,
						JOptionPane.PLAIN_MESSAGE,
						null,
						options,
						null );

				if (choice != JOptionPane.CLOSED_OPTION) {
					UserManager.instance().setINDEX(choice);
				} else {
					System.exit(0);
				}
			}
		} catch (Exception ex) {
			HOLogger.instance().log(HO.class, ex);
		}

		// start display splash image
		final SplashFrame interruptionWindow = new SplashFrame();

		// Backup
		interruptionWindow.setInfoText(1, "Backup Database");
		BackupHelper.backup(new File(UserManager.instance().getCurrentUser().getDbFolder()));


		// Load user parameters from the DB
		interruptionWindow.setInfoText(2, "Initialize Database");
		DBManager.instance().loadUserParameter();

		String[] finalArgs = args;
		new Thread(() -> {
			RT.var("user", "dbManager", DBManager.instance());
			RT.var("user", "admin", HOVerwaltung.instance());

			IFn require = Clojure.var("clojure.core", "require");
			require.invoke(Clojure.read("nrepl.server"));
			IFn startServer = Clojure.var("nrepl.server", "start-server");
			// Can connect to HO using cider on localhost:5555
			startServer.invoke(Clojure.read("{:port 5555 :name \"HO\"}"));
		}).start();


		// init Theme
		try {
			ThemeManager.instance().setCurrentTheme();
		} catch (Exception e) {
			HOLogger.instance().log(HO.class, "Can´t load Theme:" + UserParameter.instance().theme);
			JOptionPane.showMessageDialog(null, e.getMessage(), "Can´t load Theme: "
					+ UserParameter.instance().theme, JOptionPane.WARNING_MESSAGE);
		}
		// Init!
		interruptionWindow.setInfoText(3, "Initialize Data-Administration");

		// Ask for language at first start
		if (DBManager.instance().isFirstStart()) {
			interruptionWindow.setVisible(false);
			new core.option.InitOptionsDialog();
			interruptionWindow.setVisible(true);
		}

		// Check if language file available
		interruptionWindow.setInfoText(4, "Check Language files");
		HOVerwaltung.checkLanguageFile(UserParameter.instance().sprachDatei);
		TranslationFacility.setLanguage(UserParameter.instance().sprachDatei);

		if (DBManager.instance().isFirstStart()) {
			interruptionWindow.setVisible(false);
			JOptionPane.showMessageDialog(null,
					TranslationFacility.tr("firststartup.infowinmessage"),
					TranslationFacility.tr("firststartup.infowinmessage.title"), JOptionPane.INFORMATION_MESSAGE);
			interruptionWindow.setVisible(true);
		}

		interruptionWindow.setInfoText(5, "Load latest Data");
		HOVerwaltung.instance().loadLatestHoModel();
		interruptionWindow.setInfoText(6, "Load  XtraDaten");

		// Load table columns information
		UserColumnController.instance().load();

		// Set the currency from HRF
		var model = HOVerwaltung.instance().getModel();
		if ( model != null) {
			var xtra = HOVerwaltung.instance().getModel().getXtraDaten();
			if (xtra != null) {
				float fxRate = (float) xtra.getCurrencyRate();
				if (fxRate > -1) UserParameter.instance().FXrate = fxRate;
			}
		}
		// Upgrade database configuration
		if (!DBManager.instance().isFirstStart()) {
			interruptionWindow.setInfoText(7, "Upgrade DB configuration");
			DBManager.instance().updateConfig();
		}

		initJmxSupport();

		// Training
		interruptionWindow.setInfoText(8, "Initialize Training");

		// Training estimation calculated on DB manual entries

		TrainingManager.instance();

		interruptionWindow.setInfoText(9, "Prepare to show");
		SwingUtilities.invokeLater(() -> {
			HOMainFrame.instance().setVisible(true);

			// stop display splash image
			interruptionWindow.setVisible(false);
			interruptionWindow.dispose();
		});
	}

	private static void initJmxSupport() {
		if (HO.isDevelopment()) {
			try {
				MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
				platformMBeanServer.registerMBean(
						new StatementCacheMonitor(),
						new ObjectName("io.github.ho-dev:name=StatementCacheMonitor")
				);
			} catch (MalformedObjectNameException | NotCompliantMBeanException | InstanceAlreadyExistsException |
					 MBeanRegistrationException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static Object[] createOptionsArray() {
		var buttons = new ArrayList<JButton>();
		int keyEvent = VK_1;
		for ( var user : UserManager.instance().getAllUser() ) {
			buttons.add(createIconButton(user.getTeamName(), user.getClubLogo(), keyEvent++));
		}
		return buttons.toArray();
	}

	static JButton createIconButton(String teamName, String iconPath, int keyEvent){
		//return getScaledIcon(HOIconName.NO_CLUB_LOGO, width, height);
		var width = 210;
		var height = (int)(width*26./21.);
		Icon scaledIcon;
		try {
			var buttonIcon = ImageIO.read(new File(iconPath));
			var icon = new ImageIcon(buttonIcon);
			scaledIcon = ImageUtilities.getScaledIcon(icon, width, height);
		}catch (Exception exception){
			scaledIcon = new ImageIcon();
		}
		var ret  = new JButton(teamName,scaledIcon);
		ret.setVerticalTextPosition(AbstractButton.BOTTOM);
		ret.setHorizontalTextPosition(AbstractButton.CENTER);
		ret.setMnemonic(keyEvent);

		ret.addActionListener(evt -> {
			var pane = getOptionPane((JComponent)evt.getSource());
			// set the value of the option pane
			pane.setValue(ret);
			var w = SwingUtilities.getWindowAncestor(ret);
			if (w != null) {
				w.setVisible(false);
			}
		});
		return ret;
	}

	private static JOptionPane getOptionPane(JComponent source) {
		var ret = source;
		while (ret != null &&  ! (ret instanceof JOptionPane)) {
			ret = (JComponent) ret.getParent();
		}
		return (JOptionPane) ret;
	}

}
