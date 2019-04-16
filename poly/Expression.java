package poly;

import java.util.Vector;

public class Expression {
    private Vector<Term> element = new Vector<>();

    public Expression(Term term) {
        element.clear();
        element.add(term);
    }

    public void addTerm(Term term) {
        element.add(term);
    }

    public Vector<Term> getElement() {
        return element;
    }
}
