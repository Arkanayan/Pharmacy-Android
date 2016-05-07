package bankura.pharmacy.pharmacyapp.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import bankura.pharmacy.pharmacyapp.App;
import bankura.pharmacy.pharmacyapp.R;
import bankura.pharmacy.pharmacyapp.Utils.Constants;
import bankura.pharmacy.pharmacyapp.Utils.Utils;
import bankura.pharmacy.pharmacyapp.models.Order;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.subscriptions.CompositeSubscription;

/**
 * A fragment representing a single Order detail screen.
 * This fragment is either contained in a {@link OrderListActivity}
 * in two-pane mode (on tablets) or a {@link OrderDetailActivity}
 * on handsets.
 */
public class OrderDetailFragment extends Fragment {

    private final String TAG = this.getClass().getSimpleName();

    @BindView(R.id.order_detail)
    TextView orderTextView;

    Firebase mRef;

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ORDER_PATH = "order_path";

    /**
     * The dummy content this fragment is presenting.
     */
    private String mOrderPath;

    private CompositeSubscription compositeSubscription;

    @BindView(R.id.image_view_prescripiton)
    ImageView prescriptionImageView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public OrderDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        compositeSubscription = new CompositeSubscription();

        if (getArguments().containsKey(ORDER_PATH)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mOrderPath = getArguments().getString(ORDER_PATH);

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
        View rootView = inflater.inflate(R.layout.fragment_order_detail, container, false);
        ButterKnife.bind(this, rootView);

        // Show the dummy content as text in a TextView.
        if (mOrderPath != null) {
            Log.d(TAG, "onCreateView: order path: " + mOrderPath);
            mRef = App.getFirebase();

/*           Subscription fetchOrderSubscription =  OrderManager.fetchOrder(mOrderPath).subscribe(order -> {
                orderTextView.setText(order.getUid());
            }, throwable -> {
               orderTextView.setText(throwable.getLocalizedMessage());
            });

            compositeSubscription.add(fetchOrderSubscription);*/
            mRef.child(Constants.Path.ORDERS).child(mOrderPath).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Order order = dataSnapshot.getValue(Order.class);

                    if (order != null) {
                        orderTextView.setText(order.getNote());

                        String url = Utils.getImageLowerUrl(order.getPrescriptionUrl(), null);

                        Glide.with(OrderDetailFragment.this)
                                .load(url)
                                .into(prescriptionImageView);
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

        }

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeSubscription.unsubscribe();

    }
}
