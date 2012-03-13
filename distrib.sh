rm cplanner.tar.*
tar cf cplanner.tar --exclude-vcs --exclude-backups \
    --exclude=junit\* \
    updebugger \
    deplibs/* \
    lib/java/dataviz.jar lib/java/j2emacs.jar lib/java \
    etc/* \
    doc/shortdoc.pdf \
    doc/presentation* \
    doc/readme.txt \
    target/cplan-gui.jar &&
gzip -9 cplanner.tar