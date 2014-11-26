rm cplanner.tar.*
tar cf cplanner.tar --exclude-vcs --exclude-backups --exclude=junit\* \
    updebugger \
    target/lib/java/*.jar \
    etc/* \
    doc/shortdoc.pdf \
    doc/presentation* \
    doc/readme.txt &&
gzip -9 cplanner.tar
