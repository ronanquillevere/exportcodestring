package com.github.ronanquillevere.client;

import com.github.ronanquillevere.generator.ExportAnnotations.ExportMethod;
import com.github.ronanquillevere.generator.ExportShell;

public interface Export extends ExportShell
{
    @ExportMethod(type=WhatIWantToExport.class, methodName="myMethod")
    String getMethod();
}
