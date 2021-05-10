# Grepy
A version of the grep utility, in Java, that searches files for regular expression, pattern matches and produces dot graph file output for the automata used in the matching computation.

## Getting started

  Grepy [-n NFA-FILE][-d DFA FILE] REGEX FILE

  - REGEX FILE, provides Grepy with the correct regular expression.
  - -n [NFA-FILE] specifies the name of the NFA DOT output file and png file.
  - -d [DFA-FILE] specifies the name of the DFA DOT output file and png file.
  
### Prerequisites

This project requires the installation of the JDK 12.0.5 (SE is included in JDK).

Outlining the docs, follow these steps for Windows:

  1. Download the JDK Installer
  2. Run the JDK installer .exe
  3. Set the PATH variable to the JDK bin
      * Go to the Control Panel > System > Edit the system environment variables
      * On the System Properties Window go to the Advanced Tab
      * At the bottom click environment variables
      * Under USER VARIABLES NOT System Variables, click PATH
      * While PATH is selected, click EDIT
      * Click NEW and add the path to the JDK bin
  
For Mac of Linux refer to the official oracle docs:
  - https://docs.oracle.com/en/java/javase/16/install
  
### Installation

Clone the repository make sure that:
  - You have the PATH variable specified
  - You are in the Grepy/bin directory where the .class files are specifed

In the command line:

  1. The projects class files are already compiled
    - cd into the Grepy.bin directory
  2. Run Grepy using the JDK 
    - java Grepy

## Built With

- IDE: Eclipse
- Languages: Java

## Authors
- Alex Badia
- Michael Gildeon (teacher)
