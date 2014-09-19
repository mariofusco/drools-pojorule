package org.drools.pojorule;

import org.drools.model.Variable;
import org.drools.retebuilder.CanonicalKieBase;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class PojoRuleAnalyzerTest {

    @Test
    public void testPojoRuleDescr() {
        PojoRuleDescr ruleDescr = PojoRuleAnalyzer.analyze(MyRule.class);
        assertEquals("MyRule", ruleDescr.getRuleName());
        Variable[] thenParams = ruleDescr.getThenParams();
        assertSame(thenParams[0], ruleDescr.getVariable("mark"));
        assertSame(thenParams[1], ruleDescr.getVariable("older"));
    }

    @Test
    public void testRuleExecution() {
        CanonicalKieBase kieBase = new CanonicalKieBase();
        kieBase.addRules(PojoRuleAnalyzer.toRule(MyRule.class));

        KieSession ksession = kieBase.newKieSession();

        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));

        ksession.fireAllRules();
    }
}
