package com.github.ronanquillevere.generator;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

public class BuildInfoGenerator extends Generator {
    
    @Override
    public String generate(TreeLogger logger, GeneratorContext context,
            String typeName) throws UnableToCompleteException {
        TypeOracle typeOracle = context.getTypeOracle();
        assert (typeOracle != null);
 
        JClassType classType = typeOracle.findType(typeName);
        if (classType == null) {
            logger.log(TreeLogger.ERROR, "Unable to find metadata for type '" + typeName + "'", null);
            throw new UnableToCompleteException();
        }
 
        String packageName = classType.getPackage().getName();
        String simpleName = classType.getSimpleSourceName() + "Impl";
        String qualifiedClassName = packageName + "." + simpleName;
 
        ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(
                packageName, simpleName);
        composerFactory.addImport(BuildInfo.class.getCanonicalName());
        composerFactory.addImplementedInterface(BuildInfo.class.getName());
 
        PrintWriter printWriter = context.tryCreate(logger, packageName, simpleName);
        if (printWriter == null)
            return qualifiedClassName;
 
        SourceWriter sourceWriter = composerFactory.createSourceWriter(context, printWriter);
        if (sourceWriter == null)
            return qualifiedClassName;
 
        String buildTimestamp = getCurrentTimestampString();
 
        // write the method body of getBuildTimestamp
        sourceWriter.println("public String getBuildTimestamp() {");
        sourceWriter.println("    return \"" + buildTimestamp + "\";");
        sourceWriter.println("}");
        // method body ends
 
        sourceWriter.commit(logger);
        return packageName + "." + simpleName;
    }
 
    private String getCurrentTimestampString() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
        return format.format(new Date());
    }
 
}
