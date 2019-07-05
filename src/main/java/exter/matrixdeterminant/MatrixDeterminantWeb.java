package exter.matrixdeterminant;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
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
import io.javalin.http.Context;
import io.javalin.Javalin;

public class MatrixDeterminantWeb {
    private static Fraction determinant(Matrix input,Process process) {
        int size = input.getSize();
        if(size == 1) {
            return input.getElement(0, 0);
        } else if(size == 2) {
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

    private static void calculateSolution(Matrix input,VelocityContext ctx) {
        Process process = new Process();
        Fraction solution = determinant(input,process);
        ctx.put("input", input);
        ctx.put("solution", solution);
        ctx.put("process", process.getSteps());
    }
  
    private static void inputPage(Context ctx) {
        StringWriter writer = new StringWriter();
        VelocityContext vctx = new VelocityContext();
    
        inputTemplate.merge(vctx, writer);
        ctx.html(writer.toString());
    }
  
    private static void outputPage(Context ctx) {
        StringWriter writer = new StringWriter();
        Matrix input;
        try {
            input = new Matrix(ctx.req);
        } catch (Exception e) {
            ctx.status(422);
            ctx.result("Invalid request argument");
            return;
        }
        VelocityContext vctx = new VelocityContext();
        calculateSolution(input,vctx);
        outputTemplate.merge(vctx, writer);
        ctx.html(writer.toString());
    }


    static private Template inputTemplate;
    static private Template outputTemplate;
    static private VelocityEngine ve;
    
    public static void main(String[] args) {
        Javalin web = Javalin.create(config -> {
            config.autogenerateEtags = true;
            config.addStaticFiles("/pub");
            config.asyncRequestTimeout = 10000L;
            config.logIfServerNotStarted = true;
            config.dynamicGzip = true;
            config.enforceSsl = false;
        }).start(8080);
        
        
        ve = new VelocityEngine();
        ve.setProperty(RuntimeConstants.RESOURCE_LOADERS,RuntimeConstants.RESOURCE_LOADER_CLASS);
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER + "." + RuntimeConstants.RESOURCE_LOADER_CLASS + "." + RuntimeConstants.RESOURCE_LOADER_CLASS,
                       ClasspathResourceLoader.class.getName()); 
       // ve.setProperty("resource.loaders", );
        ve.init();
        
        inputTemplate = ve.getTemplate("/vm/input.vm");
        outputTemplate = ve.getTemplate("/vm/output.vm");
        web.exception(Exception.class, (ex,ctx) -> {
            ctx.result(ExceptionUtils.getStackTrace(ex));
        });
        web.get("/", MatrixDeterminantWeb::inputPage);
        web.get("/calc", MatrixDeterminantWeb::outputPage);
    }
}
