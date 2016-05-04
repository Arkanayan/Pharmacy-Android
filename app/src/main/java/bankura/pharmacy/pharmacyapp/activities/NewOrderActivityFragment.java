package bankura.pharmacy.pharmacyapp.activities;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bankura.pharmacy.pharmacyapp.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class NewOrderActivityFragment extends Fragment {

    public NewOrderActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_order, container, false);
    }
}
