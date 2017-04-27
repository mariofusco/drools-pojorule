package org.drools.pojorule;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.drools.model.Consequence;
import org.drools.model.Rule;
import org.drools.model.Variable;
import org.drools.model.View;
import org.drools.model.consequences.ConsequenceBuilder;
import org.drools.model.functions.BlockN;
import org.drools.model.impl.RuleImpl;

public class PojoRuleDescr {

    private Object pojoInstance;

    private String ruleName;
    private String pkg;

    private Map<String, Variable> vars = new HashMap<String, Variable>();

    private View when;

    private Method then;
    private Variable[] thenParams;

    private boolean usingDroolsInConsequence = false;

    public Object getPojoInstance() {
        return pojoInstance;
    }

    void setPojoInstance(Object pojoInstance) {
        this.pojoInstance = pojoInstance;
    }

    public String getRuleName() {
        return ruleName;
    }

    void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getPackage() {
        return pkg;
    }

    public void setPackage( String pkg ) {
        this.pkg = pkg;
    }

    public View getWhen() {
        return when;
    }

    void setWhen(View when) {
        this.when = when;
    }

    void addVariable(String name, Variable var) {
        vars.put(name, var);
    }

    public Variable getVariable(String name) {
        return vars.get(name);
    }

    public Method getThen() {
        return then;
    }

    public Variable[] getThenParams() {
        return thenParams;
    }

    void setThen(Method then) {
        this.then = then;
        this.thenParams = new Variable[usingDroolsInConsequence ? then.getParameterCount()-1 : then.getParameterCount()];
    }

    void setThenParams(String name, int pos) {
        thenParams[pos] = vars.get(name);
        if (thenParams[pos] == null) {
            throw new RuntimeException("Unknown consequence formal parameter: " + name);
        }
    }

    public void setUsingDroolsInConsequence(boolean usingDroolsInConsequence) {
        this.usingDroolsInConsequence = usingDroolsInConsequence;
    }

    public Rule toRule() {
        Consequence consequence = new ConsequenceBuilder().on(thenParams)
                                                          .execute(new MethodInvoker(pojoInstance, then))
                                                          .setUsingDrools(usingDroolsInConsequence)
                                                          .get();
        return new RuleImpl(pkg, ruleName, null, when, consequence, null);
    }

    public static class MethodInvoker implements BlockN {

        private final Object obj;
        private final Method method;

        public MethodInvoker(Object obj, Method method) {
            this.obj = obj;
            this.method = method;
        }

        @Override
        public void execute(Object... objs) {
            try {
                method.invoke(obj, objs);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
