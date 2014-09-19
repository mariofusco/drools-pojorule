package org.drools.pojorule;

import org.drools.model.Variable;

import static org.drools.model.DSL.*;

public class MyRule {

    Variable<Person> mark = any(Person.class);
    Variable<Person> older = any(Person.class);

    Object when = when(
            input(mark),
            input(older),
            expr(mark, mark -> mark.getName().equals("Mark")),
            expr(mark, older, (mark, older) -> mark.getAge() < older.getAge()),
            expr(older, older -> !older.getName().equals("Mark"))
    );

    public void then( Person mark, Person older )  {
        System.out.println( older + " is older than " + mark);
    }

}
