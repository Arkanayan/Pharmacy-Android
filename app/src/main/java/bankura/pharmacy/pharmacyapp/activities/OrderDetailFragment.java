package bankura.pharmacy.pharmacyapp.activities;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import bankura.pharmacy.pharmacyapp.R;
import bankura.pharmacy.pharmacyapp.controllers.OrderManager;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A fragment representing a single Order detail screen.
 * This fragment is either contained in a {@link OrderListActivity}
 * in two-pane mode (on tablets) or a {@link OrderDetailActivity}
 * on handsets.
 */
public class OrderDetailFragment extends Fragment {

    @BindView(R.id.order_detail)
    TextView orderTextView;

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ORDER_ID = "order_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private String mOrderId;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public OrderDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ORDER_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mOrderId = getArguments().getString(ORDER_ID);

/*            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.content);
            }*/
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.order_detail, container, false);
        ButterKnife.bind(this, rootView);

        // Show the dummy content as text in a TextView.
        if (mOrderId != null) {

            OrderManager.fetchOrder(mOrderId).subscribe(order -> {
                orderTextView.setText(order.getUid());
                Toast.makeText(getActivity(), "Order uid: " + order.getUid(), Toast.LENGTH_SHORT).show();
            }, throwable -> {
                Snackbar.make(rootView, throwable.getMessage(), Snackbar.LENGTH_SHORT);
            });

        }

        return rootView;
    }
}
