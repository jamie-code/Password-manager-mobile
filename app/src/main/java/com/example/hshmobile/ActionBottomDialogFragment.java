package com.example.hshmobile;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
public class ActionBottomDialogFragment extends BottomSheetDialogFragment
        implements View.OnClickListener {
    public static final String TAG = "ActionBottomDialog";
    private ItemClickListener mListener;
    public static ActionBottomDialogFragment newInstance() {
        return new ActionBottomDialogFragment();
    }
    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet, container, false);
    }
    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.txtEdit).setOnClickListener(this);
        view.findViewById(R.id.txtShowPassword).setOnClickListener(this);
        view.findViewById(R.id.txtCopyPassword).setOnClickListener(this);
        view.findViewById(R.id.txtCopyUsername).setOnClickListener(this);
        view.findViewById(R.id.txtCopyUrl).setOnClickListener(this);
        view.findViewById(R.id.txtPasswordHistory).setOnClickListener(this);
        view.findViewById(R.id.txtShare).setOnClickListener(this);
        view.findViewById(R.id.txtDelete).setOnClickListener(this);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ItemClickListener) {
            mListener = (ItemClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ItemClickListener");
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    @Override public void onClick(View view) {
        TextView tvSelected = (TextView) view;
        mListener.onItemClick(Integer.toString(tvSelected.getId()));
        dismiss();
    }
    public interface ItemClickListener {
        void onItemClick(String item);
    }
}