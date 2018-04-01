package exter.matrixdeterminant.matrix;

import exter.matrixdeterminant.fraction.Fraction;
import spark.Request;

public final class Matrix
{
    private final Fraction[][] elements;
    
    public Matrix(int size)
    {
        if(size < 1)
        {
            throw new IllegalArgumentException();
        }
        this.elements = new Fraction[size][size];
        for(int i = 0; i < size; i++)
        {
            for(int j = 0; j < size; j++)
            {
                this.elements[i][j] = new Fraction();
            }
        }
    }

    public Matrix(Matrix m)
    {
        int size = m.elements.length;
        this.elements = new Fraction[size][];
        for(int i = 0; i < size; i++)
        {
            this.elements[i] = m.elements[i].clone();
        }
    }

    public Matrix(final Request request)
    {
        int size = Integer.valueOf(request.queryParams("size"));
        if(size < 1)
        {
            throw new IllegalArgumentException();
        }
        this.elements = new Fraction[size][size];
        for(int i = 0; i < size; i++)
        {
            for(int j = 0; j < size; j++)
            {
                this.elements[i][j] = new Fraction(request.queryParams(String.format("matrix_%d_%d",i,j)));
            }
        }
    }
    
    public Matrix minor(int row,int col)
    {
        int newSize = this.elements.length - 1;
        Matrix m = new Matrix(newSize);
        for(int i = 0, k = 0; i < newSize; i++, k++)
        {
            if(i == row)
            {
                k++;
            }
            for(int j = 0,l = 0; j < newSize ; j++,l++)
            {
                if(j == col)
                {
                    l++;
                }
                m.elements[i][j] = this.elements[k][l];
            }
        }
        return m;
    }
    
    public int getSize()
    {
        return this.elements.length;
    }
    
    public Fraction getElement(int row,int col)
    {
        return this.elements[row][col];
    }
}
