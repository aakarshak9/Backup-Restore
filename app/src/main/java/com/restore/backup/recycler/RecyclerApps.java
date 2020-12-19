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

package com.restore.backup.recycler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.restore.backup.R;
import com.restore.backup.app.ApplicationInfo;
import com.restore.backup.interfaceCall.CallBack;

import java.util.List;

public class RecyclerApps extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int APP_ROW = 0, HEADER_TOP = 1;
    private final List<ApplicationInfo> mAppList;
    private final CallBack mCallBack;

    public RecyclerApps(Context pContext, List<ApplicationInfo> pAppList) {
        this.mCallBack = (CallBack) pContext;
        this.mAppList = pAppList;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case APP_ROW:
                View totalHeader = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_row, parent, false);
                return new AppViewHolder(totalHeader);
            case HEADER_TOP:
                View blank = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_top, parent, false);
                return new BlankHolder(blank);

            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof AppViewHolder) {
            AppViewHolder appViewHolder = (AppViewHolder) holder;
            appViewHolder.mAppName.setText(mAppList.get(position - 1).getmAppName());
            appViewHolder.mPackageName.setText(mAppList.get(position - 1).getmPackageName());
            appViewHolder.mAppIcon.setImageDrawable(mAppList.get(position - 1).getmAppIcon());
            appViewHolder.mExtractApp.setOnClickListener(v -> mCallBack.extract(mAppList.get(position - 1).getmPackageName(), mAppList.get(position - 1).getmAppName()));
        }

    }

    @Override
    public int getItemCount() {
        return mAppList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEADER_TOP;
        }
        return APP_ROW;
    }

    private static class AppViewHolder extends RecyclerView.ViewHolder {
        TextView mAppName, mPackageName, mExtractApp;
        ImageView mAppIcon;

        AppViewHolder(View view) {
            super(view);
            mAppName = view.findViewById(R.id.app_name);
            mExtractApp = view.findViewById(R.id.extract_app);

            mPackageName = view.findViewById(R.id.package_name);
            mAppIcon = view.findViewById(R.id.app_icon);
        }
    }

    private static class BlankHolder extends RecyclerView.ViewHolder {
        BlankHolder(View view) {
            super(view);
        }
    }
}
