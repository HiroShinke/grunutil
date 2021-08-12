
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


import static com.github.hiroshinke.cobolsample.AntlrUtil.*;

public class App {

    interface Consumer<T> {
	void accept(T t) throws Exception;
    }
    
    public static void main(String[] args) throws Exception {

        // create a CharStream that reads from standard input

	if( args.length > 0 ){

	    String filePath = args[0];
	    File fileInput = new File(filePath);

	    long start0 = System.currentTimeMillis();
	    System.err.printf( "process start: %s\n",filePath);
	    
	    doFile(fileInput,(file) -> {

		    long start = System.currentTimeMillis();

		    System.err.printf( "file start: %s\n",file.toString());
		    
		    InputStream is = toSrcStream(new FileInputStream(file));
		    Cobol85PreprocessorParser parser = createParser(is);
		    ParseTree tree = parser.startRule();
		    // System.out.println(tree.toStringTree(parser));
		    printTree(parser,tree);
		    
		    System.err.printf( "file end: %s, %f s\n",
				       file, (System.currentTimeMillis() - start)/1000.0);
		    
		});

	    System.err.printf( "process end: %s, %f s\n",filePath,
			       (System.currentTimeMillis() - start0)/1000.0);

	} else {

	    Cobol85PreprocessorParser parser = createParser(System.in);
	    ParseTree tree = parser.startRule();	    
	    System.out.println(tree.toStringTree(parser));	    
	}
    }

    public static void printTree(Parser parser, ParseTree tree){

	Collection<ParseTree> subs = xpathSubTrees(parser,tree,List.of("/startRule/*"));
	for( ParseTree s : subs ){
	    if( s instanceof RuleContext ){
		RuleContext rc = (RuleContext)s;
		String ruleName = parser.getRuleNames()[rc.getRuleIndex()];
		if( ruleName.equals("charDataLine") ){
		    System.out.println( srcString(rc,72,false) );
		}
		else if( ruleName.equals("copyStatement") ){
		    System.err.println( srcString(rc,72,false) );
		    String copymem = xpathSubTreeText(parser,
						      s,
						      "*/copySource");
		    System.err.printf("copy = %s\n",copymem);
		    
		}
		else if( ruleName.equals("replaceOffStatement") ){
		    System.err.println( srcString(rc,72,false) );
		}
		else if( ruleName.equals("replaceArea") ){
		    System.err.println( srcString(rc,72,false) );
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


    public static InputStream preprocessStream(InputStream is) throws Exception {

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
		    buff.append(srcString(rc,72,false));
		}
		else if( ruleName.equals("copyStatement") ){
		    System.err.println( "copyStatement is not supported");
		    System.err.println( srcString(rc,72,false) );
		}
		else if( ruleName.equals("replaceOffStatement") ){
		    System.err.println( "replaceOffSteatement is not supported");
		    System.err.println( srcString(rc,72,false) );
		}
		else if( ruleName.equals("replaceArea") ){
		    System.err.println( "replaceArea is not supported");
		    System.err.println( srcString(rc,72,false) );
		}
		else {
		    throw new RuntimeException("unsupportedRule: " + ruleName);
		}
	    }
	    else {
		// System.out.println( s.getText() );
	    }
	}
	return new ByteArrayInputStream(buff.toString().
					getBytes(StandardCharsets.UTF_8));
    }
    

    static void doFile(File file, Consumer<File> proc) throws Exception {

	if( file.isDirectory() ){
	    doDir(file,proc);
	}
	else {
	    try {
		proc.accept(file);
	    } catch( Exception e ){
		e.printStackTrace();
		throw e;
	    }
	}
    }

    static void doDir(File file, Consumer<File> proc) throws Exception {

	for(File f: file.listFiles() ){
	    doFile(f,proc);
	}
    }


    static Pattern pattern = Pattern.compile("^.{6}-\\s+(\"|\')");

    static InputStream toSrcStream(InputStream is) throws Exception {

	BufferedReader rd = new BufferedReader(new InputStreamReader(is));
	StringBuffer buff = new StringBuffer();

	String line;
	while( (line = rd.readLine()) != null ){

	    int len = line.length();
	    int cutoff = len < 72 ? len : 72;

	    if( len < 7 ){
		;
	    } else if( line.charAt(6) == ' ' ){
		buff.append(line.substring(7,cutoff));
		buff.append('\n');
	    }
	    else if( line.charAt(6) == '-' ){
		int last = buff.length();
		buff.delete(last-1,last);
		Matcher m = pattern.matcher(line);
		if( m.find() ){
		    int e = m.end();
		    buff.append(line.substring(e,cutoff));
		} else {
		    buff.append(line.substring(7,cutoff));
		}
		buff.append('\n');
	    }
	}
	return new ByteArrayInputStream(buff.toString().
					getBytes(StandardCharsets.UTF_8));
    }

    static Cobol85PreprocessorParser createParser(InputStream is) throws Exception {
    
        ANTLRInputStream input = new ANTLRInputStream(is); 
        Cobol85PreprocessorLexer lexer = new Cobol85PreprocessorLexer(input); 
        CommonTokenStream tokens = new CommonTokenStream(lexer); 
        Cobol85PreprocessorParser parser = new Cobol85PreprocessorParser(tokens);

	return parser;
    }
}

