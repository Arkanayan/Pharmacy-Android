package bankura.pharmacy.pharmacyapp.activities;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;

import bankura.pharmacy.pharmacyapp.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class NewOrderFragment extends BottomSheetDialogFragment {

    private final String TAG = this.getClass().getSimpleName();

    @BindView(R.id.toolbar)
    Toolbar toolbar;


    public NewOrderFragment() {
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {

            switch (newState) {
                case BottomSheetBehavior.STATE_HIDDEN:
                    dismiss();
                    break;
            }




        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };


    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext()
                , R.layout.fragment_new_order, null);
        ButterKnife.bind(this, contentView);
        dialog.setContentView(contentView);

        setupToolbar();

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        if( behavior != null && behavior instanceof BottomSheetBehavior ) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }

    }

    private void setupToolbar() {
        if (toolbar == null) {
            return;
        }

/*        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle("New Order");
        activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        activity.getSupportActionBar().setHomeButtonEnabled(true);*/

        toolbar.setNavigationIcon(R.drawable.ic_close);
        toolbar.setTitle("New Order");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }


    /*    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_order, container, false);
    }*/
}
