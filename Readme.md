# Rule-based Graph Rewriting Tool

## Purpose

*cplan* is a general purpose graph rewriting tool/library which is mainly
used for semantic transfer from application to linguistic semantics, but
also for template generation, and could potentially also be used for
rule-based transfer for deep MT.

The rule syntax is currently inspired by the Hybrid CCG semantics of the
OpenCCG framework, other dialects that are closer to typed feature structures
are planned.

The user manual and a presentation with examples, can be found in the `gui/doc`
subdirectory. In addition, after starting the program, you can open the file

`core/src/test/resources/grammars/examples.cpr`

which contains examples for most of the things that you can do with cplanner.
In the comments of the `examples.trf` rule file which is loaded as part of the
project, you will find inputs that you can copy into the input field of the
main window and run interactively to see the effects.

## Installation and Usage

The following installation instructions are tested on Ubuntu 22.04

Prerequisites for installing cplan are:
- OpenJDK 11 (at least), maven build tool, and git
  ```
  sudo apt install openjdk-11-jdk maven git
  ```

- get cplan, install some dependencies from github, compile them, and finally
  the framework itself
  ```
  git clone https://github.com/bkiefer/cplan.git
  cd cplan
  # install dependencies
  ./install_locallibs.sh
  mvn install
  ```

In the top-level directory is a start script (`cplanner` resp. `cplanner.bat`)
that starts the interactive version of the tool. Putting a link to this script
in a place that is in your `PATH` should suffice to start it from any
location. A `.cplanner` config file will be created in the home directory and
is human-readable.
