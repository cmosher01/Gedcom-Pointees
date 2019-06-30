# Gedcom-Pointees

Copyright © 2017, Christopher Alan Mosher, Shelton, Connecticut, USA, <cmosher01@gmail.com>.

[![Donate](https://img.shields.io/badge/Donate-PayPal-green.svg)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=CVSSQ2BWDCKQ2)
[![License](https://img.shields.io/github/license/cmosher01/Gedcom-Pointees.svg)](https://www.gnu.org/licenses/gpl.html)

Given a GEDCOM file and a list of INDI IDs, output two lists of IDs:

1. All records pointed to by the given INDIs (recursively), except for
2. INDIs not in the input set (which are not recursed into)
