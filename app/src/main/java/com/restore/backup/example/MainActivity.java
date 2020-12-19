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

package com.restore.backup.example;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.restore.backup.FixedContent;
import com.restore.backup.R;
import com.restore.backup.app.ApplicationInfo;
import com.restore.backup.backup.BackupFile;
import com.restore.backup.interfaceCall.CallBack;
import com.restore.backup.recycler.RecyclerApps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, CallBack {
    private static final String TAG = "MainActivity";
    private final List<ApplicationInfo> mAppList = new ArrayList<>();
    private String packageNamePending = "";
    private SharedPreferences pref;
    private RecyclerView mAppRecycler;
    private RecyclerApps mRecyclerApps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        mAppRecycler = findViewById(R.id.app_recycler);
        ImageView mAppFolder = findViewById(R.id.folder_apps);
        mAppFolder.setOnClickListener(this);
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        mAppRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerApps = new RecyclerApps(this, mAppList);
        mAppRecycler.setAdapter(mRecyclerApps);
        new AsyncCaller().execute();

    }

    private void listApps() {
        List<PackageInfo> packList = getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packList.size(); i++) {
            PackageInfo packInfo = packList.get(i);
            if ((packInfo.applicationInfo.flags & android.content.pm.ApplicationInfo.FLAG_SYSTEM) == 0) {
                String appName = packInfo.applicationInfo.loadLabel(getPackageManager()).toString();
                String appPackage = packInfo.packageName;
                Drawable appIcon = packInfo.applicationInfo.loadIcon(getPackageManager());

                mAppList.add(new ApplicationInfo(appName, appPackage, appIcon));
            }
        }
        Collections.sort(mAppList, (obj1, obj2) -> obj1.getmAppName().compareToIgnoreCase(obj2.getmAppName()));

    }

    void showTutorial() {
        if (pref.getBoolean(FixedContent.DEMO, false)) {
            return;
        }

        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(FixedContent.DEMO, true);
        editor.apply(); // commit changes
    }

    private void openFolder() {
        Uri selectedUri = Uri.parse(Environment.getExternalStorageDirectory() + "/APK_BACKUP/");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(selectedUri, "resource/folder");

        if (intent.resolveActivityInfo(getPackageManager(), 0) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "Goto APK_BACKUP folder on your phone!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.folder_apps) {
            openFolder();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
            new BackupFile(this).extractapk(packageNamePending);
            showTutorial();

        }
    }

    @Override
    public void extract(String packageName, String AppName) {
        packageNamePending = packageName;
        if (isStoragePermissionGranted()) {
            new BackupFile(this).extractapk(packageName);
            showTutorial();

        }
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else {
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }

    public void rateUs(View v) {
        Intent rate = new Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/forms/d/e/1FAIpQLSdP2dh8hNJhxqFtGFXSGyOg6pTtGvePUvQ3Sc9FfMm_zMDJaQ/viewform?usp=sf_link"));
        startActivity(rate);
    }

    public void shareApp(View v) {
        Intent share = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.cuchd.in/"));
        startActivity(share);

    }

    public void donate(View v) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://pmnrf.gov.in/en/online-donation"));
        startActivity(browserIntent);

    }

    @SuppressLint("StaticFieldLeak")
    private class AsyncCaller extends AsyncTask<Void, Void, Void> {
        ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdLoading.setMessage("\tLoading...");
            pdLoading.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            listApps();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            mRecyclerApps.notifyDataSetChanged();
            pdLoading.dismiss();
        }

    }
}