package poly;

import java.math.BigInteger;

public class Term {
    private BigInteger coeff;
    private Factor factor;

    public Term(BigInteger coeff, Factor factor) {
        this.coeff = coeff;
        this.factor = factor;
    }

    public void setCoeff(BigInteger coeff) {
        this.coeff = coeff;
    }

    public Term multiply(Term other) {
        return new Term(this.coeff.multiply(other.coeff),
                this.factor.multiply(other.factor));
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
}
