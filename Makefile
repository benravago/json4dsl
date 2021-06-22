
JDK=/opt/jdk16

JAVA=$(JDK)/bin/java
JAVAC=$(JDK)/bin/javac

default:
	@echo "make [ json | jsl | jbml ]"

json: bin
	$(JAVA) -cp bin lib.json.tool.Json test/example.json

jsl: bin
	$(JAVA) -cp bin lib.json.tool.Jsl test/example.jsl

jbml: bin
	$(JAVA) -cp bin lib.json.tool.Jbml test/example.jbml

bin:
	rm -fr bin
	mkdir -p bin
	$(JAVAC) -d bin -sourcepath src $(shell find src -name '*.java')

clean:
	rm -fr bin

