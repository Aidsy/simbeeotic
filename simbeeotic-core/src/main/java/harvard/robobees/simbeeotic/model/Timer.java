package harvard.robobees.simbeeotic.model;


import harvard.robobees.simbeeotic.SimEngine;
import harvard.robobees.simbeeotic.SimTime;

import java.util.concurrent.TimeUnit;


/**
 * An abstraction around the underlying discrete event mechanism that
 * allows modelers to schedule future (possibly recurring) events.
 *
 * @author bkate
 */
public final class Timer {

    private TimerCallback callback;

    private SimTime lastFired = null;
    private SimTime nextFiringTime = null;
    private long period = 0;  // nanoseconds
    private boolean canceled = false;

    private int modelId;
    private SimEngine simEngine;
    private long nextEvent;


    /**
     * Establishes a new Timer with the given parameters.
     *
     * @param modelId The model upon which an event will be scheduled to trigger the timer.
     * @param engine The discrete event sime engine managing the model.
     * @param callback The method to be called when the timer fires.
     * @param start The first firing time. Must be at or later than the time of the current event being executed.
     * @param period The period at which this timer fires. A value less than or equal to zero indicates no recurrance.
     * @param periodUnit The time unit in which the period is measured.
     */
    public Timer(int modelId, SimEngine engine, TimerCallback callback, SimTime start, long period, TimeUnit periodUnit) {

        this.modelId = modelId;
        this.simEngine = engine;
        this.callback = callback;

        if (period > 0) {
            this.period = periodUnit.toNanos(period);
        }

        scheduleNextFiring(start);
    }


    /**
     * Fires the timer. The custom callback is made and the timer is reset (if it is periodic).
     *
     * @param time The time at which the timer is firing (now).
     */
    public void fire(SimTime time) {

        nextEvent = 0;

        callback.fire(time);

        lastFired = time;

        if ((period > 0) && !canceled) {
            scheduleNextFiring(new SimTime(time, period, TimeUnit.NANOSECONDS));
        }
    }


    /**
     * Resets the timer by canceling the next triggered firing and starting over with a new
     * schedule.
     *
     * @param now The current time.
     * @param offset The offset from the current time, representing the first time
     *               this timer will fire. Must be greater than or equal to zero.
     * @param offsetUnit The time unit in which the offset is measured.
     * @param period The period at which this timer fires. Values less than or equal to
     *               zero indicate no periodicity (a one-shot timer).
     * @param periodUnit The time unit in which the period is measured.
     */
    public void reset(SimTime now, long offset, TimeUnit offsetUnit, long period, TimeUnit periodUnit) {

        canceled = false;

        if (period > 0) {
            this.period = periodUnit.toNanos(period);
        }

        if (offset < 0) {
            throw new RuntimeModelingException("The timer offset cannot be less than 0 - you would go back in time!");
        }

        scheduleNextFiring(new SimTime(now, offset, offsetUnit));
    }


    /**
     * Stops the timer by canceling the next firing. The Timer may be reset if it is to be reactivated in the future.
     */
    public void cancel() {

        if (nextEvent > 0) {

            simEngine.cancelEvent(nextEvent);
            nextEvent = 0;
        }

        nextFiringTime = null;
        canceled = true;
    }


    private void scheduleNextFiring(SimTime next) {

        cancel();

        canceled = false;
        nextFiringTime = next;
        nextEvent = simEngine.scheduleEvent(modelId, next, new TimerEvent(this));
    }


    /**
     * Gets the last time that this timer fired.
     *
     * @return The last firing time, or a value less than zero if it has never fired.
     */
    public SimTime getLastFiredTime() {
        return lastFired;
    }


    /**
     * Gets the next time this timer will fire.
     *
     * @return The next firing time, or a value less than zero if the timers is not scheduled to fire.
     */
    public SimTime getNextFiringTime() {
        return nextFiringTime;
    }


    /**
     * Gets the period at which this timer fires.
     *
     * @return The timer period (in nanoseconds).
     */
    public long getPeriod() {
        return period;
    }
}
