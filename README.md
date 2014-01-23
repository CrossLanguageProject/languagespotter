languagespotter
===============

# Requirements

## external libraries 
* weka 3.7.9 (or greater) http://www.cs.waikato.ac.nz/ml/weka/downloading.html
* HMMWeka 0.1 (or greater) https://github.com/marcogillies/HMMWeka/

## programming language profiles
see profile folder

# Usage

## CLI
java -cp /path/weka weka.core.converters.TextDirectoryLoader -dir profiles > profiles.arff
java -cp /path/weka weka.filters.unsupervised.attribute.StringToWordVector -W 10000000 -i profiles.arff -o profiles.vector.arff
java -cp /path/weka weka.filters.unsupervised.attribute.Reorder -R 2-last,first -i profiles.vector.arff -o profiles.vector.ordered.arff
...

## Java
1. Make the model (see Learner.java)
2. Perform language detection (see LanguageIdentifier.java)

