package com.github.ronanquillevere.generator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


public class ExportAnnotations 
{

  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.TYPE})
  public @interface ExportClass 
  {
      boolean withImport() default true;
      
      Class<?> type();
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.METHOD)
  public static @interface ExportMethod 
  {
    boolean withSignature() default true;
    
    Class<?> type();
    
    String methodName();
  }

}