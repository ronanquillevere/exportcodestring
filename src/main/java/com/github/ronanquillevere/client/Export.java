package com.github.ronanquillevere.client;

import com.github.ronanquillevere.generator.ExportAnnotations.ExportMethod;

public interface Export
{
    @ExportMethod(type=ClassWhereIWantToExportSomeMethodCode.class, marker="myMethod")
    String getMethodCode();
}
