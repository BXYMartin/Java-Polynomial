package poly;

import java.math.BigInteger;
import java.util.Map;

public class Term {
    private BigInteger coeff = new BigInteger("0");
    private Factor factor = new Factor();

    public Term() {}

    public Term(BigInteger coeff, Factor factor) {
        this.coeff = coeff;
        this.factor = factor;
    }

    public void setCoeff(BigInteger coeff) {
        this.coeff = coeff;
    }

    public void setFactor(Factor factor) {
        this.factor = factor;
    }

    public BigInteger getCoeff() {
        return coeff;
    }

    public Factor getFactor() {
        return factor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Term)) {
            return false;
        }
        Term i = (Term) o;
        return i.getCoeff().equals(coeff) && i.getFactor().equals(factor);
    }

    @Override
    public int hashCode() {
        return coeff.toString().hashCode() * 31 + factor.hashCode();
    }
}
