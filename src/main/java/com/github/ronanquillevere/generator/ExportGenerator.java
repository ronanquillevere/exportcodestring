package com.github.ronanquillevere.generator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringEscapeUtils;

import com.github.ronanquillevere.client.Export;
import com.github.ronanquillevere.generator.ExportAnnotations.ExportMethod;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.thirdparty.guava.common.base.Strings;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

public class ExportGenerator extends Generator {
    
    /**
     * The class loader used to get resources.
     */
    private ClassLoader classLoader = null;
    
    /**
     * The {@link TreeLogger} used to log messages.
     */
    private TreeLogger logger = null;
    
    
    @Override
    public String generate(TreeLogger logger, GeneratorContext context,
            String typeName) throws UnableToCompleteException {
        
        this.classLoader = Thread.currentThread().getContextClassLoader();
        this.logger = logger;
        
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
        composerFactory.addImport(Export.class.getCanonicalName());
        composerFactory.addImplementedInterface(Export.class.getName());
 
        PrintWriter printWriter = context.tryCreate(logger, packageName, simpleName);
        if (printWriter == null)
            return qualifiedClassName;
 
        SourceWriter sourceWriter = composerFactory.createSourceWriter(context, printWriter);
        if (sourceWriter == null)
            return qualifiedClassName;
 
        
        JMethod[] methods = classType.getMethods();
        for (JMethod jMethod : methods)
        {
            if (jMethod.isAnnotationPresent(ExportMethod.class)){
                  writeExportedMethodCode(sourceWriter, jMethod);
            }
        }
        

 
        sourceWriter.commit(logger);
        
        return packageName + "." + simpleName;
    }
 
    private void writeExportedMethodCode(SourceWriter sourceWriter, JMethod jMethod) throws UnableToCompleteException
    {
        JParameter[] parameters = jMethod.getParameters();
        
        if (parameters.length > 0)
            throw new RuntimeException("method cannot have parameters");
        
        if (!jMethod.getReturnType().getQualifiedSourceName().equals(String.class.getName()))
            throw new RuntimeException("method return type must be String");
        
        if (!jMethod.isPublic())
            throw new RuntimeException("method return type must be String");

        ExportMethod exportInfos = jMethod.getAnnotation(ExportMethod.class);
        
        String marker = exportInfos.marker();
        
        if (Strings.isNullOrEmpty(marker))
            throw new RuntimeException("marker identifiying destination method should be filled and not empty");
        
        
        Class<?> type = exportInfos.type();        
        String filePath = type.getName().replace('.', '/') + ".java";
        String fileContents = getResourceContents(filePath);
        int methodIndex = fileContents.indexOf(marker);
        
        int beginIndex = fileContents.indexOf("{", methodIndex) + 1;
        
        int endIndex = beginIndex;
        
        int nextOpen = fileContents.indexOf("{", beginIndex);
        int nextClose = fileContents.indexOf("}", beginIndex);
        
        if (nextOpen == -1 || nextClose < nextOpen){
            endIndex = nextClose;
        }
        else
        {
            
        }
        
        String methodContent = fileContents.substring(beginIndex, endIndex);
        
        sourceWriter.println("public final " + jMethod.getReturnType().getSimpleSourceName() + " " + jMethod.getName() + "(){");
        sourceWriter.println("return \"" + StringEscapeUtils.escapeJava(methodContent) + "\";");
        sourceWriter.println("}");

    }


    private String getCurrentTimestampString() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
        return format.format(new Date());
    }
    
    /**
     * Get the full contents of a resource.
     *
     * @param path the path to the resource
     * @return the contents of the resource
     */
    private String getResourceContents(String path)
        throws UnableToCompleteException {
      InputStream in = classLoader.getResourceAsStream(path);
      if (in == null) {
        logger.log(TreeLogger.ERROR, "Resource not found: " + path);
        throw new UnableToCompleteException();
      }

      StringBuffer fileContentsBuf = new StringBuffer();
      BufferedReader br = null;
      try {
        br = new BufferedReader(new InputStreamReader(in));
        String temp;
        while ((temp = br.readLine()) != null) {
          fileContentsBuf.append(temp).append('\n');
        }
      } catch (IOException e) {
        logger.log(TreeLogger.ERROR, "Cannot read resource", e);
        throw new UnableToCompleteException();
      } finally {
        if (br != null) {
          try {
            br.close();
          } catch (IOException e) {
          }
        }
      }

      // Return the file contents as a string
      return fileContentsBuf.toString();
    }

 
}
