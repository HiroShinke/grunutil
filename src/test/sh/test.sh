

CLASSPATH=target/classes:~/.m2/repository/org/antlr/antlr4-runtime/4.9/antlr4-runtime-4.9.jar

echo "3=1+2" > equations.txt
java com.github.hiroshinke.antlrsample.App equations.txt

# grun com.github.hiroshinke.antlrsample.Arithmetic equations.txt
CLASSPATH=target/classes:~/lib/antlr-4.9.2-complete.jar
java org.antlr.v4.gui.TestRig com.github.hiroshinke.antlrsample.Arithmetic file_ -tokens


