package dk.bison.rpg.core.combat;

public class Attack
{
    public static final int MAIN_HAND = 0;
    public static final int OFF_HAND = 1;
    public static final int TWO_HAND = 2;
    public static final int MONSTER = 3;

    public String name;
    public String damage;
    public int type;
    public boolean isRanged;

    @Override
    public String toString() {
        String hand = typeToString(type);
        return "(" + name + ", " + damage + ", " + hand +", ranged=" + isRanged + ")";
    }

    public String typeToString(int type)
    {
        switch(type)
        {
            case MAIN_HAND:
                return "mainhand";
            case OFF_HAND:
                return "offhand";
            case TWO_HAND:
                return "twohand";
            case MONSTER:
                return "monster";
        }
        return "unknown";
    }
}