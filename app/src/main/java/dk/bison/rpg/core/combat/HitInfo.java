package dk.bison.rpg.core.combat;

/**
 * Created by bison on 19-08-2016.
 */
public class HitInfo {
    public static final int HIT = 0;
    public static final int MISS = 1;
    public static final int CRITICAL_HIT = 2;
    public static final int CRITICAL_MISS = 3;

    public int type;

    public HitInfo(int type) {
        this.type = type;
    }
}
