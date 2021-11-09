package org.shop.thewarehouse.ui.cart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import org.shop.thewarehouse.data.model.CartItem;
import org.shop.thewarehouse.databinding.CartRowBinding;

public class CartListAdapter extends ListAdapter<CartItem,CartListAdapter.CartVH> {
    private CartInterface cartInterface;
    protected CartListAdapter(CartInterface cartInterface) {
        super(CartItem.itemCallback);
        this.cartInterface = cartInterface;
    }

    @NonNull
    @Override
    public CartVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        CartRowBinding cartRowBinding = CartRowBinding.inflate(layoutInflater,parent,false);
        return new CartVH(cartRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CartVH holder, int position) {
        holder.cartRowBinding.setCartItem(getItem(position));
        holder.cartRowBinding.executePendingBindings();

    }

    class CartVH extends RecyclerView.ViewHolder {
        CartRowBinding cartRowBinding;
        public CartVH(@NonNull CartRowBinding cartRowBinding) {
            super(cartRowBinding.getRoot());
            this.cartRowBinding = cartRowBinding;
            cartRowBinding.deleteProductButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cartInterface.deleteItem(getItem(getAdapterPosition()));
                }
            });
            cartRowBinding.quantitySpinner.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            cartRowBinding.quantitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    int quantity = i+1;
                    if(quantity ==getItem(getAdapterPosition()).getQuantity()){
                        return;
                    }
                    cartInterface.changeQuantity(getItem(getAdapterPosition()),quantity);
                }
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }
    }
    public interface CartInterface{
        void deleteItem(CartItem cartItem);
        void changeQuantity(CartItem cartItem,int quantity);
    }
}