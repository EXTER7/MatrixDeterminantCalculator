package exter.matrixdeterminant.solution;

import java.util.ArrayList;
import java.util.List;

public class Process
{
    private final List<Step> steps;
    private int matrixNo;
  
    public Process()
    {
        this.steps = new ArrayList<>();
        this.matrixNo = 0;
    }
  
    public List<Step> getSteps()
    {
        return steps;
    }

    public int getMatrixNo()
    {
        return matrixNo;
    }
    
    public void incMatrixNo()
    {
        this.matrixNo++;
    }
}
