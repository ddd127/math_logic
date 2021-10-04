.PHONY: all compile run pack clean out

SOURCES = $(shell find commons/src/main/java -type f -name "*.kt") $(shell find taskA/src/main/java -type f -name "*.kt") $(shell find taskB/src/main/java -type f -name "*.kt")
CLASSES = $(patsubst commons/src/main/java/%.kt,taskA/src/main/java/%.kt,taskB/src/main/java/%.kt,out/%.class,$(SOURCES))
DEPS = lib/kotlin-stdlib-jdk8-1.5.10.jar:lib/better-parse-0.4.2.jar
MAINCLASS = MainKt

KOTLIN=kotlin
KOTLINC=kotlinc

all: out compile

run:
	${KOTLIN} -cp out:${DEPS} -Djava.io.tmpdir=tmp task.a.solve.${MAINCLASS} $(IN) $(OUT)

pack:
	zip taskA.zip -r Makefile lib commons taskA taskB

clean:
	rm -rf out
	rm -rf tmp
	rm -rf kotlin-tmp

compile: ${SOURCES}
	${KOTLINC} -cp commons/src/main/java:taskA/src/main/java:taskB/src/main/java:${DEPS} -Djava.io.tmpdir=kotlin-tmp $^ -d out

out:
	mkdir -p out