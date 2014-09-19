package org.drools.pojorule.fireandalarm;

import org.drools.model.Drools;
import org.drools.model.Variable;
import org.drools.pojorule.fireandalarm.model.Alarm;
import org.drools.pojorule.fireandalarm.model.Fire;
import org.drools.pojorule.fireandalarm.model.Sprinkler;

import static org.drools.model.DSL.*;
import static org.drools.model.DSL.not;

public class FireAndAlarmModule {

    public static class WhenThereIsAFireTurnOnTheSprinkler {
        Variable<Fire> fire = any(Fire.class);
        Variable<Sprinkler> sprinkler = any(Sprinkler.class);

        Object when = when(
                input(fire),
                input(sprinkler),
                expr(sprinkler, s -> !s.isOn()),
                expr(sprinkler, fire, (s, f) -> s.getRoom().equals(f.getRoom()))
        );

        public void then(Drools drools, Sprinkler sprinkler) {
            System.out.println("Turn on the sprinkler for room " + sprinkler.getRoom().getName());
            sprinkler.setOn(true);
            drools.update(sprinkler);
        }
    }

    public static class WhenTheFireIsGoneTurnOffTheSprinkler {
        Variable<Fire> fire = any(Fire.class);
        Variable<Sprinkler> sprinkler = any(Sprinkler.class);

        Object when = when(
                input(sprinkler),
                expr(sprinkler, Sprinkler::isOn),
                input(fire),
                not(fire, sprinkler, (f, s) -> f.getRoom().equals(s.getRoom()))
        );

        public void then(Drools drools, Sprinkler sprinkler) {
            System.out.println("Turn off the sprinkler for room " + sprinkler.getRoom().getName());
            sprinkler.setOn(false);
            drools.update(sprinkler);
        }
    }

    public static class RaiseTheAlarmWhenWeHaveOneOrMoreFires {
        Variable<Fire> fire = any(Fire.class);

        Object when = when(
                input(fire),
                exists(fire)
        );

        public void then(Drools drools) {
            System.out.println("Raise the alarm");
            drools.insert(new Alarm());
        }
    }

    public static class LowerTheAlarmWhenAllTheFiresHaveGone {
        Variable<Fire> fire = any(Fire.class);
        Variable<Alarm> alarm = any(Alarm.class);

        Object when = when(
                input(fire),
                not(fire),
                input(alarm)
        );

        public void then(Drools drools, Alarm alarm)  {
            System.out.println("Lower the alarm");
            drools.delete(alarm);
        }
    }

    public static class StatusOutputWhenThingsAreOk {
        Variable<Fire> fire = any(Fire.class);
        Variable<Sprinkler> sprinkler = any(Sprinkler.class);
        Variable<Alarm> alarm = any(Alarm.class);

        Object when = when(
                input(alarm),
                not(alarm),
                input(sprinkler),
                not(sprinkler, Sprinkler::isOn)
        );

        public void then()  {
            System.out.println("Everything is ok");
        }
    }
}
