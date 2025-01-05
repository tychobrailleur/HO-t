package module.teamAnalyzer.vo;

import core.db.AbstractTable;
import core.file.xml.SafeInsertMap;
import core.util.AmountOfMoney;
import core.util.HODateTime;
import core.util.HOLogger;
import module.teamAnalyzer.manager.PlayerDataManager;

import static module.lineup.substitution.LanguageStringLookup.getPosition;

public class PlayerInfo extends AbstractTable.Storable {

    private int injuryLevel;
    private HODateTime lastMatchDate = null;
    private int lastMatchId;
    private int lastMatchPosition;
    private int lastMatchPlayedMinutes;
    private float lastMatchRatingEndOfGame;
    private float rating;
    //~ Instance fields ----------------------------------------------------------------------------
    String name = "";
    //~ Methods ------------------------------------------------------------------------------------
    int age;
    int experience;
    int form;
    int playerId;
    int specialEvent;
    int status;
    int injuryStatus = 0;
    int bookingStatus = 0;
    int transferListedStatus = 0;
    int tsi;
    int teamId;
    @Setter
    AmountOfMoney salary; // Money in SEK
    @Setter
    int stamina;
    boolean motherClubBonus;
    int loyalty;
    private int week;

    public PlayerInfo(SafeInsertMap i) {
        this.age = Integer.parseInt(i.get("Age"));
        this.experience = Integer.parseInt(i.get("Experience"));
        this.form = Integer.parseInt(i.get("PlayerForm"));
        this.loyalty = Integer.parseInt(i.get("Loyalty"));
        this.motherClubBonus = Boolean.parseBoolean(i.get("MotherClubBonus"));
        this.name = i.get("FirstName") + " " + i.get("LastName");
        this.playerId = Integer.parseInt(i.get("PlayerID"));
        this.salary = new AmountOfMoney(Integer.parseInt(i.get("Salary")));
        this.specialEvent = Integer.parseInt(i.get("Specialty"));
        this.stamina = Integer.parseInt(i.get("StaminaSkill"));
        this.status = 0;

        int cards = parseIntWithDefault(i.get("Cards"), 0);
        this.injuryLevel = parseIntWithDefault(i.get("InjuryLevel"), -1);

        switch (cards) {
            case 1 -> bookingStatus = PlayerDataManager.YELLOW;
            case 2 -> bookingStatus = PlayerDataManager.DOUBLE_YELLOW;
            case 3 -> bookingStatus = PlayerDataManager.SUSPENDED;
            default -> bookingStatus = 0;
        }

        switch (injuryLevel) {
            case -1 -> injuryStatus = 0;
            case 0 -> injuryStatus = PlayerDataManager.BRUISED;
            default -> injuryStatus = PlayerDataManager.INJURED;
        }

        if (parseBooleanWithDefault(i.get("TransferListed"), false)) {
            transferListedStatus = PlayerDataManager.TRANSFER_LISTED;
        } else {
            transferListedStatus = 0;
        }

        this.status = injuryStatus + 10 * bookingStatus + 100 * transferListedStatus;


        this.teamId = Integer.parseInt(i.get("TeamID"));
        this.tsi = Integer.parseInt(i.get("MarketValue"));
        this.lastMatchDate = HODateTime.fromHT(i.get("LastMatch_Date"));
        this.rating = Float.parseFloat(i.get("LastMatch_Rating"));
        this.lastMatchId = Integer.parseInt(i.get("LastMatch_id"));
        this.lastMatchPosition = Integer.parseInt(i.get("LastMatch_PositionCode"));
        this.lastMatchPlayedMinutes = Integer.parseInt(i.get("LastMatch_PlayedMinutes"));
        this.lastMatchRatingEndOfGame = Float.parseFloat(i.get("LastMatch_RatingEndOfGame"));
    }

    private int parseIntWithDefault(String s, int i) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException ignored) {
        }
        return i;
    }

    private boolean parseBooleanWithDefault(String s, boolean res) {
        try {
            return Boolean.parseBoolean(s);
        } catch (NumberFormatException e) {
            HOLogger.instance().error(this.getClass(), res + " could not be recognized as a valid boolean");
        }
        return res;
    }

    public PlayerInfo() {
    }

    public void setStatus(int i) {
        status = i;

        int digit = i % 10;
        this.injuryStatus = digit;
        i = i / 10;

        digit = i % 10;
        this.bookingStatus = digit;
        i = i / 10;

        digit = i % 10;
        this.transferListedStatus = digit;
    }

    /**
     * toString methode: creates a String representation of the object
     *
     * @return the String representation
     */
    @Override
    public String toString() {
        return getPosition(lastMatchPosition) +
                " " + name +
                ", age=" + age +
                ", experience=" + experience +
                ", form=" + form +
                ", rating=" + rating +
                ", status=" + status +
                ", motherClubBonus=" + motherClubBonus +
                ", loyalty=" + loyalty;
    }

    public boolean isTransferListed() {
        return transferListedStatus != 0;
    }

    public int getInjuryLevel() {
        return this.injuryLevel;
    }

    public HODateTime getLastMatchDate() {
        return this.lastMatchDate;
    }

    public int getLastMatchId() {
        return this.lastMatchId;
    }

    public int getLastMatchPosition() {
        return this.lastMatchPosition;
    }

    public int getLastMatchPlayedMinutes() {
        return this.lastMatchPlayedMinutes;
    }

    public float getLastMatchRatingEndOfGame() {
        return this.lastMatchRatingEndOfGame;
    }

    public float getRating() {
        return this.rating;
    }

    public String getName() {
        return this.name;
    }

    public int getAge() {
        return this.age;
    }

    public int getExperience() {
        return this.experience;
    }

    public int getForm() {
        return this.form;
    }

    public int getPlayerId() {
        return this.playerId;
    }

    public int getSpecialEvent() {
        return this.specialEvent;
    }

    public int getStatus() {
        return this.status;
    }

    public int getInjuryStatus() {
        return this.injuryStatus;
    }

    public int getBookingStatus() {
        return this.bookingStatus;
    }

    public int getTransferListedStatus() {
        return this.transferListedStatus;
    }

    public int getTsi() {
        return this.tsi;
    }

    public int getTeamId() {
        return this.teamId;
    }

    public int getSalary() {
        return this.salary;
    }

    public int getStamina() {
        return this.stamina;
    }

    public boolean isMotherClubBonus() {
        return this.motherClubBonus;
    }

    public int getLoyalty() {
        return this.loyalty;
    }

    public int getWeek() {
        return this.week;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public void setForm(int form) {
        this.form = form;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public void setSpecialEvent(int specialEvent) {
        this.specialEvent = specialEvent;
    }

    public void setTsi(int tsi) {
        this.tsi = tsi;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public void setStamina(int stamina) {
        this.stamina = stamina;
    }

    public void setMotherClubBonus(boolean motherClubBonus) {
        this.motherClubBonus = motherClubBonus;
    }

    public void setLoyalty(int loyalty) {
        this.loyalty = loyalty;
    }

    public void setWeek(int week) {
        this.week = week;
    }
}
