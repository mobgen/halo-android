package com.mobgen.halo.android.content.processor;

import com.google.auto.service.AutoService;
import com.mobgen.halo.android.content.annotations.HaloTable;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@AutoService(Processor.class)
@SupportedAnnotationTypes("com.mobgen.halo.android.content.annotations.HaloTable")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class HaloContentDatabaseProcessor extends AbstractProcessor {

    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotataions = new LinkedHashSet<String>();
        annotataions.add(HaloTable.class.getCanonicalName());
        return annotataions;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }


    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        StringBuilder builder = new StringBuilder()
                .append("package com.mobgen.halo.content.generated;\n\n")
                .append("public class HaloTableName {\n\n") // open class
                .append("\tpublic String getMessage() {\n") // open method
                .append("\t\treturn \"");

        // for each javax.lang.model.element.Element annotated with the CustomAnnotation
        for (Element element : roundEnvironment.getElementsAnnotatedWith(HaloTable.class)) {
            //this return all method on the object annotated
//            for (ExecutableElement ee : ElementFilter.methodsIn(element.getEnclosedElements())) {
//                HaloTable haloTable = ee.getAnnotation(HaloTable.class);
//                System.out.println(ee.getReturnType().toString());
//                ee.
//               // messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING, "=====>>>>>>>Table name " + haloTable.name());
//                //messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING, "=====>>>>>>>Table query: " + haloTable.query());
//            }
            System.out.println("=====>>>>>>>Table name " + element.getAnnotation(HaloTable.class).name());

            messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING, "=====>>>>>>>Table name " + element.getAnnotation(HaloTable.class).name());
            messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING, "=====>>>>>>>Table query: " + element.getAnnotation(HaloTable.class).query());
            String name = element.getAnnotation(HaloTable.class).name();
            String query = element.getAnnotation(HaloTable.class).query();
            String objectType = element.getSimpleName().toString();
            // this is appending to the return statement
            builder.append(name).append(" want to query\\n").append(query).append("\\n");
        }

        builder.append("\";\n") // end return
                .append("\t}\n") // close method
                .append("}\n"); // close class

        try { // write the file
            JavaFileObject source = processingEnv.getFiler().createSourceFile("com.mobgen.halo.content.generated.HaloTableName");
            Writer writer = source.openWriter();
            writer.write(builder.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            // Note: calling e.printStackTrace() will print IO errors
            // that occur from the file already existing after its first run, this is normal
        }


        return true;
    }
}
