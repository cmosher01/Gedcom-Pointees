# Gedcom-Pointees

Given a GEDCOM file and a list of INDI IDs, output two lists of IDs:

1. All records pointed to by the given INDIs (recursively), except for
2. INDIs not in the input set (which are not recursed into)
