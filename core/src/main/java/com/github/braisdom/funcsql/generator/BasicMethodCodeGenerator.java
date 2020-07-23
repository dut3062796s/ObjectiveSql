package com.github.braisdom.funcsql.generator;

import com.github.braisdom.funcsql.Database;
import com.github.braisdom.funcsql.DefaultQuery;
import com.github.braisdom.funcsql.Query;
import com.github.braisdom.funcsql.QueryFactory;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Names;

import javax.lang.model.element.Element;
import java.util.ArrayList;

public class BasicMethodCodeGenerator extends AbstractCodeGenerator {

    private static final String METHOD_NAME_CREATE_QUERY = "createQuery";
    private static final String METHOD_NAME_CREATE_PERSISTENCE = "createPersistence";

    public BasicMethodCodeGenerator() {
        addImportItem(Database.class.getPackage().getName(), Database.class.getSimpleName());
        addImportItem(Query.class.getPackage().getName(), Query.class.getSimpleName());
        addImportItem(QueryFactory.class.getPackage().getName(), QueryFactory.class.getSimpleName());
    }

    @Override
    public JCTree.JCMethodDecl[] generateMethods(TreeMaker treeMaker, Names names,
                                                 Element element, JCTree.JCClassDecl jcClassDecl) {
        java.util.List<JCTree.JCMethodDecl> methodDecls = new ArrayList<>();

        methodDecls.add(createQueryMethod(treeMaker, names, element));

        return methodDecls.toArray(new JCTree.JCMethodDecl[]{});
    }

    private JCTree.JCMethodDecl createQueryMethod(TreeMaker treeMaker, Names names, Element element) {
        ListBuffer<JCTree.JCStatement> jcStatements = new ListBuffer<>();
        ListBuffer<JCTree.JCExpression> jcVariableExpressions = new ListBuffer<>();

        jcVariableExpressions.append(
                treeMaker.Select(
                        treeMaker.Ident(names.fromString(element.getSimpleName().toString())),
                        names.fromString("class")
                )
        );

        jcStatements.append(
                treeMaker.VarDef(
                        treeMaker.Modifiers(Flags.PARAMETER),
                        names.fromString("queryFactory"),
                        treeMaker.Ident(names.fromString(QueryFactory.class.getSimpleName())),
                        treeMaker.Apply(List.nil(),
                                treeMaker.Select(
                                        treeMaker.Ident(names.fromString(Database.class.getSimpleName())),
                                        names.fromString("getQueryFactory")
                                )
                                , List.nil()
                        )
                )
        );

        jcStatements.append(
                treeMaker.Return(
                        treeMaker.Apply(List.nil(),
                                treeMaker.Select(
                                        treeMaker.Ident(names.fromString("queryFactory")),
                                        names.fromString("createQuery")
                                )
                                , List.of(
                                        treeMaker.Select(
                                                treeMaker.Ident(names.fromString(element.getSimpleName().toString())),
                                                names.fromString("class")
                                        )
                                )
                        ))
        );

        return treeMaker.MethodDef(
                treeMaker.Modifiers(Flags.PUBLIC + Flags.STATIC + Flags.FINAL),
                names.fromString(METHOD_NAME_CREATE_QUERY),
                treeMaker.Ident(names.fromString(Query.class.getSimpleName())),
                List.nil(),
                List.nil(),
                List.nil(),
                treeMaker.Block(0, jcStatements.toList()),
                null
        );
    }

}