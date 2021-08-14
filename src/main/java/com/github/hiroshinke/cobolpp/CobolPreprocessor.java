
package com.github.hiroshinke.cobolpp;

/**
 * Hello world!
 *
 */

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.pattern.ParseTreePattern;
import org.antlr.v4.runtime.tree.pattern.ParseTreeMatch;
import org.antlr.v4.runtime.tree.xpath.XPath;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import static com.github.hiroshinke.cobolsample.ParserCommon.*;
import static com.github.hiroshinke.cobolsample.AntlrUtil.*;


public class CobolPreprocessor  {


    List<String> libpathes;

    public CobolPreprocessor(String... pathes){
	libpathes = List.of(pathes);
    }
    
    public static void main(String[] args) throws Exception {

	if( args.length > 0 ){

	    String filePath = args[0];
	    File fileInput = new File(filePath);

	    long start0 = System.currentTimeMillis();
	    System.err.printf( "process start: %s\n",filePath);
	    doFile(fileInput,CobolPreprocessor::parseFile);
	    System.err.printf( "process end: %s, %f s\n",filePath,
			       (System.currentTimeMillis() - start0)/1000.0);

	} else {

	    Cobol85PreprocessorParser parser = createParser(System.in);
	    ParseTree tree = parser.startRule();	    
	    System.out.println(tree.toStringTree(parser));	    
	}
    }

    static void parseFile(File file) throws Exception {

	long start = System.currentTimeMillis();
	
	System.err.printf( "file start: %s\n",file.toString());
	
	InputStream is = toSrcStream(new FileInputStream(file));
	Cobol85PreprocessorParser parser = createParser(is);
	ParseTree tree = parser.startRule();
	// System.out.println(tree.toStringTree(parser));
	printTree(parser,tree);
	
	System.err.printf( "file end: %s, %f s\n",
			   file, (System.currentTimeMillis() - start)/1000.0);

    }
    
    public static void printTree(Parser parser, ParseTree tree){

	Collection<ParseTree> subs = xpathSubTrees(parser,tree,List.of("/startRule/*"));
	for( ParseTree s : subs ){
	    if( s instanceof RuleContext ){
		RuleContext rc = (RuleContext)s;
		String ruleName = parser.getRuleNames()[rc.getRuleIndex()];
		if( ruleName.equals("charDataLine") ){
		    System.out.println( srcString(rc,65) );
		}
		else if( ruleName.equals("copyStatement") ){
		    System.err.println( srcString(rc,65) );
		    String copymem = xpathSubTreeText(parser,
						      s,
						      "*/copySource");
		    System.err.printf("copy = %s\n",copymem);
		    
		}
		else if( ruleName.equals("replaceOffStatement") ){
		    System.err.println( srcString(rc,65) );
		}
		else if( ruleName.equals("replaceArea") ){
		    System.err.println( srcString(rc,65) );
		}
		else {
		    throw new RuntimeException("unsupportedRule: " + ruleName);
		}
	    }
	    else {
		// System.out.println( s.getText() );
	    }

	}
    }


    public InputStream preprocessStream(InputStream is) throws Exception {

	Cobol85PreprocessorParser parser = createParser(is);
	ParseTree tree = parser.startRule();

	Collection<ParseTree> subs = xpathSubTrees(parser,
						   tree,
						   "/startRule/*");
	StringBuffer buff = new StringBuffer();
	
	for( ParseTree s : subs ){

	    if( s instanceof RuleContext ){

		RuleContext rc = (RuleContext)s;
		String ruleName = parser.getRuleNames()[rc.getRuleIndex()];

		if( ruleName.equals("charDataLine") ){
		    buff.append(srcString(rc,65));
		    buff.append('\n');
		}
		else if( ruleName.equals("copyStatement") ){

		    System.err.println( "copyStatement expanded");
		    System.err.println( srcString(rc,65) );
		    String copymem = xpathSubTreeText(parser,s,"*/copySource");
		    
		    processCopySentence(copymem, buff);
		}
		else if( ruleName.equals("replaceOffStatement") ){
		    System.err.println( "replaceOffSteatement is not supported");
		    System.err.println( srcString(rc,65) );
		}
		else if( ruleName.equals("replaceArea") ){
		    System.err.println( "replaceArea is not supported");
		    System.err.println( srcString(rc,65) );
		}
		else {
		    throw new RuntimeException("unsupportedRule: " + ruleName);
		}
	    }
	    else {
		// buff.append(srcString(s,65)); 
	    }
	}
	return new ByteArrayInputStream(buff.toString().
					getBytes(StandardCharsets.UTF_8));
    }

    File findFile(String copymem) throws Exception {

	for(String ext: List.of("", ".cbl", ".txt")){
	    for(String path: libpathes){
		File file = new File(path, copymem + ext);
		if( file.exists() ){
		    return file;
		}
	    }
	}
	return null;
    }
    
    void processCopySentence(String copymem, StringBuffer buff) throws Exception {

	File lib = findFile(copymem);
	if( lib == null) {
	    System.err.println("copymem not found: " + copymem);
	    return;
	}
	System.err.println("lib=" + lib.getPath());
	
	InputStream is0 = toSrcStream(new FileInputStream(lib));
	BufferedReader rd =
	    new BufferedReader
	    ( new InputStreamReader( preprocessStream(is0) ) );

	String line = null;
	while( (line = rd.readLine()) != null ){
	    buff.append(line);
	    buff.append('\n');
	}
    }

    static Cobol85PreprocessorParser createParser(InputStream is) throws Exception {
    
        ANTLRInputStream input = new ANTLRInputStream(is); 
        Cobol85PreprocessorLexer lexer = new Cobol85PreprocessorLexer(input); 
        CommonTokenStream tokens = new CommonTokenStream(lexer); 
        Cobol85PreprocessorParser parser = new Cobol85PreprocessorParser(tokens);

	return parser;
    }
}
