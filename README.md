UTRDCL
====

## Introduction
This is the official repository of UTRDCL. The core implementation and a demo project of UTRDCL has been uploaded, the rest data(including the customized malware and attack template implementation) and some documents will be uploaded gradually.

## Usage
Just pull down the corresponding code, and start the test application. To get the patch file, what we need to do is to use the *Repackage Generator* in Tools folder via following command: `python3 repackage.py --patch OLDPATCHFILE --output OUTPUTFOLDER --clean True `. Then you can find the generated apk in `OLDPATCHFILE`, unzip it and push the classes.dex to application folder. Finally, press the *Fix Bug* button, the patch file will be fixed in the test application via memory directly.

## Attention
This repository is for academic research purposes only. It contains content that may be considered malicious. The use of these files should comply with all applicable laws and regulations. We assume no liability for any misuse of this code.
