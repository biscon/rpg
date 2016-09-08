package dk.bison.rpg.core.character;

/**
 * Created by bison on 07-08-2016.
 */
public abstract class CharacterClass {
    public static final int BASE_AC = 10;

    public abstract String getName();
    public abstract int calcLevel(int xp);
    public abstract String getHitDice(int level);
    public abstract int getAttackBonus(int level);
    public abstract int getXPForLevel(int level);
    public abstract String getClassName();
}
