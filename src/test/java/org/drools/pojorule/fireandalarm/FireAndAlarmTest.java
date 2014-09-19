package org.drools.pojorule.fireandalarm;

import org.drools.pojorule.fireandalarm.model.Fire;
import org.drools.pojorule.fireandalarm.model.Room;
import org.drools.pojorule.fireandalarm.model.Sprinkler;
import org.drools.retebuilder.CanonicalKieBase;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import static org.drools.pojorule.PojoRuleAnalyzer.toModule;
import static org.junit.Assert.assertTrue;

public class FireAndAlarmTest {

    @Test
    public void testFireAndAlarm() {
        CanonicalKieBase kieBase = new CanonicalKieBase();
        kieBase.addRules(toModule(FireAndAlarmModule.class));

        KieSession ksession = kieBase.newKieSession();

        // phase 1
        Room room1 = new Room("Room 1");
        ksession.insert(room1);
        FactHandle fireFact1 = ksession.insert(new Fire(room1));
        ksession.fireAllRules();

        // phase 2
        Sprinkler sprinkler1 = new Sprinkler(room1);
        ksession.insert(sprinkler1);
        ksession.fireAllRules();

        assertTrue(sprinkler1.isOn());

        // phase 3
        ksession.delete(fireFact1);
        ksession.fireAllRules();
    }
}
