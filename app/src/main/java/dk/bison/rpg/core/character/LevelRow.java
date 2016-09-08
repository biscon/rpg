package dk.bison.rpg.core.character;

public class LevelRow
    {
        public int level;
        public int minXP;
        public int maxXP;
        public String hitDice;

        public LevelRow(int level, int minXP, int maxXP, String hitDice) {
            this.level = level;
            this.minXP = minXP;
            this.maxXP = maxXP;
            this.hitDice = hitDice;
        }
    }