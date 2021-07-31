

package com.github.hiroshinke.antlrsample;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.StringJoiner;
import java.util.function.Predicate;


interface Continuation {
    ArrayList<String> process(Expr e);
}


public class GrunUtil {

    String text;

    public static void main(String[] args) throws Exception {

        // Read Grun LISP style output for Analyse it
        String filePath = args[0];
        File fileInput = new File(filePath);
        FileInputStream fileInputStream = new FileInputStream(fileInput);

	String text = new String(fileInputStream.readAllBytes(),
				 StandardCharsets.UTF_8);
	GrunReader util = new GrunReader(text);

	while(true) {
	    ParseResult r = util.readExpr(0);
	    if (r == null ) break;
	}
    }

    static Predicate<Expr> specToPred(String spec) {

	return ( (e) -> {
		if( e.getName().equals(spec) ){
			return true;
		    } else {
			return false;
		    }
		}
	    );
	    
    }

    static ArrayList<String> findTag(Expr e, String spec) {

	return findBase(e,
			specToPred(spec),
			(x) -> {
			    ArrayList<String> buff = new ArrayList<String>();
			    buff.add(x.getName());
			    return buff;
			}
			);
    }

    static ArrayList<String> findBase(Expr e,
				      Predicate<Expr> pred,
				      Continuation cont) {

	ArrayList<String> buff = new ArrayList<String>();
	
	if( pred.test(e) ){
	    buff.addAll(cont.process(e));
	}

	if( e instanceof ListExpr ){
	    ListExpr le = (ListExpr)e;
	    for( Expr c : le.children) {
		for( String ans : findBase(c,pred,cont) ){
		    buff.add( le.tag + "/" + ans );
		}
	    }
	}
	return buff;
    }
    
}

