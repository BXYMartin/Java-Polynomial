package poly;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

public class Accumulation {
    private Vector<Expression> expression = new Vector<>();

    public Accumulation(Expression exp) {
        expression.clear();
        expression.add(exp);
    }

    public void addExpression(Expression exp) {
        expression.add(exp);
    }

    private Map<Factor, BigInteger> cloneMap(Map<Factor, BigInteger> source) {
        Map<Factor, BigInteger> target = new HashMap<>();
        Iterator<Map.Entry<Factor, BigInteger>> it =
                source.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Factor, BigInteger> entry = it.next();
            target.put(entry.getKey(), entry.getValue());
        }
        return target;
    }

    public Vector<Term> calcExpression() {
        if (expression.isEmpty()) {
            Vector<Term> result = new Vector<>();
            result.add(new Term(new BigInteger("0"), new Factor()));
            return result;
        } else if (expression.size() == 1) {
            return expression.elementAt(0).getElement();
        } else {
            Vector<Term> result = new Vector<>();

            result.add(new Term(new BigInteger("1"), new Factor()));
            for (int j = 0; j < expression.size(); j++) {
                Vector<Term> other = expression.elementAt(j).getElement();
                Vector<Term> temp = new Vector<>();
                for (int i = 0; i < result.size(); i++) {
                    for (int k = 0; k < other.size(); k++) {
                        temp.add(result.elementAt(i)
                                .multiply(other.elementAt(k)));
                    }
                }
                result = new Vector<>(temp);
            }
            return result;
        }
    }
}
