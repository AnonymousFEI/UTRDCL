"""
This tool is used to fix the package import error of the patch APK and generate a patch APK that
can be used by my AndFix framework.

Input
 APK_OLD: with com.ali.euler.andfix import
Output
 APK_FIX: with com.moran.andfix import

You should signature the whole apk after put the PATCH.MF into META-INF

Step
1. Get the smali code of APK_OLD
   Apktool d APK_OLD
2. Find the smali code in APK_OLD, replace all of
  `.annotation runtime Lcom/alipay/euler/andfix/annotation/MethodReplace;` by
  `.annotation runtime Lcom/moran/andfix/annotation/MethodReplace;`
   TODO: Get the PATCH.MF file.
3. Repackage the APK_OLD, unzip it as zipfile then obtain the classes.dex
4. Obtain a new APK_OLD, then replace the classes.dex with generated one.
"""


import os
import shutil
from re import sub
import subprocess
from loguru import logger
import argparse

class PatchFix(object):
    def __init__(self, old_apk_path: str, output_path: str) -> None:
        super().__init__()
        self.old_apk_path = old_apk_path
        self.output_path =  output_path
        self.old_apk_name = os.path.basename(os.path.splitext(old_apk_path)[0])
        self.old_decompile_path = os.path.join(output_path, self.old_apk_name)
        
    
    def decompile_apk(self):
        command = "apktool d {} -o {}".format(self.old_apk_path, self.old_decompile_path)
        logger.info("decompile by {}".format(command))
        p = subprocess.Popen(command, shell=True)
        ret_code = p.wait()
        return ret_code
    
    
    # command: find {PATH} -name "*.smali" | xargs -i sed -i "s/Lcom\/alipay\/euler\/andfix\/annotation\/MethodReplace/Lcom\/moran\/andfix\/annotation\/MethodReplace/g" {}
    
    def replace_annotation(self):
        smali_folder = os.path.join(self.old_decompile_path, "smali")
        old_import = r"Lcom\/alipay\/euler\/andfix\/annotation\/MethodReplace"
        fix_import = r"Lcom\/moran\/andfix\/annotation\/MethodReplace"
        command = r'find {} -name "*.smali" | xargs -i sed -i "s/{}/{}/g" {}'.format(smali_folder, old_import, fix_import, "{}")
        logger.info("replace method by {}".format(command))
        p = subprocess.Popen(command, shell=True)
        ret_code = p.wait()
        return ret_code
    
    def repackage_apk(self):
        output_apk_path = os.path.join(self.output_path, "{}_fixed.apk".format(self.old_apk_name))
        command = "apktool b -o {} {}".format(output_apk_path, self.old_decompile_path)
        logger.info("repakcage patch file by {}".format(command))
        p = subprocess.Popen(command, shell=True)
        ret_code = p.wait()
        return output_apk_path
    
    def obtain_fixed_dex(self, target_apk_path: str):
        command = "unzip {} -d {}".format(target_apk_path, self.output_path)
        logger.info("unzip the apk:{}".format(target_apk_path))
        p = subprocess.Popen(command, shell=True)
        ret_code = p.wait()
        
        names = os.listdir(self.output_path)
        for name in names:
            if name != "classes.dex":
                del_file_path = os.path.join(self.output_path, name)
                logger.info("deleting {}".format(del_file_path))
                if os.path.isdir(del_file_path):
                    shutil.rmtree(del_file_path)
                else:
                    os.remove(del_file_path)
                
    
    def rezip_apk(self):
        # copy the apk to workspace
        new_apk_path = os.path.join(self.output_path, "{}.{}".format(self.old_apk_name, "apk"))
        logger.debug("does {} exist? {}".format(new_apk_path, os.path.exists(new_apk_path)))
        logger.debug("does {} exist? {}".format(self.old_apk_path, os.path.exists(self.old_apk_path)))
        command = "cp {} {}".format(self.old_apk_path, new_apk_path)
        logger.info("copy old apk by {}".format(command))
        p = subprocess.Popen(command, shell=True)
        ret_code = p.wait()
        
        
        # update the apkfile
        new_dex_path = os.path.join(self.output_path, "classes.dex")
        command = "zip -j -u {} classes.dex {}".format(new_apk_path, new_dex_path)
        logger.info("update the classes.dex by {}".format(command))
        p = subprocess.Popen(command, shell=True)
        ret_code = p.wait()
        
        # clean up
        os.remove(new_dex_path)

        return new_apk_path
    
    
    def sign_apk(self):
        pass
    
    def start(self):
        self.decompile_apk()
        self.replace_annotation()
        # input("hold on")
        new_apk_path = self.repackage_apk()
        # input("hold on")
        self.obtain_fixed_dex(new_apk_path)
        # input("hold on")
        new_apk_path = self.rezip_apk()
        logger.info("Finished, patch file locates at {}".format(new_apk_path))

def clean_up(target_path: str):
    names = os.listdir(target_path)
    for name in names:
        current_path = os.path.join(target_path, name)
        logger.info("deleting {}".format(current_path))
        if os.path.isdir(current_path):
            shutil.rmtree(current_path)
        else:
            os.remove(current_path)


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="fixup the package error in patch file")
    parser.add_argument("--patch", type=str, required=True, help="old patch path")
    parser.add_argument("--output", type=str, required=True, help="output path, it must be empty")
    parser.add_argument("--clean", type=bool, required=True, help="whether need to clean the output path")

    args = parser.parse_args()

    if args.clean:
        clean_up(args.output)

    patch_fix = PatchFix(args.patch, args.output)
    patch_fix.start()