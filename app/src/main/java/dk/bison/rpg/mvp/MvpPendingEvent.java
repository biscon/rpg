package dk.bison.rpg.mvp;

/**
 * Created by bison on 29-09-2016.
 */

public class MvpPendingEvent implements MvpEvent {
    Class<?> target_cls;
    MvpEvent event;
    public final static byte DELIVER_ON_ATTACH = (byte)0b00000001;
    public final static byte DELIVER_ON_CREATE = (byte)0b00000010;
    byte flags = 0;

    public MvpPendingEvent(Class<?> target_cls, MvpEvent event, byte flags) {
        this.target_cls = target_cls;
        this.event = event;
        this.flags |= flags;
    }

    public boolean hasFlag(byte flag)
    {
        if((flag & flags) == flags)
        {
            return true;
        }
        else
            return false;
    }
}
