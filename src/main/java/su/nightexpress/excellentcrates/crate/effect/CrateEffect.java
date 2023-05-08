package su.nightexpress.excellentcrates.crate.effect;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.particle.SimpleParticle;
import su.nexmedia.engine.utils.LocationUtil;

public abstract class CrateEffect {

    protected int  step;
    protected long interval;
    protected int  duration;
    protected int  count;

    public CrateEffect(long interval, int duration) {
        this.step = 0;
        this.count = 0;
        this.interval = interval;
        this.duration = duration;
    }

    public void reset() {
        this.step = 0;
        this.count = 0;
    }

    public void addStep() {
        if (++this.step > this.getDuration()) {
            this.reset();
        }
    }

    public void step(@NotNull Location location, @NotNull SimpleParticle particle) {
        /*if (this.step < 0) {
            this.step++;
        }*/

        // Do not play an effect while paused.
        if (this.count++ % (int) this.getInterval() != 0) return;
        if (this.step < 0) return;

        this.doStep(LocationUtil.getCenter(location.clone(), false), particle, this.step);

        // Do a 0.5s pause when particle effect is finished.
        if (this.step/*++*/ >= this.getDuration()) {
            this.step = -10;
            this.count = 0;
        }
    }

    public abstract void doStep(@NotNull Location location, @NotNull SimpleParticle particle, int step);

    public final long getInterval() {
        return this.interval;
    }

    public final int getDuration() {
        return this.duration;
    }
}
