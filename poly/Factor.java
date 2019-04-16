package poly;

import java.math.BigInteger;

public class Factor {

    private BigInteger sinPower;
    private BigInteger cosPower;
    private BigInteger varPower;

    private boolean isNull() {
        if (sinPower.equals(new BigInteger("0")) && 
                cosPower.equals(new BigInteger("0")) && 
                varPower.equals(new BigInteger("0"))) {
            return true;
        } else {
            return false;
        }
    }

    public Factor() {
        this.varPower = new BigInteger("0");
        this.sinPower = new BigInteger("0");
        this.cosPower = new BigInteger("0");
    }

    public Factor(BigInteger x, BigInteger sinPower, BigInteger cosPower) {
        this.varPower = x;
        this.sinPower = sinPower;
        this.cosPower = cosPower;
    }

    public void setCosPower(BigInteger cosPower) {
        this.cosPower = cosPower;
    }

    public void setSinPower(BigInteger sinPower) {
        this.sinPower = sinPower;
    }

    public void setxPower(BigInteger x) {
        this.varPower = x;
    }

    public BigInteger getxPower() {
        return varPower;
    }

    public BigInteger getSinPower() {
        return sinPower;
    }

    public BigInteger getCosPower() {
        return cosPower;
    }

    public Factor multiply(Factor other) {
        return new Factor(this.getxPower().add(other.getxPower()),
                this.getSinPower().add(other.getSinPower()),
                this.getCosPower().add(other.getCosPower()));
    }

    public String print(boolean isFirst, BigInteger coeff) {
        if (coeff.equals(new BigInteger("0"))) {
            return "";
        } else {
            String target = "";
            boolean isOmit = false;
            if (coeff.equals(new BigInteger("1")) && !isNull()) {
                if (!isFirst) {
                    target += "+";
                }
                isOmit = true;
            } else if (coeff.equals(new BigInteger("-1")) && !isNull()) {
                target += "-";
                isOmit = true;
            } else if (!isFirst && coeff.compareTo(new BigInteger("0")) > 0) {
                target += "+" + coeff;
            } else {
                target += coeff;
            }
            if (varPower.equals(new BigInteger("1"))) {
                if (!isOmit) {
                    target += "*";
                }
                isOmit = false;
                target += "x";
            } else if (!varPower.equals(new BigInteger("0"))) {
                if (!isOmit) {
                    target += "*";
                }
                isOmit = false;
                target += "x^" + varPower;
            }
            if (sinPower.equals(new BigInteger("1"))) {
                if (!isOmit) {
                    target += "*";
                }
                isOmit = false;
                target += "sin(x)";
            } else if (!sinPower.equals(new BigInteger("0"))) {
                if (!isOmit) {
                    target += "*";
                }
                isOmit = false;
                target += "sin(x)^" + sinPower;
            }
            if (cosPower.equals(new BigInteger("1"))) {
                if (!isOmit) {
                    target += "*";
                }
                target += "cos(x)";
            } else if (!cosPower.equals(new BigInteger("0"))) {
                if (!isOmit) {
                    target += "*";
                }
                target += "cos(x)^" + cosPower;
            }
            return target;
        }
    }

    public String peek(BigInteger coeff) {
        String target = "";
        target += coeff;
        target += "*x^" + varPower;
        target += "*sin(x)^" + sinPower;
        target += "*cos(x)^" + cosPower;
        return target;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Factor)) {
            return false;
        }
        Factor i = (Factor) o;
        return i.sinPower.equals(sinPower)
                && i.cosPower.equals(cosPower) && i.varPower.equals(varPower);
    }

    @Override
    public int hashCode() {
        String result = varPower.toString() + " " + 
                sinPower.toString() + " " + cosPower.toString();
        return result.hashCode();
    }
}
