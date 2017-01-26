/*
 * This file is part of Hakbot Origin Controller.
 *
 * Hakbot Origin Controller is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * Hakbot Origin Controller is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Hakbot Origin Controller. If not, see http://www.gnu.org/licenses/.
 */
package io.hakbot.controller.tasks;

import io.hakbot.controller.event.LdapSyncEvent;
import io.hakbot.controller.event.framework.EventService;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TaskScheduler {

    // Holds an instance of TaskScheduler
    private static final TaskScheduler instance = new TaskScheduler();

    // Holds a list of all timers created during construction
    private List<Timer> timers = new ArrayList<>();

    private TaskScheduler() {
        // Creates a new task that executes every 6 hours (21600000) after an initial 1 minute (60000) delay
        Timer ldapTimer = new Timer();
        ldapTimer.schedule(new ScheduledLdapSyncTask(), 60000, 21600000);
        timers.add(ldapTimer);
    }

    /**
     * Return an instance of the TaskScheduler instance
     * @return a TaskScheduler instance
     */
    public static TaskScheduler getInstance() {
        return instance;
    }

    private class ScheduledLdapSyncTask extends TimerTask {
        public synchronized void run() {
            EventService.getInstance().publish(new LdapSyncEvent());
        }
    }

    public void shutdown() {
        for (Timer timer: timers) {
            timer.cancel();
        }
    }
}
