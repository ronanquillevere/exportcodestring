package com.github.ronanquillevere.generator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.apache.commons.lang3.StringEscapeUtils;

import com.github.ronanquillevere.generator.ExportAnnotations.ExportMethod;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
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
        
        //composerFactory.addImport(Export.class.getCanonicalName());
        //composerFactory.addImplementedInterface(Export.class.getName());
 
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
//        JParameter[] parameters = jMethod.getParameters();
        
//        if (parameters.length > 0)
//            throw new RuntimeException("method cannot have parameters");
        
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
        
        String methodContent = getCodeToExport(fileContents, marker, type);
        
        String methodSignature = getMethodSignature(jMethod);
        
        sourceWriter.println(methodSignature);
        
        sourceWriter.println("{");
        sourceWriter.println("return \"" + StringEscapeUtils.escapeJava(methodContent) + "\";");
        sourceWriter.println("}");

    }

    private String getMethodSignature(JMethod jMethod)
    {
        StringBuilder b = new StringBuilder();
        b.append(jMethod.getReadableDeclaration(false, true, true, false, true));
        return b.toString();
    }

    private String getCodeToExport(String fileContents, String marker, Class<?> type)
    {
        int methodIndex = fileContents.indexOf(marker);
        
        if (methodIndex == -1)
            throw new RuntimeException("Could not find marker;"+ marker +";inside destination class;" + type.getSimpleName() );
        
        
        int beginIndex = findOpenBraces(fileContents, methodIndex) + 1;
        
        
        int endIndex = findEndIndex(fileContents, beginIndex);
        
        String methodContent = fileContents.substring(beginIndex, endIndex);
        return methodContent;
    }

    private int findEndIndex(String fileContents, int beginIndex)
    {
        int endIndex = beginIndex;
        
        int nextOpen = findOpenBraces(fileContents, beginIndex);
        int nextClose = findCloseBraces(fileContents, beginIndex);
        
        
        if (nextOpen != -1  && nextOpen < nextClose)
        {
            int closing = findEndIndex(fileContents, nextOpen + 1);
            endIndex =  findCloseBraces(fileContents, closing + 1);
        }
        else if (nextOpen == -1 || nextClose < nextOpen)
        {
            endIndex = nextClose;
        }
       
        return endIndex;
    }

    private int findCloseBraces(String fileContents, int beginIndex)
    {
        return fileContents.indexOf("}", beginIndex);
    }

    private int findOpenBraces(String fileContents, int beginIndex)
    {
        return fileContents.indexOf("{", beginIndex);
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
