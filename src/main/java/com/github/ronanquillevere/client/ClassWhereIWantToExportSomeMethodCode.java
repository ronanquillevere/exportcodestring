package com.github.ronanquillevere.client;

import com.github.ronanquillevere.generator.ExportAnnotations.ExportMaker;

public class ClassWhereIWantToExportSomeMethodCode
{
    @ExportMaker(name = "myMethod")
    public void theMethodWithCodeIWantToExport()
    {
        String test = "test";
        int var2 = 5;
        test += String.valueOf(var2);
        System.out.println(test);
    }
}
