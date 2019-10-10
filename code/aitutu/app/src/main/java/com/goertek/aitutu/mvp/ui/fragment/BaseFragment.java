/*
 * Copyright (c) 2019 -Goertek -All rights reserved.
 */

package com.goertek.aitutu.mvp.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.goertek.aitutu.mvp.ui.activity.ImageEditActivity;

public abstract class BaseFragment extends Fragment {

    protected ImageEditActivity activity;

    protected ImageEditActivity ensureEditActivity() {
        if (activity == null) {
            activity = (ImageEditActivity) getActivity();
        }
        return activity;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ensureEditActivity();
    }

    public abstract void onShow();

    public abstract void backToMain();
}
