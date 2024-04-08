package in.hypernation.storyscrolls.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import in.hypernation.storyscrolls.R;
import in.hypernation.storyscrolls.models.Order;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private Context context;
    private ArrayList<QueryDocumentSnapshot> orders;
    private boolean isUserShow;

    public OrderAdapter(Context context, ArrayList<QueryDocumentSnapshot> orders, boolean isUserShow) {
        this.context = context;
        this.orders = orders;
        this.isUserShow=isUserShow;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_order_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBind(orders.get(position), context, isUserShow);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView dateTime, totalAmount, userName, userAddress;
        private ListView productList;
        private Button moreBtn;
        private LinearLayout userLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTime = itemView.findViewById(R.id.datetime);
            totalAmount = itemView.findViewById(R.id.total_product_amount);
            productList = itemView.findViewById(R.id.product_list);
            moreBtn = itemView.findViewById(R.id.moreBtn);
            userLayout = itemView.findViewById(R.id.user_layout);
            userName = itemView.findViewById(R.id.user_name);
            userAddress = itemView.findViewById(R.id.user_address);

        }

        private void onBind(DocumentSnapshot order, Context context, boolean isUserShow){
            if(order.contains("orderTime")){
                Date date = order.getTimestamp("orderTime").toDate();

                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
                String dateString = sdf.format(date);
                dateTime.setText(dateString);
            }
            if(isUserShow){
                userLayout.setVisibility(View.VISIBLE);
            } else {
                userLayout.setVisibility(View.GONE);
            }
            userAddress.setText(order.getString("address"));
            userName.setText(order.getString("name"));
            String totalAmountTxt = order.getString("totalAmount");
            totalAmount.setText(totalAmountTxt);
            List<HashMap<String, Object>> products = (List<HashMap<String, Object>>) order.get("products");
            productList.setAdapter(new OrderProductAdapter(products,context));
        }
    }
}
