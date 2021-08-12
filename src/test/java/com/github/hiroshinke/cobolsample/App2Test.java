

package com.github.hiroshinke.cobolsample;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Rule;
import static org.hamcrest.Matchers.is;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.junit.contrib.java.lang.system.SystemOutRule;

import java.util.Collection;
import java.util.List;

import org.antlr.v4.runtime.tree.xpath.XPath;
import org.antlr.v4.runtime.tree.ParseTree;

import static com.github.hiroshinke.cobolsample.ParserCommon.pattern;

/**
 * Unit test for simple App.
 */
public class App2Test
{
    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();

    public InputStream toInputStream(String text) throws IOException {
	    return new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
	}

    public String cobolTemplate(String dataDivision, String procDivision){

	StringBuffer buff = new StringBuffer();

	buff.append("IDENTIFICATION DIVISION.\n");
	buff.append("PROGRAM-ID. AAAAA.\n");
	buff.append("ENVIRONMENT DIVISION.\n");
	buff.append("DATA DIVISION.\n");
	buff.append("WORKING-STORAGE SECTION.\n");
	buff.append( dataDivision);
	buff.append("PROCEDURE DIVISION.\n");
	buff.append( procDivision);

	return buff.toString();
    }
    
    @Test
    public void testDataDesc1() throws Exception 
    {
	String src = cobolTemplate
	    (
	     "01 XXXX PIC 9(10). \n",
	     "CALL 'aaaa' USING XXXX.\n" +
	     "MOVE 1 TO XXXX.\n"
	     );
	InputStream is = toInputStream(src);
	Cobol85Parser parser = App.createParser(is);
	App.printDataDescriptionInfo("prog1",parser);
	assertThat(systemOutRule.getLog(),
		   is("dataDescription,prog1,01,XXXX,9(10),,,,\n"));
    }

    @Test
    public void testDataDescPict1() throws Exception 
    {
	String src = cobolTemplate
	    (
	     "01 XXXX PIC XXX. \n",
	     "CALL 'aaaa' USING XXXX.\n" +
	     "MOVE 1 TO XXXX.\n"
	     );
	InputStream is = toInputStream(src);
	Cobol85Parser parser = App.createParser(is);
	App.printDataDescriptionInfo("prog1",parser);
	assertThat(systemOutRule.getLog(),
		   is("dataDescription,prog1,01,XXXX,XXX,,,,\n"));
    }

    @Test
    public void testDataDescPict2() throws Exception 
    {
	String src = cobolTemplate
	    (
	     "01 XXXX PIC S999V999. \n",
	     "CALL 'aaaa' USING XXXX.\n" +
	     "MOVE 1 TO XXXX.\n"
	     );
	InputStream is = toInputStream(src);
	Cobol85Parser parser = App.createParser(is);
	App.printDataDescriptionInfo("prog1",parser);
	assertThat(systemOutRule.getLog(),
		   is("dataDescription,prog1,01,XXXX,S999V999,,,,\n"));
    }

    @Test
    public void testDataDescPict3() throws Exception 
    {
	String src = cobolTemplate
	    (
	     "01 XXXX PIC 999.999. \n",
	     "CALL 'aaaa' USING XXXX.\n" +
	     "MOVE 1 TO XXXX.\n"
	     );
	InputStream is = toInputStream(src);
	Cobol85Parser parser = App.createParser(is);
	App.printDataDescriptionInfo("prog1",parser);
	assertThat(systemOutRule.getLog(),
		   is("dataDescription,prog1,01,XXXX,999.999,,,,\n"));
    }

    @Test
    public void testDataDescPict4() throws Exception 
    {
	String src = cobolTemplate
	    (
	     "01 XXXX PIC X( \n" +
	     "     10).\n",
	     "CALL 'aaaa' USING XXXX.\n" +
	     "MOVE 1 TO XXXX.\n"
	     );
	InputStream is = toInputStream(src);
	Cobol85Parser parser = App.createParser(is);
	App.printDataDescriptionInfo("prog1",parser);
	assertThat(systemOutRule.getLog(),
		   is("dataDescription,prog1,01,XXXX,X(10),,,,\n"));
    }
    
    @Test
    public void testDataDesc2() throws Exception 
    {
	String src = cobolTemplate
	    (
	     "01 XXXX PIC 9(10) OCCURS 10. \n",
	     "CALL 'aaaa' USING XXXX.\n" +
	     "MOVE 1 TO XXXX.\n"
	     );
	InputStream is = toInputStream(src);
	Cobol85Parser parser = App.createParser(is);
	App.printDataDescriptionInfo("prog1",parser);
	assertThat(systemOutRule.getLog(),
		   is("dataDescription,prog1,01,XXXX,9(10),,,,10\n"));
    }
    @Test
    public void testDataDesc3() throws Exception 
    {
	String src = cobolTemplate
	    (
	     "01 XXXX PIC 9(10) VALUE 'ssss'. \n",
	     "CALL 'aaaa' USING XXXX.\n" +
	     "MOVE 1 TO XXXX.\n"
	     );
	InputStream is = toInputStream(src);
	Cobol85Parser parser = App.createParser(is);
	App.printDataDescriptionInfo("prog1",parser);
	assertThat(systemOutRule.getLog(),
		   is("dataDescription,prog1,01,XXXX,9(10),,'ssss',,\n"));
    }

    @Test
    public void testDataDesc4() throws Exception 
    {
	String src = cobolTemplate
	    (
	     "01 PIC 9(10). \n",
	     "CALL 'aaaa' USING XXXX.\n" +
	     "MOVE 1 TO XXXX.\n"
	     );
	InputStream is = toInputStream(src);
	Cobol85Parser parser = App.createParser(is);
	App.printDataDescriptionInfo("prog1",parser);
	assertThat(systemOutRule.getLog(),
		   is("dataDescription,prog1,01,,9(10),,,,\n"));
    }

    @Test
    public void testDataDesc5() throws Exception 
    {
	String src = cobolTemplate
	    (
	     "01 XXXX PIC S9(10) COMP. \n",
	     "CALL 'aaaa' USING XXXX.\n" +
	     "MOVE 1 TO XXXX.\n"
	     );
	InputStream is = toInputStream(src);
	Cobol85Parser parser = App.createParser(is);
	App.printDataDescriptionInfo("prog1",parser);
	assertThat(systemOutRule.getLog(),
		   is("dataDescription,prog1,01,XXXX,S9(10),COMP,,,\n"));
    }

    @Test
    public void testDataDesc6() throws Exception 
    {
	String src = cobolTemplate
	    (
	     "01 XXXX PIC 9(10) REDEFINES YYYY. \n",
	     "CALL 'aaaa' USING XXXX.\n" +
	     "MOVE 1 TO XXXX.\n"
	     );
	InputStream is = toInputStream(src);
	Cobol85Parser parser = App.createParser(is);
	App.printDataDescriptionInfo("prog1",parser);
	assertThat(systemOutRule.getLog(),
		   is("dataDescription,prog1,01,XXXX,9(10),,,YYYY,\n"));
    }
    
    
    @Test
    public void testCall1() throws Exception 
    {
	String src = cobolTemplate
	    (
	     "01 XXXX PIC 9(10). \n",
	     "CALL 'aaaa' USING XXXX.\n" +
	     "MOVE 1 TO XXXX.\n"
	     );
	InputStream is = toInputStream(src);
	Cobol85Parser parser = App.createParser(is);
	App.printCallInfo("prog1",parser);
	assertThat(systemOutRule.getLog(),
		   is("callStatement,prog1,'aaaa',XXXX\n"));
    }

    @Test
    public void testCall2() throws Exception 
    {
	String src = cobolTemplate
	    (
	     "01 XXXX PIC 9(10). \n",
	     "CALL 'aaaa' USING XXXX YYYY.\n" +
	     "MOVE 1 TO XXXX.\n"
	     );
	InputStream is = toInputStream(src);
	Cobol85Parser parser = App.createParser(is);
	App.printCallInfo("prog1",parser);
	assertThat(systemOutRule.getLog(),
		   is("callStatement,prog1,'aaaa',XXXX\n" +
		      "callStatement,prog1,'aaaa',YYYY\n"));
    }


    @Test
    public void testMove1() throws Exception 
    {
	String src = cobolTemplate
	    (
	     "01 XXXX PIC 9(10). \n",
	     "CALL 'aaaa' USING XXXX.\n" +
	     "MOVE 1 TO XXXX.\n"
	     );
	InputStream is = toInputStream(src);
	Cobol85Parser parser = App.createParser(is);
	App.printMoveInfo("prog1",parser);
	assertThat(systemOutRule.getLog(),
		   is("moveStetement,prog1,1,XXXX\n"));
    }

    @Test
    public void testMove2() throws Exception 
    {
	String src = cobolTemplate
	    (
	     "01 XXXX PIC 9(10). \n",
	     "CALL 'aaaa' USING XXXX.\n" +
	     "MOVE 1 TO XXXX(1).\n"
	     );
	InputStream is = toInputStream(src);
	Cobol85Parser parser = App.createParser(is);
	App.printMoveInfo("prog1",parser);
	assertThat(systemOutRule.getLog(),
		   is("moveStetement,prog1,1,XXXX ( 1 )\n"));
    }

    @Test
    public void testMove3() throws Exception 
    {
	String src = cobolTemplate
	    (
	     "01 XXXX PIC 9(10). \n",
	     "CALL 'aaaa' USING XXXX.\n" +
	     "MOVE 1 TO XXXX(I).\n"
	     );
	InputStream is = toInputStream(src);
	Cobol85Parser parser = App.createParser(is);
	App.printMoveInfo("prog1",parser);
	assertThat(systemOutRule.getLog(),
		   is("moveStetement,prog1,1,XXXX ( I )\n"));
    }

    @Test
    public void testMove4() throws Exception 
    {
	String src = cobolTemplate
	    (
	     "01 XXXX PIC 9(10). \n",
	     "CALL 'aaaa' USING XXXX.\n" +
	     "MOVE 1 TO XXXX(I) \n" +
	     "          YYYY. \n"
	     );
	InputStream is = toInputStream(src);
	Cobol85Parser parser = App.createParser(is);
	App.printMoveInfo("prog1",parser);
	assertThat(systemOutRule.getLog(),
		   is("moveStetement,prog1,1,XXXX ( I )\n"+
		      "moveStetement,prog1,1,YYYY\n"));
    }

    @Test
    public void testMove5() throws Exception 
    {
	String src = cobolTemplate
	    (
	     "01 XXXX PIC 9(10). \n",
	     "CALL 'aaaa' USING XXXX.\n" +
	     "MOVE 'ssss' TO XXXX.\n"
	     );
	InputStream is = toInputStream(src);
	Cobol85Parser parser = App.createParser(is);
	App.printMoveInfo("prog1",parser);
	assertThat(systemOutRule.getLog(),
		   is("moveStetement,prog1,'ssss',XXXX\n"));
    }

    @Test
    public void testMove6() throws Exception 
    {
	String src = cobolTemplate
	    (
	     "01 XXXX PIC 9(10). \n",
	     "CALL 'aaaa' USING XXXX.\n" +
	     "MOVE SPACE TO XXXX.\n"
	     );
	InputStream is = toInputStream(src);
	Cobol85Parser parser = App.createParser(is);
	App.printMoveInfo("prog1",parser);
	assertThat(systemOutRule.getLog(),
		   is("moveStetement,prog1,SPACE,XXXX\n"));
    }

    @Test
    public void testMove7() throws Exception 
    {
	String src = cobolTemplate
	    (
	     "01 XXXX PIC 9(10). \n",
	     "CALL 'aaaa' USING XXXX.\n" +
	     "MOVE ALL SPACE TO XXXX.\n"
	     );
	InputStream is = toInputStream(src);
	Cobol85Parser parser = App.createParser(is);
	App.printMoveInfo("prog1",parser);
	assertThat(systemOutRule.getLog(),
		   is("moveStetement,prog1,SPACE,XXXX\n"));
    }

    @Test
    public void testMove8() throws Exception 
    {
	String src = cobolTemplate
	    (
	     "01 XXXX PIC 9(10). \n",
	     "CALL 'aaaa' USING XXXX.\n" +
	     "MOVE X IN Y TO XXXX.\n"
	     );
	InputStream is = toInputStream(src);
	Cobol85Parser parser = App.createParser(is);
	App.printMoveInfo("prog1",parser);
	assertThat(systemOutRule.getLog(),
		   is("moveStetement,prog1,X IN Y,XXXX\n"));
    }

    @Test
    public void testMove9() throws Exception 
    {
	String src = cobolTemplate
	    (
	     "01 XXXX PIC 9(10). \n",
	     "CALL 'aaaa' USING XXXX.\n" +
	     "MOVE X OF Y TO XXXX.\n"
	     );
	InputStream is = toInputStream(src);
	Cobol85Parser parser = App.createParser(is);
	App.printMoveInfo("prog1",parser);
	assertThat(systemOutRule.getLog(),
		   is("moveStetement,prog1,X OF Y,XXXX\n"));
    }

    
}