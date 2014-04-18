DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
M2=/home/federico/.m2/repository
CP=${DIR}/target/classes:${DIR}/lib/weka.jar:${DIR}/lib/HMM.jar
CP=${CP}:${M2}/org/parboiled/parboiled-core/1.1.6/parboiled-core-1.1.6.jar
CP=${CP}:${M2}/org/parboiled/parboiled-java/1.1.6/parboiled-java-1.1.6.jar
CP=${CP}:${M2}/org/ow2/asm/asm/4.1/asm-4.1.jar
CP=${CP}:${M2}/org/ow2/asm/asm-tree/4.1/asm-tree-4.1.jar
CP=${CP}:${M2}/org/ow2/asm/asm-analysis/4.1/asm-analysis-4.1.jar
java -cp ${CP} it.polito.languagespotter.tokenizer.TokenCounter $1