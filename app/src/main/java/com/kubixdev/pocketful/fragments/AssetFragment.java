package com.kubixdev.pocketful.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.kubixdev.pocketful.R;

public class AssetFragment extends Fragment {

    public AssetFragment(){
        // require an empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_asset, container, false);
    }
}