package tool.injury;

/**
 * The Main Injury Calculator
 *
 * @author draghetto
 */
public final class InjuryCalculator {
    // ~ Constructors
    // -------------------------------------------------------------------------------

    /**
     * Creates a new InjuryCalculator object.
     */
    private InjuryCalculator() {
    }

    // ~ Methods
    // ------------------------------------------------------------------------------------

    /**
     * Returns the estimated doctor number
     *
     * @param age     player's age
     * @param injury  actual injury level
     * @param target  desired injury level
     * @param updates number of updates
     *
     * @return The number of doctor for the player to fullfil the passed parameters
     */
    public static double getDoctorNumber(int age, double injury, double target, double updates) {
        if (age == 0) {
            return -1;
        }

        if (injury < 1) {
            return -1;
        }

        if (updates == 0) {
            return -1;
        }

        final double effInjury = injury - target;
        final double rate = (effInjury * 1.0d) / updates;
        final double mult = (rate * 10d) / getCoeff(age);
        double doc = Math.pow(mult, 1.0d / 0.2155) - 1;

        if (doc > 255) {
            doc = 255;
        }

        return doc;
    }

    /**
     * Returns the estimated update number
     *
     * @param age     player's age
     * @param injury  actual injury level
     * @param target  desired injury level
     * @param doctors number of doctors
     *
     * @return The number of update for the player to fullfil the passed parameters
     */
    public static double getUpdateNumber(int age, double injury, double target, int doctors) {
        if (age == 0) {
            return -1;
        }

        if (injury < 1) {
            return -1;
        }

        final double mult = Math.pow(doctors + 1, 0.2155);
        final double rate = getCoeff(age) / 10d * mult;
        final double effInjury = injury - target;
        return effInjury / rate;
    }

    /**
     * Returns the exact update number
     *
     * @param tsiPre  TSI Before injury
     * @param tsiPost TSI After injury, but before a training
     * @param target  desired injury level
     * @param tsi     TSI gained last update
     *
     * @return The number of update for the player to fullfil the passed parameters
     */
    public static double getUpdateTSINumber(double tsiPre, double tsiPost, double target, double tsi) {
        if (tsiPre == 0) {
            return -1;
        }

        if (tsiPost == 0) {
            return -1;
        }

        if (tsiPre < tsiPost) {
            return -1;
        }

        if (tsi == 0) {
            return -1;
        }

        double injury = (tsiPre - tsiPost) / tsiPre * 10.0d;
        injury = injury - target;

        final double toRecover = tsiPre * (injury / 10);
        return toRecover / tsi;
    }

    /**
     * Returns the Healing Rate
     *
     * @param age player's age
     *
     * @return the healing rate for the player
     */
    private static double getCoeff(int age) {
        return switch (age) {
            case 17 -> 1.825;
            case 18 -> 1.741;
            case 19 -> 1.657;
            case 20 -> 1.573;
            case 21 -> 1.489;
            case 22 -> 1.405;
            case 23 -> 1.321;
            case 24 -> 1.237;
            case 25 -> 1.153;
            case 26 -> 1.069;
            case 27 -> 0.985;
            case 28 -> 0.901;
            case 29 -> 0.817;
            case 30 -> 0.802769867;
            case 31 -> 0.72385681;
            case 32 -> 0.647619991;
            case 33 -> 0.584762756;
            case 34 -> 0.494985902;
            case 35 -> 0.423019866;
            case 36 -> 0.357160486;
            case 37 -> 0.281613792;
            case 38 -> 0.205675176;
            case 39 -> 0.138711165;
            case 40 -> 0.079527735;
            // 41 and default
            default -> 0.018074485;
        };
    }
}
