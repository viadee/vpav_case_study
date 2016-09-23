package de.viadee.bpmnAnalytics;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.junit.Ignore;
import org.junit.Test;

public class SpringConfigAnalyzerTest {

  @Ignore
  @Test
  public void testSpringConfigAnalyzer() throws IOException {
    final String fileContent = readFileToString(
        "src/test/java/de/viadee/bpmnAnalytics/delegates/DelegateWithVariablesAgainstConvention.java");

    final ASTParser parser = ASTParser.newParser(AST.JLS3);
    parser.setSource(fileContent.toCharArray());
    parser.setKind(ASTParser.K_COMPILATION_UNIT);

    final CompilationUnit cu = (CompilationUnit) parser.createAST(null);

    cu.accept(new ASTVisitor() {

      public boolean visit(MethodDeclaration node) {
        System.out.println("Declaration of '" + node.getName() + "' at line"
            + cu.getLineNumber(node.getStartPosition()));
        if (node.getName().toString().equals("execute")) {
          Block block = node.getBody();

          block.accept(new ASTVisitor() {

            public boolean visit(MethodInvocation node) {
              // System.out.println(node.getExpression());
              System.out.println("Name: " + node.getName() + ", Arguments: " + node.arguments());
              // if (node.getName().equals("name")) {
              System.out.println(node.toString());
              // }
              return true;
            }

          });

        }
        return true;
      }

      // public boolean visit(VariableDeclarationFragment node) {
      // SimpleName name = node.getName();
      // this.names.add(name.getIdentifier());
      // System.out.println(
      // "Declaration of '" + name + "' at line" + cu.getLineNumber(name.getStartPosition()));
      // return false; // do not continue
      // }
      //
      // public boolean visit(SimpleName node) {
      // if (this.names.contains(node.getIdentifier())) {
      // System.out.println(
      // "Usage of '" + node + "' at line " + cu.getLineNumber(node.getStartPosition()));
      // }
      // return true;
      // }
    });
  }

  // read file content into a string
  public static String readFileToString(String filePath) throws IOException {
    StringBuilder fileData = new StringBuilder(1000);
    BufferedReader reader = new BufferedReader(new FileReader(filePath));

    char[] buf = new char[10];
    int numRead = 0;
    while ((numRead = reader.read(buf)) != -1) {
      System.out.println(numRead);
      String readData = String.valueOf(buf, 0, numRead);
      fileData.append(readData);
      buf = new char[1024];
    }

    reader.close();

    return fileData.toString();
  }
}
