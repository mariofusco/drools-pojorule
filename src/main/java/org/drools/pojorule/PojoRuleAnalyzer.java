package org.drools.pojorule;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import org.drools.model.Drools;
import org.drools.model.Rule;
import org.drools.model.Variable;
import org.drools.model.View;

public class PojoRuleAnalyzer {
    
    public static Rule[] toModule(Class<?> clazz) {
        Class<?>[] innerClasses = clazz.getClasses();
        Rule[] rules = new Rule[innerClasses.length];
        int i = 0;
        for (Class<?> innerClass : innerClasses) {
            rules[i++] = toRule(innerClass);
        }
        return rules;
    }

    public static Rule toRule(Class<?> clazz) {
        return analyze(clazz).toRule();
    }

    public static PojoRuleDescr analyze(Class<?> clazz) {
        PojoRuleDescr ruleDescr = new PojoRuleDescr();
        ruleDescr.setRuleName(clazz.getSimpleName());
        ruleDescr.setPackage(clazz.getPackage().getName());

        Object pojoInstance;
        try {
            pojoInstance = clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        ruleDescr.setPojoInstance(pojoInstance);

        for (Field f : clazz.getDeclaredFields()) {
            f.setAccessible(true);
            if (Variable.class.isAssignableFrom(f.getType())) {
                try {
                    Variable var = (Variable)f.get(pojoInstance);
                    ruleDescr.addVariable(f.getName(), var);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            } else if (f.getName().equals("when")) {
                try {
                    ruleDescr.setWhen((View)f.get(pojoInstance));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }


        for (Method m : clazz.getMethods()) {
            if (m.getName().equals("then")) {
                Parameter[] params = m.getParameters();
                boolean usingDrools = params.length > 0 && Drools.class.isAssignableFrom(params[0].getType());
                ruleDescr.setUsingDroolsInConsequence(usingDrools);
                ruleDescr.setThen(m);
                int i = 0;
                for (Parameter p : m.getParameters()) {
                    if (!Drools.class.isAssignableFrom(p.getType()) && p.isNamePresent()) {
                        ruleDescr.setThenParams(p.getName(), i++);
                    }
                }
                if (i != m.getParameterCount() - (usingDrools ? 1 : 0)) {
                    throw new RuntimeException("Wrong number of consequence parameters. " +
                                               "Method arguments' names not available?");
                }
                break;
            }
        }

        return ruleDescr;
    }
}
