# Rule-based Graph Rewriting Tool

## Purpose

*cplan* is a general purpose graph rewriting tool/library which is mainly
used for semantic transfer from application to linguistic semantics, but
also for template generation, and could potentially also be used for
rule-based transfer for deep MT.

The rule syntax is currently inspired by the Hybrid CCG semantics of the
OpenCCG framework, other dialects that are closer to typed feature structures
are planned.

The full documentation, with examples, can be found in the `gui/doc`
subdirectory.

## Installation and Usage

1. Make sure you have an internet connection
2. Install maven on your computer
3. enter

   `mvn install`

In the `gui` directory is a start script (`cplanner`) that starts the
interactive version of the tool. Putting a link to this script in a place
that is in your `PATH` should suffice to start it from any location. A
`.cplanner` config file will be created in the home directory and is
human-readable.
