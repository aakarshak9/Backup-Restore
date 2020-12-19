/*
 *
 *   Created Aakarshak on 19/12/20 9:29 PM
 *   Copyright Ⓒ 2020. All rights reserved Ⓒ 2020
 *   Last modified: 19/12/20 8:06 PM
 *
 *   Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 *   except in compliance with the License. You may obtain a copy of the License at
 *   http://www.apache.org/licenses/LICENS... Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 *    either express or implied. See the License for the specific language governing permissions and
 *    limitations under the License.
 * /
 */

package com.restore.backup.backup;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.restore.backup.FixedContent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BackupFile {
    String TAG = "APK_BACKUP_ACTIVITY";
    Context context;


    public BackupFile(Context context) {
        this.context = context;
        File sdcard = Environment.getExternalStorageDirectory();
        File f = new File(sdcard + FixedContent.directory);
        f.mkdir();

    }


    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    public void extractapk(final String packagename) {
        Log.d(TAG, "APK BACKUP STARTED FOR APP" + packagename);
        PackageManager packageManager = context.getPackageManager();
        try {
            File file = new File(packageManager.getApplicationInfo(packagename, PackageManager.GET_META_DATA).publicSourceDir);
            File output = new File(Environment.getExternalStorageDirectory().getPath() + FixedContent.directory + packagename + ".apk");
            try {
                output.createNewFile();
                FileOutputStream fos;
                try {

                    InputStream assetFile = new FileInputStream(file);
                    fos = new FileOutputStream(output);
                    copyFile(assetFile, fos);
                    fos.close();
                    assetFile.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Toast.makeText(context, "APK BACKED!!!", Toast.LENGTH_SHORT).show();
        new AlertDialog.Builder(context)
                .setTitle("APK BACKUP")
                .setMessage("You can find APK File in APK_BACKUP directory of your Internal Storage")
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    // Continue with delete operation
                    dialog.dismiss();
                })
                .setIcon(android.R.drawable.checkbox_on_background)
                .show();
    }
}
