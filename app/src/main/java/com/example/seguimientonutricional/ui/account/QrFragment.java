package com.example.seguimientonutricional.ui.account;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.example.seguimientonutricional.R;
import com.google.zxing.Result;

/**
 * A simple {@link Fragment} subclass.
 */
public class QrFragment extends Fragment {



    private CodeScanner mQRScanner;

    QrFragment.OnQrFragmentInteractionListener mQrListener;

    public QrFragment() {
        // Required empty public constructor
    }


    interface OnQrFragmentInteractionListener {
        void onQRCodeFound(Boolean foundCode);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_qr, container, false);
        final Activity activity = getActivity();
        CodeScannerView scannerView = root.findViewById(R.id.scanner_view);
        mQRScanner = new CodeScanner(activity,scannerView);
        mQRScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity,result.getText(),Toast.LENGTH_SHORT).show();
                        mQrListener.onQRCodeFound(true);
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                });
            }
        });

        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mQRScanner.startPreview();
            }
        });

        return root;
    }




    @Override
    public void onResume() {
        super.onResume();
        mQRScanner.startPreview();
    }

    @Override
    public void onPause() {
        mQRScanner.releaseResources();
        super.onPause();
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);

        if(context instanceof QrFragment.OnQrFragmentInteractionListener){
            mQrListener = (QrFragment.OnQrFragmentInteractionListener) context;
        } else {
            throw new ClassCastException(context.toString() +
                    "Should implement OnQrFragmentInteractionListener");
        }
    }
}
