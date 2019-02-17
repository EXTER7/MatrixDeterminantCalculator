package exter.matrixdeterminant.solution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import exter.matrixdeterminant.fraction.Fraction;
import exter.matrixdeterminant.matrix.Matrix;

public abstract class Step {
    enum Type {
        ELEMENTS,
        MINOR,
        COFACTOR,
        SUM,
        CROSS
    }
    
    static public final class ElementsStep extends Step {
        private final Matrix source;
        private final boolean elementsCol;
        private final int elementsIndex;
        private final List<Integer> elements;

        public ElementsStep(Matrix source,int matrixNo) {
            super(matrixNo);
            boolean column = false;
            int index = -1;
            int zeros = -1;
            List<Integer> bestElements = null;
            int size = source.getSize();

            for(int i = 0; i < size; i++) {
                int rowZeros = 0;
                List<Integer> elements = new ArrayList<Integer>();
                for(int j = 0; j < size; j++) {
                    if(source.getElement(i, j).isZero()) {
                        rowZeros++;
                    } else {
                        elements.add(j);
                    }
                }
                if(rowZeros > zeros) {
                    index = i;
                    bestElements = elements;
                    zeros = rowZeros;
                }
            }

            for(int i = 0; i < size; i++) {
                int colZeros = 0;
                List<Integer> elements = new ArrayList<Integer>();
                for(int j = 0; j < size; j++) {
                    if(source.getElement(j, i).isZero()) {
                        colZeros++;
                    } else {
                        elements.add(j);
                    }
                }
                if(colZeros > zeros) {
                    index = i;
                    bestElements = elements;
                    column = true;
                    zeros = colZeros;
                }
            }
            
            this.source = source;
            this.elementsCol = column;
            this.elementsIndex = index;
            this.elements = bestElements;
        }

        
        @Override
        public Type getType() {
            return Type.ELEMENTS;
        }
        
        public Matrix getSource() {
            return source;
        }
        
        public boolean isElementsColumn() {
            return this.elementsCol;
        }
        
        public int getElementsIndex() {
            return this.elementsIndex;
        }

        public List<Integer> getElements() {
            return this.elements;
        }
        
        public int getElementRow(int k) {
            return this.elementsCol ? k : this.elementsIndex;
        }

        public int getElementColumn(int k) {
            return this.elementsCol ? this.elementsIndex : k;
        }

        public Stream<Fraction> getElementStream() {
            return this.elements.stream().map((k) -> this.source.getElement(this.getElementRow(k), getElementColumn(k)));
        }

        public List<Fraction> getElementValues() {
            return this.getElementStream().collect(Collectors.toList());
        }
    }
    
    static public class MinorStep extends Step {
        private final Matrix source;
        private final Matrix minor;
        private final int row,col;
        private final int parentMatrixNo;

        public MinorStep(Matrix source,int matrixNo,int parentMatrixNo,int row,int col) {
            super(matrixNo);
            this.source = source;
            this.minor = source.minor(row, col);
            this.parentMatrixNo = parentMatrixNo;
            this.row = row;
            this.col = col;
        }
        
        @Override
        public Type getType() {
            return Type.MINOR;
        }

        public Matrix getSource() {
            return source;
        }

        public Matrix getMinorMatrix() {
            return minor;
        }

        public int getParentMatrixNo() {
            return this.parentMatrixNo;
        }

        public int getRow() {
            return this.row;
        }
        
        public int getColumn() {
            return this.col;
        }
    }

    static public class CofactorStep extends Step {
        private final int row,col;
        private final int parentMatrixNo;
        private final Fraction minor;
        private final int cofactor;

        public CofactorStep(int matrixNo,int parentMatrixNo,int row,int col,Fraction minor) {
            super(matrixNo);
            this.parentMatrixNo = parentMatrixNo;
            this.row = row;
            this.col = col;
            this.minor = minor;
            this.cofactor = 1 - ((row + col) % 2) * 2;
        }
    
        @Override
        public Type getType() {
            return Type.COFACTOR;
        }

        public int getRow() {
            return this.row;
        }
        
        public int getColumn() {
            return this.col;
        }

        public int getParentMatrixNo() {
            return this.parentMatrixNo;
        }
        
        public Fraction getMinor() {
            return this.minor;
        }
        
        public int getCofactor() {
            return this.cofactor;
        }

        public Fraction getCofactorMinor() {
            return this.minor.mul(this.cofactor);
        }
    }

    static public class SumStep extends Step {
        static public class Element {
            private final int row,col;
            private final Fraction cofactor;
            private final Fraction coefficient;

            public Element(int row, int col, Fraction cofactor, Fraction coefficient) {
                this.row = row;
                this.col = col;
                this.cofactor = cofactor;
                this.coefficient = coefficient;
            }

            public Fraction getCofactor() {
                return this.cofactor;
            }

            public Fraction getCoefficient() {
                return this.coefficient;
            }

            public int getRow() {
                return row;
            }

            public int getColumn() {
                return col;
            }
            
            public Fraction getResult() {
                return this.coefficient.mul(this.cofactor);
            }
        }

        private final List<Element> sum;

        public SumStep(List<Element> sum,int matrixNo) {
            super(matrixNo);
            this.sum = new ArrayList<>(sum);
        }

        @Override
        public Type getType() {
            return Type.SUM;
        }

        public List<Element> getSumElements() {
            return Collections.unmodifiableList(this.sum);
        }
        

        public Fraction getSum() {
            return this.sum.stream().map(Element::getResult).reduce(new Fraction(), Fraction::add);
        }
    }
    
    static public final class CrossStep extends Step {

        private final Fraction a,b,c,d;
        private final Fraction ad,bc;

        public CrossStep(int matrixNo, Fraction a,Fraction b,Fraction c,Fraction d) {
            super(matrixNo);
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
            this.ad = a.mul(d);
            this.bc = b.mul(c);
        }
        
        @Override
        public Type getType() {
            return Type.CROSS;
        }


        public Fraction getA() {
            return this.a;
        }

        public Fraction getB() {
            return this.b;
        }

        public Fraction getC() {
            return this.c;
        }

        public Fraction getD() {
            return this.d;
        }

        public Fraction getProductAD() {
           return this.ad;
        }

        public Fraction getProductBC() {
           return this.bc; 
        }

        public Fraction getDeterminant() {
           return this.ad.sub(this.bc); 
        }
    }

    private final int matrixNo;

    private Step(int matrixNo) {
        this.matrixNo = matrixNo;
    }
    
    public abstract Type getType();

    public final int getMatrixNo() {
        return this.matrixNo;
    }
}
