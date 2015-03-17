package com.github.ronanquillevere.generator;

import java.io.PrintWriter;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

public class ExportCodeAsStringGenerator extends Generator
{

    @Override
    public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException
    {
        
        JClassType classType;
        try {
            classType = context.getTypeOracle().getType(typeName);
            
            String packageName = classType.getPackage().getName();
            
            String simpleName = classType.getSimpleSourceName() + "Generated";
            
            ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(packageName, simpleName);
            
            composer.setSuperclass(classType.getName());       
            composer.addImplementedInterface("com.github.ronanquillevere.generator.ExportedCodeAsString");
            
            PrintWriter printWriter = context.tryCreate(logger, packageName, simpleName);
            SourceWriter sw = composer.createSourceWriter(context, printWriter);
            
            sw.println("public void test() {");
            
            sw.println("}");   
            
            return typeName + "Generated";
            
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
