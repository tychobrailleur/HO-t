package module.statistics;

import core.constants.player.PlayerAbility;
import core.datatype.CBItem;
import core.db.DBManager;
import core.gui.HOMainFrame;
import core.gui.comp.ImageCheckbox;
import core.gui.comp.panel.ImagePanel;
import core.gui.comp.panel.LazyImagePanel;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.TranslationFacility;
import core.model.UserParameter;
import core.model.match.MatchLineupPosition;
import core.model.match.Matchdetails;
import core.model.player.IMatchRoleID;
import core.util.HODateTime;
import core.util.HOLogger;
import core.util.Helper;
import core.util.chart.HOLinesChart;
import core.util.chart.LinesChartDataModel;
import module.matches.MatchLocation;
import module.matches.MatchesPanel;
import org.knowm.xchart.style.lines.SeriesLines;
import org.knowm.xchart.style.markers.SeriesMarkers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Panel Matches in Module Statistics
 */
public class MatchesStatisticsPanel extends LazyImagePanel {

	private ImageCheckbox c_jcbCentralDefence;
	private ImageCheckbox c_jcbCentralAttack;
	private ImageCheckbox c_jcbRating;
	private ImageCheckbox c_jcbTotalStrength;
	private ImageCheckbox c_jcbLeftDefence;
	private ImageCheckbox c_jcbLeftAttack;
	private ImageCheckbox c_jcbMidfield;
	private ImageCheckbox c_jcbRightDefence;
	private ImageCheckbox c_jcbRightAttack;
	private ImageCheckbox c_jcbConfidence;
	private ImageCheckbox c_jcbTeamSpirit;
	private ImageCheckbox c_jcbHatStats;
	private ImageCheckbox c_jcbLoddarStats;
	private JButton c_jbApply;
	private JCheckBox c_jcbHelpLines;
	private JCheckBox jcbInscribe;

	private JComboBox c_jcbMatchesFilter;
	private JTextField c_jtfNumberHRF;
	private HOLinesChart c_jpChart;

	private final String sSum = "Σ ";
	private final String sAvg = "Ø ";

	private final String avgRating = sAvg + TranslationFacility.tr("Rating");
	private final String sumStars = sSum + TranslationFacility.tr("RecapPanel.Stars");

	@Override
	protected void initialize() {
		initComponents();
		addListeners();
		initStatistik();
		setNeedsRefresh(false);
		registerRefreshable(true);
	}

	@Override
	protected void update() {
		initStatistik();
	}

	private void addListeners() {

		ActionListener checkBoxActionListener = e -> {
			if (e.getSource() == c_jcbHelpLines) {
				c_jpChart.setHelpLines(c_jcbHelpLines.isSelected());
				UserParameter.instance().statistikSpielerFinanzenHilfslinien = c_jcbHelpLines.isSelected();
			} else if (e.getSource() == jcbInscribe) {
				c_jpChart.setLabelling(jcbInscribe.isSelected());
				UserParameter.instance().statistikAlleBeschriftung = jcbInscribe.isSelected();
			} else if (e.getSource() == c_jcbRating.getCheckbox()) {
				c_jpChart.setShow(sumStars, c_jcbRating.isSelected());
				UserParameter.instance().statistikSpieleBewertung = c_jcbRating.isSelected();
			} else if (e.getSource() == c_jcbTotalStrength.getCheckbox()) {
				c_jpChart.setShow("Gesamtstaerke", c_jcbTotalStrength.isSelected());
				UserParameter.instance().statistikSpieleGesamt = c_jcbTotalStrength.isSelected();
			} else if (e.getSource() == c_jcbMidfield.getCheckbox()) {
				c_jpChart.setShow("ls.match.ratingsector.midfield",
						c_jcbMidfield.isSelected());
				UserParameter.instance().statistikSpieleMittelfeld = c_jcbMidfield
						.isSelected();
			} else if (e.getSource() == c_jcbRightDefence.getCheckbox()) {
				c_jpChart.setShow("ls.match.ratingsector.rightdefence",
						c_jcbRightDefence.isSelected());
				UserParameter.instance().statistikSpieleRechteAbwehr = c_jcbRightDefence
						.isSelected();
			} else if (e.getSource() == c_jcbCentralDefence.getCheckbox()) {
				c_jpChart.setShow("ls.match.ratingsector.centraldefence",
						c_jcbCentralDefence.isSelected());
				UserParameter.instance().statistikSpieleAbwehrzentrum = c_jcbCentralDefence
						.isSelected();
			} else if (e.getSource() == c_jcbLeftDefence.getCheckbox()) {
				c_jpChart.setShow("ls.match.ratingsector.leftdefence",
						c_jcbLeftDefence.isSelected());
				UserParameter.instance().statistikSpieleLinkeAbwehr = c_jcbLeftDefence
						.isSelected();
			} else if (e.getSource() == c_jcbRightAttack.getCheckbox()) {
				c_jpChart.setShow("ls.match.ratingsector.rightattack",
						c_jcbRightAttack.isSelected());
				UserParameter.instance().statistikSpieleRechterAngriff = c_jcbRightAttack
						.isSelected();
			} else if (e.getSource() == c_jcbCentralAttack.getCheckbox()) {
				c_jpChart.setShow("ls.match.ratingsector.centralattack",
						c_jcbCentralAttack.isSelected());
				UserParameter.instance().statistikSpieleAngriffszentrum = c_jcbCentralAttack
						.isSelected();
			} else if (e.getSource() == c_jcbLeftAttack.getCheckbox()) {
				c_jpChart.setShow("ls.match.ratingsector.leftattack",
						c_jcbLeftAttack.isSelected());
				UserParameter.instance().statistikSpieleLinkerAngriff = c_jcbLeftAttack
						.isSelected();
			} else if (e.getSource() == c_jcbTeamSpirit.getCheckbox()) {
				c_jpChart.setShow("ls.team.teamspirit", c_jcbTeamSpirit.isSelected());
				UserParameter.instance().statistikSpieleStimmung = c_jcbTeamSpirit.isSelected();
			} else if (e.getSource() == c_jcbHatStats.getCheckbox()) {
				c_jpChart.setShow("ls.match.ratingtype.hatstats",
						c_jcbHatStats.isSelected());
				UserParameter.instance().statistikSpieleHatStats = c_jcbHatStats.isSelected();
			} else if (e.getSource() == c_jcbLoddarStats.getCheckbox()) {
				c_jpChart.setShow("ls.match.ratingtype.loddarstats",
						c_jcbLoddarStats.isSelected());
				UserParameter.instance().statistikSpieleLoddarStats = c_jcbLoddarStats
						.isSelected();
			} else if (e.getSource() == c_jcbConfidence.getCheckbox()) {
				c_jpChart.setShow("ls.team.confidence",
						c_jcbConfidence.isSelected());
				UserParameter.instance().statistikSpieleSelbstvertrauen = c_jcbConfidence
						.isSelected();
			}
		};
		c_jcbHelpLines.addActionListener(checkBoxActionListener);
		jcbInscribe.addActionListener(checkBoxActionListener);
		c_jcbRating.addActionListener(checkBoxActionListener);
		c_jcbHatStats.addActionListener(checkBoxActionListener);
		c_jcbLoddarStats.addActionListener(checkBoxActionListener);
		c_jcbTotalStrength.addActionListener(checkBoxActionListener);
		c_jcbMidfield.addActionListener(checkBoxActionListener);
		c_jcbRightDefence.addActionListener(checkBoxActionListener);
		c_jcbCentralDefence.addActionListener(checkBoxActionListener);
		c_jcbLeftDefence.addActionListener(checkBoxActionListener);
		c_jcbRightAttack.addActionListener(checkBoxActionListener);
		c_jcbCentralAttack.addActionListener(checkBoxActionListener);
		c_jcbLeftAttack.addActionListener(checkBoxActionListener);
		c_jcbTeamSpirit.addActionListener(checkBoxActionListener);
		c_jcbConfidence.addActionListener(checkBoxActionListener);

		c_jbApply.addActionListener(e -> initStatistik());

		c_jtfNumberHRF.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent focusEvent) {
				Helper.parseInt(HOMainFrame.instance(), c_jtfNumberHRF, false);
			}
		});

		c_jcbMatchesFilter.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				initStatistik();
			}
		});
	}

	private void initComponents() {
		JLabel label;

		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		constraints.insets = new Insets(2, 0, 2, 0);

		setLayout(layout);

		JPanel panel2 = new ImagePanel();
		GridBagLayout layout2 = new GridBagLayout();
		GridBagConstraints constraints2 = new GridBagConstraints();
		constraints2.fill = GridBagConstraints.HORIZONTAL;
		constraints2.weightx = 0.0;
		constraints2.weighty = 0.0;
		constraints2.insets = new Insets(2, 2, 2, 2);

		panel2.setLayout(layout2);

		label = new JLabel(getLangStr("Wochen"));
		constraints2.fill = GridBagConstraints.HORIZONTAL;
		constraints2.anchor = GridBagConstraints.WEST;
		constraints2.insets = new Insets(10, 0, 0, 0);  //top padding
		constraints2.gridx = 0;
		constraints2.gridy = 0;
		constraints2.gridwidth = 1;
		layout2.setConstraints(label, constraints2);
		panel2.add(label);

		c_jtfNumberHRF = new JTextField(
				String.valueOf(UserParameter.instance().statistikSpielerFinanzenAnzahlHRF), 3);
		c_jtfNumberHRF.setHorizontalAlignment(SwingConstants.RIGHT);
		constraints2.gridx = 1;
		constraints2.insets = new Insets(10, 5, 0, 0);  //top padding
		layout2.setConstraints(c_jtfNumberHRF, constraints2);
		panel2.add(c_jtfNumberHRF);

		constraints2.gridx = 2;
		constraints2.insets = new Insets(10, 20, 0, 0);  //top padding
		c_jbApply = new JButton(getLangStr("ls.button.apply"));
		layout2.setConstraints(c_jbApply, constraints2);
		c_jbApply.setToolTipText(getLangStr("tt_Statistik_HRFAnzahluebernehmen"));
		panel2.add(c_jbApply);

		constraints2.gridx = 0;
		constraints2.gridy = 1;
		constraints2.gridwidth = 3;
		constraints2.insets = new Insets(20, 0, 0, 0);  //top padding
		c_jcbMatchesFilter = new JComboBox(getMatchFilterItems());
		Helper.setComboBoxFromID(c_jcbMatchesFilter, UserParameter.instance().statistikSpieleFilter);
		c_jcbMatchesFilter.setFont(c_jcbMatchesFilter.getFont().deriveFont(Font.BOLD));
		layout2.setConstraints(c_jcbMatchesFilter, constraints2);
		panel2.add(c_jcbMatchesFilter);


		constraints2.gridy++;
		c_jcbHelpLines = new JCheckBox(getLangStr("Hilflinien"),
				UserParameter.instance().statistikSpielerFinanzenHilfslinien);
		c_jcbHelpLines.setOpaque(false);
		c_jcbHelpLines.setBackground(Color.white);
		layout2.setConstraints(c_jcbHelpLines, constraints2);
		panel2.add(c_jcbHelpLines);

		constraints2.gridy++;
		jcbInscribe = new JCheckBox(getLangStr("Beschriftung"), UserParameter.instance().statistikAlleBeschriftung);
		jcbInscribe.setOpaque(false);
		jcbInscribe.setBackground(Color.white);
		layout2.setConstraints(jcbInscribe, constraints2);
		panel2.add(jcbInscribe);

		constraints2.gridy++;
		c_jcbTotalStrength = new ImageCheckbox(avgRating, Colors.getColor(Colors.COLOR_TEAM_TOTAL_STRENGTH),
				UserParameter.instance().statistikSpieleGesamt);
		c_jcbTotalStrength.setOpaque(false);
		layout2.setConstraints(c_jcbTotalStrength, constraints2);
		panel2.add(c_jcbTotalStrength);

		constraints2.gridy++;
		constraints2.insets = new Insets(0, 0, 0, 0);  //top padding
		c_jcbMidfield = new ImageCheckbox(getLangStr("ls.match.ratingsector.midfield"),
				Colors.getColor(Colors.COLOR_TEAM_MID),
				UserParameter.instance().statistikSpieleMittelfeld);
		c_jcbMidfield.setOpaque(false);
		layout2.setConstraints(c_jcbMidfield, constraints2);
		panel2.add(c_jcbMidfield);

		constraints2.gridy++;
		c_jcbRightDefence = new ImageCheckbox(getLangStr("ls.match.ratingsector.rightdefence"),
				Colors.getColor(Colors.COLOR_TEAM_RD),
				UserParameter.instance().statistikSpieleRechteAbwehr);
		c_jcbRightDefence.setOpaque(false);
		layout2.setConstraints(c_jcbRightDefence, constraints2);
		panel2.add(c_jcbRightDefence);

		constraints2.gridy++;
		c_jcbCentralDefence = new ImageCheckbox(getLangStr("ls.match.ratingsector.centraldefence"),
				Colors.getColor(Colors.COLOR_TEAM_CD),
				UserParameter.instance().statistikSpieleAbwehrzentrum);
		c_jcbCentralDefence.setOpaque(false);
		layout2.setConstraints(c_jcbCentralDefence, constraints2);
		panel2.add(c_jcbCentralDefence);

		constraints2.gridy++;
		c_jcbLeftDefence = new ImageCheckbox(getLangStr("ls.match.ratingsector.leftdefence"),
				Colors.getColor(Colors.COLOR_TEAM_LD),
				UserParameter.instance().statistikSpieleLinkeAbwehr);
		c_jcbLeftDefence.setOpaque(false);
		layout2.setConstraints(c_jcbLeftDefence, constraints2);
		panel2.add(c_jcbLeftDefence);

		constraints2.gridy++;
		c_jcbRightAttack = new ImageCheckbox(getLangStr("ls.match.ratingsector.rightattack"),
				Colors.getColor(Colors.COLOR_TEAM_RA),
				UserParameter.instance().statistikSpieleRechterAngriff);
		c_jcbRightAttack.setOpaque(false);
		layout2.setConstraints(c_jcbRightAttack, constraints2);
		panel2.add(c_jcbRightAttack);

		constraints2.gridy++;
		c_jcbCentralAttack = new ImageCheckbox(getLangStr("ls.match.ratingsector.centralattack"),
				Colors.getColor(Colors.COLOR_TEAM_CA),
				UserParameter.instance().statistikSpieleAngriffszentrum);
		c_jcbCentralAttack.setOpaque(false);
		layout2.setConstraints(c_jcbCentralAttack, constraints2);
		panel2.add(c_jcbCentralAttack);

		constraints2.gridy++;
		c_jcbLeftAttack = new ImageCheckbox(getLangStr("ls.match.ratingsector.leftattack"),
				Colors.getColor(Colors.COLOR_TEAM_LA),
				UserParameter.instance().statistikSpieleLinkerAngriff);
		c_jcbLeftAttack.setOpaque(false);
		layout2.setConstraints(c_jcbLeftAttack, constraints2);
		panel2.add(c_jcbLeftAttack);

		constraints2.gridy++;
		c_jcbTeamSpirit = new ImageCheckbox(getLangStr("ls.team.teamspirit"),
				Colors.getColor(Colors.COLOR_TEAM_TS),
				UserParameter.instance().statistikSpieleStimmung);
		c_jcbTeamSpirit.setOpaque(false);
		layout2.setConstraints(c_jcbTeamSpirit, constraints2);
		panel2.add(c_jcbTeamSpirit);

		constraints2.gridy++;
		c_jcbConfidence = new ImageCheckbox(getLangStr("ls.team.confidence"),
				Colors.getColor(Colors.COLOR_TEAM_CONFIDENCE),
				UserParameter.instance().statistikSpieleSelbstvertrauen);
		c_jcbConfidence.setOpaque(false);
		layout2.setConstraints(c_jcbConfidence, constraints2);
		panel2.add(c_jcbConfidence);

		constraints2.gridy++;
		constraints2.insets = new Insets(25, 0, 0, 0);  //top padding
		String textLabel = sumStars + " (" + getLangStr("ls.chart.second_axis") + ")";
		c_jcbRating = new ImageCheckbox(textLabel, Colors.getColor(Colors.COLOR_TEAM_RATING),
				UserParameter.instance().statistikSpieleBewertung);
		c_jcbRating.setOpaque(false);
		layout2.setConstraints(c_jcbRating, constraints2);
		panel2.add(c_jcbRating);

		constraints2.gridy++;
		constraints2.insets = new Insets(0, 0, 0, 0);  //top padding
		textLabel = getLangStr("ls.match.ratingtype.hatstats") + " (" + getLangStr("ls.chart.second_axis") + ")";
		c_jcbHatStats = new ImageCheckbox(textLabel, Colors.getColor(Colors.COLOR_TEAM_HATSTATS), UserParameter.instance().statistikSpieleHatStats);
		c_jcbHatStats.setOpaque(false);
		layout2.setConstraints(c_jcbHatStats, constraints2);
		panel2.add(c_jcbHatStats);

		constraints2.gridy++;
		textLabel = getLangStr("ls.match.ratingtype.loddarstats") + " (" + getLangStr("ls.chart.second_axis") + ")";
		c_jcbLoddarStats = new ImageCheckbox(textLabel, Colors.getColor(Colors.COLOR_TEAM_LODDAR), UserParameter.instance().statistikSpieleLoddarStats);
		c_jcbLoddarStats.setOpaque(false);
		layout2.setConstraints(c_jcbLoddarStats, constraints2);
		panel2.add(c_jcbLoddarStats);

		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.weightx = 0.01;
		constraints.weighty = 0.001;
		constraints.anchor = GridBagConstraints.NORTH;
		layout.setConstraints(panel2, constraints);
		add(panel2);

		// Table
		JPanel panel = new ImagePanel();
		panel.setLayout(new BorderLayout());

		c_jpChart = new HOLinesChart(true, null, null, null, "#,##0", 0d, 20d);
		panel.add(c_jpChart.getPanel());

		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.weighty = 1.0;
		constraints.weightx = 1.0;
		constraints.anchor = GridBagConstraints.NORTH;
		panel.setBorder(BorderFactory.createLineBorder(ThemeManager.getColor(HOColorName.PANEL_BORDER)));
		layout.setConstraints(panel, constraints);
		add(panel);
	}

	private void initStatistik() {
		try {
			int anzahlHRF = Integer.parseInt(c_jtfNumberHRF.getText());

			if (anzahlHRF <= 0) {
				anzahlHRF = 1;
			}

			UserParameter.instance().statistikSpielerFinanzenAnzahlHRF = anzahlHRF;
			var selectedItem = (CBItem) c_jcbMatchesFilter.getSelectedItem();
			if (selectedItem != null) {
				UserParameter.instance().statistikSpieleFilter = selectedItem.getId();
			}

			var from = HODateTime.now().minus(anzahlHRF*7, ChronoUnit.DAYS);
			var matchkurzinfos = DBManager.instance().getMatchesKurzInfo(
					HOVerwaltung.instance().getModel().getBasics().getTeamId(),
					selectedItem.getId(), MatchLocation.ALL, from.toDbTimestamp(), false);

			int anzahl = matchkurzinfos.size();
			int teamid = HOVerwaltung.instance().getModel().getBasics().getTeamId();

			double[][] statistikWerte = new double[14][anzahl];

			// Infos zusammenstellen
			int i= -1;
			for (var match : matchkurzinfos) {
				i++;
//				var match = matchkurzinfos[i];
				Matchdetails details = match.getMatchdetails();

				int bewertungwert;
				// Für match
				int sublevel;

				// Für gesamtstärke
				double temp;

				if (details.getHomeTeamId() == teamid) {
					sublevel = calcSublevel(details.getHomeMidfield());

					bewertungwert = ((details.getHomeMidfield() - 1) / 4) + 1;
					statistikWerte[1][i] = bewertungwert
							+ PlayerAbility.getValue4Sublevel(sublevel);
					sublevel = calcSublevel(details.getHomeRightDef());

					bewertungwert = ((details.getHomeRightDef() - 1) / 4) + 1;
					statistikWerte[2][i] = bewertungwert
							+ PlayerAbility.getValue4Sublevel(sublevel);
					sublevel = calcSublevel(details.getHomeMidDef());

					bewertungwert = ((details.getHomeMidDef() - 1) / 4) + 1;
					statistikWerte[3][i] = bewertungwert
							+ PlayerAbility.getValue4Sublevel(sublevel);
					sublevel = calcSublevel(details.getHomeLeftDef());

					bewertungwert = ((details.getHomeLeftDef() - 1) / 4) + 1;
					statistikWerte[4][i] = bewertungwert
							+ PlayerAbility.getValue4Sublevel(sublevel);
					sublevel = calcSublevel(details.getHomeRightAtt());

					bewertungwert = ((details.getHomeRightAtt() - 1) / 4) + 1;
					statistikWerte[5][i] = bewertungwert
							+ PlayerAbility.getValue4Sublevel(sublevel);
					sublevel = calcSublevel(details.getHomeMidAtt());

					bewertungwert = ((details.getHomeMidAtt() - 1) / 4) + 1;
					statistikWerte[6][i] = bewertungwert
							+ PlayerAbility.getValue4Sublevel(sublevel);
					sublevel = calcSublevel(details.getHomeLeftAtt());

					bewertungwert = ((details.getHomeLeftAtt() - 1) / 4) + 1;
					statistikWerte[7][i] = bewertungwert
							+ PlayerAbility.getValue4Sublevel(sublevel);
					temp = details.getHomeGesamtstaerke(false);
					sublevel = calcSublevel((int) temp);
					statistikWerte[8][i] = (((int) temp - 1) / 4) + 1
							+ PlayerAbility.getValue4Sublevel(sublevel);
					statistikWerte[11][i] = details.getHomeHatStats();
					// Calculate and return the LoddarStats rating
					statistikWerte[12][i] = details.getHomeLoddarStats();
				} else {
					sublevel = calcSublevel(details.getGuestMidfield());

					bewertungwert = ((details.getGuestMidfield() - 1) / 4) + 1;
					statistikWerte[1][i] = bewertungwert
							+ PlayerAbility.getValue4Sublevel(sublevel);
					sublevel = calcSublevel(details.getGuestRightDef());

					bewertungwert = ((details.getGuestRightDef() - 1) / 4) + 1;
					statistikWerte[2][i] = bewertungwert
							+ PlayerAbility.getValue4Sublevel(sublevel);
					sublevel = calcSublevel(details.getGuestMidDef());

					bewertungwert = ((details.getGuestMidDef() - 1) / 4) + 1;
					statistikWerte[3][i] = bewertungwert
							+ PlayerAbility.getValue4Sublevel(sublevel);
					sublevel = calcSublevel(details.getGuestLeftDef());

					bewertungwert = ((details.getGuestLeftDef() - 1) / 4) + 1;
					statistikWerte[4][i] = bewertungwert
							+ PlayerAbility.getValue4Sublevel(sublevel);
					sublevel = calcSublevel(details.getGuestRightAtt());

					bewertungwert = ((details.getGuestRightAtt() - 1) / 4) + 1;
					statistikWerte[5][i] = bewertungwert
							+ PlayerAbility.getValue4Sublevel(sublevel);
					sublevel = calcSublevel(details.getGuestMidAtt());

					bewertungwert = ((details.getGuestMidAtt() - 1) / 4) + 1;
					statistikWerte[6][i] = bewertungwert
							+ PlayerAbility.getValue4Sublevel(sublevel);
					sublevel = calcSublevel(details.getGuestLeftAtt());

					bewertungwert = ((details.getGuestLeftAtt() - 1) / 4) + 1;
					statistikWerte[7][i] = bewertungwert
							+ PlayerAbility.getValue4Sublevel(sublevel);
					temp = details.getGuestGesamtstaerke(false);
					sublevel = calcSublevel((int) temp);
					statistikWerte[8][i] = (((int) temp - 1) / 4) + 1
							+ PlayerAbility.getValue4Sublevel(sublevel);
					statistikWerte[11][i] = details.getAwayHatStats();
					// Calculate and return the LoddarStats rating
					statistikWerte[12][i] = details.getAwayLoddarStats();
				}

				// Stimmung, Selbstvertrauen
				var matchDate = match.getMatchSchedule();
				var matchDateTimestamp = matchDate.toDbTimestamp();
				int hrfid = DBManager.instance().getHRFID4Date(matchDateTimestamp);
				var teamInfo = DBManager.instance().getTeam(hrfid);
				int[] stimmungSelbstvertrauen  = {
						teamInfo.getTeamSpiritLevel(),
						teamInfo.getConfidence()
				};

				statistikWerte[9][i] = stimmungSelbstvertrauen[0];
				statistikWerte[10][i] = stimmungSelbstvertrauen[1];

				statistikWerte[13][i] = matchDateTimestamp.getTime();

				List<MatchLineupPosition> team = DBManager.instance().getMatchLineupPlayers(match.getMatchID(), match.getMatchType(), teamid);
				float sterne = 0;

				// Sterne
				for (final MatchLineupPosition player : team) {
					if (player.getRoleId() < IMatchRoleID.startReserves
							&& player.getRoleId() >= IMatchRoleID.startLineup) {
						float rating = (float) player.getRating();

						if (rating > 0) {
							sterne += rating;
						}
					}
				}

				statistikWerte[0][i] = sterne;
			}

			LinesChartDataModel[] models = new LinesChartDataModel[statistikWerte.length];

			models[0] = new LinesChartDataModel(statistikWerte[0], sumStars, c_jcbRating.isSelected(),
					Colors.getColor(Colors.COLOR_TEAM_RATING), SeriesLines.DASH_DASH, SeriesMarkers.DIAMOND, 0d, true);
			models[1] = new LinesChartDataModel(statistikWerte[1], "ls.match.ratingsector.midfield", c_jcbMidfield.isSelected(),
					Colors.getColor(Colors.COLOR_TEAM_MID));
			models[2] = new LinesChartDataModel(statistikWerte[2], "ls.match.ratingsector.rightdefence", c_jcbRightDefence.isSelected(),
					Colors.getColor(Colors.COLOR_TEAM_RD));
			models[3] = new LinesChartDataModel(statistikWerte[3], "ls.match.ratingsector.centraldefence", c_jcbCentralDefence.isSelected(),
					Colors.getColor(Colors.COLOR_TEAM_CD));
			models[4] = new LinesChartDataModel(statistikWerte[4], "ls.match.ratingsector.leftdefence", c_jcbRightDefence.isSelected(),
					Colors.getColor(Colors.COLOR_TEAM_LD));
			models[5] = new LinesChartDataModel(statistikWerte[5], "ls.match.ratingsector.rightattack", c_jcbRightAttack.isSelected(),
					Colors.getColor(Colors.COLOR_TEAM_RA));
			models[6] = new LinesChartDataModel(statistikWerte[6], "ls.match.ratingsector.centralattack", c_jcbCentralAttack.isSelected(),
					Colors.getColor(Colors.COLOR_TEAM_CA));
			models[7] = new LinesChartDataModel(statistikWerte[7], "ls.match.ratingsector.leftattack", c_jcbLeftAttack.isSelected(),
					Colors.getColor(Colors.COLOR_TEAM_LA));
			models[8] = new LinesChartDataModel(statistikWerte[8], "Gesamtstaerke", c_jcbTotalStrength.isSelected(),
					Colors.getColor(Colors.COLOR_TEAM_TOTAL_STRENGTH));
			models[9] = new LinesChartDataModel(statistikWerte[9], "ls.team.teamspirit", c_jcbTeamSpirit.isSelected(),
					Colors.getColor(Colors.COLOR_TEAM_TS));
			models[10] = new LinesChartDataModel(statistikWerte[10], "ls.team.confidence", c_jcbConfidence.isSelected(),
					Colors.getColor(Colors.COLOR_TEAM_CONFIDENCE));
			models[11] = new LinesChartDataModel(statistikWerte[11], "ls.match.ratingtype.hatstats",
					c_jcbHatStats.isSelected(), Colors.getColor(Colors.COLOR_TEAM_HATSTATS), SeriesLines.DASH_DASH, SeriesMarkers.DIAMOND, 0d, true);
			models[12] = new LinesChartDataModel(statistikWerte[12], "ls.match.ratingtype.loddarstats", c_jcbLoddarStats.isSelected(),
					Colors.getColor(Colors.COLOR_TEAM_LODDAR), SeriesLines.DASH_DASH, SeriesMarkers.DIAMOND, 0d, true);

			c_jpChart.setAllValues(models, statistikWerte[13], Helper.DEFAULTDEZIMALFORMAT,
					getLangStr("Spiele"), null, jcbInscribe.isSelected(), c_jcbHelpLines.isSelected());

		} catch (Exception e) {
			HOLogger.instance().log(getClass(), e);
		}
	}

	private int calcSublevel(int rating) {
		return (rating - 1) % 4;
	}

	private CBItem[] getMatchFilterItems() {
		return new CBItem[]{
				new CBItem(Helper.getTranslation("AlleSpiele"), MatchesPanel.ALL_GAMES),
				new CBItem(Helper.getTranslation("NurEigeneSpiele"), MatchesPanel.OWN_GAMES),
				new CBItem(Helper.getTranslation("NurEigenePflichtspiele"), MatchesPanel.OWN_OFFICIAL_GAMES),
				new CBItem(Helper.getTranslation("AllCupMatches"), MatchesPanel.OWN_CUP_GAMES),
				new CBItem(Helper.getTranslation("NurEigenePokalspiele"), MatchesPanel.OWN_NATIONAL_CUP_GAMES),
				new CBItem(Helper.getTranslation("OnlySecondaryCup"), MatchesPanel.OWN_SECONDARY_CUP_GAMES),
				new CBItem(Helper.getTranslation("NurEigeneLigaspiele"), MatchesPanel.OWN_LEAGUE_GAMES),
				new CBItem(Helper.getTranslation("OnlyQualificationMatches"), MatchesPanel.OWN_QUALIF_GAMES),
				new CBItem(Helper.getTranslation("NurEigeneFreundschaftsspiele"), MatchesPanel.OWN_FRIENDLY_GAMES),
				new CBItem(Helper.getTranslation("NurEigeneTournamentsspiele"), MatchesPanel.OWN_TOURNAMENT_GAMES),
				new CBItem(Helper.getTranslation("NurFremdeSpiele"), MatchesPanel.OTHER_TEAM_GAMES)
		};
	}

	private String getLangStr(String key) {
		return TranslationFacility.tr(key);
	}

}