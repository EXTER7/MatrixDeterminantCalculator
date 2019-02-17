package exter.matrixdeterminant.fraction;

public final class Fraction {

    private long num;
    private long den;

    static private long gcd(long a, long b) {
        if(a < 0) {
          a = -a;
        }
        if(b < 0) {
          b = -b;
        }
        long r;
        while(b != 0) {
            r = a % b;
            a = b;
            b = r;
        }
        return a;
    }

    private void simplify() {
        if(this.num == 0) {
            this.den = 1;
            return;
        }
        long g = gcd(this.num,this.den);
        this.num /= g;
        this.den /= g;
        if(this.den < 0) {
            this.num = -this.num;
            this.den = -this.den;
        }
    }
    
    public Fraction() {
        this.num = 0;
        this.den = 1;
    }
    
    public Fraction(long i) {
        num = i;
        den = 1;
    }

    public Fraction(long num,long den) {
        this.num = num;
        this.den = den;
        this.simplify();
    }

    public Fraction(String str) {
        if(str == null) {
          this.num = 0;
          this.den = 1;
          throw new NumberFormatException();
        }
        str = str.replaceAll("[^0-9/\\-]", "");
        String[] fstr = str.split("/");
        if(fstr.length == 1) {
            this.num = Long.valueOf(fstr[0]);
            this.den = 1;
        } else if(fstr.length == 2) {
            this.num = Long.valueOf(fstr[0]);
            this.den = Long.valueOf(fstr[1]);
        } else {
           this.num = 0;
           this.den = 1;
           throw new NumberFormatException();
        }
        this.simplify();
    }
    
    public long getNumerator() {
        return this.num;
    }
    
    public long getDenominator() {
        return this.den;
    }
    
    public Fraction add(Fraction f) {
        return new Fraction(this.num * f.den + f.num * this.den, this.den * f.den);
    }

    public Fraction sub(Fraction f) {
        return new Fraction(this.num * f.den - f.num * this.den, this.den * f.den);
    }

    public Fraction mul(Fraction f) {
        return new Fraction(f.num * this.num, this.den * f.den);
    }

    public Fraction mul(int i) {
        return new Fraction(this.num * i, this.den);
    }

    public Fraction div(Fraction f) {
        return new Fraction(this.num * f.den, this.den * f.num);
    }

    public Fraction div(int i) {
        return new Fraction(this.num, this.den * i);
    }

    public Fraction neg() {
        return new Fraction(-this.num, this.den);
    }

    public Fraction inv() {
        return new Fraction(this.den, this.num);
    }


    @Override
    public int hashCode() {
        final int prime = 997;
        int result = 1;
        result = prime * result + (int) (den ^ (den >>> 32));
        result = prime * result + (int) (num ^ (num >>> 32));
        return result;
    }
  
    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null) return false;
        if(getClass() != obj.getClass()) return false;
        Fraction other = (Fraction) obj;
        if(den != other.den) return false;
        if(num != other.num) return false;
        return true;
    }
    
    public boolean isZero() {
        return this.num == 0;
    }

    public boolean isOne() {
        return this.num * this.den == 1;
    }
    
    @Override
    public String toString() {
        if(den == 1) {
            return String.valueOf(this.num);
        } else {
            return String.format("%d/%d",this.num,this.den);
        }
    }
}
