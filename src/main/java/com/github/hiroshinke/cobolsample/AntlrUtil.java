

package com.github.hiroshinke.cobolsample;;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.pattern.ParseTreePattern;
import org.antlr.v4.runtime.tree.pattern.ParseTreeMatch;
import org.antlr.v4.runtime.tree.xpath.XPath;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

import java.util.function.Function;
import java.util.function.Consumer;
import java.util.stream.Collectors;


public class AntlrUtil {


    public static ParseTree xpathSubTree(Parser parser,
					 ParseTree tree,
					 String xpath){
	
	Collection<ParseTree> subs = XPath.findAll(tree,xpath,parser);
	for (ParseTree s: subs) {
	    return s;
	}
	return null;
    }

    public static ParseTree xpathSubTree(Parser parser,
					 ParseTree tree,
					 List<String> xpaths){
	
	for(String xpath: xpaths){
	    Collection<ParseTree> subs = XPath.findAll(tree,xpath,parser);
	    for (ParseTree s: subs) {
		return s;
	    }
	}
	return null;
    }

    public static String xpathSubTreeText(Parser parser,
					  ParseTree tree,
					  String xpath) {
	
	ParseTree ret = xpathSubTree(parser,tree,xpath);
	if( ret != null ){
	    return ret.getText();
	} else {
	    return "";
	}
    }

    public static String xpathSubTreeText(Parser parser,
					  ParseTree tree,
					  List<String> xpaths) {
	
	ParseTree ret = xpathSubTree(parser,tree,xpaths);
	if( ret != null ){
	    return ret.getText();
	} else {
	    return "";
	}
    }


    public static List<String> xpathSubTreesTexts(Parser parser,
						  ParseTree tree,
						  String xpath) {
	
	return xpathSubTreesCont
	    (parser,tree,xpath,
	     (subs) -> {
		return subs.stream().map(f -> f.getText())
		    .collect(Collectors.toList());
	    });
    }

    public static List<String> xpathSubTreeTexts(Parser parser,
						 ParseTree tree,
						 List<String> xpaths) {
	return xpathSubTreesCont
	    (parser,tree,xpaths,
	     (subs) -> {
		return subs.stream().map(f -> f.getText())
		    .collect(Collectors.toList());
	    });
    }

    
    public static String xpathSubTreePatternText(Parser parser,
						 ParseTree tree,
						 String xpath,
						 String pattern,
						 String tag) {
	
	ParseTree ret = xpathSubTree(parser,tree,xpath);
	if( ret != null ){
	    if( ret instanceof TerminalNode ){
		return ret.getText();
	    }
	    else {
		RuleContext r = (RuleContext)ret;
		ParseTreePattern pat =
		    parser.compileParseTreePattern(pattern,
						   r.getRuleIndex());
		ParseTreeMatch m = pat.match(ret);
		return m.succeeded() ? m.get(tag).getText() : "";
	    }
	} else {
	    return "";
	}
    }
    
    public static Collection<ParseTree> xpathSubTrees(Parser parser,
						      ParseTree tree,
						      String xpath) {
	return XPath.findAll(tree,xpath,parser);
    }
    
    public static Collection<ParseTree> xpathSubTrees(Parser parser,
						      ParseTree tree,
						      List<String> xpaths) {

	ArrayList<ParseTree> buff = new ArrayList<ParseTree>();
	for( String xpath : xpaths ){
	    buff.addAll( XPath.findAll(tree,xpath,parser) );
	}
	return buff;
    }

    
    public static <T> T xpathSubTreesCont(Parser parser,
					  ParseTree tree,
					  String xpath,
					  Function<Collection<ParseTree>,T> cont
					  ) {

	Collection<ParseTree> subs = xpathSubTrees(parser,tree,xpath);
	return cont.apply(subs);
    }

    public static <T> T xpathSubTreesCont(Parser parser,
					  ParseTree tree,
					  List<String> xpaths,
					  Function<Collection<ParseTree>,T> cont
					  ) {

	Collection<ParseTree> subs = xpathSubTrees(parser,tree,xpaths);
	return cont.apply(subs);
    }
    
    public static <T> void xpathSubTreesDo(Parser parser,
					   ParseTree tree,
					   String xpath,
					   Consumer<ParseTree> cont
					   ) {
	
	Collection<ParseTree> subs = xpathSubTrees(parser,tree,xpath);
	for( ParseTree t : subs ){
	    cont.accept(t);
	}
    }

    public static <T> void xpathSubTreesDo(Parser parser,
					   ParseTree tree,
					   List<String> xpaths,
					   Consumer<ParseTree> cont
					   ) {
	
	Collection<ParseTree> subs = xpathSubTrees(parser,tree,xpaths);
	for( ParseTree t : subs ){
	    cont.accept(t);
	}
    }

    public static ParseTreePattern patternMatcher(Parser parser,
						  String ruleName,
						  String pattern){
	return parser.compileParseTreePattern(pattern,
					      parser.getRuleIndex(ruleName));
    }


    public static List<String> prettyStringHelper(ParseTree tree) {

	ArrayList<String> buff = new ArrayList<String>();
	
	if( tree instanceof TerminalNode ){
	    buff.add(tree.getText());
	} else {
	    int n = tree.getChildCount();
	    for(int i=0; i<n; i++){
		buff.addAll(prettyStringHelper(tree.getChild(i)));
	    }
	}
	return buff;
    }

    public static String prettyString(ParseTree tree) {

	List<String> buff = prettyStringHelper(tree);
	return String.join(" ",buff);
    }






    

}
