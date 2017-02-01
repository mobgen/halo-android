package com.mobgen.halo.android.content.processor;

import com.google.auto.service.AutoService;
import com.mobgen.halo.android.content.annotations.HaloField;
import com.mobgen.halo.android.content.annotations.HaloQuery;
import com.mobgen.halo.android.content.annotations.HaloSearchable;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
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
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;

import static javax.lang.model.element.ElementKind.FIELD;

@AutoService(Processor.class)
@SupportedAnnotationTypes({"com.mobgen.halo.android.content.annotations.HaloQuery",
        "com.mobgen.halo.android.content.annotations.HaloField",
        "com.mobgen.halo.android.content.annotations.HaloSearchable"})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class HaloContentDatabaseProcessor extends AbstractProcessor {

    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;
    private Messager messager;
    private List<String> databaseTables = new ArrayList<String>();
    private List<Integer> databaseVersion = new ArrayList<>();

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
        annotataions.add(HaloQuery.class.getCanonicalName());
        annotataions.add(HaloField.class.getCanonicalName());
        annotataions.add(HaloSearchable.class.getCanonicalName());
        return annotataions;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }


    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        generateHaloQueryClass(roundEnvironment);
        //add all annotated query elements
        for (Element element : roundEnvironment.getElementsAnnotatedWith(HaloSearchable.class)) {
            databaseTables.add(generateHaloSearchableClass(element));
            databaseVersion.add(element.getAnnotation(HaloSearchable.class).version());
        }
        for (Element element : roundEnvironment.getElementsAnnotatedWith(HaloField.class)) {
           // databaseFields.add(generateHaloSearchableClass(element));
            System.err.println(element.getSimpleName().toString());
        }
        if(databaseTables.size()>0) {
            generateHaloContentTable();
            generateDatabaseMigration(roundEnvironment,1);
        }
        return true;
    }

    /**
     * Generate Halo query class with all the HaloQuery annotation
     *
     * @param roundEnvironment
     */
    private void generateHaloQueryClass(RoundEnvironment roundEnvironment){
        JavaFile javaFile=null;
        String className = "HaloContentQueryApi";
        ClassName halo = ClassName.get("com.mobgen.halo.android.sdk.api","Halo");
        ClassName haloPulgin = ClassName.get("com.mobgen.halo.android.sdk.api","HaloPluginApi");
        ClassName thisClass = ClassName.get("com.mobgen.halo.android.app.generated","HaloContentQueryApi");

        ClassName interactor = ClassName.get("com.mobgen.halo.android.content.generated","GeneratedContentQueriesInteractor");
        ClassName repository = ClassName.get("com.mobgen.halo.android.content.generated","GeneratedContentQueriesRepository");
        ClassName datasource = ClassName.get("com.mobgen.halo.android.content.generated","GeneratedContentQueriesLocalDataSource");

        TypeSpec.Builder queryClassBuilder = TypeSpec.classBuilder(className);
        queryClassBuilder.addModifiers(Modifier.PUBLIC);
        queryClassBuilder.superclass(haloPulgin);
        queryClassBuilder.addJavadoc("This class was autogenerated to perfom queries.");


        //prepare method
        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                .addJavadoc("Internal private constructor for the halo plugin.")
                .addModifiers(Modifier.PRIVATE)
                .addParameter(halo,"halo")
                .addStatement("super(halo)");

        queryClassBuilder.addMethod(constructorBuilder.build());

        MethodSpec.Builder withMethod = MethodSpec.methodBuilder("with")
                .addJavadoc("Creates the Content Query Api instance with the reference to a yet created HALO instance")
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.STATIC)
                .addParameter(halo,"halo")
                .returns(thisClass)
                .addStatement("return new $T(halo)",thisClass);

        queryClassBuilder.addMethod(withMethod.build());

        ClassName checkResult = ClassName.get("android.support.annotation","CheckResult");
        AnnotationSpec checkResultAnnontation = AnnotationSpec.builder(checkResult)
                .addMember("suggest","\"You may want to call execute() to run the task\"")
                .build();

        //add all annotated query elements
        for (Element element : roundEnvironment.getElementsAnnotatedWith(HaloQuery.class)) {

            String query = element.getAnnotation(HaloQuery.class).query();
            String methodName = element.getAnnotation(HaloQuery.class).name();

            ClassName haloInteractor = ClassName.get("com.mobgen.halo.android.sdk.core.threading","HaloInteractorExecutor");
            ClassName cursor = ClassName.get("android.database","Cursor");

            //prepare statements
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(methodName)
                    .addJavadoc("Query by codegen: "+ query)
                    .addAnnotation(checkResultAnnontation)
                    .addModifiers(Modifier.PUBLIC);

            List<String> paramNames = new ArrayList<>();
            //create the query method
            for(int i=0;i<query.length();i++){
                if(query.charAt(i) == '@'){
                    i++;//skip { char
                    String param = query.substring(i+1);
                    paramNames.add(param.substring(0,param.indexOf(":")));
                    methodBuilder.addParameter(resolveDataType(param.substring(param.indexOf(":")+1,param.indexOf("}"))),param.substring(0,param.indexOf(":")));
                    query = query.replace("@{" + param.substring(0,param.indexOf("}")+1),"?");
                }
            }
            //return statement of the query method
            methodBuilder.addStatement("String query = $S",query);
            methodBuilder.addStatement("Object[] bindArgs = new Object[$L]", paramNames.size());
            for(int j=0;j<paramNames.size();j++) {
                methodBuilder.addStatement("bindArgs[$L]=$L", j, paramNames.get(j));
            }
            methodBuilder.returns(ParameterizedTypeName.get(haloInteractor, cursor))
                    .addStatement("return new HaloInteractorExecutor<$4T>(halo()," +
                            "\"Generated field query\"," +
                            "new $1T(new $2T(new $3T(halo().framework())),query,bindArgs)" +
                            ")",interactor,repository,datasource,cursor);

            MethodSpec queryDatabase = methodBuilder.build();
            queryClassBuilder.addMethod(queryDatabase);
        }

        TypeSpec queryClass = queryClassBuilder.build();

        javaFile = JavaFile.builder("com.mobgen.halo.android.app.generated", queryClass)
                .build();

        try {
            JavaFileObject source = processingEnv.getFiler().createSourceFile("com.mobgen.halo.android.app.generated." + className);
            Writer writer = source.openWriter();
            writer.write(javaFile.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create a class per model annotated with HaloSearchable annotation
     * @param element
     * @return
     */
    private String generateHaloSearchableClass(Element element){
        JavaFile javaFile=null;
        String className = "HaloTable$$" + element.getSimpleName();
        String tableAnnotationName = "HALO_GC_" + element.getSimpleName().toString().toUpperCase();

        TypeSpec.Builder queryClassBuilder = TypeSpec.classBuilder(className);
        queryClassBuilder.addModifiers(Modifier.PUBLIC);
        queryClassBuilder.addJavadoc("This class was autogenerated to perfom queries.");

        ClassName keepClass = ClassName.get("android.support.annotation", "Keep");
        ClassName tableAnnotationClass = ClassName.get("com.mobgen.halo.android.framework.storage.database.dsl.annotations", "Table");
        ClassName tableClass = ClassName.get(" com.mobgen.halo.android.framework.storage.database.dsl", "HaloTable");
        ClassName columnClass = ClassName.get("com.mobgen.halo.android.framework.storage.database.dsl.annotations", "Column");

        AnnotationSpec tableAnnotation = AnnotationSpec.builder(tableAnnotationClass)
                .addMember("value","$S",tableAnnotationName)
                .build();

        AnnotationSpec columnAnnotationID = AnnotationSpec.builder(columnClass)
                .addMember("type", "$L", "Column.Type.NUMERIC")
                .addMember("isPrimaryKey", "$L", true)
                .build();

        //create table
        TypeSpec.Builder consturctorBuilder = TypeSpec.interfaceBuilder(className)
                .addJavadoc("Constructor of the class to create table")
                .addSuperinterface(tableClass)
                .addAnnotation(keepClass)
                .addAnnotation(tableAnnotation)
                .addModifiers(Modifier.PUBLIC);

        consturctorBuilder.addField(FieldSpec.builder(String.class, "ID")
                .addAnnotation(keepClass)
                .addAnnotation(columnAnnotationID)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("$S", "GC_ID")
                .build());

        //prepare all fields to convert to columns on halotable
        for(int k=0;k<element.getEnclosedElements().size();k++){
            FieldSpec fieldSpec = setHaloTableFields(element.getEnclosedElements().get(k));
            if(fieldSpec!=null){
                consturctorBuilder.addField(fieldSpec);
            }
        }
        int version = element.getAnnotation(HaloSearchable.class).version();

        TypeSpec queryClass = consturctorBuilder.build();

        javaFile = JavaFile.builder("com.mobgen.halo.android.app.generated", queryClass)
                .build();

        try {
            JavaFileObject source = processingEnv.getFiler().createSourceFile("com.mobgen.halo.android.app.generated." + className);
            Writer writer = source.openWriter();
            writer.write(javaFile.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return element.getSimpleName().toString();
    }

    /**
     * Generates a shared table to store the versions of the content tables
     * @return
     */
    private String generateHaloContentTable(){
        JavaFile javaFile=null;
        String className = "HaloTable$$ContentVersion";

        TypeSpec.Builder queryClassBuilder = TypeSpec.classBuilder(className);
        queryClassBuilder.addModifiers(Modifier.PUBLIC);
        queryClassBuilder.addJavadoc("This class was autogenerated to perfom queries.");

        ClassName keepClass = ClassName.get("android.support.annotation", "Keep");
        ClassName tableAnnotationClass = ClassName.get("com.mobgen.halo.android.framework.storage.database.dsl.annotations", "Table");
        ClassName tableClass = ClassName.get(" com.mobgen.halo.android.framework.storage.database.dsl", "HaloTable");
        ClassName columnClass = ClassName.get("com.mobgen.halo.android.framework.storage.database.dsl.annotations", "Column");

        AnnotationSpec tableAnnotation = AnnotationSpec.builder(tableAnnotationClass)
                .addMember("value","$S", "HALO_GC_CONTENT_VERSION")
                .build();
        AnnotationSpec columnAnnotation = AnnotationSpec.builder(columnClass)
                .addMember("type", "$L", "Column.Type.TEXT")
                .build();
        AnnotationSpec columnAnnotationVersion = AnnotationSpec.builder(columnClass)
                .addMember("type", "$L", "Column.Type.NUMERIC")
                .build();
        AnnotationSpec columnAnnotationID = AnnotationSpec.builder(columnClass)
                .addMember("type", "$L", "Column.Type.NUMERIC")
                .addMember("isPrimaryKey", "$L", true)
                .build();
        //create table
        TypeSpec.Builder consturctorBuilder = TypeSpec.interfaceBuilder(className)
                .addJavadoc("Constructor of the class to create table")
                .addSuperinterface(tableClass)
                .addAnnotation(keepClass)
                .addAnnotation(tableAnnotation)
                .addModifiers(Modifier.PUBLIC)
                .addField(FieldSpec.builder(String.class, "TABLE_ID")
                        .addAnnotation(keepClass)
                        .addAnnotation(columnAnnotationID)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                        .initializer("$S", "GCS_TABLE_ID")
                        .build())
                .addField(FieldSpec.builder(String.class, "TABLE_NAME")
                        .addAnnotation(keepClass)
                        .addAnnotation(columnAnnotation)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                        .initializer("$S", "GCS_TABLE_NAME")
                        .build())
                .addField(FieldSpec.builder(String.class, "TABLE_VERSION")
                        .addAnnotation(keepClass)
                        .addAnnotation(columnAnnotationVersion)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                        .initializer("$S", "GCS_TABLE_VERSION")
                        .build());

        TypeSpec queryClass = consturctorBuilder.build();

        javaFile = JavaFile.builder("com.mobgen.halo.android.app.generated", queryClass)
                .build();

        try {
            JavaFileObject source = processingEnv.getFiler().createSourceFile("com.mobgen.halo.android.app.generated." + className);
            Writer writer = source.openWriter();
            writer.write(javaFile.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "HALO_GC_CONTENT_VERSION";
    }

    /**
     * Generate the database migration to create tables on database
     * @param roundEnvironment
     * @param version
     */
    private void generateDatabaseMigration(RoundEnvironment roundEnvironment,int version){


        JavaFile javaFile=null;
        String className = "HaloDataBase$$GeneratedMigration";

        ClassName generatedHaloDatabase = ClassName.get("com.mobgen.halo.android.content.generated", "GeneratedHaloDatabase");

        TypeSpec.Builder queryClassBuilder = TypeSpec.classBuilder(className);
        queryClassBuilder.addJavadoc("Database migration for autogenerated tables.");
        queryClassBuilder.addModifiers(Modifier.PUBLIC);
        queryClassBuilder.addSuperinterface(generatedHaloDatabase);

        FieldSpec versionField = FieldSpec.builder(int.class,"VERSION",Modifier.PUBLIC,Modifier.STATIC,Modifier.FINAL)
                .initializer("$L",version)
                .build();
        queryClassBuilder.addField(versionField);

        ClassName sqLite = ClassName.get("android.database.sqlite", "SQLiteDatabase");
        ClassName cursor = ClassName.get("android.database","Cursor");
        MethodSpec.Builder updateDatabaseBuilder = MethodSpec.methodBuilder("updateDatabase")
                .addAnnotation(Override.class)
                .returns(void.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(sqLite,"database");

        ClassName ormUtils = ClassName.get("com.mobgen.halo.android.framework.storage.database.dsl", "ORMUtils");
        ClassName createQuery = ClassName.get("com.mobgen.halo.android.framework.storage.database.dsl.queries", "Create");
        ClassName selectQuery = ClassName.get("com.mobgen.halo.android.framework.storage.database.dsl.queries", "Select");
        ClassName dropQuery = ClassName.get("com.mobgen.halo.android.framework.storage.database.dsl.queries", "Drop");
        ClassName contentValues = ClassName.get("android.content", "ContentValues");

        generateHaloContentTable();
        updateDatabaseBuilder.addCode("$1T.table($2L.class).on(database, \"Creates the HaloTable$$ContentVersion table from codegen\");\n",createQuery,"HaloTable$$ContentVersion");

        //create each table on dabase
        for(int i=0; i<databaseTables.size();i++){
            String classNameTable = "HaloTable$$" + databaseTables.get(i);
            String tableName = "HALO_GC_" + databaseTables.get(i).toUpperCase();
            int index = i+1;
            //Select version from ContentVersion table
            updateDatabaseBuilder.addCode("int version$1L=$2L;\n",index,databaseVersion.get(i));
            updateDatabaseBuilder.addCode("$3T result$5L = $1T.columns($2L.TABLE_VERSION)" +
                    ".from($2L.class)" +
                    ".where($2L.TABLE_NAME)" +
                    ".eq($4S)" +
                    ".on(database,\"Select version from $2L table from codegen\");\n",selectQuery,"HaloTable$$ContentVersion",cursor,tableName,index);
            //check current version and stored version to update database content version
            updateDatabaseBuilder.addCode("if(result$4L.moveToFirst()){\n" +
                    "\tversion$1L = result$4L.getInt(result$4L.getColumnIndex($2L.TABLE_VERSION));\n" +
                    "\tif(version$1L<$3L){\n" +
                   // "\t\tversion$1L =$3L;\n"+
                    "\t\t$5T.table($6L.class).on(database,\"Drop table of type $6L due to new version from codegen\");\n"+
                    "\t}\n"+
                    "}\n",index,"HaloTable$$ContentVersion",databaseVersion.get(i),index,dropQuery,classNameTable);
            updateDatabaseBuilder.addCode("result$1L.close();\n",index);
            updateDatabaseBuilder.addCode("$1T.table($2L.class).on(database, \"Creates the $2L table from codegen\");\n",createQuery,classNameTable);
            updateDatabaseBuilder.addCode("$1T values$2L = new $1T();\n" +
                    "values$2L.put($4L.TABLE_ID, $2L);\n" +
                    "values$2L.put($4L.TABLE_NAME, $3S);\n" +
                    "values$2L.put($4L.TABLE_VERSION,version$2L);\n" +
                    "database.insertWithOnConflict($5S,null,values$2L,SQLiteDatabase.CONFLICT_REPLACE);\n",
                    contentValues,index,tableName,"HaloTable$$ContentVersion","HALO_GC_CONTENT_VERSION");
            updateDatabaseBuilder.addCode("//End of the $1L database table\n",index);
        }

        queryClassBuilder.addMethod(updateDatabaseBuilder.build());

        MethodSpec getDatabaseVersion = MethodSpec.methodBuilder("getDatabaseVersion")
                .addAnnotation(Override.class)
                .returns(int.class)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return VERSION")
                .build();

        queryClassBuilder.addMethod(getDatabaseVersion);

        TypeSpec queryClass = queryClassBuilder.build();

        javaFile = JavaFile.builder("com.mobgen.halo.android.app.generated", queryClass)
                .build();

        try {
            JavaFileObject source = processingEnv.getFiler().createSourceFile("com.mobgen.halo.android.app.generated." + className);
            Writer writer = source.openWriter();
            writer.write(javaFile.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Set the halo table annotation
     * @param element
     * @return
     */
    private FieldSpec setHaloTableFields(Element element){
        ClassName keepClass = ClassName.get("android.support.annotation", "Keep");
        if(element.getKind()==FIELD){
            AnnotationSpec annotationSpec =  resolveAnnotations(((VariableElement) element).asType());
            if( annotationSpec!=null) {
                return FieldSpec.builder(String.class, "C_" + element.toString().toUpperCase())
                        .addAnnotation(keepClass)
                        .addAnnotation(annotationSpec)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                        .initializer("$S", "GC_" + element.toString().toUpperCase())
                        .build();
            }
        }
        return null;
    }

    /**
     * Get the annotation with data type on HaloTable
     * @param typeMirror
     * @return
     */
    private AnnotationSpec resolveAnnotations(TypeMirror typeMirror){
        ClassName columnClass = ClassName.get("com.mobgen.halo.android.framework.storage.database.dsl.annotations", "Column");
        if(typeUtils.asElement(typeMirror).getSimpleName().toString().equals("String")){
            return  AnnotationSpec.builder(columnClass)
                    .addMember("type", "$L", "Column.Type.TEXT")
                    .build();
        } else if(typeUtils.asElement(typeMirror).getSimpleName().toString().equals("int")){
            return  AnnotationSpec.builder(columnClass)
                    .addMember("type", "$L", "Column.Type.NUMERIC")
                    .build();
        } else if(typeUtils.asElement(typeMirror).getSimpleName().toString().equals("Date")){
            return  AnnotationSpec.builder(columnClass)
                    .addMember("type", "$L", "Column.Type.DATE")
                    .build();
        }
        return  null;//not supported this type
    }

    /**
     * Get the current data type
     *
     * @param type
     * @return
     */
    private Class resolveDataType(String type) {
        if(type.equals("String")){
            return String.class;
        } else if(type.equals("Integer")){
            return Integer.class;
        } else if(type.equals("Date")){
            return Date.class;
        } else {
            return Object.class;
        }
    }
}
