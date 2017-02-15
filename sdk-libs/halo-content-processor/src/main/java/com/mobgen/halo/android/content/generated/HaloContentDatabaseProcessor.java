package com.mobgen.halo.android.content.generated;

import com.google.auto.service.AutoService;
import com.mobgen.halo.android.content.annotations.HaloConstructor;
import com.mobgen.halo.android.content.annotations.HaloField;
import com.mobgen.halo.android.content.annotations.HaloQueries;
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
import java.util.HashMap;
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
        "com.mobgen.halo.android.content.annotations.HaloQueries",
        "com.mobgen.halo.android.content.annotations.HaloField",
        "com.mobgen.halo.android.content.annotations.HaloSearchable",
        "com.mobgen.halo.android.content.annotations.HaloConstructor"})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class HaloContentDatabaseProcessor extends AbstractProcessor {

    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;
    private Messager messager;
    private List<String> databaseTables = new ArrayList<String>();
    private List<Integer> databaseVersion = new ArrayList<>();
    private List<Element> constructorElements =  new ArrayList<>();
    private List<Element> queriesElments = new ArrayList<>();
    private HashMap<String, List<String>> indexableFields = new HashMap<>();

    //CONSTANTS
    private String PACKAGE_GENERATED = "com.mobgen.halo.android.app.generated";
    private String CLASS_NAME_GENERATED = "com.mobgen.halo.android.app.generated.";

    //classes
    private ClassName halo = ClassName.get("com.mobgen.halo.android.sdk.api","Halo");
    private ClassName haloPulgin = ClassName.get("com.mobgen.halo.android.sdk.api","HaloPluginApi");
    private ClassName thisClass = ClassName.get(PACKAGE_GENERATED,"HaloContentQueryApi");
    private ClassName keepClass = ClassName.get("android.support.annotation", "Keep");
    private ClassName dateClass = ClassName.get("java.util","Date");
    private ClassName tableAnnotationClass = ClassName.get("com.mobgen.halo.android.framework.storage.database.dsl.annotations", "Table");
    private ClassName tableClass = ClassName.get(" com.mobgen.halo.android.framework.storage.database.dsl", "HaloTable");
    private ClassName sqLite = ClassName.get("android.database.sqlite", "SQLiteDatabase");
    private ClassName columnClass = ClassName.get("com.mobgen.halo.android.framework.storage.database.dsl.annotations", "Column");
    private ClassName generatedHaloDatabase = ClassName.get("com.mobgen.halo.android.content.generated", "GeneratedHaloDatabase");
    private ClassName createQuery = ClassName.get("com.mobgen.halo.android.framework.storage.database.dsl.queries", "Create");
    private ClassName selectQuery = ClassName.get("com.mobgen.halo.android.framework.storage.database.dsl.queries", "Select");
    private ClassName dropQuery = ClassName.get("com.mobgen.halo.android.framework.storage.database.dsl.queries", "Drop");
    private ClassName contentValues = ClassName.get("android.content", "ContentValues");
    private ClassName contentSelectorFactory = ClassName.get("com.mobgen.halo.android.content.selectors","HaloContentSelectorFactory");
    private ClassName cursor = ClassName.get("android.database","Cursor");
    private ClassName paginated = ClassName.get("com.mobgen.halo.android.content.models","Paginated");
    private ClassName generatedContentQueriesInteractorInstance = ClassName.get("com.mobgen.halo.android.content.generated","GeneratedContentQueriesInteractor");
    private ClassName generatedContentQueriesRepositoryInstance = ClassName.get("com.mobgen.halo.android.content.generated","GeneratedContentQueriesRepository");
    private ClassName generatedContentQueriesLocalDataSource = ClassName.get("com.mobgen.halo.android.content.generated","GeneratedContentQueriesLocalDataSource");
    private ClassName cursor2ContentInstanceGeneratedModelConverter = ClassName.get("com.mobgen.halo.android.content.generated","Cursor2ContentInstanceGeneratedModelConverter");
    private ClassName cursor2GeneratedModelClassConverterFactory = ClassName.get("com.mobgen.halo.android.content.generated","Cursor2GeneratedModelClassConverterFactory");
    private ClassName storageData = ClassName.get("com.mobgen.halo.android.framework.toolbox.data","Data");
    private ClassName checkResult = ClassName.get("android.support.annotation","CheckResult");

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
        annotataions.add(HaloQueries.class.getCanonicalName());
        annotataions.add(HaloField.class.getCanonicalName());
        annotataions.add(HaloSearchable.class.getCanonicalName());
        annotataions.add(HaloConstructor.class.getCanonicalName());
        return annotataions;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }


    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        for (Element query : roundEnvironment.getElementsAnnotatedWith(HaloQueries.class)) {
            queriesElments.add(query);
        }
        generateHaloQueryClass();

        //add all constructor to dabase
        for (Element constructor : roundEnvironment.getElementsAnnotatedWith(HaloConstructor.class)) {
            constructorElements.add(constructor);
        }
        //add all annotated query elements
        int haloConstructorIndex = 0;
        for (Element element : roundEnvironment.getElementsAnnotatedWith(HaloSearchable.class)) {
            String haloTableName = generateHaloSearchableClass(element,constructorElements.get(haloConstructorIndex));
            databaseTables.add(haloTableName);
            //get index fields
            List<String> indexFields = new ArrayList<>();
            for(Element encloseElements : element.getEnclosedElements()){
                if(encloseElements.getAnnotation(HaloField.class)!=null && encloseElements.getAnnotation(HaloField.class).index()) {
                    indexFields.add(encloseElements.getAnnotation(HaloField.class).columnName());
                }
            }
            indexableFields.put(haloTableName,indexFields);
            databaseVersion.add(element.getAnnotation(HaloSearchable.class).version());
            haloConstructorIndex++;
        }
        //generate version table and database generated model creation
        if(databaseTables.size()>0) {
            generateHaloContentVersionTable();
            generateDatabaseMigration(roundEnvironment,1);
        }
        return true;
    }

    /**
     * Generate Halo query class with all the HaloQuery annotation
     *
     */
    private void generateHaloQueryClass(){
        String className = "HaloContentQueryApi";

        TypeSpec.Builder contentQueryBuilder = TypeSpec.classBuilder(className);
        contentQueryBuilder.addModifiers(Modifier.PUBLIC);
        contentQueryBuilder.superclass(haloPulgin);
        contentQueryBuilder.addJavadoc("This class was autogenerated to perfom queries.");

        //prepare method
        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                .addJavadoc("Internal private constructor for the halo plugin.")
                .addModifiers(Modifier.PRIVATE)
                .addParameter(halo,"halo")
                .addStatement("super(halo)");

        contentQueryBuilder.addMethod(constructorBuilder.build());

        MethodSpec.Builder withMethod = MethodSpec.methodBuilder("with")
                .addJavadoc("Creates the Content Query Api instance with the reference to a yet created HALO instance")
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.STATIC)
                .addParameter(halo,"halo")
                .returns(thisClass)
                .addStatement("return new $T(halo)",thisClass);

        contentQueryBuilder.addMethod(withMethod.build());

        AnnotationSpec checkResultAnnontation = AnnotationSpec.builder(checkResult)
                .addMember("suggest","\"You may want to call execute() to run the task\"")
                .build();

        //add all annotated query elements for all elements with halo queries
        for(int w=0;w<queriesElments.size();w++) {
            HaloQuery[] queries = queriesElments.get(w).getAnnotation(HaloQueries.class).queries();
            for (int q = 0; q < queries.length; q++) {

                String query = queries[q].query();
                String methodName = queries[q].name();

                ClassName modelClass = ClassName.get(getPackageName(queriesElments.get(w)), queriesElments.get(w).getSimpleName().toString());
                //prepare statements
                MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(methodName)
                        .addJavadoc("Query by codegen: " + query)
                        .addAnnotation(checkResultAnnontation)
                        .addModifiers(Modifier.PUBLIC);

                List<String> paramNames = new ArrayList<>();
                //create the query method
                for (int i = 0; i < query.length(); i++) {
                    if (query.charAt(i) == '@') {
                        i++;//skip { char
                        String param = query.substring(i + 1);
                        paramNames.add(param.substring(0, param.indexOf(":")));
                        methodBuilder.addParameter(resolveDataType(param.substring(param.indexOf(":") + 1, param.indexOf("}"))), param.substring(0, param.indexOf(":")));
                        query = query.replace("@{" + param.substring(0, param.indexOf("}") + 1), "?");
                    }
                }
                //return statement of the query method
                methodBuilder.addStatement("String query = $S", query);
                methodBuilder.addStatement("Object[] bindArgs = new Object[$L]", paramNames.size());
                for (int j = 0; j < paramNames.size(); j++) {
                    methodBuilder.addStatement("bindArgs[$L]=$L", j, paramNames.get(j));
                }

                methodBuilder.returns(ParameterizedTypeName.get(contentSelectorFactory, ParameterizedTypeName.get(paginated, modelClass), cursor))
                        .addStatement("return new HaloContentSelectorFactory<>(\n" +
                                        "\thalo(),\n" +
                                        "\tnew $1T(new $2T(new $3T(halo().framework())),query,bindArgs),\n" +
                                        "\tnew $4T($5T.class),\n" +
                                        "\tnew $6T(halo().framework().parser()),\n" +
                                        "\tnull,\n" +
                                        "\t$7T.STORAGE_ONLY,\n" +
                                        "\t\"generatedModelQuery with method $8L\")", generatedContentQueriesInteractorInstance, generatedContentQueriesRepositoryInstance,
                                generatedContentQueriesLocalDataSource, cursor2ContentInstanceGeneratedModelConverter,
                                modelClass, cursor2GeneratedModelClassConverterFactory, storageData, methodName);

                MethodSpec queryDatabase = methodBuilder.build();
                contentQueryBuilder.addMethod(queryDatabase);
            }
        }
        TypeSpec contentQueryClass = contentQueryBuilder.build();
        generatedJavaFile(className, contentQueryClass);
    }

    /**
     * Create a class per model annotated with HaloSearchable annotation
     * @param element Element to generate halo database table.
     * @param constructorElement Constructor model annotation with column names.
     * @return The database table name.
     */
    private String generateHaloSearchableClass(Element element, Element constructorElement){
        String className = "HaloTable$$" + element.getAnnotation(HaloSearchable.class).tableName();
        String tableAnnotationName = element.getAnnotation(HaloSearchable.class).tableName();

        TypeSpec.Builder tableClassBuilder = TypeSpec.classBuilder(className);
        tableClassBuilder.addModifiers(Modifier.PUBLIC);
        tableClassBuilder.addJavadoc("This class was autogenerated to perfom queries.");

        AnnotationSpec tableAnnotation = AnnotationSpec.builder(tableAnnotationClass)
                .addMember("value","$S",tableAnnotationName)
                .build();

        //create table
        TypeSpec.Builder constructorBuilder = TypeSpec.interfaceBuilder(className)
                .addJavadoc("Constructor of the class to create table")
                .addSuperinterface(tableClass)
                .addAnnotation(keepClass)
                .addAnnotation(tableAnnotation)
                .addModifiers(Modifier.PUBLIC);

        //prepare all fields to convert to columns on halotable
        int haloConstructorFieldIndex = 0;
        for(int k=0;k<element.getEnclosedElements().size();k++){
            FieldSpec fieldSpec = setHaloTableFields(element.getEnclosedElements().get(k), constructorElement, haloConstructorFieldIndex);
            if(fieldSpec!=null){
                constructorBuilder.addField(fieldSpec);
                haloConstructorFieldIndex++;
            }
        }
        TypeSpec tableClass = constructorBuilder.build();
        generatedJavaFile(className, tableClass);
        return element.getAnnotation(HaloSearchable.class).tableName();
    }

    /**
     * Generates a shared table to store the versions of the content tables
     * @return The name of the database table.
     */
    private String generateHaloContentVersionTable(){
        String className = "HaloTable$$ContentVersion";

        TypeSpec.Builder versionClassBuilder = TypeSpec.classBuilder(className);
        versionClassBuilder.addModifiers(Modifier.PUBLIC);
        versionClassBuilder.addJavadoc("This class was autogenerated to perfom queries.");

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
        AnnotationSpec columnAnnotationDate = AnnotationSpec.builder(columnClass)
                .addMember("type", "$L", "Column.Type.DATE")
                .build();
        //create table
        TypeSpec.Builder constructorBuilder = TypeSpec.interfaceBuilder(className)
                .addJavadoc("Constructor of the class to create table")
                .addSuperinterface(tableClass)
                .addAnnotation(keepClass)
                .addAnnotation(tableAnnotation)
                .addModifiers(Modifier.PUBLIC)
                .addField(FieldSpec.builder(String.class, "TABLE_ID")
                        .addAnnotation(keepClass)
                        .addAnnotation(columnAnnotationID)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                        .initializer("$S", "GC_TABLE_ID")
                        .build())
                .addField(FieldSpec.builder(String.class, "TABLE_NAME")
                        .addAnnotation(keepClass)
                        .addAnnotation(columnAnnotation)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                        .initializer("$S", "GC_TABLE_NAME")
                        .build())
                .addField(FieldSpec.builder(String.class, "TABLE_VERSION")
                        .addAnnotation(keepClass)
                        .addAnnotation(columnAnnotationVersion)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                        .initializer("$S", "GC_TABLE_VERSION")
                        .build())
                .addField(FieldSpec.builder(String.class, "TABLE_DATE")
                        .addAnnotation(keepClass)
                        .addAnnotation(columnAnnotationDate)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                        .initializer("$S", "GC_TABLE_DATE")
                        .build());

        TypeSpec versionClass = constructorBuilder.build();
        generatedJavaFile(className, versionClass);
        return "HALO_GC_CONTENT_VERSION";
    }

    /**
     * Generate the database migration to create tables on database
     * @param roundEnvironment
     * @param version
     */
    private void generateDatabaseMigration(RoundEnvironment roundEnvironment,int version){
        String className = "GeneratedDatabaseFromModel";
        TypeSpec.Builder generatedDatabaseClassBuilder = TypeSpec.classBuilder(className);
        generatedDatabaseClassBuilder.addJavadoc("Database migration for autogenerated tables.");
        generatedDatabaseClassBuilder.addModifiers(Modifier.PUBLIC);
        generatedDatabaseClassBuilder.addSuperinterface(generatedHaloDatabase);

        MethodSpec.Builder updateDatabaseBuilder = MethodSpec.methodBuilder("updateDatabaseWithAutoGeneratedModels")
                .addAnnotation(Override.class)
                .returns(void.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(sqLite,"database");
        updateDatabaseBuilder.addCode("$1T.table($2L.class).on(database, \"Creates the HaloTable$$ContentVersion table from codegen\");\n",createQuery,"HaloTable$$ContentVersion");

        //create each table on dabase
        for(int i=0; i<databaseTables.size();i++){
            String classNameTable = "HaloTable$$" + databaseTables.get(i);
            String tableName = databaseTables.get(i).toUpperCase();
            int index = i+1;
            //Select version from ContentVersion table
            updateDatabaseBuilder.addCode("int version$1L = $2L;\n",index,databaseVersion.get(i));
            updateDatabaseBuilder.addCode("$3T result$5L = $1T.columns(\"MAX(\"+ $2L.TABLE_VERSION + \") as \" + $2L.TABLE_VERSION)" +
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
            //get max id from shared table
            updateDatabaseBuilder.addCode("int id$1L = 1;\n",index);
            updateDatabaseBuilder.addCode("$3T identifier$4L = $1T.columns(\"MAX(\"+ $2L.TABLE_ID + \") as \" + $2L.TABLE_ID)" +
                    ".from($2L.class)" +
                    ".on(database,\"Select max id from $2L table from codegen\");\n",selectQuery,"HaloTable$$ContentVersion",cursor,index);
            updateDatabaseBuilder.addCode("if(identifier$1L.moveToFirst()){\n" +
                    "\tid$1L = identifier$1L.getInt(identifier$1L.getColumnIndex($2L.TABLE_ID)) + 1;\n" +
                    "}\n",index,"HaloTable$$ContentVersion");
            updateDatabaseBuilder.addCode("identifier$1L.close();\n",index);
            //insert shared table statement
            updateDatabaseBuilder.addCode("$1T.table($2L.class).on(database, \"Creates the $2L table from codegen\");\n",createQuery,classNameTable);
            //set index
            List<String> indexFields = indexableFields.get(databaseTables.get(i));
            for(int j=0;j<indexFields.size();j++) {
                String indexName = indexFields.get(j) + "index";
                updateDatabaseBuilder.addCode("$1T.index($2L.class,$3S,new String[]{$4S}).on(database, \"Creates index into the $2L table from codegen\");\n",createQuery,classNameTable,indexName, indexFields.get(j));
            }
            updateDatabaseBuilder.addCode("$1T now$2L = new $1T();\n",dateClass,index);
            updateDatabaseBuilder.addCode("$1T values$2L = new $1T();\n" +
                            "values$2L.put($4L.TABLE_ID, id$2L);\n" +
                            "values$2L.put($4L.TABLE_NAME, $3S);\n" +
                            "values$2L.put($4L.TABLE_VERSION,$6L);\n" +
                            "values$2L.put($4L.TABLE_DATE, now$2L.getTime());\n" +
                            "if (version$2L!= $6L) {\n" +
                            "\tdatabase.insertWithOnConflict($5S,null,values$2L,SQLiteDatabase.CONFLICT_REPLACE);\n" +
                            "}\n",
                    contentValues,index,tableName,"HaloTable$$ContentVersion","HALO_GC_CONTENT_VERSION",databaseVersion.get(i));
            updateDatabaseBuilder.addCode("//End of the $1L database table\n",index);
        }
        generatedDatabaseClassBuilder.addMethod(updateDatabaseBuilder.build());
        TypeSpec generatedDatabaseClass = generatedDatabaseClassBuilder.build();
        generatedJavaFile(className, generatedDatabaseClass);
    }

    /**
     * Generate the java file
     * @param className The class name
     * @param classSpec The type spec generated
     */
    private void generatedJavaFile(String className, TypeSpec classSpec) {
        JavaFile javaFile=null;
        javaFile = JavaFile.builder(PACKAGE_GENERATED, classSpec)
                .build();

        try {
            JavaFileObject source = processingEnv.getFiler().createSourceFile(CLASS_NAME_GENERATED + className);
            Writer writer = source.openWriter();
            writer.write(javaFile.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get package name of a element
     *
     * @param element the element which is annotated
     * @return The package name as string
     */
    private String getPackageName(Element element) {
        String packageName = "";
        boolean moreEnclosingElements = true;
        Element recursiveElement = element;
        while(moreEnclosingElements){
            if(recursiveElement.getEnclosingElement()!=null) {
                packageName = packageName + recursiveElement.getEnclosingElement();
                recursiveElement = recursiveElement.getEnclosingElement();
            } else {
                moreEnclosingElements = false;
            }
        }
        return packageName;
    }

    /**
     * Set the halo table annotation
     * @param element
     * @return
     */
    private FieldSpec setHaloTableFields(Element element, Element constructor, int index){
        if(element.getKind()==FIELD){
            AnnotationSpec annotationSpec =  resolveAnnotations(((VariableElement) element).asType());
            if(annotationSpec!=null && constructor.getAnnotation(HaloConstructor.class)!= null && index < constructor.getAnnotation(HaloConstructor.class).columnNames().length) {
                return FieldSpec.builder(String.class, constructor.getAnnotation(HaloConstructor.class).columnNames()[index])
                        .addAnnotation(keepClass)
                        .addAnnotation(annotationSpec)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                        .initializer("$S",  constructor.getAnnotation(HaloConstructor.class).columnNames()[index])
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
        if(typeUtils.asElement(typeMirror).getSimpleName().toString().equals("String")){
            return  AnnotationSpec.builder(columnClass)
                    .addMember("type", "$L", "Column.Type.TEXT")
                    .build();
        } else if(typeUtils.asElement(typeMirror).getSimpleName().toString().equals("Integer")){
            return  AnnotationSpec.builder(columnClass)
                    .addMember("type", "$L", "Column.Type.NUMERIC")
                    .build();
        } else if(typeUtils.asElement(typeMirror).getSimpleName().toString().equals("Date")){
            return  AnnotationSpec.builder(columnClass)
                    .addMember("type", "$L", "Column.Type.DATE")
                    .build();
        } else if(typeUtils.asElement(typeMirror).getSimpleName().toString().equals("Boolean")){
            return  AnnotationSpec.builder(columnClass)
                    .addMember("type", "$L", "Column.Type.BOOLEAN")
                    .build();
        } else {
            return  AnnotationSpec.builder(columnClass)
                    .addMember("type", "$L", "Column.Type.TEXT")
                    .build();
        }
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
        } else if(type.equals("Boolean")){
            return Boolean.class;
        }else {
            return Object.class;
        }
    }
}
