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
        
        for (int i = 0; i < 10; i++)
        {
            var2++;
            if (var2 > 8)
            {
                var2 += 1;
            }
        }
        System.out.println(test + var2);
    }
}
