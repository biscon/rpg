package dk.bison.rpg.ui.encounter.combat_log;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import dk.bison.rpg.mvp.MvpEvent;

/**
 * Created by bison on 24-09-2016.
 */

public class CombatLogMessage implements MvpEvent {
    public static final int FADE = 0;
    public static final int SLIDE = 1;
    public static final int BOUNCE = 2;
    public static final int OVERSHOOT = 3;
    public static final int ROTATE = 4;
    public static final int SLIDE_SCALE_FADE = 5;

    int effect = FADE;
    SpannableStringBuilder sb;
    boolean divider = false;
    boolean roundDone = false;


    public CombatLogMessage() {
        sb = new SpannableStringBuilder();
    }

    public static CombatLogMessage create()
    {
        CombatLogMessage msg = new CombatLogMessage();
        return msg;
    }

    public CombatLogMessage roundDone()
    {
        roundDone = true;
        return this;
    }

    public CombatLogMessage divider()
    {
        divider = true;
        return this;
    }

    public CombatLogMessage effect(int fx)
    {
        effect = fx;
        return this;
    }

    public CombatLogMessage normal(String txt)
    {
        final ForegroundColorSpan normalCs = new ForegroundColorSpan(0xffe0e0e0);
        int start = 0;
        if(sb.length() > 0)
            start = sb.length();

        sb.append(txt);
        int end = start+txt.length();
        sb.setSpan(normalCs, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return this;
    }

    public CombatLogMessage bright(String txt)
    {
        final ForegroundColorSpan normalCs = new ForegroundColorSpan(0xffffffff);
        int start = 0;
        if(sb.length() > 0)
            start = sb.length();

        sb.append(txt);
        int end = start+txt.length();
        sb.setSpan(normalCs, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return this;
    }

    public CombatLogMessage dark(String txt)
    {
        final ForegroundColorSpan normalCs = new ForegroundColorSpan(0xff757575);
        int start = 0;
        if(sb.length() > 0)
            start = sb.length();

        sb.append(txt);
        int end = start+txt.length();
        sb.setSpan(normalCs, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return this;
    }

    public CombatLogMessage bold(String txt)
    {
        int start = 0;
        if(sb.length() > 0)
            start = sb.length();
        sb.append(txt);
        int end = start+txt.length();
        StyleSpan boldSp = new StyleSpan(android.graphics.Typeface.BOLD);
        sb.setSpan(boldSp, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return this;
    }

    public CombatLogMessage red(String txt)
    {
        int start = 0;
        if(sb.length() > 0)
            start = sb.length();
        sb.append(txt);
        int end = start+txt.length();
        final ForegroundColorSpan redCs = new ForegroundColorSpan(0xffFF0000);
        sb.setSpan(redCs, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return this;
    }

    public CombatLogMessage violent(String txt)
    {
        int start = 0;
        if(sb.length() > 0)
            start = sb.length();
        sb.append(txt);
        int end = start+txt.length();
        final ForegroundColorSpan redCs = new ForegroundColorSpan(0xffFF0000);
        StyleSpan boldSp = new StyleSpan(android.graphics.Typeface.BOLD);
        sb.setSpan(redCs, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        sb.setSpan(boldSp, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        sb.setSpan(new RelativeSizeSpan(1.25f), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return this;
    }

    public SpannableStringBuilder getSb()
    {
        return sb;
    }

    public int getEffect() {
        return effect;
    }

    public boolean isDivider() {
        return divider;
    }

    public boolean isRoundDone() { return roundDone; }
}
