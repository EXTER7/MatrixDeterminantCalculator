package exter.matrixdeterminant;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import exter.matrixdeterminant.fraction.Fraction;
import exter.matrixdeterminant.matrix.Matrix;
import exter.matrixdeterminant.solution.Process;
import exter.matrixdeterminant.solution.Step.CofactorStep;
import exter.matrixdeterminant.solution.Step.CrossStep;
import exter.matrixdeterminant.solution.Step.ElementsStep;
import exter.matrixdeterminant.solution.Step.MinorStep;
import exter.matrixdeterminant.solution.Step.SumStep;
import spark.Request;
import spark.Response;
import spark.Spark;

public class MatrixDeterminantWeb
{
    private static Fraction determinant(Matrix input,Process process) {
        int size = input.getSize();
        if(size == 1)
        {
            return input.getElement(0, 0);
        } else if(size == 2)
        {
            CrossStep cross = new CrossStep(process.getMatrixNo(),
                                            input.getElement(0, 0),
                                            input.getElement(0, 1),
                                            input.getElement(1, 0),
                                            input.getElement(1, 1));
            process.getSteps().add(cross);
            return cross.getDeterminant();
        }
  
        ElementsStep elemStep = new ElementsStep(input,process.getMatrixNo());
        process.getSteps().add(elemStep);
        
        int cfMatrixNo = process.getMatrixNo();

        List<SumStep.Element> sum = new ArrayList<>();
        for(int k: elemStep.getElements())
        {
            int i = elemStep.getElementRow(k);
            int j = elemStep.getElementColumn(k);
            Fraction element = input.getElement(i, j); 

            process.incMatrixNo();
            int valMatrixNo = process.getMatrixNo();
            
            MinorStep minorStep = new MinorStep(input,process.getMatrixNo(),cfMatrixNo,i,j);
            process.getSteps().add(minorStep);
            Fraction determinant = determinant(minorStep.getMinorMatrix(),process);

            CofactorStep cofactorStep = new CofactorStep(valMatrixNo,cfMatrixNo,i,j,determinant);
            process.getSteps().add(cofactorStep);
            sum.add(new SumStep.Element(i,j,determinant.mul(cofactorStep.getCofactor()),element));
        }

        SumStep sumStep = new SumStep(sum,cfMatrixNo);
        process.getSteps().add(sumStep);
        return sumStep.getSum();
    }

    private static void calculateSolution(Matrix input,VelocityContext ctx)
    {
        Process process = new Process();
        Fraction solution = determinant(input,process);
        ctx.put("input", input);
        ctx.put("solution", solution);
        ctx.put("process", process.getSteps());
    }
  
    private static Object inputPage(final Request request, final Response response) throws Exception
    {
        StringWriter writer = new StringWriter();
        VelocityContext ctx = new VelocityContext();
    
        inputTemplate.merge(ctx, writer);
        return writer.toString();
    }
  
    private static Object outputPage(final Request request, final Response response) throws Exception
    {
        try
        {
            StringWriter writer = new StringWriter();
            Matrix input;
            try
            {
                input = new Matrix(request);
            } catch (Exception e)
            {
                response.status(422);
                return "Invalid request argument";
            }
            VelocityContext ctx = new VelocityContext();
            calculateSolution(input,ctx);
            outputTemplate.merge(ctx, writer);
            return writer.toString();
        } catch (Exception e)
        {
            response.status(500);
            return "Error processing request: " + e.getMessage();
        }
    }


    static private Template inputTemplate;
    static private Template outputTemplate;
    static private VelocityEngine ve;
    
    public static void main(String[] args)
    {
        ve = new VelocityEngine();
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath"); 
        ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        ve.init();
        
        inputTemplate = ve.getTemplate("/vm/input.vm");
        outputTemplate = ve.getTemplate("/vm/output.vm");
        Spark.exception(Exception.class, (exception, request, response) -> {
            exception.printStackTrace();
        });
        Spark.get("/", MatrixDeterminantWeb::inputPage);
        Spark.get("/calc", MatrixDeterminantWeb::outputPage);
    }
}
